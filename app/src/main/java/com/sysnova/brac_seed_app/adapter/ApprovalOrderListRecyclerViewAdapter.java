package com.sysnova.brac_seed_app.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

//import com.sysnova.brac_fisheries_app.Activity.UpdateApprovalActivity;
import com.sysnova.brac_seed_app.Activity.UpdateApprovalActivity;
import com.sysnova.brac_seed_app.modal.ApprovalOrderModal;
import com.sysnova.brac_seed_app.R;

import java.util.List;

public class ApprovalOrderListRecyclerViewAdapter extends RecyclerView.Adapter<ApprovalOrderListRecyclerViewAdapter.ViewHolder> {

    List<ApprovalOrderModal> orderListActivities;
    Context mContext;

    public ApprovalOrderListRecyclerViewAdapter(List<ApprovalOrderModal> orderListActivities, Context mContext) {
        this.orderListActivities = orderListActivities;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.approval_order_list,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

            holder.serialTextView.setText(String.valueOf(position + 1));
            holder.sendedDateTextView.setText(orderListActivities.get(position).getSentDate());
            //holder.orderDocTextView.setText(orderListActivities.get(position).getOrderDoc());
            holder.erpDocTextView.setText(orderListActivities.get(position).getErpDoc());

            holder.goApprovalTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, UpdateApprovalActivity.class);
                    intent.putExtra("order_doc",orderListActivities.get(position).getOrderDoc());
                    intent.putExtra("erp_doc",orderListActivities.get(position).getErpDoc());

                    mContext.startActivity(intent);
                }
            });


//            holder.tableRow.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View v) {
//                    final DatabaseHelper databaseHelper = new DatabaseHelper(mContext);
//                    final Dialog dialog = new Dialog(mContext);
//                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
//                    dialog.setContentView(R.layout.custom);
//
//                    Button yesBtn = dialog.findViewById(R.id.dialogButtonOK);
//                    Button noBtn  = dialog.findViewById(R.id.cancel);
//
//                    yesBtn.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            databaseHelper.deleteSalesOrderLine(orderListActivities.get(position).getProductID(),orderListActivities.get(position).getDocNo());
//                            orderListActivities.remove(position);
//                            notifyItemRemoved(position);
//                            notifyItemRangeChanged(position,orderListActivities.size());
//                            OrderListActivity orderListActivity = new OrderListActivity();
//                            orderListActivity.loadOrderPrice(mContext);
//                            dialog.dismiss();
//                        }
//                    });
//
//                    noBtn.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            dialog.dismiss();
//                        }
//                    });
//                    dialog.show();
//                    return false;
//                }
//            });
    }

    @Override
    public int getItemCount() {
        return orderListActivities.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView orderDocTextView,erpDocTextView,goApprovalTextView,serialTextView,sendedDateTextView;
        public ViewHolder(View itemView){
            super(itemView);
            serialTextView    = itemView.findViewById(R.id.serial_id);
            sendedDateTextView = itemView.findViewById(R.id.sended_date);
            //orderDocTextView  = itemView.findViewById(R.id.order_doc);
            erpDocTextView = itemView.findViewById(R.id.erp_doc);
            goApprovalTextView = itemView.findViewById(R.id.go_approval);
        }
    }
}
