CREATE TABLE student (
                         id INT AUTO_INCREMENT PRIMARY KEY ,
                         name VARCHAR(100) NOT NULL ,
                         address VARCHAR(400) NOT NULL
);

CREATE TABLE picture (
                         student_id INT PRIMARY KEY ,
                         path VARCHAR(1000) NOT NULL ,
                         CONSTRAINT fk_picture FOREIGN KEY (student_id) REFERENCES student (id)
                             ON DELETE CASCADE
);