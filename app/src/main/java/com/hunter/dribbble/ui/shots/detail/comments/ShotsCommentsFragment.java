package com.hunter.dribbble.ui.shots.detail.comments;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.hunter.dribbble.AppConstants;
import com.hunter.dribbble.R;
import com.hunter.dribbble.base.mvp.BaseMVPListFragment;
import com.hunter.dribbble.entity.CommentEntity;
import com.hunter.dribbble.entity.ShotsEntity;
import com.hunter.lib.widget.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class ShotsCommentsFragment extends BaseMVPListFragment<ShotsCommentsPresenter, ShotsCommentsModel> implements
        ShotsCommentsContract.View {

    @BindView(R.id.rv_shots_detail_comments)
    RecyclerView       mRvShotsComments;
    @BindView(R.id.refresh_shots_detail_comments)
    SwipeRefreshLayout mRefresh;

    private int mShotsId;

    private CommentsAdapter mAdapter;

    public static ShotsCommentsFragment newInstance(ShotsEntity entity) {
        Bundle args = new Bundle();
        args.putSerializable(AppConstants.EXTRA_SHOTS_ENTITY, entity);
        ShotsCommentsFragment fragment = new ShotsCommentsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_shots_comments;
    }

    @Override
    protected void init(View view, Bundle savedInstanceState) {
        ShotsEntity shotsEntity = (ShotsEntity) getArguments().getSerializable(AppConstants.EXTRA_SHOTS_ENTITY);
        mShotsId = shotsEntity.getId();
        initList();
        setupList(mRefresh, mRvShotsComments, mAdapter);
    }

    @Override
    protected String getEmptyViewMsg() {
        return "还没有人评论哦";
    }

    @Override
    protected void requestData(boolean isRefresh) {
        super.requestData(isRefresh);
        mPresenter.getComments(mShotsId, mPage);
    }

    private void initList() {
        mAdapter = new CommentsAdapter(new ArrayList<CommentEntity>());
        mRvShotsComments.setAdapter(mAdapter);
        mRvShotsComments.setLayoutManager(new LinearLayoutManager(mActivity));
        mRvShotsComments.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.LIST_VERTICAL));
    }

    @Override
    public void getCommentsOnSuccess(List<CommentEntity> data) {
        setData(data, mAdapter);
    }

    @Override
    public void onSuccess() {
        mRefresh.setRefreshing(false);
        mRefresh.setEnabled(false);
    }
}