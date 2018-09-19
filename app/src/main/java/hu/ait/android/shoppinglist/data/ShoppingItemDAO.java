package hu.ait.android.shoppinglist.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface ShoppingItemDAO {

    @Query("SELECT * FROM shoppingitem")
    List<ShoppingItem> getAll();

    @Insert
    long insertItem(ShoppingItem item);

    @Update
    void update(ShoppingItem item);

    @Delete
    void delete(ShoppingItem item);

    @Query("DELETE FROM shoppingitem")
    void deleteAll();
}
