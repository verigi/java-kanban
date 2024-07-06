package server.serializers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import task.elements.Task;

import java.lang.reflect.Type;
import java.time.format.DateTimeFormatter;

public class TaskSerializer implements JsonSerializer<Task> {
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm");

    @Override
    public JsonElement serialize(Task task, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject jsonTask = new JsonObject();
        jsonTask.addProperty("id", task.getId());
        jsonTask.addProperty("name", task.getName());
        jsonTask.addProperty("description", task.getDescription());
        jsonTask.addProperty("status", task.getStatus().toString());
        if (task.getStartTime() != null) {
            jsonTask.addProperty("start time", task.getStartTime().format(dateTimeFormatter));
            jsonTask.addProperty("duration", String.format("%02d:%02d",
                    task.getDuration().toHoursPart(), task.getDuration().toMinutesPart()));
            jsonTask.addProperty("end time", task.getEndTime().format(dateTimeFormatter));
        }
        return jsonTask;
    }
}
