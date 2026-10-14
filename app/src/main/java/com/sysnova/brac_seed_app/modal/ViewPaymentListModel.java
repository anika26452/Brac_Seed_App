package com.sysnova.brac_seed_app.modal;

public class ViewPaymentListModel {
    String orderno;
    String date;
    String total;
    String bpname;
    String docstatus;
    String bank_name;

    public ViewPaymentListModel(String orderno, String date, String total, String bpname, String docstatus, String bank_name) {
        this.bpname=bpname;
        this.orderno = orderno;
        this.date = date;
        this.total = total;
        this.docstatus=docstatus;
        this.bank_name = bank_name;
    }

    public String getBpname() {
        return bpname;
    }

    public void setBpname(String bpname) {
        this.bpname = bpname;
    }

    public String getBank_name() {
        return bank_name;
    }

    public void setBank_name(String bank_name) {
        this.bank_name = bank_name;
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
