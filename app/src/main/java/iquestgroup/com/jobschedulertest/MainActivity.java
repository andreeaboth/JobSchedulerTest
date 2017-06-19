package iquestgroup.com.jobschedulertest;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.JobService;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private boolean askedForBatteryOptimization = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate()");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            final Intent intent = new Intent();
            final String packageName = getPackageName();
            final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);

            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Disable doze");
                builder.setCancelable(false);
                builder.setMessage("disable doze");
                builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                            if (!askedForBatteryOptimization) {
                                if (pm.isIgnoringBatteryOptimizations(packageName)) {
                                } else {
                                    intent.setAction(android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                                    intent.setData(Uri.parse("package:" + packageName));
                                    startActivityForResult(intent, 1);
                                    askedForBatteryOptimization = true;
                                }
                            } else {
                                if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                                    finish();
                                } else {
                                }
                            }
                    }
                });

                final AlertDialog alertDialog = builder.create();
                alertDialog.setCanceledOnTouchOutside(false);
                if (!alertDialog.isShowing())
                    if (!askedForBatteryOptimization)
                        alertDialog.show();
                    else if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                        AlertDialog.Builder mbuilder = new AlertDialog.Builder(this);
                        mbuilder.setTitle("Disable doze");
                        mbuilder.setCancelable(false);
                        mbuilder.setMessage("Disable doze");
                        mbuilder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                        final AlertDialog mCloseAlertDialog = mbuilder.create();
                        mCloseAlertDialog.setCanceledOnTouchOutside(false);
                        mCloseAlertDialog.show();
                    }
            }
        }
        scheduleJob(this);
    }

    public static void scheduleJob(Context context) {
        Log.d(TAG, "scheduleJob");
        //creating new firebase job dispatcher
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));
        dispatcher.cancelAll();
        //creating new job and adding it with dispatcher
        Job job = createJob1(dispatcher);
        dispatcher.mustSchedule(job);
    }

    public static Job createJob1(FirebaseJobDispatcher dispatcher) {
        Log.d(TAG, "createJob1");
        Job job = dispatcher.newJobBuilder()
                //persist the task across boots
                .setLifetime(Lifetime.FOREVER)
                //.setLifetime(Lifetime.UNTIL_NEXT_BOOT)
                //call this service when the criteria are met.
                .setService(MyService.class)
                //unique id of the task
                .setTag("UniqueTagForYourJob")
                //don't overwrite an existing job with the same tag
                .setReplaceCurrent(false)
                // We are mentioning that the job is periodic.
                .setRecurring(true)
                // Run between 30 - 60 seconds from now.
                .setTrigger(Trigger.executionWindow(0, 10))
                // retry with exponential backoff
                .setRetryStrategy(RetryStrategy.DEFAULT_LINEAR)
                //.setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                //Run this job only when the network is available.
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .build();
        return job;
    }
}
