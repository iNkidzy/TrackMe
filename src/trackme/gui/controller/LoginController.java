/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trackme.gui.controller;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author mac
 * 
 */
public class LoginController implements Initializable {
  
    @FXML
    private AnchorPane LoginStage;
    @FXML
    private Button loginbtn;
    @FXML
    private JFXTextField emaillbl;
    @FXML
    private JFXPasswordField passlbl;
   
    private final String UserLogin = "/trackme/gui/view/UserMainPage.fxml";
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
            
    }    


    private void authentication() throws IOException{
        
        FXMLLoader fxmloader  = new FXMLLoader(getClass().getResource("/trackme/gui/view/UserMainPage.fxml"));
        Parent root = fxmloader.load();
        
        
            Stage stage = new Stage();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
            
            closeLoginScene();
    }
    
    @FXML
    private void pressEnter(KeyEvent event) throws IOException {
        if(event.getCode()==KeyCode.ENTER){
            authentication();
        }
    }

    @FXML
    private void clickLogIn(ActionEvent event) throws IOException {
        authentication();
        
    }
    
    private void closeLoginScene(){
        Stage loginStage;
        loginStage = (Stage) loginbtn.getScene().getWindow();
        loginStage.close();
    }
}
