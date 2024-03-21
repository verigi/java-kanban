package task.manager.history;

import task.elements.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private HandmadeLinkedList handmadeLinkedList = new HandmadeLinkedList();

    @Override
    public List<Task> getHistoryList() {
        return handmadeLinkedList.getTasks();
    }

    @Override
    public void add(Task task) {
        handmadeLinkedList.linkNode(task);
    }

    @Override
    public void remove(int id) {
        handmadeLinkedList.removeNode(handmadeLinkedList.nodeMap.get(id));
    }

    private class HandmadeLinkedList {
        private Map<Integer, Node> nodeMap = new HashMap<>();
        private Node head;
        private Node tail;

        private void linkNode(Task task) {
            Node node = new Node();
            node.setTask(task);
            // при наличии задачи с таким id в мапе - удали
            if (nodeMap.containsKey(task.getId())) {
                removeNode(nodeMap.get(task.getId()));
            }
            // если узел первый
            if (head == null) {
                head = node;
                tail = node;
                node.setNext(null);
                node.setPrev(null);
            } else {
                node.setPrev(tail);
                node.setNext(null);
                tail.setNext(node);
                tail = node;
            }
            nodeMap.put(task.getId(), node);
        }

        private void removeNode(Node node) {
            if (node == null) {
                return;
            }
            Node prev = node.getPrev();
            Node next = node.getNext();
            nodeMap.remove(node.getTask().getId());
            // если головной узел
            if (head == node) {
                head = next;
            }
            // если хвостовой узел
            if (tail == node) {
                tail = prev;
            }
            // если предшествующий и последующий узлы - не null, свяжи предыдущий и последующий узлы между собой
            if (prev != null) {
                prev.setNext(next);
            }
            if (next != null) {
                next.setPrev(prev);
            }
        }

        private List<Task> getTasks() {
            List<Task> tasks = new ArrayList<>();
            // начало с головы
            Node node = head;
            // пока есть следующий элемент, != null, добавляй задание из ноды в список
            while (node != null) {
                tasks.add(node.getTask());
                node = node.getNext();
            }
            return tasks;
        }

        class Node {
            private Node prev;
            private Node next;
            private Task task;

            public Node() {
            }

            public Node(Node prev, Node next, Task data) {
                this.prev = prev;
                this.next = next;
                this.task = data;
            }

            public Node getPrev() {
                return prev;
            }

            public void setPrev(Node prev) {
                this.prev = prev;
            }

            public Node getNext() {
                return next;
            }

            public void setNext(Node next) {
                this.next = next;
            }

            public Task getTask() {
                return task;
            }

            public void setTask(Task task) {
                this.task = task;
            }
        }
    }
}
