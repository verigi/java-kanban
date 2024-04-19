package task.managers.history_manager;

import task.elements.Epic;
import task.elements.Subtask;
import task.elements.Task;
import task.enums.Status;
import task.managers.Managers;
import task.managers.service_manager.TaskManager;

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
        handmadeLinkedList.removeNode(id);
    }

    @Override
    public void removeTaskType(List<Task> taskTypeList) {
        if (taskTypeList != null) {
            for (Task task : taskTypeList) {
                handmadeLinkedList.removeNode(task.getId());
            }
        }
    }

    private class HandmadeLinkedList {
        private Map<Integer, Node> nodeMap = new HashMap<>();
        private Node head;
        private Node tail;

        private void linkNode(Task task) {
            if (nodeMap.containsKey(task.getId())) {
                removeNode(task.getId());
            }
            // переиграл реализацию: через переменную и ссылку на старый хвост
            Node oldTail = tail;
            Node node = new Node(oldTail, null, task);
            tail = node;
            if (oldTail == null) {
                head = node;
            } else {
                oldTail.next = node;
            }
            nodeMap.put(task.getId(), node);
        }

        private void removeNode(Integer id) {
            if (id != null && nodeMap.containsKey(id)) {
                Node target = nodeMap.remove(id);
                Node prev = target.getPrev();
                Node next = target.getNext();
                // если головной узел
                if (head == target) {
                    head.setPrev(null);
                    head = next;
                }
                // если хвостовой узел
                if (tail == target) {
                    tail.setNext(null);
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
