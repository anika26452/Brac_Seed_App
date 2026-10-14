package com.sysnova.brac_seed_app.Activity;

import static com.sysnova.brac_seed_app.Activity.PostgreSQL.apiUrl;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sysnova.brac_seed_app.R;
import com.sysnova.brac_seed_app.adapter.OrderListRecyclerViewAdapter;
import com.sysnova.brac_seed_app.modal.OrderListModal;

import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

public class OrderListActivity extends AppCompatActivity {
   // String ip = "172.16.9.76";
    //String ip = "35.189.187.66"
    //final String apiUrl = "http://"+ip+":8080/FisheriesWEBAPI";
  //  final String apiUrl = "http://"+ip+":9090/SeedsWEBAPI";
    static String docNo;
    BigDecimal total_price;
    static TextView totalPriceTextView,totalPriceInWordsTextView;
    Button sendToServerButton;
    RecyclerView orderListRecyclerView;
    DatabaseHelper mDatabaseHelper;
    String customerName_Code="", cdate="";
    TextView qtySum,toatlSum,customerName_CodeTV, commsum, totalwithcommsum;
    List<OrderListModal> model;
    EditText discout_editText,Remarks_editText,total_editTextET;
    CheckBox discountCheckBox;
    RecyclerView recyclerView;
    boolean ispercentdiscount=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list_activty);

        /**designing code**/
        getSupportActionBar().setTitle("Brac Seed Sales App");
        getSupportActionBar().setBackgroundDrawable(
                new ColorDrawable(Color.parseColor("#97c740")));
        /**designing code**/

        docNo=getIntent().getStringExtra("DOCNO");
        customerName_Code=getIntent().getStringExtra("CUSTOMERNAMECODE");

        orderListRecyclerView = findViewById(R.id.order_list_recyclerview);
        customerName_CodeTV=findViewById(R.id.customernamecode);
        customerName_CodeTV.setText(customerName_Code);
        totalPriceTextView = findViewById(R.id.tPrice_textView);
        totalPriceInWordsTextView = findViewById(R.id.tPrice_textView_inwords);
        sendToServerButton = findViewById(R.id.send_order_btn);
        Remarks_editText=(EditText)findViewById(R.id.Remarks_editText); // Remarks_editText newly added by shanzida 2021-11-20
        qtySum=(TextView)findViewById(R.id.listQtySum);
        toatlSum=(TextView)findViewById(R.id.listTotalSum);
        commsum = findViewById(R.id.listCommSum);
        totalwithcommsum = findViewById(R.id.totalwithcommSum);

        toatlSum.setText("");

        mDatabaseHelper=DatabaseHelper.getInstance(this);
        model=new ArrayList<>();

        recyclerView=(RecyclerView)findViewById(R.id.order_list_recyclerview);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(OrderListActivity.this);
        recyclerView.setLayoutManager(layoutManager);

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("custom-message"));
        //loadOrderList();
        loadProductOrderLine();
        // loadOrderPrice(OrderListActivity_json.this);
        //  sendToServer();
        //ondiscountEditTextFocusChange();
        ispercentdiscount=false;
    }

    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            double ItemName = intent.getDoubleExtra("TOTAL",0.0);
            int itemName2=intent.getIntExtra("TOTALQTY",0);
            //anika 2023-05-11
            double ItemName3 = intent.getDoubleExtra("TOTALCOMM", 0.0);
            double ItemName4 = intent.getDoubleExtra("tOTALWITHCOMM", 0.0);

            toatlSum.setText(String.valueOf(ItemName));
            qtySum.setText(String.valueOf(itemName2));
            commsum.setText(String.valueOf(ItemName3));
            totalwithcommsum.setText(String.valueOf(ItemName4));

        }
    };

    private void loadProductOrderLine(){
        int id=0,qty=0;
        String code="",name="", bonus = "";
        double price=0.00,total=0.00, discount = 0.00;
        Cursor data=null;
        try {
            data = mDatabaseHelper.getProductOrderLine(LoginActivity.ad_user_id);

            if (data.moveToFirst()) {
                do {
                    // int p_key = data.getInt(0);
                    id = data.getInt(0);
                    code = data.getString(1);
                    name = data.getString(2);
                    qty = data.getInt(3);
                    price = data.getDouble(4);
                    total = data.getDouble(5);
                    discount = data.getDouble(12);
                    bonus = data.getString(13);
                    OrderListModal orderListModal = new OrderListModal(id, code, name, qty, price, total, discount, bonus);
                    model.add(orderListModal);
                } while (data.moveToNext());
            }
        }
        finally {
            if (data != null)
                data.close();  // RIGHT: ensure resource is always recovered
        }

        if(model == null || model.isEmpty()){
            toast("No Order Found");
        }else{
            initRecyclerView();
            double t=0.0;
            t=getTotal();
            double Tqty=0.0;
            Tqty=getTotalQty();
            double tcomm = 0.0;
            tcomm = getCommission();
            double twithcomm = 0.0;
            twithcomm = getTotalwithComm();
            qtySum.setText(String.valueOf(Tqty));
            toatlSum.setText(String.valueOf(t));
            commsum.setText(String.valueOf(tcomm));
            totalwithcommsum.setText(String.valueOf(twithcomm));

         //   long intTotalPrice = (new Double(t)).longValue();
            long intTotalPrice = (new Double(twithcomm)).longValue();
            String return_val_in_words =   NumberToWords2.convert(intTotalPrice);
            totalPriceInWordsTextView.setText(return_val_in_words +" Taka");

        }

    }
    public double getTotal()
    {
        double total=0.0;
        for(int i=0;i<model.size();i++)
        {
            total=total+model.get(i).getTotal();
            //  System.out.println("Total@@@@@@@@@@@@@@@"+total);
            Log.d("TOTAL",String.valueOf(i+" "+total));
        }
        return total;
    }
    public double getTotalQty()
    {
        double total=0;
        for(int i=0;i<model.size();i++)
        {
            total=total+model.get(i).getQty();
            // System.out.println("Total@@@@@@@@@@@@@@@"+total);
            Log.d("TOTAL",String.valueOf(i+" "+total));
        }
        return total;
    }
    //anika 2023-05-11
    public double getCommission()
    {
        double total=0.0;
        for(int i=0;i<model.size();i++) {
            total=total+model.get(i).getDiscount();

        }
        return total;
    }

    public double getTotalwithComm()
    {
        double total=0.0;
        for(int i=0;i<model.size();i++)
        {
            total=total+(model.get(i).getTotal()-model.get(i).getDiscount());
        }
        return total;
    }

    //anika 2023-05-11
    public void loadOrderPrice(Context mContext){

        try{
            DatabaseHelper mDatabaseHelper = new DatabaseHelper(mContext);
            //total_price=Double.parseDouble(mDatabaseHelper.getTotalPrice(docNo));
            //total_price convert to new BigDecimal 2021-11-17
            total_price=new BigDecimal(mDatabaseHelper.getTotalPrice(docNo));
            Log.d("DEBUG",String.valueOf(total_price));
            System.out.println(String.valueOf(total_price));

            totalPriceTextView.setText(String.valueOf(total_price));

            //int intTotalPrice = (int) total_price;
            long intTotalPrice = total_price.longValue() ;
            String return_val_in_words =   NumberToWords2.convert(intTotalPrice);
            totalPriceInWordsTextView.setText(return_val_in_words +" Taka");

            //total_editTextET.setText(String.valueOf(Double.parseDouble(mDatabaseHelper.getTotalPrice(docNo))));
            //total_editTextET.setText(String.valueOf(new BigDecimal(mDatabaseHelper.getTotalPrice(docNo))));
        }
        catch (Exception e)
        {
            // toastMessage(e.getMessage().toString());
            Log.d("DEBUG",String.valueOf(e.getMessage()));
            System.out.println(String.valueOf(e.getMessage()));
        }

        /**Cursor totalPrice = mDatabaseHelper.getTotalPrice(docNo);
         if(totalPrice.moveToFirst()){
         do{
         total_price = totalPrice.getDouble(0);
         }while(totalPrice.moveToNext());
         }**/
//        Cursor check = mDatabaseHelper.getSalesOrderLineDataTemp(docNo);
//        if(!check.moveToNext()) {
//            sendToServerButton.setEnabled(false);
//        }
    }
    void loadTotalPrice()
    {

    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent i=new Intent(OrderListActivity.this,Sales_Order_Activity1.class);
                i.putExtra("DOCNO",docNo);
                i.putExtra("CUSTOMERNAMECODE",customerName_CodeTV.getText().toString());
                startActivity(i);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*private void loadOrderList(){
        Cursor data = mDatabaseHelper.getSalesOrderLineDataTemp(docNo);
        if(data.moveToFirst()){
            do{
               // int p_key = data.getInt(0);
                String productcat_ID=data.getString(2);
                String customername_value =String.valueOf(data.getString(3));
                //int qty= data.getInt(4);
                String qty= data.getString(4);
                //price =Double.parseDouble(new DecimalFormat("##.####").format(price));
                String productName = data.getString(1);
                String total = data.getString(5);
                String uom       = data.getString(6);
                String doc       = data.getString(7);
                String product_ID=data.getString(8);

               // total_price = total_price + price;
                OrderListModal orderListModal = new OrderListModal(productName,productcat_ID,customername_value,
                        qty,total,uom,doc,product_ID);
                orderListModalArrayList.add(orderListModal);
            }while(data.moveToNext());
        }
        initRecyclerView();

    }*/

    /*public void initRecyclerView1() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        orderListRecyclerView.setLayoutManager(linearLayoutManager);
        //OrderListRecyclerViewAdapter adapter = new OrderListRecyclerViewAdapter(orderListModalArrayList, OrderListActivity_json.this);
        OrderListRecyclerViewAdapter adapter = new OrderListRecyclerViewAdapter(model, OrderListActivity_json.this);
        orderListRecyclerView.setAdapter(adapter);
    }*/
    private void initRecyclerView(){
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(OrderListActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        OrderListRecyclerViewAdapter adapter = new OrderListRecyclerViewAdapter(model,OrderListActivity.this);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        //getTotal();


    }

    /** private void sendToServer(){
     sendToServerButton.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
    sendToServerButton.setEnabled(false);
    AsyncSendToServer task = new AsyncSendToServer();
    task.execute();
    }
    });
     }**/


    private final class AsyncSendToServer extends AsyncTask<String,String,String> {

        // String result = "";
        @Override
        protected String doInBackground(String... strings) {
            String result = "";


            boolean isActive=true;

            //Cursor check = mDatabaseHelper.getSalesOrderLineDataTemp(docNo);
            Cursor check = mDatabaseHelper.getProductOrderLine(LoginActivity.ad_user_id);
            //getProductOrderLineTemp(docNo);


            String Remarks= Remarks_editText.getText().toString();
            if(!Remarks.equals("null") && Remarks!= null && !Remarks.isEmpty()) {
                mDatabaseHelper.updateOrderDes(docNo, Remarks);
            }
            if(check.moveToNext()) {

            } else {
                // toastMessage("No OrderLine Exists ");
                return "noOrderLine";
            }
            //try {ArrayList salesHeader=new ArrayList();

            String customer_code="";

            String[] sourceSplit= (Customer_Selection_Activity.showdateNew.replace(" ", "")).split("-");
            int yyyy= Integer.parseInt(sourceSplit[2]);
            int mm= Integer.parseInt(sourceSplit[1]);
            int dd= Integer.parseInt(sourceSplit[0]);
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.set(yyyy,mm-1,dd);
            Date data1= calendar.getTime();
            cdate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(data1);

            //String discount=discout_editText.getText().toString();
            int pos=customerName_CodeTV.getText().toString().indexOf('-');
            customer_code=customerName_CodeTV.getText().toString().substring(pos+2,customerName_CodeTV.getText().toString().length());
                  /*  Vector headerLines=new Vector();
                    headerLines.add(selectedOrgId);

                    headerLines.add(selectedWHId);
                    headerLines.add(docNo);
                    headerLines.add(employeeid);
                    headerLines.add(selectedSalesType);
                    headerLines.add(customer_code);
                    headerLines.add(total_editTextET.getText().toString());
                    headerLines.add(reference_no_et.getText().toString());
                    //headerLines.add(Remarks_editText.getText().toString()); //2021-11-20
                    if(discount.isEmpty())
                    {
                        discount="0";
                    }

                    headerLines.add(discount);
                if(ispercentdiscount)
                    {
                        headerLines.add("Y");
                    }
                    else
                    {
                        headerLines.add("N");
                    }
                headerLines.add(Remarks_editText.getText().toString());
                headerLines.add(cdate);

                salesHeader.add(headerLines);

                String headerString = "";
                for (Object s : salesHeader) {
                    if (headerString.isEmpty()) {
                        headerString =headerString+s;
                    } else {
                        headerString =headerString+"_" + s;

                    }
                }

                Cursor dataorderLine = mDatabaseHelper.getSalesOrderLineDataTemp(docNo);
                String replacedProductname="";
                ArrayList salesOrder=new ArrayList();
                while (dataorderLine.moveToNext()) {


                    String product_name = dataorderLine.getString(1);
                    replacedProductname=product_name.replaceAll("_","-");
                    //int quantity = dataorderLine.getInt(4); //2021-12-22
                    String quantity = dataorderLine.getString(4);
                    String unit_price  = dataorderLine.getString(3);
                    String uom=dataorderLine.getString(6);
                    String searchkey=dataorderLine.getString(9);
                    Vector orderLines=new Vector();
                    orderLines.add(selectedOrgId);
                    orderLines.add(replacedProductname);
                    orderLines.add(searchkey);
                    orderLines.add(String.valueOf(quantity));
                    orderLines.add(uom);
                    orderLines.add(String.valueOf(unit_price));

                    Log.d("DEBUG","selectedOrgId"+selectedOrgId);
                    Log.d("DEBUG","product_name"+replacedProductname);
                    Log.d("DEBUG","Search"+"key");
                    Log.d("DEBUG","quantity"+quantity);
                    Log.d("DEBUG","uom"+uom);
                    Log.d("DEBUG","unit_price"+unit_price);


                    salesOrder.add(orderLines);


                }


                String lineString = "";
                for (Object s : salesOrder) {
                    if (lineString.isEmpty()) {
                        lineString =lineString+s;
                    } else {
                        lineString =lineString+"_" + s;

                    }
                }

                HttpURLConnection con = (HttpURLConnection) ( new URL(""+apiUrl+"/SalesOrder")).openConnection();
                con.setRequestMethod("POST");
                con.setDoInput(true);
                con.setDoOutput(true);
                con.connect();

                HttpClient httpClient=new DefaultHttpClient();
                HttpPost httpPost=new HttpPost(""+apiUrl+"/SalesOrder");

                List<NameValuePair> list=new ArrayList<NameValuePair>();
                list.add(new BasicNameValuePair("type", "salesorder"));
                list.add(new BasicNameValuePair("header", headerString));
                list.add(new BasicNameValuePair("lines",lineString));
                httpPost.setEntity(new UrlEncodedFormEntity(list));
                HttpResponse httpResponse=	httpClient.execute(httpPost);



                HttpEntity httpEntity=httpResponse.getEntity();
                String s= readResponse(httpResponse);

                if (s.isEmpty() || !s.equals("Success")){
                    result=s;
                    return result;
                } else if(s.equals("Success")){
                    return "Success";
                }

            } catch (Exception e){
                result = e.getMessage();
            }
            return result;
        }*/
            String tt=toatlSum.getText().toString(); //total_editTextET
            String Remarks1=Remarks_editText.getText().toString();
            try {
                JSONObject jsonParam = new JSONObject();


                /*jsonParam.put("AD_Org_ID", Customer_Selection_Activity.selectedOrgId);
                jsonParam.put("M_Warehouse_ID", Customer_Selection_Activity.selectedWHId);
                jsonParam.put("docNo", docNo);
                jsonParam.put("ad_user_id", LoginActivity.employeeid);
                jsonParam.put("selectedSalesType", Customer_Selection_Activity.selectedSalesType);
                jsonParam.put("customer_code", customer_code);
                jsonParam.put("total", tt);
                jsonParam.put("Remarks", Remarks1);
                jsonParam.put("cdate", cdate);*/

                JSONObject jsonParamhead = new JSONObject();
                JSONObject valuesObject = new JSONObject();
                JSONArray list = new JSONArray();
                valuesObject.put("AD_Org_ID", Customer_Selection_Activity.selectedOrgId);
                valuesObject.put("M_Warehouse_ID", Customer_Selection_Activity.selectedWHId);
                valuesObject.put("docNo", docNo);
                valuesObject.put("ad_user_id", LoginActivity.employeeid);
                valuesObject.put("selectedSalesType", Customer_Selection_Activity.selectedSalesType);
                valuesObject.put("customer_code", customer_code);
                valuesObject.put("total", tt);
                valuesObject.put("Remarks", Remarks1);
                valuesObject.put("cdate", cdate);
                list.put(valuesObject);
                jsonParamhead.accumulate("header", list);
                System.out.println(jsonParamhead);

                JSONObject jsonParamline = new JSONObject();
                //Cursor dataorderLine = mDatabaseHelper.getSalesOrderLineDataTemp(docNo);
                Cursor dataorderLine = mDatabaseHelper.getProductOrderLine(LoginActivity.ad_user_id);

                String replacedProductname="";
                //JSONObject valuesObject2 = new JSONObject();
                JSONArray list2 = new JSONArray();
                while (dataorderLine.moveToNext()) {
                    /*String product_name = dataorderLine.getString(1);
                    replacedProductname=product_name.replaceAll("_","-");
                    //int quantity = dataorderLine.getInt(4); //2021-12-22
                    String quantity = dataorderLine.getString(4);
                    String unit_price  = dataorderLine.getString(3);
                    String uom=dataorderLine.getString(6);
                    String searchkey=dataorderLine.getString(9);*/
                    String searchkey=dataorderLine.getString(1);
                    String productName = dataorderLine.getString(2);
                    replacedProductname=productName.replaceAll("_","-");
                    String quantity = String.valueOf(dataorderLine.getInt(3));
                    String unit_price  = String.valueOf(dataorderLine.getDouble(4));
                    String total1=String.valueOf(dataorderLine.getDouble(5));
                    String mproduct_id=String.valueOf(dataorderLine.getString(7));
                    String uom=dataorderLine.getString(10);
                    JSONObject valuesObject2 = new JSONObject();
                    /*JSONArray list2 = new JSONArray();*/

                    valuesObject2.put("AD_Org_ID",Customer_Selection_Activity.selectedOrgId);
                    valuesObject2.put("product_name",replacedProductname);
                    valuesObject2.put("product_value",searchkey);
                    valuesObject2.put("quantity",String.valueOf(quantity));
                    valuesObject2.put("uom",uom);
                    valuesObject2.put("unit_price",String.valueOf(unit_price));
                    /*Vector valuesObject2=new Vector();
                    valuesObject2.add(Customer_Selection_Activity.selectedOrgId);
                    valuesObject2.add(replacedProductname);
                    valuesObject2.add(searchkey);
                    valuesObject2.add(String.valueOf(quantity));
                    valuesObject2.add(uom);
                    valuesObject2.add(String.valueOf(unit_price));*/
                    list2.put(valuesObject2);
                }
                jsonParamline.accumulate("lines", list2);
                System.out.println(jsonParamline);

                jsonParam.put("Object1", jsonParamhead);
                //jsonParam.put("Object2", jsonParamline);
                //toastMessage(encodedString);


                //String jsonObj = jsonParam.toString();
                //System.out.println("jsonobj=========================" + jsonObj);
                //String url = "http://" + ip + ":8080/KFGSalesNewWEBAPI/jsonlearning?"; //?data='"+jsonParam+"'
                HttpURLConnection conn = (HttpURLConnection) (new URL("" + apiUrl + "/SalesOrderJson")).openConnection();
                conn.setRequestMethod("POST");
                //conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-Type", "application/json");// application/x-www-form-urlencode");
                //conn.setRequestProperty("Accept", "application/json,text/html");
                // conn.setRequestProperty("Content-Type", "application/x-www-form-urlencode;");// application/x-www-form-urlencode");
                conn.setRequestProperty("Accept", "application/json");
                //conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                //conn.setRequestProperty("Accept","application/json,text/html");

                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.connect();

                // OutputStream os = conn.getOutputStream();
                //  os.write(jsonObject.toString().getBytes("UTF-8"));

                DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                //os.writeBytes(jsonParam.toString());
                os.writeBytes(jsonParamhead.toString());

                int code = conn.getResponseCode();
                //if (code == 200) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                StringBuffer stringBuffer = new StringBuffer();
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    stringBuffer.append(line);
                }
                bufferedReader.close();
                result = stringBuffer.toString();
                //}
                //jsonParam =  new JSONObject(stringBuffer.toString());
                //   array = new JSONArray(stringBuffer.toString());
                //array = jsonParam.getJSONArray("response");
                os.flush();
                os.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
                //System.out.println(ioException.printStackTrace());
            } catch (JSONException jsonException) {
                jsonException.printStackTrace();
            }
            System.out.println(result);
            //toastMessage(result);

            //2022-06-16
            return result;
        }
    }



        /*@Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try{
                if(s.equals("Success")){
                    mDatabaseHelper.deleteSalesOrder();
                    mDatabaseHelper.deleteSalesOrderLine();
                    toastMessage("Order Placed Successfully");
                   // sendToServerButton.setBackgroundColor(getColor(R.color.colorGreen));
                    Intent intent = new Intent(OrderListActivity.this,Home.class);
                    startActivity(intent);
                    finish();
                }else if(s.equals("noOrderLine")){
                    toastMessage("No order line exits");
                } else{
                    //sendToServerButton.setEnabled(true);
                    //toastMessage("Server Connection lost Please Send Again");
                    ShowAlert("Error","Server Connection Failed Please Send Again, "+s);
                }
            }catch (NullPointerException ex){

            }
        }*/
    //}

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
        }
        return a;
    }


    private void toastMessage(String message){
        Toast.makeText(OrderListActivity.this,message, Toast.LENGTH_SHORT).show();
    }

    public void ShowAlert(String title,String msg){


        androidx.appcompat.app.AlertDialog.Builder dlgAlert  = new androidx.appcompat.app.AlertDialog.Builder(OrderListActivity.this);

        dlgAlert.setMessage(msg);
        dlgAlert.setTitle(title);
        dlgAlert.setIcon(R.drawable.logo);
        dlgAlert.setPositiveButton("OK", null);
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
    }

    public void SendToServerTemp(View view) {
        /** mDatabaseHelper.deleteSalesOrderLine();
         toastMessage("order Placed");
         Intent i=new Intent(OrderListActivity
         .this,Home.class);

         startActivity(i);**/
        AsyncSendToServer asyncSendToServer=new AsyncSendToServer();
        asyncSendToServer.execute();
    }

    private void ondiscountEditTextFocusChange(){

        discout_editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!totalPriceTextView.getText().toString().equals("") && !discout_editText.getText().toString().equals("") ){
                    if(!totalPriceTextView.getText().toString().equals(".") && !discountCheckBox
                            .getText().toString().equals(".") ) {
                        //double price = Double.parseDouble(totalPriceTextView.getText().toString());
                        //double to BigDecimal
                        BigDecimal price = new BigDecimal(totalPriceTextView.getText().toString());

                        String d=discout_editText.getText().toString();
                        BigDecimal total_price=new BigDecimal(0);
                        if(!d.isEmpty())
                        {
                            double discount=0.0;
                            if(discountCheckBox.isChecked())
                            {
                                ispercentdiscount=true;
                                //discount=((price )*Double.parseDouble(discout_editText.getText().toString()))/100;
                                BigDecimal discount1=(price.multiply(new BigDecimal(discout_editText.getText().toString()))).divide(new BigDecimal(100));
                                //total_price=(price )-discount;
                                total_price=price.subtract(discount1);
                                /**  if(total_price<0)
                                 {
                                 toast("discount amount is higher than total price");
                                 //total_editText.setText(String.valueOf(0.0));

                                 }**/

                            }
                            else
                            {
                                ispercentdiscount=false;
                                //discount=Double.parseDouble(discout_editText.getText().toString());
                                BigDecimal discount1=new BigDecimal(discout_editText.getText().toString());



                                //total_price = (price )-discount;
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
                            toast("discount amount is higher than total price");
                            //total_editText.setText(String.valueOf(0.0));
                            total_editTextET.setText(String.valueOf(0.0));
                        }
                        else
                        {
                            total_editTextET.setText(String.valueOf(total_price));
                        }

                    }
                }else{
                    total_editTextET.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
    public void toast(String msg)
    {
        Toast.makeText(OrderListActivity.this,msg,Toast.LENGTH_SHORT).show();
    }
    public void callAddMoreProducts(View view) {

        Intent i=new Intent(OrderListActivity.this, Sales_Order_Activity1.class);
        startActivity(i);
    }
    public void callCancel(View view) {
        //databaseHelper.deleteAllProductOrderLine();
        Intent i=new Intent(OrderListActivity.this, Sales_Order_Activity1.class);
        startActivity(i);
    }
    public void callCancelLogOut(View view) {
        //cancelalert();
        AlertDialog.Builder builder1 = new AlertDialog.Builder(OrderListActivity.this);
        builder1.setMessage("Are you sure you want to LogOut?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        Intent i=new Intent(OrderListActivity.this,LoginActivity.class);
                        startActivity(i);
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
//                        Intent intent = new Intent(CreatePaymentActivity.this, SelectionAcitvity.class);
//                        startActivity(intent);
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    public void cancelalert(){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(OrderListActivity.this);
        builder1.setMessage("Are you sure you want to LogOut?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        Intent i=new Intent(OrderListActivity.this,LoginActivity.class);
                        startActivity(i);
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
//                        Intent intent = new Intent(CreatePaymentActivity.this, SelectionAcitvity.class);
//                        startActivity(intent);
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i=new Intent(OrderListActivity.this, Sales_Order_Activity1.class);
        i.putExtra("DOCNO",docNo);
        i.putExtra("CUSTOMERNAMECODE",customerName_Code);
        startActivity(i);
    }




}
