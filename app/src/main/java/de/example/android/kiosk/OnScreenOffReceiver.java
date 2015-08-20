package de.example.android.kiosk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

public class OnScreenOffReceiver extends BroadcastReceiver {

    private static final String PREF_KIOSK_MODE = "pref_kiosk_mode";

    @Override
    public void onReceive(Context context, Intent intent) {


        if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
            AppContext ctx = (AppContext) context.getApplicationContext();


            // is Kiosk Mode active?
            if (PrefUtils.isKioskModeActive(ctx)) {
                wakeUpDevice(ctx);
                Intent i = new Intent(ctx, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(i);
            }
        }
    }

    private void wakeUpDevice(AppContext context) {
        PowerManager.WakeLock wakeLock = context.getWakeLock(); // get WakeLock reference via AppContext
        if (wakeLock.isHeld()) {
            wakeLock.release(); // release old wake lock


        }

        // create a new wake lock...
        wakeLock.acquire();


        // ... and release again
        wakeLock.release();
    }

}