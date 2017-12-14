--Franchises
Insert into franchise (name, created) VALUES ('Rema 1000',current_timestamp);
Insert into franchise (name, created) VALUES ('Kitchn',current_timestamp);
Insert into franchise (name, created) VALUES ('Ringo',current_timestamp);
Insert into franchise (name, created) VALUES ('Teknik Magasinet',current_timestamp);



--stores
INSERT into stores (franchise_id, name, created, valid_from, valid_to, org_number, longitude, latitude,icon_src,phone,address,email)
VALUES (1,'Rema 1000 Jessheim',current_timestamp,current_timestamp,'2100-09-28 01:00:00','883409442',1337,1337,'rema/rema_1000.png','12345678','Pepperkakegata 1, 1337 J-Town','kontakt@rema.no');

--these three have invalid (mock) orgnumbers
INSERT into stores (franchise_id, name, created, valid_from, valid_to, org_number, longitude, latitude,icon_src)
VALUES (2,'Kitchn Jessheim',current_timestamp,current_timestamp,'2100-09-28 01:00:00','883409443',1337,1337,'kitchn/kitchn.png');

INSERT into stores (franchise_id, name, created, valid_from, valid_to, org_number, longitude, latitude,icon_src)
VALUES (3,'Ringo Jessheim',current_timestamp,current_timestamp,'2100-09-28 01:00:00','883409444',1337,1337,'ringo/ringo.png');

INSERT into stores (franchise_id, name, created, valid_from, valid_to, org_number, longitude, latitude,icon_src)
VALUES (4,'Teknik Magasinet Jessheim',current_timestamp,current_timestamp,'2100-09-28 01:00:00','883409445',1337,1337,'teknik_magasinet/teknikmagasinet.png');


-- products with prices
Insert Into product (store_id, name, description, "Group", valid_from, valid_to, Icon_src)
VALUES (1,'Kjøttdeig','Storfe kjøttdeig av høy kvalitet','Kjøtt',current_timestamp,'2100-09-28 01:00:00','rema/rema1000_kjott.png');
Insert into product_price (price, valid_from, valid_to,product_id) VALUES (15,current_timestamp,'2100-09-28 01:00:00',1);
--this one should not be picked as it is "old"
Insert into product_price (price, valid_from, valid_to,product_id) VALUES (-99,'1990-09-28 01:00:00','1991-09-28 01:00:00',1);

Insert Into product (store_id, name, description, "Group", valid_from, valid_to, Icon_src)
VALUES (1,'Storfe Strimler','Renskåret strimler av Storfe ','Kjøtt',current_timestamp,'2100-09-28 01:00:00','rema/storferema1000.png');
Insert into product_price (price, valid_from, valid_to,product_id) VALUES (49,current_timestamp,'2100-09-28 01:00:00',2);

Insert Into product (store_id, name, description, "Group", valid_from, valid_to, Icon_src)
VALUES (4,'Roller Guard','Elektrisk Segway uten ratt','Elektriske hjelpemidler',current_timestamp,'2100-09-28 01:00:00','teknik_magasinet/teknik_mag_roller.png');
Insert into product_price (price, valid_from, valid_to,product_id) VALUES (249,current_timestamp,'2100-09-28 01:00:00',3);

Insert Into product (store_id, name, description, "Group", valid_from, valid_to, Icon_src)
VALUES (4,'Trådløst høytaler','Kraftige og lett bluetooth høyttaler', 'Lyd',current_timestamp,'2100-09-28 01:00:00','teknik_magasinet/teknik_mag_speaker.png');
Insert into product_price (price, valid_from, valid_to,product_id) VALUES (499,current_timestamp,'2100-09-28 01:00:00',4);

Insert Into product (store_id, name, description, "Group", valid_from, valid_to, Icon_src)
VALUES (3,'VIP-service på flyplass','Lego byggesett - flyplass','Lego',current_timestamp,'2100-09-28 01:00:00','ringo/ringo_vip_flyplass.jpg');
Insert into product_price (price, valid_from, valid_to,product_id) VALUES (549,current_timestamp,'2100-09-28 01:00:00',5);

Insert Into product (store_id, name, description, "Group", valid_from, valid_to, Icon_src)
VALUES (3,'Klokkepingvin','Klokke for barn utformet som en pingvin','Babyleker',current_timestamp,'2100-09-28 01:00:00','ringo/ringo_klokkepingvin.jpg');
Insert into product_price (price, valid_from, valid_to,product_id) VALUES (549,current_timestamp,'2100-09-28 01:00:00',6);

Insert Into product (store_id, name, description, "Group", valid_from, valid_to, Icon_src)
VALUES (2,'EVA SOLO, CHAMPAGNEGLASS 20CL','Stilfulle champagneglass i krystall','Glass og Krus',current_timestamp,'2100-09-28 01:00:00','kitchn/kitchn_champagne.jpg');
Insert into product_price (price, valid_from, valid_to,product_id) VALUES (249,current_timestamp,'2100-09-28 01:00:00',7);

Insert Into product (store_id, name, description, "Group", valid_from, valid_to, Icon_src)
VALUES (2,'FISKARS, OSTEHØVEL COCONUT','Skarp og stilfull osthøvel','Kjøkkenutstyr',current_timestamp,'2100-09-28 01:00:00','kitchn/kitchn_ostehøvel.jpg');
Insert into product_price (price, valid_from, valid_to,product_id) VALUES (89,current_timestamp,'2100-09-28 01:00:00',8);

--- ads

Insert into advertisement (product_id, valid_from, valid_to) VALUES (1,'1990-09-28 01:00:00','2991-09-28 01:00:00');
Insert into advertisement (product_id, valid_from, valid_to) VALUES (3,'1990-09-28 01:00:00','2991-09-28 01:00:00');
Insert into advertisement (product_id, valid_from, valid_to) VALUES (2,'1990-09-28 01:00:00','2991-09-28 01:00:00');
Insert into advertisement (product_id, valid_from, valid_to) VALUES (4,'1990-09-28 01:00:00','2991-09-28 01:00:00');

--- 1337 user
INSERT INTO users (email, password_hash, name, mobile,type)
    VALUES ('kris@nc3a.no','$2a$16$RxvaqUaOoEp6/6yucp.zKeo8is1txuvAOiNJ7WGXfQt3MhQu3VcSu','Kris Gundersen','45266683','A');
INSERT INTO users (email, password_hash, name, mobile,type)
    VALUES ('tim@nc3a.no','$2a$16$tU1AisJ8F0PnGYiiMk/CyO1QsRKd8O70FWI.8N6McL.SF8NfpPoQC','Tim Gundersen','45850012','A');


---store_settings

insert into store_settings (store_id, manual_time_verification, automatic_print_dialog) VALUES (1,true,true);
insert into store_settings (store_id, manual_time_verification, automatic_print_dialog) VALUES (2,FALSE ,FALSE);
insert into store_settings (store_id, manual_time_verification, automatic_print_dialog) VALUES (3,FALSE,FALSE);
insert into store_settings (store_id, manual_time_verification, automatic_print_dialog) VALUES (4,FALSE,FALSE);


--- opening hours:
Insert into store_opening_hours (store_id, monday_start, monday_end, tuesday_start, tuesday_end, wednesday_start, wednesday_end, thursday_start, thursday_end, friday_start, friday_end, saturday_start, saturday_end, sunday_start, sunday_end)
    VALUES (1,'07:00:00','22:00:00','07:00:00','22:00:00','07:00:00','22:00:00','07:00:00','22:00:00','07:00:00','22:00:00','09:00:00','21:00:00','00:00:00','00:00:00')