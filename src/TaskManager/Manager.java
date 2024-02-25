package TaskManager;

import TaskElements.Epic;
import TaskElements.Subtask;
import TaskElements.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class Manager {
    private int id = 0;
    private HashMap<Integer, Task> taskStorage = new HashMap<>();
    private HashMap<Integer, Subtask> subtaskStorage = new HashMap<>();
    private HashMap<Integer, Epic> epicStorage = new HashMap<>();

    private int generateID() {
        id++;
        return id;
    }

    private void updateEpicStatus(Epic epic) {
        Integer subtaskAmount = epic.getSubtasks().size();
        Integer newSubtasks = 0;
        Integer doneSubtasks = 0;
        if (subtaskAmount == 0) {
            epic.setStatus(Status.NEW);
        } else {
            for (Integer subtask_ID : epic.getSubtasks()) {
                Subtask subtask = subtaskStorage.get(subtask_ID);
                if (Objects.equals(subtask.getId(), subtask_ID)) {
                    if (subtask.getStatus() == Status.NEW) {
                        newSubtasks++;
                    } else if (subtask.getStatus() == Status.DONE) {
                        doneSubtasks++;
                    }
                }
            }
            if (subtaskAmount.equals(newSubtasks)) {
                epic.setStatus(Status.NEW);
            } else if (subtaskAmount.equals(doneSubtasks)) {
                epic.setStatus(Status.DONE);
            } else {
                epic.setStatus(Status.IN_PROGRESS);
            }
        }
    }

    public Task addTask(Task task) {
        task.setId(generateID());
        taskStorage.put(task.getId(), task);
        return task;
    }

    public Subtask addSubtask(Subtask subtask) {
        Epic target = epicStorage.get(subtask.getEpicID());
        if (target != null) {
            subtask.setId(generateID());
            target.addSubtask(subtask.getId());
            subtaskStorage.put(subtask.getId(), subtask);
            updateEpicStatus(target);
            return subtask;
        } else {
            System.out.println("Попытка добавить подзадание к несуществующему эпику.");
            return null;
        }
    }

    public Epic addEpic(Epic epic) {
        epic.setId(generateID());
        epicStorage.put(epic.getId(), epic);
        return epic;
    }

    public void updateTask(Task task) {
        if (taskStorage.containsKey(task.getId())) {
            taskStorage.put(task.getId(), task);
        } else {
            System.out.println("Задания с таким ID не существует.");
        }
    }

    public void updateSubtask(Subtask subtask) {
        if (subtaskStorage.containsKey(subtask.getId())) {
            Subtask target = subtaskStorage.get(subtask.getId());
            if (subtask.getEpicID() == target.getEpicID()) {
                subtaskStorage.put(subtask.getId(), subtask);
                updateEpicStatus(epicStorage.get(subtask.getEpicID()));
            } else {
                System.out.println("Подзадачи не соотносятся по ID эпика.");
            }
        } else {
            System.out.println("Подзадачи с таким ID не существует.");
        }
    }

    public void updateEpic(Epic epic) {
        if (epicStorage.containsKey(epic.getId())) {
            epicStorage.get(epic.getId()).setName(epic.getName());
            epicStorage.get(epic.getId()).setDescription(epic.getDescription());
        } else {
            System.out.println("Эпика с таким ID не существует.");
        }
    }

    public List<Task> getAllTasks() {
        return new ArrayList<>(taskStorage.values());
    }

    public List<Subtask> getAllSubTasks() {
        return new ArrayList<>(subtaskStorage.values());
    }

    public List<Epic> getAllEpicTasks() {
        return new ArrayList<>(epicStorage.values());
    }

    public List<Subtask> getCertainEpicSubtasks(Integer epic_ID) {
        List<Subtask> subtasks = new ArrayList<>();
        if (epicStorage.containsKey(epic_ID)) {
            for (Integer subtask_id : epicStorage.get(epic_ID).getSubtasks()) {
                subtasks.add(subtaskStorage.get(subtask_id));
            }
        }
        return subtasks;
    }

    public void clearAllTasks() {
        taskStorage.clear();
    }

    public void clearAllSubtasks() {
        subtaskStorage.clear();
        for (Epic epic : epicStorage.values()) {
            epic.removeAllSubtask();
            updateEpicStatus(epic);
        }
    }

    public void clearAllEpics() {
        epicStorage.clear();
        subtaskStorage.clear();
    }

    public Task findTaskByID(Integer ID) {
        if (taskStorage.containsKey(ID)) {
            return taskStorage.get(ID);
        } else {
            System.out.println("Задания с таким ID не существует.");
            return null;
        }
    }

    public Subtask findSubtaskByID(Integer ID) {
        if (subtaskStorage.containsKey(ID)) {
            return subtaskStorage.get(ID);
        } else {
            System.out.println("Подзадания с таким ID не существует.");
            return null;
        }
    }

    public Epic findEpicByID(Integer ID) {
        if (epicStorage.containsKey(ID)) {
            return epicStorage.get(ID);
        } else {
            System.out.println("Эпика с таким ID не существует.");
            return null;
        }
    }

    public void deleteTaskByID(Integer ID) {
        if (taskStorage.containsKey(ID)) {
            taskStorage.remove(ID);
        } else {
            System.out.println("Задания с таким ID не существует.");
        }
    }

    public void deleteSubtaskByID(Integer ID) {
        if (subtaskStorage.containsKey(ID)) {
            epicStorage.get(subtaskStorage.get(ID)
                            .getEpicID())
                    .removeSubtask(ID);
            updateEpicStatus(epicStorage.get(subtaskStorage.get(ID).getEpicID()));
            subtaskStorage.remove(ID);
        } else {
            System.out.println("Подзадания с таким ID не существует.");
        }
    }

    public void deleteEpicByID(Integer ID) {
        if (epicStorage.containsKey(ID)) {
            for (Integer subtask_ID : epicStorage.get(ID).getSubtasks()) {
                subtaskStorage.remove(subtask_ID);
            }
            epicStorage.remove(ID);
        } else {
            System.out.println("Эпика с таким ID не существует.");
        }
    }
}



