package edu.canisius.csc213.complaints.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import edu.canisius.csc213.complaints.model.Complaint;
import edu.canisius.csc213.complaints.service.ComplaintSimilarityService;

@Controller
public class ComplaintController {

    private final List<Complaint> complaints;
    private final ComplaintSimilarityService similarityService;

    public ComplaintController(List<Complaint> complaints, ComplaintSimilarityService similarityService) {
        this.complaints = complaints;
        this.similarityService = similarityService;
    }

    @GetMapping("/complaint")
    public String showComplaint(@RequestParam(defaultValue = "0") String index, Model model) {
        int max = complaints.size();
        String error = null;
        int indexInt = 0;
        try {
            indexInt = Integer.parseInt(index);
        } catch (NumberFormatException e) {
            error = "Index must be a number. Going to Index 0.";
        }

        if (indexInt < 0) {
            indexInt = 0;
            error = "Index cannot be negative. Going to Index 0.";
        }

        if (indexInt >= max) {
            indexInt = max - 1;
            error = "Index exceeds the number of complaints. Going to Index " + (max - 1) + ".";
        }

        Complaint current = complaints.get(indexInt);
        List<Complaint> similar = similarityService.findTop3Similar(current);

        model.addAttribute("complaint", current);
        model.addAttribute("similarComplaints", similar);
        model.addAttribute("prevIndex", indexInt > 0 ? indexInt - 1 : 0);
        model.addAttribute("nextIndex", indexInt < max - 1 ? indexInt + 1 : max - 1);
        model.addAttribute("error", error);

        return "complaint"; // ← This maps to complaint.html
    }

    @GetMapping("/search")
    public String showSearch(@RequestParam(defaultValue = "0") String company, Model model) {
        String error = null;

        if(company.equals("0")){
            error = "Enter a company name to search.";
            model.addAttribute("error", error);
            return "search";
        }

        List<Complaint> searchResults = new ArrayList<Complaint>();

        for (Complaint complaint : complaints) {
            if (complaint.getCompany().toLowerCase().contains(company.toLowerCase())) {
                searchResults.add(complaint);
            }
        }
        if (searchResults.isEmpty()) {
            error = "No complaints found for the company: " + company;
        }

        model.addAttribute("searchResults", searchResults);
        model.addAttribute("error", error);

        return "search";
    }
}
