package com.hunter.cookies.ui.profile.shots;

import com.hunter.cookies.api.ApiClient;
import com.hunter.cookies.api.ApiConstants;
import com.hunter.cookies.entity.ShotsEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;

public class ProfileShotsModel implements ProfileShotsContract.Model {

    @Override
    public Observable<List<ShotsEntity>> getUserShots(String id, int page) {
        Map<String, String> params = new HashMap<>();
        params.put(ApiConstants.ParamKey.PAGE, page + "");
        params.put(ApiConstants.ParamKey.PER_PAGE, ApiConstants.ParamValue.PAGE_SIZE + "");
        return ApiClient.getForRest().getUserShots(id, params);
    }
}
