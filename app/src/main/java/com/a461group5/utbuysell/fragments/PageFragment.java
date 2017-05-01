package com.a461group5.utbuysell.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.a461group5.utbuysell.MessageActivity;
import com.a461group5.utbuysell.R;
import com.a461group5.utbuysell.adapters.ListingsAdapter;
import com.a461group5.utbuysell.models.InboxEntry;
import com.a461group5.utbuysell.models.Post;
import com.a461group5.utbuysell.models.User;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.a461group5.utbuysell.R.id.usersListView;

/**
 *
 */
public class PageFragment extends Fragment {

    //variables for Transaction
    private RecyclerView recyclerViewTrans;
    private RecyclerView.LayoutManager layoutManagerTrans;
    private ArrayList<Post> allPosts = new ArrayList<Post>();
    private ArrayList<String> allKeys = new ArrayList<String>();
    String pID;
    //private FrameLayout fragmentContainer;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    //private variables used for inbox fragment////////
    private DatabaseReference chatRef;
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private ValueEventListener mInboxListener;
    private ArrayAdapter<InboxEntry> namesArrayAdapter;
    private ArrayList<InboxEntry> inboxEntries;
    private ListView inboxListView;
    private Context inboxContext;
    ArrayList<Post> itemsData = new ArrayList<Post>();
    ArrayList<String> keyData = new ArrayList<String>();
    //////////////////////////////////////////////////
    EditText searchQuery;
    TextView ListingsHeader;
    TextView TransListingsHeader;

    private enum Type {
        LISTINGS, TRANSACTIONS, INBOX, PROFILE
    }

    Type myType;

    /**
     * Create a new instance of the fragment
     */
    public static PageFragment newInstance(int index) {
        PageFragment fragment = new PageFragment();
        Bundle b = new Bundle();
        b.putInt("index", index);
        fragment.setArguments(b);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getArguments().getInt("index", 0) == 3) {
            myType = Type.PROFILE;
            View view = inflater.inflate(R.layout.fragment_profile, container, false);
            initProfile(view);
            return view;
        } else if (getArguments().getInt("index", 0) == 2) {
            myType = Type.INBOX;
            View view = inflater.inflate(R.layout.fragment_inbox, container, false);
            initInbox(view);
            return view;
        } else if (getArguments().getInt("index", 0) == 1) {
            myType = Type.TRANSACTIONS;
            View view = inflater.inflate(R.layout.fragment_transactions, container, false);
            initTransactions(view);
            return view;
        } else {
            myType = Type.LISTINGS;
            View view = inflater.inflate(R.layout.fragment_listings, container, false);
            initListings(view);
            return view;
        }
    }

    /**
     * Init Profile View
     * TODO: profile tab does not refresh (does not update user info such as not-verified/verified)
     */
    private void initProfile(View view) {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final CircleImageView mCircleImageView = (CircleImageView) view.findViewById(R.id.profile_image);
        final Drawable mDefaultProPic = view.getContext().getDrawable(R.drawable.default_profile_photo);
        final Context curContext = view.getContext();

        mCircleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImageIntent();
            }
        });

        FirebaseDatabase.getInstance().getReference("users/" + user.getUid()).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User currentUser = dataSnapshot.getValue(User.class);

                        if (currentUser.profilePicturePath == null || currentUser.profilePicturePath.trim().equals("")) {
                            mCircleImageView.setImageDrawable(mDefaultProPic);
                        } else {
                            Task<Uri> uri = FirebaseStorage.getInstance().getReference().child("profilePictures/").
                                    child(user.getUid()).child(currentUser.profilePicturePath).getDownloadUrl();

                            uri.addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {

                                    Uri uri = task.getResult();
                                    Glide
                                            .with(curContext)
                                            .load(uri) // the uri you got from Firebase
                                            .centerCrop()
                                            .into(mCircleImageView); //Your imageView variable
                                }
                            });
                        }



                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


        Button logOutButton = (Button) view.findViewById(R.id.log_out_button);
        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user != null) {
                    // User is signed in
                    FirebaseAuth.getInstance().signOut();
                } else {
                    // No user is signed in
                }
            }
        });

        Button deleteAccountButton = (Button) view.findViewById(R.id.delete_account_button);
        deleteAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                user.delete();

            }
        });

        TextView ProfileHeader = (TextView) view.findViewById(R.id.profile_header);
        ProfileHeader.setText("Profile");


        TextView displayName = (TextView) view.findViewById(R.id.show_name);
        displayName.setText(user.getDisplayName());

        TextView displayEmail = (TextView) view.findViewById(R.id.show_email);

        String email = user.getEmail();
        if (!user.isEmailVerified()) {
            email += " (Not-verified)";
            SpannableStringBuilder sb = new SpannableStringBuilder(email);
            ForegroundColorSpan fcs = new ForegroundColorSpan(Color.rgb(255, 0, 0));
            StyleSpan iss = new StyleSpan(android.graphics.Typeface.ITALIC);
            sb.setSpan(fcs, user.getEmail().length() + 1, email.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            sb.setSpan(iss, user.getEmail().length() + 1, email.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            displayEmail.setText(sb);
        } else {
            displayEmail.setText(user.getEmail());
        }

    }

    /**
     * Init Listings View
     */
    private void initListings(View view) {

        recyclerView = (RecyclerView) view.findViewById(R.id.fragment_listings_recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        ListingsHeader = (TextView) view.findViewById(R.id.listings_header);
        searchQuery = (EditText) view.findViewById(R.id.searchField);
        ListingsHeader.setText("All Listings");


        Button submitPost = (Button) view.findViewById(R.id.submitSearch);
        submitPost.setOnClickListener(new View.OnClickListener() {
            boolean searchSubmitted = false;
            @Override
            public void onClick(View view) {
                String query = searchQuery.getText().toString().trim();
                if(!TextUtils.isEmpty(query) && !searchSubmitted) {
                    view.setBackgroundResource(R.drawable.ic_clear_black_24dp);
                    ListingsHeader.setText("Search: '" + query + "'");
                    searchSubmitted = true;
                    getPostsByQuery(query);
                } else if (searchSubmitted) {
                    searchQuery.getText().clear();
                    view.setBackgroundResource(R.drawable.places_ic_search);
                    ListingsHeader.setText("All Listings");
                    searchSubmitted = false;
                    getRecentPosts();
                }

            }
        });

//        if (myType == Type.TRANSACTIONS) {
//            ListingsHeader.setText("Transactions");
//        }
       getRecentPosts();


    }

    /**
     * Init Inbox View
     */
    private void initInbox(View view) {
        TextView inboxHeader = (TextView) view.findViewById(R.id.inbox_header);
        inboxHeader.setText("Inbox");
        inboxListView = (ListView) view.findViewById(usersListView);
        inboxContext = getActivity().getApplicationContext();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String path = "/users/" + user.getUid() + "/chats";
        chatRef = FirebaseDatabase.getInstance().getReference(path);


        mInboxListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Map<String, String> chats = (Map<String, String>) dataSnapshot.getValue();
                    inboxEntries = new ArrayList<>();
                    for (String c : chats.keySet()) {
                        inboxEntries.add(new InboxEntry(chats.get(c), c));
                    }
                    namesArrayAdapter =
                            new ArrayAdapter<>(inboxContext,
                                    R.layout.user_list, inboxEntries);
                    inboxListView.setAdapter(namesArrayAdapter);

                    inboxListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> a, View v, int i, long l) {
                            Intent intent = new Intent(getActivity(), MessageActivity.class);
                            intent.putExtra("CHAT_ID", inboxEntries.get(i).getChatId());
                            startActivity(intent);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        chatRef.addValueEventListener(mInboxListener);


    }


    /**
     * Init Transactions View (Seller Posts)
     */
    private void initTransactions(View view) {

        recyclerView = (RecyclerView) view.findViewById(R.id.fragment_transaction_recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        TransListingsHeader = (TextView) view.findViewById(R.id.transaction_header);

        TransListingsHeader.setText("Your Transactions");


        Button favoritePosts = (Button) view.findViewById(R.id.view_favorite_posts);
        Button sellerPosts = (Button) view.findViewById(R.id.view_seller_posts);
        favoritePosts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getRecentTransactions("favoritePosts");

            }
        });

        sellerPosts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getRecentTransactions("sellerPosts");


            }
        });



    }


    private void getRecentTransactions(String typeTransaction) {

        if (myType == Type.TRANSACTIONS) {
            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            //DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users/" + user.getUid());

            Query userPostsQuery = mDatabase.child("users").child(user.getUid()).child(typeTransaction);

            if (typeTransaction.equals("favoritePosts")) {
                TransListingsHeader.setText("Your favorites");
            } else {
                TransListingsHeader.setText("Your posts");
            }

            allPosts.clear();
            allKeys.clear();
            ListingsAdapter adapter = (ListingsAdapter) recyclerView.getAdapter();

            if (adapter != null) {
                adapter.clear();
                recyclerView.setAdapter(adapter);
            }

            userPostsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {

                        ArrayList<String> keys = new ArrayList<String>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            pID = snapshot.getKey();
                            keys.add(pID);
                        }

                        final ArrayList<String> finalKeys = new ArrayList<String>(keys);
                        Query postsQ = mDatabase.child("posts");
                        postsQ.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                for (DataSnapshot post : snapshot.getChildren()) {
                                    for (String k : finalKeys) {
                                        if (post.getKey().equals(k)) {
                                            Post wantedPost = post.getValue(Post.class);
                                            allPosts.add(0, wantedPost);
                                            allKeys.add(0,k);
                                        }
                                    }


                                }
                                ListingsAdapter adapter = new ListingsAdapter(allPosts, getContext(), allKeys);
                                recyclerView.setAdapter(adapter);

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }
    }


    private void openImageIntent() {

        // Filesystem.
        final Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_PICK);

        // Chooser of filesystem options.
        final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");

        startActivityForResult(chooserIntent, 42);
    }

    /**
     * Refresh
     * Called when a tab is clicked while currently in that tab
     */
    public void refresh() {
        if (recyclerView != null) {
            recyclerView.smoothScrollToPosition(0);
        }
    }

    /**
     * Called when a fragment will be displayed
     */
    public void willBeDisplayed() {
        View view =  getView();

        // Animate according to layout
        Animation fadeIn = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);
        LinearLayout layoutContainer = null;
        switch(myType) {
            case LISTINGS:
                layoutContainer = (LinearLayout) view.findViewById(R.id.fragment_listings_container);

                //getRecentPosts();

                break;
            case TRANSACTIONS:
                layoutContainer = (LinearLayout) view.findViewById(R.id.fragment_transaction_container);
                break;
            case INBOX:
                layoutContainer = (LinearLayout) view.findViewById(R.id.fragment_inbox_container);
                break;
            case PROFILE:
                layoutContainer = (LinearLayout) view.findViewById(R.id.fragment_profile_container);
                break;
        }
        layoutContainer.startAnimation(fadeIn);

        // Rechecks if verified
        if (myType == Type.PROFILE) {

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            TextView displayName = (TextView) view.findViewById(R.id.show_name);
            displayName.setText(user.getDisplayName());

            TextView displayEmail = (TextView) view.findViewById(R.id.show_email);
            String email = user.getEmail();
            if (!user.isEmailVerified()) {
                email += " (Not-verified)";
                SpannableStringBuilder sb = new SpannableStringBuilder(email);
                ForegroundColorSpan fcs = new ForegroundColorSpan(Color.rgb(255, 0, 0));
                StyleSpan iss = new StyleSpan(android.graphics.Typeface.ITALIC);
                sb.setSpan(fcs, user.getEmail().length() + 1, email.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                sb.setSpan(iss, user.getEmail().length() + 1, email.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                displayEmail.setText(sb);
            } else {
                displayEmail.setText(user.getEmail());
            }
        }


    }

    /**
     * Called when a fragment will be hidden
     */
    public void willBeHidden() {
        View view =  getView();

        // Animate according to layout
        Animation fadeOut = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_out);
        LinearLayout layoutContainer = null;
        switch(myType) {
            case LISTINGS:
                layoutContainer = (LinearLayout) view.findViewById(R.id.fragment_listings_container);
                break;
            case TRANSACTIONS:
                layoutContainer = (LinearLayout) view.findViewById(R.id.fragment_transaction_container);
                break;
            case INBOX:
                layoutContainer = (LinearLayout) view.findViewById(R.id.fragment_inbox_container);
                break;
            case PROFILE:
                layoutContainer = (LinearLayout) view.findViewById(R.id.fragment_profile_container);
                break;
        }
        layoutContainer.startAnimation(fadeOut);
    }

    public void getRecentPosts() {
        if (myType == Type.LISTINGS) {

            Query postsQuery = mDatabase.child("posts").orderByKey();

            postsQuery.limitToFirst(50).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    keyData.clear();
                    itemsData.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String key = snapshot.getKey();
                        keyData.add(0, key);
                        Post post = snapshot.getValue(Post.class);
                        itemsData.add(0,post);
                    }
                    ListingsAdapter adapter = new ListingsAdapter(itemsData, getContext(), keyData);
                    recyclerView.setAdapter(adapter);

                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
    }

    private void getPostsByQuery(String queryWord) {
        String search = queryWord.toLowerCase();
        Query postsQuery = mDatabase.child("posts").orderByChild("queryTitle")
                .startAt(search).endAt(search + "\uf8ff");

        postsQuery.limitToFirst(50).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                keyData.clear();
                itemsData.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String key = snapshot.getKey();
                    keyData.add(0, key);
                    Post post = snapshot.getValue(Post.class);
                    itemsData.add(0,post);
                }
                ListingsAdapter adapter = new ListingsAdapter(itemsData, getContext(), keyData);
                recyclerView.setAdapter(adapter);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}
