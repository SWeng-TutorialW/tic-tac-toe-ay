package il.cshaifasweng.OCSFMediatorExample.entities;

public class Game {
    public Object player1, player2, currentPlayer;
    private char[][] board = new char[3][3];

    public Game(Object p1, Object p2, Object startingPlayer) {
        this.player1 = p1;
        this.player2 = p2;
        this.currentPlayer = startingPlayer;

        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                board[i][j] = ' ';
    }

    public boolean hasPlayer(Object client) {
        return client == player1 || client == player2;
    }

    public boolean makeMove(int row, int col) {
        if (row >= 0 && row < 3 && col >= 0 && col < 3) {
            if (board[row][col] == ' ') {
                board[row][col] = (currentPlayer == player1) ? 'X' : 'O';
                return true;
            }
        }
        return false;
    }

    public boolean checkWin() {
        char symbol = (currentPlayer == player1) ? 'X' : 'O';

        for (int i = 0; i < 3; i++) {
            if (board[i][0] == symbol && board[i][1] == symbol && board[i][2] == symbol)
                return true;
            if (board[0][i] == symbol && board[1][i] == symbol && board[2][i] == symbol)
                return true;
        }

        if (board[0][0] == symbol && board[1][1] == symbol && board[2][2] == symbol)
            return true;
        if (board[0][2] == symbol && board[1][1] == symbol && board[2][0] == symbol)
            return true;

        return false;
    }

    public boolean isDraw() {
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                if (board[i][j] == ' ') return false;
        return true;
    }

    public void switchPlayer() {
        currentPlayer = (currentPlayer == player1) ? player2 : player1;
    }

    public char[][] getBoard() {
        return board;
    }
}
