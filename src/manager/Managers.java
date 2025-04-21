package manager;

import task.Task;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class Managers {

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static TaskManager getFileBackedTaskManager() {
        try {
            Path csvFilePath = Files.createTempFile("fm_", "csv");
            //в задании нужно проверить сохранение и загрузку пустого файла; поэтому не просто создаем файл
            //пишем в него заголовок и сразу же его загрузим
            //по хорошему достаточно создать пустой файл если его нет, или загрузить менеджер из файла если есть
            try (FileWriter fileWriter = new FileWriter(csvFilePath.toFile(), StandardCharsets.UTF_8)) {
                fileWriter.write(Task.getFileHeader() + "\n");
            } catch (IOException e) {
                throw new ManagerSaveException("Ошибка сохранения в файл: " + csvFilePath.toString());
            }

            return getFileBackedTaskManager(csvFilePath.toFile());

        } catch (IOException e) {
            e.getMessage();
        }
        return null;
    }

    public static TaskManager getFileBackedTaskManager(File file) {
        return FileBackedTaskManager.loadFromFile(file);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
