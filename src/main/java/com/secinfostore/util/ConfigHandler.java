package com.secinfostore.util;

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
            boolean default_passwordCharSet = rootNode.has("default_passwordCharSet");
            boolean default_pagesize = rootNode.has("default_pagesize");
            boolean default_sorting = rootNode.has("default_sorting");

            boolean fieldsCheck = default_passwordCharSet
                    && default_pagesize
                    && default_sorting;

            if(!fieldsCheck){
                createDefaultConfig(configFile);
                return getConfig();
            }

            configData.put("default_passwordCharSet", rootNode.get("default_passwordCharSet").asText());
            configData.put("default_pagesize", rootNode.get("default_pagesize").asText());
            configData.put("default_sorting", rootNode.get("default_sorting").asText());
        } catch (Exception e) {
        }
        return configData;
    }

    public static void createDefaultConfig(File configFile) {
        Map<String, String> defaultConfig = new HashMap<>();
        defaultConfig.put("default_passwordCharSet", "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()");
        defaultConfig.put("default_sorting", "Ascending");
        defaultConfig.put("default_pagesize", "10");
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
