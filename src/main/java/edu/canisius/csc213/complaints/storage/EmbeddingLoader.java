package edu.canisius.csc213.complaints.storage;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class EmbeddingLoader {

    /**
     * Loads complaint embeddings from a JSONL (newline-delimited JSON) file.
     * Each line must be a JSON object with:
     * {
     *   "complaintId": <long>,
     *   "embedding": [<double>, <double>, ...]
     * }
     *
     * @param jsonlStream InputStream to the JSONL file
     * @return A map from complaint ID to its embedding vector
     * @throws IOException if the file cannot be read or parsed
     */
    public static Map<Long, double[]> loadEmbeddings(InputStream jsonlStream) throws IOException {
        // BufferedReader reader = new BufferedReader(new InputStreamReader(jsonlStream));
        Map<Long, double[]> embeddings = new HashMap<>();
        Scanner scanner = new Scanner(jsonlStream);


        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            //System.out.println(line);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode json = mapper.readTree(line);
            long id = json.get("id").asLong();
            JsonNode embeddingNode = json.get("embedding");

            // System.out.println(id);
            // System.out.println(embeddingNode);
            
            double[] embedding = new double[embeddingNode.size()];
            for(int i = 0; i < embeddingNode.size(); i++) {
                embedding[i] = embeddingNode.get(i).asDouble();
            }

            embeddings.put(id, embedding);

        }
        scanner.close();

        return embeddings;
    }
}
