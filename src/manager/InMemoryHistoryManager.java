package manager;

import task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager{
    private Node header;
    private Node tail;
    private List<Node> nodeListHistory;
    private Map<Integer, Node> nodeMapHistory;

    public InMemoryHistoryManager() {
        this.header = null;
        this.tail = null;
        this.nodeListHistory = new ArrayList<>();
        this.nodeMapHistory = new HashMap();
    }

    @Override
    public void add(Task task){
        remove(task.getId());
        linkLast(task);

    }

    @Override
    public void remove(int id){
        Node node = nodeMapHistory.get(id);
        if(node!=null){
            nodeMapHistory.remove(id);
            removeNode(node);
        }
    }

    @Override
    public List<Task> getHistory(){
        return getTasks();
    }

    @Override
    public  Task getTaskInHistoryById(int id){
        Node node = nodeMapHistory.get(id);
        if(node!=null){
            return node.getData();
        }
        return null;
    }

    private void removeNode(Node node){
        if(node!=header && node!=tail){
            //удаляем что то внутри
            Node next = node.getNext();
            Node prev = node.getPrev();
            next.setPrev(prev);
            prev.setNext(next);
        }else{
            if (node == header){
                //удаляем голову
                if(node.getPrev()!=null){
                    header = node.getPrev();
                    header.setNext(null);
                }else{
                    header = null;
                }
            }
            if (node == tail) {
                //удаляем хвост
                if(node.getNext()!=null){
                    tail = node.getNext();
                    tail.setPrev(null);
                }else{
                    tail = null;
                }
            }
        }
        nodeListHistory.remove(node);
    }

    private void linkLast(Task task){
        Node node = new Node(task);
        if(header==null){
           //первый элемент
           header = node;
           tail = node;
       }else{
            header.setNext(node);
            node.setPrev(header);
            header = node;
       }
        nodeListHistory.add(node);
        nodeMapHistory.put(task.getId(), node);
    }

    private List<Task> getTasks(){
        List<Task> tasks = new ArrayList<>();
        for (Node node : nodeListHistory){
            tasks.add(node.getData());
        }
        return tasks;
    }

    public Node getHeader() {
        return header;
    }

    public Node getTail() {
        return tail;
    }

    public Node getNodeByIdTask(int id) {
        return nodeMapHistory.get(id);
    }


}
