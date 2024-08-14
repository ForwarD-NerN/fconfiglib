package ru.nern.fconfiglib.v1.config.json;

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
                    //If a path doesn't exist, creating it
                    pathObject.add(toPart, new JsonObject());
                    pathToMove = pathObject.get(toPart).getAsJsonObject();
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

    public static boolean isNumber(JsonElement element) {
        return element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber();
    }
}
