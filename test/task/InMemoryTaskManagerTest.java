package task;

import manager.InMemoryTaskManager;
import org.junit.jupiter.api.Test;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @Override
    protected InMemoryTaskManager createTaskManager() {
        return new InMemoryTaskManager();
    }

    @Override
    @Test
    void addNewTask() {
        super.addNewTask();
    }

    @Override
    @Test
    void instancesTaskEqualIfTheirIdIsEqual() {
        super.instancesTaskEqualIfTheirIdIsEqual();
    }

    @Override
    @Test
    void checkTheImmutabilityOfTaskWhenAddingToManager() {
        super.checkTheImmutabilityOfTaskWhenAddingToManager();
    }

    @Override
    @Test
    void theStatusForNewOneIsSetCorrectly() {
        super.theStatusForNewOneIsSetCorrectly();
    }

    @Override
    @Test
    void assignedIdCannotBeRedefined() {
        super.assignedIdCannotBeRedefined();
    }

    @Override
    @Test
    void addNewEpicSubtask() {
        super.addNewEpicSubtask();
    }

    @Override
    @Test
    void instancesEpicSubtaskEqualIfTheirIdIsEqual() {
        super.instancesEpicSubtaskEqualIfTheirIdIsEqual();
    }

    @Override
    @Test
    void checkTheImmutabilityOfEpicSubtaskWhenAddingToManager() {
        super.checkTheImmutabilityOfEpicSubtaskWhenAddingToManager();
    }

    @Override
    @Test
    void theStatusForNewEpicSubtaskOneIsSetCorrectly() {
        super.theStatusForNewEpicSubtaskOneIsSetCorrectly();
    }

    @Override
    @Test
    void ownerIDCheck() {
        super.ownerIDCheck();
    }

    @Override
    @Test
    void checkingEpicStatusSetting() {
        super.checkingEpicStatusSetting();
    }

    @Override
    @Test
    void cantAddEmptyDateInTasksOfPriority() {
        super.cantAddEmptyDateInTasksOfPriority();
    }

    @Override
    @Test
    void priorityСalculatedСorrectly() {
        super.priorityСalculatedСorrectly();
    }

    @Override
    @Test
    void taskAddedExceptionOccurred() {
        super.taskAddedExceptionOccurred();
    }

    @Override
    @Test
    void startDateEndDateDurationCalculatedCorrectly() {
        super.startDateEndDateDurationCalculatedCorrectly();
    }

    @Override
    @Test
    void updateTaskForPriorityListCorrectly() {
        super.updateTaskForPriorityListCorrectly();
    }

    @Override
    @Test
    void whenTasksDeletedAreRemovedFromPriorityList() {
        super.whenTasksDeletedAreRemovedFromPriorityList();
    }

}
