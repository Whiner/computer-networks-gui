package ui.main;


import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import org.donntu.databaseworker.StudentTask;

public class Controller implements Initializable {

    @FXML
    ImageView top_panel_imageView;

    @FXML
    Button generationButton;

    @FXML
    Button aboutProgramButton;

    @FXML
    ComboBox<Integer> cb_WAN_nodes_quantity;

    @FXML
    ComboBox<Integer> cb_WAN_max_rel_quantity;

    @FXML
    ComboBox<Integer> cb_WAN_rel_with_LAN_quantity;

    @FXML
    ComboBox<Integer> cb_LAN_networks_quantity;

    @FXML
    ComboBox<Integer> cb_LAN_nodes_quantity;

    @FXML
    ComboBox<Integer> cb_LAN_max_rel_quantity;

    @FXML
    Slider slider_RAM;

    @FXML
    TextField textField_RAM;

    @FXML
    Button defaultButton;

    @FXML
    Button addButton;

    @FXML
    TableView<StudentTask> table;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            new FormWorker(this).fillAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
