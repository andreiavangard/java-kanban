import manager.TaskManager;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        System.out.println("-----------------------------------------------------------------------");
        TaskManager taskManager = new TaskManager();


        Task task1 = new Task("Задача номер 1", "Описание задачи номер 1");
        taskManager.addTask(task1);

        Task task2 = new Task("Задача номер 2", "Описание задачи номер 2");
        taskManager.addTask(task2);

        System.out.println("Задача 1 - "+task1);
        System.out.println("Задача 2 - "+task2);
        System.out.println("-----------------------------------------------------------------------");

        Epic epic1 = new Epic("Эпик номер 1", "Две подзадачи");
        taskManager.addEpic(epic1);
        Epic epic2 = new Epic("Эпик номер 2", "Одна подзадача");
        taskManager.addEpic(epic2);

        Subtask subtask11 = new Subtask("Задача номер 1 эпика 1", "Описание задачи 1.1");
        taskManager.addSubtask(subtask11, epic1);

        Subtask subtask12 = new Subtask("Задача номер 2 эпика 1", "Описание задачи 1.2");
        taskManager.addSubtask(subtask12, epic1);

        Subtask subtask21 = new Subtask("Задача номер 1 эпика 2", "Описание задачи 2.1");
        taskManager.addSubtask(subtask21, epic2);

        System.out.println("Задача 1.1 - "+subtask11);
        System.out.println("Задача 1.2 - "+subtask12);
        System.out.println("Задача 2.1 - "+subtask21);
        System.out.println("Эпик 1 - "+epic1);
        System.out.println("Эпик 2 - "+epic2);
        System.out.println("-----------------------------------------------------------------------");

        System.out.println("Список задач' - "+taskManager.getTasks().toString());
        System.out.println("Список эпиков' - "+taskManager.getTasks().toString());
        System.out.println("Список подзадач эпика 1' - "+taskManager.getSubtasks(epic1).toString());
        System.out.println("Список подзадач эпика 2' - "+taskManager.getSubtasks(epic2).toString());
        System.out.println("-----------------------------------------------------------------------");

        task1.setStatus(Status.IN_PROGRESS);
        task1.setName(task1.getName()+"*");
        task1.setDescription(task1.getDescription()+"**");
        taskManager.updateTask(task1);

        task2.setStatus(Status.DONE);
        task2.setName(task2.getName()+"*");
        task2.setDescription(task2.getDescription()+"**");
        taskManager.updateTask(task2);
        System.out.println("Задача 1 после обновления- "+task1);
        System.out.println("Задача 2 - после обновления"+task2);
        System.out.println("-----------------------------------------------------------------------");
        System.out.println("Список задач после обновления' - "+taskManager.getTasks().toString());
        System.out.println("-----------------------------------------------------------------------");
        epic1.setName(epic1.getName()+"*");
        epic1.setDescription(epic1.getDescription()+"**");
        taskManager.updateEpic(epic1);

        epic2.setName(epic2.getName()+"*");
        epic2.setDescription(epic2.getDescription()+"**");
        taskManager.updateEpic(epic2);

        subtask11.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(subtask11, epic1);
        subtask12.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask12, epic1);
        subtask21.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask21, epic2);
        System.out.println("Эпик 1 после обновления- "+epic1);
        System.out.println("Эпик 2 после обновления- "+epic2);
        System.out.println("Задача 1.1 после обновления - "+subtask11);
        System.out.println("Задача 1.2 после обновления - "+subtask12);
        System.out.println("Задача 2.1 после обновления - "+subtask21);
        System.out.println("-----------------------------------------------------------------------");
        System.out.println("Список эпиков' - "+taskManager.getEpics().toString());
        System.out.println("Список подзадач эпика 1' - "+taskManager.getSubtasks(epic1).toString());
        System.out.println("Список подзадач эпика 2' - "+taskManager.getSubtasks(epic2).toString());
        System.out.println("-----------------------------------------------------------------------");
        System.out.println("Список задач эпика 1 после обновления- "+taskManager.getSubtasks(epic1));
        System.out.println("Список задач эпика 2 после обновления- "+taskManager.getSubtasks(epic2));
        System.out.println("-----------------------------------------------------------------------");

        taskManager.deleteTask(task1);
        System.out.println("Список задач после удаления задачи' - "+taskManager.getTasks().toString());
        System.out.println("-----------------------------------------------------------------------");

        taskManager.deleteSubtask(subtask11, epic1);
        System.out.println("Список эпиков после удаления подзадачи' - "+taskManager.getEpics().toString());
        System.out.println("Список подзадач эпика 1 после удаления подзадачи' - "+taskManager.getSubtasks(epic1).toString());
        System.out.println("Список подзадач эпика 2 после удаления подзадачи' - "+taskManager.getSubtasks(epic2).toString());
        System.out.println("-----------------------------------------------------------------------");

        taskManager.deleteEpic(epic2);
        System.out.println("Список эпиков после удаления эпика' - "+taskManager.getEpics().toString());
        System.out.println("Список подзадач эпика 2 после удаления эпика 2' - "+taskManager.getSubtasks(epic1).toString());
        System.out.println("-----------------------------------------------------------------------");
        taskManager.deleteSubtaskById(6);
        System.out.println("Список эпиков после удаления подзадачи по ид' - "+taskManager.getEpics().toString());
        System.out.println("Список подзадач эпика 1 после удаления подзадачи по ид' - "+taskManager.getSubtasks(epic1).toString());
        System.out.println("-----------------------------------------------------------------------");
        taskManager.clearTask();
        taskManager.clearEpics();
        taskManager.clearSubtask();
        System.out.println("Список эпиков после очистки' - "+taskManager.getEpics().toString());
        System.out.println("Список подзадач эпика 1 после очистки' - "+taskManager.getSubtasks(epic1).toString());
        System.out.println("Список подзадач эпика 2 после очистки' - "+taskManager.getSubtasks(epic2).toString());
        System.out.println("Список задач после очистки' - "+taskManager.getTasks().toString());

        System.out.println("Приехали!");


    }
}
