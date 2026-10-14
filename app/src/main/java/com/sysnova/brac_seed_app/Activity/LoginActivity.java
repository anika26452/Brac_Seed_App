package com.sysnova.brac_seed_app.Activity;

import static com.sysnova.brac_seed_app.Activity.PostgreSQL.apiUrl;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

//import com.sysnova.brac_seed_app.DB.DatabaseHelper;
import com.sysnova.brac_seed_app.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class LoginActivity extends AppCompatActivity {

    EditText userNameEditText,passwordEditText;
    TextView version_editText;
    CheckBox rememberCkBox;
    Button loginButton;
    String password;
    public static int ad_user_id1 = 0;
    private SharedPreferences rememberLoginPreference;
    private SharedPreferences.Editor rememberLoginPreferenceEditor;
    boolean rememberLogin = false;
    String logged_user_id="";
    DatabaseHelper mDatabaseHelper;
    ProgressDialog pd;
    public static String employeeid="",currentdate="",ad_user_id = "",userName="",feed_org_id="",userbp_id="";
    public static String Version="4.2" ; //"3.7";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().setTitle("Brac Seed Sales App");
        getSupportActionBar().setBackgroundDrawable(
                new ColorDrawable(Color.parseColor("#97c740")));

        //Shared Preference
        rememberLoginPreference = getSharedPreferences("loginPrefsCustomerVisit",MODE_PRIVATE);
        rememberLoginPreferenceEditor = rememberLoginPreference.edit();

        mDatabaseHelper=DatabaseHelper.getInstance(this);

        initializeUI();
        clickLoginButton();
        loadFromSharedPreferences();
    }

    private void initializeUI(){
        userNameEditText = findViewById(R.id.employeeID_editText);
        passwordEditText = findViewById(R.id.password_editText);

        rememberCkBox = findViewById(R.id.remember_checkbox);
        loginButton   = findViewById(R.id.login_button);
        version_editText=(TextView)findViewById(R.id.version_editText);

        version_editText.setText("Version : "+Version);
    }

    private void loadFromSharedPreferences(){
        rememberLogin = rememberLoginPreference.getBoolean("rememberLogin",false);
        if(rememberLogin){
            userNameEditText.setText(rememberLoginPreference.getString("userName",""));
            passwordEditText.setText(rememberLoginPreference.getString("password",""));
            rememberCkBox.setChecked(true);
        }
    }

    private void clickLoginButton(){
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userName = userNameEditText.getText().toString();
                password = passwordEditText.getText().toString();
                employeeid = userNameEditText.getText().toString().trim();

                if(userName.isEmpty()){
                    userNameEditText.setError("Please Enter user Name");
                    return;
                }

                if(password.isEmpty()){
                    passwordEditText.setError("Please Enter Password");
                    return;
                }

                if(rememberCkBox.isChecked()){

                    rememberLoginPreferenceEditor.putBoolean("rememberLogin",true);
                    rememberLoginPreferenceEditor.putString("userName",userName);
                    rememberLoginPreferenceEditor.putString("password",password);
                    rememberLoginPreferenceEditor.commit();
                }else {
                    rememberLoginPreferenceEditor.clear();
                    rememberLoginPreferenceEditor.commit();
                }
                AysncLogin task =new AysncLogin();
                task.execute();
            }
        });
    }
    private final class AysncLogin extends AsyncTask<String, String, String> {

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }

        @Override
        protected String doInBackground(String... params) {

            String result="",error="";boolean r=false;
            try {
                mDatabaseHelper.deleteEmployee();
            } catch (Exception e) {
            }
            try {
                // mDatabaseHelper.deleteEmployee();
                HttpURLConnection con = (HttpURLConnection) ( new URL(""+apiUrl+"/Login")).openConnection();
                try {
                    con.setRequestMethod("POST");
                    con.setDoInput(true);
                    con.setDoOutput(true);
                    con.connect();
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost("" + apiUrl + "/Login");

                    List<NameValuePair> list = new ArrayList<NameValuePair>();
                    list.add(new BasicNameValuePair("name", employeeid));
                    list.add(new BasicNameValuePair("pass", password));
                    httpPost.setEntity(new UrlEncodedFormEntity(list));
                    HttpResponse httpResponse = httpClient.execute(httpPost);

                    HttpEntity httpEntity = httpResponse.getEntity();
                    String s = readResponse(httpResponse);
                    int y = s.length();
                    if (s.isEmpty() || !s.startsWith("[")) {
                        result = s;
                        Log.d("DEBUG", "Result " + result);
                        return result;
                    }
                    ArrayList list4 = new ArrayList();

                    String[] parts = s.split("_");
                    for (int i = 0; i < parts.length; i++) {
                        Vector line1 = new Vector();

                        String[] parts1 = parts[i].split(",");
                        for (int j = 0; j < parts1.length; j++) {
                            String value = parts1[j].replaceFirst("[\\[\\]\\\" \"\\^:,]", "");
                            value = value.replaceAll("[\\[\\]]", "");
                            line1.add(value);

                        }
                        list4.add(line1);
                    }
                    String branch_name = "", branch_id = "";
                    for (int i1 = 0; i1 < list4.size(); i1++) {
                        Vector line = (Vector) list4.get(i1);
                        ad_user_id = (String) line.get(0);
                        String bpartner_id = (String) line.get(1);
                        currentdate = (String) line.get(2);
                        feed_org_id = (String) line.get(3);
                        userbp_id = bpartner_id;

                        logged_user_id = ad_user_id;

                        r = mDatabaseHelper.insertIntoEmployee(ad_user_id, bpartner_id, "", currentdate, feed_org_id);
                        Log.d("DEBUG", "***************** ");
                        Log.d("DEBUG", "ad_user_id " + ad_user_id);
                        Log.d("DEBUG", "bpartner_id " + bpartner_id);
                        Log.d("DEBUG", "***************** ");
                    }

                    if (r) {
                        result = "Success";
                    } else {
                        error = "Failed to insert into Employee Table";
                        return error;
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                    System.out.println(e.getMessage());
                    //toastMessage(e.getMessage());
                }
                finally
                {
                    try {
                        if (con != null){
                            con.disconnect();
                            con=null;
                        }
                    }catch (Exception e){
                    }
                }
            }
            catch (ClientProtocolException e) {
                error=e.getMessage();
                return  error;
            } catch (UnsupportedEncodingException e) {
                error=e.getMessage();
                return  error;
            } catch (IOException e) {
                error="Failed To Connect";
                return  error;
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dismissProgressDialog();
            try{
                if(s.equals("Success")) {
                    finish();
                    Intent activityintent = new Intent(LoginActivity.this, Home.class);
                    activityintent.putExtra("LOGGED_USER_ID",logged_user_id);
                    activityintent.putExtra("userName",userName);
                    startActivity(activityintent);
                } else if(s.equals("Employee ID or Password Did not Matched!??")){
                    showAlert("Login Error", "Employee ID or Password Did not Match!!!");
                }
                else {
                    showAlert("Login Error","Please Check Your Internet Connection");
                }
            }catch (NullPointerException ex){
                showAlert("Error",ex.getMessage());
            }
        }
    }
    public String readResponse(HttpResponse res) {
        InputStream is=null;
        String return_text="",a="";
        try {
            is=res.getEntity().getContent();
            BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(is));
            String line="";
            StringBuffer sb=new StringBuffer();
            while ((line=bufferedReader.readLine())!=null)
            {

                //sb.append(line);
                a=line;
            }
            return_text=sb.toString();
        } catch (Exception e)
        {
            a = "Error" + e.getMessage();
            return a;
        }
        return a;
    }



    private void toastMessage(String message) {
        Toast toast = Toast.makeText(LoginActivity.this,message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.BOTTOM, 0, 0);
        toast.show();
    }


    public void showAlert(String title,String message){
        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(LoginActivity.this);

        dlgAlert.setMessage(message);
        dlgAlert.setTitle(title);
        dlgAlert.setPositiveButton("OK", null);
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
    }

    private void showProgressDialog(){
        if(pd == null){
            pd = new ProgressDialog(LoginActivity.this);
            pd.setTitle("Please Wait");
            pd.setMessage("Logging in.....");
            pd.setCancelable(false);
            pd.setIndeterminate(true);
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        }
        pd.show();
    }

    private void dismissProgressDialog(){
        if (pd != null && pd.isShowing()) {
            pd.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        dismissProgressDialog();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    public void ShowAlert(String title,String msg){


        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(LoginActivity.this);

        dlgAlert.setMessage(msg);
        dlgAlert.setTitle(title);
        dlgAlert.setIcon(R.drawable.seed);
        dlgAlert.setPositiveButton("OK", null);
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
    }

}