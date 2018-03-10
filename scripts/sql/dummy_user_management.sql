
 -- DUMMY USER DATA

INSERT INTO Context (id, name) VALUES (1, "tpro");

INSERT INTO Role(id, name, context_id) VALUES (1, 'admin', 1);

INSERT INTO User (id, prename, surname, username, email, password) VALUES (1, "Max", "Mustermann", "admin", "mustermann@htw-berlin.de", "1234");

INSERT INTO User (id, prename, surname, username, email, password) VALUES (2, "Stephan", "Salinger", "salinger", "salinger@htw-berlin.de", "1234");

INSERT INTO User (id, prename, surname, username, email, password) VALUES (3, "Christin", "Schmidt", "schmidt", "schmidt@htw-berlin.de", "1234");

INSERT INTO User (id, prename, surname, username, email, password) VALUES (4, "Michael", "Baumert", "s012345", "s012345@htw-berlin.de", "1234");

INSERT INTO User (id, prename, surname, username, email, password) VALUES (5, "Peter", "Heinrich", "s012346", "s012346@htw-berlin.de", "1234");

INSERT INTO User (id, prename, surname, username, email, password) VALUES (6, "Pauline", "Schwert", "s012347", "s012347@htw-berlin.de", "1234");

INSERT INTO User_Role (user_id, role_id) VALUES (1, 1);

INSERT INTO `Group` (id, name) VALUES (1, "Professoren");

INSERT INTO `Group` (id, name) VALUES (2, "Studenten");

INSERT INTO Group_User (group_id, user_id) VALUES (1, 2);

INSERT INTO Group_User (group_id, user_id) VALUES (1, 3);

INSERT INTO Group_User (group_id, user_id) VALUES (2, 4);

INSERT INTO Group_User (group_id, user_id) VALUES (2, 5);
