package edu.ucsd.cse110.project_ms1;

import com.google.gson.Gson;
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

public class StringAndAnimalItem {

    //Animal to String
    public String AnimalItemToString(AnimalItem animalItem){
        return new Gson().toJson(animalItem);
    }
    //String to AnimalItem
    public AnimalItem StringToAnimalItem(String animalItem){
        Gson g = new Gson();
        AnimalItem currentAnimalItem = g.fromJson(animalItem, AnimalItem.class);
        return currentAnimalItem;
    }
}
