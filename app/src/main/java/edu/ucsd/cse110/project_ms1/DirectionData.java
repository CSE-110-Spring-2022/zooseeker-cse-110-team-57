package edu.ucsd.cse110.project_ms1;

import java.util.List;

public class DirectionData {
    public String exhibitName;
    public String exhibitID;
    public List<String> childList;
    //A mediator function
    DirectionData(String exhibitName,
                  String exhibitID,
                  List<String> childList){

        this.exhibitName = exhibitName;
        this.exhibitID = exhibitID;
        this.childList = childList;

    }

    public String getExhibitName(){
        return exhibitName;
    }

    public String getExhibitID(){
        return exhibitID;
    }

    List<String> getChildList(){ return childList;}

}


