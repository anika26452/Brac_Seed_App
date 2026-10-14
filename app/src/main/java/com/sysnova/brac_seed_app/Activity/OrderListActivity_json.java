package com.sysnova.brac_seed_app.Activity;

import static com.sysnova.brac_seed_app.Activity.Customer_Selection_Activity.iscash;
import static com.sysnova.brac_seed_app.Activity.LoginActivity.Version;
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
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
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

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
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
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

public class OrderListActivity_json extends AppCompatActivity {
    static String docNo;
    BigDecimal total_price;
    static TextView totalPriceTextView,totalPriceInWordsTextView,SelectedOrgName;
    Button sendToServerButton;
    RecyclerView orderListRecyclerView,recyclerView;
    DatabaseHelper mDatabaseHelper;
    String selectedBPTerrID = "",org_id="",total_dueAmt = "", cashpos = "", creditlimitval = "" ,selectedBPCustomerID="", customerName_Code="",date_order="", c_bp_group_id = "", cash = "",cdate="", salestype= "";
    Double total_Due=0.00;
    TextView qtySum,toatlSum, commsum, totalwithcommsum, bonusum,customerName_CodeTV;
    List<OrderListModal> model;
    EditText discout_editText,Remarks_editText,total_editTextET;
    CheckBox discountCheckBox;
    boolean ispercentdiscount=false;
    private static final DecimalFormat decfor = new DecimalFormat("0.00");
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
        selectedBPCustomerID = getIntent().getStringExtra("selectedBPCustomerID");
        date_order = getIntent().getStringExtra("date_ordered");
        c_bp_group_id = getIntent().getStringExtra("c_bp_group_id");
        cash = getIntent().getStringExtra("iscash");
        salestype = getIntent().getStringExtra("salesType");
        total_dueAmt =getIntent().getStringExtra("total_dueAmt"); //anika 2023-10-04
        creditlimitval = getIntent().getStringExtra("creditlimitval"); //anika 2023-10-04
        cashpos = getIntent().getStringExtra("cashpos");
        org_id = getIntent().getStringExtra("ad_org_id");
        selectedBPTerrID = getIntent().getStringExtra("selectedBPTerrID");

        orderListRecyclerView = findViewById(R.id.order_list_recyclerview);
        customerName_CodeTV=findViewById(R.id.customernamecode);
        customerName_CodeTV.setText(customerName_Code);
        totalPriceTextView = findViewById(R.id.tPrice_textView);
        totalPriceInWordsTextView = findViewById(R.id.tPrice_textView_inwords);
        sendToServerButton = findViewById(R.id.send_order_btn);
        Remarks_editText=(EditText)findViewById(R.id.Remarks_editText);
        qtySum=(TextView)findViewById(R.id.listQtySum);
        toatlSum=(TextView)findViewById(R.id.listTotalSum);
        commsum = findViewById(R.id.listCommSum);  //anika 2023-06-11
        totalwithcommsum = findViewById(R.id.totalwithcommSum); //anika 2023-06-11
        bonusum = findViewById(R.id.TotalbonusSum);   //anika 2023-06-11

        toatlSum.setText("");

        mDatabaseHelper=DatabaseHelper.getInstance(this);
        model=new ArrayList<>();

        recyclerView=(RecyclerView)findViewById(R.id.order_list_recyclerview);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(OrderListActivity_json.this);
        recyclerView.setLayoutManager(layoutManager);

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("custom-message"));

        loadProductOrderLine();
        ispercentdiscount=false;
    }

    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            double ItemName = intent.getDoubleExtra("TOTAL",0.0);
            int itemName2=intent.getIntExtra("TOTALQTY",0);
            toatlSum.setText(String.valueOf(decfor.format(ItemName)));
            qtySum.setText(String.valueOf(decfor.format(itemName2)));
            //anika 2023-06-11
            double ItemName3 = intent.getDoubleExtra("TOTALCOMM", 0.0);
            double ItemName4 = intent.getDoubleExtra("tOTALWITHCOMM", 0.0);
            int ItemName5 = intent.getIntExtra("totalbonussum", 0);
            commsum.setText(String.valueOf(decfor.format(ItemName3)));
            totalwithcommsum.setText(String.valueOf(decfor.format(ItemName4)));
            bonusum.setText(String.valueOf(decfor.format(ItemName5)));

            long intTotalPrice = (new Double(ItemName4)).longValue();
            String return_val_in_words =   NumberToWords2.convert(intTotalPrice);
            totalPriceInWordsTextView.setText(return_val_in_words +" Taka Only");
            //anika 2023-06-11
        }
    };

    private void loadProductOrderLine(){
        int id=0;
        String code="",name="", bonus = "";
        double price=0.00,total=0.00, discount = 0.00,qty=0.00;
        Cursor data=null;
        try {
            data = mDatabaseHelper.getProductOrderLine(LoginActivity.ad_user_id);

            if (data.moveToFirst()) {
                do {
                    id = data.getInt(0);
                    code = data.getString(1);
                    name = data.getString(2);
                    qty = data.getDouble(3);
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
                data.close();
        }

        if(model == null || model.isEmpty()){
            toast("No Order Found");
        }else{
            initRecyclerView();
            double t=0.0;
            t=getTotal();
            double Tqty=0.0;
            Tqty=getTotalQty();
            //anika 2023-06-11
            double tcomm = 0.0;
            tcomm = getCommission();
            double twithcomm = 0.0;
            twithcomm = getTotalwithComm();
            int tbonus = 0;
            tbonus = getTotalBonus();
            qtySum.setText(String.valueOf(decfor.format(Tqty)));
            toatlSum.setText(String.valueOf(decfor.format(t)));
            commsum.setText(String.valueOf(decfor.format(tcomm)));
            totalwithcommsum.setText(String.valueOf(decfor.format(twithcomm)));
            bonusum.setText(String.valueOf(decfor.format(tbonus)));

            long intTotalPrice = (new Double(twithcomm)).longValue();
            //anika 2023-06-11
//            qtySum.setText(String.valueOf(Tqty));
//            toatlSum.setText(String.valueOf(t));
//            long intTotalPrice = (new Double(t)).longValue();
            String return_val_in_words =   NumberToWords2.convert(intTotalPrice);
            totalPriceInWordsTextView.setText(return_val_in_words +" Taka");
        }
    }
    public double getTotal() {
        double total=0.0;
        for(int i=0;i<model.size();i++) {
            total=total+model.get(i).getTotal();
        }
        return total;
    }
    public double getTotalQty() {
        double total=0;
        for(int i=0;i<model.size();i++) {
            total=total+model.get(i).getQty();
        }
        return total;
    }

    //anika 2023-06-11
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

    public int getTotalBonus() {
        int total=0;
        for(int i=0;i<model.size();i++)
        {
            total=total+Integer.parseInt(model.get(i).getBonus());
        }
        return total;
    }
    //anika 2023-06-11

    public void loadOrderPrice(Context mContext){

        try{
            DatabaseHelper mDatabaseHelper = new DatabaseHelper(mContext);
            total_price=new BigDecimal(mDatabaseHelper.getTotalPrice(docNo));
            System.out.println(String.valueOf(total_price));

            totalPriceTextView.setText(String.valueOf(total_price));

            long intTotalPrice = total_price.longValue() ;
            String return_val_in_words =   NumberToWords2.convert(intTotalPrice);
            totalPriceInWordsTextView.setText(return_val_in_words +" Taka");
        }
        catch (Exception e)
        {
           // toastMessage(e.getMessage().toString());
            Log.d("DEBUG",String.valueOf(e.getMessage()));
            System.out.println(String.valueOf(e.getMessage()));
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent i=new Intent(OrderListActivity_json.this,Sales_Order_Activity1.class);
                i.putExtra("DOCNO",docNo);
                i.putExtra("CUSTOMERNAMECODE",customerName_CodeTV.getText().toString());
                startActivity(i);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initRecyclerView(){
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(OrderListActivity_json.this);
        recyclerView.setLayoutManager(layoutManager);
        OrderListRecyclerViewAdapter adapter = new OrderListRecyclerViewAdapter(model,OrderListActivity_json.this);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private final class AsyncSendToServer extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String... strings) {
            String result = "";

            boolean isActive=true;

            Cursor check = mDatabaseHelper.getProductOrderLine(LoginActivity.ad_user_id);

            String Remarks=((Remarks_editText.getText().toString().replace(",", "|")).replace("_", "-")); //.setTypeface(Typeface.createFromAsset(Context.getAssets(), "fonts/Durga.ttf"));

            String ttotal=toatlSum.getText().toString();
            if(!Remarks.equals("null") && Remarks!= null && !Remarks.isEmpty()) {
                mDatabaseHelper.updateOrderDes(docNo, Remarks);
            }
            if(check.moveToNext()) {

            } else {
                 return "noOrderLine";
            }
            try {
                ArrayList salesHeader=new ArrayList();
                String customer_code="";

                String[] sourceSplit= (Customer_Selection_Activity.showdateNew.replace(" ", "")).split("-");
                int yyyy= Integer.parseInt(sourceSplit[2]);
                int mm= Integer.parseInt(sourceSplit[1]);
                int dd= Integer.parseInt(sourceSplit[0]);
                GregorianCalendar calendar = new GregorianCalendar();
                calendar.set(yyyy,mm-1,dd);
                Date data1= calendar.getTime();
                cdate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(data1);

                String salesType = (Customer_Selection_Activity.selectedSalesType.replace(",", "|")).replace("_", "-");

                customer_code = (Customer_Selection_Activity.selectedBPCustomerName.replace(",", "|")).replace("_", "-");

                Vector headerLines=new Vector();
                headerLines.add(Customer_Selection_Activity.selectedOrgId);
                headerLines.add(Customer_Selection_Activity.selectedWHId);
                headerLines.add(docNo);
                headerLines.add(LoginActivity.ad_user_id);
                headerLines.add(salesType);
                headerLines.add(customer_code);
                headerLines.add(ttotal);
                headerLines.add(Remarks);
                headerLines.add(cdate);
                headerLines.add(Customer_Selection_Activity.iscash);

                if(Customer_Selection_Activity.selectedTerritoryId.isEmpty()) {
                    headerLines.add("0");
                } else {
                    headerLines.add(Customer_Selection_Activity.selectedTerritoryId);
                }
                if(Customer_Selection_Activity.selectedPriceListId.isEmpty()) {
                    headerLines.add("0");
                } else {
                    headerLines.add(Customer_Selection_Activity.selectedPriceListId);
                }
                if(Customer_Selection_Activity.selectedBPCustomerID.isEmpty()) {
                    headerLines.add("0");
                } else {
                    headerLines.add(Customer_Selection_Activity.selectedBPCustomerID);
                }
                headerLines.add(Version);

                salesHeader.add(headerLines);

                String headerString = "";
                for (Object s : salesHeader) {
                    if (headerString.isEmpty()) {
                        headerString =headerString+s;
                    } else {
                        headerString =headerString+"_" + s;

                    }
                }

                Cursor dataorderLine = mDatabaseHelper.getProductOrderLine(LoginActivity.ad_user_id);
                String replacedProductname="";
                ArrayList salesOrder=new ArrayList();
                while (dataorderLine.moveToNext()) {

                    String searchkey=dataorderLine.getString(1);
                    String productName = dataorderLine.getString(2);
                    replacedProductname=productName.replaceAll("_","-");
                    String quantity = String.valueOf(dataorderLine.getDouble(3));
                    String unit_price  = String.valueOf(dataorderLine.getDouble(4));
                    String total1=String.valueOf(dataorderLine.getDouble(5));
                    String mproduct_id=String.valueOf(dataorderLine.getString(7));
                    String uom=dataorderLine.getString(10);
                    String quantity1 = dataorderLine.getString(11);
                    String discount = dataorderLine.getString(12);
                    String discountschemaID = dataorderLine.getString(14);
                    String discountschemabreakID = dataorderLine.getString(15);

                    Vector orderLines=new Vector();
                    orderLines.add(Customer_Selection_Activity.selectedOrgId);
                    orderLines.add(replacedProductname);
                    orderLines.add(mproduct_id); //(searchkey);
                    orderLines.add(String.valueOf(quantity));
                    orderLines.add(uom);
                    orderLines.add(unit_price);
                    orderLines.add(String.valueOf(quantity1));
                    orderLines.add(discount);
                    orderLines.add(discountschemaID);
                    orderLines.add(discountschemabreakID);

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

            /*String tt=toatlSum.getText().toString(); //total_editTextET
            String Remarks1=Remarks_editText.getText().toString();
            try {
                JSONObject jsonParam = new JSONObject();

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
                    String searchkey=dataorderLine.getString(1);
                    String productName = dataorderLine.getString(2);
                    replacedProductname=productName.replaceAll("_","-");
                    String quantity = String.valueOf(dataorderLine.getInt(3));
                    String unit_price  = String.valueOf(dataorderLine.getDouble(4));
                    String total1=String.valueOf(dataorderLine.getDouble(5));
                    String mproduct_id=String.valueOf(dataorderLine.getString(7));
                    String uom=dataorderLine.getString(10);
                    JSONObject valuesObject2 = new JSONObject();

                    valuesObject2.put("AD_Org_ID",Customer_Selection_Activity.selectedOrgId);
                    valuesObject2.put("product_name",replacedProductname);
                    valuesObject2.put("product_value",searchkey);
                    valuesObject2.put("quantity",String.valueOf(quantity));
                    valuesObject2.put("uom",uom);
                    valuesObject2.put("unit_price",String.valueOf(unit_price));
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
            return result;*/
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try{
                if(s.equals("Success")){
                    mDatabaseHelper.deleteProductOrderHeadNew();
                    mDatabaseHelper.deleteAllProductOrderLine();
                    toastMessage("Order Placed Successfully.");
                    Intent intent = new Intent(OrderListActivity_json.this, Home.class);
                    startActivity(intent);
                }else if(s.equals("noOrderLine")){
                    toastMessage("No order line exits");
                } else{
                    ShowAlert("Error","Server Connection Failed, Please Send Again, "+s);
                }
            }catch (NullPointerException ex){

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
            }
        return a;
    }

    private void toastMessage(String message){
        Toast.makeText(OrderListActivity_json.this,message, Toast.LENGTH_SHORT).show();
    }

    public void ShowAlert(String title,String msg){
        androidx.appcompat.app.AlertDialog.Builder dlgAlert  = new androidx.appcompat.app.AlertDialog.Builder(OrderListActivity_json.this);

        dlgAlert.setMessage(msg);
        dlgAlert.setTitle(title);
        dlgAlert.setIcon(R.drawable.logo);
        dlgAlert.setPositiveButton("OK", null);
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
    }

    public void ShowAlert1(String msg){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage(msg);
        builder1.setCancelable(true);
        builder1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Intent i=new Intent(OrderListActivity_json.this, Home.class);
                startActivity(i);
                finish();
            }
        });
        AlertDialog alert11 = builder1.create();
        alert11.getWindow().setType(WindowManager.LayoutParams.LAST_APPLICATION_WINDOW);
        alert11.show();
        alert11.getWindow().setGravity(Gravity.BOTTOM);
    }

    public void SendToServerTemp(View view) {

        if(iscash.equals("N")){
            if(total_dueAmt.equals("")){
                AsyncSendToServer asyncSendToServer=new AsyncSendToServer();
                asyncSendToServer.execute();
            }else{
                total_Due=Double.parseDouble(totalwithcommsum.getText().toString())+(Double.parseDouble(total_dueAmt));

                if(total_Due > Double.parseDouble(creditlimitval)){
                    ShowAlert("Error",  "Business Partner's credit used amount (with grand total of this order) is greater than credit limit.\nBP Credit Limit : "+creditlimitval+"\nBP Credit Used (With this Order) : "+String.valueOf(total_Due));
                }else{
                    AsyncSendToServer asyncSendToServer=new AsyncSendToServer();
                    asyncSendToServer.execute();
                }
            }
        }else{
            AsyncSendToServer asyncSendToServer=new AsyncSendToServer();
            asyncSendToServer.execute();
        }
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
        Toast.makeText(OrderListActivity_json.this,msg,Toast.LENGTH_SHORT).show();
    }
    public void callAddMoreProducts(View view) {

        Intent i=new Intent(OrderListActivity_json.this, Sales_Order_Activity1.class);
        i.putExtra("DOCNO",docNo);
        i.putExtra("CUSTOMERNAMECODE", customerName_Code);
        i.putExtra("selectedBPCustomerID", selectedBPCustomerID);
        i.putExtra("date_ordered", date_order);
        i.putExtra("c_bp_group_id", c_bp_group_id);
        i.putExtra("iscash", cash);
        i.putExtra("selectedSalesType", salestype); //anika 2023-08-24
        i.putExtra("cashpos", cashpos);  // Anika 2023-10-10
        i.putExtra("ad_org_id", org_id);  // Anika 2023-10-10
        i.putExtra("selectedBPTerrID", selectedBPTerrID);    //anika 2026-07-29
        startActivity(i);
    }
    public void callCancel(View view) {
        //databaseHelper.deleteAllProductOrderLine();
        Intent i=new Intent(OrderListActivity_json.this, Sales_Order_Activity1.class);
        i.putExtra("DOCNO",docNo);
        i.putExtra("CUSTOMERNAMECODE", customerName_Code);
        i.putExtra("selectedBPCustomerID", selectedBPCustomerID);
        i.putExtra("date_ordered", date_order);
        i.putExtra("c_bp_group_id", c_bp_group_id);
        i.putExtra("iscash", cash);
        i.putExtra("selectedSalesType", salestype); //anika 2023-08-24
        i.putExtra("cashpos", cashpos);  // Anika 2023-10-10
        i.putExtra("ad_org_id", org_id);  // Anika 2023-10-10
        i.putExtra("selectedBPTerrID", selectedBPTerrID);    //anika 2026-07-29
        startActivity(i);
    }
    public void callCancelLogOut(View view) {
        //cancelalert();
        AlertDialog.Builder builder1 = new AlertDialog.Builder(OrderListActivity_json.this);
        builder1.setMessage("Are you sure you want to LogOut?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        Intent i=new Intent(OrderListActivity_json.this,LoginActivity.class);
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
        AlertDialog.Builder builder1 = new AlertDialog.Builder(OrderListActivity_json.this);
        builder1.setMessage("Are you sure you want to LogOut?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        Intent i=new Intent(OrderListActivity_json.this,LoginActivity.class);
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
        Intent i=new Intent(OrderListActivity_json.this, Sales_Order_Activity1.class);
        i.putExtra("DOCNO",docNo);
        i.putExtra("CUSTOMERNAMECODE", customerName_Code);
        i.putExtra("selectedBPCustomerID", selectedBPCustomerID);
        i.putExtra("date_ordered", date_order);
        i.putExtra("c_bp_group_id", c_bp_group_id);
        i.putExtra("iscash", cash);
        i.putExtra("selectedSalesType", salestype); //anika 2023-10-03
        i.putExtra("cashpos", cashpos);  // Anika 2023-10-10
        i.putExtra("ad_org_id", org_id);  // Anika 2023-10-10
        i.putExtra("selectedBPTerrID", selectedBPTerrID);    //anika 2026-07-29
        startActivity(i);
    }




}
