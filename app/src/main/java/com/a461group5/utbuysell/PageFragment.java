package com.a461group5.utbuysell;

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
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

/**
 *
 */
public class PageFragment extends Fragment {

    private FrameLayout fragmentContainer;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

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
            View view = inflater.inflate(R.layout.fragment_profile, container, false);
            initProfile(view);
            return view;
        } else if (getArguments().getInt("index", 0) == 2) {
            View view = inflater.inflate(R.layout.fragment_inbox, container, false);
            initInbox(view);
            return view;
        } else if (getArguments().getInt("index", 0) == 1) {
            // TODO: Change this to transactions view specific
            View view = inflater.inflate(R.layout.fragment_listings, container, false);
            initListings(view);
            return view;
        } else {
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
     * TODO: profile tab does not refresh (does not update user info such as not-verified/verified)
     */
    private void initListings(View view) {

        fragmentContainer = (FrameLayout) view.findViewById(R.id.fragment_listings_container);
        recyclerView = (RecyclerView) view.findViewById(R.id.fragment_listings_recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        TextView ListingsHeader = (TextView) view.findViewById(R.id.listings_header);
        ListingsHeader.setText("All Listings");

        if (getArguments().getInt("index", 0) == 1) {
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

    /**
     * Refresh
     * Called when a tab is clicked while currently in that tab
     */
    public void refresh() {
        if (getArguments().getInt("index", 0) > 0 && recyclerView != null) {
            recyclerView.smoothScrollToPosition(0);
        }
    }

    /**
     * Called when a fragment will be displayed
     */
    public void willBeDisplayed() {
        // Do what you want here, for example animate the content
        if (fragmentContainer != null) {
            Animation fadeIn = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);
            fragmentContainer.startAnimation(fadeIn);
        }

        // Rechecks if verified
        if (getArguments().getInt("index", 0) == 3) {
            View view =  getView();
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
        if (fragmentContainer != null) {
            Animation fadeOut = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_out);
            fragmentContainer.startAnimation(fadeOut);
        }
    }
}
