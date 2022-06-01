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
import android.widget.EditText;
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

import edu.ucsd.cse110.project_ms1.location.Coord;

public class EnterActivity extends AppCompatActivity {
    OnMockChangeListener onMockChangeListener;
    Coord entered_coord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter);
        loadEnteredCoord();
    }

    public void onMockButtonClick(View view){
        entered_coord= readEnteredCoord();
        onMockChangeListener.OnMockChange(entered_coord);
        finish();
    }

    public Coord readEnteredCoord(){
        EditText lat_text = findViewById(R.id.Latitude_text);
        EditText lng_text = findViewById(R.id.Longitude_text);
        Double lat = Double.valueOf(lat_text.getText().toString());
        Double lng = Double.valueOf(lng_text.getText().toString());
        Coord coord = new Coord(lat, lng);
        return coord;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveEnteredCoord();
    }

    private void saveEnteredCoord() {
        SharedPreferences sharedPreferences = getSharedPreferences("Team57", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        entered_coord = readEnteredCoord();
        editor.putString("currentLat", Double.toString(entered_coord.lat));
        editor.putString("currentLng", Double.toString(entered_coord.lng));
        editor.commit();
        editor.apply();
    }

    private void loadEnteredCoord() {
        Coord gate_coord = AnimalItem.getExtranceGateCoord();
        SharedPreferences sharedPreferences = getSharedPreferences("Team57", Activity.MODE_PRIVATE);
        TextView lat_text = findViewById(R.id.Latitude_text);
        TextView lng_text = findViewById(R.id.Longitude_text);
        lat_text.setText(sharedPreferences.getString("currentLat", Double.toString(gate_coord.lat)));
        lng_text.setText(sharedPreferences.getString("currentLat", Double.toString(gate_coord.lng)));
    }

}