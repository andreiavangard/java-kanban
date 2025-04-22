package manager;

import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskType;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
        try (BufferedReader br = new BufferedReader(new FileReader(file.toString()))) {
            while (br.ready()) {
                String line = br.readLine();
                String[] mLine = line.split(",");
                if (!mLine[0].trim().equals("id")) {
                    int id = Integer.parseInt(mLine[0].trim());
                    switch (TaskType.getTypeFromString(mLine[1].trim())) {
                        case TASK:
                            Task task = new Task(line);
                            fileBackedTaskManager.setTaskInTasks(id, task);
                            break;
                        case EPIC:
                            Epic epic = new Epic(line);
                            fileBackedTaskManager.setEpicInEpics(id, epic);
                            break;
                        case SUBTASK:
                            Subtask subtask = new Subtask(line);
                            fileBackedTaskManager.setSubtaskInSubtasks(id, subtask);
                            break;
                    }
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка чтения файла: " + file.toString());
        }

        return fileBackedTaskManager;
    }

    public static String getFileHeader() {
        return "id, type, name, status, description, epic";
    }

    public void save() {
        try (FileWriter fileWriter = new FileWriter(file, StandardCharsets.UTF_8)) {
            fileWriter.write(getFileHeader() + "\n");
            for (Task task : getMapTasks().values()) {
                fileWriter.write(task.toString() + "\n");
            }
            for (Epic epic : getMapEpics().values()) {
                fileWriter.write(epic.toString() + "\n");
            }
            for (Subtask subtask : getMapMapSubtasks().values()) {
                fileWriter.write(subtask.toString() + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения в файл: " + file.toString());
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public Task getTaskInHistoryById(int id) {
        return super.getTaskInHistoryById(id);
    }

    @Override
    public int addTask(Task taskAdd) {
        int id = super.addTask(taskAdd);
        save();
        return id;
    }

    @Override
    public void updateTask(Task taskUpdate) {
        super.updateTask(taskUpdate);
        save();
    }

    @Override
    public void deleteTask(Task taskDelete) {
        super.deleteTask(taskDelete);
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void clearTask() {
        super.clearTask();
        save();
    }

    @Override
    public Task getTask(int taskId) {
        Task task = super.getTask(taskId);
        return task;
    }

    @Override
    public int addEpic(Epic epic) {
        int id = super.addEpic(epic);
        save();
        return id;
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void deleteEpic(Epic epic) {
        super.deleteEpic(epic);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void clearEpics() {
        super.clearEpics();
        save();
    }

    @Override
    public Epic getEpic(int epicId) {
        Epic epic = super.getEpic(epicId);
        save();
        return epic;
    }

    @Override
    public int addSubtask(Subtask subtask, Epic epic) {
        int id = super.addSubtask(subtask, epic);
        save();
        return id;
    }

    @Override
    public void updateSubtask(Subtask subtask, Epic epic) {
        super.updateSubtask(subtask, epic);
        save();
    }

    @Override
    public void deleteSubtask(Subtask subtask, Epic epic) {
        super.deleteSubtask(subtask, epic);
        save();
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }

    @Override
    public void clearSubtask() {
        super.clearSubtask();
        save();
    }

    @Override
    public void clearSubtask(Epic epic) {
        super.clearSubtask(epic);
        save();
    }

    @Override
    public ArrayList<Subtask> getSubtasks(Epic epic) {
        ArrayList<Subtask> outSubtasks = super.getSubtasks(epic);//new ArrayList<>();
        return outSubtasks;
    }

    @Override
    public Subtask getSubtask(int subtaskId) {
        Subtask subTask = super.getSubtask(subtaskId);
        return subTask;
    }

    @Override
    public ArrayList<Task> getTasks() {
        ArrayList<Task> tasks = super.getTasks();
        return tasks;
    }

    @Override
    public ArrayList<Epic> getEpics() {
        ArrayList<Epic> epics = super.getEpics();
        return epics;
    }

}
