package manager;

import task.Task;

public class Node {
    public Task data;
    public Node next;
    public Node prev;

    public Node(Task data) {
        this.data = data;
        this.prev = prev;
    }

    public void setNext(Node next) {
        this.next = next;
    }

    public void setPrev(Node prev) {
        this.prev = prev;
    }

    public Task getData() {
        return data;
    }

    public Node getNext() {
        return next;
    }

    public Node getPrev() {
        return prev;
    }
}
