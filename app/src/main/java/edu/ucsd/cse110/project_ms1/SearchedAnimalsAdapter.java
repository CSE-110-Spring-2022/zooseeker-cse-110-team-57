package edu.ucsd.cse110.project_ms1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class SearchedAnimalsAdapter extends RecyclerView.Adapter<SearchedAnimalsAdapter.ViewHolder> {
    private List<AnimalItem> searched_animal_items = Collections.emptyList();
    private Consumer<AnimalItem> onCheckBoxClicked;

    public void setSearched_animal_items(List<AnimalItem> new_searched_animal_items){
        searched_animal_items.clear();
        searched_animal_items = new_searched_animal_items;
        notifyDataSetChanged();
    }
    public void setOnCheckBoxClickedHandler(Consumer<AnimalItem> onCheckBoxClicked){
        this.onCheckBoxClicked = onCheckBoxClicked;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView animalView;
        private CheckBox animalCheckBox;
        private AnimalItem animal_item;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.animalView = itemView.findViewById(R.id.an_animal_from_search);
            this.animalCheckBox = itemView.findViewById(R.id.an_animal_checkBox);

            this.animalCheckBox.setOnClickListener(view -> {
                if (onCheckBoxClicked == null) return;
                onCheckBoxClicked.accept(animal_item);
            });
        }

        public AnimalItem getAnimalItem(){
            return animal_item;
        }

        public void setAnimal_item(AnimalItem animal_item) {
            this.animal_item = animal_item;
            this.animalView.setText(animal_item.name);
        }

        public AnimalItem getAnimal_item() {
            return animal_item;
        }

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.searched_animals, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setAnimal_item(searched_animal_items.get(position));
    }

    @Override
    public int getItemCount() {
        return searched_animal_items.size();
    }

    @Override
    public  long getItemId(int position) {

        return searched_animal_items.get(position).id;
    }



}
