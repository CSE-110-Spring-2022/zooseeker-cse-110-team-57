package edu.ucsd.cse110.project_ms1;
import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;


public class DirectionAdapter extends RecyclerView.Adapter<edu.ucsd.cse110.project_ms1.DirectionAdapter.ViewHolder>{
    private List<String> directionsStringList = Collections.emptyList();

    public void setDirectionsStringList(List<String> new_direction_string_list){
        this.directionsStringList.clear();
        this.directionsStringList = new_direction_string_list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.direction_items, parent, false);

        return new DirectionAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setEdge(directionsStringList.get(position));
    }

    @Override
    public int getItemCount() {
        return directionsStringList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView edgeTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.edgeTextView = itemView.findViewById(R.id.a_direction_item);
        }

        public void setEdge(String currentDirection) {
            this.edgeTextView.setText(currentDirection);
        }
    }
}

