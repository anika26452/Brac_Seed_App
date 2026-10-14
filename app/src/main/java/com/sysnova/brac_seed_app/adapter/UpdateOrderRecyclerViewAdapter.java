package com.sysnova.brac_seed_app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sysnova.brac_seed_app.modal.UpdateOrderListModal;
import com.sysnova.brac_seed_app.R;

import java.util.List;

public class UpdateOrderRecyclerViewAdapter extends RecyclerView.Adapter<UpdateOrderRecyclerViewAdapter.ViewHolder> {

    List<UpdateOrderListModal> updateOrderListModals;
    Context mContext;

    public UpdateOrderRecyclerViewAdapter(List<UpdateOrderListModal> updateOrderListModals, Context mContext) {
        this.updateOrderListModals = updateOrderListModals;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.update_order_product_list,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.slTextView.setText(String.valueOf(position + 1));
        holder.productTextView.setText(updateOrderListModals.get(position).getProductName());
        holder.qtyTextView.setText(String.valueOf(updateOrderListModals.get(position).getQty()));
        holder.uomTextView.setText(updateOrderListModals.get(position).getUom());

        holder.editTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.qtyTextView.setEnabled(true);
            }
        });
    }

    @Override
    public int getItemCount() {
        return updateOrderListModals.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView slTextView,productTextView,qtyTextView,uomTextView,editTextView;

        public ViewHolder(View itemView){
            super(itemView);
            slTextView  = itemView.findViewById(R.id.sl_id_order);
            productTextView = itemView.findViewById(R.id.product_name_id_order);
            qtyTextView = itemView.findViewById(R.id.qty_id_order);
            uomTextView = itemView.findViewById(R.id.uom_id_order);
            editTextView = itemView.findViewById(R.id.edit_id_order);
        }
    }
}
