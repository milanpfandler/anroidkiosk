package de.example.android.kiosk;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class KioskService extends Service{
  private static final long INTERVAL = TimeUnit.MILLISECONDS.toMillis(2); // periodic interval to check in seconds -> 2
    private static final String TAG = KioskService.class.getSimpleName();

    private Thread t = null;
    private Context ctx = null;
    private boolean running = false;



    @Override
    public void onDestroy() {
        Log.i(TAG, "Stopping service 'KioskService'");
        running = false;
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Starting service 'KioskService'");
        running = true;
        ctx = this;



        // start a thread that periodically checks if your app is in the foreground
        t = new Thread(new Runnable() {
            @Override
            public void run() {
                do {
                    handleKioskMode();
                    try {
                        Thread.sleep(INTERVAL);
                    } catch (InterruptedException e) {
                        Log.i(TAG, "Thread interrupted: 'KioskService'");
                    }
                } while (running);
                stopSelf();
            }
        });

        t.start();
        return Service.START_NOT_STICKY;
    }

private void handleKioskMode() {
        // is Kiosk Mode active?
        if (PrefUtils.isKioskModeActive(ctx)) {
            // is App in background?

            if (isInBackground()) {

                restoreApp();
            }
        }

    }



    private boolean isInBackground() {

        Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        sendBroadcast(closeDialog);

        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
        ComponentName componentInfo = taskInfo.get(0).topActivity;

        return (!ctx.getApplicationContext().getPackageName().equals(componentInfo.getPackageName()));

    }

    private void restoreApp() {

        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
        String ActivePackage = taskInfo.get(0).topActivity.getPackageName();


        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Set<String> myAps = prefs.getStringSet("AllowedApps", null);

        if(myAps !=null){

            if(!myAps.contains(ActivePackage)) {
            Intent h = new Intent(ctx, MainActivity.class);
            h.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(h);
            }
           else{
                    Log.v("This is cool","This is cool");
        }}

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}