package ru.dfhub.enigmaircmobile;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

/**
 * Class for working with config
 */
public class Config {

    /**
     * Get config (access method)
     * @return Config
     */
    public static JSONObject getConfig() {
        try {
            return new JSONObject(readConfig());
        } catch (Exception e) {
            return new JSONObject();
        }
    }

    /**
     * Get config from /Android/.../config.json file
     * @return String config
     */
    private static String readConfig() throws Exception {
        List<String> lines = Files.readAllLines(new File(MainActivity.FILES_DIR, "config.json").toPath());
        StringBuilder sb = new StringBuilder();
        lines.forEach(sb::append);
        return sb.toString();
    }

    /**
     * Save new config to /Android/.../config.json file
     * @param newConfig Config
     */
    public static void saveConfig(JSONObject newConfig, Context context) throws Exception {
        Files.write(
                new File(MainActivity.FILES_DIR, "config.json").toPath(),
                newConfig.toString().getBytes(StandardCharsets.UTF_8)
        );
    }

}