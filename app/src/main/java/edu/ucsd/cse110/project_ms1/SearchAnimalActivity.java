package edu.ucsd.cse110.project_ms1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
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


/*
public class SearchAnimalActivity extends AppCompatActivity
        implements SearchView.OnQueryTextListener, SearchedAnimalsAdapter.OnAddListener {

 */
    public class SearchAnimalActivity extends AppCompatActivity
            implements SearchedAnimalsAdapter.OnAddListener, SearchedAnimalsAdapter.IsAnimalFoundPass {

    public RecyclerView searched_recyclerView;
    public RecyclerView selected_recyclerView;
    SearchView searchView;
    AnimalViewModel viewModel;
    SearchedAnimalsAdapter search_adapter;
    AddToListAdapter addToList_adapter;
    List<AnimalItem> allAnimalItem;
    List<AnimalItem> searchedAnimalItemList;
    List<AnimalItem> selectedAnimalItemList;
    List<AnimalItem> preSelectedAnimalItemList;
    List<String> selectedAnimalNameStringList;
    TextView NoSuchAnimal;


    @Override
    protected void onCreate(Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_animal);
        try {
            AnimalItem.loadInfo(this,"sample_node_info.json", "sample_edge_info.json","sample_zoo_graph.json");
        } catch (IOException e) {
            e.printStackTrace();
        }

/*
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();

 */

        //make "No such animal" invisible
        NoSuchAnimal = findViewById(R.id.no_such_animal);
        NoSuchAnimal.setVisibility(View.INVISIBLE);
        TextView BelowAreSelectedAnimals = findViewById(R.id.below_are_selected_animals);
        BelowAreSelectedAnimals.setVisibility(View.VISIBLE);


        //AnimalItemDao
        ///AnimalItemDao animalItemDao = AnimalItemDatabase.getSingleton(this).AnimalItemDao();
        allAnimalItem = AnimalItem.search_by_tag(null);



        //SearchedAnimalsAdapter
        SearchedAnimalsAdapter.IsAnimalFoundPass isAnimalFoundPass = new SearchedAnimalsAdapter.IsAnimalFoundPass() {
            @Override
            public void pass(List<AnimalItem> isAnimalFound) {
                if (isAnimalFound.isEmpty()){
                    NoSuchAnimal.setVisibility(View.VISIBLE);
                }
                else{
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
        TextView animalNumbers = findViewById(R.id.selected_animals_number);
        animalNumbers.setText(Integer.toString(preSelectedAnimalItemList.size()));
        for (AnimalItem selectedAnimal : preSelectedAnimalItemList){
            selectedAnimalNameStringList.add(selectedAnimal.name);
        }

        //reset the addToList


//        addToList_adapter.setSelectedAnimalItems(selectedAnimalItemList);
//        addToList_adapter.notifyDataSetChanged();

        //list bar
        selected_recyclerView = findViewById(R.id.all_selected_animals);
        selected_recyclerView.setLayoutManager(new LinearLayoutManager(this));
        selected_recyclerView.setAdapter(addToList_adapter);
    }

    //----------------------Below are the functions of SearchView--------------------------------
/*
    @Override
    public boolean onQueryTextSubmit(String query) {
        //Get the searched animals
        List<AnimalItem> searchedAnimalsList = AnimalItem.search_by_tag(query);
        //Check if there is no such animal
        TextView NoSuchAnimal = findViewById(R.id.no_such_animal);
        if (searchedAnimalsList.size() == 0){
            NoSuchAnimal.setVisibility(View.VISIBLE);
            return false;
        }
        else{
            NoSuchAnimal.setVisibility(View.INVISIBLE);
        }
        //set the searched animals
        search_adapter.setSearched_animal_items(searchedAnimalsList);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
*/
    //--------------------------Below is the Add Button Click--------------------------------------
    //When the user taps the "Add" button, add the selected animal to selectedAnimalItemList.
    @Override
    public void OnAddClick(int position) {
        AnimalItem newSelectedAnimalItem = allAnimalItem.get(position);
        saveAddToList(newSelectedAnimalItem);
        loadAddToList();

        //addToList_adapter.setSelectedAnimalItems(selectedAnimalItemList);
    }

    //----------------------Below are functions in SharedPreferences-------------------------------

    public void saveAddToList(AnimalItem newSelectedAnimalItem){
        //link the animalItem name with the string form of animalItem
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        //convert AnimalItem into a string containing all related information of a selected animal
        String animalItemInfoString = AnimalItemToString(newSelectedAnimalItem);
        //Map(animal name, animal information)
        editor.putString(newSelectedAnimalItem.name, animalItemInfoString);
        editor.commit();
        editor.apply();
    }

    public List<AnimalItem> loadAddToList(){
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        List<AnimalItem> selectedAnimalItemList = new ArrayList<AnimalItem>();
        //get the list of selected animal names
        Set<String> selectedAnimalNameStringSet = sharedPreferences.getAll().keySet();
        List<String> selectedAnimalNameStringList = new ArrayList<String>(selectedAnimalNameStringSet);

        //check if the user hasn't selected any animal
        if (selectedAnimalNameStringList.isEmpty()) {
            //return empty list of AnimalItem
            return selectedAnimalItemList;
        }
        //set the recycler view
        for (String animalName: selectedAnimalNameStringList){
            //find the string containing all related information of a selected animal
            String animalItemInfoString = sharedPreferences.getString(animalName,
                    "No found such animal in sharedPreference");
            //get the animalItem
            AnimalItem animalItem = StringToAnimalItem(animalItemInfoString);
            //add animalItem to the AnimalItem list
            selectedAnimalItemList.add(animalItem);
        }
        addToList_adapter.setSelectedAnimalItems(selectedAnimalItemList);

        return selectedAnimalItemList;
    }

    public String AnimalItemToString(AnimalItem animalItem){
        return new Gson().toJson(animalItem);
    }

    public AnimalItem StringToAnimalItem(String animalItem){
        Gson g = new Gson();
        AnimalItem currentAnimalItem = g.fromJson(animalItem, AnimalItem.class);
        return currentAnimalItem;
    }

    public void onPlanClick(View view) {
        Intent intent = new Intent(this, PlanActivity.class);
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu (Menu menu){
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
        return true;
    }

    @Override
    public void pass(List<AnimalItem> isAnimalFound) {
        if (isAnimalFound.isEmpty()){
            NoSuchAnimal.setVisibility(View.VISIBLE);
        }
        else{
            NoSuchAnimal.setVisibility(View.INVISIBLE);
        }
    }

}