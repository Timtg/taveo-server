Insert into franchise (name, created) VALUES ('Dons',current_timestamp);

INSERT into stores (franchise_id, name, created, valid_from, valid_to, org_number, longitude, latitude,icon_src,phone,address,email)
VALUES (1,'Don',current_timestamp,current_timestamp,'2100-09-28 01:00:00','815777182',60.141451,11.191260,'don/don.jpg','92070165','Myrvegen 88, 2050 Jessheim',null);


Insert Into product (store_id, name, description, "Group", valid_from, valid_to, Icon_src)
VALUES (1,'Cheese Burger','Jarlsberg og ketchup salsa','Burger',current_timestamp,'2100-09-28 01:00:00','unknown.png');
Insert into product_price (price, valid_from, valid_to,product_id) VALUES (124,current_timestamp,'2100-09-28 01:00:00',1);

Insert Into product (store_id, name, description, "Group", valid_from, valid_to, Icon_src)
VALUES (1,'Don Diablo','Jarlsberg, jalapenos og Spicy BBQ','Burger',current_timestamp,'2100-09-28 01:00:00','unknown.png');
Insert into product_price (price, valid_from, valid_to,product_id) VALUES (132,current_timestamp,'2100-09-28 01:00:00',2);

Insert Into product (store_id, name, description, "Group", valid_from, valid_to, Icon_src)
VALUES (1,'Don Blues','Gorgonzola, bacon, karamelisert løk og dijon sennep','Burger',current_timestamp,'2100-09-28 01:00:00','unknown.png');
Insert into product_price (price, valid_from, valid_to,product_id) VALUES (148,current_timestamp,'2100-09-28 01:00:00',3);

Insert Into product (store_id, name, description, "Group", valid_from, valid_to, Icon_src)
VALUES (1,'Chilli Cheese','Flytende cheddar med 3 typer chilli og nachos','Burger',current_timestamp,'2100-09-28 01:00:00','unknown.png');
Insert into product_price (price, valid_from, valid_to,product_id) VALUES (134,current_timestamp,'2100-09-28 01:00:00',4);

Insert Into product (store_id, name, description, "Group", valid_from, valid_to, Icon_src)
VALUES (1,'Truffl''issues','Inneholder bare Trøffel ostesaus, trøffel mayo,Bacon, patty og salat blad ( no fresh truffle used )','Burger',current_timestamp,'2100-09-28 01:00:00','unknown.png');
Insert into product_price (price, valid_from, valid_to,product_id) VALUES (145,current_timestamp,'2100-09-28 01:00:00',5);

Insert Into product (store_id, name, description, "Group", valid_from, valid_to, Icon_src)
VALUES (1,'French fries','French fries','Sides',current_timestamp,'2100-09-28 01:00:00','unknown.png');
Insert into product_price (price, valid_from, valid_to,product_id) VALUES (39,current_timestamp,'2100-09-28 01:00:00',6);

Insert Into product (store_id, name, description, "Group", valid_from, valid_to, Icon_src)
VALUES (1,'Sweet potato fries','Sweet potato fries','Sides',current_timestamp,'2100-09-28 01:00:00','unknown.png');
Insert into product_price (price, valid_from, valid_to,product_id) VALUES (44,current_timestamp,'2100-09-28 01:00:00',7);

Insert Into product (store_id, name, description, "Group", valid_from, valid_to, Icon_src)
VALUES (1,'Løkringer','Løkringer','Sides',current_timestamp,'2100-09-28 01:00:00','unknown.png');
Insert into product_price (price, valid_from, valid_to,product_id) VALUES (33,current_timestamp,'2100-09-28 01:00:00',8);

Insert Into product (store_id, name, description, "Group", valid_from, valid_to, Icon_src)
VALUES (1,'Chicken nuggets','Chicken nuggets','Sides',current_timestamp,'2100-09-28 01:00:00','unknown.png');
Insert into product_price (price, valid_from, valid_to,product_id) VALUES (64,current_timestamp,'2100-09-28 01:00:00',9);

Insert Into product (store_id, name, description, "Group", valid_from, valid_to, Icon_src)
VALUES (1,'Bacon','Extra bacon på burgerne i bestillingen','Extra',current_timestamp,'2100-09-28 01:00:00','unknown.png');
Insert into product_price (price, valid_from, valid_to,product_id) VALUES (34,current_timestamp,'2100-09-28 01:00:00',10);

Insert Into product (store_id, name, description, "Group", valid_from, valid_to, Icon_src)
VALUES (1,'Drikke','Gazzosa (italiensk sitron brus) - Coke - Coke Zero - Urge - Fanta - Fanta Lemon - Sprite - Iste peach - BonAqua naturell med kullsyre','Drikke',current_timestamp,'2100-09-28 01:00:00','unknown.png');
Insert into product_price (price, valid_from, valid_to,product_id) VALUES (34,current_timestamp,'2100-09-28 01:00:00',10);

Insert Into product (store_id, name, description, "Group", valid_from, valid_to, Icon_src)
VALUES (1,'Bacon','Extra bacon på burgerne i bestillingen','Extra',current_timestamp,'2100-09-28 01:00:00','unknown.png');
Insert into product_price (price, valid_from, valid_to,product_id) VALUES (20,current_timestamp,'2100-09-28 01:00:00',11);

Insert Into product (store_id, name, description, "Group", valid_from, valid_to, Icon_src)
VALUES (1,'Cheese','Extra Cheese på burgerne i bestillingen','Extra',current_timestamp,'2100-09-28 01:00:00','unknown.png');
Insert into product_price (price, valid_from, valid_to,product_id) VALUES (12,current_timestamp,'2100-09-28 01:00:00',12);

Insert Into product (store_id, name, description, "Group", valid_from, valid_to, Icon_src)
VALUES (1,'Double Meat','En ekstra patty for burgerne i bestillingen','Extra',current_timestamp,'2100-09-28 01:00:00','unknown.png');
Insert into product_price (price, valid_from, valid_to,product_id) VALUES (50,current_timestamp,'2100-09-28 01:00:00',13);

Insert Into product (store_id, name, description, "Group", valid_from, valid_to, Icon_src)
VALUES (1,'Ananas ','Extra ananas  på burgerne i bestillingen','Extra',current_timestamp,'2100-09-28 01:00:00','unknown.png');
Insert into product_price (price, valid_from, valid_to,product_id) VALUES (20,current_timestamp,'2100-09-28 01:00:00',14);

Insert Into product (store_id, name, description, "Group", valid_from, valid_to, Icon_src)
VALUES (1,'Sopp, sjampinjong','Extra sopp/sjampinjong på burgerne i bestillingen','Extra',current_timestamp,'2100-09-28 01:00:00','unknown.png');
Insert into product_price (price, valid_from, valid_to,product_id) VALUES (20,current_timestamp,'2100-09-28 01:00:00',15);



Insert into advertisement (product_id, valid_from, valid_to) VALUES (5,'1990-09-28 01:00:00','2991-09-28 01:00:00');

insert into store_settings (store_id, manual_time_verification, automatic_print_dialog) VALUES (1,true,true);

Insert into store_opening_hours (store_id, monday_start, monday_end, tuesday_start, tuesday_end, wednesday_start, wednesday_end, thursday_start, thursday_end, friday_start, friday_end, saturday_start, saturday_end, sunday_start, sunday_end)
VALUES (1,'00:00:00','00:00:00','05:00:00','21:00:00','15:00:00','21:00:00','15:00:00','21:00:00','15:00:00','21:00:00','15:00:00','21:00:00','15:00:00','21:00:00')

