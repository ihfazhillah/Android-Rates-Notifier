package com.ihfazh.exchangeratenotifier;

import android.app.job.JobParameters;
import android.app.job.JobService;

public class GetCurrencyJobService extends JobService {
    private final String TAG = GetCurrencyJobService.class.getSimpleName();
    private String apiKey = BuildConfig.FIXER_IO_API_KEY;

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }
}
