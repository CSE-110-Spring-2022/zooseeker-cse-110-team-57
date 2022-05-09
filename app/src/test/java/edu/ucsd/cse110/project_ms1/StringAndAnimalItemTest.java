package edu.ucsd.cse110.project_ms1;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import android.content.Context;
import android.util.Log;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.gson.Gson;

import org.checkerframework.checker.units.qual.A;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

@RunWith(AndroidJUnit4.class)
public class StringAndAnimalItemTest {
    StringAndAnimalItem stringAndAnimalItem;

    @Test
    public String animalitem_to_string_test(){
        ArrayList<String> gorilla_tag = new ArrayList<>();
        gorilla_tag.add("gorilla");
        gorilla_tag.add("monkey");
        gorilla_tag.add("ape");
        gorilla_tag.add("mammal");
        AnimalItem myAnimal = new AnimalItem("gorillas",gorilla_tag, "Gorillas");
        return stringAndAnimalItem.AnimalItemToString(myAnimal);
    }

    @Test
    public AnimalItem string_to_animalitem_test(){
        /*
        Gson g = new Gson();
        AnimalItem currentAnimalItem = g.fromJson(animalitem_to_string_test(), AnimalItem.class);
        return currentAnimalItem;
        */
    };
}
