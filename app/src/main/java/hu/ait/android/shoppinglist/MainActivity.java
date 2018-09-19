package hu.ait.android.shoppinglist;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import hu.ait.android.shoppinglist.adapter.ShoppingListRecyclerAdapter;
import hu.ait.android.shoppinglist.data.AppDatabase;
import hu.ait.android.shoppinglist.data.ShoppingItem;
import hu.ait.android.shoppinglist.touch.ShoppingItemTouchHelperCallback;
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;

public class MainActivity extends AppCompatActivity implements
        ShoppingCreateAndEditDialog.ShoppingItemHandler {

    public static final String KEY_ITEM_TO_EDIT = "KEY_ITEM_TO_EDIT";
    private ShoppingListRecyclerAdapter shoppingListRecyclerAdapter;
    public double totalPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNewShoppingItemDialog();
            }
        });

        if (isFirstRun()) {
            showAddButtonTutorial();
        }

        RecyclerView recyclerView = findViewById(R.id.recyclerShopping);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        initShoppingItem(recyclerView);

        saveThatItWasStarted();
    }

    private void showAddButtonTutorial() {
        new MaterialTapTargetPrompt.Builder(MainActivity.this)
                .setTarget(findViewById(R.id.fab))
                .setPrimaryText(R.string.add_button_primary_text)
                .setSecondaryText(R.string.add_button_secondary_text)
                .show();
    }

    public boolean isFirstRun() {
        return PreferenceManager.getDefaultSharedPreferences(this).
                getBoolean("KEY_FIRST", true);
    }

    public void saveThatItWasStarted() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("KEY_FIRST", false);
        editor.apply();
    }

    public void initShoppingItem(final RecyclerView recyclerView) {
        new Thread() {
            @Override
            public void run() {
                final List<ShoppingItem> items =
                        AppDatabase.getAppDatabase(MainActivity.this).shoppingItemDao().getAll();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        shoppingListRecyclerAdapter = new ShoppingListRecyclerAdapter(items,
                                MainActivity.this);
                        recyclerView.setAdapter(shoppingListRecyclerAdapter);

                        final ShoppingItemTouchHelperCallback callback =
                                new ShoppingItemTouchHelperCallback(shoppingListRecyclerAdapter);
                        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
                        touchHelper.attachToRecyclerView(recyclerView);
                    }
                });

                for (int i = 0; i < items.size(); i++) {
                    String itemPrice = items.get(i).getEstimatedPrice();
                    if (!TextUtils.isEmpty(itemPrice)) {
                        totalPrice += Double.parseDouble(itemPrice);
                    }
                }
            }
        }.start();
    }

    private void showNewShoppingItemDialog() {
        new ShoppingCreateAndEditDialog().show(getSupportFragmentManager(),
                "ShoppingCreateAndEditDialog");
    }

    @Override
    public void onNewItemCreated(final String itemName, final String itemType,
                                 final String estimatedPrice, final String itemDescription,
                                 final boolean purchased) {
        new Thread() {
            @Override
            public void run() {
                final ShoppingItem newItem = new ShoppingItem(itemName, itemType, estimatedPrice,
                        itemDescription, purchased);

                long id = AppDatabase.getAppDatabase(MainActivity.this).
                        shoppingItemDao().insertItem(newItem);
                newItem.setShoppingItemId(id);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        shoppingListRecyclerAdapter.addItem(newItem);
                    }
                });

                String itemPrice = newItem.getEstimatedPrice();
                if (!TextUtils.isEmpty(itemPrice)) {
                    totalPrice += Double.parseDouble(itemPrice);
                }
            }
        }.start();
    }

    @Override
    public void onItemUpdated(final ShoppingItem item, final String oldPrice) {
        new Thread() {
            @Override
            public void run() {
                AppDatabase.getAppDatabase(MainActivity.this).shoppingItemDao().update(item);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        shoppingListRecyclerAdapter.updateItem(item);
                    }
                });

                String itemPrice = item.getEstimatedPrice();
                if (!TextUtils.isEmpty(oldPrice)) {
                    totalPrice -= Double.parseDouble(oldPrice);
                }
                if (!TextUtils.isEmpty(itemPrice)) {
                    totalPrice += Double.parseDouble(itemPrice);
                }
            }
        }.start();
    }

    public void editShoppingItem(ShoppingItem item) {
        ShoppingCreateAndEditDialog editDialog = new ShoppingCreateAndEditDialog();

        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_ITEM_TO_EDIT, item);
        editDialog.setArguments(bundle);

        editDialog.show(getSupportFragmentManager(), "ShoppingEditDialog");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_price) {
            Toast.makeText(MainActivity.this, getString(R.string.total_price)+
                    totalPrice, Toast.LENGTH_LONG).show();
            return true;
        }

        if (id == R.id.action_sort) {
            shoppingListRecyclerAdapter.sortList();
            return true;
        }

        if (id == R.id.action_clear) {
            totalPrice = 0;
            ClearAlertDialog clearAlertDialog = new ClearAlertDialog(shoppingListRecyclerAdapter);
            clearAlertDialog.show(getFragmentManager(), "ClearAlertDialog");
            return true;
        }

        return onOptionsItemSelected(item);
    }

    public void deleteItemPrice(final ShoppingItem item) {
        new Thread() {
            @Override
            public void run() {
                String itemPrice = item.getEstimatedPrice();
                if (!TextUtils.isEmpty(itemPrice)) {
                    totalPrice -= Double.parseDouble(itemPrice);
                }
            }
        }.start();
    }
}
