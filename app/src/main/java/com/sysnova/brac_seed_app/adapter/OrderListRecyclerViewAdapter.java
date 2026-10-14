package com.sysnova.brac_seed_app.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sysnova.brac_seed_app.Activity.*;

import com.sysnova.brac_seed_app.modal.OrderListModal;
import com.sysnova.brac_seed_app.R;

import java.text.DecimalFormat;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

public class OrderListRecyclerViewAdapter extends RecyclerView.Adapter<OrderListRecyclerViewAdapter.ViewHolder>{
    DatabaseHelper databaseHelper;
    private List<OrderListModal> medium;
    private Context context;
    private static final DecimalFormat decfor = new DecimalFormat("0.00");
    private static final DecimalFormat decforthree = new DecimalFormat("0.000");

    public OrderListRecyclerViewAdapter( List<OrderListModal> medium, Context context) {
        //this.orderListActivities = orderListActivities;
        this.medium = medium;
        this.context = context;
        databaseHelper = new DatabaseHelper(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.order_list_temp, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holders, int position) {
        final ViewHolder holder = holders;
        OrderListModal model = medium.get(position);

            holder.name.setText(" "+model.getName());
            holder.qty.setText(String.valueOf(decfor.format(model.getQty())));
            holder.priceRate.setText(String.valueOf(decfor.format(model.getPrice())));
            holder.total.setText(String.valueOf(decfor.format(model.getTotal())));
            holder.serial.setText(String.valueOf(position+1)+".");
            holder.disc.setText(String.valueOf(decfor.format(model.getDiscount())));
            holder.totalwithdiscount.setText(String.valueOf(decfor.format(model.getTotal()-model.getDiscount())));
            holder.bonus.setText(String.valueOf(model.getBonus()));
            holder.cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseHelper.deleteOneProductOrderLine(model.getId());
                int newPosition = holder.getAbsoluteAdapterPosition();
                medium.remove(newPosition);
                notifyItemRemoved(newPosition);
                notifyItemRangeChanged(newPosition, medium.size());
                double grandTotal=getTotal(medium);
                double totalQty=getTotaQty(medium);
                double totalCommission = getTotaCommission(medium);
                double totalwithcomm = getTotalWithComm(medium);
                int totalbonus = getTotalbonus(medium);

                Intent intent = new Intent("custom-message");
                intent.putExtra("TOTAL",grandTotal);
                intent.putExtra("TOTALQTY",totalQty);
                intent.putExtra("TOTALCOMM", totalCommission);
                intent.putExtra("tOTALWITHCOMM", totalwithcomm);
                intent.putExtra("totalbonussum", totalbonus);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }
        });
        //2022-12-03
        holder.qty.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                int gottenQTY = 0;
                Boolean isNumber;

                try{
                    int n = Integer.parseInt(holder.qty.getText().toString());
                    gottenQTY = Integer.parseInt(holder.qty.getText().toString());
                    isNumber = true;
                }
                catch (Exception e){
                    isNumber = false;
                    toast("Quantity Must be Numeric/Without Fraction!");
                    return;
                }
                double gottenTotal = Double.parseDouble(holder.total.getText().toString());

                if (gottenQTY == 1) {
                    toast("Minimum 1 unit must be ordered");
                    return;
                } else {
                    gottenTotal = (Double)(model.getPrice()*gottenQTY);
                    if (databaseHelper.updateProductOrderLine(model.getId(), gottenQTY, gottenTotal)) {
                        holder.qty.setText(String.valueOf(gottenQTY));
                        holder.total.setText(String.valueOf(gottenTotal)); //gottenTotal));
                        Intent intent = new Intent("custom-message");
                        loadArrayList();
                        intent.putExtra("TOTAL", getTotal(medium));
                        intent.putExtra("TOTALQTY", getTotaQty(medium));
                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                    } else {
                        toast("Could not update !");
                    }
                }
            }
        });
    }
    public void loadArrayList(){
        medium.clear();
        int id=0,qty=0;
        String code="",name="", bonus = "";
        double price=0.00,total=0.00, discount;
        Cursor data = databaseHelper.getProductOrderLine(LoginActivity.ad_user_id);
        if(data.moveToFirst()){
            do{
                id= data.getInt(0);
                code=data.getString(1);
                name=data.getString(2);
                qty=data.getInt(3);
                price=data.getDouble(4);
                total=data.getDouble(5);
                discount = data.getDouble(12);
                bonus = data.getString(13);
                OrderListModal orderListModal = new OrderListModal(id,code,name,qty,price,total, discount, bonus);
                medium.add(orderListModal);
            }while(data.moveToNext());
        }
    }
    public double getTotal(List<OrderListModal> list){
        double total=0;
        for(int i=0;i<list.size();i++){
            total=total+list.get(i).getTotal();
        }
        return total;
    }
    public double getTotaQty(List<OrderListModal> list){
        double total=0;
        for(int i=0;i<list.size();i++){
            total=total+list.get(i).getQty();
        }
        return total;
    }
    public int getTotalbonus(List<OrderListModal> list){
        int total=0;
        for(int i=0;i<list.size();i++){
            total=total+Integer.parseInt(list.get(i).getBonus());
        }
        return total;
    }
    public double getTotaCommission(List<OrderListModal> list){
        double totalcomm=0;
        for(int i=0;i<list.size();i++){
            totalcomm=totalcomm+list.get(i).getDiscount();
        }
        return totalcomm;
    }

    public double getTotalWithComm(List<OrderListModal> list){
        double totalcomm=0;
        for(int i=0;i<list.size();i++){
            totalcomm=totalcomm+(list.get(i).getTotal()-list.get(i).getDiscount());
        }
        return totalcomm;
    }
    void toast(String msg)
    {
        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
    }

    @Override
    public int getItemCount() {
        return medium.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView cross;
        TextView disc,name,total,serial,priceRate,qty, totalwithdiscount, bonus;
      //  EditText qty;
        public ViewHolder(View itemView){
            super(itemView);

            name = itemView.findViewById(R.id.nameTV);
            qty = itemView.findViewById(R.id.qtyTV);
            total = itemView.findViewById(R.id.totalTV);
            priceRate = itemView.findViewById(R.id.priceTV);
            serial = itemView.findViewById(R.id.serial);
            cross=(ImageView)itemView.findViewById(R.id.crossTV);
            disc=itemView.findViewById(R.id.discounttv);
            totalwithdiscount = itemView.findViewById(R.id.totalwithdiscounttv);
            bonus = itemView.findViewById(R.id.bonustv);
        }
    }
}
