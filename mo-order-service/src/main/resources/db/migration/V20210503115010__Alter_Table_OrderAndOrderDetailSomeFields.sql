alter table  mp_order_detail change product_order_id  order_id  bigint(11);
alter table  mp_order change  nickname user_name varchar(64);
alter table  mp_order change  pay_amount actual_amount decimal(16, 2);
alter table  mp_order change  user_id user_id bigint(11);