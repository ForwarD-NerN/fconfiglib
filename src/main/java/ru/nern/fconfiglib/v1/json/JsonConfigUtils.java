package ru.nern.fconfiglib.v1.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class JsonConfigUtils {
    public static void move(JsonObject rawData, String from, String to) {
        String[] fromParts = from.split("\\.");
        String[] toParts = to.split("\\.");

        JsonElement objectToMove = rawData;
        String newName = "";

        //Searching for an object to move
        for (int i = 0; i < fromParts.length; i++) {
            String fromPart = fromParts[i];
            if (objectToMove.isJsonObject()) {
                JsonElement element = objectToMove.getAsJsonObject().get(fromPart);
                newName = fromPart;

                if (element == null) {
                    System.out.println("Path " + from + " is invalid. Can't find " + fromPart);
                    return;
                } else {
                    objectToMove = element;
                }
            }
        }
        JsonObject pathToMove = rawData;

        // Searching for a path to move to
        for(int i = 0; i < toParts.length; i++) {
            String toPart = toParts[i];
            if(pathToMove.isJsonObject()) {
                JsonObject pathObject = pathToMove.getAsJsonObject();
                JsonElement element = pathObject.get(toPart);

                if(i == toParts.length-1 && (element == null || element.isJsonPrimitive())) {
                    newName = toPart;
                    break;
                } else if(element == null) {
                    System.out.println(to + " is invalid. " + toPart + " doesn't exist");
                    return;
                } else if(element.isJsonObject()) {
                    pathToMove = element.getAsJsonObject();
                } else {
                    System.out.println(to + " is invalid. " + toPart + " has incompatible type");
                    return;
                }
            }
        }
        if(pathToMove.has(newName)) {
            pathToMove.remove(newName);
        }
        pathToMove.add(newName, objectToMove);
    }

    public static void createPath(JsonObject rawData, String path) {
        String[] pathParts = path.split("\\.");
        JsonObject to = rawData;

        for(String pathPart : pathParts) {
            JsonElement element = to.get(pathPart);
            if(element == null) {
                to.add(pathPart, new JsonObject());
            }else if(!element.isJsonObject()) {
                return;
            }
            to = to.get(pathPart).getAsJsonObject();
        }
    }

    public static boolean isNumber(JsonElement element) {
        return element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber();
    }
}
