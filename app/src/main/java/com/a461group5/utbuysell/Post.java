package com.a461group5.utbuysell;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

// [START post_class]
@IgnoreExtraProperties
public class Post {

    public String title;
    public String description;
    public String status;
    public String seller;
    public String buyer;
    public int price;
    public Map<String, Boolean> categories;

    public Post() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Post(String title, String description, String seller, int price, String[] categories) {
        this.title = title;
        this.description = description;
        this.status = "Posted";
        this.seller = seller;
        this.buyer = "";
        this.price = price;
        this.categories = new HashMap<>();
        for (String c : categories) {
            String category = c.trim().toLowerCase();
            this.categories.put(category, true);
        }
    }

    // [START post_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("title", title);
        result.put("description", description);
        result.put("status", status);
        result.put("seller", seller);
        result.put("buyer", buyer);
        result.put("price", price);
        result.put("categories", categories);

        return result;
    }
    // [END post_to_map]

}
// [END post_class]