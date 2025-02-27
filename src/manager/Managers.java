package manager;

public class Managers {

    public static TaskManager getDefault(){
        return new InMemoryTaskManager(Managers.getDefaultHistory());
    }

    public static HistoryManager getDefaultHistory(){
        return new InMemoryHistoryManager();
    }
}
