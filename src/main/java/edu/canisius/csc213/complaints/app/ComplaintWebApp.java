package edu.canisius.csc213.complaints.app;

import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import edu.canisius.csc213.complaints.model.Complaint;
import edu.canisius.csc213.complaints.service.ComplaintSimilarityService;
import edu.canisius.csc213.complaints.storage.ComplaintLoader;

/**
 * Entry point to run the Spring Boot web app.
 */
@SpringBootApplication(scanBasePackages = "edu.canisius.csc213.complaints")
public class ComplaintWebApp {

    public static void main(String[] args) {
        // ✅ Make sure we start the Spring Boot app *using this class*
        SpringApplication.run(ComplaintWebApp.class, args);
    }

    @Bean
    public CommandLineRunner listBeans(ApplicationContext ctx) {
        return args -> {
            System.out.println("👀 Loaded Spring Beans:");
            for (String name : ctx.getBeanDefinitionNames()) {
                if (name.contains("complaint")) {
                    System.out.println("👉 " + name);
                }
            }
        };
    }

    @Bean
    public List<Complaint> complaints() throws Exception {
        // Read config.properties from resources
        Properties config = new Properties();
        try (InputStream in = ComplaintWebApp.class.getResourceAsStream("/config.properties")) {
            if (in == null) {
                throw new IllegalStateException("Missing config.properties in resources directory!");
            }
            config.load(in);
        }

        String csvPath = config.getProperty("complaints.csv");
        String jsonlPath = config.getProperty("embeddings.jsonl");

        if (csvPath == null || jsonlPath == null) {
            throw new IllegalArgumentException("Missing file paths in config.properties.");
        }

        System.out.println("📥 Loading complaints from: " + csvPath);
        System.out.println("📥 Loading embeddings from: " + jsonlPath);

        return ComplaintLoader.loadComplaintsWithEmbeddings(csvPath, jsonlPath);
    }

    @Bean
    public ComplaintSimilarityService complaintSimilarityService(List<Complaint> complaints) {
        return new ComplaintSimilarityService(complaints);
    }
}
