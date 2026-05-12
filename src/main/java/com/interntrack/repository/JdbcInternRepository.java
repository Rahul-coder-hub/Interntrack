package com.interntrack.repository;

import com.interntrack.model.Intern;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class JdbcInternRepository implements InternRepository {
    private final Database database;

    public JdbcInternRepository(Database database) {
        this.database = database;
    }

    @Override
    public List<Intern> findAll(String query) throws SQLException {
        String baseSql = """
                SELECT id, full_name, email, college, domain, project_title, skills,
                       review_status, score, mentor_note, created_at
                FROM interns
                """;
        String searchSql = """
                WHERE LOWER(full_name) LIKE ?
                   OR LOWER(college) LIKE ?
                   OR LOWER(domain) LIKE ?
                   OR LOWER(review_status) LIKE ?
                """;
        String orderSql = " ORDER BY created_at DESC, id DESC";
        boolean hasQuery = query != null && !query.isBlank();
        String sql = baseSql + (hasQuery ? searchSql : "") + orderSql;

        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            if (hasQuery) {
                String term = "%" + query.toLowerCase().trim() + "%";
                for (int i = 1; i <= 4; i++) {
                    statement.setString(i, term);
                }
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                List<Intern> interns = new ArrayList<>();
                while (resultSet.next()) {
                    interns.add(mapRow(resultSet));
                }
                return interns;
            }
        }
    }

    @Override
    public void save(Intern intern) throws SQLException {
        String sql = """
                INSERT INTO interns (
                    full_name, email, college, domain, project_title, skills,
                    review_status, score, mentor_note
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, intern.getFullName());
            statement.setString(2, intern.getEmail());
            statement.setString(3, intern.getCollege());
            statement.setString(4, intern.getDomain());
            statement.setString(5, intern.getProjectTitle());
            statement.setString(6, intern.getSkills());
            statement.setString(7, intern.getReviewStatus());
            statement.setInt(8, intern.getScore());
            statement.setString(9, intern.getMentorNote());
            statement.executeUpdate();
        }
    }

    @Override
    public void updateStatus(int id, String status) throws SQLException {
        String sql = "UPDATE interns SET review_status = ? WHERE id = ?";

        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, status);
            statement.setInt(2, id);
            statement.executeUpdate();
        }
    }

    @Override
    public void deleteById(int id) throws SQLException {
        String sql = "DELETE FROM interns WHERE id = ?";

        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        }
    }

    private Intern mapRow(ResultSet resultSet) throws SQLException {
        Timestamp createdAt = resultSet.getTimestamp("created_at");
        return new Intern(
                resultSet.getInt("id"),
                resultSet.getString("full_name"),
                resultSet.getString("email"),
                resultSet.getString("college"),
                resultSet.getString("domain"),
                resultSet.getString("project_title"),
                resultSet.getString("skills"),
                resultSet.getString("review_status"),
                resultSet.getInt("score"),
                resultSet.getString("mentor_note"),
                createdAt == null ? null : createdAt.toLocalDateTime()
        );
    }
}
