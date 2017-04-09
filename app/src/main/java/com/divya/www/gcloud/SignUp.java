package com.divya.www.gcloud;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.DataInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class SignUp extends AppCompatActivity {

    EditText etemail, etpassword, etcontact, etsecans, etconfirm;
    Spinner sp;
    String securityquestion;
    DataInputStream dis;


    String types[] = {"-----Select your action-----", "What is your hobby?", "What is your mother's maiden name?",
            "What is your nickname?", "Which is your favorite book?", "What is your pet name ?"};
    private String securityanswer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        etemail = (EditText) findViewById(R.id.etemail);
        etpassword = (EditText) findViewById(R.id.etpassword);
        etconfirm = (EditText) findViewById(R.id.etconfirm);
        etsecans = (EditText) findViewById(R.id.etsecans);
        etcontact = (EditText) findViewById(R.id.etcontact);

        sp = (Spinner) findViewById(R.id.spinner);


        ArrayAdapter<String> ad = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, types);
        sp.setAdapter(ad);


        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position != 0) {
                    securityquestion = types[position];
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    public void Submit(View v) {

        final String email = etemail.getText().toString();
        final String password = etpassword.getText().toString();
        final String confirmpassword = etconfirm.getText().toString();
        final String contact = etcontact.getText().toString();
        final String securityanswer = etsecans.getText().toString();
        final String secques = securityquestion.toString();

        if (password.equals(confirmpassword)) {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        URL url = new URL("http://192.168.43.192:8084/GCloud/mobile_signup");
                        URLConnection conn = url.openConnection();
                        conn.setRequestProperty("email", email);
                        conn.setRequestProperty("password", password);
                        conn.setRequestProperty("contact", contact);
                        conn.setRequestProperty("security_answer", securityanswer);
                        conn.setRequestProperty("security_question", secques);
                        conn.connect();


                        dis = new DataInputStream(conn.getInputStream());
                        String str = dis.readLine();

                        if (str.equals("fail")) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getBaseContext(), "Failed !", Toast.LENGTH_SHORT).show();
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

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
                else

                {
                    Toast.makeText(getBaseContext(), "Password Fields donot match !", Toast.LENGTH_SHORT).show();
                }
            }

    }
