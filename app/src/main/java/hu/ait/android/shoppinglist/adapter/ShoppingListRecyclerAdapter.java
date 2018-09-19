package hu.ait.android.shoppinglist.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;

import java.util.Collections;
import java.util.List;

import hu.ait.android.shoppinglist.MainActivity;
import hu.ait.android.shoppinglist.R;
import hu.ait.android.shoppinglist.data.AppDatabase;
import hu.ait.android.shoppinglist.data.ShoppingItem;
import hu.ait.android.shoppinglist.touch.ShoppingItemTouchHelperAdapter;

public class ShoppingListRecyclerAdapter extends RecyclerView.Adapter<ShoppingListRecyclerAdapter.
        ViewHolder> implements ShoppingItemTouchHelperAdapter {

    private List<ShoppingItem> shoppingList;
    private Context context;

    public ShoppingListRecyclerAdapter(List<ShoppingItem> items, Context context) {
        shoppingList = items;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewRow = LayoutInflater.from(parent.getContext()).inflate(R.layout.shopping_item_row,
                parent, false);

        return new ViewHolder(viewRow);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        String itemType = shoppingList.get(holder.getAdapterPosition()).getItemType();
        switch (itemType) {
            case "Asians":
                holder.imIcon.setImageResource(R.drawable.asians);
                break;
            case "Babies":
                holder.imIcon.setImageResource(R.drawable.babies);
                break;
            case "Bakery":
                holder.imIcon.setImageResource(R.drawable.bakery);
                break;
            case "Beauty and Health":
                holder.imIcon.setImageResource(R.drawable.beauty_and_health);
                break;
            case "Books":
                holder.imIcon.setImageResource(R.drawable.books);
                break;
            case "Cans and Jars":
                holder.imIcon.setImageResource(R.drawable.cans_and_jars);
                break;
            case "Chocolates and Snacks":
                holder.imIcon.setImageResource(R.drawable.chocolates_and_snacks);
                break;
            case "Dairy and Eggs":
                holder.imIcon.setImageResource(R.drawable.dairy_and_eggs);
                break;
            case "Drinks":
                holder.imIcon.setImageResource(R.drawable.drinks);
                break;
            case "Fresh Food":
                holder.imIcon.setImageResource(R.drawable.fresh_food);
                break;
            case "Gluten Free":
                holder.imIcon.setImageResource(R.drawable.gluten_free);
                break;
            case "Household":
                holder.imIcon.setImageResource(R.drawable.household);
                break;
            case "Packets and Cereals":
                holder.imIcon.setImageResource(R.drawable.packets_and_cereals);
                break;
            case "Pets":
                holder.imIcon.setImageResource(R.drawable.pets);
                break;
        }

        holder.tvName.setText(shoppingList.get(holder.getAdapterPosition()).getItemName());
        holder.tvPrice.setText(shoppingList.get(holder.getAdapterPosition()).getEstimatedPrice());
        holder.cbPurchased.setChecked(shoppingList.get(holder.getAdapterPosition()).isPurchased());
        holder.btnDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, shoppingList.get(holder.getAdapterPosition()).
                        getItemDescription(), Toast.LENGTH_LONG).show();
            }
        });
        holder.swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
        holder.swipeLayout.addDrag(SwipeLayout.DragEdge.Left, holder.bottom_wrapper_edit);
        holder.swipeLayout.addDrag(SwipeLayout.DragEdge.Right, holder.bottom_wrapper_delete);
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) context).editShoppingItem(shoppingList.get(
                        holder.getAdapterPosition()));
            }
        });
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) context).deleteItemPrice(shoppingList.get(
                        holder.getAdapterPosition()));
                onItemDismiss(holder.getAdapterPosition(), context);
            }
        });
    }

    @Override
    public int getItemCount() {
        return shoppingList.size();
    }

    public void addItem(ShoppingItem item) {
        shoppingList.add(item);
        notifyDataSetChanged();
    }

    @Override
    public void onItemDismiss(final int position, final Context context) {
        final ShoppingItem item = shoppingList.remove(position);
        new Thread() {
            @Override
            public void run() {
                AppDatabase.getAppDatabase(context).shoppingItemDao().delete(item);
            }
        }.start();
        notifyItemRemoved(position);
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(shoppingList, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(shoppingList, i, i - 1);
            }
        }

        notifyItemMoved(fromPosition, toPosition);
    }

    public void updateItem(ShoppingItem item) {
        int editPos = findItemIndexByItemId(item.getShoppingItemId());
        shoppingList.set(editPos, item);
        notifyItemChanged(editPos);
    }

    private int findItemIndexByItemId(long itemId) {
        for (int i = 0; i < shoppingList.size(); i++) {
            if (shoppingList.get(i).getShoppingItemId() == itemId) {
                return i;
            }
        }

        return -1;
    }

    public void sortList() {
        Collections.sort(shoppingList);
        notifyDataSetChanged();
    }

    public void clearList() {
        shoppingList.clear();
        new Thread() {
            @Override
            public void run() {
                AppDatabase.getAppDatabase(context).shoppingItemDao().deleteAll();
            }
        }.start();
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imIcon;
        private TextView tvName;
        private TextView tvPrice;
        private CheckBox cbPurchased;
        private Button btnDetails;
        private SwipeLayout swipeLayout;
        private LinearLayout bottom_wrapper_edit;
        private LinearLayout bottom_wrapper_delete;
        private Button edit;
        private Button delete;

        ViewHolder(View itemView) {
            super(itemView);

            imIcon = itemView.findViewById(R.id.imIcon);
            tvName = itemView.findViewById(R.id.tvName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            cbPurchased = itemView.findViewById(R.id.cbPurchased);
            btnDetails = itemView.findViewById(R.id.btnDetails);
            swipeLayout = itemView.findViewById(R.id.swipeLayout);
            bottom_wrapper_edit = itemView.findViewById(R.id.bottom_wrapper_edit);
            bottom_wrapper_delete = itemView.findViewById(R.id.bottom_wrapper_delete);
            edit = itemView.findViewById(R.id.edit);
            delete = itemView.findViewById(R.id.delete);
        }
    }

}
