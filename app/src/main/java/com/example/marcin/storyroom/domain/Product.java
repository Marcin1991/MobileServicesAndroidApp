package com.example.marcin.storyroom.domain;

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
    private String GUID = "";
    //v2
    private double lon;
    private double lat;

    public Product(String name, double lon, double lat) {
        this(Product.getUniqeId(), name, 0, lon, lat);
    }

    public Product(String id, String name, int amount, double lon, double lat) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.lon = lon;
        this.lat = lat;
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
        if(GUID.isEmpty()) {
            GUID = getUniqeId();
        }
        this.delta += amount;
    }

    public void addAmount(int amount) {
        if(GUID.isEmpty()) {
            GUID = getUniqeId();
        }
        this.amount += amount;
    }

    @Override
    public String toString() {
        return getName() + ' ' + '(' + getTotalAmount() + ')';
    }

    public static Product fromJsonToJava(JSONObject jsonProduct) throws JSONException {

        System.out.println("P: "+jsonProduct.toString());

        return new Product(
                jsonProduct.getString("id"),
                jsonProduct.getString("name"),
                jsonProduct.has("amount") ? jsonProduct.getInt("amount") : 0,
                jsonProduct.has("lon") ? jsonProduct.getDouble("lon") : 0.0,
                jsonProduct.has("lat") ? jsonProduct.getDouble("lat") : 0.0
        );

    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setDelta(int delta) {
        this.delta = delta;
    }

    public double getLon() {
        return lon;
    }

    public double getLat() {
        return lat;
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
