package com.sysnova.brac_seed_app.modal;

public class ViewProductDetailsModal {
    String productName;
    String kgQty;
    String Uom;
    String bagQty;
    String rate;
    String totalTaka;
    String dis_price, volume, bonus_qty;

    public ViewProductDetailsModal(String productName, String Uom, String bagQty, String rate, String totalTaka,String dis_price, String volume, String bonus_qty) {
        this.productName = productName;
        this.Uom=Uom;
        this.bagQty = bagQty;
        this.rate = rate;
        this.totalTaka = totalTaka;
        this.dis_price=dis_price;
        this.volume = volume;
        this.bonus_qty = bonus_qty;
    }

    public String getBonus_qty() {
        return bonus_qty;
    }

    public void setBonus_qty(String bonus_qty) {
        this.bonus_qty = bonus_qty;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getProductName() {
        return productName;
    }

    /*public String getKgQty() {
        return kgQty;
    }*/
    public String getUom() {
        return Uom;
    }

    public String getBagQty() {
        return bagQty;
    }

    public String getRate() {
        return rate;
    }

    public String getTotalTaka() {
        return totalTaka;
    }

    public String getdis_price() {
        //return dis_price.split("\\.")[0];
        return dis_price;
    }
}
