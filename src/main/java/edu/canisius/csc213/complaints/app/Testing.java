package edu.canisius.csc213.complaints.app;

import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import edu.canisius.csc213.complaints.model.Complaint;
import edu.canisius.csc213.complaints.storage.ComplaintLoader;

/**
 * A diagnostic runner that loads complaints and prints the
 * 3 most similar complaints (by embedding) for each company.
 */
public class Testing {

    public static void main(String[] args) {
        try {

            // 🔧 Load configuration from config.properties
            Properties config = new Properties();
            try (InputStream in = Testing.class.getResourceAsStream("/config.properties")) {
                if (in == null) {
                    throw new IllegalStateException("Missing config.properties in resources!");
                }
                config.load(in);
            }

            String csvPath = config.getProperty("complaints.csv");
            String jsonlPath = config.getProperty("embeddings.jsonl");
            // Load complaints & embeddings
            List<Complaint> complaints = ComplaintLoader.loadComplaintsWithEmbeddings(
                   csvPath,
                    jsonlPath
            );




        } catch (Exception e) {
            System.err.println("❌ Failed to process complaints:");
            e.printStackTrace();
        }
    }
}