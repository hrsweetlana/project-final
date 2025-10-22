---------  users ----------------------
delete
from ROLES;
delete
from CONTACT;
delete
from PROFILE;

delete
from ACTIVITY;
drop sequence if exists ACTIVITY_ID_SEQ;
create sequence ACTIVITY_ID_SEQ start with 1; 

delete
from TASK;
drop sequence if exists TASK_ID_SEQ;
create sequence TASK_ID_SEQ start with 1;

delete
from SPRINT;
drop
sequence if exists SPRINT_ID_SEQ;
create sequence SPRINT_ID_SEQ start with 1;

delete
from PROJECT;
drop sequence if exists PROJECT_ID_SEQ;
create sequence PROJECT_ID_SEQ start with 1;

delete
from APP_USERS;
drop sequence if exists USERS_ID_SEQ;
create sequence USERS_ID_SEQ start with 1;

insert into APP_USERS (EMAIL, PASSWORD, FIRST_NAME, LAST_NAME, DISPLAY_NAME)
values ('user@gmail.com', '{noop}password', 'userFirstName', 'userLastName', 'userDisplayName'),
       ('admin@gmail.com', '{noop}admin', 'adminFirstName', 'adminLastName', 'adminDisplayName'),
       ('guest@gmail.com', '{noop}guest', 'guestFirstName', 'guestLastName', 'guestDisplayName'),
       ('manager@gmail.com', '{noop}manager', 'managerFirstName', 'managerLastName', 'managerDisplayName');
       
-- 0 DEV
-- 1 ADMIN
-- 2 MANAGER

insert into roles (app_user_id, app_roles)
values (1, 0),
       (2, 0),
       (2, 1),
       (4, 2);

insert into PROFILE (ID, LAST_FAILED_LOGIN, LAST_LOGIN, MAIL_NOTIFICATIONS)
values (1, null, null, 49),
       (2, null, null, 14);

insert into CONTACT (ID, CODE, CONTACT_VALUE)
values (1, 'skype', 'userSkype'),
       (1, 'mobile', '+01234567890'),
       (1, 'website', 'user.com'),
       (2, 'github', 'adminGitHub'),
       (2, 'tg', 'adminTg');


insert into PROJECT (code, title, description, type_code, parent_id)
values ('PR1', 'PROJECT-1', 'test project 1', 'task_tracker', null),
       ('PR2', 'PROJECT-2', 'test project 2', 'task_tracker', 1);

insert into SPRINT (status_code, startpoint, endpoint, code, project_id)
values ('finished', '2023-05-01 08:05:10', '2023-05-07 17:10:01', 'SP-1.001', 1),
       ('active', '2023-05-01 08:06:00', null, 'SP-1.002', 1),
       ('active', '2023-05-01 08:07:00', null, 'SP-1.003', 1),
       ('planning', '2023-05-01 08:08:00', null, 'SP-1.004', 1),
       ('active', '2023-05-10 08:06:00', null, 'SP-2.001', 2),
       ('planning', '2023-05-10 08:07:00', null, 'SP-2.002', 2),
       ('planning', '2023-05-10 08:08:00', null, 'SP-2.003', 2);

insert into TASK (TITLE, TYPE_CODE, STATUS_CODE, PROJECT_ID, SPRINT_ID, STARTPOINT)
values ('Data', 'epic', 'in_progress', 1, 1, '2023-05-15 09:05:10'),
       ('Trees', 'epic', 'in_progress', 1, 1, '2023-05-15 12:05:10'),
       ('task-3', 'task', 'ready_for_test', 2, 5, '2023-06-14 09:28:10'),
       ('task-4', 'task', 'ready_for_review', 2, 5, '2023-06-14 09:28:10'),
       ('task-5', 'task', 'todo', 2, 5, '2023-06-14 09:28:10'),
       ('task-6', 'task', 'done', 2, 5, '2023-06-14 09:28:10'),
       ('task-7', 'task', 'canceled', 2, 5, '2023-06-14 09:28:10');


insert into ACTIVITY(AUTHOR_ID, TASK_ID, UPDATED, COMMENT, TITLE, DESCRIPTION, ESTIMATE, TYPE_CODE, STATUS_CODE,
                     PRIORITY_CODE)
values (1, 1, '2023-05-15 09:05:10', null, 'Data', null, 3, 'epic', 'in_progress', 'low'),
       (2, 1, '2023-05-15 12:25:10', null, 'Data', null, null, null, null, 'normal'),
       (1, 1, '2023-05-15 14:05:10', null, 'Data', null, 4, null, null, null),
       (1, 2, '2023-05-15 12:05:10', null, 'Trees', 'Trees desc', 4, 'epic', 'in_progress', 'normal');

-- Додаємо колонку, яка має значення тільки для активних тасок
ALTER TABLE USER_BELONG 
ADD COLUMN UNIQUE_KEY BIGINT AS (CASE WHEN ENDPOINT IS NULL THEN OBJECT_ID ELSE NULL END);

-- Створюємо унікальний індекс на цю колонку + решту колонок
drop index UK_USER_BELONG;
CREATE UNIQUE INDEX UK_USER_BELONG 
ON USER_BELONG (UNIQUE_KEY, OBJECT_TYPE, APP_USER_ID, USER_TYPE_CODE);


insert into USER_BELONG (OBJECT_ID, OBJECT_TYPE, APP_USER_ID, USER_TYPE_CODE, STARTPOINT, ENDPOINT)
values (1, 2, 2, 'task_developer', '2023-06-14 08:35:10', '2023-06-14 08:55:00'),
       (1, 2, 2, 'task_reviewer', '2023-06-14 09:35:10', null),
       (1, 2, 1, 'task_developer', '2023-06-12 11:40:00', '2023-06-12 12:35:00'),
       (1, 2, 1, 'task_developer', '2023-06-13 12:35:00', null),
       (1, 2, 1, 'task_tester', '2023-06-14 15:20:00', null),
       (2, 2, 2, 'task_developer', '2023-06-08 07:10:00', null),
       (2, 2, 1, 'task_developer', '2023-06-09 14:48:00', null),
       (2, 2, 1, 'task_tester', '2023-06-10 16:37:00', null);
    
       
delete 
from CONTACT
where CODE='vk';

insert into PROJECT (CODE, TITLE, DESCRIPTION, TYPE_CODE, PARENT_ID)
values ('PR-3', 'PROJECT-3', 'test project 3', 'task_tracker', null);

insert into SPRINT (STATUS_CODE, STARTPOINT, ENDPOINT, CODE, PROJECT_ID)
values ('active', '2025-07-10 10:10:11', null, 'SP-3.001', 3);

insert into TASK (TITLE, TYPE_CODE, STATUS_CODE, PROJECT_ID, SPRINT_ID, STARTPOINT)
values ('task-8', 'task', 'todo', 2, 7, '2023-06-17 09:35:10'),
       ('task-9', 'task', 'canceled', 3, null, '2025-10-07 09:05:00'),
       ('task-10', 'task', 'in_progres', 3, 8, '2025-07-10 14:25:07');

insert into TASK_TAG(TASK_ID, TAG)
values (1, 'pr1_sprnt1_task1_tag1'),
       (1, 'pr1_sprnt1_task1_tag2'),
       (2, 'pr1_sprnt1_task2_tag1'),
       (2, 'pr1_sprnt1_task2_tag2'),
       (3, 'pr2_sprnt5_task3_tag1'),
       (4, 'pr2_sprnt5_task4_tag1'),
       (4, 'pr2_sprnt5_task4_tag2'),
       (4, 'pr2_sprnt5_task4_tag3'),
       (5, 'pr2_sprnt5_task5_tag1'),
       (6, 'pr2_sprnt5_task6_tag1'),
       (7, 'pr2_sprnt5_task7_tag1'),
       (7, 'pr2_sprnt5_task7_tag2'),
       (8, 'pr2_sprnt7_task8_tag3'),
       (9, 'pr3_sprnt0_task9_tag1'),
       (10, 'pr3_sprnt8_task10_tag2');
       
insert into ACTIVITY(AUTHOR_ID, TASK_ID, UPDATED, COMMENT, TITLE, DESCRIPTION, ESTIMATE, TYPE_CODE, STATUS_CODE,
                     PRIORITY_CODE)
 values(2, 8, '2025-10-15 09:18:00', null, 'Task in to do', null, 2, 'epic', 'todo', 'normal'),                    
       (2, 8, '2025-10-15 09:20:00', null, 'Task in progres', null, 2, 'epic', 'in_progress', 'normal'),
	   (2, 8, '2025-10-15 09:31:00', null, 'Task in ready for review', null, 2, 'epic', 'ready_for_review', 'normal'),
	   (2, 8, '2025-10-15 09:35:00', null, 'Task in review', null, 2, 'epic', 'review', 'normal'),
	   (2, 8, '2025-10-15 09:40:00', null, 'Task in to do', null, 2, 'epic', 'todo', 'normal'),
	   (2, 8, '2025-10-15 09:50:00', null, 'Task in progres', null, 2, 'epic', 'in_progress', 'normal'),
	   (2, 8, '2025-10-15 10:00:00', null, 'Task in ready for review', null, 2, 'epic', 'ready_for_review', 'normal'),
	   (2, 8, '2025-10-15 10:15:00', null, 'Task in review', null, 2, 'epic', 'review', 'normal'),
	   (2, 8, '2025-10-15 10:25:00', null, 'Task in ready for test', null, 2, 'epic', 'ready_for_test', 'normal'),
	   (2, 8, '2025-10-15 10:30:00', null, 'Task in test', null, 2, 'epic', 'test', 'normal'),
	   (2, 8, '2025-10-15 10:40:00', null, 'Task in to do', null, 2, 'epic', 'todo', 'normal'),
	   (2, 8, '2025-10-15 11:00:00', null, 'Task in progres', null, 2, 'epic', 'in_progress', 'normal'),
	   (2, 8, '2025-10-15 12:00:00', null, 'Task in ready for review', null, 2, 'epic', 'ready_for_review', 'normal'),
	   (2, 8, '2025-10-15 12:10:00', null, 'Task in review', null, 2, 'epic', 'review', 'normal'),
	   (2, 8, '2025-10-15 12:20:00', null, 'Task in ready_for_test', null, 2, 'epic', 'ready_for_test', 'normal'),
	   (2, 8, '2025-10-15 12:30:00', null, 'Task in test', null, 2, 'epic', 'test', 'normal'),
	   (2, 8, '2025-10-15 13:25:00', null, 'Task done', null, 2, 'epic', 'done', 'normal');