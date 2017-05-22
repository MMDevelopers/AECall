package com.magnetimarelli.aecall.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.RemoteException;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.ActionMenuItemView;
import android.support.v7.widget.AppCompatTextView;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.internal.telephony.ITelephony;
import com.magnetimarelli.aecall.R;
import com.magnetimarelli.aecall.view.CircularView;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.Permission;
import java.util.logging.Logger;

public class MainActivity extends AppCompatActivity {

    public static int ON_CALL = -1;//1 onforeground; 2 on pause; 3 on stop
    TelephonyManager telephonyManager;
    ImageButton acceptCall = null, rejectCall = null;
    RelativeLayout hangUpCall = null;
    ITelephony telephonyService;
    CircularView circularView = null;
    FrameLayout framelayout = null;
    TextView callerNameLetter = null, contactNumber = null, firstName = null,lastName =null ;
    RelativeLayout nameLayout = null;
    ConstraintLayout callActionLayout = null, hangUpCallLayout = null;
    AppCompatTextView toolBarText = null;

    Handler mhandler = null;
    long MillisecondTime = 0, StartTime = 0;
    long TimeBuff, UpdateTime = 0L ;
    int Seconds, Minutes, MilliSeconds ;

    private AudioManager audioManager;
    private static Logger logger = Logger.getLogger(String.valueOf(MainActivity.class));

    private static final String MANUFACTURER_HTC = "HTC";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("ACTIVITY","ONCreate");
        ON_CALL = 1;
        setContentView(R.layout.activity_main);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        acceptCall = (ImageButton)findViewById(R.id.imageButton6);
        rejectCall = (ImageButton)findViewById(R.id.imageButton5);
        hangUpCall = (RelativeLayout) findViewById(R.id.hangup_call_view);
        callActionLayout = (ConstraintLayout) findViewById(R.id.action_button_layout);
        hangUpCallLayout = (ConstraintLayout) findViewById(R.id.action_reject_button_layout);
        toolBarText = (AppCompatTextView) findViewById(R.id.header_title);


        circularView = (CircularView)findViewById(R.id.caller_image_view) ;
        framelayout = (FrameLayout)findViewById(R.id.caller_name_image) ;
        callerNameLetter = (TextView) findViewById(R.id.caller_name_text_F) ;
        contactNumber = (TextView)findViewById(R.id.contact_number_textview) ;
        firstName = (TextView)findViewById(R.id.first_name_caller) ;
        lastName = (TextView)findViewById(R.id.last_name_caller) ;
        nameLayout = (RelativeLayout)findViewById(R.id.caller_full_name);

        circularView.setVisibility(View.GONE);
        framelayout.setVisibility(View.VISIBLE);
        nameLayout.setVisibility(View.VISIBLE);
        callActionLayout.setVisibility(View.VISIBLE);
        hangUpCallLayout.setVisibility(View.GONE);
        firstName.setSelected(true);

        mhandler = new Handler();

        String number = getIntent().getStringExtra("PHONE_NUM");
        contactNumber.setText(number);
        String name = getIntent().getStringExtra("PHONE_NAME");
        if(name!=null)
        {
            String []names = name.split(" ");
            firstName.setText(name);
            String fl = name.substring(0,1);
            callerNameLetter.setText(fl);
        }


        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction("com.durga.action.close");
        mLocalBroadcastManager.registerReceiver(mBroadcastReceiver, mIntentFilter);

        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        Class clazz = null;
        try {
            clazz = Class.forName(telephonyManager.getClass().getName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Method method = null;
        try {
            method = clazz.getDeclaredMethod("getITelephony");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        method.setAccessible(true);
        try {
            telephonyService = (ITelephony) method.invoke(telephonyManager);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }


        rejectCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    telephonyService.endCall();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

        acceptCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Runtime.getRuntime().exec("input keyevent " + Integer.toString(KeyEvent.KEYCODE_HEADSETHOOK));
                    callActionLayout.setVisibility(View.GONE);
                    hangUpCallLayout.setVisibility(View.VISIBLE);
                    StartTime = SystemClock.uptimeMillis();
                    mhandler.postDelayed(runnable, 500);
                } catch (IOException e) {
                    e.printStackTrace();
                }
//                try {
//                    telephonyService.answerRingingCall();
//                } catch (RemoteException e) {
//                    e.printStackTrace();
//                }
            }
        });

        hangUpCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    telephonyService.endCall();
                    mhandler.removeCallbacks(runnable);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    LocalBroadcastManager mLocalBroadcastManager;
    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals("com.durga.action.close")){
                MainActivity.ON_CALL = -1;
                finish();
            }
        }
    };

    protected void onDestroy() {
        super.onDestroy();
        mLocalBroadcastManager.unregisterReceiver(mBroadcastReceiver);
        ON_CALL = -1;
        mhandler.removeCallbacks(runnable);
    }

    private void acceptCall() {

        // for HTC devices we need to broadcast a connected headset
        boolean broadcastConnected = MANUFACTURER_HTC.equalsIgnoreCase(Build.MANUFACTURER) && !audioManager.isWiredHeadsetOn();

        if (broadcastConnected) {
            broadcastHeadsetConnected(false);
        }

        try {
            try {
                Log.d("","execute input keycode headset hook");
                Runtime.getRuntime().exec("input keyevent " +Integer.toString(KeyEvent.KEYCODE_HEADSETHOOK));

            } catch (IOException e) {
                // Runtime.exec(String) had an I/O problem, try to fall back
                Log.d("","send keycode headset hook intents");
                String enforcedPerm = "android.permission.CALL_PRIVILEGED";
                Intent btnDown = new Intent(Intent.ACTION_MEDIA_BUTTON).putExtra(
                        Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN,
                                KeyEvent.KEYCODE_HEADSETHOOK));
                Intent btnUp = new Intent(Intent.ACTION_MEDIA_BUTTON).putExtra(
                        Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_UP,
                                KeyEvent.KEYCODE_HEADSETHOOK));

                sendOrderedBroadcast(btnDown, enforcedPerm);
                sendOrderedBroadcast(btnUp, enforcedPerm);
            }
        } finally {
            if (broadcastConnected) {
                broadcastHeadsetConnected(false);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("ACTIVITY_STATE","PAUSED");
//        Intent it = new Intent("intent.my.action");
//        it.setComponent(new ComponentName(getPackageName(), MainActivity.class.getName()));
//        it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        getApplicationContext().startActivity(it);
        ON_CALL = 2;
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("ACTIVITY_STATE","STOP");
//        Intent it = new Intent("intent.my.action");
//        it.setComponent(new ComponentName(getPackageName(), MainActivity.class.getName()));
//        it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        getApplicationContext().startActivity(it);
        ON_CALL = 3;
    }

    @Override
    protected void onStart() {
        super.onStart();
        String imageUrl = getIntent().getStringExtra("CONTACT_IMG");
        if (imageUrl != null) {
            System.out.println(Uri.parse(imageUrl));
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(imageUrl));
                circularView.setImageBitmap(bitmap);
                circularView.setVisibility(View.VISIBLE);
                framelayout.setVisibility(View.GONE);

            } catch (Exception e) { // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }else{
            String name = getIntent().getStringExtra("PHONE_NAME");
            if(name==null){
                nameLayout.setVisibility(View.GONE);
                circularView.setImageResource(R.drawable.ic_blue_avtar);
                circularView.setVisibility(View.VISIBLE);
                framelayout.setVisibility(View.GONE);
            }
        }

    }

    private void broadcastHeadsetConnected(boolean connected) {
        Intent i = new Intent(Intent.ACTION_HEADSET_PLUG);
        i.addFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY);
        i.putExtra("state", connected ? 1 : 0);
        i.putExtra("name", "mysms");
        try {
            sendOrderedBroadcast(i, null);
        } catch (Exception e) {
        }
    }
    public Runnable runnable = new Runnable() {

        public void run() {

            MillisecondTime = SystemClock.uptimeMillis() - StartTime;

            UpdateTime = TimeBuff + MillisecondTime;

            Seconds = (int) (UpdateTime / 1000);

            Minutes = Seconds / 60;

            Seconds = Seconds % 60;

            toolBarText.setText("" + Minutes + ":"
                    + String.format("%02d", Seconds));

            mhandler.postDelayed(this, 500);
        }

    };
}
