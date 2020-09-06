package com.ihfazh.exchangeratenotifier;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

import cz.msebera.android.httpclient.Header;

public class GetCurrencyJobService extends JobService {
    private final String TAG = GetCurrencyJobService.class.getSimpleName();
    private String apiKey = BuildConfig.OPEN_EXCHANGE_RATES;

    private String BASE_URL = "https://openexchangerates.org/api/latest.json";

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.d(TAG, "Job Executed");
        getCurrency(jobParameters);
        return true;
    }

    private void getCurrency(final JobParameters jobParameters) {
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

                    String title = "Konversi Rate USD -> IDR saat ini";
                    String description = "USD -> IDR = " + rateDecimal;

                    int notifId = 100;

                    showNotification(getApplicationContext(), title, description, notifId);
                    jobFinished(jobParameters, false);

                } catch (JSONException e) {
                    e.printStackTrace();
                    jobFinished(jobParameters, true);
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                jobFinished(jobParameters, true);
            }
        });
    }

    private void showNotification(Context context, String title, String description, int notifId) {
        String channelId = "Channel_1";
        String channelName = "Job Scheduler Channel";

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Uri ringtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId);
        builder
                .setContentTitle(title)
                .setContentText(description)
                .setColor(ContextCompat.getColor(context, android.R.color.black))
                .setVibrate(new long[] {2000, 1000, 2000, 4000, 1000})
                .setSound(ringtone);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[] {2000, 1000, 2000, 4000, 1000});

            builder.setChannelId(channelId);

            if (notificationManager != null){
                notificationManager.createNotificationChannel(channel);
            }
        }

        Notification notification = builder.build();

        if (notificationManager != null){
            notificationManager.notify(notifId, notification);
        }

    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }
}
