package com.rhino.ui.utils.okhttp;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;


/**
 * @author LuoLin
 * @since Create on 2018/10/8.
 */
public class Callback implements okhttp3.Callback {

    @Override
    public void onFailure(Call call, IOException e) {

    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {

    }

    public void onProgressChanged(int progress) {

    }

    public void onSuccess(File file){

    }

    public void onError(String error) {

    }

}
