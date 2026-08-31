--After all inserts, run the below scripts
ALTER TABLE schooldev."MENU_LIST" ALTER COLUMN menu_id DROP DEFAULT;
ALTER TABLE schooldev."MENU_LIST" ALTER COLUMN menu_id TYPE bigint;
select setval('schooldev.menu_list_menu_id_seq', (select MAX(menu_id) + 1 from schooldev."MENU_LIST"));
ALTER TABLE schooldev."MENU_LIST" ALTER COLUMN menu_id SET DEFAULT nextval('schooldev.menu_list_menu_id_seq');

SELECT setval('schooldev."IIT_WD_DASHBOARD_TAB_MASTER_id_seq"', (SELECT MAX(id) FROM schooldev."IIT_WD_DASHBOARD_TAB_MASTER"));
ALTER TABLE schooldev."IIT_WD_DASHBOARD_TAB_MASTER" ALTER COLUMN id SET DEFAULT nextval('schooldev."IIT_WD_DASHBOARD_TAB_MASTER_id_seq"');
SELECT setval('schooldev."IIT_WD_ROLE_TAB_SETTINGS_id_seq"', (SELECT MAX(id) FROM schooldev."IIT_WD_ROLE_TAB_SETTINGS"));
SELECT setval('schooldev."IIT_WD_USER_TAB_SETTINGS_id_seq"', (SELECT MAX(id) FROM schooldev."IIT_WD_USER_TAB_SETTINGS"));
select setval('schooldev.menu_list_menu_id_seq', (select MAX(menu_id) + 1 from schooldev."MENU_LIST"));
ALTER SEQUENCE schooldev.menu_list_menu_id_seq OWNED BY schooldev."MENU_LIST".menu_id;
SELECT setval('schooldev.roles_role_id_seq', 45);
SELECT setval('schooldev.role_menu_privilege_id_seq', (SELECT MAX(id) FROM schooldev.role_menu_privilege));
SELECT setval('schooldev."SEQ_SIMS_CONFIG_DATA_CONFIG_ID"', (SELECT max(config_id) + 1 FROM schooldev."SIMS_CONFIG_DATA")::bigint);

update schooldev."USER_MANAGEMENT" set role_id=24 where user_name in ('srr.ms','sgr.ms','gouras.ms','fm.ms','prisim.ohm','sugan.mess','sk.mess','crcl.mess','kstar.mess');
update schooldev."USER_MANAGEMENT" set role_id =41 where role_id =38;


SELECT setval('schooldev."MESS_LEDGER_seq"', 545837);