INSERT INTO shootings SELECT * FROM CSVREAD('classpath:shootings.csv');

INSERT INTO shootings_anon
SELECT
    armed,
    age,
    gender,
    race,
    city
FROM
    shootings;