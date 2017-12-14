CREATE TABLE public.Franchise
(
  Id SERIAL PRIMARY KEY NOT NULL,
  Name TEXT NOT NULL,
  Created TIMESTAMP NOT NULL
);
CREATE UNIQUE INDEX "Franchise_Id_uindex" ON public.Franchise (Id);
CREATE UNIQUE INDEX "Franchise_Name_uindex" ON public.Franchise (Name);