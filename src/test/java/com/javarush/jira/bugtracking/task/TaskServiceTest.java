package com.javarush.jira.bugtracking.task;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import com.javarush.jira.bugtracking.Handlers;
import com.javarush.jira.common.error.DataConflictException;
import com.javarush.jira.ref.RefTo;
import com.javarush.jira.ref.RefType;
import com.javarush.jira.ref.ReferenceService;

@ExtendWith({MockitoExtension.class, OutputCaptureExtension.class})
class TaskServiceTest {
	@Mock
	private ActivityRepository activityRepository;
	@Mock
	private Handlers.ActivityHandler activityHandler;
	@Mock
	private ReferenceService referenceService;

	@InjectMocks
	private TaskService taskService;
	
	@Test
	void getTestingTimeSpent() {
		
		long expectedTime = 120L;
		Task task = TaskTestData.task8;
		
		long resultTime = getStatusTimeSpent(task, taskService::getTestingTimeSpent, TaskTestData.activitiesForTask8);
		
		assertEquals(expectedTime, resultTime);
	}
	
	@Test
	void getTestingTimeWaiting() {
		
		long expectedTime = 44L;
		Task task = TaskTestData.task8;
		
		long resultTime = getStatusTimeSpent(task, taskService::getTestingTimeWaiting, TaskTestData.activitiesForTask8);
		
		assertEquals(expectedTime, resultTime);
	}
	
	@Test
	void getTotalTestingTime() {
		long expectedTime = 164L;
		Task task = TaskTestData.task8;
		
		long resultTime = getStatusTimeSpent(task, taskService::getTestingTimeSpent, TaskTestData.activitiesForTask8) + getStatusTimeSpent(task, taskService::getTestingTimeWaiting, TaskTestData.activitiesForTask8);
		
		assertEquals(expectedTime, resultTime);
	}
	
	@Test
	void getDevelopmentTimeSpent() {
		
		long expectedTime = 81L;
		Task task = TaskTestData.task8;
		
		long resultTime = getStatusTimeSpent(task, taskService::getDevelopmentTimeSpent, TaskTestData.activitiesForTask8);
		
		assertEquals(expectedTime, resultTime);
	}
	
	@Test
	void getDevelopmentTimeWaiting() {
		
		long expectedTime = 2L;
		Task task = TaskTestData.task8;
		
		long resultTime = getStatusTimeSpent(task, taskService::getDevelopmentTimeWaiting, TaskTestData.activitiesForTask8);
		
		assertEquals(expectedTime, resultTime);
	}
	
	@Test
	void getTotalDevelopmentTime() {
		long expectedTime = 83L;
		Task task = TaskTestData.task8;
		
		long resultTime = getStatusTimeSpent(task, taskService::getDevelopmentTimeSpent, TaskTestData.activitiesForTask8) + getStatusTimeSpent(task, taskService::getDevelopmentTimeWaiting, TaskTestData.activitiesForTask8);
		assertEquals(expectedTime, resultTime);
	}
	
	private long getStatusTimeSpent(Task task, Function<Task, Long> methodToTest, List<Activity> activitiesUnmutable){
		
		List<Activity> activities = new ArrayList<>(activitiesUnmutable);
		Collections.reverse(activities);
		Map<String, RefTo> expectedStatusMap = TaskTestData.createTaskStatusMap();
		
		when(activityHandler.getRepository()).thenReturn(activityRepository);
		when(activityRepository.findAllByTaskIdOrderByUpdatedDesc(task.getId())).thenReturn(activities);
		
		try (MockedStatic<ReferenceService> mockedStatic = mockStatic(ReferenceService.class)) {
			mockedStatic.when(() -> ReferenceService.getRefs(RefType.TASK_STATUS)).thenReturn(TaskTestData.createTaskStatusMap());
			
			Map<String, RefTo> mockedStatusMap = ReferenceService.getRefs(RefType.TASK_STATUS);
			
			assertEquals(expectedStatusMap, mockedStatusMap);
			
			mockedStatic.verify(() -> ReferenceService.getRefs(RefType.TASK_STATUS));

			long resultTime = methodToTest.apply(task);
			
			verify(activityRepository, atLeastOnce()).findAllByTaskIdOrderByUpdatedDesc(task.getId());
			
			return resultTime;
		}
	}
	
	@Test
	void getDevelopmentTimeWaitingWhenTodoCanceled() {
		
		long expectedTime = 2L;
		Task task = TaskTestData.task8;
		
		long resultTime = getStatusTimeSpent(task, taskService::getDevelopmentTimeWaiting, TaskTestData.createActivitiesCanceledAfterTodoStatus());
		
		assertEquals(expectedTime, resultTime);
	}
	
	@Test
	void getDevelopmentTimeSpentWhenInProgressCanceled() {
		
		long expectedTime = 21L;
		Task task = TaskTestData.task8;
		
		long resultTime = getStatusTimeSpent(task, taskService::getDevelopmentTimeSpent, TaskTestData.createActivitiesCanceledAfterInProgressStatus());
		
		assertEquals(expectedTime, resultTime);
	}
	
	@Test
	void getTestingTimeWaitingWhenReadyForReviewCanceled() {
		
		long expectedTime = 19L;
		Task task = TaskTestData.task8;
		
		long resultTime = getStatusTimeSpent(task, taskService::getTestingTimeWaiting, TaskTestData.createActivitiesCanceledAfterReadyForReviewStatus());
		
		assertEquals(expectedTime, resultTime);
	}
	
	@Test
	void getTestingTimeSpentWhenReviewCanceled() {
		
		long expectedTime = 25L;
		Task task = TaskTestData.task8;
		
		long resultTime = getStatusTimeSpent(task, taskService::getTestingTimeSpent, TaskTestData.createActivitiesCanceledAfterReviewStatus());
		
		assertEquals(expectedTime, resultTime);
	}
	
	@Test
	void getTestingTimeWaitingWhenReadyForTestCanceled() {
		
		long expectedTime = 24L;
		Task task = TaskTestData.task8;
		
		long resultTime = getStatusTimeSpent(task, taskService::getTestingTimeWaiting, TaskTestData.createActivitiesCanceledAfterReadyForTestStatus());
		
		assertEquals(expectedTime, resultTime);
	}
	
	@Test
	void getTestingTimeWaitingWhenTestCanceled() {
		
		long expectedTime = 55L;
		Task task = TaskTestData.task8;
		
		long resultTime = getStatusTimeSpent(task, taskService::getTestingTimeSpent, TaskTestData.createActivitiesCanceledAfterTestStatus());
		
		assertEquals(expectedTime, resultTime);
	}
	
	@Test
	void ignoreNonPositiveDuration(CapturedOutput output) {
		Task task = TaskTestData.task8;
		String logWarn = "Ignored non-positive duration:";
		
		getStatusTimeSpent(task, taskService::getTestingTimeWaiting, TaskTestData.createActivitiesWhenTimeSpentLessOrEqualsZero());
		
		assertTrue(output.getOut().contains(logWarn));
	}
	
	@Test
	void throwExceptionWhenTaskIdNull(CapturedOutput output) {
		Task task = TaskTestData.taskWithNullId;
		task.setId(null);
		String exceptionMessage = "Task id must not be null";

		IllegalArgumentException ex = assertThrows(
				IllegalArgumentException.class, () -> taskService.getTimeSpent(task, TaskTestData.TODO));
		
		assertEquals(exceptionMessage, ex.getMessage());
	}
	
	@Test
	void throwExceptionWhenTaskIsNull(CapturedOutput output) {
		Task task = null;
		String exceptionMessage = "Task must not be null";
		
		IllegalArgumentException ex = assertThrows(
				IllegalArgumentException.class, () -> taskService.getTimeSpent(task, TaskTestData.TODO));
		
		assertEquals(exceptionMessage, ex.getMessage());
	}
	
	@Test
	void throwExceptionWhenWrongStatusOrder() {
		
		Task task = TaskTestData.task8;
		String exceptionMessage = "Cannot change task status from ";
		
		DataConflictException ex = assertThrows(DataConflictException.class, () 
				-> getStatusTimeSpent(task, taskService::getTestingTimeSpent, TaskTestData.createActivitesWithWrongStatusOrder));
		
		assertTrue(ex.getMessage().contains(exceptionMessage));
	}
	
}
