package server.serializers;

import com.google.gson.*;
import task.elements.Epic;
import task.elements.Task;

import java.lang.reflect.Type;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

public class EpicSerializer implements JsonSerializer<Epic> {
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm");

    @Override
    public JsonElement serialize(Epic epic, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject jsonEpic = new JsonObject();
        jsonEpic.addProperty("id", epic.getId());
        jsonEpic.addProperty("name", epic.getName());
        jsonEpic.addProperty("description", epic.getDescription());
        jsonEpic.addProperty("status", epic.getStatus().toString());
        if (epic.getStartTime() != null) {
            jsonEpic.addProperty("start time", epic.getStartTime().format(dateTimeFormatter));
            jsonEpic.addProperty("duration", String.format("%02d:%02d",
                    epic.getDuration().toHoursPart(), epic.getDuration().toMinutesPart()));
            jsonEpic.addProperty("end time", epic.getEndTime().format(dateTimeFormatter));
        }
        JsonArray subtasks = new JsonArray();
        if (!epic.getSubtasks().isEmpty()) {
            for (Integer subtask : epic.getSubtasks()) {
                subtasks.add(subtask);
            }
            jsonEpic.add("subtasks", subtasks);
        }
        return jsonEpic;
    }
}
