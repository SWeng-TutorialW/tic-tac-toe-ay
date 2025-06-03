package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.server.ocsf.AbstractServer;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.ConnectionToClient;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.SubscribedClient;
import il.cshaifasweng.OCSFMediatorExample.entities.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SimpleServer extends AbstractServer {
	private static final List<SubscribedClient> subscribers = new ArrayList<>();
	private final List<ConnectionToClient> waitingPlayers = new ArrayList<>();
	private final List<Game> activeGames = new ArrayList<>();

	public SimpleServer(int port) { super(port); }

	@Override
	protected void clientConnected(ConnectionToClient client) {
		waitingPlayers.add(client);

		if (waitingPlayers.size() == 2) {
			ConnectionToClient p1 = waitingPlayers.get(0);
			ConnectionToClient p2 = waitingPlayers.get(1);

			// תמיד השחקן הראשון הוא X והשני הוא O
			try {
				p1.sendToClient(new TicTacToeMessage("START_GAME", "X"));
				p2.sendToClient(new TicTacToeMessage("START_GAME", "O"));
			} catch (IOException e) {
				e.printStackTrace();
			}

			// מייצרים משחק חדש, כאשר השחקן הראשון (p1) מתחיל כ־X
			activeGames.add(new Game(p1, p2, p1));
			waitingPlayers.clear();
		}
	}


	@Override
	protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
		if (!(msg instanceof TicTacToeMessage)) return;
		TicTacToeMessage tmsg = (TicTacToeMessage) msg;
		if (!"MOVE".equals(tmsg.getType())) return;
		Move move = (Move) tmsg.getData();

		for (Game game : activeGames) {
			if (!game.hasPlayer(client)) continue;

			// 🚧 Out-of-turn guard: echo board & turn, ignore move
			if (game.currentPlayer != client) {
				sendBoardAndTurn(game);
				return;
			}

			// apply move
			if (!game.makeMove(move.getRow(), move.getCol())) return;

			// WIN?
			if (game.checkWin()) {
				Object[] finalBoard = { game.getBoard(), "" };
				sendBoardUpdate(game, finalBoard);
				// announce the winner symbol
				String winnerSymbol = (game.currentPlayer == game.player1) ? "X" : "O";
				TicTacToeMessage winMsg = new TicTacToeMessage("WIN", winnerSymbol);
				sendToBoth(game, winMsg);
				activeGames.remove(game);
				return;
			}

			// DRAW?
			if (game.isDraw()) {
				Object[] finalBoard = { game.getBoard(), "" };
				sendBoardUpdate(game, finalBoard);
				TicTacToeMessage drawMsg = new TicTacToeMessage("DRAW", null);
				sendToBoth(game, drawMsg);
				activeGames.remove(game);
				return;
			}

			// normal turn switch
			game.switchPlayer();
			String nextSymbol = (game.currentPlayer == game.player1) ? "X" : "O";
			Object[] upd = { game.getBoard(), nextSymbol };
			sendBoardUpdate(game, upd);
			break;
		}
	}

	// --- Helpers ---
	private void sendBoardAndTurn(Game g) {
		String next = (g.currentPlayer == g.player1) ? "X" : "O";
		Object[] upd = { g.getBoard(), next };
		sendBoardUpdate(g, upd);
	}

	private void sendBoardUpdate(Game g, Object[] data) {
		try {
			((ConnectionToClient) g.player1).sendToClient(new TicTacToeMessage("UPDATE_BOARD", data));
			((ConnectionToClient) g.player2).sendToClient(new TicTacToeMessage("UPDATE_BOARD", data));
		} catch (IOException e) { e.printStackTrace(); }
	}

	private void sendToBoth(Game g, TicTacToeMessage m) {
		try {
			((ConnectionToClient) g.player1).sendToClient(m);
			((ConnectionToClient) g.player2).sendToClient(m);
		} catch (IOException e) { e.printStackTrace(); }
	}
}
