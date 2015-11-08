package com.example.marcin.myapplication.com.example.marcin.myapplication.domain;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Comparator;

/**
 * Created by marcin on 30.10.15.
 */
public class Product {

    private final String name;
    private final String id;
    private int amount;

    public Product(String name, String id) {
        this(name, id, 0);
    }

    public Product(String id, String name, int amount) {
        this.name = name;
        this.id = id;
        this.amount = amount;
    }

    public Product() {
        this("", "", 0);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getAmount() {return amount; }

    @Override
    public String toString() {
        return name + ' ' + '(' + amount + ')';
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

    public static class CompareByAmount implements Comparator<Product> {
        @Override
        public int compare(Product firstProduct, Product secondProduct) {
            return firstProduct.getAmount() - secondProduct.getAmount();
        }
    }
}
