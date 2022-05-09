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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

@RunWith(AndroidJUnit4.class)
public class US0_AnimalItemDataBaseTest {
    private AnimalItemDao dao;
    private AnimalItemDatabase db;
    @Before
    public void createDb(){
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context,AnimalItemDatabase.class)
                .allowMainThreadQueries()
                .build();

        dao = db.AnimalItemDao();
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }


    @Test
    public void testInsert(){
        AnimalItem item1 = new AnimalItem("entrance_exit_gate", new ArrayList<>(Arrays.asList("gate","monkey")),"Entrance and Exit Gate");
        AnimalItem item2 = new AnimalItem("entrance_plaza", new ArrayList<>(Arrays.asList("gate","monkey")),"Entrance Plaza");

        long id1 = dao.insert(item1);
        long id2 = dao.insert(item2);

        assertNotEquals(id1,id2);
    }

    @Test
    public void testGet() {
        AnimalItem insertedItem = new AnimalItem("entrance_exit_gate", new ArrayList<>(Arrays.asList("gate","monkey")),"Entrance and Exit Gate");
        long unique_id = dao.insert(insertedItem);

        AnimalItem item = dao.get(unique_id);
        assertEquals(unique_id, item.unique_id);
        assertEquals(insertedItem.id,item.id);
        assertEquals(insertedItem.tags,item.tags);
        assertEquals(insertedItem.name,item.name);
    }


    @Test
    public void testUpdate(){
        AnimalItem insertedItem = new AnimalItem("entrance_exit_gate", new ArrayList<>(Arrays.asList("gate","monkey")),"Entrance and Exit Gate");
        long id = dao.insert(insertedItem);

        insertedItem = dao.get(id);
        insertedItem.id = "Photos of Spider-Man";
        int itemsUpdated = dao.update(insertedItem);
        assertEquals(1,itemsUpdated);

        insertedItem = dao.get(id);
        assertNotNull(insertedItem);
        assertEquals("Photos of Spider-Man",insertedItem.id);
    }


    @Test
    public void testDelete(){
        AnimalItem insertedItem = new AnimalItem("entrance_exit_gate", new ArrayList<>(Arrays.asList("gate","monkey")),"Entrance and Exit Gate");
        long unique_id = dao.insert(insertedItem);

        AnimalItem item = dao.get(unique_id);
        int itemDeleted = dao.delete(item);
        assertEquals(1,itemDeleted);
        assertNull(dao.get(unique_id));
    }


}