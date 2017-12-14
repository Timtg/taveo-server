CREATE TABLE public.Receipt_Products
(
  Receipt_Id INT NOT NULL,
  Product_Count INT NOT NULL,
  Product_Id INT NOT NULL,
  Price DECIMAL NOT NULL,
  CONSTRAINT Receipt_Products_Receipt_Id_Product_Id_pk PRIMARY KEY (Receipt_Id, Product_Id),
  CONSTRAINT Receipt_Products_receipts_id_fk FOREIGN KEY (Receipt_Id) REFERENCES receipts (id),
  CONSTRAINT Receipt_Products_product_id_fk FOREIGN KEY (Product_Id) REFERENCES product (id)
);