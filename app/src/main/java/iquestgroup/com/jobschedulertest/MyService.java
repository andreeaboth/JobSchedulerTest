package iquestgroup.com.jobschedulertest;

import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.SimpleJobService;

/**
 * @author Andreea Both
 */

public class MyService extends SimpleJobService {

    private static final String LOG_TAG = MyService.class.getSimpleName();

    @Override
    public boolean onStartJob(final JobParameters params) {
        super.onStartJob(params);
        Log.d(LOG_TAG, "onStartJob");
        new Thread(new Runnable() {
            @Override
            public void run() {
                codeYouWantToRun(params);
            }
        }).start();

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        super.onStopJob(job);
        Log.d(LOG_TAG, "onStopJob");
        return false;
    }

    @Override
    public int onRunJob(JobParameters job) {
        Log.d(LOG_TAG, "onRunJob");
        return 0;
    }

    public void codeYouWantToRun(final JobParameters parameters) {
        try {

            Log.d(LOG_TAG, "completeJob: " + "jobStarted");
            //This task takes 2 seconds to complete.
            Thread.sleep(2000);

            Log.d(LOG_TAG, "completeJob: " + "jobFinished : " + Math.random());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            //Tell the framework that the job has completed and does not needs to be reschedule
            jobFinished(parameters, true);
        }
    }
}
