package ru.nern.config.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import ru.nern.config.ConfigFixer;

public class JsonConfigUtils {

    public static void move(JsonObject rawData, String from, String to) {
        String[] fromParts = from.split("\\.");
        String[] toParts = to.split("\\.");

        JsonObject current = rawData;
        JsonElement elementToMove = null;
        JsonObject parentObject = null;

        for (int i = 0; i < fromParts.length; i++) {
            if (current.has(fromParts[i])) {
                if (i == fromParts.length - 1) {
                    elementToMove = current.get(fromParts[i]);
                } else {
                    parentObject = current;
                    current = current.getAsJsonObject(fromParts[i]);
                }
            } else {
                System.out.println("[JsonConfigUtils.move()] Path not found " + from);
                return;
            }
        }

        if (elementToMove == null) {
            System.out.println("[JsonConfigUtils.move()] Element to move not found");
            return;
        }

        // Renaming the object if necessary
        if (toParts.length > 0) {
            String newName = toParts[toParts.length - 1];
            JsonObject newParent = rawData;

            // Finding the object that we need to move into
            for (int i = 0; i < toParts.length - 1; i++) {
                if (!newParent.has(toParts[i])) {
                    newParent.add(toParts[i], new JsonObject());
                }
                newParent = newParent.getAsJsonObject(toParts[i]);
            }

            // If such an object already exists, removing it
            if (newParent.has(newName)) {
                newParent.remove(newName);
            }

            // Adding a new element
            newParent.add(newName, elementToMove);
        } else {
            // If there is no name, we just move it
            parentObject.remove(fromParts[fromParts.length - 1]);
            current.add(fromParts[fromParts.length - 1], elementToMove);
        }
    }
}
