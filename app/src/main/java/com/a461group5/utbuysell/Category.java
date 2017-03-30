package com.a461group5.utbuysell;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

// [START post_class]
@IgnoreExtraProperties
public class Category {

    // name field isn't needed because it will be the UID used
    public Map<String, Boolean> posts;

    public Category() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Category(String[] postIds) {
        this.posts = new HashMap<>();
        for (String postId : postIds) {
            this.posts.put(postId, true);
        }
    }

    // [START post_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("posts", posts);

        return result;
    }
    // [END post_to_map]

}
// [END post_class]