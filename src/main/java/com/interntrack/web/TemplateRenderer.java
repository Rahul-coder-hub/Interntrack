package com.interntrack.web;

import com.interntrack.model.Intern;
import com.interntrack.service.DashboardStats;
import com.interntrack.service.InternService;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TemplateRenderer {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd MMM yyyy");
    private final InternService service;

    public TemplateRenderer(InternService service) {
        this.service = service;
    }

    public String dashboard(String query) throws SQLException {
        List<Intern> interns = service.listInterns(query);
        DashboardStats stats = service.getStats(interns);
        StringBuilder rows = new StringBuilder();

        for (Intern intern : interns) {
            rows.append(internCard(intern));
        }

        if (rows.isEmpty()) {
            rows.append("""
                    <section class="empty-state">
                        <h2>No records found</h2>
                        <p>Try a different search or add a new internship review.</p>
                    </section>
                    """);
        }

        return layout("Dashboard", """
                <header class="topbar">
                    <a class="brand" href="/">
                        <span class="brand-mark">IT</span>
                        <span>
                            <strong>InternTrack</strong>
                            <small>Review Manager</small>
                        </span>
                    </a>
                    <nav class="nav-links">
                        <a href="/">Dashboard</a>
                        <a href="/interns/new">New Review</a>
                    </nav>
                </header>

                <section class="hero">
                    <div>
                        <p class="eyebrow">Internship Project Review</p>
                        <h1>InternTrack</h1>
                        <p class="lead">A clean full-stack Java application for tracking interns, project review status, mentor notes, and SQL-backed performance scores.</p>
                        <div class="hero-tags">
                            <span>Java OOP</span>
                            <span>JDBC + SQL</span>
                            <span>HTML + CSS</span>
                        </div>
                    </div>
                    <a class="primary-action" href="/interns/new">Add Review</a>
                </section>

                <section class="stats-grid">
                    %s
                    %s
                    %s
                    %s
                    %s
                </section>

                <section class="toolbar">
                    <div>
                        <p class="section-label">Intern Records</p>
                        <h2>Review Dashboard</h2>
                    </div>
                    <form class="search" method="get" action="/">
                        <input name="q" value="%s" placeholder="Search by name, college, domain, or status">
                        <button type="submit">Search</button>
                    </form>
                    <a class="ghost-link" href="/">Clear</a>
                </section>

                <main class="record-grid">
                    %s
                </main>
                """.formatted(
                stat("Total", stats.getTotal(), "All records"),
                stat("Scheduled", stats.getScheduled(), "Pending review"),
                stat("Reviewed", stats.getReviewed(), "Completed"),
                stat("Selected", stats.getSelected(), "Finalized"),
                stat("Avg Score", stats.getAverageScore(), "Overall"),
                HttpUtil.escape(query),
                rows
        ));
    }

    public String newInternForm(String error) {
        String errorHtml = error == null || error.isBlank()
                ? ""
                : "<p class=\"form-error\">" + HttpUtil.escape(error) + "</p>";

        return layout("Add Review", """
                <header class="topbar">
                    <a class="brand" href="/">
                        <span class="brand-mark">IT</span>
                        <span>
                            <strong>InternTrack</strong>
                            <small>Review Manager</small>
                        </span>
                    </a>
                    <nav class="nav-links">
                        <a href="/">Dashboard</a>
                        <a href="/interns/new">New Review</a>
                    </nav>
                </header>

                <section class="page-heading">
                    <div>
                        <p class="eyebrow">New Record</p>
                        <h1>Add Internship Review</h1>
                        <p class="lead compact">Capture intern details, technical domain, project score, and mentor feedback in one structured form.</p>
                    </div>
                    <a class="ghost-link" href="/">Back</a>
                </section>

                <form class="review-form" method="post" action="/interns">
                    %s
                    <label>Full Name<input name="fullName" required placeholder="Enter intern name"></label>
                    <label>Email<input name="email" type="email" required placeholder="name@example.com"></label>
                    <label>College<input name="college" required placeholder="College or institute"></label>
                    <label>Domain<input name="domain" required placeholder="Full Stack Java"></label>
                    <label>Project Title<input name="projectTitle" required placeholder="Project title"></label>
                    <label>Skills<input name="skills" required placeholder="Java, OOP, SQL, HTML, CSS"></label>
                    <label>Review Status
                        <select name="reviewStatus">
                            <option>Scheduled</option>
                            <option>Reviewed</option>
                            <option>Selected</option>
                            <option>Needs Improvement</option>
                        </select>
                    </label>
                    <label>Score<input name="score" type="number" min="0" max="100" value="75" required></label>
                    <label class="wide">Mentor Note<textarea name="mentorNote" rows="4" placeholder="Write short review feedback"></textarea></label>
                    <button class="submit-button" type="submit">Save Review</button>
                </form>
                """.formatted(errorHtml));
    }

    private String internCard(Intern intern) {
        String statusClass = intern.getReviewStatus().toLowerCase().replace(" ", "-");
        String date = intern.getCreatedAt() == null ? "Today" : intern.getCreatedAt().format(DATE_FORMAT);

        return """
                <article class="record-card">
                    <div class="card-topline">
                        <span class="status %s">%s</span>
                        <span class="score">%d/100</span>
                    </div>
                    <h2>%s</h2>
                    <p class="project">%s</p>
                    <dl>
                        <div><dt>Email</dt><dd>%s</dd></div>
                        <div><dt>College</dt><dd>%s</dd></div>
                        <div><dt>Domain</dt><dd>%s</dd></div>
                        <div><dt>Skills</dt><dd>%s</dd></div>
                        <div><dt>Added</dt><dd>%s</dd></div>
                    </dl>
                    <p class="note">%s</p>
                    <div class="card-actions">
                        <form method="post" action="/interns/%d/status">
                            <select name="reviewStatus">
                                %s
                            </select>
                            <button type="submit">Update</button>
                        </form>
                        <form method="post" action="/interns/%d/delete">
                            <button class="danger" type="submit">Delete</button>
                        </form>
                    </div>
                </article>
                """.formatted(
                statusClass,
                HttpUtil.escape(intern.getReviewStatus()),
                intern.getScore(),
                HttpUtil.escape(intern.getFullName()),
                HttpUtil.escape(intern.getProjectTitle()),
                HttpUtil.escape(intern.getEmail()),
                HttpUtil.escape(intern.getCollege()),
                HttpUtil.escape(intern.getDomain()),
                HttpUtil.escape(intern.getSkills()),
                HttpUtil.escape(date),
                HttpUtil.escape(intern.getMentorNote()),
                intern.getId(),
                statusOptions(intern.getReviewStatus()),
                intern.getId()
        );
    }

    private String statusOptions(String selected) {
        String[] statuses = {"Scheduled", "Reviewed", "Selected", "Needs Improvement"};
        StringBuilder options = new StringBuilder();
        for (String status : statuses) {
            String selectedAttr = status.equals(selected) ? " selected" : "";
            options.append("<option").append(selectedAttr).append(">")
                    .append(HttpUtil.escape(status))
                    .append("</option>");
        }
        return options.toString();
    }

    private String stat(String label, int value, String caption) {
        return """
                <article class="stat">
                    <span>%s</span>
                    <strong>%d</strong>
                    <small>%s</small>
                </article>
                """.formatted(HttpUtil.escape(label), value, HttpUtil.escape(caption));
    }

    private String layout(String title, String content) {
        return """
                <!doctype html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>%s - InternTrack</title>
                    <link rel="stylesheet" href="/static/style.css">
                </head>
                <body>
                    <div class="shell">
                        %s
                    </div>
                </body>
                </html>
                """.formatted(HttpUtil.escape(title), content);
    }
}
