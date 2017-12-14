CREATE TABLE public.Store_Contact_Person
(
  Id SERIAL PRIMARY KEY NOT NULL,
  Store_Id INT NOT NULL,
  Name TEXT NOT NULL,
  Mail TEXT NOT NULL,
  Mobile TEXT NOT NULL,
  CONSTRAINT Store_Contact_Person_stores_id_fk FOREIGN KEY (Store_Id) REFERENCES stores (id)
);
CREATE UNIQUE INDEX "Store_Contact_Person_Id_uindex" ON public.Store_Contact_Person (Id);
CREATE UNIQUE INDEX "Store_Contact_Person_Name_uindex" ON public.Store_Contact_Person (Name);
CREATE UNIQUE INDEX "Store_Contact_Person_Store_Id_uindex" ON public.Store_Contact_Person (Store_Id);