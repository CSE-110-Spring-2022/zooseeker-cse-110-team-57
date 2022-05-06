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

import java.sql.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class SearchedAnimalsAdapter extends RecyclerView.Adapter<SearchedAnimalsAdapter.ViewHolder>
                                    implements Filterable {
    private List<AnimalItem> all_animal_items;
    public  List<AnimalItem> searched_animal_items ;
    private Consumer<AnimalItem> onAnimalButtonClicked;
    private OnAddListener myOnAddListener;
    private IsAnimalFoundPass myIsAnimalFoundPass;

    public interface OnAddListener{
        void OnAddClick(int position);
    }

    public interface IsAnimalFoundPass{
        public void pass(List<AnimalItem> isAnimalFound);
    }

    public SearchedAnimalsAdapter(OnAddListener onAddListener, IsAnimalFoundPass isAnimalFoundPass){
        this.all_animal_items = AnimalItem.search_by_tag(null);
        this.searched_animal_items = new ArrayList<>();
        this.myOnAddListener = onAddListener;
        this.myIsAnimalFoundPass = isAnimalFoundPass;
    }

    public void setSearched_animal_items(List<AnimalItem> new_searched_animal_items){
        searched_animal_items.clear();
        searched_animal_items = new_searched_animal_items;
        notifyDataSetChanged();
    }

    public Filter animalItemFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<AnimalItem> filteredList = new ArrayList<AnimalItem>();
            //if the user hasn't entered anything.
            if (charSequence == null || charSequence.length() == 0){
                filteredList.addAll(all_animal_items);
            }
            else{
                String searchText = charSequence.toString().toLowerCase().trim();
                filteredList = AnimalItem.search_by_tag(searchText);

            }
            FilterResults filtered = new FilterResults();
            filtered.values = filteredList;
            return filtered;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            searched_animal_items.clear();
            searched_animal_items.addAll((List) filterResults.values);
            myIsAnimalFoundPass.pass(searched_animal_items);
            notifyDataSetChanged();
        }
    };



    @Override
    public Filter getFilter() {
        return animalItemFilter;
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

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

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
