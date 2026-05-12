package com.interntrack.service;

public class DashboardStats {
    private final int total;
    private final int scheduled;
    private final int reviewed;
    private final int selected;
    private final int averageScore;

    public DashboardStats(int total, int scheduled, int reviewed, int selected, int averageScore) {
        this.total = total;
        this.scheduled = scheduled;
        this.reviewed = reviewed;
        this.selected = selected;
        this.averageScore = averageScore;
    }

    public int getTotal() {
        return total;
    }

    public int getScheduled() {
        return scheduled;
    }

    public int getReviewed() {
        return reviewed;
    }

    public int getSelected() {
        return selected;
    }

    public int getAverageScore() {
        return averageScore;
    }
}
