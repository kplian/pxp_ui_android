package com.kplian.pxpui.retrofit;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.POST;

public interface GetAppVersion {
    @POST("lib/rest/seguridad/App/AndroidVersion")
    Call<ResponseBody> getAppVersion();
}
