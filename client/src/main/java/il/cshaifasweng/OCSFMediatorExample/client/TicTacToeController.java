package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.event.ActionEvent;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.EventBus;

import il.cshaifasweng.OCSFMediatorExample.entities.TicTacToeMessage;
import il.cshaifasweng.OCSFMediatorExample.entities.Move;

public class TicTacToeController {

    @FXML private Button btn00, btn01, btn02;
    @FXML private Button btn10, btn11, btn12;
    @FXML private Button btn20, btn21, btn22;

    private Button[][] board;
    private String mySymbol = "";  // "X" or "O"
    private boolean myTurn = false;

    @FXML
    public void initialize() {
        EventBus.getDefault().register(this);
        board = new Button[][] {
                { btn00, btn01, btn02 },
                { btn10, btn11, btn12 },
                { btn20, btn21, btn22 }
        };
        // manual row/col mapping
        btn00.setUserData(new int[]{0,0});  btn01.setUserData(new int[]{0,1});  btn02.setUserData(new int[]{0,2});
        btn10.setUserData(new int[]{1,0});  btn11.setUserData(new int[]{1,1});  btn12.setUserData(new int[]{1,2});
        btn20.setUserData(new int[]{2,0});  btn21.setUserData(new int[]{2,1});  btn22.setUserData(new int[]{2,2});
    }

    @FXML
    private void handleButtonClick(ActionEvent event) {
        if (!myTurn) return;                   // GUI‐level guard
        Button btn = (Button) event.getSource();
        int[] pos = (int[]) btn.getUserData();
        int row = pos[0], col = pos[1];
        if (!btn.getText().isEmpty()) return;

        myTurn = false;
        setBoardDisabled(true);

        try {
            SimpleClient.getClient().sendToServer(new TicTacToeMessage("MOVE", new Move(row, col)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Subscribe
    public void onTicTacToeMessage(TicTacToeMessage msg) {
        Platform.runLater(() -> {
            switch (msg.getType()) {
                case "START_GAME":
                    mySymbol = (String) msg.getData();
                    myTurn   = "X".equals(mySymbol);
                    showAlert("Game Started!", "You are player " + mySymbol + ". " +
                            (myTurn ? "Your turn!" : "Waiting..."));
                    clearBoard();
                    setBoardDisabled(!myTurn);
                    break;

                case "UPDATE_BOARD":
                    Object[] data = (Object[]) msg.getData();
                    char[][] serverBoard = (char[][]) data[0];
                    String nextPlayerSymbol = (String) data[1];
                    updateBoardFromServer(serverBoard);
                    myTurn = mySymbol.equals(nextPlayerSymbol);
                    setBoardDisabled(!myTurn);
                    break;

                case "WIN":
                    String winnerSymbol = (String) msg.getData();
                    showAlert("Game Over", "🎉 " + winnerSymbol + " wins!");
                    disableBoard();
                    break;

                case "DRAW":
                    showAlert("Game Over", "😐 It's a draw!");
                    disableBoard();
                    break;
            }
        });
    }

    private void updateBoardFromServer(char[][] b) {
        for (int i=0;i<3;i++) for (int j=0;j<3;j++) {
            String c = String.valueOf(b[i][j]);
            board[i][j].setText(c.equals(" ") ? "" : c);
            board[i][j].setStyle(
                    "X".equals(c)
                            ? "-fx-text-fill:red;-fx-font-size:24px;"
                            : "O".equals(c)
                            ? "-fx-text-fill:blue;-fx-font-size:24px;"
                            : ""
            );
        }
    }

    private void clearBoard() {
        for (Button[] row: board) for (Button b: row) {
            b.setText(""); b.setStyle(""); b.setDisable(false);
        }
    }

    private void disableBoard() {
        for (Button[] row: board) for (Button b: row) b.setDisable(true);
    }

    private void setBoardDisabled(boolean disable) {
        for (Button[] row: board) for (Button b: row)
            if (b.getText().isEmpty()) b.setDisable(disable);
    }

    private void showAlert(String t, String c) {
        Alert a = new Alert(AlertType.INFORMATION);
        a.setTitle(t); a.setHeaderText(null); a.setContentText(c);
        a.showAndWait();
    }
}
