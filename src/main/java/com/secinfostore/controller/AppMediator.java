package com.secinfostore.controller;

import com.secinfostore.SecureInformationStore;
import com.secinfostore.controller.components.ErrorDialog;
import com.secinfostore.controller.interfaces.WindowMediator;
import com.secinfostore.util.DataStore;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.HashMap;
import java.util.Map;

public class AppMediator implements WindowMediator {
    private final Stage primaryStage;
    private final Map<String, String> controllerMap = new HashMap<>();

    public AppMediator(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @Override
    public void switchTo(String screenName, Object data) {
        String fxmlName = controllerMap.get(screenName);
        if (fxmlName != null) {
            try {
                FXMLLoader loader = new FXMLLoader(SecureInformationStore.class.getResource(fxmlName));
                primaryStage.setScene(new Scene(loader.load()));

                BaseController controller = loader.getController();

                controller.setMediator(this);
                controller.setupSelectedController(data);

                primaryStage.show();
            } catch (Exception e) {
                ErrorDialog.showErrorDialog(e, "FXML loading Error", String.format("There was an error loading %s", screenName));
            }
        } else {
            ErrorDialog.showErrorDialog(new Exception("No Such UI"),"FXML loading Error", "Specified UI does not exist");
        }
    }

    @Override
    public void registerFXMLName(String screenName, String fxmlName){
        controllerMap.put(screenName,fxmlName);
    }

    @Override
    public void windowMediaInfo() {
        System.out.println("Media Check");
    }
}
