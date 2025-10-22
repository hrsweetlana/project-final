package com.javarush.jira.bugtracking.task;

import com.javarush.jira.MatcherFactory;
import com.javarush.jira.bugtracking.UserBelong;
import com.javarush.jira.bugtracking.task.to.ActivityTo;
import com.javarush.jira.bugtracking.task.to.TaskTo;
import com.javarush.jira.bugtracking.task.to.TaskToExt;
import com.javarush.jira.bugtracking.task.to.TaskToFull;
import com.javarush.jira.common.to.CodeTo;
import com.javarush.jira.ref.RefTo;
import com.javarush.jira.ref.RefType;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static com.javarush.jira.bugtracking.ObjectType.TASK;
import static com.javarush.jira.login.internal.web.UserTestData.ADMIN_ID;
import static com.javarush.jira.login.internal.web.UserTestData.USER_ID;

public class TaskTestData {
    public static final MatcherFactory.Matcher<Task> TASK_MATCHER = MatcherFactory.usingIgnoringFieldsComparator(Task.class, "id", "startpoint", "endpoint", "activities", "project", "sprint", "parent", "tags");
    public static final MatcherFactory.Matcher<TaskTo> TASK_TO_MATCHER = MatcherFactory.usingIgnoringFieldsComparator(TaskTo.class, "id", "startpoint", "endpoint");
    public static final MatcherFactory.Matcher<Set> TAG_MATCHER = MatcherFactory.usingEqualsComparator(Set.class);
    public static final MatcherFactory.Matcher<TaskToFull> TASK_TO_FULL_MATCHER = MatcherFactory.usingIgnoringFieldsComparator(TaskToFull.class, "id", "updated", "activityTos.id");
    public static final MatcherFactory.Matcher<Activity> ACTIVITY_MATCHER = MatcherFactory.usingIgnoringFieldsComparator(Activity.class, "title", "updated", "author");
    public static final MatcherFactory.Matcher<UserBelong> USER_BELONG_MATCHER = MatcherFactory.usingIgnoringFieldsComparator(UserBelong.class, "id", "startpoint", "endpoint");

    public static final long TASK1_ID = 1;
    public static final long TASK2_ID = 2;
    public static final long TASK8_ID = 8;
    public static final long READY_FOR_TEST_TASK_ID = 3;
    public static final long READY_FOR_REVIEW_TASK_ID = 4;
    public static final long TODO_TASK_ID = 5;
    public static final long DONE_TASK_ID = 6;
    public static final long CANCELED_TASK_ID = 7;
    public static final long SPRINT1_ID = 1;
    public static final long SPRINT5_ID = 5;
    public static final long SPRINT7_ID = 7;
    public static final long SPRINT_ID_NOT_EXIST = 100;
    public static final long PROJECT1_ID = 1;
    public static final long PROJECT2_ID = 2;
    public static final long PROJECT3_ID = 3;
    public static final long PROJECT_ID_NOT_EXIST = 100;
    public static final long ACTIVITY1_ID = 1;
    public static final long AUTHOR2_ID = 2;
    public static final long NOT_FOUND = 100;
    public static final String TODO = "todo";
    public static final String IN_PROGRESS = "in_progress";
    public static final String READY_FOR_REVIEW = "ready_for_review";
    public static final String READY_FOR_TEST = "ready_for_test";
    public static final String TEST = "test";
    public static final String DONE = "done";
    public static final String CANCELED = "canceled";
    public static final String TASK_DEVELOPER = "task_developer";
    public static final String TASK_REVIEWER = "task_reviewer";

    public static final Set<String> project1Tags = Set.of("pr1_sprnt1_task1_tag1", "pr1_sprnt1_task1_tag2", "pr1_sprnt1_task2_tag1", "pr1_sprnt1_task2_tag2");
    public static final Set<String> project1task1Tag1 = Set.of("pr1_sprnt1_task1_tag1");
    public static final Set<String> project1task1Tag2 = Set.of("pr1_sprnt1_task1_tag2");
    public static final Set<String> project1task2Tag1 = Set.of("pr1_sprnt1_task2_tag1");
    public static final Set<String> project1task2Tag2 = Set.of("pr1_sprnt1_task2_tag2");
    public static final Set<String> project2Tags = Set.of("pr2_sprnt5_task3_tag1", "pr2_sprnt5_task4_tag1", "pr2_sprnt5_task4_tag2", "pr2_sprnt5_task4_tag3", "pr2_sprnt5_task5_tag1", "pr2_sprnt5_task6_tag1", "pr2_sprnt5_task7_tag1", "pr2_sprnt5_task7_tag2", "pr2_sprnt7_task8_tag3");
    public static final Set<String> project3Tags = Set.of("pr3_sprnt0_task9_tag1"); 
    public static final Set<String> sprint5Tags = Set.of("pr2_sprnt5_task6_tag1","pr2_sprnt5_task3_tag1","pr2_sprnt5_task7_tag2","pr2_sprnt5_task7_tag1","pr2_sprnt5_task5_tag1","pr2_sprnt5_task4_tag1","pr2_sprnt5_task4_tag3","pr2_sprnt5_task4_tag2");
    public static final Set<String> allTags = Set.of("pr1_sprnt1_task1_tag1","pr1_sprnt1_task1_tag2", "pr1_sprnt1_task2_tag1", "pr1_sprnt1_task2_tag2", "pr2_sprnt5_task3_tag1", "pr2_sprnt5_task4_tag1", "pr2_sprnt5_task4_tag2", "pr2_sprnt5_task4_tag3", "pr2_sprnt5_task5_tag1", "pr2_sprnt5_task6_tag1", "pr2_sprnt5_task7_tag1", "pr2_sprnt5_task7_tag2", "pr2_sprnt7_task8_tag3", "pr3_sprnt0_task9_tag1", "pr3_sprnt8_task10_tag2");
    public static final Set<String> notExistTag1 = Set.of("notExist1");
    public static final Set<String> notExistTag2 = Set.of("notExist2");
    
    public static final Task task8 = new Task(TASK8_ID, "task-8", "task", "todo",null, PROJECT2_ID, SPRINT7_ID, Set.of("pr2_sprnt7_task8_tag3")); 
    public static final TaskTo taskTo1 = new TaskTo(TASK1_ID, "epic-" + TASK1_ID, "Data", "epic", "in_progress", null, PROJECT1_ID, SPRINT1_ID, Set.of("pr1_sprnt1_task1_tag1", "pr1_sprnt1_task1_tag2"));
    public static final TaskTo taskTo2 = new TaskTo(TASK2_ID, "epic-" + TASK2_ID, "Trees", "epic", "in_progress", null, PROJECT1_ID, SPRINT1_ID, Set.of("pr1_sprnt1_task2_tag1", "pr1_sprnt1_task2_tag2"));
    public static final TaskToFull taskToFull1 = new TaskToFull(TASK1_ID, "epic-1", "Data", null, "epic", "in_progress", "normal", null, 4, Set.of("pr1_sprnt1_task1_tag1", "pr1_sprnt1_task1_tag2"), null, new CodeTo(PROJECT1_ID, "PR1"), new CodeTo(SPRINT1_ID, "SP-1.001"), null);
    public static final TaskToFull taskToFull2 = new TaskToFull(TASK2_ID, "epic-2", "Trees UPD", "task UPD", "epic", "ready_for_review", "high", null, 4,  Set.of(Stream.concat(project1task2Tag1.stream(), project1task2Tag2.stream()).toArray(String[]::new)), null, new CodeTo(PROJECT1_ID, "PR1"), new CodeTo(SPRINT1_ID, "SP-1.001"), null);
    public static final ActivityTo activityTo1ForTask1 = new ActivityTo(ACTIVITY1_ID, TASK1_ID, USER_ID, null, null, "in_progress", "low", "epic", "Data", null, 3, null);
    public static final ActivityTo activityTo2ForTask1 = new ActivityTo(ACTIVITY1_ID + 1, TASK1_ID, ADMIN_ID, null, null, null, "normal", null, "Data", null, null, null);
    public static final ActivityTo activityTo3ForTask1 = new ActivityTo(ACTIVITY1_ID + 2, TASK1_ID, USER_ID, null, null, null, null, null, "Data", null, 4, null);
    public static final List<ActivityTo> activityTosForTask1 = List.of(activityTo3ForTask1, activityTo2ForTask1, activityTo1ForTask1);
    public static final ActivityTo activityTo1ForTask2 = new ActivityTo(ACTIVITY1_ID + 3, TASK2_ID, USER_ID, null, null, "in_progress", "normal", "epic", "Trees", "Trees desc", 4, null);
    public static final ActivityTo updatePriorityCode = new ActivityTo(ACTIVITY1_ID + 4, TASK2_ID, USER_ID, null, null, "ready_for_review", "high", "epic", "Trees UPD", "task UPD", 4, null);
    public static final List<ActivityTo> activityTosForTask2 = List.of(updatePriorityCode, activityTo1ForTask2);
    public static final List<Activity> activitiesForTask8 = List.of(
    	    new Activity(ACTIVITY1_ID, TASK8_ID, AUTHOR2_ID, LocalDateTime.parse("2025-10-15T09:18:00"), null, "todo", "normal", "epic", "Task in to do", null, 2),
    	    new Activity(ACTIVITY1_ID + 1, TASK8_ID, AUTHOR2_ID, LocalDateTime.parse("2025-10-15T09:20:00"), null, "in_progress", "normal", "epic", "Task in progres", null, 2),
    	    new Activity(ACTIVITY1_ID + 2, TASK8_ID, AUTHOR2_ID, LocalDateTime.parse("2025-10-15T09:31:00"), null, "ready_for_review", "normal", "epic", "Task in ready for review", null, 2),
    	    new Activity(ACTIVITY1_ID + 3, TASK8_ID, AUTHOR2_ID, LocalDateTime.parse("2025-10-15T09:35:00"), null, "review", "normal", "epic", "Task in review", null, 2),
    	    new Activity(ACTIVITY1_ID + 4, TASK8_ID, AUTHOR2_ID, LocalDateTime.parse("2025-10-15T09:40:00"), null, "todo", "normal", "epic", "Task in to do", null, 2),
    	    new Activity(ACTIVITY1_ID + 5, TASK8_ID, AUTHOR2_ID, LocalDateTime.parse("2025-10-15T09:50:00"), null, "in_progress", "normal", "epic", "Task in progres", null, 2),
    	    new Activity(ACTIVITY1_ID + 6, TASK8_ID, AUTHOR2_ID, LocalDateTime.parse("2025-10-15T10:00:00"), null, "ready_for_review", "normal", "epic", "Task in ready for review", null, 2),
    	    new Activity(ACTIVITY1_ID + 7, TASK8_ID, AUTHOR2_ID, LocalDateTime.parse("2025-10-15T10:15:00"), null, "review", "normal", "epic", "Task in review", null, 2),
    	    new Activity(ACTIVITY1_ID + 8, TASK8_ID, AUTHOR2_ID, LocalDateTime.parse("2025-10-15T10:25:00"), null, "ready_for_test", "normal", "epic", "Task in ready for test", null, 2),
    	    new Activity(ACTIVITY1_ID + 9, TASK8_ID, AUTHOR2_ID, LocalDateTime.parse("2025-10-15T10:30:00"), null, "test", "normal", "epic", "Task in test", null, 2),
    	    new Activity(ACTIVITY1_ID + 10, TASK8_ID, AUTHOR2_ID, LocalDateTime.parse("2025-10-15T10:40:00"), null, "todo", "normal", "epic", "Task in to do", null, 2),
    	    new Activity(ACTIVITY1_ID + 11, TASK8_ID, AUTHOR2_ID, LocalDateTime.parse("2025-10-15T11:00:00"), null, "in_progress", "normal", "epic", "Task in progres", null, 2),
    	    new Activity(ACTIVITY1_ID + 12, TASK8_ID, AUTHOR2_ID, LocalDateTime.parse("2025-10-15T12:00:00"), null, "ready_for_review", "normal", "epic", "Task in ready for review", null, 2),
    	    new Activity(ACTIVITY1_ID + 13, TASK8_ID, AUTHOR2_ID, LocalDateTime.parse("2025-10-15T12:10:00"), null, "review", "normal", "epic", "Task in review", null, 2),
    	    new Activity(ACTIVITY1_ID + 14, TASK8_ID, AUTHOR2_ID, LocalDateTime.parse("2025-10-15T12:20:00"), null, "ready_for_test", "normal", "epic", "Task in ready_for_test", null, 2),
    	    new Activity(ACTIVITY1_ID + 15, TASK8_ID, AUTHOR2_ID, LocalDateTime.parse("2025-10-15T12:30:00"), null, "test", "normal", "epic", "Task in test", null, 2),
    	    new Activity(ACTIVITY1_ID + 16, TASK8_ID, AUTHOR2_ID, LocalDateTime.parse("2025-10-15T13:25:00"), null, "done", "normal", "epic", "Task done", null, 2)
    	);

    public static final UserBelong userTask1Assignment1 = new UserBelong(1L, TASK, USER_ID, "task_developer");
    public static final UserBelong userTask1Assignment2 = new UserBelong(1L, TASK, USER_ID, "task_tester");
    public static final UserBelong userTask2Assignment1 = new UserBelong(2L, TASK, USER_ID, "task_developer");
    public static final UserBelong userTask2Assignment2 = new UserBelong(2L, TASK, USER_ID, "task_tester");

    static {
        taskToFull1.setActivityTos(activityTosForTask1);
        taskToFull2.setActivityTos(activityTosForTask2);
    }

    public static TaskToExt getNewTaskTo() {
        return new TaskToExt(null, "epic-1", "Data New", "task NEW", "epic", "in_progress", "low", null, 3, null, PROJECT1_ID, SPRINT1_ID, null);
    }

    public static ActivityTo getNewActivityTo() {
        return new ActivityTo(null, TASK1_ID, USER_ID, null, null, "ready_for_review", null, "epic", null, null, 4, null);
    }

    public static TaskToExt getUpdatedTaskTo() {
        return new TaskToExt(TASK2_ID, "epic-2", "Trees UPD", "task UPD", "epic", "ready_for_review", "high", null, 4, null, PROJECT1_ID, SPRINT1_ID, Set.of(Stream.concat(project1task2Tag1.stream(), project1task2Tag2.stream()).toArray(String[]::new)));
    }

    public static ActivityTo getUpdatedActivityTo() {
        return new ActivityTo(ACTIVITY1_ID, TASK1_ID, USER_ID, null, null, "in_progress", "low", "epic", null, null, 3, null);
    }
    
    public static List<Activity> activitiesWithoutTestingWaitingStatuses(){
    	   List<Activity> activities= List.of(
    	    	    new Activity(ACTIVITY1_ID + 1, TASK8_ID, AUTHOR2_ID, LocalDateTime.parse("2025-10-15T09:20:00"), null, "in_progress", "normal", "epic", "Task in progres", null, 2),
    	    	    new Activity(ACTIVITY1_ID + 2, TASK8_ID, AUTHOR2_ID, LocalDateTime.parse("2025-10-15T09:31:00"), null, "ready_for_review", "normal", "epic", "Task in ready for review", null, 2),
    	    	    new Activity(ACTIVITY1_ID + 3, TASK8_ID, AUTHOR2_ID, LocalDateTime.parse("2025-10-15T09:35:00"), null, "review", "normal", "epic", "Task in review", null, 2),
    	    	    new Activity(ACTIVITY1_ID + 4, TASK8_ID, AUTHOR2_ID, LocalDateTime.parse("2025-10-15T09:40:00"), null, "todo", "normal", "epic", "Task in to do", null, 2),
    	    	    new Activity(ACTIVITY1_ID + 5, TASK8_ID, AUTHOR2_ID, LocalDateTime.parse("2025-10-15T09:50:00"), null, "in_progress", "normal", "epic", "Task in progres", null, 2));
    	    	 return activities;   
    }
    


        public static Map<String, RefTo> createTaskStatusMap() {
            Map<String, RefTo> map = new HashMap<>();

            map.put("todo", new RefTo(1L, RefType.TASK_STATUS,"todo", "Todo", "in_progress,canceled|"));
            map.put("in_progress", new RefTo(2L, RefType.TASK_STATUS,"in_progress", "In progress", "ready_for_review,canceled|task_developer"));
            map.put("ready_for_review", new RefTo(3L, RefType.TASK_STATUS, "ready_for_review", "Ready for review", "review,canceled|"));
            map.put("review", new RefTo(4L, RefType.TASK_STATUS,"review", "Review", "todo,ready_for_test,canceled|task_reviewer"));
            map.put("ready_for_test", new RefTo(5L, RefType.TASK_STATUS, "ready_for_test", "Ready for test", "test,canceled|"));
            map.put("test", new RefTo(6L, RefType.TASK_STATUS, "test", "Test", "done,canceled|task_tester"));
            map.put("done", new RefTo(7L, RefType.TASK_STATUS, "done", "Done", "canceled|"));
            map.put("canceled", new RefTo(8L, RefType.TASK_STATUS, "canceled", "Canceled",null));

            return map;
        }
            
       public static Map<String, String> createStatusChangePossibleMap(){
    	   Map<String, String> map = new HashMap<>();
    	   
    	   map.put("todo", "in_progress,canceled");
    	   map.put("in_progress", "ready_for_review,canceled");
    	   map.put("ready_for_review", "review,canceled");
    	   map.put("review", "todo,ready_for_test,canceled");
    	   map.put("ready_for_test", "test,canceled");
    	   map.put("test", "done,canceled");
    	   map.put("done", "canceled");
    	   map.put("canceled", null);
    	   
    	   return map;
       } 
        

}
