package com.sysnova.brac_seed_app.Activity;

import static com.sysnova.brac_seed_app.Activity.PostgreSQL.apiUrl;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sysnova.brac_seed_app.R;
import com.sysnova.brac_seed_app.adapter.ShowPaymentDateWise;
import com.sysnova.brac_seed_app.modal.ViewPaymentListModel;

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

public class ViewDealerPayment extends AppCompatActivity {
    Button showdate;
    String cdate,cdate1,dateOrdered="";
    RecyclerView orderListRecyclerView;
    DatabaseHelper mDatabaseHelper;
    TextView emptyListText;
    ArrayList<ViewPaymentListModel> orderListModalArrayList = new ArrayList<>();
    private DatePickerDialog.OnDateSetListener dateSetListener1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_payment);

        getSupportActionBar().setTitle("View Payments");
        getSupportActionBar().setBackgroundDrawable(
                new ColorDrawable(Color.parseColor("#97c740")));

        showdate = (Button) findViewById(R.id.date);
        orderListRecyclerView = findViewById(R.id.showorderinlist);
        emptyListText = findViewById(R.id.no_list_id);

        mDatabaseHelper = new DatabaseHelper(this);
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
            cdate = dateOrdered;
            cdate1 = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(dateSql);
        }

        showdate.setText("" + cdate1 + "");
        showdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        ViewDealerPayment.this,
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
                Showallorder();
            }
        };
        AsyncViewer task = new AsyncViewer();
        task.execute();
    }

    private void Showallorder() {
        AsyncViewer task = new AsyncViewer();
        task.execute();
    }

    private final class AsyncViewer extends AsyncTask<String, String, String> {
        ProgressDialog pd;
        @Override
        protected String doInBackground(String... strings) {
            String result = "", error = "";
            try {
                result = DBSYNCVIEW();
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

            pd = new ProgressDialog(ViewDealerPayment.this);
            pd.setTitle("Please Wait ");
            pd.setMessage("Payment is Fetching ........");
            pd.setCancelable(false);
            pd.setIndeterminate(true);
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pd.show();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pd.dismiss();

            if (result.equals("Insert Successfully into View Payment Table")) {
                initRecyclerView();
            } else {
                initRecyclerView();
                Toast.makeText(ViewDealerPayment.this, result, Toast.LENGTH_LONG).show();
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

    private String DBSYNCVIEW() {

        String res = "";

        res=fetchAllOrderDateWise();

        try {
            return res;
        } catch (Exception e){
            res=e.getMessage();
        }
        return res;
    }

    private String  fetchAllOrderDateWise() {
        String res = "";
        String doc_no ="",cost_centre ="", grandtotal ="",user_id, bpname ="",docstatus="", bank_name= "";
        boolean result = false;
        if(!orderListModalArrayList.isEmpty())
        {
            orderListModalArrayList.clear();
        }
        try {
            HttpURLConnection con = (HttpURLConnection) ( new URL(""+apiUrl+"/DBSync")).openConnection();
            con.setRequestMethod("POST");
            con.setDoInput(true);
            con.setDoOutput(true);
            con.connect();

            HttpClient httpClient=new DefaultHttpClient();
            HttpPost httpPost=new HttpPost(""+apiUrl+"/DBSync");

            List<NameValuePair> list=new ArrayList<NameValuePair>();
            list.add(new BasicNameValuePair("name", "ViewPayment"));

            list.add(new BasicNameValuePair("date", cdate));
            list.add(new BasicNameValuePair("ad_user_id", LoginActivity.ad_user_id));

            httpPost.setEntity(new UrlEncodedFormEntity(list));
            HttpResponse httpResponse=	httpClient.execute(httpPost);

            HttpEntity httpEntity=httpResponse.getEntity();
            String s= readResponse(httpResponse);
            if (s.isEmpty() || !s.startsWith("[")){
                res="There are no Payment";
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

                doc_no = (String) line.get(0);
                cost_centre = (String) line.get(1);
                grandtotal = (String) line.get(2);
                user_id = (String) line.get(3);
                bpname = (String) line.get(4);
                docstatus = (String) line.get(5);
                bank_name = (String) line.get(6);

                bpname = bpname.replaceFirst("\\(", "_");

                ViewPaymentListModel orderListModal = new ViewPaymentListModel(doc_no,cost_centre,grandtotal,bpname,docstatus, bank_name);
                orderListModalArrayList.add(orderListModal);
                result =true;
            }

            if (result) {
                res = "Insert Successfully into View Payment Table";
            }
        } catch (Exception e) {
            res = e.getMessage();
        }
        return res;
    }

    public void initRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        orderListRecyclerView.setLayoutManager(linearLayoutManager);
        ShowPaymentDateWise adapter = new ShowPaymentDateWise(orderListModalArrayList, ViewDealerPayment.this);
        orderListRecyclerView.setAdapter(adapter);
    }
    public void callCancel(View view) {
        Intent i=new Intent(ViewDealerPayment.this,Home.class);
        startActivity(i);

    }
    public void callCancelNew(View view) {
        Intent i=new Intent(ViewDealerPayment.this,Home.class);
        startActivity(i);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i=new Intent(ViewDealerPayment.this, Home.class);
        startActivity(i);
    }


}