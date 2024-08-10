package task.managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import server.adapters.DurationAdapter;
import server.adapters.LocalDateTimeAdapter;
import task.managers.history_manager.HistoryManager;
import task.managers.history_manager.InMemoryHistoryManager;
import task.managers.service_manager.InMemoryTaskManager;
import task.managers.service_manager.TaskManager;

import java.time.Duration;
import java.time.LocalDateTime;

public class Managers {


    public Managers() {
    }

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static Gson createGson() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
    }

}
