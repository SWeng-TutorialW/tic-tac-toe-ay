package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;

public class TicTacToeMessage implements Serializable {

    private String type;
    private Object data;

    public TicTacToeMessage(String type, Object data) {
        this.type = type;
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public Object getData() {
        return data;
    }
}

