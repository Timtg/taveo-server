CREATE TABLE public.Stores
(
  Id SERIAL PRIMARY KEY NOT NULL,
  Franchise_Id INT NOT NULL,
  Name TEXT NOT NULL,
  Created TIMESTAMP  WITH time zone NOT NULL,
  Valid_From TIMESTAMP  WITH time zone NOT NULL,
  Valid_To TIMESTAMP  WITH time zone NOT NULL,
  Org_Number INT NOT NULL,
  Longitude DECIMAL NOT NULL,
  Latitude DECIMAL NOT NULL,
  Icon_Src TEXT NOT NULL,
  phone TEXT,
  address TEXT,
  email TEXT,
  CONSTRAINT Stores_franchise_id_fk FOREIGN KEY (Franchise_Id) REFERENCES franchise (id)
);
CREATE UNIQUE INDEX "Stores_Id_uindex" ON public.Stores (Id);
CREATE UNIQUE INDEX "Stores_Name_uindex" ON public.Stores (Name);
CREATE UNIQUE INDEX "Stores_Org_Number_uindex" ON public.Stores (Org_Number);