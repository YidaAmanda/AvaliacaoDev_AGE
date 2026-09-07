CREATE TABLE funcionario (
	rowid BIGINT auto_increment PRIMARY KEY, 
	nm_funcionario VARCHAR(255) NOT NULL
);
	
INSERT INTO funcionario (nm_funcionario) VALUES 
('João'), 
('Maria'), 
('José'), 
('Joana');

CREATE TABLE agenda (
	rowid BIGINT auto_increment PRIMARY KEY, 
	nm_agenda VARCHAR(255) NOT NULL, 
	prd_disponivel INT NOT NULL -- 1 = Manhã, 2 = Tarde, 3 = Ambos
);

INSERT INTO agenda (nm_agenda, prd_disponivel) VALUES 
('Oftalmologia', 3), 
('Pediatria', 1), 
('Psiquiatria', 2), 
('Ginecologia e Obstetrícia', 3);

CREATE TABLE compromisso (
	rowid BIGINT auto_increment PRIMARY KEY,
	rowid_agenda BIGINT NOT NULL,
	rowid_funcionario BIGINT NOT NULL,
	dt_compromisso DATE NOT NULL,
	hr_compromisso TIME NOT NULL,
	
	CONSTRAINT fk_agenda FOREIGN KEY (rowid_agenda) REFERENCES agenda(rowid),
	CONSTRAINT fk_funcionario FOREIGN KEY (rowid_funcionario) REFERENCES funcionario(rowid)
);

INSERT INTO compromisso (rowid_agenda, rowid_funcionario, dt_compromisso, hr_compromisso) VALUES
(1, 1, DATEADD(DAY, 100, CURRENT_DATE), '13:00'), -- mysql -> DATE_ADD(CURRENT_DATE, INTERVAL 378 DAY)
(2, 3, DATEADD(DAY, 30, CURRENT_DATE), '09:00'),
(3, 4, DATEADD(DAY, 180, CURRENT_DATE), '19:00'),
(4, 4, DATEADD(DAY, 200, CURRENT_DATE), '18:00');