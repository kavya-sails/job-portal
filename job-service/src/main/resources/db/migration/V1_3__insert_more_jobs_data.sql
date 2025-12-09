INSERT INTO jobs (title, description, location, experience_required, posted_date)
VALUES
-- SDE levels
('SDE1', 'Entry-level software engineer.', 'Hyderabad', 0, CURRENT_TIMESTAMP),
('SDE2', 'Mid-level software engineer.', 'Bengaluru', 2, CURRENT_TIMESTAMP),
('SDE3', 'Senior software engineer.', 'Pune', 4, CURRENT_TIMESTAMP),

-- DevOps roles
('DevOps L1', 'Basic CI/CD & Linux skills.', 'Hyderabad', 1, CURRENT_TIMESTAMP),
('DevOps L2', 'CI/CD, Docker, Kubernetes.', 'Bengaluru', 3, CURRENT_TIMESTAMP),
('DevOps L3', 'K8s, Terraform, AWS expert.', 'Chennai', 5, CURRENT_TIMESTAMP),

-- QA roles
('QA Manual', 'Manual test cases & functional testing.', 'Noida', 1, CURRENT_TIMESTAMP),
('QA Automation', 'Selenium, TestNG, Cypress.', 'Hyderabad', 2, CURRENT_TIMESTAMP),
('QA Lead', 'Lead QA team, strategy & planning.', 'Remote', 5, CURRENT_TIMESTAMP),

-- Data roles
('Data Analyst', 'SQL, dashboards, reporting.', 'Gurgaon', 1, CURRENT_TIMESTAMP),
('Data Engineer L1', 'ETL pipelines, Python.', 'Pune', 2, CURRENT_TIMESTAMP),
('Data Engineer L2', 'Big Data, Spark, Airflow.', 'Hyderabad', 4, CURRENT_TIMESTAMP),
('Data Scientist L2', 'ML models & experimentation.', 'Bengaluru', 3, CURRENT_TIMESTAMP),

-- Backend roles
('Java Dev L1', 'Spring Boot basics.', 'Hyderabad', 1, CURRENT_TIMESTAMP),
('Java Dev L2', 'Microservices & REST APIs.', 'Chennai', 3, CURRENT_TIMESTAMP),
('NodeJS Dev', 'APIs with Express.js.', 'Remote', 2, CURRENT_TIMESTAMP),
('Python Backend', 'FastAPI, Django APIs.', 'Kochi', 2, CURRENT_TIMESTAMP),

-- Frontend roles
('React Dev', 'React, Redux, TypeScript.', 'Hyderabad', 2, CURRENT_TIMESTAMP),
('Angular Dev', 'Angular + RxJS.', 'Bengaluru', 3, CURRENT_TIMESTAMP),
('UI Engineer', 'HTML, CSS, JS.', 'Pune', 1, CURRENT_TIMESTAMP),

-- Cloud / Infra roles
('AWS Engineer', 'AWS EC2, S3, Lambda.', 'Chennai', 3, CURRENT_TIMESTAMP),
('Azure Engineer', 'Azure DevOps, Functions.', 'Hyderabad', 3, CURRENT_TIMESTAMP),
('GCP Engineer', 'GCP services & IAM.', 'Gurgaon', 3, CURRENT_TIMESTAMP),

-- Security roles
('Security Analyst', 'SOC monitoring, SIEM.', 'Bengaluru', 1, CURRENT_TIMESTAMP),
('Pen Tester', 'VAPT, Kali Linux.', 'Hyderabad', 2, CURRENT_TIMESTAMP),

-- Mobile roles
('Android Dev', 'Kotlin, Jetpack.', 'Pune', 1, CURRENT_TIMESTAMP),
('iOS Dev', 'Swift, Xcode.', 'Chennai', 2, CURRENT_TIMESTAMP),

-- Database roles
('DBA Junior', 'MySQL/Postgres basics.', 'Hyderabad', 1, CURRENT_TIMESTAMP),
('DBA Senior', 'DB tuning, replication.', 'Bengaluru', 5, CURRENT_TIMESTAMP),

-- Management roles
('Tech Lead', 'Lead development team.', 'Remote', 6, CURRENT_TIMESTAMP),
('Project Manager', 'Manage SCRUM & delivery.', 'Hyderabad', 7, CURRENT_TIMESTAMP);
