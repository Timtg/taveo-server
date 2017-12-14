CREATE TABLE public.Product_Price
(
  Id SERIAL PRIMARY KEY NOT NULL,
  Price DECIMAL NOT NULL,
  Valid_From TIMESTAMP  WITH time zone NOT NULL ,
  Valid_To TIMESTAMP  WITH time zone,
  Product_Id INT NOT NULL,
  CONSTRAINT Product_Price_Product__Id_fk FOREIGN KEY (Product_Id) REFERENCES Product(Id)
);
CREATE UNIQUE INDEX "Product_Price_Id_uindex" ON public.Product_Price (Id);
