# FactChecker API

This is a forked repository of [GDRB](https://github.com/wsu-db/GDRB), for the purposes of integration with [WW_AI_GK](https://github.com/AY2425S1-DSA3101-Weeping-Wranglers/WW-AI-GK).

This tool runs the GFC mining algorithm to extract rules (GFCs) from a knowledge graph. The given input edges are then checked if they are supported by the extracted rules. A REST API was set up using Spring Boot.

Modifications:
1. Added support for multiple edges between nodes
2. Filters for patterns that are at least weakly connected so as to generate more useful patterns.

## Running the API

### Run Docker Image from GHCR

```bash
docker run -p 8080:8080 ghcr.io/001waiyan/factchecker-api:latest
```

### Build and Run

#### Build and run with Docker

```bash
docker build -t factchecker-api
docker run -p 8080:8080 factchecker-api
```

#### Build and run manually

Prerequisites:

- Java 11 or higher
- Maven 3.6 or higher

```bash
mvn clean package
java -jar target/factchecker-api-1.0-SNAPSHOT.jar
```

## API Endpoints

The API will be available at `http://localhost:8080`.

### Check Facts

Analyzes graph data and computes confidence scores.

**URL**: `/api/factchecker/check`

**Method**: `POST`

**Content-Type**: `multipart/form-data`

**Parameters**:

| Parameter     | Type    | Required | Description                                                          |
| ------------- | ------- | -------- | -------------------------------------------------------------------- |
| graphNodes    | File    | Yes      | TSV file containing graph nodes (Fields: id, label)                  |
| graphEdges    | File    | Yes      | TSV file containing graph edges (Fields: srcId, dstId, edgeLabel)    |
| graphOntology | File    | Yes      | TSV file containing graph ontology (Fields: childLabel, parentLabel) |
| inputEdges    | File    | Yes      | TSV file containing edges to test (Fields: srcId, dstId, edgeLabel)  |
| minSupp       | Number  | Yes      | Minimum support of GFCs (Range: 0.0 to 1.0)                          |
| minConf       | Number  | Yes      | Minimum confidence of GFCs (Range: 0.0 to 1.0)                       |
| maxSize       | Integer | Yes      | Maximum size of extracted patterns                                   |
| topK          | Integer | Yes      | Number of patterns extracted for each relation                       |

**Response**:

- Content-Type: `application/json`
- The response will contain the analysis results in JSON format

**Example Request**:

```bash
curl -X POST \
  -F "graphNodes=@sample_data/graph_nodes.tsv" \
  -F "graphEdges=@sample_data/graph_edges.tsv" \
  -F "graphOntology=@sample_data/graph_ontology.tsv" \
  -F "inputEdges=@sample_data/input_edges.tsv" \
  -F "minSupp=0.01" \
  -F "minConf=0.0001" \
  -F "maxSize=2" \
  -F "topK=50" \
  http://localhost:8080/api/factchecker/check
```

## Input File Formats

### graph_nodes.tsv

```
id    label
1     Entity1
2     Entity2
```

### graph_edges.tsv

```
srcId    dstId    edgeLabel
1        2        relationshipType
```

### graph_ontology.tsv

```
childLabel    parentLabel
Type1         ParentType1
```

### input_edges.tsv
Note: All edges must be for the same relation type (i.e. same srcLabel, dstLabel and edgeLabel)
```
srcId    dstId    edgeLabel
1        2        relationshipToTest
```

## Output Schema

```json
{
    "patterns": [
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
    ],
    "results": [
        {
            "srcId": "srcId",
            "dstId": "dstId",
            "label": "edgeLabel",
            "hits": [0, "topK"]
        }
    ]
}

```

```
"patterns": array of objects, topK patterns
	⎿ relations: array of objects, relations in the extracted pattern
		⎿  srcLabel: string, label of source node in pattern relation
		⎿  dstLabel: string, label of destination node in pattern relation
		⎿  edgeLabel: string, label of pattern relation
	⎿  supp: double, support of pattern
	⎿  conf: double, confidence of pattern
"results": array of objects, fact checking scores of input edges
	⎿  srcId: string, id of source node in edge
	⎿  dstId: string, id of destination node in edge
	⎿  edgeLabel: string, label of edge
	⎿  hits: int, number of patterns that cover the edge
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
