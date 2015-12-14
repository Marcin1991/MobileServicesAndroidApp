package com.example.marcin.myapplication.com.example.marcin.myapplication.domain;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Comparator;
import java.util.UUID;

/**
 * Created by marcin on 30.10.15.
 */
public class Product {

    private final String id;
    private final String name;
    private int amount;
    private int delta = 0;

    public Product(String name) {
        this(Product.getUniqeId(), name, 0);
    }

    public Product(String id, String name, int amount) {
        this.id = id;
        this.name = name;
        this.amount = amount;
    }

    public Product() {
        this(Product.getUniqeId(), "", 0);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getAmount() {return amount; }

    public int getTotalAmount() {return amount + delta; }

    public int getDelta() {
        return delta;
    }

    public void clearDelta() {
        this.delta = 0;
    }

    public void addDelta(int amount) {
        this.delta += amount;
    }

    public void addAmount(int amount) {
        this.amount += amount;
    }

    @Override
    public String toString() {
        return getName() + ' ' + '(' + getTotalAmount() + ')';
    }

    public static Product fromJsonToJava(JSONObject jsonProduct) throws JSONException {
        return new Product(
                jsonProduct.getString("id"),
                jsonProduct.getString("name"),
                jsonProduct.getInt("amount")
        );
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setDelta(int delta) {
        this.delta = delta;
    }

    public static class CompareByAmount implements Comparator<Product> {
        @Override
        public int compare(Product firstProduct, Product secondProduct) {
            return firstProduct.getTotalAmount() - secondProduct.getTotalAmount();
         }
    }

    public static String getUniqeId() {
        return UUID.randomUUID().toString();
    }
}
