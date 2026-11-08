package br.com.rotalivre;

import static br.com.rotalivre.Models.*;
import java.util.*;

/** Dijkstra: O((V+E) log V), memória O(V+E). Rede sintética não certifica a via real. */
public final class RoutePlanner {
    private record Arc(int to, Edge edge) {}
    private record QueueItem(int node, double distance) {}
    private record Path(List<Node> nodes, List<Edge> edges, double distance) {}
    private RoutePlanner() {}

    public static RouteResult calculate(List<Node> nodes, List<Edge> edges, Set<Integer> blocked,
        int from, int to, Constraints constraints, long now) {
        long begun=System.nanoTime();
        Map<Integer,Node> lookup=new HashMap<>();nodes.forEach(n->lookup.put(n.id(),n));
        if(!lookup.containsKey(from)||!lookup.containsKey(to)) throw new IllegalArgumentException("Origem ou destino fora da rede do piloto.");
        Set<Integer> unsuitable=new TreeSet<>();
        for(var e:edges) if(!eligible(e,constraints,now)) unsuitable.add(e.id());
        Set<Integer> excluded=new TreeSet<>(unsuitable);excluded.addAll(blocked);
        var path=shortest(lookup,edges,excluded,from,to);
        var baseline=shortest(lookup,edges,unsuitable,from,to);
        Double baselineDistance=baseline==null?null:round(baseline.distance());
        Double detour=path==null||baseline==null?null:round(Math.max(0,path.distance()-baseline.distance()));
        Double percent=detour==null||baseline.distance()==0?null:round(detour*100/baseline.distance());
        var warnings=List.of(
            "Rede, atributos e distâncias de demonstração: não use este percurso para deslocamento real.",
            "Trechos sem dados atuais, com degraus, piso incompatível ou abaixo dos critérios foram excluídos.",
            "Relatos bloqueantes pendentes são tratados conservadoramente; não equivalem a vistoria.",
            "Limites de largura e inclinação são preferências experimentais, não certificação normativa.",
            "Distância de referência ignora relatos, mas mantém os mesmos critérios; não é a menor rota da cidade.");
        return new RouteResult(path!=null,path==null?List.of():path.nodes(),path==null?List.of():path.edges(),
            path==null?0:round(path.distance()),baselineDistance,detour,percent,List.copyOf(excluded),
            path==null?"Não encontramos percurso com os dados e critérios disponíveis.":
            from==to?"Origem e destino coincidem; nenhum trecho percorrido.":"Percurso calculado apenas na rede sintética do piloto.",
            warnings,"Dijkstra — Java 21",(System.nanoTime()-begun)/1_000_000.0,true,now);
    }
    static boolean eligible(Edge e, Constraints c, long now) {
        return e.distanceM()>0 && Double.isFinite(e.distanceM()) && e.stepFree()
            && e.widthCm()>=c.minWidthCm() && e.slopePct()<=c.maxSlopePct()
            && "REGULAR".equals(e.surface()) && "KNOWN".equals(e.conditionStatus())
            && e.verifiedAt()!=null && e.verifiedAt()<=now
            && now-e.verifiedAt()<=c.maxAgeDays()*86_400_000L;
    }
    private static Path shortest(Map<Integer,Node> nodes,List<Edge> edges,Set<Integer> excluded,int from,int to) {
        Map<Integer,List<Arc>> graph=new HashMap<>();nodes.keySet().forEach(id->graph.put(id,new ArrayList<>()));
        for(var e:edges) if(!excluded.contains(e.id()) && graph.containsKey(e.fromNode()) && graph.containsKey(e.toNode())) {
            graph.get(e.fromNode()).add(new Arc(e.toNode(),e));
            if(e.bidirectional()) graph.get(e.toNode()).add(new Arc(e.fromNode(),e));
        }
        Map<Integer,Double> distances=new HashMap<>(); Map<Integer,Arc> parent=new HashMap<>();
        PriorityQueue<QueueItem> queue=new PriorityQueue<>(Comparator.comparingDouble(QueueItem::distance).thenComparingInt(QueueItem::node));
        distances.put(from,0.0);queue.add(new QueueItem(from,0));
        while(!queue.isEmpty()) {
            var current=queue.poll();
            if(current.distance()>distances.getOrDefault(current.node(),Double.POSITIVE_INFINITY)) continue;
            if(current.node()==to) break;
            for(var arc:graph.get(current.node())) {
                double next=current.distance()+arc.edge().distanceM();
                if(next<distances.getOrDefault(arc.to(),Double.POSITIVE_INFINITY)) {
                    distances.put(arc.to(),next);parent.put(arc.to(),new Arc(current.node(),arc.edge()));queue.add(new QueueItem(arc.to(),next));
                }
            }
        }
        if(!distances.containsKey(to)) return null;
        LinkedList<Node> resultNodes=new LinkedList<>();LinkedList<Edge> resultEdges=new LinkedList<>();
        int cursor=to;resultNodes.addFirst(nodes.get(cursor));
        while(cursor!=from) {var p=parent.get(cursor);resultEdges.addFirst(p.edge());cursor=p.to();resultNodes.addFirst(nodes.get(cursor));}
        return new Path(List.copyOf(resultNodes),List.copyOf(resultEdges),distances.get(to));
    }
    private static double round(double value) { return Math.round(value*100)/100.0; }
}
