package manager;

import task.Task;

public class Node {
    public Task task;
    public Node next;
    public Node prev;

    public Node(Task task) {
        this.task = task;
        this.prev = prev;
    }

    public void setNext(Node next) {
        this.next = next;
    }

    public void setPrev(Node prev) {
        this.prev = prev;
    }

    public Task getTask() {
        return task;
    }

    public Node getNext() {
        return next;
    }

    public Node getPrev() {
        return prev;
    }
}
