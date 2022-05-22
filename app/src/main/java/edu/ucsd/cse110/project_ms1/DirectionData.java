package edu.ucsd.cse110.project_ms1;

import java.util.List;

public class DirectionData {
    public String startExhibit;
    public String goalExhibit;
    double distance;
    List<String> detailPaths;
    List<String> detailPrevPaths;
    List<String> briefPath;
    List<String> briefPrevPath;
    DirectionData(String startExhibit,
                  String goalExhibit,
                  Double distance,
                  List<String> paths,
                  List<String> prevPaths,
                  List<String> briefPath,
                  List<String> briefPrevPath){

        this.startExhibit = startExhibit;
        this.goalExhibit = goalExhibit;
        this.distance = distance;
        this.detailPaths = paths;
        this.detailPrevPaths = prevPaths;
        this.briefPath = briefPath;
        this.briefPrevPath = briefPrevPath;
    }

    String getStartExhibit(){
        return startExhibit;
    }

    String getGoalExhibit(){
        return goalExhibit;
    }


}


