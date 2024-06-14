package task.managers.service_manager;

import task.elements.Epic;
import task.elements.Subtask;
import task.elements.Task;
import task.enums.Type;
import task.exceptions.TaskDetailsFormatException;
import task.managers.Managers;
import task.enums.Status;
import task.managers.history_manager.HistoryManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected int id = 0;
    protected Map<Integer, Task> taskStorage = new HashMap<>();
    protected Map<Integer, Subtask> subtaskStorage = new HashMap<>();
    protected Map<Integer, Epic> epicStorage = new HashMap<>();
    protected Set<Task> prioritizedStorage = new TreeSet<>();
    protected HistoryManager inMemoryHistoryManager = Managers.getDefaultHistory();

    @Override
    public Task addTask(Task task) {
        task.setId(generateID());
        taskStorage.put(task.getId(), task);
        addToPrioritized(task);
        return task;
    }

    @Override
    public Subtask addSubtask(Subtask subtask) {
        Epic target = epicStorage.get(subtask.getEpicID());
        if (target != null) {
            subtask.setId(generateID());
            target.addSubtask(subtask.getId());
            subtaskStorage.put(subtask.getId(), subtask);
            addToPrioritized(subtask);
            updateEpicStatus(target);
            updateEpicDateTime(target);
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
    public void addToPrioritized(Task task) {
        if (!hasIntersections(task) && hasTimeDetails(task)) {
            prioritizedStorage.add(task);
        } else {
            if (hasIntersections(task)) {
                throw new TaskDetailsFormatException("У задачи имеются пересечения по времени: " + task.getName());
            }
            if (!hasTimeDetails(task)) {
                throw new TaskDetailsFormatException("Нет данных по времени начала и/или длительности задачи - " +
                        task.getName());
            }
        }
    }

    @Override
    public void updateTask(Task task) {
        if (taskStorage.containsKey(task.getId())) {
            taskStorage.put(task.getId(), task);
            prioritizedStorage.removeIf(x -> x.getId() == task.getId());
            prioritizedStorage.add(task);
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
                prioritizedStorage.removeIf(x -> x.getId() == subtask.getId());
                addToPrioritized(subtask);
                updateEpicStatus(epicStorage.get(subtask.getEpicID()));
                updateEpicDateTime(epicStorage.get(subtask.getEpicID()));
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
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedStorage);
    }

    @Override
    public List<Subtask> getCertainEpicSubtasks(Integer epicID) {
        List<Subtask> subtasks = subtaskStorage.values().stream()
                .filter(x -> x.getEpicID() == epicID)
                .collect(Collectors.toList());
        return subtasks;
    }

    @Override
    public void clearAllTasks() {
        List<Task> tasksList = new ArrayList<>(taskStorage.values());
        inMemoryHistoryManager.removeTaskType(tasksList);
        taskStorage.clear();
        prioritizedStorage.removeIf(x -> x.getType().equals(Type.TASK));
    }

    @Override
    public void clearAllSubtasks() {
        List<Task> subtasksList = new ArrayList<>(subtaskStorage.values());
        inMemoryHistoryManager.removeTaskType(subtasksList);
        epicStorage = epicStorage.entrySet().stream().map(epicEntry -> {
            epicEntry.getValue().removeAllSubtask();
            updateEpicStatus(epicEntry.getValue());
            return epicEntry;
        }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        prioritizedStorage.removeIf(x -> x.getType().equals(Type.SUBTASK));
        subtaskStorage.clear();
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
        Optional<Task> optionalTask = taskStorage.entrySet().stream()
                .filter(x -> x.getKey() == id)
                .map(Map.Entry::getValue)
                .findFirst();
        if (optionalTask.isPresent()) inMemoryHistoryManager.add(optionalTask.get());
        return optionalTask.orElseThrow();
    }

    @Override
    public Subtask getSubtask(Integer id) {
        Optional<Subtask> optionalSubtask = subtaskStorage.entrySet().stream()
                .filter(x -> x.getKey() == id)
                .map(Map.Entry::getValue)
                .findFirst();
        if (optionalSubtask.isPresent()) inMemoryHistoryManager.add(optionalSubtask.get());
        return optionalSubtask.orElseThrow();
    }

    @Override
    public Epic getEpic(Integer id) {
        Optional<Epic> optionalEpic = epicStorage.entrySet().stream()
                .filter(x -> x.getKey() == id)
                .map(Map.Entry::getValue)
                .findFirst();
        if (optionalEpic.isPresent()) inMemoryHistoryManager.add(optionalEpic.get());
        return optionalEpic.orElseThrow();
    }

    @Override
    public List<Task> getHistory() {
        return inMemoryHistoryManager.getHistoryList();
    }

    @Override
    public void deleteTaskByID(Integer id) {
        if (taskStorage.containsKey(id)) {
            inMemoryHistoryManager.remove(id);
            prioritizedStorage.remove(taskStorage.remove(id));
        } else {
            System.out.println("Задания с таким id не существует.");
        }
    }

    @Override
    public void deleteSubtaskByID(Integer id) {
        if (subtaskStorage.containsKey(id)) {
            Epic epic = epicStorage.get(subtaskStorage.get(id).getEpicID());
            epic.removeSubtask(id);
            prioritizedStorage.remove(subtaskStorage.get(id));
            subtaskStorage.remove(id);
            updateEpicStatus(epic);
            updateEpicDateTime(epic);
        } else {
            System.out.println("Подзадания с таким ID не существует.");
        }
    }

    @Override
    public void deleteEpicByID(Integer id) {
        if (epicStorage.containsKey(id)) {
            for (Integer subtaskID : epicStorage.get(id).getSubtasks()) {
                inMemoryHistoryManager.remove(subtaskID);
                prioritizedStorage.remove(subtaskStorage.get(subtaskID));
                subtaskStorage.remove(subtaskID);
            }
            inMemoryHistoryManager.remove(id);
            prioritizedStorage.remove(epicStorage.remove(id));
        } else {
            System.out.println("Эпика с таким id не существует.");
        }
    }

    protected HistoryManager getInMemoryHistoryManager() {
        return inMemoryHistoryManager;
    }

    protected void addToHistory(int id) {
        if (epicStorage.containsKey(id)) {
            inMemoryHistoryManager.add(epicStorage.get(id));
        } else if (subtaskStorage.containsKey(id)) {
            inMemoryHistoryManager.add(subtaskStorage.get(id));
        } else if (taskStorage.containsKey(id)) {
            inMemoryHistoryManager.add(taskStorage.get(id));
        }
    }

    protected void updateEpicStatus(Epic epic) {
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

    protected void updateEpicDateTime(Epic epic) {
        epic.setStartTime(calculateEpicStartTime(epic));
        epic.setEndTime(calculateEpicEndTime(epic));
        epic.setDuration(calculateEpicDuration(epic));
    }

    private int generateID() {
        id++;
        return id;
    }

    private boolean hasIntersections(Task task) {
        boolean hasIntersections = false;
        if (hasTimeDetails(task)) {
            LocalDateTime taskStartTime = task.getStartTime();
            LocalDateTime taskEndTime = task.getEndTime();
            for (var verifying : prioritizedStorage) {
                if (verifying.getStartTime() == null) {
                    continue;
                }
                LocalDateTime verifyingStartTime = verifying.getStartTime();
                LocalDateTime verifyingEndTime = verifying.getEndTime();
                if ((taskStartTime.isAfter(verifyingStartTime) && taskStartTime.isBefore(verifyingEndTime)) ||
                        (taskEndTime.isAfter(verifyingStartTime) && taskEndTime.isBefore(verifyingEndTime)) ||
                        taskStartTime.isEqual(verifyingStartTime) && taskEndTime.isEqual(verifyingEndTime)) {
                    hasIntersections = true;
                }
            }
        }
        return hasIntersections;
    }

    private boolean hasTimeDetails(Task task) {
        boolean hasTimeDetails = (task.getStartTime() != null) && (task.getDuration() != null);
        return hasTimeDetails;
    }

    private LocalDateTime calculateEpicStartTime(Epic epic) {
        LocalDateTime epicStartTime = getCertainEpicSubtasks(epic.getId()).stream()
                .map(x -> x.getStartTime())
                .min(Comparator.naturalOrder())
                .orElseThrow(() -> new TaskDetailsFormatException("Не обнаружены подзадачи эпика"));
        return epicStartTime;
    }

    private LocalDateTime calculateEpicEndTime(Epic epic) {
        LocalDateTime epicEndTime = getCertainEpicSubtasks(epic.getId()).stream()
                .map(x -> x.getEndTime())
                .max(Comparator.naturalOrder())
                .orElseThrow(() -> new TaskDetailsFormatException("Не обнаружены подзадачи эпика"));
        return epicEndTime;
    }

    private Duration calculateEpicDuration(Epic epic) {
        Duration epicDuration = Duration.between(calculateEpicStartTime(epic), calculateEpicEndTime(epic));
        return epicDuration;
    }
}



