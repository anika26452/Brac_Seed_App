package com.sysnova.brac_seed_app.adapter;

import static com.sysnova.brac_seed_app.Activity.PostgreSQL.apiUrl;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.sysnova.brac_seed_app.Activity.DealerPayment;
import com.sysnova.brac_seed_app.Activity.PaymentDetail;
import com.sysnova.brac_seed_app.R;
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
import java.util.ArrayList;
import java.util.List;

public class ShowPaymentDateWise extends RecyclerView.Adapter<ShowPaymentDateWise.ViewHolder> {
    List<ViewPaymentListModel> orderListActivities;
    Context mContext;
    String Order_ID,dateText;
    View itemView1=null;

    public ShowPaymentDateWise(ArrayList<ViewPaymentListModel> orderListActivities, Context mContext) {
        this.orderListActivities = orderListActivities;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.show_payment_list_temp,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Order_ID=orderListActivities.get(position).getOrderno();

        String status = orderListActivities.get(position).getDocstatus();

        holder.serialTextView.setText(String.valueOf(position + 1));
        holder.bpnameView.setText(orderListActivities.get(position).getBpname());
        holder.ordernoTextView.setText(orderListActivities.get(position).getDate());
        holder.TotalPriceTextView.setText(String.valueOf(orderListActivities.get(position).getTotal()));
        holder.docstatusView.setText(String.valueOf(orderListActivities.get(position).getDocstatus()));
        holder.bankacctextvw.setText(String.valueOf(orderListActivities.get(position).getBank_name()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (status.equals("Reverted")) {

                    Intent intent = new Intent(mContext, PaymentDetail.class);
                    intent.putExtra("order_doc", orderListActivities.get(position).getOrderno());
                    intent.putExtra("status", status);

                    mContext.startActivity(intent);
                } else{
                    Intent intent = new Intent(mContext, PaymentDetail.class);
                    intent.putExtra("order_doc", orderListActivities.get(position).getOrderno());
                    intent.putExtra("status", status);

                    mContext.startActivity(intent);
                }
            }
        });

    /*    holder.dateTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (status.equals("Reverted")) {

                        Intent intent = new Intent(mContext, DealerPayment.class);
                        intent.putExtra("order_doc", orderListActivities.get(position).getOrderno());

                        mContext.startActivity(intent);
                    } else{
                        Intent intent = new Intent(mContext, PaymentDetail.class);
                        intent.putExtra("order_doc", orderListActivities.get(position).getOrderno());

                        mContext.startActivity(intent);
                    }


                }
        });*/

        if(status.equals("Completed")){
            holder.docstatusView.setTextColor(
                    ContextCompat.getColor(mContext, R.color.darkgreen));
        } else if(status.equals("Reverted")){
            holder.docstatusView.setTextColor(
                    ContextCompat.getColor(mContext, R.color.redcancel));
        } else if (status.equals("Rejected")){
            holder.docstatusView.setTextColor(
                    ContextCompat.getColor(mContext, R.color.grey));
        } else{
            holder.docstatusView.setTextColor(
                    ContextCompat.getColor(mContext, R.color.black));
        }

    }

    @Override
    public int getItemCount() {
        return orderListActivities.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView ordernoTextView,dateTextView,TotalPriceTextView,bpnameView,docstatus_cancel,docstatusView,
                serialTextView, bankacctextvw;
        LinearLayout tableRow;
        public ViewHolder(View itemView){
            super(itemView);
            serialTextView    = itemView.findViewById(R.id.sl_id);
            tableRow         = itemView.findViewById(R.id.orderListRow);
            bpnameView = itemView.findViewById(R.id.dealer);
            ordernoTextView = itemView.findViewById(R.id.order_No);
         //   dateTextView  = itemView.findViewById(R.id.date);
            TotalPriceTextView = itemView.findViewById(R.id.total_price);
            docstatusView = itemView.findViewById(R.id.docstatus);
            docstatus_cancel= itemView.findViewById(R.id.docstatus_cancel);
            bankacctextvw = itemView.findViewById(R.id.bankacc_textvw);
            itemView1=itemView;

        }
    }
}
