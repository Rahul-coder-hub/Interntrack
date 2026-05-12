package com.interntrack.service;

import com.interntrack.model.Intern;
import com.interntrack.repository.InternRepository;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class InternService {
    private final InternRepository repository;

    public InternService(InternRepository repository) {
        this.repository = repository;
    }

    public List<Intern> listInterns(String query) throws SQLException {
        return repository.findAll(query);
    }

    public DashboardStats getStats(List<Intern> interns) {
        int scheduled = 0;
        int reviewed = 0;
        int selected = 0;
        int totalScore = 0;

        for (Intern intern : interns) {
            if ("Scheduled".equalsIgnoreCase(intern.getReviewStatus())) {
                scheduled++;
            } else if ("Reviewed".equalsIgnoreCase(intern.getReviewStatus())) {
                reviewed++;
            } else if ("Selected".equalsIgnoreCase(intern.getReviewStatus())) {
                selected++;
            }
            totalScore += intern.getScore();
        }

        int average = interns.isEmpty() ? 0 : Math.round((float) totalScore / interns.size());
        return new DashboardStats(interns.size(), scheduled, reviewed, selected, average);
    }

    public void addIntern(Map<String, String> form) throws SQLException {
        Intern intern = new Intern(
                0,
                required(form, "fullName"),
                required(form, "email"),
                required(form, "college"),
                required(form, "domain"),
                required(form, "projectTitle"),
                required(form, "skills"),
                required(form, "reviewStatus"),
                parseScore(form.get("score")),
                form.getOrDefault("mentorNote", "").trim(),
                LocalDateTime.now()
        );
        repository.save(intern);
    }

    public void changeStatus(int id, String status) throws SQLException {
        repository.updateStatus(id, status);
    }

    public void deleteIntern(int id) throws SQLException {
        repository.deleteById(id);
    }

    private String required(Map<String, String> form, String key) {
        String value = form.getOrDefault(key, "").trim();
        if (value.isEmpty()) {
            throw new IllegalArgumentException(key + " is required");
        }
        return value;
    }

    private int parseScore(String value) {
        try {
            int score = Integer.parseInt(value == null ? "0" : value.trim());
            return Math.max(0, Math.min(100, score));
        } catch (NumberFormatException ex) {
            return 0;
        }
    }
}
