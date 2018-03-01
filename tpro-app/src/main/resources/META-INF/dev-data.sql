
 -- DUMMY USER DATA

INSERT INTO Context (id, name) VALUES (1, "tpro");

INSERT INTO Permission(id, name, context_id) VALUES (1, 'admin', 1);

INSERT INTO Permission(id, name, context_id) VALUES (2, 'user', 1);

INSERT INTO User (id, prename, surname, username, email, password) VALUES (1, "Max", "Mustermann", "admin", "mustermax@tpro.de", "admin");

INSERT INTO User (id, prename, surname, username, email, password) VALUES (2, "Maxime", "Musterfrau", "user", "mustermaxime@tpro.de", "user");

INSERT INTO User_Permission (user_id, permission_id) VALUES (1, 1);

INSERT INTO User_Permission (user_id, permission_id) VALUES (1, 2);

INSERT INTO User_Permission (user_id, permission_id) VALUES (2, 2);

 -- DUMMY DATA FOR BOOKSTORE PLUGIN

INSERT INTO Book (id, title, author) VALUES (1, "Der Flug des Falken I", "Walter Baumert");

INSERT INTO Book (id, title, author) VALUES (2, "Der Flug des Falken II", "Walter Baumert");

INSERT INTO Book (id, title, author) VALUES (3, "Der Flug des Falken III", "Walter Baumert");

INSERT INTO Book (id, title, author) VALUES (4, "Der Flug des Falken IV", "Walter Baumert");

INSERT INTO Book (id, title, author) VALUES (5, "Der Flug des Falken V", "Walter Baumert");
