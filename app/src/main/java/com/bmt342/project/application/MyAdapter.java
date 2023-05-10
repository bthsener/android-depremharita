package com.bmt342.project.application;

import android.content.Context;
import android.graphics.drawable.shapes.RoundRectShape;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bmt342.project.application.model.Post;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    Context context;
    ArrayList<Post> postList;

    public MyAdapter(Context context, ArrayList<Post> postList){
        this.context = context;
        this.postList = postList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Post post = postList.get(position);
        holder.title.setText(post.getTitle());
        holder.description.setText(post.getContent());
        holder.person.setText(String.valueOf(post.getPerson())+" kişi");
        holder.city.setText(String.valueOf(post.getPublishDate().toLocaleString()));
        holder.imageView.setImageBitmap(post.convertStringToBitmap(post.getImageUrl()));
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView title, description, person, city;
        ImageView imageView;

        public MyViewHolder(@NonNull View itemView){
            super(itemView);

            title = itemView.findViewById(R.id.postTitleTextView);
            description = itemView.findViewById(R.id.postBodyTextView);
            person = itemView.findViewById(R.id.postPerson);
            city = itemView.findViewById(R.id.postCity);
            imageView = itemView.findViewById(R.id.miniImage);
        }
    }
}
