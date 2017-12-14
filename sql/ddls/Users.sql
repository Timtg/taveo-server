CREATE TABLE public.Users
(
  id SERIAL PRIMARY KEY NOT NULL,
  Email TEXT NOT NULL,
  Password_Hash TEXT NOT NULL,
  deleted boolean not null default false,
  Name TEXT NOT NULL,
  Mobile TEXT NOT NULL,
  Type char(1) not null DEFAULT 'n',
  Reset_Confirmation_Hash TEXT NULL,
  Reset_Code_Valid_To TIMESTAMP,
  Accepted_disclaimer boolean not null default false,
  email_verified boolean not null default false,
  mobile_verified boolean not null default false,
  store_id int DEFAULT NULL NULL,
  last_login TIMESTAMP null DEFAULT null
  );
CREATE UNIQUE INDEX "Users_id_uindex" ON public.Users (id);
CREATE UNIQUE INDEX "Users_Email_uindex" ON public.Users (Email);
ALTER TABLE users ADD CONSTRAINT check_types CHECK (type IN ('N', 'A', 'M','P') );
ALTER TABLE users ADD CONSTRAINT reset_integrity CHECK ((Reset_Code_Valid_To is not null and Reset_Confirmation_Hash is not null) or (Reset_Code_Valid_To is  null and Reset_Confirmation_Hash is null));

-- ALTER TABLE public.users ADD Store_Id INT DEFAULT NULL  NULL;
