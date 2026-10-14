package com.sysnova.brac_seed_app.Activity;

import static com.sysnova.brac_seed_app.Activity.PostgreSQL.apiUrl;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sysnova.brac_seed_app.R;
import com.sysnova.brac_seed_app.adapter.ViewProductsListRecyclerViewAdapter;
import com.sysnova.brac_seed_app.modal.ViewProductDetailsModal;

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
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

public class FullViewOrderActivity extends AppCompatActivity {

    String bpCode,bpName,phone,dateOrdered,warehouse,total_openbalance,bp_location,description,orgname, volume;
    String  old_kg,documentNo,des,promised_date,s_name,s_phone,c_order_id,C_Doctype_ID;
    String wordsAmtBags;
    TextView partyCodeTextView,partyNameTextView,addressTextView,mobileNoTextView,soNumberTextView,soIssueDateTextView,srNameTextView,srMobileTextView;
    TextView total_bagsTextView,total_moneyTextView,qty_wordsTextView,orgnameTexView, noofpcktvw, totalvw, totalamtvw;
    TextView mtAmtTextView,deliveryFromTextView,desTextView,totalPriceTextView,truckno_text,prodactivity_name,activitytype_name;//,proAmtTextView,discout_editText1,partyBalanceTextView,undeliveredAmtTextView;
    EditText discout_EditText,total_editTextET;
    RecyclerView recyclerViewList;
    double totalMetricTon = 0,totalBagQty = 0,totalTakaAmt = 0, sumvol=0, bonus_amt = 0;
    BigDecimal total_price1=new BigDecimal(0);
    ArrayList<ViewProductDetailsModal> productDetailsModals = new ArrayList<>();
    String msg = "",isApproved = "",documentNumber = "",erpDocumentNumber = "",odiscount,percentdis="",ispdis="",tmsg="";
    String selectedForwardToId="";
    CheckBox discountCheckBox;
    DatabaseHelper dbHelper;
    private static final DecimalFormat decfor = new DecimalFormat("0.00");
    boolean ispercentdiscount=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_vieworder);

        orgnameTexView = findViewById(R.id.org_approval);
        partyCodeTextView = findViewById(R.id.party_code_approval);
        partyNameTextView = findViewById(R.id.party_name_approval);
        addressTextView = findViewById(R.id.address_approval);
        mobileNoTextView = findViewById(R.id.mobile_approval);
        soNumberTextView = findViewById(R.id.so_number);
        soIssueDateTextView = findViewById(R.id.so_issue_date);
        srNameTextView      = findViewById(R.id.sr_name);
        srMobileTextView    = findViewById(R.id.sr_mobile);

        recyclerViewList = findViewById(R.id.order_product_list_recyclerview);
        total_bagsTextView    = findViewById(R.id.total_bags);
        total_moneyTextView   = findViewById(R.id.total_price_amt);
        qty_wordsTextView      = findViewById(R.id.quantity_words_bags);
        mtAmtTextView          = findViewById(R.id.mt_amount);
        deliveryFromTextView   = findViewById(R.id.delivery_from);
        desTextView            = findViewById(R.id.description_id);

        truckno_text = findViewById(R.id.truckno_text);
        prodactivity_name = findViewById(R.id.prodactivity_name);
        activitytype_name  = findViewById(R.id.activitytype_name);
        noofpcktvw = findViewById(R.id.noofpckttextvw);
        totalvw = findViewById(R.id.total_disc_amt);
        totalamtvw = findViewById(R.id.totalbonus_amt);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle!=null) {
            documentNumber   = getIntent().getStringExtra("order_doc");
        }

        getSupportActionBar().setTitle("Order View Activity");
        getSupportActionBar().setBackgroundDrawable(
                new ColorDrawable(Color.parseColor("#97c740")));

        dbHelper = new DatabaseHelper(this);
        AsyncLoadData asyncLoadData = new AsyncLoadData();
        asyncLoadData.execute();


    }


    public void callCancelLogOut(View view) {
        Intent i=new Intent(FullViewOrderActivity.this, ViewOrder.class);
        i.putExtra("dateOrdered", dateOrdered);
        startActivity(i);
    }
    public void ShowAlert1(String title,String msg){
        androidx.appcompat.app.AlertDialog.Builder dlgAlert  = new androidx.appcompat.app.AlertDialog.Builder(FullViewOrderActivity.this);
        dlgAlert.setMessage(msg);
        dlgAlert.setTitle(title);
        dlgAlert.setIcon(R.drawable.seed);
        dlgAlert.setNegativeButton("NO", null);
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
    }

    private final class AsyncLoadData extends AsyncTask<String, String, String>{

        ProgressDialog pd;
        // String result = "";
        @Override
        protected String doInBackground(String... strings) {
            // result =  FetchDataFromServer();
            //return result;
            String result = "", error = "";
            try {
                result = dbSYNC();
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

            pd = new ProgressDialog(FullViewOrderActivity.this);
            pd.setTitle("Please Wait ");
            pd.setMessage("Loading....");
            pd.setCancelable(false);
            pd.setIndeterminate(true);
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pd.show();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pd.dismiss();

            if (result.equals("Success")) {

                AysncOrderlineData AysncOrderlineData = new AysncOrderlineData();
                AysncOrderlineData.execute();
            }
            else {
                toastMessage("Something Went Wrong!!!!");
            }
        }

    }

    private String dbSYNC() {
        String res = "";

        res=FetchDataFromServer();
        try {
            return res;
        } catch (Exception e){
            res=e.getMessage();
        }
        return res;
    }

    private void setTextField(){
        String bp_location1="",phone1="",s_phone1="",s_name1="",promised_date1="",promisedAmt1="",percentdis1="",documentNo1="";
        if(!bp_location.equals("null") && bp_location!= null && !bp_location.isEmpty()) {
            bp_location1=bp_location;
        }
        if(!phone.equals("null") && phone!= null && !phone.isEmpty()) {
            phone1 = phone;
        }
        if(!s_phone.equals("null") && s_phone!= null && !s_phone.isEmpty()) {
            s_phone1 = s_phone;
        }
        if(!s_name.equals("null") && s_name!= null && !s_name.isEmpty()) {
            s_name1 = s_name;
        }
        if(!documentNo.equals("null") && documentNo!= null && !documentNo.isEmpty()) {
            documentNo1 = documentNo;
        }
        orgnameTexView.setText(orgname);
        partyCodeTextView.setText(bpCode);
        partyNameTextView.setText(bpName);
        addressTextView.setText(bp_location1);
        mobileNoTextView.setText(phone1);
        soNumberTextView.setText(documentNo1); //documentNo
        dateOrdered = dateOrdered.replaceAll("\\d\\d:\\d\\d:\\d\\d.\\d","");
        soIssueDateTextView.setText(dateOrdered);
        srNameTextView.setText(s_name1);
        srMobileTextView.setText(s_phone1);
        //total_mtTextView.setText(String.valueOf(totalMetricTon));
        total_bagsTextView.setText(String.valueOf(decfor.format(totalBagQty)));
        total_moneyTextView.setText(String.valueOf(decfor.format(totalTakaAmt)));
        totalvw.setText(String.valueOf(decfor.format(sumvol)));
        totalamtvw.setText(String.valueOf(decfor.format(bonus_amt)));
        //promised_date1 = promised_date1.replaceAll("\\d\\d:\\d\\d:\\d\\d.\\d","");
        //promisedDateTextView.setText(promised_date);
        mtAmtTextView.setText(String.valueOf(total_price1));
        deliveryFromTextView.setText(warehouse);
        //partyBalanceTextView.setText(total_openbalance);
        //undeliveredAmtTextView.setText(undelivered_amt);
        qty_wordsTextView.setText(wordsAmtBags+" Taka Only");
        desTextView.setText(description);
        truckno_text.setText(total_openbalance);
        prodactivity_name.setText(odiscount);
        activitytype_name.setText(percentdis);
        //proAmtTextView.setText(promisedAmt1);
        // undeliveredAmtTextView.setText(odiscount);
        //discout_EditText.setText(String.valueOf(odiscount));
        //undeliveredAmtTextView.setText(percentdis1); //ispdis
    }
    private void initRecyclerView(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerViewList.setLayoutManager(linearLayoutManager);
        ViewProductsListRecyclerViewAdapter adapter = new ViewProductsListRecyclerViewAdapter(productDetailsModals, FullViewOrderActivity.this);
        recyclerViewList.setAdapter(adapter);
    }

    private final class AsyncSender2 extends AsyncTask<String, String, String> {

        ProgressDialog pd;

        @Override
        protected String doInBackground(String... params) {

            String result="",error="",tt="N",fid="0",dis="0";
            boolean rt = false;

            try {
                ArrayList salesHeader=new ArrayList();
                String changeDiscount=discout_EditText.getText().toString();
                HttpURLConnection con = (HttpURLConnection) ( new URL(""+apiUrl+"/ApprovalOrder")).openConnection();
                con.setRequestMethod("POST");
                con.setDoInput(true);
                con.setDoOutput(true);
                con.connect();
                if(!changeDiscount.isEmpty()) {
                    if(odiscount!=changeDiscount){
                        dis=changeDiscount;
                    }
                    //changeDiscount="0";
                }
                else{
                    dis=odiscount;
                }
                if(!selectedForwardToId.equals("null") && selectedForwardToId!= null && !selectedForwardToId.isEmpty()) {
                    fid=selectedForwardToId;
                }
                Vector headerLines=new Vector();
                headerLines.add(OrgBranchSelection.selectedOrgId);

                headerLines.add(LoginActivity.ad_user_id);
                headerLines.add(isApproved);
                headerLines.add(erpDocumentNumber);
                headerLines.add(fid);
                headerLines.add(dis);
                if(discountCheckBox.isChecked())
                {
                    headerLines.add("Y");
                }
                else
                {
                    headerLines.add(ispdis);
                }
                headerLines.add(msg);
                salesHeader.add(headerLines);

                String headerString = "";
                for (Object s : salesHeader) {
                    if (headerString.isEmpty()) {
                        headerString =headerString+s;
                    } else {
                        headerString =headerString+"_" + s;

                    }
                }

                HttpClient httpClient=new DefaultHttpClient();
                HttpPost httpPost=new HttpPost(""+apiUrl+"/ApprovalOrder");

                List<NameValuePair> list=new ArrayList<NameValuePair>();
                list.add(new BasicNameValuePair("type", "approvalOrder"));
                list.add(new BasicNameValuePair("header", headerString));
                httpPost.setEntity(new UrlEncodedFormEntity(list));
                HttpResponse httpResponse=	httpClient.execute(httpPost);

                HttpEntity httpEntity=httpResponse.getEntity();
                String s= readResponse(httpResponse);
                int y = s.length();
                if (s.isEmpty() || !s.equals("InsertedSuccessfully")){
                    result=s;
                    return result;
                }else{
                    return "Success";
                }
            }
            catch (Exception e){
                error=e.getMessage();
                return  error;
            }

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pd = new ProgressDialog(FullViewOrderActivity.this);
            pd.setTitle("Please Wait ");
            pd.setMessage("Updating....");
            pd.setCancelable(false);
            pd.setIndeterminate(true);
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pd.show();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pd.dismiss();

            if (result.equals("Success")) {
                toastMessage("Success");
                //Intent intent  = new Intent(UpdateApprovalActivity.this,Home.class);
                //startActivity(intent);
                //finish();
            }
            else {
                toastMessage("Something Went Wrong!!!!");
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
    public void onBackPressed(){
        super.onBackPressed();
        finish();
        Intent intent=new Intent(FullViewOrderActivity.this, ViewOrder.class);
        intent.putExtra("dateOrdered", dateOrdered);
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }


    public String FetchDataFromServer() {
        int catagoryID;
        boolean result = false;
        boolean result1 = false;
        String  res = "";
        HttpURLConnection con =null;

        try {
            con = (HttpURLConnection) ( new URL(""+apiUrl+"/SyncOrderDetails2")).openConnection();
            con.setRequestMethod("POST");
            con.setDoInput(true);
            con.setDoOutput(true);
            con.connect();

            HttpClient httpClient=new DefaultHttpClient();
            HttpPost httpPost=new HttpPost(""+apiUrl+"/SyncOrderDetails2");

            List<NameValuePair> list=new ArrayList<NameValuePair>();
            list.add(new BasicNameValuePair("erpdoc", documentNumber));
            httpPost.setEntity(new UrlEncodedFormEntity(list));
            HttpResponse httpResponse=	httpClient.execute(httpPost);

            HttpEntity httpEntity=httpResponse.getEntity();
            String s= readResponse(httpResponse);
            if (s.isEmpty() || !s.startsWith("[")){
                res="No Data Found";
                return res;
            }

            ArrayList list4=new ArrayList();

            String[] parts = s.split("_");
            for(int i=0;i<parts.length;i++){
                Vector line=new Vector();
                String test = parts[i];
                String[] parts1 = parts[i].split(",",30);
                for(int j=0;j<parts1.length;j++){

                    String value=parts1[j].replaceFirst("[\\[\\]\\\" \"\\^:,]","");
                    value=value.replaceAll("[\\[\\]]","");
                    line.add(value);

                }
                list4.add(line);
            }
            System.out.println(list4.size());

            for(int i1=0;i1<list4.size();i1++) {
                Vector line = (Vector) list4.get(i1);
                bpCode = ((String) line.get(0));
                bpName = (String) line.get(1);
                phone = ((String) line.get(2));
                dateOrdered = ((String) line.get(3));
                warehouse = ((String) line.get(4));
                total_openbalance = ((String) line.get(5));
                String rate = ((String) line.get(6));
                //uomsymbol  = ((String)line.get(7));
                old_kg = ((String) line.get(8));
                //String productName  = ((String)line.get(9));
                documentNo = ((String) line.get(10));
                des = ((String) line.get(11));
                promised_date = ((String) line.get(12));
                //undelivered_amt = ((String) line.get(13));
                s_name = ((String) line.get(14));
                s_phone = ((String) line.get(15));
                c_order_id = ((String) line.get(16));
                String kg = ((String) line.get(17));
                String qty = ((String) line.get(18));
                String mt_qty = ((String) line.get(19));
                description = ((String) line.get(20));
                bp_location = ((String) line.get(22));
                orgname = ((String) line.get(23));
                C_Doctype_ID = ((String) line.get(24));
                odiscount = ((String) line.get(25));
                percentdis=((String) line.get(26));
                ispdis=((String) line.get(27));
                volume = ((String) line.get(29));

                result = true;
            }
            if (result) {
                res = "Success";
            }


        } catch (Exception e) {
            res = e.getMessage();
        }
        finally {
            try {
                if (con != null)
                    con.disconnect();
                con = null;
            } catch (Exception e) {
            }
        }
        return res;

    }
    //2022-09-27
    private final class AysncOrderlineData extends AsyncTask<String, String, String> {
        ProgressDialog pd;
        @Override
        protected String doInBackground(String... params) {

            String result1="",error="";boolean r=false;
            boolean result = false;
            HttpURLConnection con=null;
            try {
                con = (HttpURLConnection) (new URL("" + apiUrl + "/DBSync")).openConnection();
                con.setRequestMethod("POST");
                con.setDoInput(true);
                con.setDoOutput(true);
                con.connect();

                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost("" + apiUrl + "/DBSync");

                List<NameValuePair> list = new ArrayList<NameValuePair>();
                list.add(new BasicNameValuePair("name", "salesorderlineList"));
                list.add(new BasicNameValuePair("erpdoc", documentNumber));

                httpPost.setEntity(new UrlEncodedFormEntity(list));
                HttpResponse httpResponse = httpClient.execute(httpPost);

                HttpEntity httpEntity = httpResponse.getEntity();
                String s = readResponse(httpResponse);
                if (s.isEmpty() || !s.startsWith("[")) {
                }

                ArrayList list4 = new ArrayList();

                String[] parts = s.split("_");
                for (int i = 0; i < parts.length; i++) {
                    Vector line = new Vector();
                    String test = parts[i];
                    String[] parts1 = parts[i].split(",", 9);
                    for (int j = 0; j < parts1.length; j++) {

                        String value = parts1[j].replaceFirst("[\\[\\]\\\" \"\\^:,]", "");
                        value = value.replaceAll("[\\[\\]]", "");
                        line.add(value);

                    }
                    list4.add(line);
                }

                for (int i1 = 0; i1 < list4.size(); i1++) {
                    Vector line = (Vector) list4.get(i1);
                    String productcode = ((String) line.get(0));
                    String productName = ((String) line.get(1));
                    String uom = ((String) line.get(2));
                    String qty = ((String) line.get(3));
                    String rate = ((String) line.get(4));
                    String total = ((String) line.get(5));
                    String dis_price = ((String) line.get(6));
                    String volume = ((String) line.get(7));
                    String bonus_qty = ((String) line.get(8));

                    double takaAmt = 0;
                    if (!qty.equals("")) {
                        totalBagQty = totalBagQty + Double.parseDouble(qty);

                       // noofpack = String.valueOf((totalBagQty + Double.parseDouble(qty))/(sumvol+Double.parseDouble(volume)));
                        sumvol = sumvol+Double.parseDouble(dis_price);
                        bonus_amt = bonus_amt+Double.parseDouble(bonus_qty);
                        if (!rate.equals("")) {
                            takaAmt =Double.parseDouble(total);
                            totalTakaAmt = totalTakaAmt + takaAmt;
                        }

                    }
                    if (!qty.equals("")) {
                        totalMetricTon = totalMetricTon + Double.parseDouble(qty);
                    }

                    ViewProductDetailsModal modal = new ViewProductDetailsModal(productName, uom, qty, rate, String.valueOf(total),dis_price, volume, bonus_qty);
                    productDetailsModals.add(modal);
                    result = true;
                }
                if (result) {
                    result1 = "Success";
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
            finally {
                try {
                    if (con != null)
                        con.disconnect();
                    con = null;
                } catch (Exception e) {
                }
            }
            return result1;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pd = new ProgressDialog(FullViewOrderActivity.this);
            //   pd.setTitle("Please Wait ");
            //  pd.setMessage("KFG Bank Accounts are fetching........");
            pd.setCancelable(false);
            pd.setIndeterminate(true);
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pd.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try{
                if(s.equals("Success")){
                    long intTotalPrice = (new Double(totalTakaAmt)).longValue();
                    wordsAmtBags =   NumberToWords2.convert(intTotalPrice);
                    setTextField();
                    initRecyclerView();
                    pd.dismiss();
                }else{
                    System.out.println(s);
                    pd.dismiss();
                }
            }catch (NullPointerException ex){

            }
        }
    }
    /*public String readResponse(HttpResponse res) {
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
    }*/



    //2022-09-27

    public void FetchOrderlineDataFromServer() {
        int catagoryID;
        boolean result = false;
        String  res = "";
        HttpURLConnection con =null;

        try {
            try {
                con = (HttpURLConnection) (new URL("" + apiUrl + "/DBSync")).openConnection();

                con.setRequestMethod("POST");
                con.setDoInput(true);
                con.setDoOutput(true);
                con.connect();

                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost("" + apiUrl + "/DBSync");

                List<NameValuePair> list = new ArrayList<NameValuePair>();
                list.add(new BasicNameValuePair("name", "salesorderlineList"));
                //list.add(new BasicNameValuePair("erpdoc", erpDocumentNumber));
                list.add(new BasicNameValuePair("erpdoc", documentNumber));
                //list.add(new BasicNameValuePair("m_warehouse_id",String.valueOf(branchId)));
                httpPost.setEntity(new UrlEncodedFormEntity(list));
                HttpResponse httpResponse = httpClient.execute(httpPost);

                HttpEntity httpEntity = httpResponse.getEntity();
                String s = readResponse(httpResponse);
                if (s.isEmpty() || !s.startsWith("[")) {
                    res = "No Data Found";
                    toast("No Forwardto found for this Order");
                }

                ArrayList list4 = new ArrayList();

                String[] parts = s.split("_");
                for (int i = 0; i < parts.length; i++) {
                    Vector line = new Vector();
                    String test = parts[i];
                    String[] parts1 = parts[i].split(",", 9);
                    for (int j = 0; j < parts1.length; j++) {

                        String value = parts1[j].replaceFirst("[\\[\\]\\\" \"\\^:,]", "");
                        value = value.replaceAll("[\\[\\]]", "");
                        line.add(value);

                    }
                    list4.add(line);
                }

                for (int i1 = 0; i1 < list4.size(); i1++) {
                    Vector line = (Vector) list4.get(i1);
                    String productcode = ((String) line.get(0));
                    String productName = ((String) line.get(1));
                    String uom = ((String) line.get(2));
                    String qty = ((String) line.get(3));
                    String rate = ((String) line.get(4));
                    String total = ((String) line.get(5));
                    String dis_price = ((String) line.get(6));
                    String volume = ((String) line.get(7));
                    String bonus_qty = ((String) line.get(8));

                    double takaAmt = 0;
                    if (!qty.equals("")) {
                        totalBagQty = totalBagQty + Double.parseDouble(qty);
                        if (!rate.equals("")) {
                            takaAmt = Double.parseDouble(qty) * Double.parseDouble(rate);
                            totalTakaAmt = totalTakaAmt + takaAmt;
                        }
                    }
                    if (!qty.equals("")) {
                        totalMetricTon = totalMetricTon + Double.parseDouble(qty);
                    }

                    ViewProductDetailsModal modal = new ViewProductDetailsModal(productName, uom, qty, rate, String.valueOf(total),dis_price, volume, bonus_qty);
                    productDetailsModals.add(modal);
                    result = true;
                }
                if (result) {

                    res = "Success";
                    toast("Success");
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                toastMessage(e.getMessage());

            }
            finally {
                try {
                    if (con != null)
                        con.disconnect();
                    con = null;
                } catch (Exception e) {
                }
            }


        } catch (Exception e) {
            res = e.getMessage();
            toast(res);
        }


    }


    public void loadOrderPrice(Context mContext) {

        try {
            totalPriceTextView.setText(String.valueOf(totalTakaAmt));
            //total_editTextET.setText(String.valueOf(totalTakaAmt));
            total_editTextET.setText(String.valueOf(total_price1));
        } catch (Exception e) {
            Log.d("DEBUG", String.valueOf(e.getMessage()));
            System.out.println(String.valueOf(e.getMessage()));
        }
    }
    private void ondiscountEditTextFocusChange(){

        discout_EditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!totalPriceTextView.getText().toString().equals("") && !discout_EditText.getText().toString().equals("") ){
                    if(!totalPriceTextView.getText().toString().equals(".") && !discountCheckBox
                            .getText().toString().equals(".") ) {
                        BigDecimal price =new BigDecimal(totalTakaAmt); //new BigDecimal(totalPriceTextView.getText().toString());

                        String d=discout_EditText.getText().toString();
                        BigDecimal total_price=new BigDecimal(0);
                        if(!d.isEmpty())
                        {
                            double discount=0.0;
                            if(discountCheckBox.isChecked())
                            {
                                ispercentdiscount=true;
                                //discount=((price )*Double.parseDouble(discout_editText.getText().toString()))/100;
                                BigDecimal discount1=(price.multiply(new BigDecimal(discout_EditText.getText().toString()))).divide(new BigDecimal(100));
                                //total_price=(price )-discount;
                                total_price=price.subtract(discount1);


                            }
                            else
                            {
                                ispercentdiscount=false;
                                //discount=Double.parseDouble(discout_editText.getText().toString());
                                BigDecimal discount1=new BigDecimal(discout_EditText.getText().toString());
                                total_price=price.subtract(discount1);


                            }


                        }
                        else
                        {
                            total_price=price ;
                        }

                        if(total_price.compareTo(new BigDecimal(0)) <=0 )
                        //if(total_price<=0)
                        {
                            toast("Discount amount is higher than total price");
                            total_editTextET.setText(String.valueOf(0.0));
                        }
                        else
                        {
                            total_editTextET.setText(String.valueOf(total_price));
                        }

                    }
                }else{
                    //total_editTextET.setText(String.valueOf(totalTakaAmt));
                    total_editTextET.setText(String.valueOf(total_price1));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
    private void ondiscountCheckBox(){
        discountCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                BigDecimal price =new BigDecimal(totalTakaAmt); // new BigDecimal(totalPriceTextView.getText().toString());
                String d=discout_EditText.getText().toString();
                BigDecimal total_price=new BigDecimal(0);
                if (buttonView.isChecked()) {
                    if(!d.isEmpty())
                    {
                        double discount=0.0;
                        ispercentdiscount=true;
                        BigDecimal discount1=(price.multiply(new BigDecimal(discout_EditText.getText().toString()))).divide(new BigDecimal(100));
                        total_price=price.subtract(discount1);
                        if(total_price.compareTo(new BigDecimal(0)) <=0 )
                        {
                            toast("Discount amount is higher than total price");
                            total_editTextET.setText(String.valueOf(0.0));
                        }
                        else
                        {
                            total_editTextET.setText(String.valueOf(total_price));
                        }
                    }
                    else
                    {
                        total_price=price ;
                        if(total_price.compareTo(new BigDecimal(0)) <=0 )
                        {
                            toast("Discount amount is higher than total price");
                            total_editTextET.setText(String.valueOf(0.0));
                        }
                        else
                        {
                            total_editTextET.setText(String.valueOf(total_price1));
                        }
                    }

                } else {
                    ispercentdiscount=false;
                    if(d.isEmpty()) {
                        total_editTextET.setText(String.valueOf(total_price1));
                    }
                    else{
                        BigDecimal discount1=new BigDecimal(discout_EditText.getText().toString());
                        total_price=price.subtract(discount1);
                        if(total_price.compareTo(new BigDecimal(0)) <=0 ) {

                            toast("Discount amount is higher than total price");
                            total_editTextET.setText(String.valueOf(0.0));
                        }else {
                            total_editTextET.setText(String.valueOf(total_price));
                        }
                    }
                }
            }
        });
    }

    public void toast(String msg) {
        Toast.makeText(FullViewOrderActivity.this,msg,Toast.LENGTH_SHORT).show();
    }
}
