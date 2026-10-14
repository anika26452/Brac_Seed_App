package com.sysnova.brac_seed_app.modal;

public class ViewOrderListModel {
    String orderno;
    String date;
    String total;
    String bpname;
    String docstatus;

    public ViewOrderListModel(String orderno, String date, String total, String bpname, String docstatus) {
        this.bpname=bpname;
        this.orderno = orderno;
        this.date = date;
        this.total = total;
        this.docstatus=docstatus;
    }
    public String getBPname() {
        return bpname;
    }

    public void setBPname(String bpname) {
        this.bpname = bpname;
    }

    public String getOrderno() {
        return orderno;
    }

    public void setOrderno(String productID) {
        this.orderno = productID;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDocstatus() {
        return docstatus;
    }

    public void setDocstatus(String docstatus) {
        this.docstatus = docstatus;
    }


}
