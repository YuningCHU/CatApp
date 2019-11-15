package app.com.catapp;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FavoriteList {
    /**
     * An array of breed items.
     */
    public static final List<Breed> ITEMS = new ArrayList<Breed>();

    public static void addItem(Breed item) {
        Log.e("add fav", item.id);

        if(!ITEMS.contains(item)){
            ITEMS.add(item);
        }
    }

    public static void removeItem(Breed item) {
        ITEMS.remove(item);
    }

}
