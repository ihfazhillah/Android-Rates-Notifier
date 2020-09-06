package com.ihfazh.exchangeratenotifier;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class CurrencyWorker extends Worker {
    private Result resultStatus;

    private int NOTIFICATION_ID = 1;
    private String channelId = "Channel_01";
    private String channelName = "exchange_rates_channel";

    public CurrencyWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        CurrencyService currencyService = new CurrencyService();
        currencyService.setmCallback(new CurrencyService.CurrencyServiceCallback() {
            @Override
            public void setRate(String rate) {
                String title = "USD -> IDR";
                String message = "USD 1 == IDR " + rate;

                showNotification(title, message);
                resultStatus = Result.success();
            }

            @Override
            public void onError(String error) {
                resultStatus = Result.failure();
            }
        });

        currencyService.getRate("USD", "IDR");

        return resultStatus;
    }

    private void showNotification(String title, String message) {
        Context context = getApplicationContext();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Uri ringtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId);
        builder
                .setContentTitle(title)
                .setContentText(message)
                .setColor(ContextCompat.getColor(context, android.R.color.black))
                .setVibrate(new long[] {2000, 1000, 2000, 4000, 1000, 10000})
                .setSmallIcon(R.drawable.ic_baseline_money_24)
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
            notificationManager.notify(NOTIFICATION_ID, notification);
        }

    }
}
