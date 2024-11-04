package edu.wsu.eecs.gfc.exps;

import edu.wsu.eecs.gfc.core.*;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * The caller to test GFC mining.
 * @author Peng Lin penglin03@gmail.com
 */
public class FactChecker {

    private static final int GLOBAL_HOPS = 2;

    public static void main(String[] args) throws Exception{
        String tempDir = args[0];
        double minSupp = Double.parseDouble(args[1]);
        double minConf = Double.parseDouble(args[2]);
        int maxSize = Integer.parseInt(args[3]);
        int topK = Integer.parseInt(args[4]);

        System.out.println("Loading the data graph....");
        Graph<String, String> graph = IO.loadStringGraph(tempDir);
        System.out.println("Graph: " + graph.toSizeString());

        System.out.println("Loading the ontology....");
        DirectedAcyclicGraph<String, String> onto = IO.loadDAGOntology(tempDir);
        System.out.println("Indexing the ontology....");
        Map<String, Map<Integer, Set<String>>> ontoIndex = Utility.indexOntology(onto, GLOBAL_HOPS);

        System.out.println("Indexing the data graph....");
        GraphDatabase<String, String> bigGraph = GraphDatabase.buildFromGraph(graph, ontoIndex);
        System.out.println("BigGraph: " + bigGraph.toSizeString());

        System.out.println("Loading input edges....");
        List<Edge<String, String>> inputEdges = IO.loadInputEdges(graph, tempDir);

        if (inputEdges.size() == 0) {
            throw new Exception("No input edges provided.");
        }

        String srcLabel = inputEdges.get(0).srcLabel();
        String dstLabel = inputEdges.get(0).dstLabel();
        String edgeLabel = inputEdges.get(0).label();
        Relation<String, String> relation = Relation.createRelation(srcLabel, dstLabel, edgeLabel);
        System.out.println("Mining rules for relation: " + relation.toString());

        RuleMiner<String, String> miner = RuleMiner.createInit(bigGraph, minSupp, minConf, maxSize, topK);

        List<Relation<String, String>> inputRelations = new ArrayList<>();
        inputRelations.add(relation);
        FactSampler<String, String> sampler = new FactSampler<>(bigGraph, new ArrayList<>(inputRelations));

        bigGraph.buildSimLabelsMap(0);

        List<OGFCRule<String, String>> patterns = miner.OGFC_stream(relation, sampler.getDataTrain().get(true), sampler.getDataTrain().get(false));

        List<OGFCRule<String, String>> filteredPatterns = new ArrayList<>();

        for (OGFCRule<String, String> p: patterns) {
            if (p.P().isWeaklyConnected()) {
                filteredPatterns.add(p);
            }
        }

        System.out.println("Discovered number of patterns: |P| = " + filteredPatterns.size());

        JSONArray allPatternsJson = new JSONArray();
        for (OGFCRule<String, String> p: filteredPatterns) {

            JSONArray edgesJson = new JSONArray();
            for (Edge<String, String> edge: p.P().edgeIter()) {
                JSONObject currEdgeJson = new JSONObject();
                currEdgeJson.put("srcId", edge.srcId());
                currEdgeJson.put("dstId", edge.dstId());
                currEdgeJson.put("edgeLabel", edge.label());
                edgesJson.put(currEdgeJson);
            }

            JSONArray nodesJson = new JSONArray();
            for(Node<String> node: p.P().nodeIter()) {
                nodesJson.put(node.label());
            }

            JSONObject currPatternJson = new JSONObject();
            currPatternJson.put("nodes", nodesJson);
            currPatternJson.put("edges", edgesJson);

            currPatternJson.put("supp", p.supp);
            currPatternJson.put("conf", p.conf);

            allPatternsJson.put(currPatternJson);
        }
        
        System.out.println("Checking pattern coverage for input edges....");
        boolean multipleRelations = false;
        JSONArray resultsJson = new JSONArray();
        for (Edge<String, String> edge: inputEdges) {
            if (edge.srcLabel() != srcLabel
                    || edge.dstLabel() != dstLabel
                        || edge.label() != edgeLabel) {
                multipleRelations = true;
                continue;
            }
            int hits = 0;
            for (OGFCRule<String, String> p : filteredPatterns) {
                if (p.matchSet().get(p.x()).contains(edge.srcNode())
                        && p.matchSet().get(p.y()).contains(edge.dstNode())) {
                    hits++;
                }
            }
            JSONObject currEdgeJson = new JSONObject();
            currEdgeJson.put("srcId", edge.srcId());
            currEdgeJson.put("dstId", edge.dstId());
            currEdgeJson.put("edgeLabel", edge.label());
            currEdgeJson.put("hits", hits);
            resultsJson.put(currEdgeJson);
        }

        if (multipleRelations) {
            System.out.println("Warning: Multiple relation types found in input edges.");
        }

        JSONObject combinedJson = new JSONObject();
        combinedJson.put("patterns", allPatternsJson);
        combinedJson.put("results", resultsJson);
        
        FileWriter jsonFileWriter = new FileWriter(new File(tempDir, "output.json"));
        jsonFileWriter.write(combinedJson.toString(4));
        jsonFileWriter.close();
        System.out.println("-------------------DONE-----------------");
    }
}