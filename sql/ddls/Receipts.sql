CREATE TABLE public.Receipts
(
  Id SERIAL PRIMARY KEY NOT NULL,
  Confirmation_Code TEXT NOT NULL,
  Order_Time TIMESTAMP  WITH time zone NOT NULL,
  Deleted BOOL DEFAULT FALSE ::BOOL NOT NULL,
  User_Id INT NOT NULL,
  CONSTRAINT Receipts_users_id_fk FOREIGN KEY (User_Id) REFERENCES users (id)
);
CREATE UNIQUE INDEX "Receipts_Id_uindex" ON public.Receipts (Id);
CREATE UNIQUE INDEX "Receipts_Confirmation_Code_uindex" ON public.Receipts (Confirmation_Code);