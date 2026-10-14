package com.sysnova.brac_seed_app.Activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.text.TextUtils;

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
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import static com.sysnova.brac_seed_app.Activity.Customer_Selection_Activity.*;
import static com.sysnova.brac_seed_app.Activity.LoginActivity.employeeid;
import static com.sysnova.brac_seed_app.Activity.PostgreSQL.apiUrl;

public class Sales_Order_Activity1 extends AppCompatActivity {
    DatabaseHelper mDatabaseHelper;
    TextView customerName_New,CostCenterText;
    EditText editText_dateTime,qtyinpacket_editText,price_editText,total_editText,quantity_editText, qtyinkg;
    HashMap<String,String> hashUOM  = new HashMap<>();
    HashMap<String,String> hashProductID  = new HashMap<>();
    HashMap<String,String> hashProdID  = new HashMap<>();
    HashMap<String, String> priceHash = new HashMap<>();
    HashMap<String, String> searchkeyHash = new HashMap<>();
    HashMap<String, String> costcenterHash = new HashMap<>();
    HashMap<String, String> elementvalHash = new HashMap<>();
    HashMap<String, String> volumeHash  = new HashMap<>();
    ArrayList<String> productCategoryArrayList = new ArrayList<String>();
    ArrayList<String> productArrayList = new ArrayList<String>();
    String selectedelementval = "",selectedProduct="",selectedProductPrice="", selectedProdId="", discountschemaID="", selectedProductUOM = "";
    String documentNo="",discountschema_id= "", c_bpartner_id = "",c_bp_group_id = "",creditlimitval = "", costcenter_id ="", daysdue = "", extraCond = "",
            t_bpcreditlimit_id = "", validfrom = "", validto="" ,customerName_Code="", salestype = "",
            ad_org_id = "", selectedBPTerrID = "", selectedBPCustomerID ="", total_dueAmt = "", dueAmt = "";
    String categ_name="",CategoryID="", todaydate;
    TextView unitOfMeasure;
    String org_id = "",selectedProductSearchkey="",selectedProductCostCenter="",selectedProductCategoryID="",
            discountschemabreakID="",selectedProductVolume="";
    Button buttonAdd;
    BigDecimal overDueAmt;
    Spinner ProductCategorySpinner,ProductSpinner;
    String date_order="", cashpos = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_order1);

        getSupportActionBar().setTitle("Create Sales Order");
        getSupportActionBar().setBackgroundDrawable(
                new ColorDrawable(Color.parseColor("#97c740")));

        long now = System.currentTimeMillis();
        String strLong = Long.toString(now);
        documentNo = employeeid + strLong;
        customerName_Code=getIntent().getStringExtra("CUSTOMERNAMECODE");
        selectedBPCustomerID = getIntent().getStringExtra("selectedBPCustomerID");
        selectedBPTerrID = getIntent().getStringExtra("selectedBPTerrID");       //anika 2026-04-22
        date_order = getIntent().getStringExtra("date_ordered");
        c_bp_group_id = getIntent().getStringExtra("c_bp_group_id");
        iscash = getIntent().getStringExtra("iscash");
        salestype = getIntent().getStringExtra("selectedSalesType");
        cashpos = getIntent().getStringExtra("cashpos");
        org_id = getIntent().getStringExtra("ad_org_id");

        customerName_New=findViewById(R.id.customernamecode);
        customerName_New.setText(customerName_Code);

        mDatabaseHelper=DatabaseHelper.getInstance(Sales_Order_Activity1.this);
        qtyinpacket_editText=(EditText)findViewById(R.id.editText_qtyinpacket);
        quantity_editText=(EditText)findViewById(R.id.editText_quantityordered);
        price_editText=(EditText)findViewById(R.id.editText_price);
        total_editText=(EditText)findViewById(R.id.total_editText);
        unitOfMeasure=(TextView)findViewById(R.id.uom);
        CostCenterText=(TextView)findViewById(R.id.CostCenter);
        buttonAdd = (Button) findViewById(R.id.button_Add);
        ProductCategorySpinner=findViewById(R.id.product_category_spinner);
        ProductSpinner=findViewById(R.id.product_spinner);
        qtyinkg = findViewById(R.id.editText_qtyinkg);

        todaydate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        if(salestype.equals("Corporate Sales Order") || salestype.equals("Sample Seed Sale") || salestype.equals("Compensation Sales Order") || salestype.equals("Special Price Sales Order")){
            price_editText.setEnabled(true);
        }

        qtyinkg.setEnabled(false);

        getMBpGrDiscounts();
        onQuantityEditTextFocusChange();
        onPriceEditTextFocusChange();
        getProductCategory(selectedBPTerrID);
        getlistproduct();
    }

    private void getMBpGrDiscounts(){
        Asyncdiscountschema task = new Asyncdiscountschema();
        task.execute();
    }

    private final class Asyncdiscountschema extends AsyncTask<String, String, String> {
        ProgressDialog pd;

        @Override
        protected String doInBackground(String... params) {
            String result = "", error = "";
            try {
                result = discountschema();
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
            pd = new ProgressDialog(Sales_Order_Activity1.this);
            pd.setTitle("Please Wait ");
            pd.setMessage("Discount Schemas are loading ........");
            pd.setCancelable(false);
            pd.setIndeterminate(true);
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pd.show();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pd.dismiss();

            if (result.equals("Discount Schema Sync Successful")) {
             //   toastMessage("Discount Schemas are found");
            } else {
              //  toastMessage("Discount Schemas are Not Found");
            }
        }
    }

    private String creditLimitAgain() {

        String res = "";

        res = FetchCreditLimitFromServer();

        try {
            return res;
        } catch (Exception e){
            res=e.getMessage();
        }

        return res;
    }
    private String creditLimit() {

        String res = "";

        res = FetchCreditLimitFromServer();

        try {
            return res;
        } catch (Exception e){
            res=e.getMessage();
        }

        return res;
    }

    private String discountschema() {

        String res = "";

        try {
            mDatabaseHelper.deleteDiscountSchema();
        } catch (Exception e) {
            res = e.getMessage();
            return res;
        }

        res = FetchDiscountSchemaFromServer();

        try {
            return res;
        } catch (Exception e){
            res=e.getMessage();
        }

        return res;
    }

    public String FetchCreditLimitFromServer() {

        boolean result = false;
        String res = "";
        try {
            HttpURLConnection con = (HttpURLConnection) (new URL("" + apiUrl + "/DBSync")).openConnection();
            con.setRequestMethod("POST");
            con.setDoInput(true);
            con.setDoOutput(true);
            con.connect();

            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("" + apiUrl + "/DBSync");

            List<NameValuePair> list = new ArrayList<NameValuePair>();
            list.add(new BasicNameValuePair("name", "creditlimit"));
           // list.add(new BasicNameValuePair("curr_date", todaydate));
            list.add(new BasicNameValuePair("curr_date", date_order));
            list.add(new BasicNameValuePair("c_bpartner_id", c_bpartner_id));
            list.add(new BasicNameValuePair("extraCond", extraCond));
            list.add(new BasicNameValuePair("daysdue",daysdue));

            httpPost.setEntity(new UrlEncodedFormEntity(list));
            HttpResponse httpResponse = httpClient.execute(httpPost);

            HttpEntity httpEntity = httpResponse.getEntity();
            String s = readResponse(httpResponse);
            System.out.println("Credit Limit Response****************" + s);

            if (s.isEmpty() || !s.startsWith("[")) {
                res = "Credit Limit Not Foundd";

                return res;
            }

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

            for (int i1 = 0; i1 < list4.size(); i1++) {
                Vector line = (Vector) list4.get(i1);
                dueAmt = ((String) line.get(0));
                total_dueAmt = (String) line.get(1);
                result = true;
            }

            if (result) {
                res = "Credit Limit Sync Successful";
            }
        } catch (Exception e) {
            res = e.getMessage();
        }
        return res;
    }

    public String FetchDiscountSchemaFromServer() {

        boolean result = false;
        String  discount_category ="" , m_discountschema_id ="" , discountschema_name ="" ,
                validfrom ="" ,validto = "",  discounttype= "",  isquantitybased= "" , ismixed= "",maxvalue= "",
                minvalue= "",discountschema_type = "", m_discountschemabreak_id= "",m_product_category_id="",
                IsBPartnerFlatDiscount= "",
                m_product_id= "", breakvalue = "", breakdiscount= "", freeqty= "",ispercentbased = "",isproductbased = "",res = "";
        try {
            HttpURLConnection con = (HttpURLConnection) (new URL("" + apiUrl + "/DBSync")).openConnection();
            con.setRequestMethod("POST");
            con.setDoInput(true);
            con.setDoOutput(true);
            con.connect();

            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("" + apiUrl + "/DBSync");

            List<NameValuePair> list = new ArrayList<NameValuePair>();
            list.add(new BasicNameValuePair("name", "discountschema"));
            list.add(new BasicNameValuePair("curr_date", date_order));
            list.add(new BasicNameValuePair("c_bp_group_id", String.valueOf(c_bp_group_id)));
            list.add(new BasicNameValuePair("iscash", cashpos));
            list.add(new BasicNameValuePair("ad_org_id", org_id));

            httpPost.setEntity(new UrlEncodedFormEntity(list));
            HttpResponse httpResponse = httpClient.execute(httpPost);

            HttpEntity httpEntity = httpResponse.getEntity();
            String s = readResponse(httpResponse);
            System.out.println("Discount Schema Response****************" + s);

            if (s.isEmpty() || !s.startsWith("[")) {
                res = "Discount Schema Not Foundd";

                return res;
            }

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

            for (int i1 = 0; i1 < list4.size(); i1++) {
                Vector line = (Vector) list4.get(i1);
                discount_category = ((String) line.get(0));
                m_discountschema_id = (String) line.get(1);
                discountschema_name = ((String) line.get(2));
                validfrom = ((String) line.get(3));
                validto = ((String) line.get(4));
                discounttype = ((String) line.get(5));
                isquantitybased = ((String) line.get(6));
                ismixed = ((String) line.get(7));
                maxvalue = ((String) line.get(8));
                minvalue = ((String) line.get(9));
                ispercentbased = ((String) line.get(10));
                m_discountschemabreak_id = ((String) line.get(11));
                m_product_id = ((String) line.get(12));
                breakvalue = ((String) line.get(13));
                breakdiscount = ((String) line.get(14));
                freeqty= ((String) line.get(15));
                isproductbased = ((String) line.get(16));
                m_product_category_id = ((String) line.get(17));
                IsBPartnerFlatDiscount = ((String) line.get(18));

                discountschema_id = m_discountschema_id;
                result = mDatabaseHelper.insertIntoDiscountSchema(discount_category, m_discountschema_id,discountschema_name, validfrom, validto,discounttype, isquantitybased, ismixed, maxvalue, minvalue,ispercentbased, m_discountschemabreak_id, m_product_id, breakvalue,breakdiscount, freeqty,isproductbased, m_product_category_id,IsBPartnerFlatDiscount);
            }

            if (result) {
                res = "Discount Schema Sync Successful";
            }
        } catch (Exception e) {
            res = e.getMessage();
        }
        return res;
    }
    private void getlistproduct() {
        Cursor cursorp = mDatabaseHelper.getProductOrderLineCategory(LoginActivity.ad_user_id);
        if (cursorp.moveToFirst()) {
            CategoryID = cursorp.getString(0);
            getProduct(CategoryID);
            Cursor cursor = mDatabaseHelper.getProductCategoryCursor(CategoryID);
            if (cursor.moveToFirst()) {
                categ_name = cursor.getString(1);
            }
            ProductCategorySpinner.setSelection(((ArrayAdapter) ProductCategorySpinner.getAdapter()).getPosition(categ_name));
        }
    }
    public void onClickDatePicker(View view) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        @SuppressLint("WrongViewCast") DatePickerDialog datePicker = new DatePickerDialog(findViewById(R.id.date_editText).getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                //   daynum = (TextView) getActivity().findViewById(R.id.daynum);
                // editText_dateTime.setText(df.format(view.getYear() + "-" + (view.getMonth() + 1) + "-" + view.getDayOfMonth()));
                //SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                String d2 = view.getYear() + "-" + (view.getMonth() + 1) + "-" + view.getDayOfMonth();
                c.set(Calendar.YEAR, year);
                c.set(Calendar.MONTH, (month));
                c.set(Calendar.DAY_OF_MONTH, day);
                String myFormat = "yyyy-MM-dd";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
                editText_dateTime.setText(sdf.format(c.getTime()));

                //dobEditText.setEnabled(false);
                //  new ReadJSON().execute("url" + "day=" + view.getDayOfMonth() + "&" + "month=" + (view.getMonth() + 1) + "&" + "year=" + view.getYear());
                //  new ReadJSON1().execute("url" + "day=" + view.getDayOfMonth() + "&" + "month=" + (view.getMonth() + 1) + "&" + "year=" + view.getYear());
            }
        }, day, month, year);
        //subtract -3000 to select date before current date
        // c.add(Calendar.YEAR, -2);
        datePicker.getDatePicker().setSpinnersShown(true);
        datePicker.getDatePicker().setCalendarViewShown(false);
        datePicker.getDatePicker().setMinDate(c.getTimeInMillis());
        c.add(Calendar.YEAR, 4);
        datePicker.getDatePicker().setMaxDate(c.getTimeInMillis());
        datePicker.setTitle("Select Date");
        datePicker.show();
    }


    private void getProductCategory(String BPTerrID) {
        Cursor cursor=null;

        try{
            cursor = mDatabaseHelper.getProductCategory(BPTerrID);
            if(cursor.moveToFirst()){
            do{
                String   id   = cursor.getString(0);
                String   name   = cursor.getString(1);
                productCategoryArrayList.add(name);
                hashProductID.put(name,id);

            }while(cursor.moveToNext());
           }
        }
        finally {
            if (cursor != null)
                cursor.close();  // RIGHT: ensure resource is always recovered
        }
        loadProductCategorySpinner();

    }
    private void getProduct()
    {
        if(!productArrayList.isEmpty())
        {
            productArrayList.clear();
        }
        Cursor cursor=null;
        try{
            cursor = mDatabaseHelper.getProductCursor(selectedProductCategoryID);

            if(cursor.moveToFirst()){
                do{
                    String   id   = cursor.getString(0);
                    String   name   = cursor.getString(1);
                    //String costcenter          = cursor.getString(7);
                    productArrayList.add(name);
                    hashProdID.put(name,id);
                    Log.d("DEBUG","prroductArrayList = "+name);

                }while(cursor.moveToNext());
            }
        }
        finally {
            if (cursor != null)
                cursor.close();  // RIGHT: ensure resource is always recovered
        }
        loadProductSpinner();

    }
    private void getProduct(String CategoryID) {
        if(!productArrayList.isEmpty()) {
            productArrayList.clear();
        }
        Cursor cursor=null;
        try{
            cursor = mDatabaseHelper.getProductCursor(CategoryID);

            if(cursor.moveToFirst()){
                do{
                    String   id   = cursor.getString(0);
                    String   name   = cursor.getString(1);
                    //String costcenter          = cursor.getString(7);
                    productArrayList.add(name);
                    hashProdID.put(name,id);
                    Log.d("DEBUG","prroductArrayList = "+name);

                }while(cursor.moveToNext());
            }
        }
        finally {
            if (cursor != null)
                cursor.close();  // RIGHT: ensure resource is always recovered
        }
        loadProductSpinner();

    }

    private void loadProductCategorySpinner() {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, productCategoryArrayList);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        ProductCategorySpinner.setAdapter(dataAdapter);
        dataAdapter.notifyDataSetChanged();
        selectProductCategorySpinner();
    }
    private void loadProductSpinner() {

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, productArrayList);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        ProductSpinner.setAdapter(dataAdapter);
        dataAdapter.notifyDataSetChanged();
        selectProductSpinner();
    }

    private void onPriceEditTextFocusChange(){
        price_editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (selectedProductVolume.equals("0")) {
                    if (!price_editText.getText().toString().equals("") && !qtyinpacket_editText.getText().toString().equals("")) {
                        if (!price_editText.getText().toString().equals(".") && !qtyinpacket_editText.getText().toString().equals(".")) {
                            BigDecimal price = new BigDecimal(price_editText.getText().toString());
                            BigDecimal quantity = new BigDecimal(qtyinpacket_editText.getText().toString());
                            BigDecimal total_price = price.multiply(quantity);
                            quantity_editText.setText(String.valueOf(quantity));
                            if(selectedProductUOM.equals("GRAM")){
                                qtyinkg.setText(String.valueOf(quantity.divide(new BigDecimal(1000))));
                            }else {
                                qtyinkg.setText(String.valueOf(quantity));
                            }
                            total_editText.setText(String.valueOf(total_price.setScale(5,2)));
                        }
                    } else {
                    //    quantity_editText.setText("");
                        total_editText.setText("");
                    }
                }
                else{
                    if (!price_editText.getText().toString().equals("") && !qtyinpacket_editText.getText().toString().equals("")) {
                        if (!price_editText.getText().toString().equals(".") && !qtyinpacket_editText.getText().toString().equals(".")) {
                            BigDecimal volume = new BigDecimal(selectedProductVolume);
                            BigDecimal price = new BigDecimal(price_editText.getText().toString());
                            BigDecimal quantity = new BigDecimal(qtyinpacket_editText.getText().toString());
                            BigDecimal quantity1 =quantity.multiply(volume);
                            BigDecimal total_price = price.multiply(quantity1);
                            quantity_editText.setText(String.valueOf(quantity1));
                            if(selectedProductUOM.equals("GRAM")){
                                qtyinkg.setText(String.valueOf(quantity1.divide(new BigDecimal(1000))));
                            }else {
                                qtyinkg.setText(String.valueOf(quantity1));
                            }
                            total_editText.setText(String.valueOf(total_price.setScale(5,2)));
                        }
                    } else {
                        total_editText.setText("");
                    //    quantity_editText.setText("");
                    }
                }}

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
    private void onQuantityEditTextFocusChange(){
        qtyinpacket_editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (selectedProductVolume.equals("0")) {
                    if (!price_editText.getText().toString().equals("") && !qtyinpacket_editText.getText().toString().equals("")) {
                        if (!price_editText.getText().toString().equals(".") && !qtyinpacket_editText.getText().toString().equals(".")) {
                            //double price = Double.parseDouble(price_editText.getText().toString());
                            // double quantity = Double.parseDouble(qtyinpacket_editText.getText().toString());
                            //double total_price = price * quantity;
                            BigDecimal price = new BigDecimal(price_editText.getText().toString());
                            BigDecimal quantity = new BigDecimal(qtyinpacket_editText.getText().toString());
                            BigDecimal total_price = price.multiply(quantity);
                            quantity_editText.setText(String.valueOf(quantity));
                            if(selectedProductUOM.equals("GRAM")){
                                qtyinkg.setText(String.valueOf(quantity.divide(new BigDecimal(1000))));
                            }else {
                                //qtyinkg.setText(String.valueOf(quantity.divide(new BigDecimal(1000))));
                                qtyinkg.setText(String.valueOf(quantity));
                            }
                            total_editText.setText(String.valueOf(total_price.setScale(5,2)));

                        }
                    } else {
                        quantity_editText.setText("");
                        total_editText.setText("");
                    }
                }

                //2023-01-29 volume part added
                else{
                    if (!price_editText.getText().toString().equals("") && !qtyinpacket_editText.getText().toString().equals("")) {
                        if (!price_editText.getText().toString().equals(".") && !qtyinpacket_editText.getText().toString().equals(".")) {
                            BigDecimal volume = new BigDecimal(selectedProductVolume);
                            BigDecimal price = new BigDecimal(price_editText.getText().toString());
                            BigDecimal quantity = new BigDecimal(qtyinpacket_editText.getText().toString());
                            BigDecimal quantity1 =quantity.multiply(volume);
                            BigDecimal total_price = price.multiply(quantity1);
                            quantity_editText.setText(String.valueOf(quantity1));
                            if(selectedProductUOM.equals("GRAM")){
                                qtyinkg.setText(String.valueOf(quantity1.divide(new BigDecimal(1000))));
                            }else {
                                qtyinkg.setText(String.valueOf(quantity1));
                            }
                          //  qtyinkg.setText(String.valueOf(quantity1.divide(new BigDecimal(1000))));
                            total_editText.setText(String.valueOf(total_price.setScale(5,2)));
                        }
                    } else {
                        total_editText.setText("");
                        quantity_editText.setText("");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        price_editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!qtyinpacket_editText.getText().toString().equals("") && !price_editText.getText().toString().equals("")){
                    if(!qtyinpacket_editText.getText().toString().equals(".") && !price_editText.getText().toString().equals(".")) {
                        //double price = Double.parseDouble(price_editText.getText().toString());
                        //double quantity = Double.parseDouble(qtyinpacket_editText.getText().toString());
                        BigDecimal price = new BigDecimal(price_editText.getText().toString());
                        BigDecimal quantity = new BigDecimal(qtyinpacket_editText.getText().toString());
                        BigDecimal total_price = price.multiply(quantity);
                        //total_price1 = BigDecimal.valueOf(total_price); //2021-11-17  total_price1 added by shanzida
                        //total_price1=new BigDecimal("406770000244E+12").toBigInteger()
                        total_editText.setText(String.valueOf(total_price));
                        //total_editText.setText(String.valueOf(total_price));
                    }
                }else{
                    total_editText.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

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

    public void callConfirm(View view) {
        toast("Confirm Order Clicked ");
    }
    void toast(String msg){
        Toast.makeText(Sales_Order_Activity1.this,msg,Toast.LENGTH_SHORT).show();
    }
    public void ShowAlert(String title,String msg){


        androidx.appcompat.app.AlertDialog.Builder dlgAlert  = new androidx.appcompat.app.AlertDialog.Builder(Sales_Order_Activity1.this);

        dlgAlert.setMessage(msg);
        dlgAlert.setTitle(title);
        dlgAlert.setIcon(R.drawable.logo);
        dlgAlert.setPositiveButton("OK", null);
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
    }
    private void selectProductCategorySpinner(){
        ProductCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    String name = parent.getSelectedItem().toString();
                    //selectedProductID = hashProductID.get(name);
                    selectedProductCategoryID=hashProductID.get(name);
                    if(!CategoryID.equals("")) {
                        if (!CategoryID.equals(selectedProductCategoryID)) {
                            cancelOrderlinealert();
                        }
                    }
                    qtyinpacket_editText.setText("");
                    qtyinkg.setText("");
                    quantity_editText.setText("");

                    AsyncProduct asyncProduct=new AsyncProduct();
                    asyncProduct.execute();

                }catch (NullPointerException ex){

                }catch (ArrayIndexOutOfBoundsException ex){

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    private void selectProductSpinner(){
        ProductSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    String name = parent.getSelectedItem().toString();
                    selectedProductPrice = priceHash.get(name);
                    selectedProductSearchkey=searchkeyHash.get(name);
                    selectedProductCostCenter=costcenterHash.get(name);
                    selectedelementval = elementvalHash.get(selectedProductCostCenter);
                    selectedProductVolume=volumeHash.get(name);
                    selectedProdId=hashProdID.get(name);
                    selectedProduct=name;
                    selectedProductUOM=hashUOM.get(name);
                    CostCenterText.setText(selectedProductCostCenter);
                    unitOfMeasure.setText(selectedProductUOM);
                    price_editText.setText(selectedProductPrice);
                    qtyinpacket_editText.setText("");
                    qtyinkg.setText("");
                    quantity_editText.setText("");

                    if(salestype.equals("Corporate Sales Order") || salestype.equals("Sample Seed Sale") || salestype.equals("Compensation Sales Order") || salestype.equals("Special Price Sales Order")){
                        price_editText.setEnabled(true);
                    }else{
                        price_editText.setEnabled(false);
                    }
                    quantity_editText.setEnabled(false);
                }catch (NullPointerException ex){

                }catch (ArrayIndexOutOfBoundsException ex){

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    public void cancelOrderlinealert(){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(Sales_Order_Activity1.this);
        builder1.setMessage("Are you sure you want to Delete All Order Line?");
        builder1.setCancelable(false);

        builder1.setPositiveButton(
                "OK",  //"YES
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        mDatabaseHelper.deleteAllProductOrderLine();
                        CostCenterText.setText("");
                        unitOfMeasure.setText("");
                        price_editText.setText("");
                    }
                });

        // commented by anika on 2024-09-04
//        builder1.setNegativeButton(
//                "No",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        dialog.cancel();
//                    }
//                });
        // commented by anika on 2024-09-04

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }
    private String  FetchProductsFromServer2() {
        mDatabaseHelper.deleteProducts();
        int catagoryID;
        boolean result = false;

        String name, productID, price, res = "";
        try {
            priceHash.clear();
            hashUOM.clear();
            searchkeyHash.clear();
            volumeHash.clear();
            HttpURLConnection con = (HttpURLConnection) ( new URL(""+apiUrl+"/DBSync")).openConnection();
            con.setRequestMethod("POST");
            con.setDoInput(true);
            con.setDoOutput(true);
            con.connect();

            HttpClient httpClient=new DefaultHttpClient();
            HttpPost httpPost=new HttpPost(""+apiUrl+"/DBSync");

            List<NameValuePair> list=new ArrayList<NameValuePair>();
            list.add(new BasicNameValuePair("name", "allproduct"));
            list.add(new BasicNameValuePair("pro_cat_id", selectedProductCategoryID));
            list.add(new BasicNameValuePair("M_PriceList_ID", selectedPriceListId));

            httpPost.setEntity(new UrlEncodedFormEntity(list));
            HttpResponse httpResponse=	httpClient.execute(httpPost);

            HttpEntity httpEntity=httpResponse.getEntity();
            String s= readResponse(httpResponse);

            if (s.isEmpty() || !s.startsWith("[")){
                res="Product Not Foundd";
                return res;
            }

            ArrayList list4=new ArrayList();

            String[] parts = s.split("]_");
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
                String unitprice=(String)line.get(3);
                String uom_id=(String)line.get(4);
                String searchkey=(String)line.get(5);
                String costcenter=(String)line.get(6);
                String volume=(String)line.get(7);
                String c_elementvalue_id = (String)line.get(8);

                System.out.println("Product id"+productID);
                priceHash.put(name,unitprice);
                hashUOM.put(name,uom_id);
                searchkeyHash.put(name,searchkey);
                costcenterHash.put(name,costcenter);
                elementvalHash.put(costcenter, c_elementvalue_id);
                volumeHash.put(name,volume);

                Log.d("DEBUG","FROM SERVER || ProductName="+name+" PID="+productID+" p_catId="+catagoryID+"price="+unitprice+"UOM="+uom_id+"Search key="+searchkey);
                result = mDatabaseHelper.insertIntoProduct(productID, name, catagoryID, unitprice,uom_id,searchkey,"",costcenter, c_elementvalue_id);
            }

            if (result) {
                res = "Product Sync Successful";
            }
        } catch (Exception e) {
            res = e.getMessage();
        }
        return res;
    }
    private final class AsyncProduct extends AsyncTask<String, String, String> {
        ProgressDialog pd;
        @Override
        protected String doInBackground(String... params) {
            String result = "", error = "";
            try {
                result =FetchProductsFromServer2();
            } catch (Exception e) {
                error = e.getMessage();
                Log.d("DEBUG",error);
                e.printStackTrace();
                return error;
            }
            return result;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pd = new ProgressDialog(Sales_Order_Activity1.this);
            pd.setTitle("Please Wait ");
            pd.setMessage("Loading Product ........");
            pd.setCancelable(false);
            pd.setIndeterminate(true);
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pd.show();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pd.dismiss();

            if (result.equals("Product Sync Successful")) {
                getProduct();
            } else {
                toastMessage(result);
                mDatabaseHelper.deleteProducts();
                getProduct();
                price_editText.setText("");
                Log.d("DEBUG",result);
            }
        }
    }

    public void callShowAddProduct(View view){
        boolean result=false;
        Cursor cursor1 = null;
        try {
            cursor1 = mDatabaseHelper.getCustomerCreditLimit(selectedBPCustomerID, selectedelementval);
            if (cursor1.moveToFirst()) {
                do {
                    c_bpartner_id = cursor1.getString(1);
                    c_bp_group_id = cursor1.getString(8);
                    costcenter_id = cursor1.getString(9);
                    creditlimitval = cursor1.getString(10);
                    daysdue = cursor1.getString(11);
                    t_bpcreditlimit_id = cursor1.getString(12);
                    validfrom = cursor1.getString(13);
                    validto = cursor1.getString(14);
                    ad_org_id = cursor1.getString(15);
                } while (cursor1.moveToNext());

                if(Integer.parseInt(costcenter_id) > 0 ){
                    extraCond=	"and inv.user1_ID="+costcenter_id+"\n" ;
                }

                if(Integer.parseInt(ad_org_id) > 0){
                    extraCond=	extraCond+"	and inv.AD_Org_ID="+ad_org_id+"\n" ;
                }

                AsyncCreditLimitAgain task = new AsyncCreditLimitAgain();
                task.execute();
            } else{

                Cursor cursor = mDatabaseHelper.getProductOrderLine(LoginActivity.ad_user_id);
                if (cursor.moveToFirst()) {
                    Intent i=new Intent(Sales_Order_Activity1.this, OrderListActivity_json.class);
                    i.putExtra("DOCNO",documentNo);
                    i.putExtra("CUSTOMERNAMECODE",customerName_Code);
                    i.putExtra("selectedBPCustomerID", selectedBPCustomerID);
                    i.putExtra("SHOWDATE", showdateNew);
                    i.putExtra("date_ordered", date_order);
                    i.putExtra("c_bp_group_id", c_bp_group_id);
                    i.putExtra("iscash", iscash);
                    i.putExtra("salesType", salestype);  //anika 2023-08-24
                    i.putExtra("total_dueAmt", total_dueAmt); //anika 2023-10-04
                    i.putExtra("creditlimitval", creditlimitval); //anika 2023-10-04
                    i.putExtra("cashpos", cashpos);  // Anika 2023-10-10
                    i.putExtra("ad_org_id", org_id);  // Anika 2023-10-10
                    i.putExtra("selectedBPTerrID",selectedBPTerrID ); // Anika 2026-07-29
                    startActivity(i);

                } else {
                    toast("No Product is Added");
                }

            }
        } finally {
            if (cursor1 != null)
                cursor1.close();
        }

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent i=new Intent(Sales_Order_Activity1.this,Customer_Selection_Activity.class);
        startActivity(i);
    }
    public void callCancelLogOut(View view) {
        cancelalert();
    }

    public void cancelalert(){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(Sales_Order_Activity1.this);
        builder1.setMessage("Are you sure you want to LogOut?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        Intent i=new Intent(Sales_Order_Activity1.this,LoginActivity.class);
                        startActivity(i);
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
    public void callConfirmNew(View view) {
        callAddProduct();
    }

    public void callCancel(View view) {
        Intent i=new Intent(Sales_Order_Activity1.this,Customer_Selection_Activity.class);
        startActivity(i);
    }

    //Credit Limit is added by Anika on 2023-10-04
    public void callAddProduct( ) {
        Cursor cursor = null;
        try {
            cursor = mDatabaseHelper.getCustomerCreditLimit(selectedBPCustomerID, selectedelementval);
            if (cursor.moveToFirst()) {
                do {
                    c_bpartner_id = cursor.getString(1);
                    String customer_name = cursor.getString(2);
                    String bpaddress = cursor.getString(4);
                    String bpphone = cursor.getString(5);
                    String so_creditlimit = cursor.getString(7);
                    c_bp_group_id = cursor.getString(8);
                    costcenter_id = cursor.getString(9);
                    creditlimitval = cursor.getString(10);
                    daysdue = cursor.getString(11);
                    t_bpcreditlimit_id = cursor.getString(12);
                    validfrom = cursor.getString(13);
                    validto = cursor.getString(14);
                    ad_org_id = cursor.getString(15);

                } while (cursor.moveToNext());

                if(Integer.parseInt(costcenter_id) > 0 ){
                    extraCond=	"and inv.user1_ID="+costcenter_id+"\n" ;
                }

                if(Integer.parseInt(ad_org_id) > 0){
                    extraCond=	extraCond+"	and inv.AD_Org_ID="+ad_org_id+"\n" ;
                }

                AsyncCreditLimit task = new AsyncCreditLimit();
                task.execute();
            }else{
                BigDecimal discount = new BigDecimal(0);
                int discountCombinationforproduct,discountCombinationforproductcat,gift=0;

                if (TextUtils.isEmpty(qtyinpacket_editText.getText().toString()) || qtyinpacket_editText.getText().toString().equals("")) {
                    qtyinpacket_editText.requestFocus();
                    qtyinpacket_editText.setError("Please Enter Quantity ");
                    return;
                }

                if (qtyinpacket_editText.getText().toString().equals("0") || qtyinpacket_editText.getText().toString().equals("")){
                    qtyinpacket_editText.requestFocus();
                    qtyinpacket_editText.setError("Please Enter Quantity !!");
                    return;
                }

                SharedPreferences sh = getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
                int id =sh.getInt("NUM",0);
                int id1=id+1;
                String quantity="0",quantity1="0";
                double qty=0.00;
                quantity1=(qtyinpacket_editText.getText().toString());
                quantity=quantity_editText.getText().toString();
                BigDecimal price2 = new BigDecimal(price_editText.getText().toString());
                // BigDecimal quantity2 = new BigDecimal(quantity_editText.getText().toString());
                BigDecimal quantity2 = new BigDecimal(quantity);  //anika 2023-06-26
                BigDecimal total_price2 = price2.multiply(quantity2);

                if (ProductCategorySpinner.getCount()==0){
                    toastMessage("No Product Category Selected !!");
                    return;
                }

                if (ProductSpinner.getCount()==0){
                    toastMessage("No Product Selected !!");
                    return;
                }

                //discount schema added by anika on 2023-06-11
                discountCombinationforproduct = getProductDiscount(selectedProdId);

                switch (discountCombinationforproduct) {

                    case 100:
                        System.out.println(selectedProdId + "!!!!!!!!!!!!!!!!!!!!!" + quantity);
                        String breakdiscount;
                        Cursor disbreak;

                        disbreak = mDatabaseHelper.getAllDiscountSchemaforproduct(selectedProdId);
                        if (disbreak.moveToFirst()) {
                            do {
                                String m_discountschema_id = disbreak.getString(2);
                                String ispercentbased = disbreak.getString(11);
                                String m_discountschemabreak_id = disbreak.getString(12);
                                String m_product_id = disbreak.getString(13);
                                breakdiscount = disbreak.getString(15);

                                if (Integer.parseInt(m_product_id) > 0) {
                                    if (selectedProdId.equals(m_product_id)) {
                                        discount = (total_price2.multiply(new BigDecimal(breakdiscount))).divide(new BigDecimal(100.00));
                                        discount = discount.setScale(2, BigDecimal.ROUND_HALF_EVEN);
                                        if (discountschemaID.equals("")) {
                                            discountschemaID = discountschemaID.concat(m_discountschema_id);
                                        } else {
                                            discountschemaID = discountschemaID.concat("," + m_discountschema_id);
                                        }
                                        if (discountschemabreakID.equals("")) {
                                            discountschemabreakID = discountschemabreakID.concat(m_discountschemabreak_id);
                                        } else {
                                            discountschemabreakID = discountschemabreakID.concat("," + m_discountschemabreak_id);
                                        }
                                        break;
                                    } else {
                                        System.out.println("I dont know");
                                    }
                                }
                            } while (disbreak.moveToNext());
                        }
                        break;
                    case 110:
                        System.out.println(selectedProdId + "!!!!!!!!!!!!!!!!!!!!!" + quantity);
                        String bdiscount;
                        Double discountqty;
                        Cursor dbreak = mDatabaseHelper.getAllDiscountSchemaforproduct(selectedProdId);

                        if (dbreak.moveToFirst()) {
                            do {
                                String m_discountschema_id = dbreak.getString(2);
                                String m_discountschemabreak_id = dbreak.getString(12);
                                String ispercentbased = dbreak.getString(11);
                                String m_product_id = dbreak.getString(13);
                                String breakvalue = dbreak.getString(14);
                                bdiscount = dbreak.getString(15);
                                String freeqty = dbreak.getString(16);
                                String isproductbased = dbreak.getString(17);

                                if (selectedProdId.equals(m_product_id)) {
                                    int newbreakval = 0, newquantity = 0;

                                    if (isproductbased.equals("Y")) {
                                        // added on 2024-09-04 by Anika
                                        if(breakvalue.contains(".")){
                                            newbreakval = (int)Double.parseDouble(breakvalue);
                                        }else{
                                            newbreakval = Integer.parseInt(breakvalue);
                                        }

                                        if(quantity.contains(".")) {
                                            newquantity = (int)Double.parseDouble(quantity);
                                        }else{
                                            newquantity = Integer.parseInt(quantity);
                                        }
                                        // added on 2024-09-04 by Anika
                                        if (newquantity >= newbreakval) {    //Integer.parseInt(quantity) >= Integer.parseInt(breakvalue)
                                            gift = newquantity / newbreakval;    //Integer.parseInt(quantity) / Integer.parseInt(breakvalue)
                                            gift = gift * Integer.parseInt(freeqty);
                                            System.out.println("Ureka! Found It ===========" + gift);
                                            if (discountschemaID.equals("")) {
                                                discountschemaID = discountschemaID.concat(m_discountschema_id);
                                            } else {
                                                discountschemaID = discountschemaID.concat("," + m_discountschema_id);
                                            }
                                            if (discountschemabreakID.equals("")) {
                                                discountschemabreakID = discountschemabreakID.concat(m_discountschemabreak_id);
                                            } else {
                                                discountschemabreakID = discountschemabreakID.concat("," + m_discountschemabreak_id);
                                            }
                                            //  CreateAlert(m_product_id, gift);
                                            break;
                                        }
                                    }
                                }
                            } while (dbreak.moveToNext());
                        }
                        break;
                    default:
                }

                discountCombinationforproductcat = getProductCategoryDiscount(selectedProductCategoryID);

                switch (discountCombinationforproductcat) {

                    case 100:
                        String bdiscount;
                        Double discountqty;
                        Cursor dbreak = mDatabaseHelper.getAllDiscountSchemaforproductcat(selectedProductCategoryID);

                        if (dbreak.moveToFirst()) {
                            do {
                                String m_discountschema_id = dbreak.getString(2);
                                String m_discountschemabreak_id = dbreak.getString(12);
                                String ispercentbased = dbreak.getString(11);
                                String m_product_id = dbreak.getString(13);
                                String breakvalue = dbreak.getString(14);
                                bdiscount = dbreak.getString(15);
                                String freeqty = dbreak.getString(16);
                                String isproductbased = dbreak.getString(17);
                                String m_product_category_id = dbreak.getString(18);

                                if (selectedProductCategoryID.equals(m_product_category_id)) {
                                    if (ispercentbased.equals("Y")) {
                                        discount = (total_price2.multiply(new BigDecimal(bdiscount))).divide(new BigDecimal(100.00));
                                        discount = discount.setScale(2, BigDecimal.ROUND_HALF_EVEN);
                                        if (discountschemaID.equals("")) {
                                            discountschemaID = discountschemaID.concat(m_discountschema_id);
                                        } else {
                                            discountschemaID = discountschemaID.concat("," + m_discountschema_id);
                                        }
                                        if (discountschemabreakID.equals("")) {
                                            discountschemabreakID = discountschemabreakID.concat(m_discountschemabreak_id);
                                        } else {
                                            discountschemabreakID = discountschemabreakID.concat("," + m_discountschemabreak_id);
                                        }
                                        break;
                                    } else {
                                        System.out.println("I dont know");
                                    }
                                }

                            } while (dbreak.moveToNext());
                        } else {
                            System.out.println("Didn't found the product");
                        }

                        break;
                    default:
                }

                Cursor cursor1 = mDatabaseHelper.getProductOrderLineProduct(LoginActivity.ad_user_id, selectedProductCategoryID);

                if (!cursor1.moveToFirst()) {

                    boolean result = false;

                    qty = Double.parseDouble(quantity);
                    result = mDatabaseHelper.insertIntoProductOrderLine(id1, "1000911", selectedProduct, qty, price2.doubleValue(), total_price2.doubleValue(), documentNo, selectedProdId, LoginActivity.ad_user_id, selectedProductCategoryID, unitOfMeasure.getText().toString(), quantity1, discount.doubleValue(), String.valueOf(gift), discountschemaID, discountschemabreakID); //,productorderhead_Id);

                    if (result) {
                        //toast("Product Inserted");
                        SharedPreferences.Editor editor = sh.edit();
                        editor.putInt("NUM", id1);
                        editor.commit();

                        Intent i = new Intent(Sales_Order_Activity1.this, OrderListActivity_json.class);
                        i.putExtra("DOCNO", documentNo);
                        i.putExtra("CUSTOMERNAMECODE", customerName_Code);
                        i.putExtra("selectedBPCustomerID", selectedBPCustomerID);
                        i.putExtra("SHOWDATE", showdateNew);
                        i.putExtra("date_ordered", date_order);
                        i.putExtra("c_bp_group_id", c_bp_group_id);
                        i.putExtra("iscash", iscash);
                        i.putExtra("salesType", salestype); //anika 2023-08-24
                        i.putExtra("total_dueAmt", total_dueAmt); //anika 2023-10-04
                        i.putExtra("creditlimitval", creditlimitval); //anika 2023-10-04
                        i.putExtra("cashpos", cashpos);  // Anika 2023-10-10
                        i.putExtra("ad_org_id", org_id);  // Anika 2023-10-10
                        i.putExtra("selectedBPTerrID",selectedBPTerrID ); // Anika 2026-07-29
                        startActivity(i);

                    } else {
                        toast("Could not insert into internal database");
                    }
                } else {
                    ShowAlert("Warning", "This Product is already added.Please Add or Remove Qty from Product list");
                }

            }

        } finally {
            if (cursor != null)
                cursor.close();  // RIGHT: ensure resource is always recovered
        }


    }

    private final class AsyncCreditLimitAgain extends AsyncTask<String, String, String> {
        ProgressDialog pd;

        @Override
        protected String doInBackground(String... params) {
            String result = "", error = "";
            try {
                result = creditLimitAgain();
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
            pd = new ProgressDialog(Sales_Order_Activity1.this);
            pd.setTitle("Please Wait ");
            pd.setMessage("Checking Credit Limits ........");
            pd.setCancelable(false);
            pd.setIndeterminate(true);
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pd.show();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pd.dismiss();

            if (result.equals("Credit Limit Sync Successful")) {
                //    toastMessage(dueAmt +"----"+ total_dueAmt);
                overDueAmt = new BigDecimal(dueAmt);
                Cursor cursor = mDatabaseHelper.getProductOrderLine(LoginActivity.ad_user_id);
                if (cursor.moveToFirst()) {
                    Intent i=new Intent(Sales_Order_Activity1.this, OrderListActivity_json.class);
                    i.putExtra("DOCNO",documentNo);
                    i.putExtra("CUSTOMERNAMECODE",customerName_Code);
                    i.putExtra("selectedBPCustomerID", selectedBPCustomerID);
                    i.putExtra("SHOWDATE", showdateNew);
                    i.putExtra("date_ordered", date_order);
                    i.putExtra("c_bp_group_id", c_bp_group_id);
                    i.putExtra("iscash", iscash);
                    i.putExtra("salesType", salestype);  //anika 2023-08-24
                    i.putExtra("total_dueAmt", total_dueAmt); //anika 2023-10-04
                    i.putExtra("creditlimitval", creditlimitval); //anika 2023-10-04
                    i.putExtra("cashpos", cashpos);  // Anika 2023-10-10
                    i.putExtra("ad_org_id", org_id);  // Anika 2023-10-10
                    i.putExtra("selectedBPTerrID",selectedBPTerrID ); // Anika 2026-07-29
                    startActivity(i);

                } else {
                    toast("No Product is Added");
                }
            } else if(result.startsWith("Failed to connect to")){
                ShowAlert("Error","Please check your net connection");
            }else{
                Cursor cursor = mDatabaseHelper.getProductOrderLine(LoginActivity.ad_user_id);
                if (cursor.moveToFirst()) {
                    Intent i=new Intent(Sales_Order_Activity1.this, OrderListActivity_json.class);
                    i.putExtra("DOCNO",documentNo);
                    i.putExtra("CUSTOMERNAMECODE",customerName_Code);
                    i.putExtra("selectedBPCustomerID", selectedBPCustomerID);
                    i.putExtra("SHOWDATE", showdateNew);
                    i.putExtra("date_ordered", date_order);
                    i.putExtra("c_bp_group_id", c_bp_group_id);
                    i.putExtra("iscash", iscash);
                    i.putExtra("salesType", salestype);  //anika 2023-08-24
                    i.putExtra("total_dueAmt", total_dueAmt); //anika 2023-10-04
                    i.putExtra("creditlimitval", creditlimitval); //anika 2023-10-04
                    i.putExtra("cashpos", cashpos);  // Anika 2023-10-10
                    i.putExtra("ad_org_id", org_id);  // Anika 2023-10-10
                    i.putExtra("selectedBPTerrID",selectedBPTerrID ); // Anika 2026-07-29
                    startActivity(i);

                } else {
                    toast("No Product is Added");
                }
            }
        }
    }

    private final class AsyncCreditLimit extends AsyncTask<String, String, String> {
        ProgressDialog pd;

        @Override
        protected String doInBackground(String... params) {
            String result = "", error = "";
            try {
                result = creditLimit();
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
            pd = new ProgressDialog(Sales_Order_Activity1.this);
            pd.setTitle("Please Wait ");
            pd.setMessage("Checking Credit Limits ........");
            pd.setCancelable(false);
            pd.setIndeterminate(true);
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pd.show();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pd.dismiss();

            if (result.equals("Credit Limit Sync Successful")) {
            //    toastMessage(dueAmt +"----"+ total_dueAmt);
                overDueAmt = new BigDecimal(dueAmt);
                TrialFunction();
            } else if(result.startsWith("Failed to connect to")){
                ShowAlert("Error","Please check your net connection");
            }else{
                ShowAlert("Error", "There are some problem");
            }
        }
    }
    public void TrialFunction(){
        if(Integer.parseInt(daysdue) > 0){
            BigDecimal discount = new BigDecimal(0);
            int discountCombinationforproduct,discountCombinationforproductcat;
            int gift=0;

            if (TextUtils.isEmpty(qtyinpacket_editText.getText().toString()) || qtyinpacket_editText.getText().toString().equals("")) {
                qtyinpacket_editText.requestFocus();
                qtyinpacket_editText.setError("Please Enter Quantity ");
                return;
            }

            if (qtyinpacket_editText.getText().toString().equals("0") || qtyinpacket_editText.getText().toString().equals("")){
                qtyinpacket_editText.requestFocus();
                qtyinpacket_editText.setError("Please Enter Quantity !!");
                return;
            }

            SharedPreferences sh = getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
            int id =sh.getInt("NUM",0);
            int id1=id+1;
            String quantity="0",quantity1="0";
            double qty=0.00;
            quantity1=(qtyinpacket_editText.getText().toString());
            quantity=quantity_editText.getText().toString();
            BigDecimal price2 = new BigDecimal(price_editText.getText().toString());
            // BigDecimal quantity2 = new BigDecimal(quantity_editText.getText().toString());
            BigDecimal quantity2 = new BigDecimal(quantity);  //anika 2023-06-26
            BigDecimal total_price2 = price2.multiply(quantity2);

            if (ProductCategorySpinner.getCount()==0){
                toastMessage("No Product Category Selected !!");
                return;
            }

            if (ProductSpinner.getCount()==0){
                toastMessage("No Product Selected !!");
                return;
            }



            if(overDueAmt.compareTo(BigDecimal.valueOf(0.0)) > 0){
                ShowAlert("OverDue Amount","Overdue Amount Not Allowed More Than "+daysdue+" Days.\nOverdue Amt : "+overDueAmt);
            }else {

            //discount schema added by anika on 2023-06-11
            discountCombinationforproduct = getProductDiscount(selectedProdId);

            Boolean gotdiscount = false;

            switch (discountCombinationforproduct) {

                case 100:
                    System.out.println(selectedProdId + "!!!!!!!!!!!!!!!!!!!!!" + quantity);
                    String breakdiscount;
                    Cursor disbreak;

                    disbreak = mDatabaseHelper.getAllDiscountSchemaforproduct(selectedProdId);
                    if (disbreak.moveToFirst()) {
                        do {
                            String m_discountschema_id = disbreak.getString(2);
                            String ispercentbased = disbreak.getString(11);
                            String m_discountschemabreak_id = disbreak.getString(12);
                            String m_product_id = disbreak.getString(13);
                            breakdiscount = disbreak.getString(15);

                            if (Integer.parseInt(m_product_id) > 0) {
                                if (selectedProdId.equals(m_product_id)) {
                                    discount = (total_price2.multiply(new BigDecimal(breakdiscount))).divide(new BigDecimal(100.00));
                                    discount = discount.setScale(2, BigDecimal.ROUND_HALF_EVEN);
                                    if (discountschemaID.equals("")) {
                                        discountschemaID = discountschemaID.concat(m_discountschema_id);
                                    } else {
                                        discountschemaID = discountschemaID.concat("," + m_discountschema_id);
                                    }
                                    if (discountschemabreakID.equals("")) {
                                        discountschemabreakID = discountschemabreakID.concat(m_discountschemabreak_id);
                                    } else {
                                        discountschemabreakID = discountschemabreakID.concat("," + m_discountschemabreak_id);
                                    }
                                    gotdiscount = true;
                                    break;
                                } else {
                                    System.out.println("I dont know");
                                }
                            }
                        } while (disbreak.moveToNext());
                    }
                    break;
                case 110:
                    System.out.println(selectedProdId + "!!!!!!!!!!!!!!!!!!!!!" + quantity);
                    String bdiscount;
                    Double discountqty;


                    Cursor dbreak = mDatabaseHelper.getAllDiscountSchemaforproduct(selectedProdId);

                    if (dbreak.moveToFirst()) {
                        do {
                            String m_discountschema_id = dbreak.getString(2);
                            String m_discountschemabreak_id = dbreak.getString(12);
                            String ispercentbased = dbreak.getString(11);
                            String m_product_id = dbreak.getString(13);
                            String breakvalue = dbreak.getString(14);
                            bdiscount = dbreak.getString(15);
                            String freeqty = dbreak.getString(16);
                            String isproductbased = dbreak.getString(17);

                            if (selectedProdId.equals(m_product_id)) {
                                int newbreakval = 0, newquantity = 0;

                                if (isproductbased.equals("Y")) {
                                    // added on 2024-09-04 by Anika
                                    if (breakvalue.contains(".")) {
                                        newbreakval = (int) Double.parseDouble(breakvalue);
                                    } else {
                                        newbreakval = Integer.parseInt(breakvalue);
                                    }

                                    if (quantity.contains(".")) {
                                        newquantity = (int) Double.parseDouble(quantity);
                                    } else {
                                        newquantity = Integer.parseInt(quantity);
                                    }
                                    // added on 2024-09-04 by Anika

                                    if (newquantity >= newbreakval) {      //Integer.parseInt(quantity)   >=  Integer.parseInt(breakvalue)
                                        gift = newquantity / newbreakval;      //Integer.parseInt(quantity)  /  Integer.parseInt(breakvalue)
                                        gift = gift * Integer.parseInt(freeqty);
                                        System.out.println("Ureka! Found It ===========" + gift);
                                        if (discountschemaID.equals("")) {
                                            discountschemaID = discountschemaID.concat(m_discountschema_id);
                                        } else {
                                            discountschemaID = discountschemaID.concat("," + m_discountschema_id);
                                        }
                                        if (discountschemabreakID.equals("")) {
                                            discountschemabreakID = discountschemabreakID.concat(m_discountschemabreak_id);
                                        } else {
                                            discountschemabreakID = discountschemabreakID.concat("," + m_discountschemabreak_id);
                                        }
                                        //  CreateAlert(m_product_id, gift);
                                        break;
                                    }
                                }
                            }
                        } while (dbreak.moveToNext());
                        break;
                    }
                default:
            }
            //gotdiscount is added by anika on 2025-10-23
            if(gotdiscount){
                System.out.println("Got Discount Already!!!");
            }else { //gotdiscount is added by anika on 2025-10-23
                discountCombinationforproductcat = getProductCategoryDiscount(selectedProductCategoryID);

                switch (discountCombinationforproductcat) {

                    case 100:
                        String bdiscount;
                        Double discountqty;
                        Cursor dbreak = mDatabaseHelper.getAllDiscountSchemaforproductcat(selectedProductCategoryID);

                        if (dbreak.moveToFirst()) {
                            do {
                                String m_discountschema_id = dbreak.getString(2);
                                String m_discountschemabreak_id = dbreak.getString(12);
                                String ispercentbased = dbreak.getString(11);
                                String m_product_id = dbreak.getString(13);
                                String breakvalue = dbreak.getString(14);
                                bdiscount = dbreak.getString(15);
                                String freeqty = dbreak.getString(16);
                                String isproductbased = dbreak.getString(17);
                                String m_product_category_id = dbreak.getString(18);

                                if (selectedProductCategoryID.equals(m_product_category_id)) {
                                    if (ispercentbased.equals("Y")) {
                                        discount = (total_price2.multiply(new BigDecimal(bdiscount))).divide(new BigDecimal(100.00));
                                        discount = discount.setScale(2, BigDecimal.ROUND_HALF_EVEN);
                                        if (discountschemaID.equals("")) {
                                            discountschemaID = discountschemaID.concat(m_discountschema_id);
                                        } else {
                                            discountschemaID = discountschemaID.concat("," + m_discountschema_id);
                                        }
                                        if (discountschemabreakID.equals("")) {
                                            discountschemabreakID = discountschemabreakID.concat(m_discountschemabreak_id);
                                        } else {
                                            discountschemabreakID = discountschemabreakID.concat("," + m_discountschemabreak_id);
                                        }
                                        break;
                                    } else {
                                        System.out.println("I dont know");
                                    }
                                }

                            } while (dbreak.moveToNext());
                        } else {
                            System.out.println("Didn't found the product");
                        }

                        break;
                    default:
                }
            }

            Cursor cursor1 = mDatabaseHelper.getProductOrderLineProduct(LoginActivity.ad_user_id, selectedProductCategoryID);


            if (!cursor1.moveToFirst()) {

                boolean result = false;

                qty = Double.parseDouble(quantity);
                result = mDatabaseHelper.insertIntoProductOrderLine(id1, "1000911", selectedProduct, qty, price2.doubleValue(), total_price2.doubleValue(), documentNo, selectedProdId, LoginActivity.ad_user_id, selectedProductCategoryID, unitOfMeasure.getText().toString(), quantity1, discount.doubleValue(), String.valueOf(gift), discountschemaID, discountschemabreakID); //,productorderhead_Id);

                if (result) {
                    //toast("Product Inserted");
                    SharedPreferences.Editor editor = sh.edit();
                    editor.putInt("NUM", id1);
                    editor.commit();

                    Intent i = new Intent(Sales_Order_Activity1.this, OrderListActivity_json.class);
                    i.putExtra("DOCNO", documentNo);
                    i.putExtra("CUSTOMERNAMECODE", customerName_Code);
                    i.putExtra("selectedBPCustomerID", selectedBPCustomerID);
                    i.putExtra("SHOWDATE", showdateNew);
                    i.putExtra("date_ordered", date_order);
                    i.putExtra("c_bp_group_id", c_bp_group_id);
                    i.putExtra("iscash", iscash);
                    i.putExtra("salesType", salestype); //anika 2023-08-24
                    i.putExtra("total_dueAmt", total_dueAmt); //anika 2023-10-04
                    i.putExtra("creditlimitval", creditlimitval); //anika 2023-10-04
                    i.putExtra("cashpos", cashpos);  // Anika 2023-10-10
                    i.putExtra("ad_org_id", org_id);  // Anika 2023-10-10
                    i.putExtra("selectedBPTerrID",selectedBPTerrID ); // Anika 2026-07-29
                    startActivity(i);

                } else {
                    toast("Could not insert into internal database");
                }
            } else {
                ShowAlert("Warning", "This Product is already added.Please Add or Remove Qty from Product list");
            }
        }

        }
    }
    public int getProductCategoryDiscount(String pro_cat_id){

        Cursor cursor1;
        int retVal = 0;

        cursor1 = mDatabaseHelper.getAllDiscountSchemaforproductcat(pro_cat_id);

        if(cursor1.moveToFirst()) {
            do {
                String m_discountschema_id = cursor1.getString(2);
                String discounttype = cursor1.getString(6);
                String isquantitybased = cursor1.getString(7);
                String ismixed = cursor1.getString(8);

                if (discounttype.equals("B")) {

                    if (isquantitybased.equals("N") && ismixed.equals("N")) {

                        retVal = 100;
                    } else if (isquantitybased.equals("N") && ismixed.equals("Y")) {

                        retVal = 101;
                    } else if (isquantitybased.equals("Y") && ismixed.equals("N")) {

                        retVal = 110;
                    } else if (isquantitybased.equals("Y") && ismixed.equals("Y")) {
                        retVal = 111;
                    }
                }
            } while (cursor1.moveToNext());
        }else{
            System.out.println("Discount Not found");
        }
        return retVal;
    }
    public int getProductDiscount(String ProdId){

        Cursor cursor1;

        cursor1 = mDatabaseHelper.getAllDiscountSchemaforproduct(ProdId);
        int retVal = 0;

        if(cursor1.moveToFirst()){
            do{
                String m_discountschema_id = cursor1.getString(2);
                String discounttype = cursor1.getString(6);
                String isquantitybased = cursor1.getString(7);
                String ismixed = cursor1.getString(8);

                if (discounttype.equals("B")){

                    if (isquantitybased.equals("N") && ismixed.equals("N")) {

                        retVal=100;
                    }
                    else if (isquantitybased.equals("N") && ismixed.equals("Y")) {

                        retVal=101;
                    }
                    else if (isquantitybased.equals("Y") && ismixed.equals("N")) {

                        retVal=110;
                    }
                    else if (isquantitybased.equals("Y") && ismixed.equals("Y")) {
                        retVal=111;
                    }
                }
            }while(cursor1.moveToNext());
        }
        return retVal;
    }
}


