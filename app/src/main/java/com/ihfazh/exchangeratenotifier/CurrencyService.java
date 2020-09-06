package com.ihfazh.exchangeratenotifier;


import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

import cz.msebera.android.httpclient.Header;



public class CurrencyService {
    private final String TAG = CurrencyService.class.getSimpleName();
    private String apiKey = BuildConfig.OPEN_EXCHANGE_RATES;

    private String BASE_URL = "https://openexchangerates.org/api/latest.json";

    private CurrencyServiceCallback mCallback;

    public void getRate(String base, String symbol){
        Log.d(TAG, "Job running...");
        AsyncHttpClient client = new AsyncHttpClient();
        String url = BASE_URL + "?app_id=" + apiKey + "&base=USD&symbols=IDR";
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                Log.d(TAG, result);

                try {
                    JSONObject resultObject = new JSONObject(result);
                    double rate = resultObject.getJSONObject("rates").getDouble("IDR");
                    String rateDecimal = new DecimalFormat("##.##").format(rate);

                    mCallback.setRate(rateDecimal);

                } catch (JSONException e) {
                    e.printStackTrace();
                    mCallback.onError(e.getMessage());
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                mCallback.onError(new String(responseBody));
            }
        });

    }

    public void setmCallback(CurrencyServiceCallback mCallback) {
        this.mCallback = mCallback;
    }

    public interface CurrencyServiceCallback{
        void setRate(String rate);
        void onError(String error);
    }
}
