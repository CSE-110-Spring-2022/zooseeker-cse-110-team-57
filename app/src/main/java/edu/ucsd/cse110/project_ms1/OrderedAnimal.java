package edu.ucsd.cse110.project_ms1;

import com.google.gson.Gson;

public class OrderedAnimal {
    public AnimalItem animalItem;
    public int order;

    public String ToString(OrderedAnimal animal){
        return new Gson().toJson(animal);
    }

    public OrderedAnimal StringToAnimal(String str){
        Gson g = new Gson();
        OrderedAnimal currentAnimalItem = g.fromJson(str, OrderedAnimal.class);
        return currentAnimalItem;
    }
}
