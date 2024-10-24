package edu.wsu.eecs.gfc.api;

import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@RestController
@RequestMapping("/api/factchecker")
public class FactCheckerController {

    @PostMapping("/check")
    public ResponseEntity<?> checkFacts(
            @RequestParam("graphNodes") MultipartFile graphNodes,
            @RequestParam("graphEdges") MultipartFile graphEdges,
            @RequestParam("graphOntology") MultipartFile graphOntology,
            @RequestParam("inputEdges") MultipartFile inputEdges,
            @RequestParam("minSupp") double minSupp,
            @RequestParam("minConf") double minConf,
            @RequestParam("maxSize") int maxSize,
            @RequestParam("topK") int topK) throws IOException {

        // Create temporary directory for input files
        String sessionId = UUID.randomUUID().toString();
        Path tempDir = Files.createTempDirectory("factchecker-input-" + sessionId);
        
        try {
            // Save uploaded files
            saveFile(graphNodes, tempDir.resolve("graph_nodes.tsv"));
            saveFile(graphEdges, tempDir.resolve("graph_edges.tsv"));
            saveFile(graphOntology, tempDir.resolve("graph_ontology.tsv"));
            saveFile(inputEdges, tempDir.resolve("input_edges.tsv"));

            // Run FactChecker
            edu.wsu.eecs.gfc.exps.FactChecker.main(new String[]{
                tempDir.toString(),
                String.valueOf(minSupp),
                String.valueOf(minConf),
                String.valueOf(maxSize),
                String.valueOf(topK)
            });

            // Read the JSON output file
            String jsonContent = Files.readString(tempDir.resolve("output.json"));

            // Return JSON response
            return ResponseEntity
                    .ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(jsonContent);

        } catch (Exception e) {
            return ResponseEntity
                .internalServerError()
                .body(e.getClass().getName() + ": " + e.getMessage());
        } finally {
            // Clean up temporary directory
            deleteDirectory(tempDir.toFile());
        }
    }

    private void saveFile(MultipartFile file, Path destination) throws Exception {
        file.transferTo(destination);
    }

    private void deleteDirectory(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
        }
        directory.delete();
    }
}