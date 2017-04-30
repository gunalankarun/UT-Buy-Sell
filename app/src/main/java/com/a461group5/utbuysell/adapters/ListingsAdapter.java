package com.a461group5.utbuysell.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.a461group5.utbuysell.R;
import com.a461group5.utbuysell.models.Post;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

/**
 *
 */
public class ListingsAdapter extends RecyclerView.Adapter<ListingsAdapter.ViewHolder> {

    private ArrayList<Post> mDataset = new ArrayList<>();
    private Context context;
    private ArrayList<String> postIds = new ArrayList<>();

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTitleTextView;
        public TextView mPriceTextView;
        public ImageView mImageView;
        public ViewHolder(View v) {
            super(v);
            mTitleTextView = (TextView) v.findViewById(R.id.layout_list_item_title);
            mPriceTextView = (TextView) v.findViewById(R.id.listPrice);
            mImageView = (ImageView) v.findViewById(R.id.listImgPreview);
        }
    }

    public ListingsAdapter(ArrayList<Post> dataset, Context context, ArrayList<String> postId) {
        mDataset.clear();
        postIds.clear();
        mDataset.addAll(dataset);
        postIds.addAll(postId);
        this.context = context;
    }

    @Override
    public ListingsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_list_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mTitleTextView.setText(mDataset.get(position).description);
        holder.mPriceTextView.setText("$" + String.format("%.2f", mDataset.get(position).price));
        holder.mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Post post = mDataset.get(position);
        if(post.imagePaths != null) {
            for (String imgPath : post.imagePaths.keySet()) {
                Task<Uri> uri = FirebaseStorage.getInstance().getReference().child("postImages/").
                        child(postIds.get(position)).child(imgPath).getDownloadUrl();

                uri.addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        //holder.mImageView.setImageBitmap(getImageBitmap(task.getResult().toString()));
                        Uri uri = task.getResult();
                        Glide
                                .with(context)
                                .load(uri) // the uri you got from Firebase
                                .centerCrop()
                                .into(holder.mImageView); //Your imageView variable
                    }
                });

            }
        } else {
            //put default picture here
            holder.mImageView.setImageDrawable(context.getDrawable(R.drawable.shopping_cart));
        }
        //holder.mImageView.setImageURI(new URI("test"));
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}
