package com.example.marcin.myapplication;

/**
 * Created by marcin on 06.11.15.
 */
import android.content.Context;

import com.loopj.android.http.*;

import org.apache.http.client.params.ClientPNames;
import org.apache.http.cookie.Cookie;

import java.net.CookieStore;
import java.util.List;

public class MyRestClient {
    private static final String BASE_URL = "http://nodejs-marolsze.rhcloud.com/products/";

    private  AsyncHttpClient client;
    private  PersistentCookieStore myCookieStore;

    public MyRestClient(Context context) {
        this.client = new AsyncHttpClient();
        myCookieStore = new PersistentCookieStore(context);
        client.getHttpClient().getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);

        client.setCookieStore(myCookieStore);
    }

    public void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    public void delete(String url, AsyncHttpResponseHandler responseHandler) {
        client.delete(getAbsoluteUrl(url), responseHandler);
    }

    public void put(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.put(getAbsoluteUrl(url), params, responseHandler);
    }

    private String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

    public List<cz.msebera.android.httpclient.cookie.Cookie> getCookies() {

        return myCookieStore.getCookies();
    }
}