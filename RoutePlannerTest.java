package br.com.rotalivre;

import static br.com.rotalivre.Models.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
import org.junit.jupiter.api.Test;

class RoutePlannerTest {
    final long now=1_800_000_000_000L;
    final List<Node> nodes=List.of(new Node(1,"A",0,0,"CROSSING",true),new Node(2,"B",1,0,"CROSSING",true),new Node(3,"C",1,1,"CROSSING",true));
    final Constraints constraints=new Constraints(90,6,30);
    Edge edge(int id,int a,int b,double distance){return new Edge(id,a,b,"T"+id,distance,120,3,true,"REGULAR","KNOWN",now,"teste",true,true);}
    List<Edge> edges(){return List.of(edge(1,1,2,10),edge(2,2,3,10),edge(3,1,3,30));}
    @Test void shortestPath(){var r=RoutePlanner.calculate(nodes,edges(),Set.of(),1,3,constraints,now);assertTrue(r.found());assertEquals(20,r.distanceM());assertEquals(List.of(1,2),r.edges().stream().map(Edge::id).toList());assertEquals(0,r.detourM());}
    @Test void detourAfterBlocking(){var r=RoutePlanner.calculate(nodes,edges(),Set.of(2),1,3,constraints,now);assertEquals(30,r.distanceM());assertEquals(10,r.detourM());assertEquals(50,r.detourPercent());}
    @Test void noRouteIsNotProofOfRealWorldAbsence(){var r=RoutePlanner.calculate(nodes,edges(),Set.of(1,3),1,3,constraints,now);assertFalse(r.found());assertTrue(r.nodes().isEmpty());assertNull(r.detourM());assertTrue(r.message().contains("dados e critérios"));}
    @Test void samePoint(){var r=RoutePlanner.calculate(nodes,edges(),Set.of(),1,1,constraints,now);assertTrue(r.found());assertEquals(0,r.distanceM());assertNull(r.detourPercent());assertEquals(1,r.nodes().size());}
    @Test void missingNode(){assertThrows(IllegalArgumentException.class,()->RoutePlanner.calculate(nodes,edges(),Set.of(),1,99,constraints,now));}
    @Test void narrowEdgesExcluded(){var r=RoutePlanner.calculate(nodes,edges(),Set.of(),1,3,new Constraints(130,6,30),now);assertFalse(r.found());assertEquals(3,r.excludedEdges().size());}
    @Test void unknownAndOldDataExcluded(){var unknown=new Edge(1,1,2,"A",10,120,3,true,"REGULAR","UNKNOWN",null,"teste",true,true);var old=new Edge(2,2,3,"B",10,120,3,true,"REGULAR","KNOWN",now-31*86_400_000L,"teste",true,true);var r=RoutePlanner.calculate(nodes,List.of(unknown,old),Set.of(),1,3,constraints,now);assertFalse(r.found());assertEquals(List.of(1,2),r.excludedEdges());}
    @Test void stepsAndSteepSlopeExcluded(){var a=new Edge(1,1,2,"A",10,120,3,false,"REGULAR","KNOWN",now,"teste",true,true);var b=new Edge(2,2,3,"B",10,120,8,true,"REGULAR","KNOWN",now,"teste",true,true);assertFalse(RoutePlanner.calculate(nodes,List.of(a,b),Set.of(),1,3,constraints,now).found());}
    @Test void directionRespected(){var a=new Edge(1,1,2,"A",10,120,3,true,"REGULAR","KNOWN",now,"teste",false,true);assertFalse(RoutePlanner.calculate(nodes,List.of(a),Set.of(),2,1,constraints,now).found());assertTrue(RoutePlanner.calculate(nodes,List.of(a),Set.of(),1,2,constraints,now).found());}
    @Test void futureDateExcluded(){var a=new Edge(1,1,2,"A",10,120,3,true,"REGULAR","KNOWN",now+1000,"teste",true,true);assertFalse(RoutePlanner.eligible(a,constraints,now));}
    @Test void invalidConstraints(){assertThrows(IllegalArgumentException.class,()->new Constraints(0,6,30));assertThrows(IllegalArgumentException.class,()->new Constraints(90,Double.NaN,30));}
}
