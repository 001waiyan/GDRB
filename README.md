# FactChecker API

A REST API FactChecker tool that analyzes graph data and computes confidence scores for relationships. This is a forked repository of [GDRB](https://github.com/wsu-db/GDRB), for the purposes of integration with [WW_AI_GK](https://github.com/AY2425S1-DSA3101-Weeping-Wranglers/WW-AI-GK).

## Prerequisites

- Java 11 or higher
- Maven 3.6 or higher

## Running the API

Build the project:

   ```bash
   mvn clean package
   ```

Start the server:

```bash
java -jar target/factchecker-api-1.0-SNAPSHOT.jar
```

The API will be available at `http://localhost:8080`.

## API Endpoints

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
  -F "graphNodes=@path/to/graph_nodes.tsv" \
  -F "graphEdges=@path/to/graph_edges.tsv" \
  -F "graphOntology=@path/to/graph_ontology.tsv" \
  -F "inputEdges=@path/to/input_edges.tsv" \
  -F "minSupp=0.01" \
  -F "minConf=0.0001" \
  -F "maxSize=4" \
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
            "hits": [0, "topK"],
            "maxConf": [0.0, 1.0],
            "suppForMaxConf": [0.0, 1.0],
            "maxScore": [0.0, 1.0]
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
"rules": array of objects, fact checking scores of input edges
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
