package com.magnetimarelli.aecall.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

import com.magnetimarelli.aecall.activity.MainActivity;
import com.magnetimarelli.aecall.broadcast.WakeUpFromBroadcast;

/**
 * Created by F49558B on 5/4/2017.
 */

public class WakeUpService  extends IntentService {
    public WakeUpService() {
        super("WakeUpService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // At this point SimpleWakefulReceiver is still holding a wake lock
        // for us.  We can do whatever we need to here and then tell it that
        // it can release the wakelock.  This sample just does some slow work,
        // but more complicated implementations could take their own wake
        // lock here before releasing the receiver's.
        //
        // Note that when using this approach you should be aware that if your
        // service gets killed and restarted while in the middle of such work
        // (so the Intent gets re-delivered to perform the work again), it will
        // at that point no longer be holding a wake lock since we are depending
        // on SimpleWakefulReceiver to that for us.  If this is a concern, you can
        // acquire a separate wake lock here.
//        for (int i=0; i<5; i++) {
//            Log.i("SimpleWakefulReceiver", "Running service " + (i+1)
//                    + "/5 @ " + SystemClock.elapsedRealtime());
//            try {
//                Thread.sleep(5000);
//            } catch (InterruptedException e) {
//            }
//        }
//        Log.i("SimpleWakefulReceiver", "Completed service @ " + SystemClock.elapsedRealtime());
//        WakeUpFromBroadcast.completeWakefulIntent(intent);
        Intent i = new Intent(this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        startActivity(i);
    }
}
