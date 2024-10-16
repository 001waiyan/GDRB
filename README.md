# GDRB

Graph Data Regularities Benchmark (GDRB)

## Introduction

This is a forked repository of [GDRB](https://github.com/wsu-db/GDRB), for the purposes of integration with [WW_AI_GK](https://github.com/AY2425S1-DSA3101-Weeping-Wranglers/WW-AI-GK).

## Quick build

### Requirements

JDK 1.8+ and Maven 3.0+

### ExtractGFCs

#### Arguments

`ExtractGFCs` expects 7 arguments:

1. `inputDir`: Directory of input files. The following files should be present:

   - `gfc_str_nodes.tsv`: Nodes in the graph
     Fields: `id`, `label`
   - `gfc_str_edges.tsv`: Edges in the graph
     Fields: `srcId`, `dstId`, `label`
   - `gfc_input_relations.tsv`: Relations to extract rules from
     Fields: `srcLabel`, `dstLabel`, `edgeLabel`
   - `gfc_str_ontology.tsv`: Ontology of graph
     Fields:
2. `outputDir`: Directory of the output file. Will be created if doesn't exist.
3. `outputFileName` Name of the output json file. Will be overriden if already exists.
4. `minSupp`: Minimum support of GFCs, Range: [0.0, 1.0]
5. `minConf`: Minimum confidence of GFCs, Range: [0.0, 1.0]
6. `maxSize`: Maximum size of extracted patterns
7. `topK`: Number of patterns extracted for each relation

#### Invocation Example

```
$ mvn package
$ java -cp ./target/factchecking-1.0-SNAPSHOT-jar-with-dependencies.jar \
    edu.wsu.eecs.gfc.exps.ExtractGFCs \
        ./sample_data/ \
        ./output \
        ./rules.json \
        0.01 \
        0.0001 \
        4 \
        50
```

#### Output Schema

```json
   [
   	{
   		"src": "srcLabel",
   		"dst": "dstLabel",
   		"label": "edgeLabel",
   		"patterns": [
   			{
   				"relations": [
   					{
   						"src": "srcLabel",
   						"dst": "dstLabel",
   						"label": "edgeLabel"
   					}
   				],
   				"supp": [0.0, 1.0],
   				"conf": [0.0, 1.0]
   			}
   		]
   	}
   ]
```

   rules: array of objects, one for each relation in `gfc_input_relations.tsv`
	- src: string, label of source node in relation
	- dst: string, label of destination node in relation
	- label: string, label of relation
	- patterns: array of objects, `topK` patterns
		- relations: array of objects, relations in the extracted pattern
			- src: string, label of source node in pattern relation
			- dst: string, label of destination node in pattern relation
			- label: string, label of pattern relation
		- supp: double, support of pattern
		- conf: double, confidence of pattern

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
