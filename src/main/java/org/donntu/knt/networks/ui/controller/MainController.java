package org.donntu.knt.networks.ui.controller;


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
import org.donntu.knt.networks.GregorianCalendar;
import org.donntu.knt.networks.Main;
import org.donntu.knt.networks.databaseworker.DBConnector;
import org.donntu.knt.networks.databaseworker.DBWorker;
import org.donntu.knt.networks.databaseworker.Student;
import org.donntu.knt.networks.databaseworker.StudentTask;
import org.donntu.knt.networks.ui.component.MessageBox;
import org.donntu.knt.networks.ui.component.NewWindowCreator;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

public class MainController implements Initializable {

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

    @FXML
    MenuItem showTask;

    @FXML
    MenuItem taskDelete;

    @FXML
    MenuItem studentDelete;

    @FXML
    MenuItem groupDelete;

    @FXML
    MenuItem deleteAllTasksForStudent;

    @FXML
    MenuItem deleteAllTasksForGroup;

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

            surname.setMinWidth(300);
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

            surname.setMinWidth(300);
            name.setMinWidth(300);

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
                tempDate = dateFormat.parse(record.get("creation_date"));
            } catch (ParseException e) {
                System.out.println("Date parse error");
                continue;
            }
            tempCalendar = new GregorianCalendar();
            tempCalendar.setTime(tempDate);
            StudentTask task = new StudentTask(
                    tempCalendar,
                    record.get("name"),
                    record.get("surname"),
                    record.get("group"));
            task.setKey(Integer.parseInt(record.get("key")));
            tableTaskStruct.add(task);
        }
        taskTableView.setItems(tableTaskStruct);
    }

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
            try {
                NewWindowCreator.create(
                        "/fxml/add.fxml",
                        "Добавить",
                        false,
                        false,
                        Main.primaryStage,
                        700,
                        450,
                        event1 -> {
                            try {
                                refreshDataOnGroupTable();
                                studentsTableView.getItems().clear();
                            } catch (SQLException e) {
                                MessageBox.error("Ошибка обновления списка групп.");
                            }
                        });
            } catch (IOException e) {
                MessageBox.error(e.getMessage());
            }
        });

        generationButton.setOnAction(event -> {
            Parent secondaryLayout;
            try {
                secondaryLayout = FXMLLoader.load(getClass().getResource("/fxml/generate.fxml"));
            } catch (IOException e) {
                MessageBox.error(e.getMessage());
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
                    MessageBox.error("Ошибка обновления списка студентов.");
                }
            });

        });
        aboutProgramButton.setOnAction(event -> {
            Parent secondaryLayout;
            try {
                secondaryLayout = FXMLLoader.load(getClass().getResource("/fxml/about_program.fxml"));
            } catch (Exception e) {
                MessageBox.error(e.getMessage());
                return;
            }
            Scene secondScene = new Scene(secondaryLayout);
            Stage newWindow = new Stage();
            newWindow.setTitle("О программе");
            newWindow.setScene(secondScene);
            newWindow.initModality(Modality.WINDOW_MODAL);
            newWindow.initOwner(Main.primaryStage);
            newWindow.setResizable(false);
            newWindow.show();
        });
    }

    private void setOnActionContextMenu(){
        studentDelete.setOnAction(event -> {
            if(MessageBox.confirmation("Удалить студента из базы? Все задания, связанные с этими студентами так же удалятся.") == ButtonType.OK){
                try {
                    final Student selectedItem = studentsTableView.getSelectionModel().getSelectedItem();
                    DBWorker.deleteStudent(selectedItem.getName(), selectedItem.getSurname(), groupListView.getSelectionModel().getSelectedItem());
                    refreshDataOnStudentsTable(groupListView.getSelectionModel().getSelectedItem());
                    refreshDataOnTaskTable();
                } catch (SQLException e) {
                    MessageBox.error(e.getMessage());
                }
            }
        });

        groupDelete.setOnAction(event -> {
            if(MessageBox.confirmation("Удалить группу из базы? " +
                    "Все студенты и их задания находящиеся в этой группе будут так же удалены.") == ButtonType.OK){
                try {
                    DBWorker.deleteGroup(groupListView.getSelectionModel().getSelectedItem());
                    refreshDataOnGroupTable();
                    studentsTableView.getItems().clear();
                    refreshDataOnTaskTable();
                } catch (SQLException e) {
                    MessageBox.error(e.getMessage());
                }
            }
        });

        taskDelete.setOnAction(event ->{
            if(MessageBox.confirmation("Удалить задание?") == ButtonType.OK){
                try {
                    DBWorker.deleteTaskByID(taskTableView.getSelectionModel().getSelectedItem().getKey());
                    refreshDataOnTaskTable();
                } catch (SQLException e) {
                    MessageBox.error(e.getMessage());
                }
            }
        });

        showTask.setOnAction(event -> {
            try {
                StudentTask task = DBWorker.getTaskByID(taskTableView.getSelectionModel().getSelectedItem().getKey());
                PreviewController.setStudentTask(task);
                Parent secondaryLayout;
                try {
                    secondaryLayout = FXMLLoader.load(getClass().getResource("/fxml/preview.fxml"));
                } catch (IOException e) {
                    MessageBox.error("Ошибка открытия окна. Текст ошибки: \n\t\"" + e.getMessage() + "\"");
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
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        deleteAllTasksForGroup.setOnAction(event ->{
            if(MessageBox.confirmation(
                            "Удалить задания для этой группы?") == ButtonType.OK){
                try {
                    DBWorker.deleteTasksByGroup(groupListView.getSelectionModel().getSelectedItem());
                    refreshDataOnTaskTable();
                } catch (SQLException e) {
                   MessageBox.error("Ошибка удаления. Текст ошибки: \n " + e.getMessage());
                }
            }

        });

        deleteAllTasksForStudent.setOnAction(event -> {
            if(MessageBox.confirmation(
                    "Удалить задания для этого студента?") == ButtonType.OK){
                try {
                    DBWorker.deleteTasksByStudent(studentsTableView.getSelectionModel().getSelectedItem());
                    refreshDataOnTaskTable();
                } catch (SQLException e) {
                    MessageBox.error("Ошибка удаления. Текст ошибки: \n " + e.getMessage());
                }
            }
        });
    }

    private void setOnTablesAction() {

        taskTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null){
                taskDelete.setDisable(false);
                showTask.setDisable(false);
            } else {
                taskDelete.setDisable(true);
                showTask.setDisable(true);
            }
        });
        groupListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null){
                try {
                    refreshDataOnStudentsTable(newValue);
                } catch (SQLException e) {
                    MessageBox.error("Ошибка заполнения таблицы. Текст ошибки: \n " + e.getMessage());
                }
            }
        });
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            setOnButtonsActions();
            taskDelete.setDisable(true);
            showTask.setDisable(true);
            DBWorker.setDBConnector(DBConnector.getInstance());
            createTaskTable();
            createStudentsTable();
            refreshDataOnTaskTable();
            refreshDataOnGroupTable();
            setOnTablesAction();
            setOnActionContextMenu();
        } catch (SQLException e) {
            MessageBox.criticalError("Работа с базой данных завершена с ошибкой: \n\t\"" + e.getMessage() + "\"");
        }
    }



}
