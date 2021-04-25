alter table mp_product change old_price old_amount decimal(16,2);
alter table mp_product change price amount decimal(16,2);