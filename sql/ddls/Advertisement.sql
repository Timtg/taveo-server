CREATE TABLE public.Advertisement
(
  Id SERIAL PRIMARY KEY NOT NULL,
  Product_Id INT NOT NULL,
  Valid_From TIMESTAMP NOT NULL,
  Valid_To TIMESTAMP NOT NULL,

  CONSTRAINT Advertisement_product_id_fk FOREIGN KEY (Product_Id) REFERENCES product (id)
);
CREATE UNIQUE INDEX "Advertisement_Id_uindex" ON public.Advertisement (Id);