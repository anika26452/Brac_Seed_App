package com.sysnova.brac_seed_app.Activity;

import static com.sysnova.brac_seed_app.Activity.LoginActivity.ad_user_id;
import static com.sysnova.brac_seed_app.Activity.LoginActivity.employeeid;
import static com.sysnova.brac_seed_app.Activity.PostgreSQL.apiUrl;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sysnova.brac_seed_app.R;
import com.sysnova.brac_seed_app.adapter.ShowCostCenter;
import com.sysnova.brac_seed_app.modal.ViewMortalityListModel;
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
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

public class DealerPayment extends AppCompatActivity {

    SearchableSpinner orgSpinner, ctypespinner, bankspinner, territoryspinner,CustomerSpinner, costcenterspinner, paymenttypespinner;
    DatabaseHelper mDatabaseHelper;
    String selectedOrgName="",selectedOrgId="", selectedctype="", selectedbankacc="", selectedbankacctID="",
           selectedTerritoryName="",selectedTerritoryId="",c_bp_group_id = "", selectedBPName= "",existing_docId = "0",
           selectedBPID = "", selectedBPTerrID= "", selectedcostcenter= "", selectedcostcenterid = "",
            selectedpaymenttype="", transactiondate = "", accdate, paymentamt, remarks,payment_remarks, depositamt = "",
            orgname = "", territory = "", collection_type = "", bank_acc = "",transaction_date = "", acc_date = "",
            bpname = "", cost_centre = "", payamt = "", paymenttype = "", paymentthrough = "", docno = "", c_bank_id = "", isrejected = "",
            isreverted = "", docstatus = "", rejectereason = "", c_payment_id = "",payment_documentNo,branchname = "",
            depositdate = "";
    ImageView calenderImageview;
    private DatePickerDialog.OnDateSetListener dateSetListener, depositdateSetListener;
    EditText transactiondateedittext, paymentamtedittext, indirectptypeedittextvw, remarksedittextvw,
            depositamteditvw, depositdateedittextvw,branchnameedttxtvw;
    SimpleDateFormat formt = new SimpleDateFormat("yyyy-MM-dd");
    TextView currentdatetxtvw,revertrejecttxtvw, rejectreasontxtvw, tdpaidamtvw;
    LinearLayout indirptypewlayoutvw,  revertrejectlayout;
    Button submitbtn,addcostcentrebtn;
    RecyclerView showrecyclervw;
    int cmort = 0;
    LinearLayout proviewlayoutvw;
    HashMap<String,String> hashOrgId  = new HashMap<>();
    HashMap<String,String> hashBankId  = new HashMap<>();
    HashMap<String, String> hashBPID = new HashMap<>();
    HashMap<String,String> hashTerritoryId  = new HashMap<>();
    HashMap<String, String> hashBPTerrID = new HashMap<>();
    HashMap<String, String> hashCostCenterID = new HashMap<>();
    ArrayList<String> orgNameArrayList = new ArrayList<String>();
    ArrayList<String> ctypeArrayList = new ArrayList<String>();
    ArrayList<String> BankArrayList = new ArrayList<String>();
    ArrayList<String> customerArrayList = new ArrayList<>();
    ArrayList<String> costcenterArrayList = new ArrayList<>();
    ArrayList<String> TerritoryNameArrayList = new ArrayList<String>();
    ArrayList<String> paymenttypeArrayList = new ArrayList<String>();
    ArrayList<ViewMortalityListModel> productmortArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dealer_payment);

        getSupportActionBar().setTitle("Dealer Payment");
        getSupportActionBar().setBackgroundDrawable(
                new ColorDrawable(Color.parseColor("#97c740")));

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle!=null) {
            existing_docId   = getIntent().getStringExtra("order_doc");
        }

        long now = System.currentTimeMillis();
        String strLong = Long.toString(now);
        payment_documentNo = "P"+employeeid + strLong;

        orgSpinner = (SearchableSpinner) findViewById(R.id.org_spinner);
        ctypespinner = findViewById(R.id.collectiontype_spinner);
        bankspinner = findViewById(R.id.bank_spinner);
        calenderImageview = findViewById(R.id.calenderview);
        transactiondateedittext = findViewById(R.id.transactiondate_edittext);
        currentdatetxtvw = findViewById(R.id.currentdate_textvw);
        territoryspinner = findViewById(R.id.territory_spinner);
        CustomerSpinner = findViewById(R.id.bp_spinner);
        paymentamtedittext = findViewById(R.id.paymentamtedittext);
        costcenterspinner = findViewById(R.id.costcenter_spinner);
        paymenttypespinner = findViewById(R.id.paymenttype_spinner);
        indirptypewlayoutvw = findViewById(R.id.indirptypewlayout);
        indirectptypeedittextvw = findViewById(R.id.indirectptypeedittext);
        remarksedittextvw = findViewById(R.id.remarksedittext);
        submitbtn = findViewById(R.id.submit_button);
        revertrejecttxtvw = findViewById(R.id.revertreject_textview);
        rejectreasontxtvw = findViewById(R.id.reason_textview);
        revertrejectlayout = findViewById(R.id.revertreject_layout);
        depositamteditvw = findViewById(R.id.depositamtedittext);
        depositdateedittextvw = findViewById(R.id.depositdate_edittext);
        addcostcentrebtn = findViewById(R.id.addcostcentre_btn);
        showrecyclervw = findViewById(R.id.showrecyclervw);
        proviewlayoutvw = findViewById(R.id.proviewlayout);
        tdpaidamtvw = findViewById(R.id.tdpaidamt);
        branchnameedttxtvw = findViewById(R.id.branchnameedittext);

        mDatabaseHelper = new DatabaseHelper(this);
        mDatabaseHelper.deleteAllPaymentLine();

        ctypeArrayList.add("Advance Collection");
        ctypeArrayList.add("Collection Against Invoice");
        ctypeArrayList.add("Booking Money");
        ctypeArrayList.add("Cash Order");

        currentdatetxtvw.setText(formt.format(new Date()));

        paymenttypeArrayList.add("Direct");
        paymenttypeArrayList.add("Indirect");

        if(!existing_docId.equals("0")){
            submitbtn.setText("Re-Submit");
            revertrejectlayout.setVisibility(View.VISIBLE);

            AsyncLoadData asyncLoadData = new AsyncLoadData();
            asyncLoadData.execute();
        }else{
            revertrejectlayout.setVisibility(View.GONE);
        }

        getOrg();
        getTerritory();
        getCustomer();
        getCostcenter();
        loadPaymentTypeSpinner();
        ViewProductLayout();
        getCollectionType();

        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month+1;
                Date date = new Date(year-1900, month-1,dayOfMonth);
                String pdate = formt.format(date);
                transactiondateedittext.setText(pdate);
            }
        };

        depositdateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month+1;
                Date date = new Date(year-1900, month-1,dayOfMonth);
                String ddate = formt.format(date);
                depositdateedittextvw.setText(ddate);
            }
        };

        submitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                submitbtn.setEnabled(false);

                AsyncSendToServer asyncSendToServer=new AsyncSendToServer();
                asyncSendToServer.execute();
            }
        });

        addcostcentrebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paymentamt = paymentamtedittext.getText().toString().trim();
                depositamt = depositamteditvw.getText().toString().trim();
                transactiondate = transactiondateedittext.getText().toString().trim();
                accdate = currentdatetxtvw.getText().toString().trim();
                remarks = remarksedittextvw.getText().toString().trim();
                payment_remarks = indirectptypeedittextvw.getText().toString().trim();
                branchname = branchnameedttxtvw.getText().toString().trim();
                depositdate = depositdateedittextvw.getText().toString().trim();

                if (selectedOrgId.equals("")){
                    toastMessage("Select Organization!!!");
                    return;
                }

                if (selectedTerritoryId.equals("")){
                    toastMessage("Select Territory!!!");
                    return;
                }

                if(selectedctype.equals("-- Select Collection Type --")){
                    toastMessage("Select Collection Type!!!");
                    return;
                }

                if(selectedbankacctID.equals("1")){
                    toastMessage("Please select Bank!!!");
                    return;
                }

                if(transactiondate.equals("")){
                    toastMessage("Please add transaction Date");
                    return;
                }

                if(depositamt.isEmpty()) {
                    depositamteditvw.requestFocus();
                    depositamteditvw.setError("Please Enter Deposit Amount!");
                    return ;
                }

                if(selectedpaymenttype.equals("Indirect")){
                    if(payment_remarks.equals("")){
                        toastMessage("Please enter remarks for Indirect Payment Type!!!");
                        return;
                    }
                }

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

                Boolean res = false, resline = false;
                BigDecimal givenPaidAmt = BigDecimal.ZERO;

                Cursor cursor = mDatabaseHelper.getcountpaidamt();

                if(cursor.moveToFirst()){
                    do{
                        String amount = cursor.getString(0);

                        if (amount != null && !amount.trim().isEmpty()) {
                            givenPaidAmt = givenPaidAmt.add(new BigDecimal(amount));
                        }
                    }while(cursor.moveToNext());
                }

                tdpaidamtvw.setText(String.valueOf(givenPaidAmt));

                BigDecimal enteredAmount = new BigDecimal(depositamt);

                if (enteredAmount.compareTo(givenPaidAmt.add(new BigDecimal(paymentamt))) >= 0){
                    res = mDatabaseHelper.insertIntoPaymentLine(selectedcostcenterid, selectedcostcenter, selectedBPID,selectedBPName, paymentamt);

                    if(res){
                        toastMessage("Done successfully");
                        paymentamtedittext.setText("");
                        loadCustomerSpinner();
                        loadCostCenterSpinner();
                    }else{
                        toastMessage("Something is wrong");
                        paymentamtedittext.setText("");
                        loadCustomerSpinner();
                        loadCostCenterSpinner();
                    }

                    ViewProductLayout();
                }else{
                    toastMessage("You have exceeded total payment amt.");
                }
            }
        });
    }

    private void getOrg() {
        hashOrgId.clear();

        if(!orgNameArrayList.isEmpty()){
            orgNameArrayList.clear();
        }

        Cursor cursor = null;

        try {
            cursor = mDatabaseHelper.getOrganization();

            if (cursor.moveToFirst()) {
                do {
                    String orgId = cursor.getString(0);
                    String orgName = cursor.getString(1);
                    orgNameArrayList.add(orgName);
                    hashOrgId.put(orgName, orgId);
                } while (cursor.moveToNext());
            }
        }
        finally {
            if (cursor != null)
                cursor.close();
        }
        loadOrganizationSpinner();
    }

    private void getCollectionType() {

        if(!ctypeArrayList.isEmpty()){
            ctypeArrayList.clear();
        }

        Cursor cursor = null;

        try {
            cursor = mDatabaseHelper.getCollectionType();

            if (cursor.moveToFirst()) {
                do {
                    String orgId = cursor.getString(0);
                    String orgName = cursor.getString(1);

                    ctypeArrayList.add(orgName);
                } while (cursor.moveToNext());
            }
        }
        finally {
            if (cursor != null)
                cursor.close();
        }
        loadCollectionTypeSpinner();
    }

/*    private void loadOrganizationSpinner(){

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, orgNameArrayList);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        orgSpinner.setAdapter(dataAdapter);
        View selectedView = orgSpinner.getSelectedView();

        if (selectedView instanceof TextView) {
            ((TextView) selectedView).setTextColor(
                    ContextCompat.getColor(this, R.color.spinner_text)
            );
        }
        dataAdapter.notifyDataSetChanged();
        selectOrgSpinner();
    }*/

    private void loadOrganizationSpinner() {

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                orgNameArrayList
        ) {
            @Override
            public View getView(int position, View convertView, android.view.ViewGroup parent) {

                View view = super.getView(position, convertView, parent);

                TextView textView = (TextView) view.findViewById(android.R.id.text1);

                if (textView != null) {
                    textView.setTextColor(
                            ContextCompat.getColor(DealerPayment.this, R.color.spinner_text)
                    );
                }

                return view;
            }

            @Override
            public View getDropDownView(
                    int position,
                    View convertView,
                    android.view.ViewGroup parent) {

                View view = super.getDropDownView(position, convertView, parent);

                TextView textView = (TextView) view.findViewById(android.R.id.text1);

                if (textView != null) {
                    textView.setTextColor(
                            ContextCompat.getColor(DealerPayment.this, R.color.spinner_text)
                    );
                }

                return view;
            }
        };

        dataAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        orgSpinner.setAdapter(dataAdapter);

        selectOrgSpinner();
    }

    private void selectOrgSpinner(){
        orgSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    String selectedOrg = parent.getSelectedItem().toString();
                    selectedOrgName=selectedOrg;
                    selectedOrgId = hashOrgId.get(selectedOrg);

                    getBank();

                }catch (NullPointerException ex){

                }catch (ArrayIndexOutOfBoundsException ex){

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void loadCollectionTypeSpinner(){

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, ctypeArrayList);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ctypespinner.setAdapter(dataAdapter);
        dataAdapter.notifyDataSetChanged();
        selectCollectionTypeSpinner();
    }

    private void selectCollectionTypeSpinner(){
        ctypespinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    selectedctype = parent.getSelectedItem().toString();

                }catch (NullPointerException ex){

                }catch (ArrayIndexOutOfBoundsException ex){

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void getBank() {
        hashBankId.clear();

        if(!BankArrayList.isEmpty()){
            BankArrayList.clear();
        }

        Cursor cursor = null;

        try {
            cursor = mDatabaseHelper.getAllBank(selectedOrgId);

            if (cursor.moveToFirst()) {
                do {
                    String c_bankaccount_id = cursor.getString(1);
                    String accountno = cursor.getString(2);
                    String bank = cursor.getString(3);
                    String ad_org_id = cursor.getString(4);
                    String bracAC = cursor.getString(5);

                    BankArrayList.add(bracAC);
                    hashBankId.put(bracAC, c_bankaccount_id);
                } while (cursor.moveToNext());
            }
        }
        finally {
            if (cursor != null)
                cursor.close();
        }

        loadBankSpinner();
    }

    private void loadBankSpinner(){

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, BankArrayList);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bankspinner.setAdapter(dataAdapter);
        dataAdapter.notifyDataSetChanged();
        selectBankSpinner();
    }

    private void selectBankSpinner(){
        bankspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    selectedbankacc = parent.getSelectedItem().toString();
                    selectedbankacctID = hashBankId.get(selectedbankacc);

                }catch (NullPointerException ex){

                }catch (ArrayIndexOutOfBoundsException ex){

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void onClickDatePicker(View view) {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                DealerPayment.this,
                dateSetListener,
                year, month, day);

        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

        datePickerDialog.show();
    }

    public void onClickdepositDatePicker(View view) {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                DealerPayment.this,
                depositdateSetListener,
                year, month, day);

        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

        datePickerDialog.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(getApplicationContext(), Home.class);
        startActivity(intent);
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
        hashBPTerrID.put("--- Select BP ---", "1");

        Cursor cursor = null;
        try {
            cursor = mDatabaseHelper.getCustomerterrwise(selectedTerritoryId);

            if (cursor.moveToFirst()) {
                do {
                    String orgId = cursor.getString(1);
                    String orgName = cursor.getString(2);
                    String territory_ID = cursor.getString(6);
                    c_bp_group_id = cursor.getString(8);

                    customerArrayList.add(orgName);
                    hashBPID.put(orgName, orgId);
                    hashBPTerrID.put(orgName, territory_ID);

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
                    selectedBPTerrID = hashBPTerrID.get(bpcustomer);

                } catch (NullPointerException ex) {

                } catch (ArrayIndexOutOfBoundsException ex) {

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void getTerritory() {

        hashTerritoryId.clear();
        if(!TerritoryNameArrayList.isEmpty())
        {
            TerritoryNameArrayList.clear();
        }
        Cursor cursor = null;
        try {
            cursor = mDatabaseHelper.getTerritory();

            if (cursor.moveToFirst()) {
                do {
                    String orgId = cursor.getString(0);
                    String orgName = cursor.getString(1);
                    TerritoryNameArrayList.add(orgName);
                    hashTerritoryId.put(orgName, orgId);
                } while (cursor.moveToNext());
            }
        }
        finally {
            if (cursor != null)
                cursor.close();
        }
        loadTerritorySpinner();

    }
    private void loadTerritorySpinner(){

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, TerritoryNameArrayList);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        territoryspinner.setAdapter(dataAdapter);
        dataAdapter.notifyDataSetChanged();
        selectTerritorySpinner();
    }

    private void selectTerritorySpinner(){
        territoryspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    String selectedOrg = parent.getSelectedItem().toString();
                    selectedTerritoryName=selectedOrg;
                    selectedTerritoryId = hashTerritoryId.get(selectedOrg);
                    getCustomer();

                }catch (NullPointerException ex){

                }catch (ArrayIndexOutOfBoundsException ex){

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

    private void loadPaymentTypeSpinner(){

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, paymenttypeArrayList);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        paymenttypespinner.setAdapter(dataAdapter);
        dataAdapter.notifyDataSetChanged();
        selectPaymentTypeSpinner();
    }

    private void selectPaymentTypeSpinner(){
        paymenttypespinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    selectedpaymenttype = parent.getSelectedItem().toString();

                    if(selectedpaymenttype.equals("Indirect")){
                        indirptypewlayoutvw.setVisibility(View.VISIBLE);
                    }else{
                        indirptypewlayoutvw.setVisibility(View.GONE);
                    }

                }catch (NullPointerException ex){

                }catch (ArrayIndexOutOfBoundsException ex){

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

                Cursor cursor = mDatabaseHelper.getPaymentline();

                if(cursor.moveToFirst()){
                    do{
                        String cost_center = cursor.getString(0);
                        String bpname = cursor.getString(1);
                        String paidamt = cursor.getString(2);
                        int id = cursor.getInt(3);
                        String c_elementvalue_id = cursor.getString(4);
                        String BPID = cursor.getString(5);

                        Vector headerLines = new Vector();

                        headerLines.add(selectedOrgId);
                        headerLines.add(ad_user_id);
                        headerLines.add(selectedTerritoryId);
                        headerLines.add(selectedctype);
                        headerLines.add(selectedbankacctID);
                        headerLines.add(transactiondate);
                        headerLines.add(accdate);
                        headerLines.add(BPID);  //selectedBPID
                        headerLines.add(c_elementvalue_id); //selectedcostcenterid
                        headerLines.add(paidamt);  //paymentamt
                        headerLines.add(selectedpaymenttype);
                        headerLines.add(remarks);
                        headerLines.add(payment_remarks);
                        headerLines.add(existing_docId);
                        headerLines.add(payment_documentNo);
                        headerLines.add(depositamt);
                        headerLines.add(branchname);
                        headerLines.add(depositdate);

                        salesHeader.add(headerLines);

                    }while(cursor.moveToNext());
                }

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

            pd = new ProgressDialog(DealerPayment.this);
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
                    Intent intent = new Intent(DealerPayment.this, Home.class);
                    startActivity(intent);
                } else if(s.equals("Payment Updated Successfully")){
                    toastMessage("Payment Updated Successfully!!!");
                    Intent intent = new Intent(DealerPayment.this, Home.class);
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

            pd = new ProgressDialog(DealerPayment.this);
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
                toastMessage("The Previous data are fetched. You can edit and resubmit now!!!");
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
            list.add(new BasicNameValuePair("t_app_payment_id", existing_docId));
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
                depositdate = (String) line.get(22);

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
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) orgSpinner.getAdapter();
        position = adapter.getPosition(orgname);

        if (position >= 0) {
            orgSpinner.setSelection(position);
        }

        ArrayAdapter<String> teradapter = (ArrayAdapter<String>) territoryspinner.getAdapter();
        position = teradapter.getPosition(territory);

        if (position >= 0) {
            territoryspinner.setSelection(position);
        }

        ArrayAdapter<String> ctadapter = (ArrayAdapter<String>) ctypespinner.getAdapter();
        position = ctadapter.getPosition(collection_type);

        if (position >= 0) {
            ctypespinner.setSelection(position);
        }

        ArrayAdapter<String> baadapter = (ArrayAdapter<String>) bankspinner.getAdapter();
        position = baadapter.getPosition(bank_acc);

        if (position >= 0) {
            bankspinner.setSelection(position);
        }

        transactiondateedittext.setText(transaction_date);
        currentdatetxtvw.setText(acc_date);

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

        ArrayAdapter<String> ptadapter = (ArrayAdapter<String>) paymenttypespinner.getAdapter();
        position = ptadapter.getPosition(paymenttype);

        if (position >= 0) {
            paymenttypespinner.setSelection(position);
        }

        remarksedittextvw.setText(remarks);
        if(docstatus.equals("Rejected")){
            revertrejecttxtvw.setText("Rejected Reason");
        }else if(docstatus.equals("Reverted")){
            revertrejecttxtvw.setText("Reverted Reason");
        }

        rejectreasontxtvw.setText(rejectereason);
        depositamteditvw.setText(depositamt);
        branchnameedttxtvw.setText(branchname);
        depositdateedittextvw.setText(depositdate);
    }

    private void ViewProductLayout(){
        Cursor cursor = mDatabaseHelper.getcountPaymentline();

        if(cursor.moveToFirst()){
            do{
                cmort = Integer.parseInt(cursor.getString(0));
            }while(cursor.moveToNext());
        }

        if(cmort<1){
            proviewlayoutvw.setVisibility(View.GONE);
        }else{
            proviewlayoutvw.setVisibility(View.VISIBLE);
        }

        GetCostCenter();
    }

    private void GetCostCenter(){
        if(!productmortArrayList.isEmpty()) {
            productmortArrayList.clear();
        }

        Cursor cursor = mDatabaseHelper.getPaymentline();

        if(cursor.moveToFirst()){
            do{
                String cost_center = cursor.getString(0);
                String bpname = cursor.getString(1);
                String paidamt = cursor.getString(2);
                int id = cursor.getInt(3);

                ViewMortalityListModel orderListModal = new ViewMortalityListModel(cost_center, bpname,paidamt, id);
                productmortArrayList.add(orderListModal);

            }while(cursor.moveToNext());
        }
        initRecyclerView();
    }

    public void initRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        showrecyclervw.setLayoutManager(linearLayoutManager);
        ShowCostCenter adapter = new ShowCostCenter(productmortArrayList, DealerPayment.this);
        showrecyclervw.setAdapter(adapter);
    }
}