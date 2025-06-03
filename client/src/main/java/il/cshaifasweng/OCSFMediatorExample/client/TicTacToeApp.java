package il.cshaifasweng.OCSFMediatorExample.client;
import org.greenrobot.eventbus.EventBus;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class TicTacToeApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/il/cshaifasweng/OCSFMediatorExample/client/TicTacToe.fxml"));
        Parent root = loader.load();

        TicTacToeController controller = loader.getController();

        primaryStage.setTitle("Tic Tac Toe");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        SimpleClient.getClient().openConnection();
        System.out.println("Client connected to server!");
    }




    public static void main(String[] args) {
        launch(args);
    }
}
