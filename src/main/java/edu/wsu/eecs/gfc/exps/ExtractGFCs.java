package edu.wsu.eecs.gfc.exps;

import com.google.common.base.Stopwatch;
import edu.wsu.eecs.gfc.core.*;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * The caller to test GFC mining.
 * @author Peng Lin penglin03@gmail.com
 */
public class ExtractGFCs {

    private static final int GLOBAL_HOPS = 0;

    public static void main(String[] args) throws Exception {
        String inputDir = args[0];
        String outputDir = args[1];
        String outputFileName = args[2];
        new File(outputDir).mkdirs();
        String outputFilePath = outputDir + "/" + outputFileName;

        double minSupp = Double.parseDouble(args[3]);
        double minConf = Double.parseDouble(args[4]);
        int maxSize = Integer.parseInt(args[5]);
        int topK = Integer.parseInt(args[6]);

        System.out.println("Configurations:"
                + "\nInputDir = " + inputDir
                + "\noutputFilePath = " + outputFilePath
                + "\nminSupp = " + minSupp
                + "\nminConf = " + minConf
                + "\nmaxSize = " + maxSize
                + "\ntop-K = " + topK);

        System.out.println("Loading the data graph....");
        Graph<String, String> graph = IO.loadStringGraph(inputDir);
        System.out.println("Graph: " + graph.toSizeString());

        System.out.println("Loading the ontology....");
        DirectedAcyclicGraph<String, String> onto = IO.loadDAGOntology(inputDir);
        System.out.println("Indexing the ontology....");
        Map<String, Map<Integer, Set<String>>> ontoIndex = Utility.indexOntology(onto, GLOBAL_HOPS);

        System.out.println("Indexing the data graph....");
        GraphDatabase<String, String> bigGraph = GraphDatabase.buildFromGraph(graph, ontoIndex);
        System.out.println("BigGraph: " + bigGraph.toSizeString());

        System.out.println("Loading the input relations....");
        List<Relation<String, String>> relationList = IO.loadRelations(inputDir);
        System.out.println(relationList.size() + " relations loaded.");

        RuleMiner<String, String> miner = RuleMiner.createInit(bigGraph, minSupp, minConf, maxSize, topK);

        JSONArray allRulesJson = new JSONArray();

        for (Relation<String, String> r : relationList) {
            System.out.println("----------------------------------------");
            System.out.println("Testing for r(x, y) = " + r);

            JSONObject currRule = new JSONObject();
            currRule.put("src", r.srcLabel());
            currRule.put("dst", r.dstLabel());
            currRule.put("label", r.edgeLabel());

            List<Relation<String, String>> inputRelations = new ArrayList<>();
            inputRelations.add(r);
            FactSampler<String, String> sampler = new FactSampler<>(bigGraph, new ArrayList<>(inputRelations));

            if (sampler.getDataTest().get(true).size() == 0) {
                System.out.println("Not enough true testing data. Skip....");
                continue;
            }
            if (sampler.getDataTest().get(false).size() == 0) {
                System.out.println("Not enough false testing data. Skip....");
                continue;
            }

            bigGraph.buildSimLabelsMap(0);

            Stopwatch w = Stopwatch.createStarted();
            List<OGFCRule<String, String>> patterns = miner.OGFC_stream(r, sampler.getDataTrain().get(true), sampler.getDataTrain().get(false));
            w.stop();

            System.out.println("Discovered number of patterns: |P| = " + patterns.size() + ", Time = " + w.elapsed(TimeUnit.SECONDS));
            
            JSONArray allPatternsJson = new JSONArray();

            for (OGFCRule<String, String> rule: patterns) {
                JSONObject currPatternJson = new JSONObject();

                Map<Object, String> nodeMap = new HashMap<>();

                for(Node<String> node: rule.P().nodeIter()) {
                    nodeMap.put(node.id(), node.label());
                }

                JSONArray edgesJson = new JSONArray();
                for (Edge<String, String> edge: rule.P().edgeIter()) {
                    JSONObject currEdgeJson = new JSONObject();
                    currEdgeJson.put("src", nodeMap.get(edge.srcId()));
                    currEdgeJson.put("dst", nodeMap.get(edge.dstId()));
                    currEdgeJson.put("label", edge.label());
                    edgesJson.put(currEdgeJson);
                }
                currPatternJson.put("relations", edgesJson);

                currPatternJson.put("supp", rule.supp);
                currPatternJson.put("conf", rule.conf);

                allPatternsJson.put(currPatternJson);
            }

            currRule.put("patterns", allPatternsJson);
            allRulesJson.put(currRule);

            System.out.println("Restore the sampled facts....");
            sampler.restore();
        }
        FileWriter jsonFile = new FileWriter(outputFilePath);
        jsonFile.write(allRulesJson.toString(4));
        jsonFile.close();

        System.out.println("-------------------DONE-----------------");
    }
}