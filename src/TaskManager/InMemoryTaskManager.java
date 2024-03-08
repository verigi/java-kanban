package TaskManager;

import TaskElements.Epic;
import TaskElements.Subtask;
import TaskElements.Task;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private int id = 0;
    private Map<Integer, Task> taskStorage = new HashMap<>();
    private Map<Integer, Subtask> subtaskStorage = new HashMap<>();
    private Map<Integer, Epic> epicStorage = new HashMap<>();
    private HistoryManager inMemoryHistoryManager = Managers.getDefaultHistory();

    @Override
    public Task addTask(Task task) {
        task.setId(generateID());
        taskStorage.put(task.getId(), task);
        return task;
    }

    @Override
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

    @Override
    public Epic addEpic(Epic epic) {
        epic.setId(generateID());
        epicStorage.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public void updateTask(Task task) {
        if (taskStorage.containsKey(task.getId())) {
            taskStorage.put(task.getId(), task);
        } else {
            System.out.println("Задания с таким ID не существует.");
        }
    }

    @Override
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

    @Override
    public void updateEpic(Epic epic) {
        if (epicStorage.containsKey(epic.getId())) {
            epicStorage.get(epic.getId()).setName(epic.getName());
            epicStorage.get(epic.getId()).setDescription(epic.getDescription());
        } else {
            System.out.println("Эпика с таким ID не существует.");
        }
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(taskStorage.values());
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtaskStorage.values());
    }

    @Override
    public List<Epic> getAllEpicTasks() {
        return new ArrayList<>(epicStorage.values());
    }

    @Override
    public List<Subtask> getCertainEpicSubtasks(Integer epic_ID) {
        List<Subtask> subtasks = new ArrayList<>();
        if (epicStorage.containsKey(epic_ID)) {
            for (Integer subtask_id : epicStorage.get(epic_ID).getSubtasks()) {
                subtasks.add(subtaskStorage.get(subtask_id));
            }
        }
        return subtasks;
    }

    @Override
    public void clearAllTasks() {
        taskStorage.clear();
    }

    @Override
    public void clearAllSubtasks() {
        subtaskStorage.clear();
        for (Epic epic : epicStorage.values()) {
            epic.removeAllSubtask();
            updateEpicStatus(epic);
        }
    }

    @Override
    public void clearAllEpics() {
        epicStorage.clear();
        subtaskStorage.clear();
    }

    @Override
    public Task getTask(Integer ID) {
        if (taskStorage.containsKey(ID)) {
            inMemoryHistoryManager.add(taskStorage.get(ID));
            return taskStorage.get(ID);
        } else {
            System.out.println("Задания с таким ID не существует.");
            return null;
        }
    }

    @Override
    public Subtask getSubtask(Integer ID) {
        if (subtaskStorage.containsKey(ID)) {
            inMemoryHistoryManager.add(subtaskStorage.get(ID));
            return subtaskStorage.get(ID);
        } else {
            System.out.println("Подзадания с таким ID не существует.");
            return null;
        }
    }

    @Override
    public Epic getEpic(Integer ID) {
        if (epicStorage.containsKey(ID)) {
            inMemoryHistoryManager.add(epicStorage.get(ID));
            return epicStorage.get(ID);
        } else {
            System.out.println("Эпика с таким ID не существует.");
            return null;
        }
    }

    @Override
    public List<Task> getHistory() {
        return inMemoryHistoryManager.getHistoryList();
    }

    @Override
    public void deleteTaskByID(Integer ID) {
        if (taskStorage.containsKey(ID)) {
            taskStorage.remove(ID);
        } else {
            System.out.println("Задания с таким ID не существует.");
        }
    }

    @Override
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

    @Override
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

    //внутренний метод класса
    private int generateID() {
        id++;
        return id;
    }

    //внутренний метод класса
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
}



