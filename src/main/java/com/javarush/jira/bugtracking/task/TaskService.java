package com.javarush.jira.bugtracking.task;

import com.javarush.jira.bugtracking.Handlers;
import com.javarush.jira.bugtracking.UserBelong;
import com.javarush.jira.bugtracking.UserBelongRepository;
import com.javarush.jira.bugtracking.sprint.Sprint;
import com.javarush.jira.bugtracking.sprint.SprintRepository;
import com.javarush.jira.bugtracking.task.mapper.TaskExtMapper;
import com.javarush.jira.bugtracking.task.mapper.TaskFullMapper;
import com.javarush.jira.bugtracking.task.to.TaskToExt;
import com.javarush.jira.bugtracking.task.to.TaskToFull;
import com.javarush.jira.common.error.DataConflictException;
import com.javarush.jira.common.error.NotFoundException;
import com.javarush.jira.common.util.Util;
import com.javarush.jira.login.AuthUser;
import com.javarush.jira.ref.RefType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.javarush.jira.bugtracking.ObjectType.TASK;
import static com.javarush.jira.bugtracking.task.TaskUtil.fillExtraFields;
import static com.javarush.jira.bugtracking.task.TaskUtil.makeActivity;
import static com.javarush.jira.bugtracking.task.TaskUtil.checkStatusChangePossible;
import static com.javarush.jira.ref.ReferenceService.getRefTo;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {
    static final String CANNOT_ASSIGN = "Cannot assign as %s to task with status=%s";
    static final String CANNOT_UN_ASSIGN = "Cannot unassign as %s from task with status=%s";
    static final String TEST = "test";
    static final String REVIEW = "review";
    static final String READY_FOR_TEST = "ready_for_test";
    static final String READY_FOR_REVIEW = "ready_for_review";
    static final String IN_PROGRESS = "in_progress";
    static final String TODO = "todo";
    

    private final Handlers.TaskExtHandler handler;
    private final Handlers.ActivityHandler activityHandler;
    private final TaskFullMapper fullMapper;
    private final SprintRepository sprintRepository;
    private final TaskExtMapper extMapper;
    private final UserBelongRepository userBelongRepository;
    
    @Transactional
    public void changeStatus(long taskId, String statusCode) {
        Assert.notNull(statusCode, "statusCode must not be null");
        Task task = handler.getRepository().getExisted(taskId);
        if (!statusCode.equals(task.getStatusCode())) {
            task.checkAndSetStatusCode(statusCode);
            Activity statusChangedActivity = new Activity(null, taskId, AuthUser.authId());
            statusChangedActivity.setStatusCode(statusCode);
            activityHandler.create(statusChangedActivity);
            String userType = getRefTo(RefType.TASK_STATUS, statusCode).getAux(1);
            if (userType != null) {
                handler.createUserBelong(taskId, TASK, AuthUser.authId(), userType);
            }
        }
    }

    @Transactional
    public void changeSprint(long taskId, Long sprintId) {
        Task task = handler.getRepository().getExisted(taskId);
        if (task.getParentId() != null) {
            throw new DataConflictException("Can't change subtask sprint");
        }
        if (sprintId != null) {
            Sprint sprint = sprintRepository.getExisted(sprintId);
            if (sprint.getProjectId() != task.getProjectId()) {
                throw new DataConflictException("Target sprint must belong to the same project");
            }
        }
        handler.getRepository().setTaskAndSubTasksSprint(taskId, sprintId);
    }

    @Transactional
    public Task create(TaskToExt taskTo) {
        Task created = handler.createWithBelong(taskTo, TASK, "task_author");
        activityHandler.create(makeActivity(created.id(), taskTo));
        return created;
    }

    @Transactional
    public void update(TaskToExt taskTo, long id) {
        if (!taskTo.equals(get(taskTo.id()))) {
            handler.updateFromTo(taskTo, id);
            activityHandler.create(makeActivity(id, taskTo));
        }
    }

    public TaskToFull get(long id) {
        Task task = Util.checkExist(id, handler.getRepository().findFullById(id));
        TaskToFull taskToFull = fullMapper.toTo(task);
        List<Activity> activities = activityHandler.getRepository().findAllByTaskIdOrderByUpdatedDesc(id);
        fillExtraFields(taskToFull, activities);
        taskToFull.setActivityTos(activityHandler.getMapper().toToList(activities));
        return taskToFull;
    }

    public TaskToExt getNewWithSprint(long sprintId) {
        Sprint sprint = sprintRepository.getExisted(sprintId);
        Task newTask = new Task();
        newTask.setSprintId(sprintId);
        newTask.setProjectId(sprint.getProjectId());
        return extMapper.toTo(newTask);
    }

    public TaskToExt getNewWithProject(long projectId) {
        Task newTask = new Task();
        newTask.setProjectId(projectId);
        return extMapper.toTo(newTask);
    }

    public TaskToExt getNewWithParent(long parentId) {
        Task parent = handler.getRepository().getExisted(parentId);
        Task newTask = new Task();
        newTask.setParentId(parentId);
        newTask.setSprintId(parent.getSprintId());
        newTask.setProjectId(parent.getProjectId());
        return extMapper.toTo(newTask);
    }

    public void assign(long id, String userType, long userId) {
        checkAssignmentActionPossible(id, userType, true);
        handler.createUserBelong(id, TASK, userId, userType);
    }

    @Transactional
    public void unAssign(long id, String userType, long userId) {
        checkAssignmentActionPossible(id, userType, false);
        UserBelong assignment = userBelongRepository.findActiveAssignment(id, TASK, userId, userType)
                .orElseThrow(() -> new NotFoundException(String
                        .format("Not found assignment with userType=%s for task {%d} for user {%d}", userType, id, userId)));
        assignment.setEndpoint(LocalDateTime.now());
    }

    private void checkAssignmentActionPossible(long id, String userType, boolean assign) {
        Assert.notNull(userType, "userType must not be null");
        Task task = handler.getRepository().getExisted(id);
        String possibleUserType = getRefTo(RefType.TASK_STATUS, task.getStatusCode()).getAux(1);
        if (!userType.equals(possibleUserType)) {
            throw new DataConflictException(String.format(assign ? CANNOT_ASSIGN : CANNOT_UN_ASSIGN, userType, task.getStatusCode()));
        }
    }
    
    public long getTestingTimeSpent(Task task) {
    	return getTimeSpent(task, TEST, REVIEW);
    }

    public long getTestingTimeWaiting(Task task) {
    	return getTimeSpent(task, READY_FOR_TEST, READY_FOR_REVIEW);
    }
    
    public long getTotalTestingTime(Task task) {
    	return getTestingTimeSpent(task) + getTestingTimeWaiting(task);
    }

    public long getDevelopmentTimeSpent(Task task) {
    	return getTimeSpent(task, IN_PROGRESS);
    }
    
    public long getDevelopmentTimeWaiting(Task task) {
    	return getTimeSpent(task, TODO);
    }
    
    public long getTotalDevelopmentTime(Task task) {
    	return getDevelopmentTimeSpent(task) + getDevelopmentTimeWaiting(task);
    }
    
    public long getTimeSpent(Task task, String... statuses) {
    	if (task == null || task.getId() == null) {
    		throw new IllegalArgumentException("Task and task id must not be null");
    	}
    	
    	long timeSpent = 0;
    	long totalTimeSpent = 0;
    	
    	List<Activity> activities = loadActivities(task.getId());
    	
    	for(int i = 0; i < activities.size() - 1; i++) {
    		Activity current = activities.get(i);
    		Activity next = activities.get(i + 1);
    		
    		LocalDateTime currentUpdated = current.getUpdated();
    		LocalDateTime nextUpdated = next.getUpdated();
    		
    		if (currentUpdated == null || nextUpdated == null) {
    			log.warn("Skipping activity pair due to null updated: current={}, next={}", current, next);
    			continue;
    		}
    		
    		String currentStatus = next.getStatusCode();
    		String nextStatus = next.getStatusCode();
    		
    		checkStatusChangePossible(currentStatus, nextStatus);
    		
    		Duration duration = Duration.between(nextUpdated, currentUpdated);
    		timeSpent = duration.toMinutes();
    		
    		if (timeSpent <= 0) {
    			log.debug("Ignored non-positive duration: {} -> {}", nextUpdated, currentUpdated);
    		}
    		
    		if(calculateTimeForStatuses(next.getStatusCode(), statuses)) {
    			totalTimeSpent = totalTimeSpent + timeSpent;
    		}
    	}
    	return totalTimeSpent;
    }
    
    private List<Activity> loadActivities(Long taskId) {
    	 List<Activity> activities = activityHandler.getRepository().findAllByTaskIdOrderByUpdatedDesc(taskId);
    	    if (activities == null) {
    	        return Collections.emptyList();
    	    }

    	    return activities;
    }
    
    private boolean calculateTimeForStatuses(String status, String...includedStatuses) {
    	return Arrays.asList(includedStatuses).contains(status);
    }
}
