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

    private final List<Product> deletedProducts;

    private final List<Product> createdProducts;

    public Model(Context context) {
        this.dataProvider = new DataProvider(context);
        this.products = new ArrayList<Product>();
        this.deletedProducts = new ArrayList<Product>();
        this.createdProducts = new ArrayList<Product>();;
    }

    public void initProductsFromApi(ProductsCallback renderProductsList, Callback failure) {
        dataProvider.initProducts(renderProductsList, failure);
    }

    public void setProducts(List<Product> productsFromApi) {
        products.clear();
        products.addAll(productsFromApi);
    }

    public List<Product> getProducts() {
        return products;
    }

    public void createProduct(String name,ProductCallback success, ProductCallback failure) {
        dataProvider.createProduct(name, success, failure);
    }

    public void addProduct(Product p) {
        products.add(p);
    }


    public void putProduct(Product p, ProductCallback productCallback, ProductCallback onFailure) {
        dataProvider.update(p, productCallback, onFailure);
    }

    public void updateProduct(Product updatedProduct) {
        for(Product p: products) {
            if(p.getId() == updatedProduct.getId()) {
                p.setAmount(updatedProduct.getAmount());
                p.setDelta(updatedProduct.getDelta());
            }
        }
    }

    public void signin(String login, String password) {
        dataProvider.signin(login, password);
    }

    public void deleteProduct(Product p, ProductCallback deleteCallback, Callback onFailure) {
        dataProvider.deleteProduct(p, deleteCallback, onFailure);
    }

    public void removeProduct(Product p) {
        products.remove(p);
    }

    public void moveProductToTrash(Product p) {
        removeProduct(p);
        if(!createdProducts.remove(p)) {
            products.remove(p);
            deletedProducts.add(p);
        }
    }

    public List<Product> getDeletedProducts() {
        return deletedProducts;
    }

    public List<Product> getCreatedProducts() {
        return createdProducts;
    }

    public void addCreated(Product p) {
        this.createdProducts.add(p);
    }

    public void setDeletedProducts(List<Product> deletedProducts) {
        this.deletedProducts.addAll(deletedProducts);
    }

    public void setCreatedProducts(List<Product> createdProducts) {
        this.createdProducts.addAll(createdProducts);
    }

    public List<Product> getProductsToShowInUI() {

        List<Product> productsToRender = new ArrayList<Product>(
                products.size() + createdProducts.size()
        );

        productsToRender.addAll(products);
        productsToRender.addAll(createdProducts);

        return productsToRender;
    }

    public void synchronizeDeviceWithServer(ProductsCallback afterSynchronization) {

        List<Product> unsynchronizedProducts = new ArrayList<Product>();

        for (Product p: products) {
            if(p.getDelta() != 0) {
                unsynchronizedProducts.add(p);
            }
        }

        dataProvider.synchronizeDevice(
                unsynchronizedProducts, createdProducts, deletedProducts, afterSynchronization
        );
    }

    public void clearDeleted() {
        deletedProducts.clear();
    }

    public void clearCreated() {
        createdProducts.clear();
    }

}
