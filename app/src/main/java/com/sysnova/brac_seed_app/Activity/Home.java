package com.sysnova.brac_seed_app.Activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
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
import java.util.List;
import java.util.Vector;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import static com.sysnova.brac_seed_app.Activity.LoginActivity.employeeid;
//import static com.sysnova.brac_fisheries_app.Activity.OrgBranchSelection.orgId;
import static com.sysnova.brac_seed_app.Activity.LoginActivity.ad_user_id;
import static com.sysnova.brac_seed_app.Activity.PostgreSQL.apiUrl;

public class Home extends AppCompatActivity {
    DatabaseHelper mDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getSupportActionBar().setTitle("Home");

        getSupportActionBar().setBackgroundDrawable(
                new ColorDrawable(Color.parseColor("#97c740")));

        mDatabaseHelper = new DatabaseHelper(this);
    }

    public void loadSalesOrder(View view) {

        AsyncLoadRecord asyncLoadRecord = new AsyncLoadRecord();
        asyncLoadRecord.execute();
    }

    public void orderActivity(View view) {

        Cursor cursor1 = mDatabaseHelper.getDB_Sync(ad_user_id);
        if (cursor1.moveToFirst()) {
            Intent intent = new Intent(this, Customer_Selection_Activity.class);
            startActivity(intent);
        } else{
            ShowAlert("Warning", "DB Sync is Required.");
        }
    }
    public void DBsyncActivity(View view) {
        AsyncSender task = new AsyncSender();
        task.execute();
    }
    public void DBsyncView(View view) {
            Intent intent = new Intent(Home.this, ViewOrder.class);
            startActivity(intent);
    }
    public void  OrderListForApprovalActivity(View view){
        Intent intent = new Intent(this, OrderListForApprovalActivity.class);
        startActivity(intent);
        finish();
    }
    //add by anika on 2026-07-19
    public void  DealerPaymentActivity(View view){
        Intent intent = new Intent(this, DealerPayment.class);
        startActivity(intent);
        finish();
    }

    public void  DealerPaymentViewActivity(View view){
        Intent intent = new Intent(this, ViewDealerPayment.class);
        startActivity(intent);
        finish();
    }
    //add by anika on 2026-07-19

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to Log Out?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        toastMessage("Logging Out...........");
                        finish();
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void toastMessage(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP, 0, 0);
        toast.show();
    }
    private String DBSYNC() {

        String res = "";
        try {
            mDatabaseHelper.deleteDB_Sync();
        } catch (Exception e) {
            res = e.getMessage();
            return res;
        }
        try {
            mDatabaseHelper.deleteSalesType();
        } catch (Exception e) {
            res = e.getMessage();
            return res;
        }
        try {
            mDatabaseHelper.deleteProducts();
        } catch (Exception e) {
            res = e.getMessage();
            return res;
        }
        try {
            mDatabaseHelper.deleteProductCatagory();
        } catch (Exception e) {
            res = e.getMessage();
            return res;
        }

        try {
            mDatabaseHelper.deleteTerritory();
        } catch (Exception e) {
            res = e.getMessage();
            return res;
        }
        try {
            mDatabaseHelper.deleteCustomer();
        } catch (Exception e) {
            res = e.getMessage();
            return res;
        }
        try {
            mDatabaseHelper.deleteOrganization();
        } catch (Exception e) {
            res = e.getMessage();
            return res;
        }
        try {
            mDatabaseHelper.deleteWarehouse();
        } catch (Exception e) {
            res = e.getMessage();
            return res;
        }
        try {
            mDatabaseHelper.deletePriceList();
        } catch (Exception e) {
            res = e.getMessage();
            return res;
        }
        try {
            mDatabaseHelper.deletePriceList();
        } catch (Exception e) {
            res = e.getMessage();
            return res;
        }
        try {
            mDatabaseHelper.deleteAllBank();
        } catch (Exception e) {
            res = e.getMessage();
            return res;
        }
        try {
            mDatabaseHelper.deleteCostcenter();
        } catch (Exception e) {
            res = e.getMessage();
            return res;
        }

        res = FetchAllOrganization();
        res = FetchAllWarehouse();
        res = FetchSalesTypeFromServer();
        res = FetchAllDealerFromServer();
        res = FetchAllCreditLimitFromServer();
        res = FetchTerritoryFromServer();
        res = FetchPriceListFromServer();
        res = FetchAllBankFromServer();
        res = FetchAllCostCentreFromServer();
        res = FetchAllCollectionTypeFromServer();
        res = FetchProductCatagoryFromServer();

        try {
            return res;
        } catch (Exception e){
            res=e.getMessage();
        }
        return res;

    }
    private String DBSYNCNew() {

        String res = "";

        try {
            mDatabaseHelper.deleteOrderApprovalTable();
            // toastMessage("Delete Promo Tools Successfull");
        } catch (Exception e) {
            res = e.getMessage();
            return res;
        }
        res = FetchAllOrderForApproval(ad_user_id);

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
            try{ con.setRequestMethod("POST");
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

                result =mDatabaseHelper.insertIntoOrganization(ad_org_id,org_name) ;

            }
            if (result) {
                //toastMessage("Insert  Successful into Doctor Table : ");
                res = "Organization Sync Successful";
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            toastMessage(e.getMessage());
        }
        finally
        {
            try {
                if (con != null)
                    con.disconnect();
                con=null;
            }catch (Exception e){
            }
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
            HttpURLConnection con = (HttpURLConnection) ( new URL(""+apiUrl+"/DBSync")).openConnection();
            try{
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
                //hashBranchId.put(warehouse_name,warehouse_id);
                Log.d("DEBUG","WAREHOUSE: "+warehouse_name+" "+warehouse_id+" "+ad_org_id);
                result =mDatabaseHelper.insertIntoWarehouse(warehouse_id,ad_org_id,warehouse_name) ;

            }
            if (result) {
                //toastMessage("Insert  Successful into Doctor Table : ");
                res = "Warehouse Sync Successful";
            }
            } catch (Exception e) {
                e.printStackTrace();
                toastMessage(e.getMessage());
            }
            finally
            {
                try {
                    if (con != null)
                        con.disconnect();
                    con=null;
                }catch (Exception e){
                }
            }
        } catch (Exception e) {
//            e.printStackTrace();
//            Toast.makeText(getApplicationContext(),"Failed to Data fetch",Toast.LENGTH_SHORT).show();
            res = e.getMessage();

        }
        return res;
    }
    private String FetchAllOrderForApproval(String user_id) {

        String order_doc, erp_orderNo, grand_total,prepared_by,sales_type,sent_date, res = "";
        String doctype_id="";

        boolean result = false;

        try {

            HttpURLConnection con = (HttpURLConnection) ( new URL(""+apiUrl+"/DBSync")).openConnection();
            try{ con.setRequestMethod("POST");
            con.setDoInput(true);
            con.setDoOutput(true);
            con.connect();

            HttpClient httpClient=new DefaultHttpClient();
            HttpPost httpPost=new HttpPost(""+apiUrl+"/DBSync");

            ArrayList list13=new ArrayList();
            List<NameValuePair> list=new ArrayList<NameValuePair>();
            list.add(new BasicNameValuePair("name", "orderApproval"));
            list.add(new BasicNameValuePair("userId", user_id));
            httpPost.setEntity(new UrlEncodedFormEntity(list));
            HttpResponse httpResponse=	httpClient.execute(httpPost);



            HttpEntity httpEntity=httpResponse.getEntity();
            String s= readResponse(httpResponse);
            if (s.isEmpty() || !s.startsWith("[")){
                res="Order Not Found";
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
                order_doc =((String) line.get(0));
                erp_orderNo=(String) line.get(1);
                grand_total=((String) line.get(2));
                prepared_by = ((String) line.get(3));
                sales_type  = ((String) line.get(4));
                sent_date   = (String) line.get(5);
                doctype_id=(String )line.get(6);
                String orgid=((String)line.get(7));
            //////////   System.out.println("Sent date from server==========: ");

                //sent_date = sent_date.replaceAll("\\d\\d:\\d\\d:\\d\\d.\\d\\d\\d","");
                System.out.println("Sent date after refactoring==========: ");
                sent_date.trim();
                int b=sent_date.indexOf(" ");
                String finaldate=sent_date.substring(0, b);
                Log.d("ID","ORGIDAPPROVAL"+orgid);
                result = mDatabaseHelper.insertIntoOrderApproval(order_doc, erp_orderNo,grand_total,prepared_by,sales_type,finaldate,doctype_id,orgid);
           System.out.println("DOCTYPEID=="+doctype_id);
            }


            if (result) {

                //toastMessage("Insert  Successful into Doctor Table : ");
                res = "Order For Approval Sync Successful";
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            toastMessage(e.getMessage());
        }
        finally
        {
            try {
                if (con != null)
                    con.disconnect();
                con=null;
            }catch (Exception e){
            }
        }
        } catch (Exception e) {
//            e.printStackTrace();
//            Toast.makeText(getApplicationContext(),"Failed to Data fetch",Toast.LENGTH_SHORT).show();
            res = e.getMessage();
        }
        return res;
    }



    private String FetchSalesTypeFromServer() {
        String name, description, res = "";
        boolean result = false;
        try {
            HttpURLConnection con = (HttpURLConnection) ( new URL(""+apiUrl+"/DBSync")).openConnection();
            try{ con.setRequestMethod("POST");
            con.setDoInput(true);
            con.setDoOutput(true);
            con.connect();

            HttpClient httpClient=new DefaultHttpClient();
            //HttpPost httpPost=new HttpPost("http://"+ip+":8080/NaafcoWEBAPI/DBSync");
            HttpPost httpPost=new HttpPost(""+apiUrl+"/DBSync");

            List<NameValuePair> list=new ArrayList<NameValuePair>();
            list.add(new BasicNameValuePair("name", "salesType"));
            httpPost.setEntity(new UrlEncodedFormEntity(list));
            HttpResponse httpResponse=	httpClient.execute(httpPost);

            HttpEntity httpEntity=httpResponse.getEntity();
            String s= readResponse(httpResponse);
            if (s.isEmpty() || !s.startsWith("[")){
                res="salesType Not Foundd";
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
                name=((String) line.get(0));

                result = mDatabaseHelper.insertIntoSalesType(name);
                Log.d("DEBUG","Salestype:"+name);

            }

            if (result) {
                //toastMessage("Insert  Successful into PrmoTools Table : ");
                res = "Sales Type Sync Successful";
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            toastMessage(e.getMessage());
        }
        finally
        {
            try {
                if (con != null)
                    con.disconnect();
                con=null;
            }catch (Exception e){
            }
        }
        }  catch (Exception e) {
//            e.printStackTrace();
//            Toast.makeText(getApplicationContext(),"Failed to Data fetch",Toast.LENGTH_SHORT).show();
            res = e.getMessage();
        }
        return res;

    }
    private String FetchAllCreditLimitFromServer() {


        String  name,res="";

        boolean result = false;

        try {
            HttpURLConnection con = (HttpURLConnection) (new URL("" + apiUrl + "/DBSync")).openConnection();
            try{
                con.setRequestMethod("POST");
                con.setDoInput(true);
                con.setDoOutput(true);
                con.connect();
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost("" + apiUrl + "/DBSync");

                List<NameValuePair> list = new ArrayList<NameValuePair>();

                list.add(new BasicNameValuePair("name", "allcreditlimitcustomer"));
                //list.add(new BasicNameValuePair("date", date));
                list.add(new BasicNameValuePair("ad_user_id", ad_user_id));
                //list.add(new BasicNameValuePair("bp_group_id", selectedBPGroupID));

                httpPost.setEntity(new UrlEncodedFormEntity(list));
                HttpResponse httpResponse = httpClient.execute(httpPost);


                HttpEntity httpEntity = httpResponse.getEntity();
                String s = readResponse(httpResponse);
                Log.d("DEBUG", "S Response Customer " + s);
                int y = s.length();
                if (s.isEmpty() || !s.startsWith("[")) {
                    res = s + " No Customer found";
                    Log.d("DEBUG", "Empty Result " + result);
                    return res;
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

                    String customer_id = (String) line.get(0);
                    String customer = (String) line.get(1);
                    String code = (String) line.get(2);
                    String customer_location = (String) line.get(3);
                    String customer_phone = (String) line.get(4);
                    String territory_ID = (String) line.get(5);
                    String so_creditlimit= (String) line.get(6);
                    String c_bp_group_id = (String) line.get(7);
                    String costcenter_id = (String) line.get(8);
                    String creditlimit = (String) line.get(9);
                    String daysdue = (String) line.get(10);
                    String t_bpcreditlimit_id = (String) line.get(11);
                    String validfrom = (String) line.get(12);
                    String validto = (String) line.get(13);
                    String ad_org_id = (String) line.get(14);

                    Log.d("DEBUG","Customer: "+customer+" "+customer_id+" "+territory_ID);
                    result = mDatabaseHelper.insertIntoCreditLimit(customer_id, customer, code,customer_location,customer_phone,territory_ID,so_creditlimit, c_bp_group_id, costcenter_id , creditlimit, daysdue, t_bpcreditlimit_id , validfrom, validto, ad_org_id);

                }


                if (result) {
                    res = "Successful";
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                toastMessage(e.getMessage());
            }
            finally
            {
                try {
                    if (con != null)
                        con.disconnect();
                    con=null;
                }catch (Exception e){
                }
            }
        }catch (Exception e) {

            res = e.getMessage();

        }
        return res;
    }
    private String FetchAllDealerFromServer() {


        String  name,res="";

        boolean result = false;

        try {
            HttpURLConnection con = (HttpURLConnection) (new URL("" + apiUrl + "/DBSync")).openConnection();
            try{
                con.setRequestMethod("POST");
            con.setDoInput(true);
            con.setDoOutput(true);
            con.connect();
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("" + apiUrl + "/DBSync");

            List<NameValuePair> list = new ArrayList<NameValuePair>();

            list.add(new BasicNameValuePair("name", "allcustomer"));
            //list.add(new BasicNameValuePair("date", date));
            list.add(new BasicNameValuePair("ad_user_id", ad_user_id));
            //list.add(new BasicNameValuePair("bp_group_id", selectedBPGroupID));

            httpPost.setEntity(new UrlEncodedFormEntity(list));
            HttpResponse httpResponse = httpClient.execute(httpPost);


            HttpEntity httpEntity = httpResponse.getEntity();
            String s = readResponse(httpResponse);
            Log.d("DEBUG", "S Response Customer " + s);
            int y = s.length();
            if (s.isEmpty() || !s.startsWith("[")) {
                res = s + " No Customer found";
                Log.d("DEBUG", "Empty Result " + result);
                return res;
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
                System.out.println(line.toString());
                String customer_id = (String) line.get(0);
                String customer = (String) line.get(1);
                String code = (String) line.get(2);
                String customer_location = (String) line.get(3);
                String customer_phone = (String) line.get(4);
                String territory_ID = (String) line.get(5);
                String so_creditlimit= (String) line.get(6);
                String c_bp_group_id = (String) line.get(7);

                Log.d("DEBUG","Customer: "+customer+" "+customer_id+" "+territory_ID);
                result = mDatabaseHelper.insertIntoOCustomer(customer_id, customer, code,customer_location,customer_phone,territory_ID,so_creditlimit, c_bp_group_id);

            }

            if (result) {

                //toastMessage("Insert  Successful into Chemist Table : ");
                res = "Successful";
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            toastMessage(e.getMessage());
        }
        finally
        {
            try {
                if (con != null)
                    con.disconnect();
                con=null;
            }catch (Exception e){
            }
        }
        }catch (Exception e) {

            res = e.getMessage();

        }
        return res;
    }

    private String  FetchAllBankFromServer() {

        String res = "";
        boolean result = false;

        try {
            HttpURLConnection con = (HttpURLConnection) ( new URL(""+apiUrl+"/DBSync")).openConnection();
            con.setRequestMethod("POST");
            con.setDoInput(true);
            con.setDoOutput(true);
            con.connect();

            HttpClient httpClient=new DefaultHttpClient();
            HttpPost httpPost=new HttpPost(""+apiUrl+"/DBSync");

            List<NameValuePair> list=new ArrayList<NameValuePair>();
            list.add(new BasicNameValuePair("name", "allbankacc"));
            httpPost.setEntity(new UrlEncodedFormEntity(list));
            HttpResponse httpResponse=	httpClient.execute(httpPost);

            HttpEntity httpEntity=httpResponse.getEntity();
            String s= readResponse(httpResponse);
            if (s.isEmpty() || !s.startsWith("[")){
                res="All Bank Not Foundd";
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
                int c_bankaccount_id=(Integer.parseInt((String) line.get(0)));
                String accountno = (String) line.get(1);
                String bank = ((String) line.get(2));
                String ad_org_id =((String) line.get(3));
                String bracAC =((String) line.get(4));

                accountno = accountno.replaceAll("&", "_");
                bracAC = bracAC.replaceAll("&", "_");

                result = mDatabaseHelper.insertIntoALLBank(c_bankaccount_id, accountno, bank, ad_org_id, bracAC);
            }

            if (result) {

                res = "Bank Sync Successful";
            }
        } catch (Exception e) {
            res = e.getMessage();
        }
        return res;
    }

    private String  FetchAllCostCentreFromServer() {

        String res = "";
        boolean result = false;

        try {
            HttpURLConnection con = (HttpURLConnection) ( new URL(""+apiUrl+"/DBSync")).openConnection();
            con.setRequestMethod("POST");
            con.setDoInput(true);
            con.setDoOutput(true);
            con.connect();

            HttpClient httpClient=new DefaultHttpClient();
            HttpPost httpPost=new HttpPost(""+apiUrl+"/DBSync");

            List<NameValuePair> list=new ArrayList<NameValuePair>();
            list.add(new BasicNameValuePair("name", "allcostcentre"));
            httpPost.setEntity(new UrlEncodedFormEntity(list));
            HttpResponse httpResponse=	httpClient.execute(httpPost);

            HttpEntity httpEntity=httpResponse.getEntity();
            String s= readResponse(httpResponse);
            if (s.isEmpty() || !s.startsWith("[")){
                res="All Cost Centre Not Foundd";
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

                String c_elementvalue_id = (String) line.get(0);
                String name = (String) line.get(1);

                result = mDatabaseHelper.insertIntoCostCenter(c_elementvalue_id, name);
            }

            if (result) {

                res = "Cost Center Sync Successful";
            }
        } catch (Exception e) {
            res = e.getMessage();
        }
        return res;
    }

    private String  FetchAllCollectionTypeFromServer() {

        String res = "";
        boolean result = false;

        try {
            HttpURLConnection con = (HttpURLConnection) ( new URL(""+apiUrl+"/DBSync")).openConnection();
            con.setRequestMethod("POST");
            con.setDoInput(true);
            con.setDoOutput(true);
            con.connect();

            HttpClient httpClient=new DefaultHttpClient();
            HttpPost httpPost=new HttpPost(""+apiUrl+"/DBSync");

            List<NameValuePair> list=new ArrayList<NameValuePair>();
            list.add(new BasicNameValuePair("name", "allcollectiontype"));
            httpPost.setEntity(new UrlEncodedFormEntity(list));
            HttpResponse httpResponse=	httpClient.execute(httpPost);

            HttpEntity httpEntity=httpResponse.getEntity();
            String s= readResponse(httpResponse);
            if (s.isEmpty() || !s.startsWith("[")){
                res="All Collection Type Not Found";
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

                String t_collectiontype_id = (String) line.get(0);
                String name = (String) line.get(1);

                result = mDatabaseHelper.insertIntoCollectionType(t_collectiontype_id, name);
            }

            if (result) {

                res = "Collection Type Sync Successful";
            }
        } catch (Exception e) {
            res = e.getMessage();
        }
        return res;
    }

    private String  FetchProductCatagoryFromServer() {


        String catagoryname, res = "";

        int id;
        boolean result = false;
        try {


            HttpURLConnection con = (HttpURLConnection) ( new URL(""+apiUrl+"/DBSync")).openConnection();
            con.setRequestMethod("POST");
            con.setDoInput(true);
            con.setDoOutput(true);
            con.connect();

            HttpClient httpClient=new DefaultHttpClient();
            HttpPost httpPost=new HttpPost(""+apiUrl+"/DBSync");

            List<NameValuePair> list=new ArrayList<NameValuePair>();
            list.add(new BasicNameValuePair("name", "product_catagory"));
            list.add(new BasicNameValuePair("userid",ad_user_id));
            httpPost.setEntity(new UrlEncodedFormEntity(list));
            HttpResponse httpResponse=	httpClient.execute(httpPost);

            HttpEntity httpEntity=httpResponse.getEntity();
            String s= readResponse(httpResponse);
            if (s.isEmpty() || !s.startsWith("[")){
                res="Product Catagory Not Foundd";
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
                int catagoryId=(Integer.parseInt((String) line.get(0)));
                String catagoryName=(String) line.get(1);
                String c_salesregion_id=(String) line.get(2);
            //    String c_salesregion_id = "1000000";
                result = mDatabaseHelper.insertIntoProductCatagory(catagoryId, catagoryName, c_salesregion_id);

            }

            if (result) {

                res = "Product Catagory Sync Successful";
            }
        } catch (Exception e) {
            res = e.getMessage();
        }
        return res;
    }

    private String  FetchTerritoryFromServer() {

        boolean result = false;

        String name, territory_Id, code, res = "";
        try {
            HttpURLConnection con = (HttpURLConnection) ( new URL(""+apiUrl+"/DBSync")).openConnection();
            try{
                con.setRequestMethod("POST");
            con.setDoInput(true);
            con.setDoOutput(true);
            con.connect();

            HttpClient httpClient=new DefaultHttpClient();
            HttpPost httpPost=new HttpPost(""+apiUrl+"/DBSync");

            List<NameValuePair> list=new ArrayList<NameValuePair>();
            list.add(new BasicNameValuePair("name", "Territory"));
            list.add(new BasicNameValuePair("ad_user_id", ad_user_id));
            httpPost.setEntity(new UrlEncodedFormEntity(list));
            HttpResponse httpResponse=	httpClient.execute(httpPost);

            HttpEntity httpEntity=httpResponse.getEntity();
            String s= readResponse(httpResponse);
            if (s.isEmpty() || !s.startsWith("[")){
                res="Product Not Foundd";
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
                territory_Id=((String) line.get(0));
                name=(String) line.get(1);
                code=(String) line.get(2);

                System.out.println("territory id"+territory_Id);
                Log.d("DEBUG","territory: "+name+" "+territory_Id+" "+code);
                result = mDatabaseHelper.insertIntoTerritory(code,territory_Id,name);
            }
            if (result) {
                //toastMessage("Insert  Successful into Product Catagory Table : ");
                res = "Successful";
            }
            } catch (Exception e) {
                e.printStackTrace();
                toastMessage(e.getMessage());
            }
            finally
            {
                try {
                    if (con != null)
                        con.disconnect();
                    con=null;
                }catch (Exception e){
                }
            }
        } catch (Exception e) {
//            e.printStackTrace();
//            Toast.makeText(getApplicationContext(),"Failed to Data fetch",Toast.LENGTH_SHORT).show();
            res = e.getMessage();
        }
        return res;
    }

    private String  FetchPriceListFromServer() {

        int catagoryID;
        boolean result = false;

        String name, productID, price, res = "";
        try {
            HttpURLConnection con = (HttpURLConnection) ( new URL(""+apiUrl+"/DBSync")).openConnection();
            try{
                con.setRequestMethod("POST");
                con.setDoInput(true);
                con.setDoOutput(true);
                con.connect();

                HttpClient httpClient=new DefaultHttpClient();
                HttpPost httpPost=new HttpPost(""+apiUrl+"/DBSync");

                List<NameValuePair> list=new ArrayList<NameValuePair>();
                list.add(new BasicNameValuePair("name", "pricelist"));
                httpPost.setEntity(new UrlEncodedFormEntity(list));
                HttpResponse httpResponse=	httpClient.execute(httpPost);

                HttpEntity httpEntity=httpResponse.getEntity();
                String s= readResponse(httpResponse);
                if (s.isEmpty() || !s.startsWith("[")){
                    res="PriceList Not Found";
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

                    String pricelist_name=(String) line.get(0);
                    String pricelist_id=((String) line.get(1));
                    String org_id =(String) line.get(2);
                    result = mDatabaseHelper.insertIntoPriceList(pricelist_id,pricelist_name,org_id);
                    Log.d("DEBUG","pricelist: "+pricelist_name+" "+pricelist_id+" "+org_id);
                }
                if (result) {
                    //toastMessage("Insert  Successful into Product Catagory Table : ");
                    res = "PriceList Sync Successful";
                }
            } catch (Exception e) {
                e.printStackTrace();
                toastMessage(e.getMessage());
            }
            finally
            {
                try {
                    if (con != null)
                        con.disconnect();
                    con=null;
                }catch (Exception e){
                }
            }
        } catch (Exception e) {
//            e.printStackTrace();
//            Toast.makeText(getApplicationContext(),"Failed to Data fetch",Toast.LENGTH_SHORT).show();
            res = e.getMessage();
        }
        return res;
    }



    public String FetchProductsFromServer() {

        int catagoryID;
        boolean result = false;

        String name, productID, price, res = "",unitName = "",avlQty = "";

        try {

            HttpURLConnection con = (HttpURLConnection) ( new URL(""+apiUrl+"/DBSync")).openConnection();
            con.setRequestMethod("POST");
            con.setDoInput(true);
            con.setDoOutput(true);
            con.connect();

            HttpClient httpClient=new DefaultHttpClient();
            HttpPost httpPost=new HttpPost(""+apiUrl+"/DBSync");

            List<NameValuePair> list=new ArrayList<NameValuePair>();
            list.add(new BasicNameValuePair("name", "allproduct"));
           // list.add(new BasicNameValuePair("m_warehouse_id",String.valueOf(branchId)));
           // list.add(new BasicNameValuePair("org_id",String.valueOf(orgId)));
            httpPost.setEntity(new UrlEncodedFormEntity(list));
            HttpResponse httpResponse=	httpClient.execute(httpPost);



            HttpEntity httpEntity=httpResponse.getEntity();
            String s= readResponse(httpResponse);
            if (s.isEmpty() || !s.startsWith("[")){
                res="Product  Not Foundd";
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
                productID=((String) line.get(0));
                name=(String) line.get(1);
                catagoryID=Integer.parseInt((String) line.get(2));
                String costcenter=(String) line.get(6);
               /** price=((String) line.get(3));
                unitName = ((String)line.get(4));
                avlQty   = ((String)line.get(5));
                String onHandQTY=((String)line.get(6));
                String reservedQTY=((String)line.get(7));**/
                Log.d("QTY","name="+name);
                Log.d("QTY","avlQTY="+avlQty);

                System.out.println("Product id"+productID);
             //   System.out.println("warehouse id"+String.valueOf(branchId));
                Log.d("DEBUG","ProductName="+name+" PID="+productID+" p_catId="+catagoryID);
                result = mDatabaseHelper.insertIntoProduct(productID, name, catagoryID, "","","","",costcenter, "");

            }




            if (result) {

                //toastMessage("Insert  Successful into Product Table : ");
                res = "Product Sync Successful";

            }


        } catch (Exception e) {
            res = e.getMessage();
            Log.d("DEBUG",res);
        }
        return res;

    }

    public void ShowAlert(String title, String msg) {


        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(Home.this);

        dlgAlert.setMessage(msg);
        // dlgAlert.setTitle(Html.fromHtml("<font color='#FF7F27'>"+title+"</font>"));
        dlgAlert.setIcon(R.drawable.logo);
        dlgAlert.setPositiveButton("OK", null);
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
    }

    public void exitApp(View view) {
       // finishAffinity();
        AlertDialog.Builder builder1 = new AlertDialog.Builder(Home.this);
        builder1.setMessage("Are you sure you want to exit the app?");
        builder1.setCancelable(true);
        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        finish();
                      //  finishAffinity();
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert11 = builder1.create();

        alert11.show();

    }

    private final class AsyncSender extends AsyncTask<String, String, String> {
        ProgressDialog pd;
        @Override
        protected String doInBackground(String... params) {
            String result = "", error = "";
            try {
                result = DBSYNC();
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

            pd = new ProgressDialog(Home.this);
            pd.setTitle("Please Wait ");
            pd.setMessage("DBSync On Progress ........");
            pd.setCancelable(false);
            pd.setIndeterminate(true);
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pd.show();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pd.dismiss();

            if (result.equals("Product Catagory Sync Successful")) {
                boolean r = false;
                String Sync="yes";
                r = mDatabaseHelper.insertIntoDB_Sync(Sync,ad_user_id);
                if (r) {
                    toastMessage("DBSYNC Insert Successful");
                }
                toastMessage("DBSYNC Successful");
            } else {
                toastMessage(result);
            }
        }
    }
    private final class AsyncSenderNew extends AsyncTask<String, String, String> {
        ProgressDialog pd;
        @Override
        protected String doInBackground(String... params) {
            String result = "", error = "";
            try {
                result = DBSYNCNew();
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

            pd = new ProgressDialog(Home.this);
            pd.setTitle("Please Wait ");
            pd.setMessage("DBSync On Progress ........");
            pd.setCancelable(false);
            pd.setIndeterminate(true);
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pd.show();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pd.dismiss();

            if (result.equals("Product Catagory Sync Successful")) {
                toastMessage("DBSYNC Successful");
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

        }
        return a;
    }

    private final class AsyncLoadRecord extends AsyncTask<String, String, String> {
        ProgressDialog pd;
        @Override
        protected String doInBackground(String... params) {
            String result = "", error = "";
            try {
                mDatabaseHelper.deleteSalesOrderRecord();
                result = salesRecordSync();
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

            pd = new ProgressDialog(Home.this);
            pd.setTitle("Please Wait ");
            pd.setMessage("Record Sync On Progress ........");
            pd.setCancelable(false);
            pd.setIndeterminate(true);
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pd.show();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pd.dismiss();
            try{
                if (result.equals("Order Record Sync Successful")) {
                    toastMessage("Order Sync Successful");
                } else {
                    toastMessage(result);
                }
            }catch (NullPointerException e){

            }
        }
    }

    public String salesRecordSync() {
        boolean result = false;
        String  res = "";
        try {

            HttpURLConnection con = (HttpURLConnection) ( new URL(""+apiUrl+"/DBSync")).openConnection();
            con.setRequestMethod("POST");
            con.setDoInput(true);
            con.setDoOutput(true);
            con.connect();

            HttpClient httpClient=new DefaultHttpClient();
            HttpPost httpPost=new HttpPost(""+apiUrl+"/DBSync");

            List<NameValuePair> list=new ArrayList<NameValuePair>();
            list.add(new BasicNameValuePair("name", "salesorderlist"));
            list.add(new BasicNameValuePair("userid",employeeid));
            httpPost.setEntity(new UrlEncodedFormEntity(list));
            HttpResponse httpResponse=	httpClient.execute(httpPost);

            HttpEntity httpEntity=httpResponse.getEntity();
            String s= readResponse(httpResponse);
            if (s.isEmpty() || !s.startsWith("[")){
                res="Sales Record Not Found";
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
                String docNo=((String) line.get(0));
                String orderDate=(String) line.get(1);
                String grandTotal=(String) line.get(2);
                String productName=((String) line.get(3));
                String  uom = ((String)line.get(4));
                String  qty_ordered   = ((String)line.get(5));
                String pricePerProduct   = ((String)line.get(6));
                String customerName   = ((String)line.get(7));
                String status=((String)line.get(8));
                String orgid=((String)line.get(9));
                String description=((String)line.get(10));
                orderDate = orderDate.replaceAll("\\d\\d:\\d\\d:\\d\\d.\\d","");
                Log.d("ID","ORGID"+orgid);
                result = mDatabaseHelper.insertIntoOrderRecord(docNo, orderDate, customerName,grandTotal,productName,uom,qty_ordered,pricePerProduct,status,orgid,description);

            }

            if (result) {
                res = "Order Record Sync Successful";
            }


        } catch (Exception e) {
            res = e.getMessage();
        }
        return res;
    }
}
