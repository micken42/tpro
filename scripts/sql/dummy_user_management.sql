
 -- DUMMY USER DATA

INSERT INTO Context (id, name) VALUES (1, "tpro");

INSERT INTO Role(id, name, context_id) VALUES (1, 'admin', 1);

INSERT INTO Role(id, name, context_id) VALUES (2, 'user', 1);

INSERT INTO User (id, prename, surname, username, email, password) VALUES (1, "Max", "Mustermann", "admin", "mustermax@tpro.de", "admin");

INSERT INTO User (id, prename, surname, username, email, password) VALUES (2, "Maxime", "Musterfrau", "user", "mustermaxime@tpro.de", "user");

INSERT INTO User (id, prename, surname, username, email, password) VALUES (3, "Mick", "Micken", "micken", "micken@tpro.de", "micken");

INSERT INTO User_Role (user_id, role_id) VALUES (1, 1);

INSERT INTO User_Role (user_id, role_id) VALUES (1, 2);

INSERT INTO User_Role (user_id, role_id) VALUES (2, 2);

INSERT INTO `Group` (id, name) VALUES (1, "admins");

INSERT INTO `Group` (id, name) VALUES (2, "users");

INSERT INTO Group_Role (group_id, role_id) VALUES (1, 1);

INSERT INTO Group_Role (group_id, role_id) VALUES (2, 2);

INSERT INTO Group_User (group_id, user_id) VALUES (1, 1);

INSERT INTO Group_User (group_id, user_id) VALUES (2, 2);

  -- DUMMY PLUGIN DATA

INSERT INTO User_Role (user_id, role_id) VALUES (2, 3);

INSERT INTO User_Role (user_id, role_id) VALUES (2, 4);
