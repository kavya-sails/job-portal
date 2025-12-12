-- V1_2__insert_mock_jobs_data.sql
INSERT INTO jobs (
    title, description, location, experience_required,
    company_name, package_offered, skills, education, posted_date
)
VALUES
-- Backend & Java Jobs
('Backend Java Developer', 'Work on microservices, Spring Boot, REST APIs.', 'Hyderabad, India', 2,
'Google', '20 LPA', 'Java, Spring Boot, Microservices, SQL', 'B.Tech / M.Tech', NOW()),

('Java Dev L1', 'Spring Boot basics.', 'Hyderabad', 1,
'TCS', '6 LPA', 'Core Java, Spring Boot', 'B.Tech', NOW()),

('Java Dev L2', 'Microservices & REST APIs.', 'Chennai', 3,
'Infosys', '10 LPA', 'Spring Boot, Microservices, Docker', 'B.Tech', NOW()),

('Java Dev L3', 'High-level distributed systems development.', 'Bengaluru', 5,
'Microsoft', '30 LPA', 'Java, System Design, Microservices', 'B.Tech / M.Tech', NOW()),


-- Frontend Jobs
('Frontend React Developer', 'Develop SPAs using React & TypeScript.', 'Bengaluru, India', 3,
'Meta', '18 LPA', 'React, Redux, TypeScript', 'Any Bachelor Degree', NOW()),

('UI Engineer', 'HTML, CSS, JS UI development.', 'Pune', 1,
'Accenture', '5 LPA', 'HTML, CSS, JavaScript', 'Any Degree', NOW()),

('Angular Dev', 'Angular + RxJS applications.', 'Bengaluru', 3,
'Wipro', '9 LPA', 'Angular, RxJS, TypeScript', 'B.Tech', NOW()),


-- DevOps Jobs
('DevOps Engineer', 'CI/CD, Kubernetes, Terraform experience required.', 'Gurgaon, India', 4,
'Amazon', '24 LPA', 'Docker, Kubernetes, Terraform, AWS', 'B.Tech', NOW()),

('DevOps L1', 'Basic CI/CD & Linux skills.', 'Hyderabad', 1,
'Tech Mahindra', '4.5 LPA', 'Linux, Git, Jenkins', 'Any Degree', NOW()),

('DevOps L2', 'CI/CD, Docker, Kubernetes.', 'Bengaluru', 3,
'Infosys', '12 LPA', 'Docker, Kubernetes, Helm', 'B.Tech', NOW()),

('DevOps L3', 'K8s, Terraform, AWS expert.', 'Chennai', 5,
'Amazon', '28 LPA', 'Terraform, AWS, Kubernetes', 'Any Engineering', NOW()),


-- Data Roles
('Data Scientist', 'Build ML models, data pipelines.', 'Remote', 1,
'Microsoft', '22 LPA', 'Python, ML, Statistics', 'B.Tech / M.Sc', NOW()),

('Data Analyst', 'SQL, dashboards, reporting.', 'Gurgaon', 1,
'Deloitte', '7 LPA', 'SQL, Power BI, Excel', 'Any Degree', NOW()),

('Data Engineer L1', 'ETL pipelines, Python.', 'Pune', 2,
'Capgemini', '8 LPA', 'Python, ETL, SQL', 'B.Tech', NOW()),

('Data Engineer L2', 'Big Data, Spark, Airflow.', 'Hyderabad', 4,
'Google', '30 LPA', 'Spark, Hadoop, Airflow, Python', 'B.Tech / M.Tech', NOW()),


-- Cloud / Infra Jobs
('AWS Engineer', 'AWS EC2, S3, Lambda.', 'Chennai', 3,
'Amazon', '18 LPA', 'AWS, Lambda, CloudFormation', 'B.Tech', NOW()),

('Azure Engineer', 'Azure DevOps, Functions.', 'Hyderabad', 3,
'Microsoft', '20 LPA', 'Azure, DevOps, Pipelines', 'B.Tech', NOW()),

('GCP Engineer', 'GCP services & IAM.', 'Gurgaon', 3,
'Google', '22 LPA', 'GCP, IAM, BigQuery', 'B.Tech', NOW()),


-- Security Jobs
('Security Analyst', 'SOC monitoring, SIEM.', 'Bengaluru', 1,
'IBM', '6 LPA', 'SOC, SIEM, Networking', 'Any Degree', NOW()),

('Pen Tester', 'VAPT, Kali Linux.', 'Hyderabad', 2,
'EY', '12 LPA', 'Pentesting, BurpSuite, Kali', 'B.Tech', NOW()),


-- QA / Testing Jobs
('QA Manual', 'Functional and regression testing.', 'Noida', 1,
'Cognizant', '4.5 LPA', 'Manual Testing, JIRA', 'Any Degree', NOW()),

('QA Automation', 'Automation testing using Selenium.', 'Hyderabad', 2,
'Wipro', '7 LPA', 'Selenium, Java, TestNG', 'B.Tech', NOW()),

('QA Lead', 'Lead QA team, test strategy.', 'Remote', 5,
'Tech Mahindra', '15 LPA', 'Automation, Leadership', 'Any Graduation', NOW()),


-- Mobile Development Jobs
('Android Dev', 'Build Android apps using Kotlin.', 'Pune', 1,
'Samsung', '8 LPA', 'Kotlin, Android SDK', 'B.Tech', NOW()),

('iOS Dev', 'Build iOS apps.', 'Chennai', 2,
'Apple', '14 LPA', 'Swift, Xcode', 'B.Tech', NOW()),


-- Management Jobs
('Product Manager', 'Define product roadmap & strategy.', 'Chennai', 5,
'Flipkart', '30 LPA', 'Leadership, Communication', 'MBA', NOW()),

('Project Manager', 'Manage SCRUM & delivery.', 'Hyderabad', 7,
'Accenture', '25 LPA', 'Agile, SCRUM, Leadership', 'MBA / B.Tech', NOW()),

('Tech Lead', 'Lead development team.', 'Remote', 6,
'Google', '35 LPA', 'Architecture, Leadership, Java', 'B.Tech / M.Tech', NOW());