package com.hunter.cookies.ui.shots.detail;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import com.hunter.cookies.R;
import com.hunter.cookies.base.BaseActivity;
import com.hunter.cookies.entity.ShotsEntity;
import com.hunter.cookies.ui.shots.ImageWatcherActivity;
import com.hunter.cookies.ui.shots.detail.comments.ShotsCommentsFragment;
import com.hunter.cookies.ui.shots.detail.des.ShotsDesFragment;
import com.hunter.cookies.utils.ImageDownloadUtils;
import com.hunter.cookies.utils.ImageUrlUtils;
import com.hunter.cookies.utils.IntentUtils;
import com.hunter.cookies.utils.StatusBarCompat;
import com.hunter.cookies.utils.glide.GlideUtils;
import com.hunter.cookies.widget.ProportionImageView;
import com.hunter.lib.base.BasePagerAdapter;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.functions.Action1;

public class ShotsDetailActivity extends BaseActivity implements Toolbar.OnMenuItemClickListener {

    public static final String EXTRA_SHOTS_ENTITY = "extra_shots_entity";
    public static final String EXTRA_IS_NEED_REQUEST = "extra_is_need_request";

    private static final String[] TAB_TITLES = {"简介", "评论"};

    @BindView(R.id.piv_shots_detail_image)
    ProportionImageView mPivShotsImage;
    @BindView(R.id.tab_shots_detail)
    TabLayout mTabShots;
    @BindView(R.id.pager_shots_detail)
    ViewPager mPagerShots;
    @BindView(R.id.toolbar_shots_detail)
    Toolbar mToolbarShots;

    private ShotsEntity mShotsEntity;

    private float mPressX;
    private float mPressY;
    private boolean mIsVerticalMove;

    private boolean mIsNeedRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shots_detail);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        mShotsEntity = (ShotsEntity) intent.getSerializableExtra(EXTRA_SHOTS_ENTITY);
        mIsNeedRequest = intent.getBooleanExtra(EXTRA_IS_NEED_REQUEST, false);

        initToolbar();
        initImage();
        initPager();
    }

    private void initToolbar() {
        StatusBarCompat.translucentStatusBar(this);
        setSupportActionBar(mToolbarShots);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbarShots.setOnMenuItemClickListener(this);
        mToolbarShots.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initImage() {
        if (mShotsEntity.isAnimated()) {
            GlideUtils.setGif(this, mShotsEntity.getImages().getHidpi(), mPivShotsImage);
        } else {
            GlideUtils.setImageWithThumb(this, ImageUrlUtils.getImageUrl(mShotsEntity.getImages()), mPivShotsImage);
        }
    }

    private void initPager() {
        for (String tabTitle : TAB_TITLES)
            mTabShots.addTab(mTabShots.newTab().setText(tabTitle));

        List<Fragment> fragments = new ArrayList<>();

        if (mIsNeedRequest) fragments.add(ShotsDesFragment.newInstance(mShotsEntity.getId()));
        else fragments.add(ShotsDesFragment.newInstance(mShotsEntity));
        fragments.add(ShotsCommentsFragment.newInstance(mShotsEntity.getId()));

        BasePagerAdapter<Fragment> adapter = new BasePagerAdapter<>(getSupportFragmentManager(), fragments,
                                                                    Arrays.asList(TAB_TITLES));
        mPagerShots.setAdapter(adapter);
        mTabShots.setupWithViewPager(mPagerShots);
    }

    @OnClick(R.id.piv_shots_detail_image)
    void toWatchImage() {
        Intent intent = new Intent(this, ImageWatcherActivity.class);
        intent.putExtra(ImageWatcherActivity.EXTRA_IMAGE_URL, ImageUrlUtils.getImageUrl(mShotsEntity.getImages()));
        intent.putExtra(ImageWatcherActivity.EXTRA_IS_ANIMATED, mShotsEntity.isAnimated());
        intent.putExtra(ImageWatcherActivity.EXTRA_IMAGE_TITLE, mShotsEntity.getTitle());
        IntentUtils.startActivity(this, mPivShotsImage, intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_shots_detail, menu);
        return true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_share:
                String shotsUrl = mShotsEntity.getHtmlUrl();
                String imageUrl = ImageUrlUtils.getImageUrl(mShotsEntity.getImages());
                ShotsShareSheet sheet = ShotsShareSheet.newInstance(shotsUrl, imageUrl);
                sheet.show(getSupportFragmentManager(), ShotsShareSheet.class.getSimpleName());
                break;
            case R.id.menu_open_on_browser:
                showInBrowser();
                break;
            case R.id.menu_download:
                downloadImage();
                break;
        }
        return true;
    }

    private void showInBrowser() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mShotsEntity.getHtmlUrl()));
        Intent chooserIntent = Intent.createChooser(intent, "选择一个应用打开该链接");
        if (chooserIntent == null) return;
        startActivity(chooserIntent);
    }

    private void downloadImage() {
        RxPermissions.getInstance(this)
                     .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                     .subscribe(new Action1<Boolean>() {
                         @Override
                         public void call(Boolean granted) {
                             if (granted) ImageDownloadUtils.downloadImage(ShotsDetailActivity.this, mShotsEntity);
                             else showToast("获取权限失败，请重试");
                         }
                     });
    }

    /**
     * 处理水平方向与垂直方向的滑动冲突
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPressX = x;
                mPressY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                if (!mIsVerticalMove) {
                    float differX = Math.abs(x - mPressX);
                    float differY = Math.abs(y - mPressY);
                    double differ = Math.sqrt(differX * differX + differY * differY);
                    int angle = Math.round((float) (Math.asin(differY / differ) / Math.PI * 180));
                    mIsVerticalMove = angle > 45;
                    if (mIsVerticalMove) mPagerShots.setEnabled(false);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mIsVerticalMove) {
                    mPagerShots.setEnabled(true);
                    mIsVerticalMove = false;
                }
                break;
        }

        return super.dispatchTouchEvent(event);
    }

}