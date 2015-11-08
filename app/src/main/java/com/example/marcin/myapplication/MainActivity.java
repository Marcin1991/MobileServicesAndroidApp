package com.example.marcin.myapplication;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;

import com.example.marcin.myapplication.com.example.marcin.myapplication.domain.CreateProductCallback;
import com.example.marcin.myapplication.com.example.marcin.myapplication.domain.ProductCallback;
import com.example.marcin.myapplication.com.example.marcin.myapplication.domain.InitProductsCallback;
import com.example.marcin.myapplication.com.example.marcin.myapplication.domain.Model;
import com.example.marcin.myapplication.com.example.marcin.myapplication.domain.Product;

import java.util.List;

public class MainActivity extends Activity {

    private final Model model = new Model(this);

    final Context context = this;

    private ListView lstProducts;
    private Button btnNewProduct;

    private ArrayAdapter<Product> adapter ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lstProducts = (ListView) findViewById(R.id.listView);
        lstProducts.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                openManagmentDialog((Product) adapterView.getItemAtPosition(i));
            }
        });

        model.initProductsFromApi(new InitProductsCallback() {
            @Override
            public void apply(List<Product> products) {
                model.setProducts(products);
                adapter = new ArrayAdapter<Product>(context, R.layout.item, products);
                lstProducts.setAdapter(adapter);
            }
        });

        btnNewProduct = (Button) findViewById(R.id.btnNewProduct);
        btnNewProduct.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                final EditText txtName = (EditText)findViewById(R.id.newProductName);

                model.createProduct( txtName.getText().toString(), new CreateProductCallback() {
                            @Override
                            public void apply(Product p) {
                                adapter.add(p);
                                adapter.sort(new Product.CompareByAmount());
                                model.addProduct(p);
                                txtName.setText("");
                                txtName.clearFocus();
                                hideKeyboard();
                            }
                        }
                );
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
        String[] nums = new String[1001];
        for(int i=0; i<nums.length; i++) {
            nums[i] = Integer.toString(i);
        }

        np.setMinValue(0);
        np.setMaxValue(1000);
        np.setWrapSelectorWheel(false);
        np.setDisplayedValues(nums);
        np.setValue(p.getAmount());

        Button btnDelete = (Button) dialog.findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                model.deleteProduct(p, new ProductCallback() {
                    @Override
                    public void apply(Product p) {
                        adapter.remove(p);
                        model.removeProduct(p);
                        dialog.hide();
                    }
                });
            }
        });

        Button btnSave = (Button) dialog.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.remove(p);
                p.setAmount(np.getValue());
                adapter.add(p);
                adapter.sort(new Product.CompareByAmount());
                dialog.hide();
                model.putProduct(p, new ProductCallback() {
                    @Override
                    public void apply(Product p) {
                        model.updateProduct(p);
                    }
                });
            }
        });

        dialog.show();

        }
    }
