package edu.canisius.csc213.complaints.service;

import java.util.List;

import edu.canisius.csc213.complaints.model.Complaint;

public class ComplaintSimilarityService {

    private final List<Complaint> complaints;

    public ComplaintSimilarityService(List<Complaint> complaints) {
        this.complaints = complaints;
    }
    /**
     * Finds the top 3 most similar complaints to a given target complaint.
     *
     * @param target The target complaint
     * @return A list of the top 3 most similar complaints
     */

    public List<Complaint> findTop3Similar(Complaint target) {
        double[] targetEmbedding = target.getEmbedding();
        if (targetEmbedding == null) return List.of();

        List<ComplaintWithScore> scored = complaints.stream()
            .filter(c -> c != target && c.getEmbedding() != null)
            .map(c -> new ComplaintWithScore(c, cosineSimilarity(targetEmbedding, c.getEmbedding())))
            .sorted((a, b) -> Double.compare(b.score, a.score))
            .limit(3)
            .toList();

        return scored.stream().map(cws -> cws.complaint).toList();
    }

    private double cosineSimilarity(double[] a, double[] b) {
        if (a == null || b == null || a.length != b.length) return -1;

        double dot = 0.0, normA = 0.0, normB = 0.0;
        for (int i = 0; i < a.length; i++) {
            dot += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }

        if (normA == 0 || normB == 0) return -1;

        return dot / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    private static class ComplaintWithScore {
        Complaint complaint;
        double score;

        ComplaintWithScore(Complaint c, double s) {
            this.complaint = c;
            this.score = s;
        }
    }
}
// Compare this snippet from src/main/java/edu/canisius/csc213/complaints/model/Complaint.java:
// package edu.canisius.csc213.complaints.model;
//
// import com.opencsv.bean.CsvBindByName;
// import com.opencsv.bean.CsvToBean;
// import com.opencsv.bean.CsvToBeanBuilder;    