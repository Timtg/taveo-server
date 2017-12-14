CREATE TABLE public.Product
(
  Id SERIAL PRIMARY KEY NOT NULL,
  Store_Id INT NOT NULL,
  Name TEXT NOT NULL,
  Description TEXT NOT NULL,
  "Group" TEXT NOT NULL,
  Valid_From TIMESTAMP WITH time zone NOT NULL,
  Valid_To TIMESTAMP  WITH time zone NOT NULL,
  Icon_src VARCHAR(60) NOT NULL,
  deleted boolean not null default false,
  CONSTRAINT Product_stores_id_fk FOREIGN KEY (Store_Id) REFERENCES stores (id)
);