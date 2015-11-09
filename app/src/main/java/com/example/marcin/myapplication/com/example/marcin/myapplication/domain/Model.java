package com.example.marcin.myapplication.com.example.marcin.myapplication.domain;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by marcin on 07.11.15.
 */
public class Model {

    private final DataProvider dataProvider;
    private String login = "";
    private boolean isAuthenticated = false;

    private final List<Product> products;

    public Model(Context context) {
        this.dataProvider = new DataProvider(context);
        this.products = new ArrayList<Product>();
    }

    public void initProductsFromApi(InitProductsCallback renderProductsList) {
        dataProvider.initProducts(renderProductsList);
    }

    public void setProducts(List<Product> productsFromApi) {
        products.clear();
        products.addAll(productsFromApi);
    }

    public List<Product> getProducts() {
        return products;
    }

    public void createProduct(String name,CreateProductCallback createProduct) {
        dataProvider.createProduct(name, createProduct);
    }

    public void addProduct(Product p) {
        products.add(p);
    }

    public void deleteProduct(Product p, ProductCallback deleteProductCallback) {
        dataProvider.deleteProduct(p, deleteProductCallback);
    }

    public void removeProduct(Product p) {
        products.remove(p);
    }

    public void putProduct(Product p, ProductCallback productCallback) {
        dataProvider.update(p, productCallback);
    }

    public void updateProduct(Product updatedProduct) {
        for(Product p: products) {
            if(p.getId() == updatedProduct.getId()) {
                p.setAmount(updatedProduct.getAmount());
            }
        }
    }

    public void signin(String login, String password) {
        dataProvider.signin(login, password);
    }
}
