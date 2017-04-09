package com.divya.www.gcloud;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.io.DataInputStream;
import java.net.URL;
import java.net.URLConnection;

public class userhome extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userhome);
        TelephonyManager mngr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        final String imei = mngr.getDeviceId();
        final String modelno = android.os.Build.MODEL;
        final String manufacturer = android.os.Build.MANUFACTURER;
        SharedPreferences sf = getSharedPreferences("mydata", MODE_PRIVATE);
        final String email = sf.getString("email", "not found");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://192.168.43.192:8084/GCloud/device_signup");
                    URLConnection conn = url.openConnection();
                    conn.setRequestProperty("email", email);
                    conn.setRequestProperty("imei", imei);
                    conn.setRequestProperty("modelno", modelno);
                    conn.setRequestProperty("manufacturer", manufacturer);
                    conn.connect();


                    DataInputStream dis = new DataInputStream(conn.getInputStream());
                    String str = dis.readLine();

                    if (str.equals("fail")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getBaseContext(), "Already Registered !", Toast.LENGTH_SHORT).show();
                            }
                        });

                    } else if (str.equals("pass")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getBaseContext(), "Pass !", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    Uri contactsuri = ContactsContract.Contacts.CONTENT_URI;
                    Cursor c = getContentResolver().query(contactsuri, null, null, null, null);
                    final int nameColumnIndex = c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                    final int idColumnIndex = c.getColumnIndex(ContactsContract.Contacts._ID);
                    final int hasPhoneColumnIndex = c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);
                    while (c.moveToNext()) {
                        int id = c.getInt(idColumnIndex);
                        String displayName = c.getString(nameColumnIndex);
                        int hasPhoneNum = c.getInt(hasPhoneColumnIndex);
                        String phones = "";
                        if (hasPhoneNum == 1)
                        {

                            Uri uri2 = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
                            Cursor cnew = getContentResolver().query(uri2, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + id, null, null);

                            while (cnew.moveToNext()) {
                                String phoneNumber = cnew.getString(cnew.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                phones += phoneNumber + ",";
                            }
                        } else {
                            phones = "No phone number";
                        }
                        Log.d("MYMESSAGE", displayName + "," + phones);
                    }
                    String messages = "";
                    Uri inboxuri = Uri.parse("content://sms/inbox");
                    Cursor cursor = getContentResolver().query(inboxuri, null, null, null, null);
                    while (cursor.moveToNext()) {
                        final int COLUMNFORBODY = cursor.getColumnIndex("body");
                        final int COLUMNFORSENDER = cursor.getColumnIndex("address");
                        String messagebody = cursor.getString(COLUMNFORBODY);
                        String sender = cursor.getString(COLUMNFORSENDER);
                        messages += sender + ":";
                        messages += messagebody;
                        Log.d("MYMESSAGE", messages);
                    }
                    Uri callLogUri = Uri.parse("content://call_log/calls");
                    Cursor cursorCall = getContentResolver().query(callLogUri, null, null, null, null);
                    while (cursorCall.moveToNext()) {
                        String name = cursorCall.getString(cursorCall.getColumnIndex(android.provider.CallLog.Calls.CACHED_NAME));
                        String number = cursorCall.getString(cursorCall.getColumnIndex(android.provider.CallLog.Calls.NUMBER));
                        int duration = Integer.parseInt(cursorCall.getString(cursorCall.getColumnIndex(android.provider.CallLog.Calls.DURATION)));
                        String finalString = name + "," + number + "," + duration;
                        Log.d("MYMESSAGE", finalString);
                    }
                } catch (Exception exc) {
                    exc.printStackTrace();
                }
            }
        }).start();
    }
}
