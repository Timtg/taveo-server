CREATE TABLE Store_Settings
(
  Store_Id INT PRIMARY KEY NOT NULL,
  Manual_Time_verification BOOLEAN NOT NULL,
  Automatic_Print_Dialog BOOLEAN NOT NULL,
  CONSTRAINT Store_Settings_stores_id_fk FOREIGN KEY (Store_Id) REFERENCES stores (id)
);
CREATE UNIQUE INDEX "Store_Settings_Store_Id_uindex" ON Store_Settings (Store_Id);