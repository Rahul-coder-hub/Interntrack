package com.interntrack.model;

import java.time.LocalDateTime;

public class Intern {
    private int id;
    private String fullName;
    private String email;
    private String college;
    private String domain;
    private String projectTitle;
    private String skills;
    private String reviewStatus;
    private int score;
    private String mentorNote;
    private LocalDateTime createdAt;

    public Intern(
            int id,
            String fullName,
            String email,
            String college,
            String domain,
            String projectTitle,
            String skills,
            String reviewStatus,
            int score,
            String mentorNote,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.college = college;
        this.domain = domain;
        this.projectTitle = projectTitle;
        this.skills = skills;
        this.reviewStatus = reviewStatus;
        this.score = score;
        this.mentorNote = mentorNote;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getCollege() {
        return college;
    }

    public String getDomain() {
        return domain;
    }

    public String getProjectTitle() {
        return projectTitle;
    }

    public String getSkills() {
        return skills;
    }

    public String getReviewStatus() {
        return reviewStatus;
    }

    public int getScore() {
        return score;
    }

    public String getMentorNote() {
        return mentorNote;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
