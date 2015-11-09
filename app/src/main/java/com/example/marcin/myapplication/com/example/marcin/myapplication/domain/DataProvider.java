package com.example.marcin.myapplication.com.example.marcin.myapplication.domain;

import android.content.Context;
import android.widget.Toast;

import com.example.marcin.myapplication.MyRestClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by marcin on 07.11.15.
 */
public class DataProvider {

    private final Context context;
    private final MyRestClient restClient;

    public DataProvider(Context context) {
        this.restClient = new MyRestClient(context);
        this.context = context;
    }

    public void initProducts(final InitProductsCallback renderProductsList) {

        restClient.get("", null, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                System.out.println(statusCode + " " + response.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {

                List<Product> productsToShow = new ArrayList<Product>();

                for (int i = 0; i < response.length(); i++) {
                    try {
                        productsToShow.add(
                                Product.fromJsonToJava(response.getJSONObject(i))
                        );
                    } catch (JSONException e) {
                        System.out.println(e.getMessage().toString());
                    }
                }

                renderProductsList.apply(productsToShow);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                Toast.makeText(context, "Check your internet connection.", Toast.LENGTH_LONG).show();
                System.out.println("products get:  " + throwable.toString() + " "+ throwable);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                System.out.println("products get:  "+ throwable);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                System.out.println(throwable);
            }
        });
    }

    public void createProduct(String productName, final CreateProductCallback createProduct) {

        RequestParams params = new RequestParams("name", productName);
        params.setUseJsonStreamer(true);

        restClient.post("", params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    createProduct.apply(Product.fromJsonToJava(response));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                Toast.makeText(context, "Sprawdź połączenie z Internetem..", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (statusCode == HttpURLConnection.HTTP_CONFLICT) {
                    Toast.makeText(context, "Produkt już istnieje.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void deleteProduct(final Product p, final ProductCallback deleteProductCallback) {

        restClient.delete(p.getId(), new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                deleteProductCallback.apply(p);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (statusCode == HttpURLConnection.HTTP_NOT_FOUND) {
                    Toast.makeText(context, "Produkt nie istnieje.", Toast.LENGTH_LONG).show();
                }
            }

        });

    }

    public void update(final Product p, final ProductCallback productCallback) {

        RequestParams params = new RequestParams("amount", p.getAmount());
        params.setUseJsonStreamer(true);

        restClient.put(p.getId(), params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                productCallback.apply(p);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (statusCode == HttpURLConnection.HTTP_NOT_FOUND) {
                    Toast.makeText(context, "Produkt nie istnieje.", Toast.LENGTH_LONG).show();
                }
            }

        });
    }

    public void signin(String login, String password) {
        RequestParams params = new RequestParams("username", login);
        params.add("password", password);
        params.setUseJsonStreamer(true);

        restClient.post("login", params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    System.out.println(response.getString("message"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                System.out.println(restClient.getCookies());

            }

        });
    }
}
