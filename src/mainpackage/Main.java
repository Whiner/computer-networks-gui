package mainpackage;

import databaseworker.DBConnector;
import databaseworker.DBConstants;
import databaseworker.DBWorker;
import databaseworker.StudentTask;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import nodegenerator.Topology;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("../interfaceclasses/main/forms.fxml"));
        primaryStage.setTitle("Компьютерные сети");
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);
        primaryStage.show();

        primaryStage.close();
    }


    public static void main(String[] args) throws Exception {
        launch(args);

        DBWorker dbWorker = new DBWorker(new DBConnector());
        dbWorker.toDB(new StudentTask(new Topology(), "Александр", "Шиленко", "ИС-13"));
        //pozhiloiClass.pozhiloiMetod();

        /*Desktop desktop = null;
        if (Desktop.isDesktopSupported()) {
            desktop = Desktop.getDesktop();
        }
        try {
            desktop.open(new File("E:/Projects/JavaProjects/ComputerNetworksGUI/1.png"));
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }*/
    }
}