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

import com.javarush.jira.bugtracking.Handlers;
import com.javarush.jira.ref.RefTo;
import com.javarush.jira.ref.RefType;
import com.javarush.jira.ref.ReferenceService;

@ExtendWith(MockitoExtension.class)
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
		
		long expectedTime = 90L;
		Task task = TaskTestData.task8;
		
		long resultTime = getStatusTimeSpent(task, taskService::getTestingTimeSpent);
		assertEquals(expectedTime, resultTime);

	}
	
	@Test
	void getTestingTimeWaiting() {
		
		long expectedTime = 44L;
		Task task = TaskTestData.task8;
		
		long resultTime = getStatusTimeSpent(task, taskService::getTestingTimeWaiting);
		assertEquals(expectedTime, resultTime);

	}
	
	@Test
	void getTotalTestingTime() {
		long expectedTime = 134L;
		Task task = TaskTestData.task8;
		
		long resultTime = getStatusTimeSpent(task, taskService::getTestingTimeSpent) + getStatusTimeSpent(task, taskService::getTestingTimeWaiting);
		assertEquals(expectedTime, resultTime);
	}
	
	@Test
	void getDevelopmentTimeSpent() {
		
		long expectedTime = 81L;
		Task task = TaskTestData.task8;
		
		long resultTime = getStatusTimeSpent(task, taskService::getDevelopmentTimeSpent);
		assertEquals(expectedTime, resultTime);

	}
	
	@Test
	void getDevelopmentTimeWaiting() {
		
		long expectedTime = 32L;
		Task task = TaskTestData.task8;
		
		long resultTime = getStatusTimeSpent(task, taskService::getDevelopmentTimeWaiting);
		assertEquals(expectedTime, resultTime);

	}
	
	@Test
	void getTotalDevelopmentTime() {
		long expectedTime = 113L;
		Task task = TaskTestData.task8;
		
		long resultTime = getStatusTimeSpent(task, taskService::getDevelopmentTimeSpent) + getStatusTimeSpent(task, taskService::getDevelopmentTimeWaiting);
		assertEquals(expectedTime, resultTime);
	}
	
	private long getStatusTimeSpent(Task task, Function<Task, Long> methodToTest){
		
		List<Activity> activities = new ArrayList<>(TaskTestData.activitiesForTask8);
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
}
