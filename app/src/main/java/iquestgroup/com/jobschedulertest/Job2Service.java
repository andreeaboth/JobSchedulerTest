package iquestgroup.com.jobschedulertest;

import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.firebase.jobdispatcher.SimpleJobService;

/**
 * @author Andreea Both
 */

public class Job2Service extends SimpleJobService {

    private static final String LOG_TAG = Job2Service.class.getSimpleName();

    @Override
    public boolean onStartJob(final JobParameters job) {
        super.onStartJob(job);

        Log.d(LOG_TAG, "onStartJob: " + job.getTag().toString());
        new Thread(new Runnable() {
            @Override
            public void run() {
                codeYouWantToRun(job);
            }
        }).start();

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        super.onStopJob(job);
        return false;
    }

    @Override
    public int onRunJob(JobParameters job) {
        return 0;
    }

    private void codeYouWantToRun(JobParameters job) {
        try {

            Log.d(LOG_TAG, "completeJob: " + "jobStarted");
            //This task takes 2 seconds to complete.
            Thread.sleep(2000);

            Log.d(LOG_TAG, "completeJob: " + "jobFinished : " + Math.random() * 100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            //Tell the framework that the job has completed and does not needs to be reschedule
            jobFinished(job, true);
        }
    }
}
