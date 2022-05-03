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

public class SearchedAnimalsAdapter extends RecyclerView.Adapter<SearchedAnimalsAdapter.ViewHolder> {
    private List<AnimalItem> searched_animal_items = Collections.emptyList();
    private Consumer<AnimalItem> onAnimalButtonClicked;
    private OnAddListener myOnAddListener;

    public interface OnAddListener{
        void OnAddClick(int position);
    }

    public SearchedAnimalsAdapter(List<AnimalItem> searched_animal_items, OnAddListener onAddListener){
        this.searched_animal_items = searched_animal_items;
        this.myOnAddListener = onAddListener;
    }

    public void setSearched_animal_items(List<AnimalItem> new_searched_animal_items){
        searched_animal_items.clear();
        searched_animal_items = new_searched_animal_items;
        notifyDataSetChanged();
    }

    public void setOnAnimalButtonClickedHandler(Consumer<AnimalItem> onAnimalButtonClicked){
        this.onAnimalButtonClicked = onAnimalButtonClicked;
    }


    @NonNull
    @Override
    public SearchedAnimalsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.searched_animals, parent, false);

        return new ViewHolder(view, myOnAddListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setAnimalItem(searched_animal_items.get(position));
    }

    @Override
    public int getItemCount() {
        return searched_animal_items.size();
    }
/*
    @Override
    public long getItemId(int position) {
        return 0;
        // return searched_animal_items.get(position).id;
    }
*/

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    //public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private final TextView animalName;
        private Button animalButton;
        private AnimalItem animal_item;
        OnAddListener onAddListener;

        public ViewHolder(@NonNull View itemView, OnAddListener onAddListener) {
            super(itemView);
            this.animalName = itemView.findViewById(R.id.an_animal_from_search);
            this.animalButton = itemView.findViewById(R.id.add_to_button);
            this.onAddListener = onAddListener;

            //itemView.setOnClickListener(this);
            this.animalButton.setOnClickListener(this);

        }

        public AnimalItem getAnimalItem(){
            return animal_item;
        }

        public void setAnimalItem(AnimalItem animal_item) {
            this.animal_item = animal_item;
            this.animalName.setText(animal_item.name);
        }

        @Override
        public void onClick(View view) {
            onAddListener.OnAddClick(getAdapterPosition());
        }
    }
}
