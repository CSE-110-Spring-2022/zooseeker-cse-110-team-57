package edu.ucsd.cse110.project_ms1;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Database;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface AnimalItemDao {
    @Insert
    long insert(AnimalItem animalItem);

    @Insert
    List<Long> insertAll(List<AnimalItem> animalItems);

    @Query("SELECT * FROM `animal_items` WHERE `unique_id`=:id")
    AnimalItem get(long id);

    @Query("SELECT * FROM `animal_items` ORDER BY `unique_id`")
    List<AnimalItem> getAll();

    @Query("SELECT * FROM `animal_items` ORDER BY `id`")
    LiveData<List<AnimalItem>> getAllLive();

    @Update
    int update(AnimalItem animalItem);

    @Delete
    int delete(AnimalItem animalItem);

    @Query("SELECT `id` + 1 FROM `animal_items` ORDER BY `id` DESC LIMIT 1")
    int getOrderForAppend();

}
