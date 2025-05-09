package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Task;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class MyLinkedHashMapTest {
    static InMemoryHistoryManager historyManager;
    static Task task1;
    static Task task2;
    static Task task3;
    protected LocalDateTime currentDate = LocalDateTime.now();
    protected int duration1m = 1;

    @BeforeEach
    void beforeAll() {
        historyManager = new InMemoryHistoryManager();
        TaskManager taskManager = Managers.getDefault();
        task1 = new Task("Тестовая задача1", "Описание тестовой задачи1", currentDate, duration1m);
        taskManager.addTask(task1);
        task2 = new Task("Тестовая задача2", "Описание тестовой задачи2", currentDate.plusMinutes(10), duration1m);
        taskManager.addTask(task2);
        task3 = new Task("Тестовая задача3", "Описание тестовой задачи3", currentDate.plusMinutes(20), duration1m);
        taskManager.addTask(task3);
    }

    @Test
    void addDeleteOneElement() {
        historyManager.add(task1);
        assertEquals(historyManager.getHistory().size(), 1);
        assertNotNull(historyManager.getHeader(), "Header не заполнен.");
        assertNotNull(historyManager.getTail(), "Tail не заполнен.");
        assertSame(historyManager.getHeader(), historyManager.getTail(), "При добавлении одного элемента голова не равна хвосту");
        assertSame(historyManager.getHeader().getTask(), task1, "Голова указывает не на правильную задачу");
        historyManager.remove(task1.getId());
        assertNull(historyManager.getHeader(), "Header не очищен.");
        assertNull(historyManager.getTail(), "Tail не очищен.");
        assertEquals(historyManager.getHistory().size(), 0);
    }

    @Test
    void addElementsDeleteHeader() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        assertEquals(historyManager.getHistory().size(), 3);
        assertNotNull(historyManager.getHeader(), "Header не заполнен.");
        assertNotNull(historyManager.getTail(), "Tail не заполнен.");
        assertSame(historyManager.getHeader().getTask(), task3, "Голова указывает не на правильную задачу");
        assertSame(historyManager.getTail().getTask(), task1, "Хвост указывает не на правильную задачу");
        historyManager.remove(task3.getId());
        assertEquals(historyManager.getHistory().size(), 2);
        assertSame(historyManager.getHeader().getTask(), task2, "Голова указывает не на правильную задачу");
        assertNull(historyManager.getHeader().getNext(), "В голове не очищена ссылка на следующий элемент.");
    }

    @Test
    void addElementsDeleteTail() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        assertEquals(historyManager.getHistory().size(), 3);
        assertNotNull(historyManager.getHeader(), "Header не заполнен.");
        assertNotNull(historyManager.getTail(), "Tail не заполнен.");
        assertSame(historyManager.getHeader().getTask(), task3, "Голова указывает не на правильную задачу");
        assertSame(historyManager.getTail().getTask(), task1, "Хвост указывает не на правильную задачу");
        historyManager.remove(task1.getId());
        assertEquals(historyManager.getHistory().size(), 2);
        assertSame(historyManager.getTail().getTask(), task2, "Хвост указывает не на правильную задачу");
        assertNull(historyManager.getTail().getPrev(), "В хвосте не очищена ссылка на предыдущий элемент.");
    }

    @Test
    void addElementsDeleteMiddle() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        assertEquals(historyManager.getHistory().size(), 3);
        assertNotNull(historyManager.getHeader(), "Header не заполнен.");
        assertNotNull(historyManager.getTail(), "Tail не заполнен.");
        assertSame(historyManager.getHeader().getTask(), task3, "Голова указывает не на правильную задачу");
        assertSame(historyManager.getTail().getTask(), task1, "Хвост указывает не на правильную задачу");
        historyManager.remove(task2.getId());
        assertEquals(historyManager.getHistory().size(), 2);
        assertSame(historyManager.getHeader().getTask(), task3, "Голова указывает не на правильную задачу");
        assertSame(historyManager.getTail().getTask(), task1, "Хвост указывает не на правильную задачу");
        assertSame(historyManager.getNodeByIdTask(task1.getId()).getNext(), historyManager.getNodeByIdTask(task3.getId()), "Первая задача не ссылается на третью");
        assertSame(historyManager.getNodeByIdTask(task1.getId()), historyManager.getNodeByIdTask(task3.getId()).getPrev(), "Третья задача не ссылается на первую");
    }
}
