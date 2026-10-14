package com.sysnova.brac_seed_app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sysnova.brac_seed_app.Activity.DatabaseHelper;
import com.sysnova.brac_seed_app.R;
import com.sysnova.brac_seed_app.modal.OrderListModal;
import com.sysnova.brac_seed_app.modal.ViewMortalityListModel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


public class ShowCostCenter extends RecyclerView.Adapter<ShowCostCenter.ViewHolder> {
    List<ViewMortalityListModel> orderListActivities;
    Context mContext;
    DatabaseHelper mdatabashelper;

    public ShowCostCenter(ArrayList<ViewMortalityListModel> orderListActivities, Context mContext) {
        this.orderListActivities = orderListActivities;
        this.mContext = mContext;
        mdatabashelper = new DatabaseHelper(mContext);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.show_mortality_list_temp,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.pcodevw.setText(orderListActivities.get(position).getCost_center());
        holder.pnamevw.setText(orderListActivities.get(position).getBpname());
        holder.qtyvw.setText(String.valueOf(orderListActivities.get(position).getPaidamt()));
        holder.product_delete_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mdatabashelper.deleteOneMortality(orderListActivities.get(position).getId());
                int newPosition = holder.getAbsoluteAdapterPosition();
                orderListActivities.remove(newPosition);
                notifyItemRemoved(newPosition);
                notifyItemRangeChanged(newPosition, orderListActivities.size());
            //    BigDecimal grandTotal=getTotal(orderListActivities);
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderListActivities.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView pcodevw,pnamevw,qtyvw;
        LinearLayout tableRow;
        ImageView product_delete_img;
        public ViewHolder(View itemView){
            super(itemView);
            tableRow         = itemView.findViewById(R.id.orderListRow);
            pcodevw = itemView.findViewById(R.id.pcode);
            pnamevw = itemView.findViewById(R.id.pname);
            qtyvw  = itemView.findViewById(R.id.qty);
            product_delete_img = itemView.findViewById(R.id.product_delete_img);
        }
    }

    public BigDecimal getTotal(List<ViewMortalityListModel> list){

        BigDecimal total= BigDecimal.valueOf(0);

        for(int i=0;i<list.size();i++){
         //   total=total.add(list.get(i).getTotal());
        }
        return total;
    }
}
