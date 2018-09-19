package hu.ait.android.shoppinglist;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import hu.ait.android.shoppinglist.data.ShoppingItem;

@SuppressWarnings("ConstantConditions")
public class ShoppingCreateAndEditDialog extends DialogFragment {

    EditText itemName;
    Spinner itemType;
    EditText estimatedPrice;
    EditText itemDescription;
    CheckBox purchased;

    public interface ShoppingItemHandler {
        void onNewItemCreated(String itemName, String itemType, String estimatedPrice,
                              String itemDescription, boolean purchased);

        void onItemUpdated(ShoppingItem item, String oldPrice);
    }

    private ShoppingItemHandler shoppingItemHandler;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof ShoppingItemHandler) {
            shoppingItemHandler = (ShoppingItemHandler) context;
        } else {
            throw new RuntimeException(
                    getString(R.string.implementation_exception));
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        @SuppressLint("InflateParams") final View view = inflater.inflate(
                R.layout.dialog_add, null);

        populateSpinner(view);

        itemName = view.findViewById(R.id.etName);
        itemType = view.findViewById(R.id.spinner);
        estimatedPrice = view.findViewById(R.id.etPrice);
        itemDescription = view.findViewById(R.id.etDescription);
        purchased = view.findViewById(R.id.cbAlreadyPurchased);

        if (getArguments() != null && getArguments().containsKey(MainActivity.KEY_ITEM_TO_EDIT)) {
            setUpEditDialog(builder, view, itemName, itemType, estimatedPrice, itemDescription,
                    purchased);
        } else {
            setUpNewDialog(builder, view);
        }

        return builder.create();
    }

    @Override
    public void onResume() {
        super.onResume();
        final AlertDialog dialog = (AlertDialog) getDialog();
        if (dialog != null) {
            Button positiveButton = dialog.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!TextUtils.isEmpty(itemName.getText())) {
                        if (getArguments() != null && getArguments().
                                containsKey(MainActivity.KEY_ITEM_TO_EDIT)) {
                            ShoppingItem item = ((ShoppingItem) getArguments().getSerializable(
                                    MainActivity.KEY_ITEM_TO_EDIT));

                            String oldPrice = item.getEstimatedPrice();

                            item.setItemName(itemName.getText().toString());
                            item.setItemType(itemType.getSelectedItem().toString());
                            item.setEstimatedPrice(estimatedPrice.getText().toString());
                            item.setItemDescription(itemDescription.getText().toString());
                            item.setPurchased(purchased.isChecked());

                            shoppingItemHandler.onItemUpdated(item, oldPrice);
                            dialog.dismiss();
                        } else {
                            shoppingItemHandler.onNewItemCreated(itemName.getText().toString(),
                                    itemType.getSelectedItem().toString(),
                                    estimatedPrice.getText().toString(),
                                    itemDescription.getText().toString(),
                                    purchased.isChecked());
                            dialog.dismiss();
                        }
                    } else {
                        itemName.setError(getString(R.string.empty_string_error));
                    }
                }
            });
        }
    }

    @SuppressLint("DefaultLocale")
    private void setUpEditDialog(AlertDialog.Builder builder, View view,
                                 final EditText itemName,
                                 final Spinner itemType, final EditText estimatedPrice,
                                 final EditText itemDescription, final CheckBox purchased) {
        final ShoppingItem item = ((ShoppingItem) getArguments().getSerializable(
                MainActivity.KEY_ITEM_TO_EDIT));

        itemName.setText(item.getItemName());
        itemType.setSelection(getSpinnerItemIndex(itemType, item.getItemType()));
        estimatedPrice.setText(item.getEstimatedPrice());
        itemDescription.setText(item.getItemDescription());
        purchased.setChecked(item.isPurchased());

        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ShoppingCreateAndEditDialog.this.getDialog().cancel();
                    }
                });
    }

    private int getSpinnerItemIndex(Spinner spinner, String myString) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)) {
                return i;
            }
        }

        return 0;
    }

    private void setUpNewDialog(AlertDialog.Builder builder, View view) {
        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ShoppingCreateAndEditDialog.this.getDialog().cancel();
                    }
                });
    }

    private void populateSpinner(View view) {
        Spinner spinner = view.findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.categories_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

}
