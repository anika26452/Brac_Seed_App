package com.sysnova.brac_seed_app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sysnova.brac_seed_app.modal.ApprovalProductDetailsModal;
import com.sysnova.brac_seed_app.R;

import java.text.DecimalFormat;
import java.util.List;

public class ApprovalProductsListRecyclerViewAdapter extends RecyclerView.Adapter<ApprovalProductsListRecyclerViewAdapter.ViewHolder> {
    List<ApprovalProductDetailsModal> approvalProductDetailsModals;
    Context mContext;
    private static final DecimalFormat decfor = new DecimalFormat("0.00");

    public ApprovalProductsListRecyclerViewAdapter(List<ApprovalProductDetailsModal> approvalProductDetailsModals, Context mContext) {
        this.approvalProductDetailsModals = approvalProductDetailsModals;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_product_appoval_list,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.slTextView.setText(String.valueOf(position + 1));
        holder.productTextView.setText(approvalProductDetailsModals.get(position).getProductName());
        holder.uomsymbolTextView.setText(approvalProductDetailsModals.get(position).getUom());
     //   Double d1 = Double.parseDouble(approvalProductDetailsModals.get(position).getBagQty());
     //   Double d2 = Double.parseDouble(approvalProductDetailsModals.get(position).getVolume());
    //    Double val = d1 /d2;
        holder.kgTextView.setText(String.valueOf(decfor.format(Double.parseDouble(approvalProductDetailsModals.get(position).getBagQty())/Double.parseDouble(approvalProductDetailsModals.get(position).getVolume()))));
        holder.bagTextView.setText(approvalProductDetailsModals.get(position).getBagQty());
        holder.rateTextView.setText(String.valueOf(decfor.format(Double.parseDouble(approvalProductDetailsModals.get(position).getRate()))));
        holder.dis_priceTextView.setText(String.valueOf(decfor.format(Double.parseDouble(approvalProductDetailsModals.get(position).getdis_price()))));
        holder.takaTextView.setText(String.valueOf(decfor.format(Double.parseDouble(approvalProductDetailsModals.get(position).getTotalTaka()))));
        holder.bonustxtvw.setText(approvalProductDetailsModals.get(position).getBonus_qty());

    }

    @Override
    public int getItemCount() {
        return approvalProductDetailsModals.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView bonustxtvw,slTextView,productTextView,kgTextView,bagTextView,rateTextView,takaTextView,uomsymbolTextView,dis_priceTextView;

        public ViewHolder(View itemView){
            super(itemView);
            slTextView  = itemView.findViewById(R.id.sl_id);
            productTextView = itemView.findViewById(R.id.product_name_id);
            uomsymbolTextView = itemView.findViewById(R.id.uom_id);
            kgTextView = itemView.findViewById(R.id.no_ofbag);
            bagTextView = itemView.findViewById(R.id.qty_bag_id);
            rateTextView = itemView.findViewById(R.id.rate_id);
            takaTextView = itemView.findViewById(R.id.total_taka_id);
            dis_priceTextView = itemView.findViewById(R.id.dis_price);
            bonustxtvw =  itemView.findViewById(R.id.bonus_price);
        }
    }
}
