package hu.ait.android.shoppinglist;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TextView;

import hu.ait.android.shoppinglist.adapter.ShoppingListRecyclerAdapter;

public class ClearAlertDialog extends DialogFragment {

    ShoppingListRecyclerAdapter shoppingListRecyclerAdapter;

    public ClearAlertDialog() {
    }

    @SuppressLint("ValidFragment")
    public ClearAlertDialog(ShoppingListRecyclerAdapter shoppingListRecyclerAdapter) {
        this.shoppingListRecyclerAdapter = shoppingListRecyclerAdapter;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.confirm_clear);
        // Set up the text
        final TextView alert = new TextView(getActivity());
        alert.setGravity(Gravity.CENTER_VERTICAL);
        alert.setPadding(65, 65, 65, 65);
        alert.setText(R.string.clear_alert);
        builder.setView(alert);
        // Set up the buttons
        builder.setPositiveButton(R.string.clear, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                shoppingListRecyclerAdapter.clearList();
            }
        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        return builder.create();
    }

}
