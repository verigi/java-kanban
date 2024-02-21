package TaskManager;

import TaskElements.Epic;
import TaskElements.Subtask;
import TaskElements.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class Manager {
    private static int id = 0;
    private HashMap<Integer, Task> taskStorage = new HashMap<>();
    private HashMap<Integer, Subtask> subtaskStorage = new HashMap<>();
    private HashMap<Integer, Epic> epicStorage = new HashMap<>();

    public static int generateID() {
        id++;
        return id;
    }

    protected Task addTask(Task task) {
        this.taskStorage.put(task.getId(), task);
        return task;
    }

    protected Subtask addSubtask(Subtask subTask) {
        try {
            Epic target = epicStorage.get(subTask.getEpicID());
            target.getSubtasks().add(subTask.getId());
            subtaskStorage.put(subTask.getId(), subTask);
            updateEpicStatus(epicStorage.get(subTask.getEpicID()));
        } catch (NullPointerException e) {
            System.out.println("Попытка добавить подзадание к несуществующему эпику.");
            return null;
        }
        return subTask;
    }

    protected Epic addEpic(Epic epic) {
        epicStorage.put(epic.getId(), epic);
        return epic;
    }

    /*
    При обновлении любого из заданий, ID нового задания задается через setID
     */
    protected void updateTask(Task task) {
        try {
            Task oldTask = this.taskStorage.get(task.getId());
            this.taskStorage.remove(oldTask.getId());
            this.taskStorage.put(task.getId(), task);
        } catch (NullPointerException e) {
            System.out.println("Задания с таким ID не существует.");
        }
    }

    protected void updateSubtask(Subtask subtask) {
        try {
            Subtask oldSubtask = subtaskStorage.get(subtask.getId());
            subtaskStorage.remove(oldSubtask.getId());
            subtaskStorage.put(subtask.getId(), subtask);
            updateEpicStatus(epicStorage.get(subtask.getEpicID()));
        } catch (NullPointerException e) {
            System.out.println("Подзадания с таким ID не существует.");
        }
    }

    /*
     При обновлении эпика, подзадания исходного эпика перейдут в новый
     */
    protected void updateEpic(Epic epic) {
        try {
            Epic oldEpic = epicStorage.get(epic.getId());
            ArrayList<Integer> oldEpicSubs = new ArrayList<>(oldEpic.getSubtasks());
            epic.setSubtasks(oldEpicSubs);
            epicStorage.remove(oldEpic.getId());
            epicStorage.put(epic.getId(), epic);
            updateEpicStatus(epic);
        } catch (NullPointerException e) {
            System.out.println("Эпика с таким ID не существует.");
        }
    }

    protected void updateEpicStatus(Epic epic) {
        Integer subtaskAmount = epic.getSubtasks().size();
        Integer newSubtasks = 0;
        Integer doneSubtasks = 0;
        if (subtaskAmount == 0) {
            epic.setStatus(Status.NEW);
        } else {
            for (Integer subtask_ID : epic.getSubtasks()) {
                for (Subtask subtask : subtaskStorage.values()) {
                    if (Objects.equals(subtask.getId(), subtask_ID)) {
                        if (subtask.getStatus() == Status.NEW) {
                            newSubtasks++;
                        } else if (subtask.getStatus() == Status.DONE) {
                            doneSubtasks++;
                        }
                    }
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

    protected List<Task> getAllTasks() {
        return new ArrayList<>(taskStorage.values());
    }

    protected List<Subtask> getAllSubTasks() {
        return new ArrayList<>(subtaskStorage.values());
    }

    protected List<Epic> getAllEpicTasks() {
        return new ArrayList<>(epicStorage.values());
    }

    protected List<Subtask> getCertainEpicSubtasks(int epic_id) {
        List<Subtask> subtasks = new ArrayList<>();
        if (epicStorage.containsKey(epic_id)) {
            for (Integer subtask_id : epicStorage.get(epic_id).getSubtasks()) {
                subtasks.add(subtaskStorage.get(subtask_id));
            }
        } else {
            subtasks = null;
            System.out.println("Эпика с таким ID не существует.");
        }
        return subtasks;
    }

    protected void clearAllTasks() {
        taskStorage.clear();
    }

    /*
    При удалении всех подзаданий из хранилища, происходит удаление и из ArrayList
     */
    protected void clearAllSubtasks() {
        subtaskStorage.clear();
        for (Epic epic : epicStorage.values()) {
            epic.setSubtasks(new ArrayList<>());
            updateEpicStatus(epic);
        }
    }

    /*
    При удалении всех эпиков, происходит удаление и всех подзаданий
     */
    protected void clearAllEpics() {
        epicStorage.clear();
        clearAllSubtasks();
    }

    /*
    Производится поочередный поиск по всем хранилищам
     */
    protected Object findTaskElementByID(Integer id) {
        Object o = null;
        if (!taskStorage.isEmpty()) {
            for (Integer key : taskStorage.keySet()) {
                if (key.equals(id)) {
                    o = taskStorage.get(key);
                }
            }
        }
        if (!subtaskStorage.isEmpty()) {
            for (Integer key : subtaskStorage.keySet()) {
                if (key.equals(id)) {
                    o = subtaskStorage.get(key);
                }
            }
        }
        if (!epicStorage.isEmpty()) {
            for (Integer key : epicStorage.keySet()) {
                if (key.equals(id)) {
                    o = epicStorage.get(key);
                }
            }
        }
        return o;
    }

    /*
    При удалении задания по ID происходит удаление из хранилища;
    При удалении эпика по ID, удаляются и все его подзадания.
    При удалении подзадания по ID, оно удаляется и из хранилища, и из ArrayList эпика
     */
    protected void deleteElementByID(Integer id) {
        if (taskStorage.containsKey(id)) {
            taskStorage.remove(id);
        } else if (subtaskStorage.containsKey(id)) {
            epicStorage.get(subtaskStorage.get(id)
                            .getEpicID())
                            .getSubtasks()
                            .remove(id);
            subtaskStorage.remove(id);
        } else if (epicStorage.containsKey(id)) {
            for (Integer subtask_id : epicStorage.get(id).getSubtasks()) {
                subtaskStorage.remove(subtask_id);
            }
            epicStorage.remove(id);
        } else {
            System.out.println("Введен несуществующий ID.");
        }
    }
}



