package manager;

import task.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{
    private List<Task> history = new ArrayList<>();

    @Override
    public void add(Task task){
        if(history.size()==10){
            history.remove(0);
        }
        history.add(task.clone());
    }

    @Override
    public List<Task> getHistory(){
        return history;
    }
}
