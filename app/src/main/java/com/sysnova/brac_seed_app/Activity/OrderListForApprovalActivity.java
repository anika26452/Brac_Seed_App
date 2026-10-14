package com.sysnova.brac_seed_app.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.sysnova.brac_seed_app.adapter.ApprovalOrderListRecyclerViewAdapter;
import com.sysnova.brac_seed_app.modal.ApprovalOrderModal;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import static com.sysnova.brac_seed_app.Activity.LoginActivity.ad_user_id;
import static com.sysnova.brac_seed_app.Activity.PostgreSQL.apiUrl;

public class OrderListForApprovalActivity extends AppCompatActivity {

    TextView emptyListText;
    Toolbar toolbar;
    RecyclerView orderListRecyclerView;
    DatabaseHelper mDatabaseHelper;
    Button showdate;
    String cdate,cdate1,datetext,dateOrdered="";
    ArrayList<ApprovalOrderModal> approvalOrderModals = new ArrayList<>();
    private DatePickerDialog.OnDateSetListener dateSetListener1;
    //String ip = "172.16.9.67";
    //String ip = "35.189.187.66";
 //   String ip = "172.16.9.76";
  //  final String apiUrl = "http://"+ip+":9090/SeedsWEBAPI";
    //final String apiUrl = "http://"+ip+":8080/FisheriesWEBAPI";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list_for_approval);
        //ActionBar actionBar = getSupportActionBar();

        // showing the back button in action bar
       // actionBar.setDisplayHomeAsUpEnabled(true);

       // toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Order List For Approval");
        //getSupportActionBar().setSubtitle("Brac Fisheries App");
        getSupportActionBar().setBackgroundDrawable(
                new ColorDrawable(Color.parseColor("#97c740")));
        showdate = (Button) findViewById(R.id.date);
        emptyListText = findViewById(R.id.no_list_id);
        orderListRecyclerView = findViewById(R.id.approve_order_list_recyclerview);
        /*toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(OrderListForApprovalActivity.this, Home.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

            }
        });*/

        mDatabaseHelper = new DatabaseHelper(this);
        //2023-01-29
        dateOrdered=getIntent().getStringExtra("dateOrdered");
        Date dateSql=new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if(dateOrdered==null || dateOrdered.equals("") || dateOrdered.equals("null")){
            cdate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            cdate1 = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        }
        else{
            try {
                dateSql = dateFormat.parse(dateOrdered);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            cdate = dateOrdered; //new SimpleDateFormat("20yy-MM-dd", Locale.getDefault()).format(data1);
            cdate1 = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(dateSql);
        }
        System.out.println(new Date());
        showdate.setText("" + cdate1 + "");
        showdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        OrderListForApprovalActivity.this,
                        dateSetListener1,
                        year, month, day);
                datePickerDialog.show();
            }
        });

        dateSetListener1 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month+1;
                String sdate = dayOfMonth+"/"+month+"/"+year;
                Date date = new Date(year-1900, month-1,dayOfMonth);
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat formatter1 = new SimpleDateFormat("dd-MM-yyyy");
                cdate = formatter.format(date);
                cdate1 = formatter1.format(date);
                showdate.setText(cdate1);
                DBsyncActivity();
            }
        };
        DBsyncActivity();
    }
    /*public void sendtoservertemp(View view) {
        AsyncSenderNew task = new AsyncSenderNew();
        task.execute();
    }*/
    public void DBsyncActivity() {
        AsyncSenderNew task = new AsyncSenderNew();
        task.execute();
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

            pd = new ProgressDialog(OrderListForApprovalActivity.this);
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

            if (result.equals("Order For Approval Sync Successful")) {

                loadOrderForApproval();
                toastMessage("DBSYNC Successful");
            } else {
               // mDatabaseHelper.deleteOrderApprovalTable();
               // loadOrderForApproval();
                initRecyclerView();//null 2023-03-07
                toastMessage(result);
            }
        }
    }
    public void toastMessage(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP, 0, 0);
        toast.show();
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
    private String DBSYNCNew() {
        String res = "";
        try {
            mDatabaseHelper.deleteOrderApprovalTable();
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
    private String FetchAllOrderForApproval(String user_id) {

        String order_doc, erp_orderNo, grand_total,prepared_by,sales_type,sent_date, res = "";
        String doctype_id="";

        boolean result = false;

        if(!approvalOrderModals.isEmpty())
        {
            approvalOrderModals.clear();
        }

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
            list.add(new BasicNameValuePair("name", "orderApproval"));
            list.add(new BasicNameValuePair("userId", user_id));
          //  list.add(new BasicNameValuePair("date", cdate));
            httpPost.setEntity(new UrlEncodedFormEntity(list));
            HttpResponse httpResponse=	httpClient.execute(httpPost);

            HttpEntity httpEntity=httpResponse.getEntity();
            String s= readResponse(httpResponse);
            if (s.isEmpty() || !s.startsWith("[")){
                mDatabaseHelper.deleteOrderApprovalTable();
                res="Order Not Found";
                //mDatabaseHelper.deleteOrderApprovalTable();
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
                /*System.out.println("Sent date after refactoring==========: ");
                sent_date.trim();
                int b=sent_date.indexOf(" ");
                String finaldate=sent_date.substring(0, b);
                Log.d("ID","ORGIDAPPROVAL"+orgid);*/
                result = mDatabaseHelper.insertIntoOrderApproval(order_doc, erp_orderNo,grand_total,prepared_by,sales_type,sent_date,doctype_id,orgid);
                System.out.println("DOCTYPEID=="+doctype_id);
            }


            if (result) {

                //toastMessage("Insert  Successful into Doctor Table : ");
                res = "Order For Approval Sync Successful";
            }


        } catch (Exception e) {
//            e.printStackTrace();
//            Toast.makeText(getApplicationContext(),"Failed to Data fetch",Toast.LENGTH_SHORT).show();
            res = e.getMessage();

        }
        return res;
    }

    private void loadOrderForApproval(){
        Cursor data = mDatabaseHelper.getSalesOrderListForApproval();
        if(data.moveToFirst()){
            do{
                // int p_key = data.getInt(0);
                String orderDoc=data.getString(1);
                String erpDoc =data.getString(2);
                String grandTotal= data.getString(3);
                String preparedBy = data.getString(4);
                String salesType = data.getString(5);
                String sent_date = data.getString(6);
                ApprovalOrderModal orderListModal = new ApprovalOrderModal(orderDoc,erpDoc,grandTotal,preparedBy,salesType,sent_date);
                approvalOrderModals.add(orderListModal);
            }while(data.moveToNext());
        }
        if(approvalOrderModals == null || approvalOrderModals.isEmpty()){
            emptyListText.setText("No OrderList TO Approve For This User");
        }else{
            initRecyclerView();
        }

    }

    private void initRecyclerView(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        orderListRecyclerView.setLayoutManager(linearLayoutManager);
        ApprovalOrderListRecyclerViewAdapter adapter = new ApprovalOrderListRecyclerViewAdapter(approvalOrderModals,OrderListForApprovalActivity.this);
        orderListRecyclerView.setAdapter(adapter);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        Intent intent = new Intent(OrderListForApprovalActivity.this, Home.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

    }
}
