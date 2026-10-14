package com.sysnova.brac_seed_app.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.sysnova.brac_seed_app.adapter.ApprovalProductsListRecyclerViewAdapter;
import com.sysnova.brac_seed_app.modal.ApprovalProductDetailsModal;
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
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import static com.sysnova.brac_seed_app.Activity.LoginActivity.ad_user_id;
import static com.sysnova.brac_seed_app.Activity.OrgBranchSelection.selectedOrgId;
import static com.sysnova.brac_seed_app.Activity.PostgreSQL.apiUrl;

public class UpdateApprovalActivity extends AppCompatActivity {

    String bpCode,bpName,phone,dateOrdered,warehouse,total_openbalance,bp_location,description,promisedAmt,orgname;
    String uomsymbol, old_kg,documentNo,des,promised_date,undelivered_amt,s_name,s_phone,c_order_id,C_Doctype_ID;
    String wordsAmtBags;
    Toolbar toolbar;
    Spinner ForwardToSpinner;
    TextView partyCodeTextView,partyNameTextView,addressTextView,mobileNoTextView,soNumberTextView,soIssueDateTextView,srNameTextView,srMobileTextView;
    TextView noofpcktvw, totalvw, totalamtvw,total_bagsTextView,total_moneyTextView,promisedDateTextView,qty_wordsTextView,orgnameTexView;
    TextView mtAmtTextView,deliveryFromTextView,partyBalanceTextView,undeliveredAmtTextView,desTextView,proAmtTextView,totalPriceTextView,discout_editText1;
    EditText msg_text,discout_EditText,total_editTextET;//discout_editText,
    Button yes_button,no_button,edit_order_button;
    RecyclerView recyclerViewList;
    double totalMetricTon = 0,totalBagQty = 0,totalTakaAmt = 0, sumvol=0, bonus_amt = 0;
    BigDecimal total_price1=new BigDecimal(0);
    ArrayList<ApprovalProductDetailsModal> productDetailsModals = new ArrayList<>();
    String msg = "",isApproved = "",documentNumber = "",erpDocumentNumber = "",odiscount,supText="",percentdis="",ispdis="",tmsg="";
    String selectedForwardToId="",selectedForwardToName="";
    String[] choicesArray;
    BigDecimal total_price;
    //static TextView totalPriceTextView,totalPriceInWordsTextView,SelectedOrgName;
    CheckBox discountCheckBox;
    DatabaseHelper dbHelper;
    List<String> ForwardToArrayList = new ArrayList<String>();
    HashMap<String,String> hashForwardToId  = new HashMap<>();
    boolean ispercentdiscount=false;
    private static final DecimalFormat decfor = new DecimalFormat("0.00");
   // String ip = "172.16.9.76";
    //String ip="35.189.187.66";
   // final String apiUrl = "http://"+ip+":9090/SeedsWEBAPI";
    //final String apiUrl = "http://"+ip+":8080/FisheriesWEBAPI";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_approval);

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
        //total_mtTextView      = findViewById(R.id.total_mt);
        total_bagsTextView    = findViewById(R.id.total_bags);
        total_moneyTextView   = findViewById(R.id.total_price_amt);
        promisedDateTextView  = findViewById(R.id.promised_date);
        qty_wordsTextView      = findViewById(R.id.quantity_words_bags);
        mtAmtTextView          = findViewById(R.id.mt_amount);
        deliveryFromTextView   = findViewById(R.id.delivery_from);
        partyBalanceTextView   = findViewById(R.id.party_balance_approval);
       // undeliveredAmtTextView = findViewById(R.id.undelivered_so_amt);
        desTextView            = findViewById(R.id.description_id);
        proAmtTextView         = findViewById(R.id.promAmt_id);
        undeliveredAmtTextView = findViewById(R.id.dis_so_amt);

        ForwardToSpinner =findViewById(R.id.ForwardTo_id_spinner);
        discout_EditText= findViewById(R.id.discout_editText);
        discountCheckBox = (CheckBox)findViewById(R.id.discount_checkbox);
        totalPriceTextView = findViewById(R.id.grandtotal);
        total_editTextET= findViewById(R.id.grandtotal);
        noofpcktvw = findViewById(R.id.noofpckttextvw);
        totalvw = findViewById(R.id.total_disc_amt);
        totalamtvw = findViewById(R.id.totalbonus_amt);

        msg_text = findViewById(R.id.msg_id);
        yes_button = findViewById(R.id.yes_btn);
        no_button = findViewById(R.id.no_btn);
        //edit_order_button = findViewById(R.id.edit_order_btn);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle!=null)
        {
              documentNumber   = getIntent().getStringExtra("order_doc");
              erpDocumentNumber= getIntent().getStringExtra("erp_doc");
        }

        getSupportActionBar().setTitle("Update Approval Activity");
        //getSupportActionBar().setSubtitle("Brac Fisheries App");
        getSupportActionBar().setBackgroundDrawable(
                new ColorDrawable(Color.parseColor("#97c740")));

        dbHelper = new DatabaseHelper(this);
        buttonClick();
        //loadOrderPrice(UpdateApprovalActivity.this);
        //ondiscountEditTextFocusChange();
        //ondiscountCheckBox();
        AsyncLoadData asyncLoadData = new AsyncLoadData();
        asyncLoadData.execute();


    }

    private void buttonClick(){
        yes_button.setOnClickListener(new View.OnClickListener() {
            Context mContext=UpdateApprovalActivity.this;
            @Override
            public void onClick(View v) {
                msg = msg_text.getText().toString();
                String text1="";
                isApproved = "Y";
                if (supText.equals("")){

                    text1 = ForwardToSpinner.getSelectedItem().toString();

                    if(text1.equals("") || text1.equals(null)) {
                        if(!odiscount.equals("0"))
                        {
                            /*if(ForwardToArrayList.size()>1) {

                                ShowAlert1("Warning", "Order has Special Discount, Please Select Forward To.");
                            }
                            else{
                                ShowAlert("Warning", "N.B. This Document will be Completed. There will be no further way to Forward.");
                            }*/
                        }
                        else {
                                ShowAlert("Warning", "N.B. This Document will be Completed. There will be no further way to Forward.");

                        }

                    }
                    else{
                        ShowAlert("Warning", "This will be approved. Forward To:"+text1);
                    }

                }
                else{
                     ShowAlert("Warning", "N.B. This Document will be Completed. There will be no further way to Forward.");
                }
            }
        });

        no_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                msg = msg_text.getText().toString();
                isApproved = "N";
                String text1="";
                if(msg.equals("") || msg.equals(null)){
                    msg_text.setError("Please Leave a message");
                    return;
                }
                if (supText.equals("")){
                    text1 = ForwardToSpinner.getSelectedItem().toString();
                    if(text1.equals("") || text1.equals(null)) {
                        ShowAlert("Warning", "N.B. This Document will be Completed. There will be no further way to Forward.");
                    }
                    else{
                        ShowAlert("Warning", "This will be rejected.");
                    }

                }
                else{
                    ShowAlert("Warning", "N.B. This Document will be Completed. There will be no further way to Forward.");
                }
            }
        });

        /*edit_order_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(UpdateApprovalActivity.this,UpdateOrderActivity.class);
                intent.putExtra("erp_doc",erpDocumentNumber);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });*/

    }
    public void ShowAlert(String title,String msg){
        androidx.appcompat.app.AlertDialog.Builder dlgAlert  = new androidx.appcompat.app.AlertDialog.Builder(UpdateApprovalActivity.this);
        dlgAlert.setMessage(msg);
        dlgAlert.setTitle(title);
        dlgAlert.setIcon(R.drawable.logo);
        dlgAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                AsyncSender2 task = new AsyncSender2();
                task.execute();
                /*Intent intent  = new Intent(UpdateApprovalActivity.this,Home.class);
                startActivity(intent);
                finish();*/
            }
            });
        dlgAlert.setNegativeButton("NO", null);
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
    }
    public void ShowAlert1(String title,String msg){
        androidx.appcompat.app.AlertDialog.Builder dlgAlert  = new androidx.appcompat.app.AlertDialog.Builder(UpdateApprovalActivity.this);
        dlgAlert.setMessage(msg);
        dlgAlert.setTitle(title);
        dlgAlert.setIcon(R.drawable.logo);
        /*dlgAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                AsyncSender2 task = new AsyncSender2();
                task.execute();
                Intent intent  = new Intent(UpdateApprovalActivity.this,Home.class);
                startActivity(intent);
                finish();
            }
        });*/
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

            pd = new ProgressDialog(UpdateApprovalActivity.this);
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

                /*FetchOrderlineDataFromServer();
                //wordsAmtBags = NumberToWords1.convert((int)totalBagQty);
                long intTotalPrice = (new Double(totalTakaAmt)).longValue();
                wordsAmtBags =   NumberToWords2.convert(intTotalPrice);
                setTextField();*/
                //FetchForwardToDataFromServer();
                //initRecyclerView();
                //loadOrderList();
                //loadOrderPrice(UpdateApprovalActivity.this);
               // getForwardTo();
            }
            else {
                toastMessage("Something Went Wrong!!!!");
            }
        }

    }

    private String dbSYNC() {
        String res = "";
        try {
            dbHelper.deleteSupervisorData();
        } catch (Exception e) {
            res = e.getMessage();
            return res;
        }
        try {
            dbHelper.deleteForwardTo();
        } catch (Exception e) {
            res = e.getMessage();
            return res;
        }

        res=FetchDataFromServer();
        res=FetchForwardToDataFromServer();
        try {
            return res;
        } catch (Exception e){
            res=e.getMessage();
        }
        return res;

    }

    private void setTextField(){
        String bp_location1="",phone1="",s_phone1="",promised_date1="",promisedAmt1="",percentdis1="";
        if(!bp_location.equals("null") && bp_location!= null && !bp_location.isEmpty()) {
            bp_location1=bp_location;
        }
        if(!phone.equals("null") && phone!= null && !phone.isEmpty()) {
            phone1 = phone;
        }
        if(!s_phone.equals("null") && s_phone!= null && !s_phone.isEmpty()) {
            s_phone1 = s_phone;
        }
        if(!promised_date.equals("null") && promised_date!= null && !promised_date.isEmpty()) {
            promised_date1 = promised_date;
        }
        if(!tmsg.equals("null") && tmsg!= null && !tmsg.isEmpty()) {
            promisedAmt1 = tmsg;
        }
        if(!odiscount.equals("null") && odiscount!= null && !odiscount.isEmpty()) {
            BigDecimal price1=new BigDecimal(totalTakaAmt);
            if (ispdis.equals("Y")){
                BigDecimal discount1=(price1.multiply(new BigDecimal(odiscount))).divide(new BigDecimal(100));
                //total_price=(price )-discount;
                total_price1=price1.subtract(discount1);

            }
            else{
                BigDecimal discount1=new BigDecimal(odiscount);
                total_price1=price1.subtract(discount1);
            }
           // BigDecimal price1=new BigDecimal(totalTakaAmt);
           // BigDecimal discount1=(price1.multiply(new BigDecimal(odiscount))).divide(new BigDecimal(100));
            //total_price=(price )-discount;
           // total_price1=price1.subtract(discount1);
        }
        if(!percentdis.equals("null") && percentdis!= null && !percentdis.isEmpty()) {
            percentdis1 = percentdis;
        }


        orgnameTexView.setText(orgname);
        partyCodeTextView.setText(bpCode);
        partyNameTextView.setText(bpName);
        addressTextView.setText(bp_location1);
        mobileNoTextView.setText(phone1);
        soNumberTextView.setText(documentNo);
        dateOrdered = dateOrdered.replaceAll("\\d\\d:\\d\\d:\\d\\d.\\d","");
        soIssueDateTextView.setText(dateOrdered);
        srNameTextView.setText(s_name);
        srMobileTextView.setText(s_phone1);
        //total_mtTextView.setText(String.valueOf(totalMetricTon));
        total_bagsTextView.setText(String.valueOf(decfor.format(totalBagQty)));
        total_moneyTextView.setText(String.valueOf(decfor.format(totalTakaAmt)));
        totalvw.setText(String.valueOf(decfor.format(sumvol)));
        totalamtvw.setText(String.valueOf(decfor.format(bonus_amt)));
        promised_date1 = promised_date1.replaceAll("\\d\\d:\\d\\d:\\d\\d.\\d","");
        promisedDateTextView.setText(promised_date1);
        mtAmtTextView.setText(String.valueOf(total_price1));
        deliveryFromTextView.setText(warehouse);
        partyBalanceTextView.setText(total_openbalance);
        qty_wordsTextView.setText(wordsAmtBags+" Taka Only");
        //anika 2023-08-16
        System.out.println("description");
        System.out.println("description="+description+"p");

        if(!description.isEmpty() && !description.equals(null) ){
            desTextView.setText(description);
        }else{
            String[] result = des.split("\\|");
            if(result.length == 2) {
                desTextView.setText(result[1] + "");
            }else if(result.length == 3){
                desTextView.setText(result[2] + "");
            }else{
                desTextView.setText("");
            }
        }
        //anika 2023-08-16
        proAmtTextView.setText(promisedAmt1);
        undeliveredAmtTextView.setText(percentdis1);
    }
    private void initRecyclerView(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerViewList.setLayoutManager(linearLayoutManager);
        ApprovalProductsListRecyclerViewAdapter adapter = new ApprovalProductsListRecyclerViewAdapter(productDetailsModals,UpdateApprovalActivity.this);
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
                headerLines.add("0");

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

            pd = new ProgressDialog(UpdateApprovalActivity.this);
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
                dbHelper.deleteOrderApprovalLine(erpDocumentNumber);
                Intent intent  = new Intent(UpdateApprovalActivity.this,Home.class);
                startActivity(intent);
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
        Intent intent=new Intent(UpdateApprovalActivity.this, OrderListForApprovalActivity.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }


    public String FetchDataFromServer() {
        int catagoryID;
        boolean result = false;
        boolean result1 = false;
        String  res = "";


        try {

            HttpURLConnection con = (HttpURLConnection) ( new URL(""+apiUrl+"/SyncOrderDetails2")).openConnection();
            con.setRequestMethod("POST");
            con.setDoInput(true);
            con.setDoOutput(true);
            con.connect();

            HttpClient httpClient=new DefaultHttpClient();
            HttpPost httpPost=new HttpPost(""+apiUrl+"/SyncOrderDetails2");

            List<NameValuePair> list=new ArrayList<NameValuePair>();
            list.add(new BasicNameValuePair("erpdoc", erpDocumentNumber));
            //list.add(new BasicNameValuePair("m_warehouse_id",String.valueOf(branchId)));
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
                undelivered_amt = ((String) line.get(13));
                s_name = ((String) line.get(14));
                s_phone = ((String) line.get(15));
                c_order_id = ((String) line.get(16));
                String kg = ((String) line.get(17));
                String qty = ((String) line.get(18));
                String mt_qty = ((String) line.get(19));
                description = ((String) line.get(20));
                promisedAmt = ((String) line.get(21));
                bp_location = ((String) line.get(22));
                orgname = ((String) line.get(23));
                C_Doctype_ID = ((String) line.get(24));
                odiscount = ((String) line.get(25));
                percentdis=((String) line.get(26));
                ispdis=((String) line.get(27));
                tmsg=((String)line.get(28));

                result = true;
            }
            if (result) {
                res = "Success";
            }


        } catch (Exception e) {
            res = e.getMessage();
        }
        return res;

    }

    //2023-01-05
    /*public void FetchOrderlineDataFromServer1() {
        int catagoryID;
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
            list.add(new BasicNameValuePair("name", "salesorderlineList"));
            list.add(new BasicNameValuePair("erpdoc", erpDocumentNumber));
            //list.add(new BasicNameValuePair("m_warehouse_id",String.valueOf(branchId)));
            httpPost.setEntity(new UrlEncodedFormEntity(list));
            HttpResponse httpResponse=	httpClient.execute(httpPost);

            HttpEntity httpEntity=httpResponse.getEntity();
            String s= readResponse(httpResponse);
            if (s.isEmpty() || !s.startsWith("[")){
                res="No Data Found";
                toast("No Forwardto found for this Order");
            }

            ArrayList list4=new ArrayList();

            String[] parts = s.split("_");
            for(int i=0;i<parts.length;i++){
                Vector line=new Vector();
                String test = parts[i];
                String[] parts1 = parts[i].split(",",7);
                for(int j=0;j<parts1.length;j++){

                    String value=parts1[j].replaceFirst("[\\[\\]\\\" \"\\^:,]","");
                    value=value.replaceAll("[\\[\\]]","");
                    line.add(value);

                }
                list4.add(line);
            }

            for(int i1=0;i1<list4.size();i1++) {
                Vector line = (Vector) list4.get(i1);
                String productcode  = ((String)line.get(0));
                String productName  = ((String)line.get(1));
                String uom  = ((String)line.get(2));
                String qty = ((String) line.get(3));
                String rate = ((String) line.get(4));
                String total = ((String) line.get(5));
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

                ApprovalProductDetailsModal modal = new ApprovalProductDetailsModal(productName,uom,qty,rate,String.valueOf(total));
                productDetailsModals.add(modal);
                result = true;
            }
            if (result) {

                res = "Success";
                //toast("Success");
            }


        } catch (Exception e) {
            res = e.getMessage();
            toast(res);
        }

    }*/
    private void selectForwardToSpinner(){
        ForwardToSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    String selectedOrg = parent.getSelectedItem().toString();
                    selectedForwardToName=selectedOrg;
                    selectedForwardToId = hashForwardToId.get(selectedOrg);
                    //toastMessage("org="+selectedOrg+" id="+selectedForwardToId);

                }catch (NullPointerException ex){

                }catch (ArrayIndexOutOfBoundsException ex){

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void loadForwardToSpinner(){

        /*List<String> itemsWithEmpty = new ArrayList<>(ForwardToArrayList);
        itemsWithEmpty.add("");

        String[] choicesArray = itemsWithEmpty.toArray(new String[itemsWithEmpty.size()]);*/

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, ForwardToArrayList);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        ForwardToSpinner.setAdapter(dataAdapter);
        dataAdapter.notifyDataSetChanged();
        selectForwardToSpinner();

    }
    private void getApprovalLineData(){
        if(supText.equals("")) {
            Cursor cursor = dbHelper.getApprovalViewLine(LoginActivity.ad_user_id);
            if (cursor.moveToFirst()) {
                do {
                    String productcode = cursor.getString(0);
                    String productName = cursor.getString(1);
                    String uom = cursor.getString(0);
                    String qty = cursor.getString(1);
                    String rate = cursor.getString(0);
                    String total = cursor.getString(1);

                } while (cursor.moveToNext());
            }
            loadForwardToSpinner();
        }
        else{}

    }

    private void getForwardTo(){
        hashForwardToId.clear();
        if(!ForwardToArrayList.isEmpty())
        {
            ForwardToArrayList.clear();
        }
        if(supText.equals("")) {
            Cursor cursor = dbHelper.getForwardTo();
            if (cursor.moveToFirst()) {
                do {
                    String supid = cursor.getString(1);
                    String supname = cursor.getString(2);
                    ForwardToArrayList.add(supname);
                    hashForwardToId.put(supname, supid);
                } while (cursor.moveToNext());
            }
            loadForwardToSpinner();
       }
        else{}

    }
    //2023-01-07
    public String FetchForwardToDataFromServer(){
            //AsyncLoadForwardToData asyncLoadForwardToData = new AsyncLoadForwardToData();
           // asyncLoadForwardToData.execute();
            String res = "";

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

                ArrayList list13 = new ArrayList();
                List<NameValuePair> list = new ArrayList<NameValuePair>();
                list.add(new BasicNameValuePair("name", "forwardto"));
                list.add(new BasicNameValuePair("userID", LoginActivity.ad_user_id));
                list.add(new BasicNameValuePair("docNo", C_Doctype_ID));
                httpPost.setEntity(new UrlEncodedFormEntity(list));
                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();
                String s = readResponse(httpResponse);
                if (s.isEmpty() || !s.startsWith("[")) {
                    //toast("No Forwardto found for this Order");
                    res="No ForwardTo Found for this Order";
                    supText = "1";

                } else {
                    ArrayList list4 = new ArrayList();
                    String[] parts = s.split("_");
                    for (int i = 0; i < parts.length; i++) {
                        Vector line = new Vector();
                        String[] parts1 = parts[i].split(",");
                        for (int j = 0; j < parts1.length; j++) {

                            String value = parts1[j].replaceFirst("[\\[\\]\\\" \"\\^:,]", "");
                            value = value.replaceAll("[\\[\\]]", "");
                            line.add(value);
                        }
                        list4.add(line);
                    }
                    String supname = "", supid = "";
                    for (int i1 = 0; i1 < list4.size(); i1++) {
                        Vector line = (Vector) list4.get(i1);
                        supname = ((String) line.get(0));
                        supid = (String) line.get(1);
                        //hashForwardToId.put(supname,supid);
                        //result = true;
                        result = dbHelper.insertIntoForwardTo(supid, supname, LoginActivity.ad_user_id);
                        //System.out.println("DOCTYPEID=="+C_Doctype_ID);

                        /*if (supText.equals("")) {
                            ForwardToArrayList.add(supname);
                            hashForwardToId.put(supname, supid);
                            loadForwardToSpinner();
                        } else {
                        }*/
                    }
                    if (result) {
                        res="Success";
                       // toast("Order For Forwardto Sync Successful");
                    }
                }
            } /*catch (Exception e) {
                res = e.getMessage();
                toast(res);
            }*/
            catch (ClientProtocolException e) {
                res=e.getMessage();
                return  res;
            } catch (UnsupportedEncodingException e) {
                res=e.getMessage();
                return  res;
            } catch (IOException e) {
                res="Failed To Connect";
                return  res;
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
    /*private void ondiscountEditTextFocusChange(){

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

                }
                else
                {
                    ispercentdiscount=false;
                    if(d.isEmpty())
                    {
                        total_editTextET.setText(String.valueOf(total_price1));
                        //BigDecimal discount1=new BigDecimal(discout_EditText.getText().toString());
                        //total_price=price.subtract(discount1);
                    }
                    else
                    {
                        BigDecimal discount1=new BigDecimal(discout_EditText.getText().toString());
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
                    //BigDecimal discount1=new BigDecimal(discout_EditText.getText().toString());
                    //total_price=price.subtract(discount1);

                }
            }

        });
    }*/
    //2023-01-07
    private final class AysncOrderlineData extends AsyncTask<String, String, String> {

        ProgressDialog pd;

        /*@Override
        protected void onPreExecute() {
            super.onPreExecute();
            //showProgressDialog();
        }*/

        @Override
        protected String doInBackground(String... params) {

            String result1="",error="";boolean r=false;
            boolean result = false;
            HttpURLConnection con=null;
            try {
                // mDatabaseHelper.deleteEmployee();
                con = (HttpURLConnection) (new URL("" + apiUrl + "/DBSync")).openConnection();

                con.setRequestMethod("POST");
                con.setDoInput(true);
                con.setDoOutput(true);
                con.connect();

                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost("" + apiUrl + "/DBSync");

                List<NameValuePair> list = new ArrayList<NameValuePair>();
                list.add(new BasicNameValuePair("name", "salesorderlineList"));
                list.add(new BasicNameValuePair("erpdoc", erpDocumentNumber));
                //list.add(new BasicNameValuePair("erpdoc", documentNumber));
                //list.add(new BasicNameValuePair("m_warehouse_id",String.valueOf(branchId)));
                httpPost.setEntity(new UrlEncodedFormEntity(list));
                HttpResponse httpResponse = httpClient.execute(httpPost);

                HttpEntity httpEntity = httpResponse.getEntity();
                String s = readResponse(httpResponse);
                if (s.isEmpty() || !s.startsWith("[")) {
                    //res = "No Data Found";
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
                        sumvol = sumvol+Double.parseDouble(dis_price);
                        bonus_amt = bonus_amt+Double.parseDouble(bonus_qty);
                        if (!rate.equals("")) {
                            takaAmt =Double.parseDouble(total);
                        //Double.parseDouble(qty) * Double.parseDouble(rate);
                            totalTakaAmt = totalTakaAmt + takaAmt;
                        }
                    }
                    if (!qty.equals("")) {
                        totalMetricTon = totalMetricTon + Double.parseDouble(qty);
                    }

                    ApprovalProductDetailsModal modal = new ApprovalProductDetailsModal(productName, uom, qty, rate, String.valueOf(total),dis_price, volume, bonus_qty);
                    productDetailsModals.add(modal);
                    result = true;
                }
                if (result) {

                    result1 = "Success";
                    //toast("Success");
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

            pd = new ProgressDialog(UpdateApprovalActivity.this);
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
                    long intTotalPrice = (new Double(totalTakaAmt)).longValue(); //totalBagQty
                    wordsAmtBags =   NumberToWords2.convert(intTotalPrice);

                    setTextField();
                    getForwardTo();
                    initRecyclerView();
                    //2023-01-07
                    pd.dismiss();
                }else{
                    System.out.println(s);
                    pd.dismiss();
                }
            }catch (NullPointerException ex){

            }
        }
    }
    //2023-01-07
    public void toast(String msg)
    {
        Toast.makeText(UpdateApprovalActivity.this,msg,Toast.LENGTH_SHORT).show();
    }

}
