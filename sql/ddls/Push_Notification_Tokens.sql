CREATE TABLE push_notification_tokens
(
  User_Id INT NOT NULL,
  Push_Token VARCHAR NOT NULL,
  System VARCHAR(8) NOT NULL ,
  created TIMESTAMP NOT NULL ,
  CONSTRAINT push_notification_tokens_User_Id_Push_Token_pk PRIMARY KEY (User_Id, Push_Token),
  CONSTRAINT push_notification_tokens_users_id_fk FOREIGN KEY (User_Id) REFERENCES users (id),
  CONSTRAINT push_notification_tokens_system_check CHECK (System in ('android','ios'))
);
CREATE UNIQUE INDEX "push_notification_tokens_Push_Token_uindex" ON push_notification_tokens (Push_Token);
CREATE UNIQUE INDEX "push_notification_tokens_Created_user_Id_uindex" ON push_notification_tokens (User_Id,created);