package com.secinfostore.secureinfostore.util;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.secinfostore.secureinfostore.model.AccountObj;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class JsonHandler {

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
}
