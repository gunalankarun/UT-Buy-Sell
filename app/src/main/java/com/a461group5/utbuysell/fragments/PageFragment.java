package com.a461group5.utbuysell.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.a461group5.utbuysell.MessageActivity;
import com.a461group5.utbuysell.R;
import com.a461group5.utbuysell.ViewPostActivity;
import com.a461group5.utbuysell.adapters.ListingsAdapter;
import com.a461group5.utbuysell.models.InboxEntry;
import com.a461group5.utbuysell.models.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.Query;
import com.google.firebase.database.ChildEventListener;
import java.util.ArrayList;
import java.util.Map;

import static com.a461group5.utbuysell.R.id.usersListView;

/**
 *
 */
public class PageFragment extends Fragment {

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
            // TODO: Change this to transactions view specific
            myType = Type.TRANSACTIONS;
            View view = inflater.inflate(R.layout.fragment_listings, container, false);
            initListings(view);
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

        Button uploadPicButton = (Button) view.findViewById(R.id.upload_pic_button);
        uploadPicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImageIntent();
            }
        });


        Button logOutButton = (Button) view.findViewById(R.id.log_out_button);
        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
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
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                user.delete();

            }
        });

        TextView ProfileHeader = (TextView) view.findViewById(R.id.profile_header);
        ProfileHeader.setText("Profile");

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

    /**
     * Init Listings View
     */
    private void initListings(View view) {

        recyclerView = (RecyclerView) view.findViewById(R.id.fragment_listings_recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        TextView ListingsHeader = (TextView) view.findViewById(R.id.listings_header);
        ListingsHeader.setText("All Listings");

        Button viewPost = (Button) view.findViewById(R.id.temp_view_post);
        viewPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String messageId = "-KgSb_iZ4JOSNTm7kmhe";
                Intent intent = new Intent(getActivity(), ViewPostActivity.class);
                intent.putExtra("messageId", messageId);
                startActivity(intent);
            }
        });

        if (myType == Type.TRANSACTIONS) {
            ListingsHeader.setText("Transactions");
        }


        Query postsQuery = mDatabase.child("posts").orderByKey();


        postsQuery.limitToFirst(50).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Post post = snapshot.getValue(Post.class);
                    String key = snapshot.getKey();
                    keyData.add(key);
                    itemsData.add(post);
                }
                ListingsAdapter adapter = new ListingsAdapter(itemsData, getContext(), keyData);
                recyclerView.setAdapter(adapter);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        postsQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Post post = snapshot.getValue(Post.class);
                    itemsData.add(0, post);
                    String key = snapshot.getKey();
                    keyData.add(0, key);

                }
                ListingsAdapter adapter = new ListingsAdapter(itemsData, getContext(), keyData);
                recyclerView.setAdapter(adapter);
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {

                String commentKey = dataSnapshot.getKey();

                // ...
            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {


                // ...
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {


                // ...
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


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
     * Init Transactions View
     * TODO: Implement this
     */
    private void initTransactions(View view) {

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
                break;
            case TRANSACTIONS:
                layoutContainer = (LinearLayout) view.findViewById(R.id.fragment_listings_container);
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
                layoutContainer = (LinearLayout) view.findViewById(R.id.fragment_listings_container);
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
}
