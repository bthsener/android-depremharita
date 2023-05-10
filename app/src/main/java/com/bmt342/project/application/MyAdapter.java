package com.bmt342.project.application;

import android.content.Context;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bmt342.project.application.model.Post;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    Context context;
    ArrayList<Post> postList;
    private AdapterView.OnItemClickListener listener;


    public MyAdapter(Context context, ArrayList<Post> postList){
        this.context = context;
        this.postList = postList;
    }

    View v;
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        v = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
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

        ConstraintLayout layout = v.findViewById(R.id.item);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("post", post);
                Navigation.findNavController(view).navigate(R.id.action_postFragment_to_postDetailsFragment, bundle);
            }
        });

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
