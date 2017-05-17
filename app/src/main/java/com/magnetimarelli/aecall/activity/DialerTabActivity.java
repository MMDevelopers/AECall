package com.magnetimarelli.aecall.activity;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.magnetimarelli.aecall.R;
import com.magnetimarelli.aecall.adaptor.PageAdaptor;
import com.magnetimarelli.aecall.fragment.Dialer;
import com.magnetimarelli.aecall.fragment.RecentCall;
import com.magnetimarelli.aecall.model.ContactModel;
import com.magnetimarelli.aecall.model.RecentCallModel;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;

public class DialerTabActivity extends AppCompatActivity {

    ViewPager mViewPager = null;
    TabLayout mTabLayout = null;
    public static Map<String, ContactModel> contactModelMap = new HashMap<String, ContactModel>();
    public static Map<String, RecentCallModel> logModelMap = new HashMap<String, RecentCallModel>();
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS_ALL = 101;
    private static final int PERMISSIONS_REQUEST_MODIFY_PHONE_STATE = 6544;
    private PageAdaptor lAdapter = null;
    MyTimerTask myTask = new MyTimerTask();
    Timer myTimer = new Timer();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialer_tab);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM, WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        // getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        mTabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        mTabLayout.addTab(mTabLayout.newTab().setText("Dial"));
        mTabLayout.addTab(mTabLayout.newTab().setText("Recent"));
        mTabLayout.addTab(mTabLayout.newTab().setText("Contacts"));
        mTabLayout.getTabAt(0).setIcon(R.drawable.ic_dial_pad);
        mTabLayout.getTabAt(1).setIcon(R.drawable.ic_recent_tab);
        mTabLayout.getTabAt(2).setIcon(R.drawable.ic_contact_tab);

        final ViewPager lViewPager = (ViewPager) findViewById(R.id.viewpager);
        lAdapter = new PageAdaptor(getSupportFragmentManager(), mTabLayout.getTabCount());
        lViewPager.setAdapter(lAdapter);
        lViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                lViewPager.setCurrentItem(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        myTask = new MyTimerTask();
        myTimer = new Timer();
        myTimer.schedule(myTask, 1000, 500);
    }


    @Override
    protected void onResume() {
        super.onResume();
        contactModelMap.clear();
        getContactsPerm();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS_ALL) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                new ContactOperation().execute("");
            }
        }
        if (requestCode == PERMISSIONS_REQUEST_MODIFY_PHONE_STATE) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this,"Can not accept call without this permisson",Toast.LENGTH_LONG).show();;
            }
        }
    }

    public void getContactsPerm() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && (checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS,Manifest.permission.READ_CALL_LOG,Manifest.permission.CALL_PHONE,Manifest.permission.READ_PHONE_STATE}, PERMISSIONS_REQUEST_READ_CONTACTS_ALL);
        } else {
            new ContactOperation().execute("");
        }
    }

    public void getContacts() {
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        String phone = null;
        String emailContact = null;
        String emailType = null;
        String image_uri = "";
        Bitmap bitmap = null;
        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                image_uri = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
                if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    ContactModel CM = new ContactModel();
                    CM.setContact_id(id);
                    CM.setDisplay_name(name);
                    //sb.append("\n Contact Name:" + name);
                    Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                    ArrayList<String> phones = new ArrayList<>();
                    while (pCur.moveToNext()) {
                        phone = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        //sb.append("\n Phone number:" + phone);
                        phones.add(phone);
                    }
                    pCur.close();
                    CM.setPhone(phones);
//                        Cursor emailCur = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", new String[]{id}, null);
//                        ArrayList<String> emails = new ArrayList<>();
//                        while (emailCur.moveToNext()) {
//                            emailContact = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
//                            emailType = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));
//                            emails.add(emailContact);
//                            //sb.append("\nEmail:" + emailContact + "Email type:" + emailType);
//                            //System.out.println("Email " + emailContact + " Email Type : " + emailType);
//                        }
//                        emailCur.close();
//                        CM.setEmailContact(emails);
                    if (image_uri != null) {
                        //System.out.println(Uri.parse(image_uri));
                        try {
                            //bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(image_uri));
                            //sb.append("\n Image in Bitmap:" + bitmap);
                            //System.out.println(bitmap);
                            CM.setImage_uri(image_uri);
                        } catch (Exception e) { // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
//                        catch (IOException e) { // TODO Auto-generated catch block
//                            e.printStackTrace();
//                        }
                    }
                    contactModelMap.put(phone, CM);
                    if(logModelMap.containsKey(phone)){
                        RecentCallModel value = (RecentCallModel) logModelMap.get(phone);
                        value.setImageUrl(CM.getImage_uri());
                        value.setName(CM.getDisplay_name());
                    }
                }
                //sb.append("\n........................................");
            }
            //Log.d("",sb+"");
        }
        //getCallLogs();
    }

    public void getCallLogs() {
        //StringBuffer sb = new StringBuffer();
        logModelMap.clear();
        RecentCall.logs.clear();
        ContentResolver cr = getContentResolver();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Cursor managedCursor = getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, null);
            int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
            int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
            int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
            int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
            //sb.append("Call Details :");
            if(managedCursor.getCount()>0) {
                managedCursor.moveToLast();
                while (managedCursor.moveToPrevious()) {
                    RecentCallModel RM = new RecentCallModel();
                    String phNumber = managedCursor.getString(number);
                    RM.setContact(phNumber);
                    String callType = managedCursor.getString(type);
                    String callDate = managedCursor.getString(date);
                    Date callDayTime = new Date(Long.valueOf(callDate));
                    String newstring = new SimpleDateFormat("dd/MM/yyyy hh:mm a").format(callDayTime);
                    RM.setDateTimes(newstring);
                    String callDuration = managedCursor.getString(duration);
                    long hours = Long.valueOf(callDuration) / 3600;
                    long minutes = (Long.valueOf(callDuration) % 3600) / 60;
                    long seconds = Long.valueOf(callDuration) % 60;
                    RM.setDuration(String.format("%02d:%02d:%02d", hours, minutes, seconds));
                    String dir = null;
                    int dircode = Integer.parseInt(callType);
                    int drawableId = 0;
                    switch (dircode) {
                        case CallLog.Calls.OUTGOING_TYPE:
                            dir = "OUTGOING";
                            drawableId = R.drawable.ic_outgoing_call_icon;
                            break;
                        case CallLog.Calls.INCOMING_TYPE:
                            dir = "INCOMING";
                            drawableId = R.drawable.ic_imcoming_call_icon;
                            break;
                        case CallLog.Calls.MISSED_TYPE:
                            dir = "MISSED";
                            drawableId = R.drawable.ic_missed_call_icon;
                            break;
                    }
                    RM.setCallType(drawableId);
//                sb.append("\nPhone Number:--- " + phNumber + " \nCall Type:--- "
//                        + dir + " \nCall Date:--- " + callDayTime
//                        + " \nCall duration in sec :--- " + callDuration);
//                sb.append("\n----------------------------------");
//                    if (contactModelMap.containsKey(phNumber)) {
//                        ContactModel value = (ContactModel) contactModelMap.get(phNumber);
//                        RM.setImageUrl(value.getImage_uri());
//                        RM.setName(value.getDisplay_name());
//                    }

                    RecentCall.logs.add(RM);
                    logModelMap.put(phNumber,RM);
                }
            }
            managedCursor.close();
        getContacts();
    }

    private class ContactOperation extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            getCallLogs();
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            lAdapter.tab2.adaptor.notifyDataSetChanged();
            myTimer.cancel();
            myTimer.purge();
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {

        }
    }

    class MyTimerTask extends TimerTask {
        public void run() {

        }
    }
}
