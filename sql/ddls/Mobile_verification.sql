CREATE TABLE Mobile_verification
(
  user_id INTEGER PRIMARY KEY NOT NULL,
  verification_code_hash TEXT NOT NULL,
  CONSTRAINT Mobile_verification_users_id_fk FOREIGN KEY (user_id) REFERENCES users (id)
);
CREATE UNIQUE INDEX "Mobile_verification_User_Id_uindex" ON Mobile_verification (user_id);