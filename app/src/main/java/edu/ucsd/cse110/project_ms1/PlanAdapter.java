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

public class PlanAdapter extends RecyclerView.Adapter<PlanAdapter.ViewHolder>{
    private List<route_node> routedNodeList = Collections.emptyList();


    public void setRouted_animal_items(List<route_node> new_routed_node_list){
        this.routedNodeList.clear();
        this.routedNodeList = new_routed_node_list;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public PlanAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.route_plan_items, parent, false);

        return new PlanAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setAnimalItem(routedNodeList.get(position));
    }

    @Override
    public int getItemCount() {
        return routedNodeList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
         private TextView routedExhibitName;
         private TextView routedExhibitDirections;
         private TextView routedExhibitDistance;
         private AnimalItem routedAnimalItem;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.routedExhibitName = itemView.findViewById(R.id.exhibit_name);
            this.routedExhibitDirections = itemView.findViewById(R.id.exhibit_address);
            this.routedExhibitDistance = itemView.findViewById(R.id.exhibit_distance);
        }

        public AnimalItem getAnimalItem(){
            return routedAnimalItem;
        }

        public void setAnimalItem(route_node animal_node) {
            this.routedAnimalItem = animal_node.animal;
            this.routedExhibitName.setText(animal_node.animal.name);
            this.routedExhibitDirections.setText(" "+animal_node.address);
            this.routedExhibitDistance.setText("("+Double.toString(animal_node.distance)+" ft)");
        }
    }



}




