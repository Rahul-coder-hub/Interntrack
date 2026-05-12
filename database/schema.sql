CREATE TABLE IF NOT EXISTS interns (
    id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(120) NOT NULL,
    email VARCHAR(160) NOT NULL,
    college VARCHAR(160) NOT NULL,
    domain VARCHAR(80) NOT NULL,
    project_title VARCHAR(180) NOT NULL,
    skills VARCHAR(240) NOT NULL,
    review_status VARCHAR(40) NOT NULL,
    score INT NOT NULL,
    mentor_note VARCHAR(500) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO interns (
    full_name, email, college, domain, project_title, skills,
    review_status, score, mentor_note
)
SELECT 'Rahul Sharma', 'rahul@example.com', 'City Engineering College',
       'Full Stack Java', 'Student Attendance Portal',
       'Java, OOP, SQL, HTML, CSS', 'Scheduled', 78,
       'Good fundamentals. Improve validation and presentation flow.'
WHERE NOT EXISTS (SELECT 1 FROM interns);

INSERT INTO interns (
    full_name, email, college, domain, project_title, skills,
    review_status, score, mentor_note
)
SELECT 'Ananya Verma', 'ananya@example.com', 'National Institute of Technology',
       'Web Development', 'Internship Task Tracker',
       'Java, JDBC, SQL, UI Design', 'Reviewed', 88,
       'Clean interface and strong explanation of database usage.'
WHERE (SELECT COUNT(*) FROM interns) = 1;
