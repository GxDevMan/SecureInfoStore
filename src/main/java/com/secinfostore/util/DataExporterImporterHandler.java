package com.secinfostore.util;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.secinfostore.model.AccountObj;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

public class DataExporterImporterHandler {

    public static void writeToJsonToFile(List<AccountObj> accountsToExport, File file) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(MapperFeature.DEFAULT_VIEW_INCLUSION);
        mapper.writerWithView(Views.Public.class)
                .withDefaultPrettyPrinter()
                .writeValue(file, accountsToExport);
    }

    public static Optional<List<AccountObj>> getAccountsFromJson(File file) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        List<AccountObj> accountObjList = mapper.readValue(file,mapper.getTypeFactory().constructCollectionType(List.class, AccountObj.class));
        return Optional.ofNullable(accountObjList);
    }

    public static void writeAccountsToTextFile(List<AccountObj> accounts, File file) throws IOException {
        try (FileWriter writer = new FileWriter(file)) {
            for (AccountObj account : accounts) {
                StringBuilder stringBuilder = new StringBuilder();

                for (Field field : AccountObj.class.getDeclaredFields()) {
                    if (field.isAnnotationPresent(JsonView.class)) {
                        JsonView view = field.getAnnotation(JsonView.class);
                        if (view.value()[0] == Views.Public.class) {
                            field.setAccessible(true);
                            Object value = field.get(account);
                            stringBuilder.append(field.getName())
                                    .append(" : ")
                                    .append(value != null ? value.toString() : "null")
                                    .append(System.lineSeparator());
                        }
                    }
                }
                writer.write(stringBuilder.toString());
                writer.write(System.lineSeparator());
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
