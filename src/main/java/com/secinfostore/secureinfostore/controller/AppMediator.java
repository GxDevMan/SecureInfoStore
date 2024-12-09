package com.secinfostore.secureinfostore.controller;

import com.secinfostore.secureinfostore.SecureInformationStore;
import com.secinfostore.secureinfostore.util.DataStore;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.HashMap;
import java.util.Map;

public class AppMediator implements WindowMediator {
    private final Stage primaryStage;
    private final Map<String, BaseController> controllerMap = new HashMap<>();
    private DataStore dataStore = DataStore.getInstance();

    public AppMediator(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    //A loaded Controller from an FXML File != controller in the hashmap
    @Override
    public void switchTo(String screenName, Object data) {
        BaseController controller = controllerMap.get(screenName);
        if (controller != null) {
            try {
                FXMLLoader loader = new FXMLLoader(SecureInformationStore.class.getResource(controller.getFxmlFileName()));
                primaryStage.setScene(new Scene(loader.load()));

                primaryStage.setTitle((String) dataStore.getObject("default_title"));
                BaseController controller2 = loader.getController();

                controller2.setMediator(this);
                controller2.setupSelectedController(data);

                primaryStage.show();
            } catch (Exception e) {
                ErrorDialog.showErrorDialog(e, "FXML loading Error", String.format("There was an error loading %s", screenName));
            }
        } else {
            ErrorDialog.showErrorDialog(new Exception("No Such UI"),"FXML loading Error", "Specified UI does not exist");
        }
    }

    @Override
    public void registerController(String screenName, BaseController controller){
        controller.setMediator(this);
        controllerMap.put(screenName,controller);
    }

    @Override
    public void windowMediaInfo() {
        System.out.println("Media Check");
    }
}
