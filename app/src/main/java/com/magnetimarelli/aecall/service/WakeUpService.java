package com.magnetimarelli.aecall.service;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.IntentService;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.util.Log;

import com.magnetimarelli.aecall.activity.MainActivity;
import com.magnetimarelli.aecall.broadcast.WakeUpFromBroadcast;
import com.magnetimarelli.aecall.model.ContactModel;

import java.util.List;

/**
 * Created by F49558B on 5/4/2017.
 */

public class WakeUpService  extends Service {
    private static final String TAG = "com.example.ServiceExample";
    public static int SERVICE_STATUS = -1;
    String phoneNumber = "";
    public WakeUpService() {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        Log.i(TAG, "Service onBind");
        return null;
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "Service onCreate");
        SERVICE_STATUS = 1;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i(TAG, "Service onStartCommand");
        phoneNumber = intent.getStringExtra("PHONE");
        ContactModel contactModel = getContactName(this,phoneNumber);
        long endTime = System.currentTimeMillis() + 1500;
        while (System.currentTimeMillis() < endTime) {
            synchronized (this) {
                try {
                    wait(endTime - System.currentTimeMillis());
                } catch (Exception e) {
                }
            }
        }

        Log.i(TAG, "Service running");
        SERVICE_STATUS = 2;
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
        try {
            Thread.sleep(250);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Intent in = new Intent(getApplicationContext(), MainActivity.class);
        in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);

        if(contactModel!=null){
            in.putExtra("PHONE_NUM",phoneNumber);
            in.putExtra("PHONE_NAME",contactModel.getDisplay_name());
            in.putExtra("CONTACT_IMG",contactModel.getImage_uri());
        }
        startActivity(in);

        Log.d("WakeUpService","Activity called");

        stopSelf();
        SERVICE_STATUS = -1;
        return Service.START_STICKY;
    }


    @Override
    public void onDestroy() {
        Log.i(TAG, "Service onDestroy");
        SERVICE_STATUS = -1;
    }


    private ContactModel getContactName(Context context, String number) {

        String name = null;
        String urlImage = null;
        ContactModel contactModel = null;

        // define the columns I want the query to return
        String[] projection = new String[] { ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup._ID,ContactsContract.PhoneLookup.PHOTO_URI};

        // encode the phone number and build the filter URI
        Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));

        // query time
        Cursor cursor = context.getContentResolver().query(contactUri, projection, null, null, null);

        if(cursor != null) {
            if (cursor.moveToFirst()) {
                contactModel = new ContactModel();
                name =      cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
                urlImage =      cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.PHOTO_URI));
                contactModel.setDisplay_name(name);
                contactModel.setImage_uri(urlImage);
                Log.v(TAG, "Started uploadcontactphoto: Contact Found @ " + number);
                Log.v(TAG, "Started uploadcontactphoto: Contact name  = " + name);
            } else {
                Log.v(TAG, "Contact Not Found @ " + number);
            }
            cursor.close();
        }
        return contactModel;
    }


    private Handler mhandler = null;

//    @Override
//    protected void onHandleIntent(Intent intent) {
//        // At this point SimpleWakefulReceiver is still holding a wake lock
//        // for us.  We can do whatever we need to here and then tell it that
//        // it can release the wakelock.  This sample just does some slow work,
//        // but more complicated implementations could take their own wake
//        // lock here before releasing the receiver's.
//        //
//        // Note that when using this approach you should be aware that if your
//        // service gets killed and restarted while in the middle of such work
//        // (so the Intent gets re-delivered to perform the work again), it will
//        // at that point no longer be holding a wake lock since we are depending
//        // on SimpleWakefulReceiver to that for us.  If this is a concern, you can
//        // acquire a separate wake lock here.
////        for (int i=0; i<5; i++) {
////            Log.i("SimpleWakefulReceiver", "Running service " + (i+1)
////                    + "/5 @ " + SystemClock.elapsedRealtime());
////            try {
////                Thread.sleep(5000);
////            } catch (InterruptedException e) {
////            }
////        }
////        Log.i("SimpleWakefulReceiver", "Completed service @ " + SystemClock.elapsedRealtime());
////        WakeUpFromBroadcast.completeWakefulIntent(intent);
//        Log.d("WakeUpService","onHandleIntent");
//
////        ActivityManager am = (ActivityManager) getSystemService(Activity.ACTIVITY_SERVICE);
////        List<ActivityManager.RunningServiceInfo> runningAppProcessInfo = am.getRunningServices(1000);
////
////        for (int i = 0; i < runningAppProcessInfo.size(); i++) {
////            Log.d("11WakeUpService",runningAppProcessInfo.get(i).process+ "\t\t ID: "+runningAppProcessInfo.get(i).service+"");
//////            if(runningAppProcessInfo.get(i).processName.equals("com.the.app.you.are.looking.for")) {
//////                // Do your stuff here.
//////            }
////        }
//
//        mhandler = new Handler();
//        mhandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Intent i = new Intent(getApplicationContext(), MainActivity.class);
//                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
//                        | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
//                startActivity(i);
//                Log.d("WakeUpService","Activity called");
//            }
//        },1500);
//    }
}
