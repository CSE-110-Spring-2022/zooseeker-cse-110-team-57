package edu.ucsd.cse110.project_ms1;

import org.jgrapht.alg.util.Pair;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.nio.Attribute;

/**
 * Exactly like a DefaultWeightedEdge, but has an id field we
 * can use to look up the information about the edge with.
 */
public class IdentifiedWeightedEdge extends DefaultWeightedEdge implements Comparable<IdentifiedWeightedEdge>{
    private String id = null;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    @Override
    public String toString() {
        return "(" + getSource() + " :" + id + ": " + getTarget() + ")";
    }

    public static void attributeConsumer(Pair<IdentifiedWeightedEdge, String> pair, Attribute attr) {
        IdentifiedWeightedEdge edge = pair.getFirst();
        String attrName = pair.getSecond();
        String attrValue = attr.getValue();

        if (attrName.equals("id")) {
            edge.setId(attrValue);
        }
    }


    public int compareTo(IdentifiedWeightedEdge otherEdge) {
        if(this.getWeight() < otherEdge.getWeight()){
            return -1;
        }else if(this.getWeight() == otherEdge.getWeight()){
            return 0;
        }else{
            return 1;
        }
    }
}