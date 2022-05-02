package edu.ucsd.cse110.project_ms1;
import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
public class AddToListAdapter extends RecyclerView.Adapter<AddToListAdapter.ViewHolder>{
    private List<AnimalItem> animalItems = Collections.emptyList();

    public void setSelectedAnimalItems(List<AnimalItem> newAnimalItem){
        this.animalItems.clear();
        this.animalItems = newAnimalItem;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.selected_animals, parent, false);

        return new AddToListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setAnimalItem(animalItems.get(position));
    }

    @Override
    public int getItemCount() {
        return animalItems.size();
    }
/*
    @Override
    public int getItemId(int position){

        return Integer.valueOf(animalItems.get(position).id);
        //return 0;
    }
*/
    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView animalName;
        private AnimalItem animalItem;
        private SharedPreferences sharedPreferences;
        private List<String> selected_animals;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.animalName = itemView.findViewById(R.id.an_animal_selected);
        }
        public AnimalItem getAnimalItem() {
            return animalItem;
        }

        public void setAnimalItem(AnimalItem animalItem) {
            this.animalItem = animalItem;
            this.animalName.setText(animalItem.name);
        }
    }

}
