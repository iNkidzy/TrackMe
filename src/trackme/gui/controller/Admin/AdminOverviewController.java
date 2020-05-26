/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trackme.gui.controller.Admin;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import trackme.be.Project;
import trackme.be.Task;
import trackme.be.User;
import trackme.bll.BLLManager;
import trackme.gui.controller.Employee.UserOverviewController;
import trackme.gui.model.UserModel;

/**
 *
 * @author mac
 */
public class AdminOverviewController implements Initializable {

    @FXML
    private AnchorPane OverviewUser;
    @FXML
    private JFXComboBox<Project> sortcombobox;
    @FXML
    private JFXComboBox<User> employeecombobox;
    @FXML
    private BarChart<String, Integer> projectBarChart;

    @FXML
    private TableView<Task> tasksOverviewTable;
    @FXML
    private TableColumn<Task, String> tasks;
    @FXML
    private TableColumn<Task, LocalDate> date;
    @FXML
    private TableColumn<Task, Integer> tamespent;

    @FXML
    private ImageView menubar;
    @FXML
    private Pane usrmenubar;
    @FXML
    private Label usrnamelbl;
    @FXML
    private JFXButton overviewbtn;
    @FXML
    private JFXButton trackerbtn;
    @FXML
    private JFXButton createbtn;
    @FXML
    private JFXButton profilesbtn;
    @FXML
    private Button logoutbtn;

    @FXML
    private JFXDatePicker fromDatePicker;
    @FXML
    private JFXDatePicker toDatePicker;

    private User user;
    private User currentUser;
    private Project project;
    private Task task;
    private List<Project> projects;

    private UserModel userModel;
    private BLLManager bllManager;

    private final String LoginScene = "/trackme/gui/view/Login.fxml";
    private final String OverviewScene = "/trackme/gui/view/AdminOverview.fxml";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        userModel = UserModel.getInstance();
        currentUser = userModel.getLoggedInUser();
        usrnamelbl.setText(currentUser.getName());
        this.bllManager = new BLLManager();

        try {
            setUsersInEmployeeBox();
        } catch (SQLServerException ex) {
            Logger.getLogger(UserOverviewController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(AdminOverviewController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Employee Combo Box selected employee method
     *
     * @param event
     * @throws SQLException
     */
    @FXML
    private void setEmployeeComboBox(ActionEvent event) throws SQLException {
        user = employeecombobox.getSelectionModel().getSelectedItem();
        projects = bllManager.getUserProjectTime(user);
        bllManager.getTotalTimeForEachProject(projects);
        projectBarChart.getData().clear();
        setBarChartForSelectedProject(projects);
        setTaskInComboBox(user);
        tasksOverviewTable.getItems().clear();
    }

    /**
     * Task Combo Box setup method
     *
     * @param user
     * @throws SQLServerException
     * @throws SQLException
     */
    private void setTaskInComboBox(User user) throws SQLServerException, SQLException {
        ObservableList<Project> projectList = FXCollections.observableArrayList(bllManager.getProjectsForUser(user));
        sortcombobox.getItems().clear();
        sortcombobox.getItems().addAll(projectList);
        sortcombobox.getSelectionModel().select(sortcombobox.getValue());
    }

    /**
     * Employee Combo Box setUp method
     *
     * @throws SQLServerException
     * @throws SQLException
     */
    private void setUsersInEmployeeBox() throws SQLServerException, SQLException {
        ObservableList<User> userList = FXCollections.observableArrayList(bllManager.getAllUsers());
        for (int i = 0; i < userList.size(); i++) {
            userList.get(i).setEmail("");
            userList.get(i).setPassword("");
        }
        employeecombobox.getItems().clear();
        employeecombobox.getItems().addAll(userList);
        employeecombobox.getSelectionModel().select(employeecombobox.getValue());
    }

    /**
     * Combo box sorting??? //MARTIN
     *
     * @param event
     * @throws SQLServerException
     * @throws ParseException
     */
    @FXML
    private void setSortComboBox(ActionEvent event) throws SQLServerException, ParseException {
        project = sortcombobox.getSelectionModel().getSelectedItem();
        setTaskOverview(user, project);
    }

    /**
     * Date Picker selection From Date Method
     *
     * @param event
     * @throws SQLServerException
     * @throws ParseException
     */
    @FXML
    private void setSelectedFromDate(ActionEvent event) throws SQLServerException, ParseException {
        if (toDatePicker.getValue() == null) {

        } else {
            setTaskOverview(user, project);
        }
    }

    /**
     * Date Picker To Date method
     *
     * @param event
     * @throws SQLServerException
     * @throws ParseException
     */
    @FXML
    private void setSelectedToDate(ActionEvent event) throws SQLServerException, ParseException {
        if (fromDatePicker.getValue() == null) {
        } else {
            setTaskOverview(user, project);
        }
    }

    /**
     * Setup Task Overview Table Method
     *
     * @param user
     * @param project
     * @throws SQLServerException
     * @throws ParseException
     */
    private void setTaskOverview(User user, Project project) throws SQLServerException, ParseException {
        List<Task> allTaskLogs = bllManager.getAllTaskLogsForProject(user, project);

        if (fromDatePicker.getValue() == null && toDatePicker.getValue() == null) {
            for (Task task : allTaskLogs) {
                bllManager.getTotalTimeForTask(task);
                task.setDate(task.getTaskTime().get(0).getTime().toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
                task.setTotalTime(convertSecondsToHourMinuteSecond(task));
            }
            ObservableList<Task> taskList = FXCollections.observableArrayList(allTaskLogs);
            tasks.setCellValueFactory(new PropertyValueFactory<>("name"));
            date.setCellValueFactory(new PropertyValueFactory<>("date"));
            tamespent.setCellValueFactory(new PropertyValueFactory<>("totalTime"));
            tasksOverviewTable.setItems(taskList);
        } else {
            bllManager.filterList(fromDatePicker.getValue(), toDatePicker.getValue(), allTaskLogs);
            for (Task task : allTaskLogs) {
                bllManager.getTotalTimeForTask(task);
                task.setDate(task.getTaskTime().get(0).getTime().toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
                task.setTotalTime(convertSecondsToHourMinuteSecond(task));
            }
            ObservableList<Task> taskList = FXCollections.observableArrayList(allTaskLogs);
            tasks.setCellValueFactory(new PropertyValueFactory<>("name"));
            date.setCellValueFactory(new PropertyValueFactory<>("date"));
            tamespent.setCellValueFactory(new PropertyValueFactory<>("totalTime"));
            tasksOverviewTable.setItems(taskList);
        }
    }

    /**
     * Converter Method
     *
     * @param task
     * @return
     */
    private String convertSecondsToHourMinuteSecond(Task task) {
        String time = "";
        int p1 = (int) task.getTotalTimeInSeconds() / 3600;
        int remainder = (int) task.getTotalTimeInSeconds() - p1 * 3600;
        int p2 = remainder / 60;
        remainder = remainder - p2 * 60;
        int p3 = remainder;
        time = p1 + ":" + p2 + ":" + p3;
        return time;
    }

    /**
     * BarChart Setup for selected project method
     *
     * @param projects
     */
    private void setBarChartForSelectedProject(List<Project> projects) {
        XYChart.Series dataSeries1 = new XYChart.Series();
        dataSeries1.setName("projects");
        for (Project var : projects) {
            String name = var.getName();
            long time = var.getTotalTimeInSeconds() / 3600;
            dataSeries1.getData().add(new XYChart.Data(name, time));
        }
        projectBarChart.setAnimated(false);
        projectBarChart.setTitle("Overall Time Spent On Projects");
        projectBarChart.getData().add(dataSeries1);
    }

    /**
     * Menu bar methods
     *
     * @param event
     */
    @FXML
    private void setShowMenubar(MouseEvent event) {
        usrmenubar.setVisible(true);
    }

    @FXML
    private void setCloseMenubar(MouseEvent event) {
        usrmenubar.setVisible(false);
    }

    /**
     * Opening Scene methods
     *
     * @param event
     * @throws IOException
     */
    @FXML
    private void setOverview(ActionEvent event) throws IOException {
        FXMLLoader fxloader = new FXMLLoader(getClass().getResource(OverviewScene));
        Parent root = fxloader.load();

        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        Stage closePreviousScene;
        closePreviousScene = (Stage) overviewbtn.getScene().getWindow();
        closePreviousScene.close();
    }

    @FXML
    private void setFrontPage(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/trackme/gui/view/AdminMainPage.fxml"));
        Parent root = loader.load();
        AdminMainPageController ctrl = loader.getController();

        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setResizable(false);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        Stage closePreviousScene;
        closePreviousScene = (Stage) trackerbtn.getScene().getWindow();
        closePreviousScene.close();
    }

    @FXML
    private void setLogOutusr(ActionEvent event) throws IOException {
        Stage logOutUser;
        logOutUser = (Stage) logoutbtn.getScene().getWindow();
        logOutUser.close();

        URL url = getClass().getResource(LoginScene);
        FXMLLoader fxmlload = new FXMLLoader();
        fxmlload.setLocation(url);
        Parent root = fxmlload.load();
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void setCreateScene(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/trackme/gui/view/AdminCreate.fxml"));
        Parent root = loader.load();
        AdminCreateController ctrl = loader.getController();

        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setResizable(false);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        Stage closePreviousScene;
        closePreviousScene = (Stage) createbtn.getScene().getWindow();
        closePreviousScene.close();
    }

    @FXML
    private void setProfilesPage(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/trackme/gui/view/AdminProfiles.fxml"));
        Parent root = loader.load();
        AdminProfilesController ctrl = loader.getController();

        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setResizable(false);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        Stage closePreviousScene;
        closePreviousScene = (Stage) profilesbtn.getScene().getWindow();
        closePreviousScene.close();
    }

}
