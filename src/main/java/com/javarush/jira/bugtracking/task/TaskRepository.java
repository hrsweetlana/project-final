package com.javarush.jira.bugtracking.task;

import com.javarush.jira.common.BaseRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Transactional(readOnly = true)
public interface TaskRepository extends BaseRepository<Task> {
    @Query("SELECT t FROM Task t LEFT JOIN FETCH t.tags WHERE t.sprintId =:sprintId ORDER BY t.startpoint DESC")
    List<Task> findAllBySprintId(long sprintId);

    @Query("SELECT t FROM Task t WHERE t.projectId =:projectId AND t.sprintId IS NULL")
    List<Task> findAllByProjectIdAndSprintIsNull(long projectId);

    @Query("SELECT t FROM Task t LEFT JOIN FETCH t.tags WHERE t.projectId =:projectId ORDER BY t.startpoint DESC")
    List<Task> findAllByProjectId(long projectId);

    @Query("SELECT t FROM Task t LEFT JOIN FETCH t.tags JOIN FETCH t.project LEFT JOIN FETCH t.sprint LEFT JOIN FETCH t.parent WHERE t.id =:id")
    Optional<Task> findFullById(long id);
    
    @Query("SELECT t FROM Task t LEFT JOIN FETCH t.tags tag WHERE t.sprintId =: sprintId AND tag IN :values")
    List<Task> findAllBySprintIdByTags(long id, Set<String> values);
    
    @Query("SELECT t FROM Task t LEFT JOIN FETCH t.tags tag WHERE t.projectId =: projectId AND tag IN :values")
    List<Task> findAllByProjectIdByTags(long id, Set<String> values);
    
    @Query("SELECT t FROM Task t LEFT JOIN FETCH t.tags tag WHERE tag IN :values")
    List<Task> findAllByTags(Set<String> values);
    
    @Modifying
    @Query(value = """
            WITH RECURSIVE task_with_subtasks AS (
                SELECT id, id AS child
                FROM task
                WHERE parent_id is null AND id =:taskId
                UNION ALL
                    SELECT task_with_subtasks.id, t.id
                    FROM task_with_subtasks JOIN task t ON t.parent_id = task_with_subtasks.child
            )
            UPDATE task
            SET sprint_id =:sprintId
            WHERE id IN (SELECT child FROM task_with_subtasks)
            """, nativeQuery = true)
    void setTaskAndSubTasksSprint(long taskId, Long sprintId);
}
