package com.sysnova.brac_seed_app.Activity;

import static com.sysnova.brac_seed_app.Activity.PostgreSQL.apiUrl;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.sysnova.brac_seed_app.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import androidx.appcompat.app.AppCompatActivity;

public class OrgBranchSelection extends AppCompatActivity {
    Spinner orgSpinner,branchSpinner;
    DatabaseHelper mDatabaseHelper;
    boolean doubleBackToExitPressedOnce=false;
    public static String selectedOrgId="",selectedWHId="",selectedOrgName="",selectedWHName="";
    String documentNo="";

    List<String> orgNameArrayList = new ArrayList<String>();
    List<String> branchNameArrayList = new ArrayList<String>();

    HashMap<String,String> hashOrgId  = new HashMap<>();
    HashMap<String,String> hashBranchId  = new HashMap<>();

    String logged_user_id="";
    Connection conn = null;
    ResultSet rs = null, rs2 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_org_branch_selection);
        /**designing code**/
        getSupportActionBar().setTitle("Select org & branch");
      //  getSupportActionBar().setSubtitle("Brac Fisheries App");
        getSupportActionBar().setBackgroundDrawable(
                new ColorDrawable(Color.parseColor("#0b79bf")));
        /**designing code**/

        orgSpinner    = findViewById(R.id.org_spinner);
        branchSpinner = findViewById(R.id.branch_spinner);
        mDatabaseHelper=new DatabaseHelper(OrgBranchSelection.this);
        AsyncSender asyncSender=new AsyncSender();
        asyncSender.execute();
       // loadOrganizationSpinner();
       // loadWarehouseSpinner();
    }
    public void callConfirm(View view) {
        Intent i=new Intent(OrgBranchSelection.this,Home.class);
       // i.putExtra("DOCNO",documentNo);
        startActivity(i);
    }

    public void callCancel(View view) {
        Intent i=new Intent(OrgBranchSelection.this,LoginActivity.class);
        startActivity(i);

    }
    private void loadOrganizationSpinner(){

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, orgNameArrayList);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        orgSpinner.setAdapter(dataAdapter);
        dataAdapter.notifyDataSetChanged();
        selectOrgSpinner();

    }
    private void loadWarehouseSpinner(){
       /*** List<String> salesTypeArrayList = new ArrayList<String>();
        salesTypeArrayList.add("dumuria warehouse");
        salesTypeArrayList.add("Barishal warehouse");
        salesTypeArrayList.add("dinajpur warehouse");
        salesTypeArrayList.add("Sreemongal warehouse");**/
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, branchNameArrayList);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        branchSpinner.setAdapter(dataAdapter);
        dataAdapter.notifyDataSetChanged();
        selectWHSpinner();

    }
    private void getOrg()
    {
        Cursor cursor = mDatabaseHelper.getOrganization();

        if(cursor.moveToFirst()){
            do{
                String   orgId   = cursor.getString(0);
                String orgName          = cursor.getString(1);
                orgNameArrayList.add(orgName);
                hashOrgId.put(orgName,orgId);
            }while(cursor.moveToNext());
        }
        loadOrganizationSpinner();

    }
    private void getWH()
    {
        if(!branchNameArrayList.isEmpty())
        {
            branchNameArrayList.clear();
        }
        Cursor cursor = mDatabaseHelper.getWarehouseByOrg(selectedOrgId);

        if(cursor.moveToFirst()){
            do{
                String   warehouseID   = cursor.getString(1);
                String warehouseName          = cursor.getString(3);
                branchNameArrayList.add(warehouseName);
                hashBranchId.put(warehouseName,warehouseID);
            }while(cursor.moveToNext());
        }
        loadWarehouseSpinner();
    }
    private void selectOrgSpinner(){
        orgSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    String selectedOrg = parent.getSelectedItem().toString();
                    selectedOrgName=selectedOrg;
                    selectedOrgId = hashOrgId.get(selectedOrg);
                    toastMessage("org="+selectedOrg+" id="+selectedOrgId);
                    getWH();

                }catch (NullPointerException ex){

                }catch (ArrayIndexOutOfBoundsException ex){

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    private void selectWHSpinner(){
        branchSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    String selectedOrg = parent.getSelectedItem().toString();
                    selectedWHName=selectedOrg;
                   selectedWHId= hashBranchId.get(selectedWHName);
                   // selectedOrgId = hashOrgId.get(selectedOrg);
                    toastMessage("org="+selectedOrg);
                    getWH();

                }catch (NullPointerException ex){

                }catch (ArrayIndexOutOfBoundsException ex){

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

   public  String SYNCORGBRANCH()
   {
       String res = "";
       try {
           mDatabaseHelper.deleteOrganization();
           // toastMessage("Delete Promo Tools Successfull");
       } catch (Exception e) {

           //Toast.makeText(getApplicationContext(),""+ e.getMessage(),Toast.LENGTH_LONG).show();
           res = e.getMessage();
           return res;
       }
       try {
           mDatabaseHelper.deleteWarehouse();
           // toastMessage("Delete Promo Tools Successfull");
       } catch (Exception e) {

           //Toast.makeText(getApplicationContext(),""+ e.getMessage(),Toast.LENGTH_LONG).show();
           res = e.getMessage();
           return res;
       }
       res = FetchAllOrganization();
       //System.out.println("User-ID "+ad_user_id);

       res = FetchAllWarehouse();

       try {
                   return res;

       } catch (Exception e){
           res=e.getMessage();
       }
       return res;
   }
    private String FetchAllOrganization() {

        String ad_org_id="",org_name="",res = "";


        boolean result = false;

        try {

            HttpURLConnection con = (HttpURLConnection) ( new URL(""+apiUrl+"/DBSync")).openConnection();
            con.setRequestMethod("POST");
            con.setDoInput(true);
            con.setDoOutput(true);
            con.connect();

            HttpClient httpClient=new DefaultHttpClient();
            HttpPost httpPost=new HttpPost(""+apiUrl+"/DBSync");

            ArrayList list13=new ArrayList();
            List<NameValuePair> list=new ArrayList<NameValuePair>();
            list.add(new BasicNameValuePair("name", "allorg"));
           // list.add(new BasicNameValuePair("userId", user_id));
            httpPost.setEntity(new UrlEncodedFormEntity(list));
            HttpResponse httpResponse=	httpClient.execute(httpPost);



            HttpEntity httpEntity=httpResponse.getEntity();
            String s= readResponse(httpResponse);
            if (s.isEmpty() || !s.startsWith("[")){
                res="Org Not Found";
                return res;
            }


            ArrayList list4=new ArrayList();


            String[] parts = s.split("_");
            for(int i=0;i<parts.length;i++){
                Vector line=new Vector();
                String[] parts1 = parts[i].split(",");
                for(int j=0;j<parts1.length;j++){

                    String value=parts1[j].replaceFirst("[\\[\\]\\\" \"\\^:,]","");
                    value=value.replaceAll("[\\[\\]]","");
                    line.add(value);

                }
                list4.add(line);
            }



            for(int i1=0;i1<list4.size();i1++){
                Vector line= (Vector) list4.get(i1);
                org_name =((String) line.get(0));
                ad_org_id=(String) line.get(1);

                Log.d("DEBUG","ORG: "+org_name+" "+ad_org_id);
                result =mDatabaseHelper.insertIntoOrganization(ad_org_id,org_name) ;

            }
            if (result) {
                //toastMessage("Insert  Successful into Doctor Table : ");
                res = "Organization Sync Successful";
            }


        } catch (Exception e) {
//            e.printStackTrace();
//            Toast.makeText(getApplicationContext(),"Failed to Data fetch",Toast.LENGTH_SHORT).show();
            res = e.getMessage();

        }
        return res;
    }
    private String FetchAllWarehouse() {

        String ad_org_id="",warehouse_id="",res = "",warehouse_name="";


        boolean result = false;

        try {
            hashBranchId.clear();

            HttpURLConnection con = (HttpURLConnection) ( new URL(""+apiUrl+"/DBSync")).openConnection();
            con.setRequestMethod("POST");
            con.setDoInput(true);
            con.setDoOutput(true);
            con.connect();

            HttpClient httpClient=new DefaultHttpClient();
            HttpPost httpPost=new HttpPost(""+apiUrl+"/DBSync");

            ArrayList list13=new ArrayList();
            List<NameValuePair> list=new ArrayList<NameValuePair>();
            list.add(new BasicNameValuePair("name", "allwarehouse"));
            // list.add(new BasicNameValuePair("userId", user_id));
            httpPost.setEntity(new UrlEncodedFormEntity(list));
            HttpResponse httpResponse=	httpClient.execute(httpPost);



            HttpEntity httpEntity=httpResponse.getEntity();
            String s= readResponse(httpResponse);
            if (s.isEmpty() || !s.startsWith("[")){
                res="WH Not Found";
                return res;
            }


            ArrayList list4=new ArrayList();


            String[] parts = s.split("_");
            for(int i=0;i<parts.length;i++){
                Vector line=new Vector();
                String[] parts1 = parts[i].split(",");
                for(int j=0;j<parts1.length;j++){

                    String value=parts1[j].replaceFirst("[\\[\\]\\\" \"\\^:,]","");
                    value=value.replaceAll("[\\[\\]]","");
                    line.add(value);

                }
                list4.add(line);
            }



            for(int i1=0;i1<list4.size();i1++){
                Vector line= (Vector) list4.get(i1);
                warehouse_name =((String) line.get(0));
                warehouse_id=(String) line.get(1);
                ad_org_id=(String) line.get(2);
                hashBranchId.put(warehouse_name,warehouse_id);
                Log.d("DEBUG","WAREHOUSE: "+warehouse_name+" "+warehouse_id+" "+ad_org_id);
                result =mDatabaseHelper.insertIntoWarehouse(warehouse_id,ad_org_id,warehouse_name) ;

            }


            if (result) {

                //toastMessage("Insert  Successful into Doctor Table : ");
                res = "Warehouse Sync Successful";
            }


        } catch (Exception e) {
//            e.printStackTrace();
//            Toast.makeText(getApplicationContext(),"Failed to Data fetch",Toast.LENGTH_SHORT).show();
            res = e.getMessage();

        }
        return res;
    }
    private final class AsyncSender extends AsyncTask<String, String, String> {
        ProgressDialog pd;
        @Override
        protected String doInBackground(String... params) {
            String result = "", error = "";
            try {
                result = SYNCORGBRANCH();
            } catch (Exception e) {
                error = e.getMessage();
                e.printStackTrace();
                return error;
            }
            return result;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pd = new ProgressDialog(OrgBranchSelection.this);
            pd.setTitle("Please Wait ");
            pd.setMessage("Sync On Progress ........");
            pd.setCancelable(false);
            pd.setIndeterminate(true);
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pd.show();
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pd.dismiss();

            if (result.equals("Warehouse Sync Successful")) {
                toastMessage("SYNC Successful");
                getOrg();
            } else {
                toastMessage(result);
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
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {

        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        toastMessage("do you surely want to logout?");

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    public void ShowAlert(String title,String msg){


        androidx.appcompat.app.AlertDialog.Builder dlgAlert  = new androidx.appcompat.app.AlertDialog.Builder(OrgBranchSelection.this);

        dlgAlert.setMessage(msg);
        dlgAlert.setTitle(title);
        dlgAlert.setIcon(R.drawable.logo);
        dlgAlert.setPositiveButton("OK", null);
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
    }
}