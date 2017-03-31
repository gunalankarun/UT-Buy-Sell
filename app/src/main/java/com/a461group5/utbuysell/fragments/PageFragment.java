package com.a461group5.utbuysell.fragments;

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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.a461group5.utbuysell.R;
import com.a461group5.utbuysell.adapters.ListingsAdapter;

import java.util.ArrayList;

/**
 *
 */
public class PageFragment extends Fragment {

    //private FrameLayout fragmentContainer;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

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

        if (myType == Type.TRANSACTIONS) {
            ListingsHeader.setText("Transactions");
        }

        ArrayList<String> itemsData = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            itemsData.add("Fragment " + getArguments().getInt("index", -1) + " / Item " + i);
        }

        ListingsAdapter adapter = new ListingsAdapter(itemsData);
        recyclerView.setAdapter(adapter);
    }

    /**
     * Init Inbox View
     */
    private void initInbox(View view) {
        TextView inboxHeader = (TextView) view.findViewById(R.id.inbox_header);
        inboxHeader.setText("Inbox");
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
