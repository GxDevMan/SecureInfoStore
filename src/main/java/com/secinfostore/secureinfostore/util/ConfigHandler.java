package com.secinfostore.secureinfostore.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ConfigHandler {

    public static Map<String, String> getConfig() {
        String currentDir = System.getProperty("user.dir");
        String filePath = currentDir + File.separator + "config.json";
        File configFile = new File(filePath);

        if (!configFile.exists()) {
            createDefaultConfig(configFile);
            return getConfig();
        }

        Map<String, String> configData = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode rootNode = mapper.readTree(configFile);
            boolean default_db = rootNode.has("default_db");
            boolean default_passwordCharSet = rootNode.has("default_passwordCharSet");

            boolean fieldsCheck = default_db && default_passwordCharSet;

            if(!fieldsCheck){
                createDefaultConfig(configFile);
                return getConfig();
            }
            configData.put("default_db", rootNode.get("default_db").asText());
            configData.put("default_passwordCharSet", rootNode.get("default_passwordCharSet").asText());
        } catch (Exception e) {
        }
        return configData;
    }

    public static void createDefaultConfig(File configFile) {
        Map<String, String> defaultConfig = new HashMap<>();
        defaultConfig.put("default_db", "informationStore.db");
        defaultConfig.put("default_passwordCharSet", "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()");
        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
        try {
            writer.writeValue(configFile, defaultConfig);
        } catch (Exception e) {
        }
    }

    public static void createCustomConfigFile(Map<String, String> customConfig) {
        String currentDir = System.getProperty("user.dir");
        String filePath = currentDir + File.separator + "config.json";
        File configFile = new File(filePath);
        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
        try {
            writer.writeValue(configFile, customConfig);
        } catch (IOException e) {
        }
    }
}
