-- Standardize cohort names: JC* -> C*, PC* -> C*, DC* -> C*
UPDATE employees SET cohort = 'C2' WHERE cohort = 'JC2';
UPDATE employees SET cohort = 'C3' WHERE cohort = 'JC3';
UPDATE employees SET cohort = 'C4' WHERE cohort = 'JC4';
UPDATE employees SET cohort = 'C1' WHERE cohort = 'PC1';
UPDATE employees SET cohort = 'C1' WHERE cohort = 'DC1';
