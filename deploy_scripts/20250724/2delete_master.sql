DELETE FROM schooldev.dashboard_widget_privilege;
DELETE FROM schooldev.dashboard_widget_master;
DELETE FROM schooldev."IIT_WD_USER_TAB_SETTINGS";
DELETE FROM schooldev."IIT_WD_ROLE_TAB_SETTINGS";
DELETE FROM schooldev."IIT_WD_DASHBOARD_TAB_MASTER";
DELETE FROM schooldev."IIT_W_MAIL_TEMPLATE";
DELETE FROM schooldev.role_menu_privilege;
DELETE FROM schooldev.user_role;
DELETE FROM schooldev.roles;
DELETE FROM schooldev."MENU_LIST";
DELETE FROM schooldev."SIMS_CONFIG_DATA";

/*
MENU_LIST
dashboard_widget_master
dashboard_widget_privilege
IIT_WD_DASHBOARD_TAB_MASTER
IIT_WD_ROLE_TAB_SETTINGS
IIT_WD_USER_TAB_SETTINGS
IIT_W_MAIL_TEMPLATE
roles
role_menu_privilege
SIMS_CONFIG_DATA

 pg_dump -h localhost -U postgres -d live_211125 --data-only  --column-inserts -n schooldev -t 'schooldev."MENU_LIST"' -t 'schooldev.dashboard_widget_master' -t 'schooldev.dashboard_widget_privilege' -t 'schooldev."IIT_WD_DASHBOARD_TAB_MASTER"' -t 'schooldev."IIT_WD_ROLE_TAB_SETTINGS"' -t 'schooldev."IIT_WD_USER_TAB_SETTINGS"' -t 'schooldev."IIT_W_MAIL_TEMPLATE"' -t 'schooldev.roles' -t 'schooldev.role_menu_privilege' -t 'schooldev."SIMS_CONFIG_DATA"' > inserts.sql
*/

