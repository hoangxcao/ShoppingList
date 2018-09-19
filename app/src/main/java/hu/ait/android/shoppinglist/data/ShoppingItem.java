package hu.ait.android.shoppinglist.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.io.Serializable;

@Entity
public class ShoppingItem implements Serializable, Comparable<ShoppingItem> {

    @PrimaryKey(autoGenerate = true)
    private long shoppingItemId;

    @ColumnInfo(name = "item_name")
    private String itemName;
    @ColumnInfo(name = "item_type")
    private String itemType;
    @ColumnInfo(name = "estimated_price")
    private String estimatedPrice;
    @ColumnInfo(name = "item_description")
    private String itemDescription;
    @ColumnInfo(name = "purchased")
    private boolean purchased;

    public ShoppingItem(String itemName, String itemType, String estimatedPrice,
                        String itemDescription, boolean purchased) {
        this.itemName = itemName;
        this.itemType = itemType;
        this.estimatedPrice = estimatedPrice;
        this.itemDescription = itemDescription;
        this.purchased = purchased;
    }

    public long getShoppingItemId() {
        return shoppingItemId;
    }

    public void setShoppingItemId(long shoppingItemId) {
        this.shoppingItemId = shoppingItemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getEstimatedPrice() {
        return estimatedPrice;
    }

    public void setEstimatedPrice(String estimatedPrice) {
        this.estimatedPrice = estimatedPrice;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public boolean isPurchased() {
        return purchased;
    }

    public void setPurchased(boolean purchased) {
        this.purchased = purchased;
    }

    @Override
    public int compareTo(@NonNull ShoppingItem o) {
        return this.itemName.compareTo(o.getItemName());
    }

}
