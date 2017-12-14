
CREATE TABLE public.store_receipts
(
  receipt_id INT NOT NULL,
  store_id INT NOT NULL,
  delivered_time TIMESTAMP,
  require_time_notification BOOLEAN not NULL ,
  ready_at TIMESTAMP NULL,

  CONSTRAINT store_receipts_receipts_id_fk FOREIGN KEY (receipt_id) REFERENCES receipts (id)
);