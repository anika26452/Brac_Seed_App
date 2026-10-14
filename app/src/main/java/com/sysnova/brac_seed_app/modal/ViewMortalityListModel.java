package com.sysnova.brac_seed_app.modal;

import java.math.BigDecimal;

public class ViewMortalityListModel {
    String cost_center, bpname,paidamt;
    int id;
    //BigDecimal total;

    public ViewMortalityListModel(String cost_center, String bpname,String paidamt, int id) {
        this.cost_center = cost_center;
        this.bpname = bpname;
        this.paidamt = paidamt;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCost_center() {
        return cost_center;
    }

    public void setCost_center(String cost_center) {
        this.cost_center = cost_center;
    }

    public String getBpname() {
        return bpname;
    }

    public void setBpname(String bpname) {
        this.bpname = bpname;
    }

    public String getPaidamt() {
        return paidamt;
    }

    public void setPaidamt(String paidamt) {
        this.paidamt = paidamt;
    }
}
