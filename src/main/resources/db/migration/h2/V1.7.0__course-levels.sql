CREATE TABLE course_levels
(
    id          INT AUTO_INCREMENT NOT NULL PRIMARY KEY,
    name        varchar(50)        NOT NULL UNIQUE,
    description varchar(255)
);


ALTER TABLE course
    ADD level_id INT;
ALTER TABLE course
    ADD CONSTRAINT FK_course_level_id FOREIGN KEY (level_id) REFERENCES course_levels (id);

INSERT INTO course_levels (name)
VALUES ('Awareness'),
       ('Working'),
       ('Practitioner'),
       ('Expert');

