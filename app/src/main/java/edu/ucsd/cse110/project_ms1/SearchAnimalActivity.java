package edu.ucsd.cse110.project_ms1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;


    public class SearchAnimalActivity extends AppCompatActivity
            implements SearchedAnimalsAdapter.OnAddListener, SearchedAnimalsAdapter.IsAnimalFoundPass {

        private static final int ACTIVITY_CONSTANT = 0;
        public RecyclerView searched_recyclerView;
        public RecyclerView selected_recyclerView;
        SearchedAnimalsAdapter search_adapter;
        AddToListAdapter addToList_adapter;
        List<AnimalItem> allAnimalItem;
        List<AnimalItem> selectedAnimalItemList;
        List<AnimalItem> preSelectedAnimalItemList;
        List<String> selectedAnimalNameStringList;
        TextView NoSuchAnimal;
        TextView animalNumbers;
        StringAndAnimalItem stringAndAnimalItem;




        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_search_animal);
            AnimalUtilities.loadZooInfo(this);
            Utilities.changeCurrentActivity(this, "SearchAnimalActivity");

            //make "No such animal" invisible
            NoSuchAnimal = findViewById(R.id.no_such_animal);
            NoSuchAnimal.setVisibility(View.INVISIBLE);
            TextView BelowAreSelectedAnimals = findViewById(R.id.below_are_selected_animals);
            BelowAreSelectedAnimals.setVisibility(View.VISIBLE);
            stringAndAnimalItem = new StringAndAnimalItem();

            //AnimalItemDao
            ///AnimalItemDao animalItemDao = AnimalItemDatabase.getSingleton(this).AnimalItemDao();
            allAnimalItem = AnimalItem.search_by_tag(null);

            //SearchedAnimalsAdapter
            SearchedAnimalsAdapter.IsAnimalFoundPass isAnimalFoundPass = new SearchedAnimalsAdapter.IsAnimalFoundPass() {
                @Override
                public void pass(List<AnimalItem> isAnimalFound) {
                    if (isAnimalFound.isEmpty()) {
                        NoSuchAnimal.setVisibility(View.VISIBLE);
                    } else {
                        NoSuchAnimal.setVisibility(View.INVISIBLE);
                    }
                }
            };

            search_adapter = new SearchedAnimalsAdapter(this, isAnimalFoundPass);
            search_adapter.setHasStableIds(true);

            //viewModel = new ViewModelProvider(this).get(AnimalViewModel.class);
            //viewModel.getSearchedAnimalItems().observe(this, search_adapter::setSearched_animal_items);

            //dropdown bar
            searched_recyclerView = findViewById(R.id.all_searched_animals);
            searched_recyclerView.setLayoutManager(new LinearLayoutManager(this));
            searched_recyclerView.setAdapter(search_adapter);


            //--------------------------Below is the AddToList part-----------------------------------


            //AddToListAdapter
            addToList_adapter = new AddToListAdapter();
            addToList_adapter.setHasStableIds(true);
            selectedAnimalItemList = loadAddToList();
            addToList_adapter.setSelectedAnimalItems(selectedAnimalItemList);


            //Retain the previous list of selected animals (List<AnimalItem>) each time we start the app
            selectedAnimalNameStringList = new ArrayList<String>();
            preSelectedAnimalItemList = loadAddToList();
            animalNumbers = findViewById(R.id.selected_animals_number);
            animalNumbers.setText(Integer.toString(preSelectedAnimalItemList.size()));
            for (AnimalItem selectedAnimal : preSelectedAnimalItemList) {
                if (!selectedAnimalNameStringList.contains(selectedAnimal.name)) {
                    selectedAnimalNameStringList.add(selectedAnimal.name);
                }
            }


            //list bar
            selected_recyclerView = findViewById(R.id.all_selected_animals);
            selected_recyclerView.setLayoutManager(new LinearLayoutManager(this));
            selected_recyclerView.setAdapter(addToList_adapter);
        }


        //--------------------------Below is the Add Button Click--------------------------------------
        //When the user taps the "Add" button, add the selected animal to selectedAnimalItemList.
        @Override
        public void OnAddClick(int position) {
            AnimalItem newSelectedAnimalItem = search_adapter.searched_animal_items.get(position);
            saveAddToList(newSelectedAnimalItem);

            animalNumbers.setText(Integer.toString(loadAddToList().size()));

            //addToList_adapter.setSelectedAnimalItems(selectedAnimalItemList);
        }

        //----------------------Below are functions in SharedPreferences-------------------------------
        //save the selected animals in sharepreference
        public void saveAddToList(AnimalItem newSelectedAnimalItem) {
            //link the animalItem name with the string form of animalItem
            SharedPreferences sharedPreferences = getSharedPreferences("Team57", 0);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            //convert AnimalItem into a string containing all related information of a selected animal
            String animalItemInfoString = stringAndAnimalItem.AnimalItemToString(newSelectedAnimalItem);
            //Map(animal name, animal information)
            editor.putString(newSelectedAnimalItem.name, animalItemInfoString);
            editor.commit();
            editor.apply();
        }
        //load the selected animals in sharepreference
        public List<AnimalItem> loadAddToList() {
            SharedPreferences sharedPreferences = getSharedPreferences("Team57", 0);
            List<AnimalItem> selectedAnimalItemList = new ArrayList<AnimalItem>();
            //get the list of selected animal names
            Set<String> selectedAnimalNameStringSet = sharedPreferences.getAll().keySet();
            selectedAnimalNameStringSet.remove("currentActivity");
            selectedAnimalNameStringSet.remove("currentOrder");
            selectedAnimalNameStringSet.remove("currentIsNext");
            selectedAnimalNameStringSet.remove("currentLat");
            selectedAnimalNameStringSet.remove("currentLng");
            selectedAnimalNameStringSet.remove("route");
            selectedAnimalNameStringList = new ArrayList<String>(selectedAnimalNameStringSet);

            //check if the user hasn't selected any animal
            if (selectedAnimalNameStringList.isEmpty()) {
                //return empty list of AnimalItem
                return selectedAnimalItemList;
            }
            //set the recycler view
            for (String animalName : selectedAnimalNameStringList) {
                //find the string containing all related information of a selected animal
                String animalItemInfoString = sharedPreferences.getString(animalName,
                        "No found such animal in sharedPreference");
                //get the animalItem
                AnimalItem animalItem = stringAndAnimalItem.StringToAnimalItem(animalItemInfoString);
                //add animalItem to the AnimalItem list
                selectedAnimalItemList.add(animalItem);
            }
            addToList_adapter.setSelectedAnimalItems(selectedAnimalItemList);

            return selectedAnimalItemList;


        }

        //act when plan button is clicked
        public void onPlanClick(View view) {
            Intent intent = new Intent(this, PlanActivity.class);
            if (selectedAnimalNameStringList.isEmpty()) {
                Utilities.showAlert(this, "Please select at least one animals");
                return;
            }
            ArrayList<String> currentNameStringList = new ArrayList<String>(selectedAnimalNameStringList);
            intent.putStringArrayListExtra("nameStringList", currentNameStringList);
            startActivityForResult(intent, ACTIVITY_CONSTANT);
        }

        //search bar menu
        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.animal_menu, menu);
            MenuItem menuItem = menu.findItem(R.id.search_bar_2);
            SearchView searchView = (SearchView) menuItem.getActionView();
            searchView.setQueryHint("Please enter an animal's name");
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    search_adapter.getFilter().filter(s);
                    return false;
                }
            });
            menuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
                @Override
                public boolean onMenuItemActionExpand(MenuItem menuItem) {
                    searched_recyclerView.setVisibility(View.VISIBLE);
                    return true;
                }

                @Override
                public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                    searched_recyclerView.setVisibility(View.INVISIBLE);
                    return true;
                }
            });
            return true;
        }

        //set the NoSuchAnimal alert
        @Override
        public void pass(List<AnimalItem> isAnimalFound) {
            if (isAnimalFound.isEmpty()) {
                NoSuchAnimal.setVisibility(View.VISIBLE);
            } else {
                NoSuchAnimal.setVisibility(View.INVISIBLE);
            }
        }

        //act when clear button is clicked
        public void onClearButtonClick_SearchActivity(View view) {
            Utilities.clearSharedPreference(this);
            selectedAnimalItemList = loadAddToList();
            addToList_adapter.setSelectedAnimalItems(selectedAnimalItemList);
            animalNumbers.setText(Integer.toString(preSelectedAnimalItemList.size()));
        }

        //https://stackoverflow.com/questions/9664108/how-to-finish-parent-activity-from-child-activity
        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            // TODO Auto-generated method stub
            super.onActivityResult(requestCode, resultCode, data);
            if(requestCode == ACTIVITY_CONSTANT)
            {
                Button button = findViewById(R.id.clear_button);
                button.performClick();
            }
        }
    }

