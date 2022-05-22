package edu.ucsd.cse110.project_ms1;

import java.util.List;

public class DirectionData {
    public String startExhibit;
    public String goalExhibit;
    double distance;
    List<String> paths;
    List<String> prevPaths;

    DirectionData(String startExhibit, String goalExhibit, Double distance,List<String> paths, List<String> prevPaths){
        this.startExhibit = startExhibit;
        this.goalExhibit = goalExhibit;
        this.distance = distance;
        this.paths = paths;
        this.prevPaths = prevPaths;
    }

    String getStartExhibit(){
        return startExhibit;
    }

    String getGoalExhibit(){
        return goalExhibit;
    }
}
