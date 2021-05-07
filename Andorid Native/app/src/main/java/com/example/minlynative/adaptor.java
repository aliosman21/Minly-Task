package com.example.minlynative;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

class someClass extends RecyclerView.ViewHolder{

    public someClass(@NonNull View itemView) {
        super(itemView);
    }
}
public class adaptor extends RecyclerView.Adapter<someClass> {
    private List<String> urls;
    private String ServerUrl = "http://10.0.2.2:3000/images/";
    public adaptor(List<String> urls){
        this.urls = urls;
    }

    @Override
    public someClass onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
        LayoutInflater inflator = LayoutInflater.from(parent.getContext());
        return new someClass(inflator.inflate(R.layout.images,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull  someClass holder, int position) {
        Picasso.get().load(ServerUrl+ urls.get(position))
                .into((ImageView) holder.itemView.findViewById(R.id.oneImage));

    }

    @Override
    public int getItemCount() {
        return this.urls.size();
    }
}
