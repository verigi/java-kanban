package task.managers.file_manager;

import task.elements.Epic;
import task.elements.Subtask;
import task.elements.Task;
import task.enums.Status;
import task.exceptions.FileProcessingException;
import task.exceptions.TaskDetailsFormatException;
import task.managers.history_manager.HistoryManager;
import task.managers.service_manager.InMemoryTaskManager;
import task.managers.service_manager.TaskManager;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {
    private final Path pathToSave = Paths.get("src/resources/save.csv");
    File load;

    //немного отошел от задания,
    public FileBackedTaskManager() {
    }

    private void save() {
        String header = "id,type,name,status,description,epic\n";
        try (BufferedWriter writer = Files.newBufferedWriter(pathToSave, StandardCharsets.UTF_8)) {
            writer.write(header);
            for (Task task : getAllTasks()) {
                writer.write(CVSHandler.taskToString(task).replaceAll(",$", "") + "\n");
            }
            for (Epic epic : getAllEpicTasks()) {
                writer.write(CVSHandler.taskToString(epic).replaceAll(",$", "") + "\n");
            }
            for (Subtask subtask : getAllSubtasks()) {
                writer.write(CVSHandler.taskToString(subtask) + "\n");
            }
            writer.write("\n");
            writer.write(CVSHandler.historyToSave(getInMemoryHistoryManager()));
        } catch (IOException e) {
            throw new FileProcessingException("Ошибка записи файла");
        }
    }

    public FileBackedTaskManager load(File file) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager();
        try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            reader.readLine();
            while (reader.ready()) {
                String line = reader.readLine();
                if (line.equals("")) {
                    break;
                }
                var target = CVSHandler.stringToTask(line);
                if (target instanceof Epic epic) {
                    super.addEpic(epic);
                } else if (target instanceof Subtask subtask) {
                    super.addSubtask(subtask);
                } else if (target instanceof Task) {
                    super.addTask(target);
                }
            }
            String historyLine = reader.readLine();
            for (Integer id : CVSHandler.historyToLoad(historyLine)) {
                addToHistory(id);
            }
        } catch (IOException e) {
            throw new FileProcessingException("Ошибка записи файла");
        }
        return fileBackedTaskManager;
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
    public List<Epic> getAllEpicTasks() {
        return super.getAllEpicTasks();
    }

    @Override
    public List<Task> getAllTasks() {
        return super.getAllTasks();
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return super.getAllSubtasks();
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

    class CVSHandler {
        private static final String COMMA = ",";

        public static String taskToString(Task task) {
            String[] taskDetails = {Integer.toString(task.getId()), task.getType().toString(), task.getName(),
                    task.getStatus().toString(), task.getDescription(), idPointer(task)};
            return String.join(COMMA, taskDetails);
        }

        public static Task stringToTask(String value) {
            String[] taskDetails = value.split(COMMA);
            int id = Integer.parseInt(taskDetails[0]);
            String type = taskDetails[1];
            String name = taskDetails[2];
            Status status = Status.valueOf(taskDetails[3].toUpperCase());
            String description = taskDetails[4];
            Integer epicId = type.equals("SUBTASK") ? Integer.parseInt(taskDetails[5]) : null;
            switch (type) {
                case "TASK":
                    Task task = new Task(name, description, status);
                    task.setId(id);
                    return task;

                case "SUBTASK":
                    Subtask subtask = new Subtask(name, description, status, epicId);
                    subtask.setId(id);
                    return subtask;

                case "EPIC":
                    Epic epic = new Epic(name, description, status);
                    epic.setId(id);
                    return epic;

                default:
                    throw new TaskDetailsFormatException("Некорректное значение поля: тип задания");
            }
        }

        private static String historyToSave(HistoryManager manager) {
            List<Task> history = manager.getHistoryList();
            StringBuilder sb = new StringBuilder();
            for (Task task : history) {
                sb.append(task.getId() + ",");
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
