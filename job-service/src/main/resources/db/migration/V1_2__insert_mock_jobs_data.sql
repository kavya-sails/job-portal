-- V1_2__insert_mock_jobs_data.sql
INSERT INTO jobs(title, description, location, experience_required, posted_date)
VALUES('Backend Java Developer', 'Work on microservices, Spring Boot, REST APIs.', 'Hyderabad, India', 2, CURRENT_TIMESTAMP),
      ('Frontend React Developer', 'Develop single-page applications with React and TypeScript.', 'Bengaluru, India', 3, CURRENT_TIMESTAMP),
      ('Data Scientist', 'Build ML models, data pipelines, and perform analysis.', 'Remote', 1, CURRENT_TIMESTAMP),
      ('DevOps Engineer', 'CI/CD, Kubernetes, Terraform experience required.', 'Gurgaon, India', 4, CURRENT_TIMESTAMP),
      ('Product Manager', 'Define product roadmap, interact with stakeholders.', 'Chennai, India', 5, CURRENT_TIMESTAMP);
