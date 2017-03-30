package com.a461group5.utbuysell;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

// [START post_class]
@IgnoreExtraProperties
public class Category {

    public String name;
    public String description;
    public Map<String, Boolean> posts;

    public Category() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Category(String name, String description, String postId) {
        this.name = name.trim().substring(0, 1).toUpperCase() + name.trim().substring(1).toLowerCase();
        this.description = description;
        this.posts = new HashMap<>();
        this.posts.put(postId, true);
    }

    // [START post_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("description", description);
        result.put("posts", posts);

        return result;
    }
    // [END post_to_map]

}
// [END post_class]