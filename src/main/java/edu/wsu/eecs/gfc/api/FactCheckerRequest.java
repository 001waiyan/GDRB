package edu.wsu.eecs.gfc.api;

import org.springframework.web.multipart.MultipartFile;

public class FactCheckerRequest {
    private MultipartFile graphNodes;
    private MultipartFile graphEdges;
    private MultipartFile graphOntology;
    private MultipartFile inputEdges;
    private double minSupp;
    private double minConf;
    private int maxSize;
    private int topK;

    // Getters and setters
    public MultipartFile getGraphNodes() { return graphNodes; }
    public void setGraphNodes(MultipartFile graphNodes) { this.graphNodes = graphNodes; }
    public MultipartFile getGraphEdges() { return graphEdges; }
    public void setGraphEdges(MultipartFile graphEdges) { this.graphEdges = graphEdges; }
    public MultipartFile getGraphOntology() { return graphOntology; }
    public void setGraphOntology(MultipartFile graphOntology) { this.graphOntology = graphOntology; }
    public MultipartFile getInputEdges() { return inputEdges; }
    public void setInputEdges(MultipartFile inputEdges) { this.inputEdges = inputEdges; }
    public double getMinSupp() { return minSupp; }
    public void setMinSupp(double minSupp) { this.minSupp = minSupp; }
    public double getMinConf() { return minConf; }
    public void setMinConf(double minConf) { this.minConf = minConf; }
    public int getMaxSize() { return maxSize; }
    public void setMaxSize(int maxSize) { this.maxSize = maxSize; }
    public int getTopK() { return topK; }
    public void setTopK(int topK) { this.topK = topK; }
}