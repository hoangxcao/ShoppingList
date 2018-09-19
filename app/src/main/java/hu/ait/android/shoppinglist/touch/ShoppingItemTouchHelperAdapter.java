package hu.ait.android.shoppinglist.touch;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

public interface ShoppingItemTouchHelperAdapter {

    void onItemDismiss(int position, Context context);

    void onItemMove(int fromPosition, int toPosition);

}
