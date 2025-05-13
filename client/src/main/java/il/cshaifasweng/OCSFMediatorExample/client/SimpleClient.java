package il.cshaifasweng.OCSFMediatorExample.client;

import org.greenrobot.eventbus.EventBus;

import il.cshaifasweng.OCSFMediatorExample.client.ocsf.AbstractClient;
import il.cshaifasweng.OCSFMediatorExample.entities.Warning;
import il.cshaifasweng.OCSFMediatorExample.entities.TicTacToeMessage;

public class SimpleClient extends AbstractClient {

    private static SimpleClient client = null; // singleton

    private SimpleClient(String host, int port) {
        super(host, port);
    }

    @Override
    protected void handleMessageFromServer(Object msg) {
        if (msg instanceof Warning) {
            EventBus.getDefault().post(new WarningEvent((Warning) msg));
        } else if (msg instanceof TicTacToeMessage) {
            // 🔵 NEW: forward TicTacToe messages to GUI
            EventBus.getDefault().post(msg);
        } else {
            System.out.println("Unknown message from server: " + msg.toString());
        }
    }

    public static SimpleClient getClient() {
        if (client == null) {
            client = new SimpleClient("localhost", 3000); // change host/port if needed
        }
        return client;
    }
}