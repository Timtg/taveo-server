CREATE TABLE email_verification
(
  user_id INTEGER PRIMARY KEY NOT NULL,
  verification_code_hash TEXT NOT NULL,
  CONSTRAINT email_verification_users_id_fk FOREIGN KEY (user_id) REFERENCES users (id)
);
CREATE UNIQUE INDEX "Email_Verification_User_Id_uindex" ON email_verification (user_id);