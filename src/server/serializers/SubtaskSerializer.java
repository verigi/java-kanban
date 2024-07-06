package server.serializers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import task.elements.Subtask;

import java.lang.reflect.Type;
import java.time.format.DateTimeFormatter;

public class SubtaskSerializer implements JsonSerializer<Subtask> {
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm");

    @Override
    public JsonElement serialize(Subtask subtask, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject jsonSubtask = new JsonObject();
        jsonSubtask.addProperty("id", subtask.getId());
        jsonSubtask.addProperty("name", subtask.getName());
        jsonSubtask.addProperty("description", subtask.getDescription());
        jsonSubtask.addProperty("status", subtask.getStatus().toString());
        if (subtask.getStartTime() != null) {
            jsonSubtask.addProperty("start time", subtask.getStartTime().format(dateTimeFormatter));
            jsonSubtask.addProperty("duration", String.format("%02d:%02d",
                    subtask.getDuration().toHoursPart(), subtask.getDuration().toMinutesPart()));
            jsonSubtask.addProperty("end time", subtask.getEndTime().format(dateTimeFormatter));
        }
        jsonSubtask.addProperty("epic id", subtask.getEpicID());
        return jsonSubtask;
    }
}
