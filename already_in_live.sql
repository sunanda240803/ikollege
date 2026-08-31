
INSERT INTO schooldev."SIMS_CONFIG_DATA"
(config_key, config_value, created_by, created_at, modified_by, modified_at, active_flag, description, school_id)
VALUES('ONLINE_MESSS_COUPON_VEG_MESSIDS', '100,97', 'Admin', now(), 'Admin', now(), 'Y', '', 1);

INSERT INTO schooldev."SIMS_CONFIG_DATA"
(config_key, config_value, created_by, created_at, modified_by, modified_at, active_flag, description, school_id)
VALUES('ONLINE_MESSS_COUPON_NONVEG_MESSIDS', '101,102,103', 'Admin', now(), 'Admin', now(), 'Y', '', 1);

INSERT INTO schooldev."SIMS_CONFIG_DATA"
(config_key, config_value, created_by, created_at, modified_by, modified_at, active_flag, description, school_id)
VALUES('ONLINE_COUPON_VEG_DISCOUNTED_AMOUNT', '178', 'Admin', now(), 'Admin', now(), 'Y', '', 1);

INSERT INTO schooldev."SIMS_CONFIG_DATA"
(config_key, config_value, created_by, created_at, modified_by, modified_at, active_flag, description, school_id)
VALUES('ONLINE_COUPON_NONVEG_DISCOUNTED_AMOUNT', '187', 'Admin', now(), 'Admin', now(), 'Y', '', 1);


-- schooldev."IITM_GUEST_COUPON_ONLINE_PAYMENT_TRANSACTIONS" definition

-- DROP TABLE schooldev."IITM_GUEST_COUPON_ONLINE_PAYMENT_TRANSACTIONS";

CREATE TABLE schooldev."IITM_GUEST_COUPON_ONLINE_PAYMENT_TRANSACTIONS" (
	id bigserial NOT NULL,
	request_id int8 NOT NULL,
	overall_amount int8 NOT NULL,
	net_payable float8 NULL,
	user_id varchar(50) NULL,
	order_no varchar(50) NOT NULL,
	transaction_ref_number varchar(50) NULL,
	ccav_reference_no varchar(50) NULL,
	payment_method varchar(50) NULL,
	payment_gateway varchar(50) NULL,
	received_amount varchar(50) NULL,
	transaction_date timestamp NULL,
	payment_status varchar(50) NOT NULL,
	active_flag varchar(1) NOT NULL,
	school_id int4 DEFAULT 0 NOT NULL,
	created_by varchar(64) NOT NULL,
	created_at timestamp NOT NULL,
	modified_by varchar(64) NOT NULL,
	modified_at timestamp NOT NULL,
	trans_fee float8 NULL,
	service_tax float8 NULL,
	retry_count int4 DEFAULT 0 NULL,
	status_message varchar NULL,
	CONSTRAINT "IITM_GUEST_COUPON_ONLINE_PAYMENT_TRANSACTIONS_PK" PRIMARY KEY (id),
	CONSTRAINT "IITM_GUEST_COUPON_ONLINE_PAYMENT_TRANSACTIONS_REQID_FK" FOREIGN KEY (request_id) REFERENCES schooldev."IITM_GUEST_COUPON_PAYMENT_ADVICE"(request_id)
);

ALTER SEQUENCE schooldev."IITM_GUEST_COUPON_ONLINE_PAYMENT_TRANSACTIONS_id_seq" RENAME TO "IITM_GUEST_COUPON_ONLINE_PAYMENT_ORDERID";


ALTER TABLE schooldev."MESS_MASTER" ADD online_coupon bool DEFAULT false NULL;

ALTER TABLE schooldev."IITM_GUEST_COUPON_PAYMENT_ADVICE" ADD veg_or_nonveg varchar(8) NULL;
ALTER TABLE schooldev."IITM_GUEST_COUPON_PAYMENT_ADVICE" ALTER COLUMN category SET NOT NULL;
ALTER TABLE schooldev."IITM_GUEST_COUPON_PAYMENT_ADVICE" ALTER COLUMN overall_amount SET NOT NULL;
ALTER TABLE schooldev."IITM_GUEST_COUPON_PAYMENT_ADVICE" ALTER COLUMN dining_from_date SET NOT NULL;
ALTER TABLE schooldev."IITM_GUEST_COUPON_PAYMENT_ADVICE" ALTER COLUMN dining_to_date SET NOT NULL;

ALTER TABLE schooldev."IITM_GUEST_COUPON_ONLINE_PAYMENT_TRANSACTIONS" ALTER COLUMN request_id SET NOT NULL;
ALTER TABLE schooldev."IITM_GUEST_COUPON_ONLINE_PAYMENT_TRANSACTIONS" ALTER COLUMN overall_amount SET NOT NULL;
ALTER TABLE schooldev."IITM_GUEST_COUPON_ONLINE_PAYMENT_TRANSACTIONS" ALTER COLUMN order_no SET NOT NULL;
ALTER TABLE schooldev."IITM_GUEST_COUPON_ONLINE_PAYMENT_TRANSACTIONS" ALTER COLUMN payment_status SET NOT NULL;

ALTER TABLE schooldev."IITM_GUEST_COUPON_ONLINE_PAYMENT_TRANSACTIONS" ADD status_message varchar NULL;


ALTER TABLE schooldev."IITM_GUEST_COUPON_CONFIG" ADD snacks_amount int4 NULL;

ALTER TABLE schooldev."IITM_GUEST_COUPON_PAYMENT_ADVICE" ADD no_of_snacks_coupons int4 NULL;
ALTER TABLE schooldev."IITM_GUEST_COUPON_PAYMENT_ADVICE" ADD snacks_coupon_rate int4 NULL;
ALTER TABLE schooldev."IITM_GUEST_COUPON_PAYMENT_ADVICE" ADD config_discounted_amount int4 NULL;
ALTER TABLE schooldev."IITM_GUEST_COUPON_PAYMENT_ADVICE" ADD total_discounted_amount int4 NULL;
