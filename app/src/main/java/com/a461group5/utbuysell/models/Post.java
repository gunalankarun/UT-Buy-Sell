package com.a461group5.utbuysell.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

// [START post_class]
@IgnoreExtraProperties
public class Post {

    public String title;
    public String queryTitle;
    public String description;
    public String status;
    public String seller;
    public String buyer;
    public float price;
    public Map<String, Boolean> categories;
    public Map<String, Boolean> imagePaths;

    public Post() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Post(String title, String description, String seller, float price, String[] categories) {
        this.title = title;
        this.queryTitle = title.toLowerCase();
        this.description = description;
        this.status = "Posted";
        this.seller = seller;
        this.buyer = "";
        this.price = price;

        this.categories = new HashMap<>();
        for (String c : categories) {
            String category = c.trim().toLowerCase();
            this.categories.put(category, true);
            this.queryTitle = this.queryTitle + " " + category;
        }
        this.imagePaths = new HashMap<>();

    }

    // [START post_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("title", title);
        result.put("queryTitle", queryTitle);
        result.put("description", description);
        result.put("status", status);
        result.put("seller", seller);
        result.put("buyer", buyer);
        result.put("price", price);
        result.put("categories", categories);
        result.put("imagePaths", imagePaths);

        return result;
    }
    // [END post_to_map]

    @Exclude
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Post)) return false;
        Post p = (Post) o;
        return title.equals(p.title) && description.equals(p.description) && seller.equals(p.seller)
                && price == p.price && categories.equals(p.categories);
    }

}
// [END post_class]