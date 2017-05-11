package com.magnetimarelli.aecall.broadcast;

import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.magnetimarelli.aecall.service.WakeUpService;
/**
 * Created by F49558B on 5/4/2017.
 */
public class WakeUpFromBroadcast extends WakefulBroadcastReceiver {
    Context contex = null;
    Intent service = null;
    @Override
    public void onReceive(Context context, Intent intent) {
        this.contex = context;
        // This is the Intent to deliver to our service.
        // TELEPHONY MANAGER class object to register one listner
        service = new Intent(contex, WakeUpService.class);
        TelephonyManager tmgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        //Create Listner
        //PhoneStateListener PhoneListener = new PhoneStateListener();
        // Register listener for LISTEN_CALL_STATE

        tmgr.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);
    }
    private final PhoneStateListener phoneListener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            try {
                switch (state) {
                    case TelephonyManager.CALL_STATE_RINGING: {
                        if(incomingNumber!=null) {
                            //incoming call

                            // Start the service, keeping the device awake while it is launching.
                            Log.i("SimpleWakefulReceiver", "Starting service @ " + SystemClock.elapsedRealtime());
                            startWakefulService(contex, service);
                        }
                        break;
                    }
                    case TelephonyManager.CALL_STATE_OFFHOOK: {
                        // Newly added code
                        if(incomingNumber!=null) {
                            //outgoing call
                        }
                        break;
                    }
                    case TelephonyManager.CALL_STATE_IDLE: {
                        //idea.
                        completeWakefulIntent(service);
                        break;
                    }
                    default: { }
                }
            } catch (Exception ex) {

            }
        }
    };
}