package com.magnetimarelli.aecall.broadcast;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.magnetimarelli.aecall.activity.MainActivity;
import com.magnetimarelli.aecall.service.WakeUpService;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import static android.content.ContentValues.TAG;

/**
 * Created by F49558B on 5/4/2017.
 */
public class WakeUpFromBroadcast extends WakefulBroadcastReceiver {
    Context contex = null;
    Intent service = null;
    static long lastCall = 0;
    long receiveCall = 0;



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

                            //checktask();
                            //printForegroundTask();
                            Thread.sleep(500);
//                            ActivityManager am = (ActivityManager) contex.getSystemService(Activity.ACTIVITY_SERVICE);
//                            List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfo = am.getRunningAppProcesses();
//
//                            for (int i = 0; i < runningAppProcessInfo.size(); i++) {
//                                Log.d("00WakeUpService",runningAppProcessInfo.get(i).processName+ "\t\t ID: "+runningAppProcessInfo.get(i).pkgList[0]+"");
////            if(runningAppProcessInfo.get(i).processName.equals("com.the.app.you.are.looking.for")) {
////                // Do your stuff here.
////            }
//                            }

                            //incoming call

                            // Start the service, keeping the device awake while it is launching.
                            long CallTime = System.currentTimeMillis();
                            Log.d("CALLTIME","T:"+CallTime+" : "+(CallTime-lastCall) );

                            if(CallTime-lastCall>1000 && MainActivity.ON_CALL == -1 && WakeUpService.SERVICE_STATUS ==-1) {
                                Log.i("SimpleWakefulReceiver", "Starting service called 0@ " + SystemClock.elapsedRealtime());
                                service.putExtra("PHONE",incomingNumber);
                                startWakefulService(contex, service);
                                lastCall = CallTime;
                            }

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
                        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(contex);
                        localBroadcastManager.sendBroadcast(new Intent("com.durga.action.close"));
                        completeWakefulIntent(service);

                        break;
                    }
                    default: { }
                }
            } catch (Exception ex) {

            }
        }
    };

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("M-d-yyyy HH:mm:ss");
    private void printForegroundTask() {
        Calendar calendar = Calendar.getInstance();
        long endTime = System.currentTimeMillis();
        calendar.add(Calendar.YEAR, -1);
        long startTime = endTime-10000;//calendar.getTimeInMillis();

        Log.d("WakeUpFromBroadcast", "Range start:" + dateFormat.format(startTime) );
        Log.d("WakeUpFromBroadcast", "Range end:" + dateFormat.format(endTime));
        String currentApp = "";
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final UsageStatsManager usageStatsManager=(UsageStatsManager)contex.getSystemService(Context.USAGE_STATS_SERVICE);// Context.USAGE_STATS_SERVICE);
            final List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_YEARLY, startTime, endTime);
            if (queryUsageStats != null && queryUsageStats.size() > 0) {
                SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
                for (UsageStats usageStats : queryUsageStats) {
                    mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                    Log.d("222WakeUpFromBroadcast", usageStats.getPackageName()+" : "+usageStats.describeContents());
                }
                if (mySortedMap != null && !mySortedMap.isEmpty()) {
                    currentApp = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
                }
            }
        }
    }

    private void checktask(){
        try {

            // Using ACTIVITY_SERVICE with getSystemService(String)
            // to retrieve a ActivityManager for interacting with the global system state.

            ActivityManager am = (ActivityManager) contex
                    .getSystemService(Context.ACTIVITY_SERVICE);

            // Return a list of the tasks that are currently running,
            // with the most recent being first and older ones after in order.
            // Taken 1 inside getRunningTasks method means want to take only
            // top activity from stack and forgot the olders.

            List<ActivityManager.RunningTaskInfo> alltasks = am
                    .getRunningTasks(1);

            //
            for (ActivityManager.RunningTaskInfo aTask : alltasks) {


                // Used to check for CALL screen

                if (aTask.topActivity.getClassName().equals("com.android.phone.InCallScreen")
                        || aTask.topActivity.getClassName().equals("com.android.contacts.DialtactsActivity"))
                {
                    // When user on call screen show a alert message
                    Log.d("TASK", "Phone Call Screen.");
                }

                // Used to check for SMS screen

                if (aTask.topActivity.getClassName().equals("com.android.mms.ui.ConversationList")
                        || aTask.topActivity.getClassName().equals("com.android.mms.ui.ComposeMessageActivity"))
                {
                    Log.d("TASK", "sms Screen.");
                }


                // Used to check for CURRENT example main screen

                String packageName = "com.example.checkcurrentrunningapplication";

                if (aTask.topActivity.getClassName().equals(
                        packageName + ".Main"))
                {
                    Log.d("TASK", "Check Screen.");
                }


                // These are showing current running activity in logcat with
                // the use of different methods

                Log.i(TAG, "===============================");

                Log.i(TAG, "aTask.baseActivity: "
                        + aTask.baseActivity.flattenToShortString());

                Log.i(TAG, "aTask.baseActivity: "
                        + aTask.baseActivity.getClassName());

                Log.i(TAG, "aTask.topActivity: "
                        + aTask.topActivity.flattenToShortString());

                Log.i(TAG, "aTask.topActivity: "
                        + aTask.topActivity.getClassName());

                Log.i(TAG, "===============================");


            }

        } catch (Throwable t) {
            Log.i(TAG, "Throwable caught: "
                    + t.getMessage(), t);
        }
    }
    static String TAG = "TAG_LOG";
}