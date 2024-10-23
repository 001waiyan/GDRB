# GDRB

Graph Data Regularities Benchmark (GDRB)

## Introduction

This is a forked repository of [GDRB](https://github.com/wsu-db/GDRB), for the purposes of integration with [WW_AI_GK](https://github.com/AY2425S1-DSA3101-Weeping-Wranglers/WW-AI-GK).

## Quick build

### Requirements

JDK 1.8+ and Maven 3.0+

### FactChecker

#### Arguments

`FactChecker` expects 7 arguments:

1. `inputDir`: Directory of input files. The following files should be present:
   - `graph_nodes.tsv`: Nodes in the graph.
   		- Fields: `id`, `label`
   - `graph_edges.tsv`: Edges in the graph.
   		- Fields: `srcId`, `dstId`, `edgeLabel`
   - `graph_ontology.tsv`: Ontology of graph.
   		- Fields: `childLabel`, `parentLabel`
   - `input_edges.tsv`: Edges to test for confidence. All edges should be for the same relation type.
   		- Fields: `srcId`, `dstId`, `edgeLabel`
1. `outputDir`: Directory of the output files. Will be created if doesn't exist.
1. `minSupp`: Minimum support of GFCs, Range: [0.0, 1.0]
1. `minConf`: Minimum confidence of GFCs, Range: [0.0, 1.0]
1. `maxSize`: Maximum size of extracted patterns
1. `topK`: Number of patterns extracted for each relation

#### Invocation Example

```
$ mvn package
$ java -cp ./target/factchecking-1.0-SNAPSHOT-jar-with-dependencies.jar \
    edu.wsu.eecs.gfc.exps.FactChecker \
        ./sample_data/ \
        ./output \
        0.01 \
        0.0001 \
        4 \
        50
```

#### Output Schema
##### `patterns.json`
```json
[
	{
		"relations": [
			{
				"srcLabel": "srcLabel",
				"dstLabel": "dstLabel",
				"edgeLabel": "edgeLabel"
			}
		],
		"supp": [0.0, 1.0],
		"conf": [0.0, 1.0]
	}
]
```
```
array of objects, topK patterns
	⎿ relations: array of objects, relations in the extracted pattern
		⎿  srcLabel: string, label of source node in pattern relation
		⎿  dstLabel: string, label of destination node in pattern relation
		⎿  edgeLabel: string, label of pattern relation
	⎿  supp: double, support of pattern
	⎿  conf: double, confidence of pattern
```
##### `results.json`
```json
[
	{
		"srcId": "srcId",
		"dstId": "dstId",
		"label": "edgeLabel",
		"hits": [0, "topK"],
		"maxConf": [0.0, 1.0],
		"suppForMaxConf": [0.0, 1.0],
		"maxScore": [0.0, 1.0]
	}
]
```
```
array of objects, fact checking scores of input edges
	⎿  src: string, id of source node in edge
	⎿  dst: string, id of destination node in edge
	⎿  edgeLabel: string, label of edge
	⎿  supp: double, support of pattern
	⎿  conf: double, confidence of pattern
```

## GFC documents

Paper: 2018-DASFAA-GFC-paper.pdf

Slides: 2018-DASFAA-GFC-slides.pptx

## Reference

```
@inproceedings{lin2018discovering,
  title={Discovering Graph Patterns for Fact Checking in Knowledge Graphs},
  author={Lin, Peng and Song, Qi and Shen, Jialiang and Wu, Yinghui},
  booktitle={International Conference on Database Systems for Advanced Applications},
  pages={783--801},
  year={2018},
  organization={Springer}
}
```

### Contact

peng.lin@wsu.edu

http://eecs.wsu.edu/~plin1

### License

MIT License
