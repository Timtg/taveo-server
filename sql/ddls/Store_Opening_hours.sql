CREATE TABLE Store_opening_hours
(
  Store_id INT PRIMARY KEY NOT NULL,
  monday_start TIME NOT NULL,
  monday_end TIME NOT NULL,
  tuesday_start TIME NOT NULL,
  tuesday_end TIME NOT NULL,
  wednesday_start TIME NOT NULL,
  wednesday_end TIME NOT NULL,
  thursday_start TIME NOT NULL,
  thursday_end TIME NOT NULL,
  friday_start TIME NOT NULL,
  friday_end TIME NOT NULL,
  saturday_start TIME,
  saturday_end TIME,
  sunday_start TIME NOT NULL,
  sunday_end TIME NOT NULL,
  CONSTRAINT Store_opening_hours_stores_id_fk FOREIGN KEY (Store_id) REFERENCES stores (id)
);
CREATE UNIQUE INDEX "Store_opening_hours_Store_id_uindex" ON Store_opening_hours (Store_id);