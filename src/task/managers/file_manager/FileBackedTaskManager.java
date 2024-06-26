package task.managers.file_manager;

import task.elements.Epic;
import task.elements.Subtask;
import task.elements.Task;
import task.enums.Status;
import task.enums.Type;
import task.exceptions.FileProcessingException;
import task.exceptions.TaskDetailsFormatException;
import task.managers.history_manager.HistoryManager;
import task.managers.service_manager.InMemoryTaskManager;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public static FileBackedTaskManager load(File file) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
        int updID = 0;
        try (BufferedReader reader = Files.newBufferedReader(file.toPath())) {
            reader.readLine();
            while (reader.ready()) {
                String line = reader.readLine();
                if (line.equals("")) {
                    break;
                }
                var target = CVSHandler.stringToTask(line);
                if (target.getType() == Type.TASK) {
                    fileBackedTaskManager.taskStorage.put(target.getId(), target);
                }
                if (target.getType() == Type.SUBTASK) {
                    fileBackedTaskManager.subtaskStorage.put(target.getId(), (Subtask) target);
                    if (!fileBackedTaskManager.epicStorage.isEmpty()) {
                        Epic epicLinkSub = fileBackedTaskManager.epicStorage.get(((Subtask) target).getEpicID());
                        epicLinkSub.addSubtask(target.getId());
                    }
                }
                if (target.getType() == Type.EPIC) {
                    fileBackedTaskManager.epicStorage.put(target.getId(), (Epic) target);
                }
                if (target.getId() > updID) {
                    updID = target.getId();
                }
            }
            String historyLine = reader.readLine();
            for (int id : CVSHandler.historyToLoad(historyLine)) {
                fileBackedTaskManager.addToHistory(id);
            }
            fileBackedTaskManager.id = updID;
            return fileBackedTaskManager;
        } catch (IOException e) {
            throw new FileProcessingException("Ошибка чтения файла");
        }
    }

    @Override
    public Task addTask(Task task) {
        super.addTask(task);
        save();
        return task;
    }

    @Override
    public Subtask addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
        save();
        return subtask;
    }

    @Override
    public Epic addEpic(Epic epic) {
        super.addEpic(epic);
        save();
        return epic;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void clearAllTasks() {
        super.clearAllTasks();
        save();
    }

    @Override
    public void clearAllSubtasks() {
        super.clearAllSubtasks();
        save();
    }

    @Override
    public void clearAllEpics() {
        super.clearAllEpics();
        save();
    }

    @Override
    public Task getTask(Integer id) {
        Task task = super.getTask(id);
        save();
        return task;
    }

    @Override
    public Subtask getSubtask(Integer id) {
        Subtask subtask = super.getSubtask(id);
        save();
        return subtask;
    }

    @Override
    public Epic getEpic(Integer id) {
        Epic epic = super.getEpic(id);
        save();
        return epic;
    }

    @Override
    public void deleteTaskByID(Integer id) {
        super.deleteTaskByID(id);
        save();
    }

    @Override
    public void deleteSubtaskByID(Integer id) {
        super.deleteSubtaskByID(id);
        save();
    }

    @Override
    public void deleteEpicByID(Integer id) {
        super.deleteEpicByID(id);
        save();
    }

    private void save() {
        String header = "id,type,name,status,description,start,duration,epic\n";
        CVSHandler cvsHandler = new CVSHandler();
        try (BufferedWriter writer = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8)) {
            writer.write(header);
            for (Task task : getAllTasks()) {
                writer.write(cvsHandler.taskToString(task).replaceAll(",$", "") + "\n");
            }
            for (Epic epic : getAllEpicTasks()) {
                writer.write(cvsHandler.taskToString(epic).replaceAll(",$", "") + "\n");
            }
            for (Subtask subtask : getAllSubtasks()) {
                writer.write(cvsHandler.taskToString(subtask) + "\n");
            }
            writer.write("\n");
            writer.write(cvsHandler.historyToSave(getInMemoryHistoryManager()));
        } catch (IOException e) {
            throw new FileProcessingException("Ошибка записи файла");
        }
    }

    static class CVSHandler {
        private static final String COMMA = ",";

        private String taskToString(Task task) {
            String[] taskDetails = {Integer.toString(task.getId()), task.getType().toString(), task.getName(),
                    task.getStatus().toString(), task.getDescription(), String.valueOf(task.getStartTime()),
                    String.valueOf(task.getDuration()), idPointer(task)};
            return String.join(COMMA, taskDetails);
        }

        private static Task stringToTask(String value) {
            String[] taskDetails = value.split(COMMA);
            int id = Integer.parseInt(taskDetails[0]);
            String type = taskDetails[1];
            String name = taskDetails[2];
            Status status = Status.valueOf(taskDetails[3].toUpperCase());
            String description = taskDetails[4];
            LocalDateTime startTime = LocalDateTime.parse(taskDetails[5]);
            Duration duration = Duration.parse(taskDetails[6]);
            Integer epicId = type.equals("SUBTASK") ? Integer.parseInt(taskDetails[7]) : null;
            switch (type) {
                case "TASK" -> {
                    Task task = new Task(name, description, startTime, duration);
                    task.setId(id);
                    task.setStatus(status);
                    return task;
                }
                case "SUBTASK" -> {
                    Subtask subtask = new Subtask(name, description, startTime, duration, epicId);
                    subtask.setId(id);
                    subtask.setStatus(status);
                    return subtask;
                }
                case "EPIC" -> {
                    Epic epic = new Epic(name, description);
                    epic.setId(id);
                    epic.setStatus(status);
                    epic.setStartTime(startTime);
                    epic.setDuration(duration);
                    return epic;
                }
                default -> throw new TaskDetailsFormatException("Некорректное значение поля: тип задания");
            }
        }

        private String historyToSave(HistoryManager manager) {
            List<Task> history = manager.getHistoryList();
            StringBuilder sb = new StringBuilder();
            for (Task task : history) {
                sb.append(task.getId()).append(",");
            }
            return sb.toString().replaceAll(",$", "");
        }

        private static List<Integer> historyToLoad(String historyString) {
            List<Integer> history = new ArrayList<>();
            if (historyString != null) {
                String[] ids = historyString.split(COMMA);
                for (String id : ids) {
                    history.add(Integer.parseInt(id));
                }
            }
            return history;
        }

        private static String idPointer(Task task) {
            if (task instanceof Subtask) {
                return String.valueOf(((Subtask) task).getEpicID());
            }
            return "";
        }
    }
}