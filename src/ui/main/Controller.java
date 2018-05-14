package ui.main;


import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mainpackage.Main;
import org.donntu.GregorianCalendar;
import org.donntu.databaseworker.DBWorker;
import org.donntu.databaseworker.Student;
import org.donntu.databaseworker.StudentTask;
import ui.MessageBox;

public class Controller implements Initializable {

    @FXML
    ImageView top_panel_imageView;

    @FXML
    Button generationButton;

    @FXML
    Button searchButton;

    @FXML
    TextField searchTextBox;

    @FXML
    Button aboutProgramButton;

    @FXML
    Button addButton;

    @FXML
    TableView<StudentTask> taskTableView;

    @FXML
    ListView<String> groupListView;

    @FXML
    TableView<Student> studentsTableView;

    private void createTaskTable(){
        if(taskTableView.getColumns().size() == 0) {
            taskTableView.getColumns().clear();

            TableColumn<StudentTask, String> surname = new TableColumn<>("Фамилия");
            TableColumn<StudentTask, String> name = new TableColumn<>("Имя");
            TableColumn<StudentTask, String> group = new TableColumn<>("Группа");
            TableColumn<StudentTask, GregorianCalendar> date = new TableColumn<>("Дата создания");

            surname.setCellValueFactory(new PropertyValueFactory<>("surname"));
            name.setCellValueFactory(new PropertyValueFactory<>("name"));
            group.setCellValueFactory(new PropertyValueFactory<>("group"));
            date.setCellValueFactory(new PropertyValueFactory<>("creationDate"));

            surname.setMinWidth(200);
            name.setMinWidth(200);
            group.setMinWidth(200);
            date.setMinWidth(200);

            taskTableView.getColumns().addAll(group, surname, name, date);
        }
    }

    private void createStudentsTable(){
        if(studentsTableView.getColumns().size() == 0) {
            studentsTableView.getColumns().clear();

            TableColumn<Student, String> surname = new TableColumn<>("Фамилия");
            TableColumn<Student, String> name = new TableColumn<>("Имя");

            surname.setCellValueFactory(new PropertyValueFactory<>("surname"));
            name.setCellValueFactory(new PropertyValueFactory<>("name"));

            surname.setMinWidth(200);
            name.setMinWidth(200);

            studentsTableView.getColumns().addAll(surname, name);
        }
    }

    private void refreshDataOnTaskTable() throws SQLException {
        taskTableView.getItems().clear();
        ObservableList<StudentTask> tableTaskStruct = FXCollections.observableArrayList();
        final List<HashMap<String, String>> students = DBWorker.getStudentsTaskList();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date tempDate;
        GregorianCalendar tempCalendar;
        for (HashMap<String, String> record: students){
            try {
                tempDate = dateFormat.parse(record.get("Дата"));
            } catch (ParseException e) {
                System.out.println("Date parse error");
                continue;
            }
            tempCalendar = new GregorianCalendar();
            tempCalendar.setTime(tempDate);
            StudentTask task = new StudentTask(
                    tempCalendar,
                    record.get("Имя"),
                    record.get("Фамилия"),
                    record.get("Группа"));
            task.setKey(Integer.valueOf(record.get("Ключ")));
            tableTaskStruct.add(task);
        }
        taskTableView.setItems(tableTaskStruct);
    } //сомнительно каждый раз по новой загружать в таблицу

    private void refreshDataOnGroupTable() throws SQLException {
        groupListView.getItems().clear();
        ObservableList<String> tableTaskStruct = FXCollections.observableArrayList();
        final List<String> groups = DBWorker.getGroups();
        tableTaskStruct.addAll(groups);
        groupListView.setItems(tableTaskStruct);
    }

    private void refreshDataOnStudentsTable(String group) throws SQLException {
        studentsTableView.getItems().clear();
        ObservableList<Student> tableTaskStruct = FXCollections.observableArrayList();
        final List<Student> students = DBWorker.getStudentsByGroup(group);
        tableTaskStruct.addAll(students);
        studentsTableView.setItems(tableTaskStruct);
    }

    private void setOnButtonsActions() {
        addButton.setOnAction(event -> {
            Parent secondaryLayout;
            try {
                secondaryLayout = FXMLLoader.load(getClass().getResource("/ui/add/forms.fxml"));
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            Scene secondScene = new Scene(secondaryLayout, 700, 460);
            Stage newWindow = new Stage();
            newWindow.setTitle("Добавить...");
            newWindow.setScene(secondScene);
            //newWindow.initModality(Modality.WINDOW_MODAL);
            //newWindow.initOwner(Main.primaryStage);
            newWindow.setResizable(false);
            newWindow.show();
            newWindow.setOnCloseRequest(event1 -> {
                try {
                    refreshDataOnGroupTable();
                    studentsTableView.getItems().clear();
                } catch (SQLException e) {
                    MessageBox.error("Ошибка",
                            "Ошибка обновления списка групп.",
                            "");
                }
            });
        });

        generationButton.setOnAction(event -> {
            Parent secondaryLayout;
            try {
                secondaryLayout = FXMLLoader.load(getClass().getResource("/ui/generate/forms.fxml"));
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            Scene secondScene = new Scene(secondaryLayout, 1200, 560);
            Stage newWindow = new Stage();
            newWindow.setTitle("Генерация");
            newWindow.setScene(secondScene);
            newWindow.initModality(Modality.WINDOW_MODAL);
            newWindow.initOwner(Main.primaryStage);
            newWindow.setResizable(false);
            newWindow.show();
            newWindow.setOnCloseRequest(event1 -> {
                try {
                    refreshDataOnTaskTable();
                } catch (SQLException e) {
                    MessageBox.error("Ошибка",
                            "Ошибка обновления списка студентов.",
                            "");
                }
            });

        });
    }

    private void setOnTablesAction() {
        taskTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            try {
                if(newValue != null){
                    StudentTask task = DBWorker.getTaskByID(newValue.getKey());
                    ui.preview.Controller.setStudentTask(task);
                    Parent secondaryLayout;
                    try {
                        secondaryLayout = FXMLLoader.load(getClass().getResource("/ui/preview/forms.fxml"));
                    } catch (IOException e) {
                        e.printStackTrace();
                        return;
                    }
                    Scene secondScene = new Scene(secondaryLayout);
                    Stage newWindow = new Stage();
                    newWindow.setTitle("Просмотр");
                    newWindow.setScene(secondScene);
                    newWindow.initModality(Modality.WINDOW_MODAL);
                    newWindow.initOwner(Main.primaryStage);
                    newWindow.setResizable(false);
                    newWindow.show();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        groupListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null){
                try {
                    refreshDataOnStudentsTable(newValue);
                } catch (SQLException e) {
                    MessageBox.error("Ошибка",
                            "",
                            "Ошибка заполнения таблицы. Текст ошибки: \n " + e.getMessage());
                }
            }
        });
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            createTaskTable();
            createStudentsTable();
            //createGroupTable();
            refreshDataOnTaskTable();
            refreshDataOnGroupTable();
            setOnButtonsActions();
            setOnTablesAction();
        } catch (SQLException e) {
            MessageBox.confirmationWithClose("Ошибка соединения с базой данных",
                    "Продолжить?",
                    "Работа с базой данных завершена с ошибкой: \n\t \"" + e.getMessage() +
                            "\"\nНажмите \"ОК\", чтобы продолжить работу с программой. " +
                            "Данные не будут сохранены в базу и будут существовать только на вашем компьютере.");
            addButton.setDisable(true);
        }
    }



}
