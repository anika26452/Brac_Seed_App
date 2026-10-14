package com.sysnova.brac_seed_app.modal;

public class OrderListModal {
    private String code,name, bonus;
    private double price,total, discount,qty;
    int id;
    public OrderListModal(int id, String code, String name, double qty, double price, double total, double discount, String bonus) {
        this.id=id;
        this.code = code;
        this.name = name;
        this.qty = qty;
        this.price=price;
        this.total = total;
        this.discount = discount;
        this.bonus = bonus;
    }

    public String getBonus() {
        return bonus;
    }

    public void setBonus(String bonus) {
        this.bonus = bonus;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getQty() {
        return qty;
    }

    public void setQty(double qty) {
        this.qty = qty;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }
}
