package com.interntrack.repository;

import com.interntrack.model.Intern;

import java.sql.SQLException;
import java.util.List;

public interface InternRepository {
    List<Intern> findAll(String query) throws SQLException;

    void save(Intern intern) throws SQLException;

    void updateStatus(int id, String status) throws SQLException;

    void deleteById(int id) throws SQLException;
}
