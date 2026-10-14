package com.sysnova.brac_seed_app.Activity;

import static com.sysnova.brac_seed_app.Activity.LoginActivity.ad_user_id;
import static com.sysnova.brac_seed_app.Activity.PostgreSQL.apiUrl;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.sysnova.brac_seed_app.R;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

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
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

public class PaymentDetail extends AppCompatActivity {

    DatabaseHelper mDatabaseHelper;
    String  remarks,payment_remarks,documentNumber = "", orgname = "", territory = "",depositamt= "",
            collection_type = "", bank_acc = "",transaction_date = "", acc_date = "", bpname = "", cost_centre = "",
            payamt = "", paymenttype = "", paymentthrough = "", docno = "", c_bank_id = "", isrejected = "",
            isreverted = "", docstatus = "", rejectereason = "", c_payment_id,branchname="", depostidate = "",
            status = "",selectedBPName= "",selectedBPID = "", selectedcostcenter= "", selectedcostcenterid = "",paymentamt;
    SimpleDateFormat formt = new SimpleDateFormat("yyyy-MM-dd");
    TextView currentdatetxtvw, orgtxtvw, territorytxtvw, collectiontypetxtvw, transactiondatetxtvw, bankacctxtvw,
            bptxtvw, costcentertxtvw, paymentamttxtvw, paymenttypetxtvw, indirectptypetxtVw,remarkstxtvw, revertrejecttxtvw,
            reasontxtvw, bankdeptxtvw, Brannametxtvw, depositedatetxtvw;
    LinearLayout indirptypewlayoutvw, revertrejectlayout,revertlayoutvw, dr_rejectlayoutvw;
    Button submitbtn;
    SearchableSpinner CustomerSpinner, costcenterspinner;
    EditText  paymentamtedittext;
    ArrayList<String> customerArrayList = new ArrayList<>();
    ArrayList<String> costcenterArrayList = new ArrayList<>();
    HashMap<String, String> hashBPID = new HashMap<>();
    HashMap<String, String> hashCostCenterID = new HashMap<>();
    String c_salesregion_id = "", totalpayamt = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_detail);

        getSupportActionBar().setTitle("Dealer Payment");
        getSupportActionBar().setBackgroundDrawable(
                new ColorDrawable(Color.parseColor("#97c740")));

        currentdatetxtvw = findViewById(R.id.currentdate_textvw);
        indirptypewlayoutvw = findViewById(R.id.indirptypewlayout);
        submitbtn = findViewById(R.id.submit_button);
        orgtxtvw = findViewById(R.id.org_textvw);
        territorytxtvw = findViewById(R.id.territory_textview);
        collectiontypetxtvw = findViewById(R.id.collectiontype_textview);
        bankacctxtvw = findViewById(R.id.bankacc_textvw);
        transactiondatetxtvw = findViewById(R.id.transactiondate_textview);
        bptxtvw = findViewById(R.id.bp_textview);
        costcentertxtvw = findViewById(R.id.costcenter_textview);
        paymentamttxtvw = findViewById(R.id.paymentamt_textview);
        paymenttypetxtvw = findViewById(R.id.paymenttype_textview);
        indirectptypetxtVw = findViewById(R.id.indirectptype_textView);
        remarkstxtvw = findViewById(R.id.remarks_textview);
        revertrejecttxtvw = findViewById(R.id.revertreject_textview);
        reasontxtvw = findViewById(R.id.reason_textview);
        revertrejectlayout = findViewById(R.id.revertreject_layout);
        bankdeptxtvw = findViewById(R.id.bankdep_textvw);
        mDatabaseHelper = new DatabaseHelper(this);
        Brannametxtvw = findViewById(R.id.Branname_textvw);
        depositedatetxtvw = findViewById(R.id.depositedate_textview);
        revertlayoutvw= findViewById(R.id.revertlayout);
        dr_rejectlayoutvw = findViewById(R.id.dr_rejectlayout);
        CustomerSpinner = findViewById(R.id.bp_spinner);
        paymentamtedittext = findViewById(R.id.paymentamtedittext);
        costcenterspinner = findViewById(R.id.costcenter_spinner);

        currentdatetxtvw.setText(formt.format(new Date()));

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle!=null) {
            documentNumber = getIntent().getStringExtra("order_doc");
            status = getIntent().getStringExtra("status");
        }

        getFullPayment(documentNumber);

        submitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                submitbtn.setEnabled(false);
                paymentamt = paymentamtedittext.getText().toString().trim();

                if(selectedBPName.equals("--- Select BP ---")){
                    toastMessage("Please select a Business Partner");
                    return;
                }

                if(selectedcostcenterid.equals("1")){
                    toastMessage("Please select a Cost Center");
                    return;
                }

                if(paymentamt.isEmpty()) {
                    paymentamtedittext.requestFocus();
                    paymentamtedittext.setError("Please Enter Payment amount!");
                    return ;
                }

                //payamt - paid for a cost center
                //depositamt - paid for total deposit amt

                BigDecimal payment = new BigDecimal(paymentamt);
                BigDecimal totalPayment = new BigDecimal(totalpayamt);

                BigDecimal pre_payamt = new BigDecimal(payamt);
                BigDecimal newttlpay = totalPayment.subtract(pre_payamt);
                BigDecimal ttldepositamt = new BigDecimal(depositamt);

                if((payment.compareTo(pre_payamt) == 0) && bpname.equals(selectedBPName) && cost_centre.equals(selectedcostcenter)){
                    toastMessage("No changes are made!!!");
                    submitbtn.setEnabled(true);
                    return ;
                }

                if ((payment.add(newttlpay)).compareTo(ttldepositamt) > 0) {
                    // paymentamt > ttldepositamt

                    toastMessage("You have exceeded total deposited amt.");
                    submitbtn.setEnabled(true);
                } else {//if ((payment.add(newttlpay)).compareTo(ttldepositamt) < 0) {
                    // paymentamt < ttldepositamt

                    AsyncSendToServer asyncSendToServer=new AsyncSendToServer();
                    asyncSendToServer.execute();
                }
            }
        });
    }

    private void getFullPayment(String docno) {
        AsyncLoadData asyncLoadData = new AsyncLoadData();
        asyncLoadData.execute();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(getApplicationContext(), ViewDealerPayment.class);
        startActivity(intent);
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
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP,0,0);
        toast.show();
    }

    private final class AsyncLoadData extends AsyncTask<String, String, String>{

        ProgressDialog pd;

        @Override
        protected String doInBackground(String... strings) {

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

            pd = new ProgressDialog(PaymentDetail.this);
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
                setTextField();
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

    public String FetchDataFromServer() {
        boolean result = false;
        String  res = "";
        HttpURLConnection con =null;

        try {
            con = (HttpURLConnection) ( new URL(""+apiUrl+"/DBSync")).openConnection();
            con.setRequestMethod("POST");
            con.setDoInput(true);
            con.setDoOutput(true);
            con.connect();

            HttpClient httpClient=new DefaultHttpClient();
            HttpPost httpPost=new HttpPost(""+apiUrl+"/DBSync");

            List<NameValuePair> list=new ArrayList<NameValuePair>();
            list.add(new BasicNameValuePair("name", "ViewPaymentDetail"));
            list.add(new BasicNameValuePair("t_app_payment_id", documentNumber));
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

                orgname = (String) line.get(0);
                territory = (String) line.get(1);
                collection_type = (String) line.get(2);
                bank_acc = (String) line.get(3);
                transaction_date = (String) line.get(4);
                acc_date = (String) line.get(5);
                bpname = (String) line.get(6);
                cost_centre = (String) line.get(7);
                payamt = (String) line.get(8);
                paymenttype = (String) line.get(9);
                paymentthrough = (String) line.get(10);
                docno = (String) line.get(11);
                c_bank_id = (String) line.get(12);
                isrejected = (String) line.get(13);
                isreverted = (String) line.get(14);
                payment_remarks = (String) line.get(15);
                docstatus = (String) line.get(16);
                rejectereason = (String) line.get(17);
                c_payment_id = (String) line.get(18);
                remarks = (String) line.get(19);
                depositamt = (String) line.get(20);
                branchname = (String) line.get(21);
                depostidate = (String) line.get(22);
                c_salesregion_id = (String) line.get(23);
                totalpayamt = (String) line.get(24);

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

    private void setTextField(){

        int position = 0;
        orgtxtvw.setText(orgname);
        territorytxtvw.setText(territory);
        collectiontypetxtvw.setText(collection_type);
        bankacctxtvw.setText(bank_acc);
        transactiondatetxtvw.setText(transaction_date);
        currentdatetxtvw.setText(acc_date);
        bptxtvw.setText(bpname);
        costcentertxtvw.setText(cost_centre);
        paymentamttxtvw.setText(payamt);
        paymenttypetxtvw.setText(paymenttype);
        remarkstxtvw.setText(remarks);
        reasontxtvw.setText(rejectereason);
        bankdeptxtvw.setText(depositamt);
        Brannametxtvw.setText(branchname);
        depositedatetxtvw.setText(depostidate);

        if(docstatus.equals("Rejected")){
            revertrejecttxtvw.setText("Rejected Reason");
        }else if(docstatus.equals("Reverted")){
            revertrejecttxtvw.setText("Reverted Reason");
        }

        if(docstatus.equals("Drafted") || docstatus.equals("Completed")){
            revertrejectlayout.setVisibility(View.GONE);
        }else {
            revertrejectlayout.setVisibility(View.VISIBLE);
        }

        getCustomer();
        getCostcenter();

        if(docstatus.equals("Reverted")){
            dr_rejectlayoutvw.setVisibility(View.GONE);
            revertlayoutvw.setVisibility(View.VISIBLE);
            submitbtn.setVisibility(View.VISIBLE);
        }else{
            dr_rejectlayoutvw.setVisibility(View.VISIBLE);
            revertlayoutvw.setVisibility(View.GONE);
            submitbtn.setVisibility(View.GONE);
        }

        ArrayAdapter<String> bpadapter = (ArrayAdapter<String>) CustomerSpinner.getAdapter();
        position = bpadapter.getPosition(bpname);

        if (position >= 0) {
            CustomerSpinner.setSelection(position);
        }

        ArrayAdapter<String> csadapter = (ArrayAdapter<String>) costcenterspinner.getAdapter();
        position = csadapter.getPosition(cost_centre);

        if (position >= 0) {
            costcenterspinner.setSelection(position);
        }

        paymentamtedittext.setText(payamt);
    }

    private void getCustomer() {
        if (!customerArrayList.isEmpty()) {
            customerArrayList.clear();
        }

        if (!hashBPID.isEmpty()) {
            hashBPID.clear();
        }

        customerArrayList.add("--- Select BP ---");
        hashBPID.put("--- Select BP ---", "1");

        Cursor cursor = null;
        try {
            cursor = mDatabaseHelper.getCustomerterrwise(c_salesregion_id);

            if (cursor.moveToFirst()) {
                do {
                    String orgId = cursor.getString(1);
                    String orgName = cursor.getString(2);
                    String territory_ID = cursor.getString(6);

                    customerArrayList.add(orgName);
                    hashBPID.put(orgName, orgId);

                } while (cursor.moveToNext());
            }
        }
        finally {
            if (cursor != null)
                cursor.close();
        }
        loadCustomerSpinner();
    }

    private void loadCustomerSpinner() {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, customerArrayList);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        CustomerSpinner.setAdapter(dataAdapter);
        dataAdapter.notifyDataSetChanged();
        selectBPCustomerSpinner();
    }

    private void selectBPCustomerSpinner() {
        CustomerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    String bpcustomer = parent.getSelectedItem().toString();
                    selectedBPName=bpcustomer;
                    selectedBPID = hashBPID.get(bpcustomer);

                } catch (NullPointerException ex) {

                } catch (ArrayIndexOutOfBoundsException ex) {

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void getCostcenter() {
        if (!costcenterArrayList.isEmpty()) {
            costcenterArrayList.clear();
        }

        if (!hashCostCenterID.isEmpty()) {
            hashCostCenterID.clear();
        }

        Cursor cursor = null;
        try {
            cursor = mDatabaseHelper.getCostCenter();

            if (cursor.moveToFirst()) {
                do {
                    String c_elementvalue_id = cursor.getString(1);
                    String costcentername = cursor.getString(2);

                    costcenterArrayList.add(costcentername);
                    hashCostCenterID.put(costcentername, c_elementvalue_id);

                } while (cursor.moveToNext());
            }
        }
        finally {
            if (cursor != null)
                cursor.close();
        }
        loadCostCenterSpinner();
    }

    private void loadCostCenterSpinner() {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, costcenterArrayList);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        costcenterspinner.setAdapter(dataAdapter);
        dataAdapter.notifyDataSetChanged();
        selectCostcenterSpinner();
    }

    private void selectCostcenterSpinner() {
        costcenterspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    String costcenter = parent.getSelectedItem().toString();
                    selectedcostcenter=costcenter;
                    selectedcostcenterid = hashCostCenterID.get(selectedcostcenter);

                } catch (NullPointerException ex) {

                } catch (ArrayIndexOutOfBoundsException ex) {

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private final class AsyncSendToServer extends AsyncTask<String,String,String> {

        ProgressDialog pd;

        @Override
        protected String doInBackground(String... strings) {

            String res = "";
            boolean isactive = true;

            try {
                ArrayList salesHeader = new ArrayList();

                Vector headerLines = new Vector();

                headerLines.add("");
                headerLines.add(ad_user_id);
                headerLines.add("");
                headerLines.add("");
                headerLines.add("");
                headerLines.add("");
                headerLines.add("");
                headerLines.add(selectedBPID);  //selectedBPID
                headerLines.add(selectedcostcenterid); //selectedcostcenterid
                headerLines.add(paymentamt);  //paymentamt
                headerLines.add("");
                headerLines.add(remarks);
                headerLines.add(payment_remarks);
                headerLines.add(documentNumber);
                headerLines.add("");
                headerLines.add(depositamt);
                headerLines.add(branchname);
                headerLines.add("");

                salesHeader.add(headerLines);

                String headerString = "";
                for (Object s : salesHeader) {
                    if (headerString.isEmpty()) {
                        headerString = headerString + s;
                    } else {
                        headerString = headerString + "_" + s;
                    }
                }

                HttpURLConnection con = (HttpURLConnection) (new URL("" + apiUrl + "/DealerPayment")).openConnection();
                con.setRequestMethod("POST");
                con.setDoInput(true);
                con.setDoOutput(true);
                con.connect();

                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost("" + apiUrl + "/DealerPayment");

                List<NameValuePair> list = new ArrayList<NameValuePair>();
                list.add(new BasicNameValuePair("type", "dealerpayment"));
                list.add(new BasicNameValuePair("header", headerString));

                httpPost.setEntity(new UrlEncodedFormEntity(list));
                HttpResponse httpResponse = httpClient.execute(httpPost);

                HttpEntity httpEntity = httpResponse.getEntity();
                String s = readResponse(httpResponse);

                if (s.isEmpty() || !s.equals("Success")) {
                    res = s;
                    return res;
                } else if (s.equals("Success")) {
                    return "Success";
                }
            }
            catch(Exception e){
                res = e.getMessage();
            }
            return res;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pd = new ProgressDialog(PaymentDetail.this);
            pd.setTitle("Please Wait ");
            pd.setMessage("Payment is creating ........");
            pd.setCancelable(false);
            pd.setIndeterminate(true);
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pd.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try{
                submitbtn.setEnabled(true);
                if(s.equals("Inserted  Successfully")){
                    toastMessage("Payment Placed Successfully!!!");
                    mDatabaseHelper.deleteAllPaymentLine();
                    Intent intent = new Intent(PaymentDetail.this, Home.class);
                    startActivity(intent);
                } else if(s.equals("Payment Updated Successfully")){
                    toastMessage("Payment Updated Successfully!!!");
                    Intent intent = new Intent(PaymentDetail.this, Home.class);
                    startActivity(intent);
                }else{
                    System.out.println(s);
                    pd.dismiss();
                    toastMessage("Payment Placed failed");
                    submitbtn.setEnabled(true);
                }
            }catch (NullPointerException ex){}
        }
    }
}