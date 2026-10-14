package com.sysnova.brac_seed_app.modal;

public class UpdateOrderListModal {

    int id;
    String productId;
    String productName;
    String uom;
    double qty;

    public UpdateOrderListModal(int id, String productId, String productName, String uom, double qty) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.uom = uom;
        this.qty = qty;
    }

    public int getId() {
        return id;
    }

    public String getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public String getUom() {
        return uom;
    }

    public double getQty() {
        return qty;
    }
}
