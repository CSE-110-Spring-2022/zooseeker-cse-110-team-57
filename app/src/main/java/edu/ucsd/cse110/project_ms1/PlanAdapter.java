package edu.ucsd.cse110.project_ms1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

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
            this.routedAnimalItem = animal_node.exhibit;
            this.routedExhibitName.setText(animal_node.exhibit.name);
            this.routedExhibitDirections.setText(" "+animal_node.address);
            this.routedExhibitDistance.setText("("+Double.toString(animal_node.distance)+" ft)");
        }
    }



}




