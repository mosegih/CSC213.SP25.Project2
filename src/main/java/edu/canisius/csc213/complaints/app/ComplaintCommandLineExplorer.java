package edu.canisius.csc213.complaints.app;

import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import edu.canisius.csc213.complaints.model.Complaint;
import edu.canisius.csc213.complaints.service.ComplaintSimilarityService;
import edu.canisius.csc213.complaints.storage.ComplaintLoader;

/**
 * A diagnostic runner that loads complaints and prints the
 * 3 most similar complaints (by embedding) for each company.
 */
public class ComplaintCommandLineExplorer {

    public static void main(String[] args) {
        try {

            // 🔧 Load configuration from config.properties
            Properties config = new Properties();
            try (InputStream in = ComplaintCommandLineExplorer.class.getResourceAsStream("/config.properties")) {
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

            ComplaintSimilarityService similarityService = new ComplaintSimilarityService(complaints);

            for (Complaint c : complaints) {
                System.out.println("===================================================");
                System.out.println("📌 Complaint ID: " + c.getComplaintId());
                System.out.println("🏢 Company: " + c.getCompany());
                System.out.println("🗣️ Narrative:");
                System.out.println(c.getNarrative());
                System.out.println("---------------------------------------------------");
                System.out.println("🔍 Top 3 Most Similar Complaints:");

                List<Complaint> similar = similarityService.findTop3Similar(c);
                for (int i = 0; i < similar.size(); i++) {
                    Complaint s = similar.get(i);
                    System.out.println("  [" + (i + 1) + "] ID: " + s.getComplaintId() + " | Company: " + s.getCompany());
                    System.out.println("      " + truncate(s.getNarrative(), 200));
                    System.out.println();
                }

                System.out.println("===================================================\n");
            }

        } catch (Exception e) {
            System.err.println("❌ Failed to process complaints:");
            e.printStackTrace();
        }
    }

    /**
     * Truncates long strings for cleaner printing.
     */
    private static String truncate(String text, int maxLength) {
        if (text == null) return "(no narrative)";
        return text.length() <= maxLength ? text : text.substring(0, maxLength) + "...";
    }
}
