package com.sysnova.brac_seed_app.modal;

public class ApprovalOrderModal {
    String orderDoc;
    String erpDoc;
    String GrandTotal;
    String preparedBy;
    String salesStatus;
    String sentDate;
    public ApprovalOrderModal(String orderDoc, String erpDoc, String grandTotal, String preparedBy, String salesStatus,String sentDate) {
        this.orderDoc = orderDoc;
        this.erpDoc = erpDoc;
        GrandTotal = grandTotal;
        this.preparedBy = preparedBy;
        this.salesStatus = salesStatus;
        this.sentDate     = sentDate;
    }

    public String getOrderDoc() {
        return orderDoc;
    }

    public String getErpDoc() {
        return erpDoc;
    }

    public String getGrandTotal() {
        return GrandTotal;
    }

    public String getPreparedBy() {
        return preparedBy;
    }

    public String getSalesStatus() {
        return salesStatus;
    }

    public String getSentDate() {
        return sentDate;
    }
}
