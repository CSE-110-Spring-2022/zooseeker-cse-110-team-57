package edu.ucsd.cse110.project_ms1;

import java.util.List;

public class DirectionData {
    public String startExhibit;
    public String goalExhibit;
    double distance;

    List<String> paths;

    DirectionData(String startExhibit, String goalExhibit, Double distance,List<String> paths){
        this.startExhibit = startExhibit;
        this.goalExhibit = goalExhibit;
        this.distance = distance;
        this.paths = paths;
    }
}
