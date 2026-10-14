package com.sysnova.brac_seed_app.Activity;

import static com.sysnova.brac_seed_app.Activity.PostgreSQL.apiUrl;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.sysnova.brac_seed_app.adapter.UpdateOrderRecyclerViewAdapter;
import com.sysnova.brac_seed_app.modal.UpdateOrderListModal;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class UpdateOrderActivity extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView recyclerView;
    Button confirmBtn;
    ArrayList<UpdateOrderListModal> orderListModalArrayList = new ArrayList<>();

    String erpDocumentNumber;
  //  String ip = "172.16.9.76";
    //String ip="35.189.187.66";
  //  final String apiUrl = "http://"+ip+":9090/SeedsWEBAPI";
    //final String apiUrl = "http://"+ip+":8080/FisheriesWEBAPI";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_order);

        confirmBtn   = findViewById(R.id.confirm_order_btn);
        recyclerView = findViewById(R.id.ordered_product_list_recyclerview);
        /*toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/
        getSupportActionBar().setTitle("Update Order Activity");
        //getSupportActionBar().setSubtitle("Brac Fisheries App");
        getSupportActionBar().setBackgroundDrawable(
                new ColorDrawable(Color.parseColor("#97c740")));

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle!=null)
        {
            erpDocumentNumber= getIntent().getStringExtra("erp_doc");
        }

        /*toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent1 = new Intent(UpdateOrderActivity.this,UpdateApprovalActivity.class);
                intent1.putExtra("erp_doc",erpDocumentNumber);
                intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent1);
            }
        });*/

        confirmEdit();
        AsyncLoadData asyncLoadData = new AsyncLoadData();
        asyncLoadData.execute();
    }


    private final class AsyncLoadData extends AsyncTask<String, String, String> {

        ProgressDialog pd;
        String result = "";
        @Override
        protected String doInBackground(String... strings) {
            result =  FetchDataFromServer();
            return result;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pd = new ProgressDialog(UpdateOrderActivity.this);
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
                initRecyclerView();
            }
            else {
                toastMessage("Something Went Wrong!!!!");
            }
        }

    }

    public String FetchDataFromServer() {
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
            list.add(new BasicNameValuePair("name", "orderlineList"));
            list.add(new BasicNameValuePair("erpdoc", erpDocumentNumber));
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

                int t_salesOrderLine_id=(Integer.parseInt((String) line.get(0)));
                String product_id =(String) line.get(1);
                String product_name =(String) line.get(2);
                String uom =(String) line.get(3);
                double ordered_qty =(Double.parseDouble((String) line.get(4)));

                UpdateOrderListModal modal = new UpdateOrderListModal(t_salesOrderLine_id,product_id,product_name,uom,ordered_qty);
                orderListModalArrayList.add(modal);
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

    public void initRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
        UpdateOrderRecyclerViewAdapter adapter = new UpdateOrderRecyclerViewAdapter(orderListModalArrayList,UpdateOrderActivity.this);
        recyclerView.setAdapter(adapter);
    }

    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        finish();
        Intent intent1 = new Intent(UpdateOrderActivity.this,UpdateApprovalActivity.class);
        intent1.putExtra("erp_doc",erpDocumentNumber);
        intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent1);
    }

    private void confirmEdit(){
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmBtn.setEnabled(false);
                AsyncSendToServer asyncSendToServer = new AsyncSendToServer();
                asyncSendToServer.execute();
            }
        });
    }
    void sendCategoryidtoserver()
    {


    }

    private final class AsyncSendToServer extends AsyncTask<String,String,String> {

        // String result = "";
        @Override
        protected String doInBackground(String... strings) {
            String result = "";

            try {

                ArrayList salesOrder=new ArrayList();
               for (int i=0;i<orderListModalArrayList.size();i++) {

                    int primaryId = orderListModalArrayList.get(i).getId();
                   // String productId = orderListModalArrayList.get(i).getProductId();
                    double orderedQty  = orderListModalArrayList.get(i).getQty();
                      Vector orderLines=new Vector();
                    orderLines.add(primaryId);
                    orderLines.add(orderedQty);

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
                list.add(new BasicNameValuePair("type", "salesorderupdate"));
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

            }
            catch (Exception e){
                result = e.getMessage();
            }
            return result;
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

                    toastMessage("Order Updated Successfully");
                    Intent intent = new Intent(UpdateOrderActivity.this,OrderListForApprovalActivity.class);
                    startActivity(intent);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    finish();
                }else {
                    confirmBtn.setEnabled(true);
                    //toastMessage("Server Connection lost Please Send Again");
                    toastMessage("Server Connection Failed Please Send Again");
                }
            }catch (NullPointerException ex){

            }
        }
    }
}
