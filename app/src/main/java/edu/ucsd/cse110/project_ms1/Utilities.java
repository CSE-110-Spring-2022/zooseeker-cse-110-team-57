package edu.ucsd.cse110.project_ms1;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Pair;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.GraphTests;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Utilities {
    public static void showAlert(Activity activity, String message){
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(activity);

        alertBuilder.setTitle("Alert!")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, id)->{
                    dialog.cancel();
                })
                .setCancelable(true);

        AlertDialog alertDialog = alertBuilder.create();
        alertDialog.show();
    }

    public static void clearSavedAnimalItem(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("Team57", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
        editor.apply();

    }

    public static void changeCurrentActivity(Context context, String activity){
        SharedPreferences sharedPreferences = context.getSharedPreferences("Team57", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        //set the currentActivity to be SearchAnimalActivity
        editor.putString("currentActivity", activity);
        editor.commit();
        editor.apply();
    }

    public static Graph<String,IdentifiedWeightedEdge> completeG (Graph<String,IdentifiedWeightedEdge> g, ArrayList<String> vertex) {
        Graph<String,IdentifiedWeightedEdge> graph = new DefaultUndirectedWeightedGraph<>(IdentifiedWeightedEdge.class);
        // add vertex to graph
        for (String str: vertex){
            graph.addVertex(str);
        }

        //add edge to graph
        for (int i = 0; i<vertex.size();i++){
            for (int j = i+1; j<vertex.size();j++){
                String start = vertex.get(i);
                String goal = vertex.get(j);
                GraphPath<String, IdentifiedWeightedEdge> path = DijkstraShortestPath.findPathBetween(g, start, goal);
                double edge_weight = 0;
                for (IdentifiedWeightedEdge e: path.getEdgeList()){edge_weight+=g.getEdgeWeight(e);}
                IdentifiedWeightedEdge edge = graph.addEdge(start,goal);
                graph.setEdgeWeight(edge,edge_weight);
            }
        }
        return graph;
    }
    //findcycle() for detecting cycle
    //DegreeOf() for checking degree


    public static ArrayList<String> TSP(Graph<String,IdentifiedWeightedEdge> g, String start){
        if(!GraphTests.isComplete(g)){
            throw new IllegalArgumentException("not a complete graph");
        }

        ArrayList<String> res = new ArrayList<>();
        int n = g.vertexSet().size();
        Set<String>  vertexSet = g.vertexSet();
        Set<String> visited = vertexSet;

        //new graph with vertex only
        Graph<String,IdentifiedWeightedEdge> graph = new DefaultUndirectedWeightedGraph<>(IdentifiedWeightedEdge.class);
        for(String s: vertexSet){
            graph.addVertex(s);
        }

        String curr = start;
        while(visited.size() != 0 ){
            visited.remove(curr);
            res.add(curr);
            //sort the edgelist
            Set<IdentifiedWeightedEdge> edgeSet =g.edgesOf(curr);
            ArrayList<IdentifiedWeightedEdge> edgeList = new ArrayList<>();
            for (IdentifiedWeightedEdge e: edgeSet){
                edgeList.add(e);
            }

            Collections.sort(edgeList);
            //need to check whether the list is sorted ^

            for (IdentifiedWeightedEdge e: edgeList){
                //不知道targetnode 会不会是source_node
                String target = g.getEdgeTarget(e);
                if (visited.contains(target)){
                    curr = target;
                    break;
                }
            }
        }

        res.add(start);

        // sorted EdgeList
//        Set<IdentifiedWeightedEdge> edgeSet = graph.edgeSet();
//        ArrayList<IdentifiedWeightedEdge> edgeList = new ArrayList<>();
//        for (IdentifiedWeightedEdge e: edgeSet){
//            edgeList.add(e);
//        }
//        Collections.sort(edgeList);

        return res;
    }


}
