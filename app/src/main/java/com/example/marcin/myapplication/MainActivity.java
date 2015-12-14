package com.example.marcin.myapplication;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;

import com.example.marcin.myapplication.com.example.marcin.myapplication.domain.Callback;
import com.example.marcin.myapplication.com.example.marcin.myapplication.domain.ProductCallback;
import com.example.marcin.myapplication.com.example.marcin.myapplication.domain.ProductsCallback;
import com.example.marcin.myapplication.com.example.marcin.myapplication.domain.Model;
import com.example.marcin.myapplication.com.example.marcin.myapplication.domain.Product;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private Model model;

    final Context context = this;

    private ListView lstProducts;
    private Button btnNewProduct;
    private Button btnSynchronize;

    private ArrayAdapter<Product> adapter ;

    public static final String PREFS_NAME = "StoryRoomPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        model = new Model(this);

        login();

        initClickOnProductsList();
        initProductsList();
        initNewProductBtn();
        initSynchronizeBtn();
    }

    private void initSynchronizeBtn() {

        btnSynchronize = (Button) findViewById(R.id.btnSynchronize);
        btnSynchronize.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                model.synchronizeDeviceWithServer(new ProductsCallback() {
                    @Override
                    public void apply(List<Product> products) {

                        model.clearDeleted();
                        model.clearCreated();
                        model.setProducts(products);

                    }
                });
            }
        });
    }

    private void initProductsList() {

        ProductsCallback onSuccess = new ProductsCallback() {
            @Override
            public void apply(List<Product> productsFromServer) {
                model.setProducts(productsFromServer);
                renderProductsList(
                        model.getProductsToShowInUI()
                );
            }
        };

        Callback onFailure = new Callback() {
            @Override
            public void apply() {
                Type listType = new TypeToken<ArrayList<Product>>(){}.getType();
                //products
                String productsJson = getSharedPreferences(PREFS_NAME, 0).getString("products", "[]");
                model.setProducts(
                        (List<Product>) new Gson().fromJson(productsJson, listType)
                );
                System.out.println("Products: "+model.getProducts());
                //deleted
                productsJson = getSharedPreferences(PREFS_NAME, 0).getString("deleted", "[]");
                model.setDeletedProducts(
                        (List<Product>) new Gson().fromJson(productsJson, listType)
                );
                System.out.println("Usunięte: " + model.getDeletedProducts());
                //created
                productsJson = getSharedPreferences(PREFS_NAME, 0).getString("created", "[]");
                model.setCreatedProducts(
                        (List<Product>) new Gson().fromJson(productsJson, listType)
                );
                System.out.println("Nowe: " + model.getCreatedProducts());
                //render UI list
                renderProductsList(
                        model.getProductsToShowInUI()
                );
            }
        };

        model.initProductsFromApi(onSuccess, onFailure);
    }

    private void renderProductsList(List<Product> products) {
        model.setProducts(products);
        adapter = new ArrayAdapter<Product>(context, R.layout.item, products);
        lstProducts.setAdapter(adapter);
    }

    private void login() {
        Intent i = getIntent();
        model.signin(
                i.getStringExtra("login"),
                i.getStringExtra("password")
        );
    }

    private void initClickOnProductsList() {
        lstProducts = (ListView) findViewById(R.id.listView);
        lstProducts.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                openManagmentDialog((Product) adapterView.getItemAtPosition(i));
            }
        });
    }

    private void initNewProductBtn() {
        btnNewProduct = (Button) findViewById(R.id.btnNewProduct);
        btnNewProduct.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                final EditText txtName = (EditText) findViewById(R.id.newProductName);

                final ProductCallback onSuccess = new ProductCallback() {
                    @Override
                    public void apply(Product p) {
                        adapter.add(p);
                        adapter.sort(new Product.CompareByAmount());
                        model.addProduct(p);
                    }
                };

                final ProductCallback onFailure = new ProductCallback() {
                    @Override
                    public void apply(Product p) {
                        adapter.add(p);
                        adapter.sort(new Product.CompareByAmount());
                        model.addCreated(p);
                    }
                };

                model.createProduct(txtName.getText().toString(), onSuccess, onFailure);

                txtName.setText("");
                txtName.clearFocus();
                hideKeyboard();

            }
        });
    }

    private void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void openManagmentDialog(final Product p) {

        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_dialog);
        dialog.setTitle(p.toString());
        final NumberPicker np = (NumberPicker) dialog.findViewById(R.id.npAmount);
        initNumberPicker(p, np);
        initDeleteBtn(p, dialog);
        initSaveBtn(p, dialog, np);
        dialog.show();

    }

    private void initNumberPicker(Product p, NumberPicker np) {
        String[] nums = new String[1001];
        for(int i=0; i<nums.length; i++) {
            nums[i] = Integer.toString(i);
        }
        np.setMinValue(0);
        np.setMaxValue(1000);
        np.setWrapSelectorWheel(false);
        np.setDisplayedValues(nums);
        np.setValue(p.getTotalAmount());
    }

    private void initSaveBtn(final Product p, final Dialog dialog, final NumberPicker np) {

        Button btnSave = (Button) dialog.findViewById(R.id.btnSave);

        final ProductCallback onSuccess = new ProductCallback() {
            @Override
            public void apply(Product p) {
                p.setAmount(np.getValue());
                model.updateProduct(p);
                adapter.remove(p);
                adapter.add(p);
                adapter.sort(new Product.CompareByAmount());
            }
        };

        final ProductCallback onFailure = new ProductCallback() {
            @Override
            public void apply(Product p) {
                p.addDelta(
                        np.getValue() - p.getTotalAmount()
                );
                model.updateProduct(p);
                adapter.remove(p);
                adapter.add(p);
                adapter.sort(new Product.CompareByAmount());
            }
        };

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                model.putProduct(p, onSuccess, onFailure);
                dialog.hide();
            }
        });

    }

    private void initDeleteBtn(final Product p, final Dialog dialog) {

        Button btnDelete = (Button) dialog.findViewById(R.id.btnDelete);

        final Callback onFailure = new Callback() {
            @Override
            public void apply() {
                model.moveProductToTrash(p);
            }
        };

        final ProductCallback onSuccess = new ProductCallback() {
            @Override
            public void apply(Product p) {
                model.removeProduct(p);
            }
        };

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.remove(p);
                dialog.hide();
                model.deleteProduct(p, onSuccess, onFailure);
            }
        });
    }

    @Override
    public void onStop() {

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.remove("products");
        editor.putString("products", new Gson().toJson(model.getProducts()));
        editor.remove("deleted");
        editor.putString("deleted", new Gson().toJson(model.getDeletedProducts()));
        editor.remove("created");
        editor.putString("created", new Gson().toJson(model.getCreatedProducts()));
        editor.commit();

        super.onStop();
    }

    }
