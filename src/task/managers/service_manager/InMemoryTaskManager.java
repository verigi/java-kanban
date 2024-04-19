package task.managers.service_manager;

import task.elements.Epic;
import task.elements.Subtask;
import task.elements.Task;
import task.managers.Managers;
import task.enums.Status;
import task.managers.history_manager.HistoryManager;

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
    public List<Subtask> getCertainEpicSubtasks(Integer epicID) {
        List<Subtask> subtasks = new ArrayList<>();
        if (epicStorage.containsKey(epicID)) {
            for (Integer subtaskID : epicStorage.get(epicID).getSubtasks()) {
                subtasks.add(subtaskStorage.get(subtaskID));
            }
        }
        return subtasks;
    }

    @Override
    public void clearAllTasks() {
        List<Task> tasksList = new ArrayList<>(taskStorage.values());
        inMemoryHistoryManager.removeTaskType(tasksList);
        taskStorage.clear();
    }

    @Override
    public void clearAllSubtasks() {
        List<Task> subtasksList = new ArrayList<>(subtaskStorage.values());
        inMemoryHistoryManager.removeTaskType(subtasksList);
        subtaskStorage.clear();
        for (Epic epic : epicStorage.values()) {
            epic.removeAllSubtask();
            updateEpicStatus(epic);
        }
    }

    @Override
    public void clearAllEpics() {
        List<Task> epicList = new ArrayList<>(epicStorage.values());
        List<Task> subtasksList = new ArrayList<>(subtaskStorage.values());
        inMemoryHistoryManager.removeTaskType(epicList);
        inMemoryHistoryManager.removeTaskType(subtasksList);
        epicStorage.clear();
        subtaskStorage.clear();
    }

    @Override
    public Task getTask(Integer id) {
        if (taskStorage.containsKey(id)) {
            inMemoryHistoryManager.add(taskStorage.get(id));
            return taskStorage.get(id);
        } else {
            System.out.println("Задания с таким ID не существует.");
            return null;
        }
    }

    @Override
    public Subtask getSubtask(Integer id) {
        if (subtaskStorage.containsKey(id)) {
            inMemoryHistoryManager.add(subtaskStorage.get(id));
            return subtaskStorage.get(id);
        } else {
            System.out.println("Подзадания с таким ID не существует.");
            return null;
        }
    }

    @Override
    public Epic getEpic(Integer id) {
        if (epicStorage.containsKey(id)) {
            inMemoryHistoryManager.add(epicStorage.get(id));
            return epicStorage.get(id);
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
    public void deleteTaskByID(Integer id) {
        if (taskStorage.containsKey(id)) {
            inMemoryHistoryManager.remove(id);
            taskStorage.remove(id);
        } else {
            System.out.println("Задания с таким id не существует.");
        }
    }

    @Override
    public void deleteSubtaskByID(Integer id) {
        if (subtaskStorage.containsKey(id)) {
            epicStorage.get(subtaskStorage.get(id)
                            .getEpicID())
                    .removeSubtask(id);
            updateEpicStatus(epicStorage.get(subtaskStorage.get(id).getEpicID()));
            inMemoryHistoryManager.remove(id);
            subtaskStorage.remove(id);
        } else {
            System.out.println("Подзадания с таким ID не существует.");
        }
    }

    @Override
    public void deleteEpicByID(Integer id) {
        if (epicStorage.containsKey(id)) {
            for (Integer subtaskID : epicStorage.get(id).getSubtasks()) {
                inMemoryHistoryManager.remove(subtaskID);
                subtaskStorage.remove(subtaskID);
            }
            inMemoryHistoryManager.remove(id);
            epicStorage.remove(id);
        } else {
            System.out.println("Эпика с таким id не существует.");
        }
    }

    public HistoryManager getInMemoryHistoryManager() {
        return inMemoryHistoryManager;
    }

    public void addToHistory(int id) {
        if (epicStorage.containsKey(id)) {
            inMemoryHistoryManager.add(epicStorage.get(id));
        } else if (subtaskStorage.containsKey(id)) {
            inMemoryHistoryManager.add(subtaskStorage.get(id));
        } else if (taskStorage.containsKey(id)) {
            inMemoryHistoryManager.add(taskStorage.get(id));
        }
    }

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
            for (Integer subtaskID : epic.getSubtasks()) {
                Subtask subtask = subtaskStorage.get(subtaskID);
                if (Objects.equals(subtask.getId(), subtaskID)) {
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



