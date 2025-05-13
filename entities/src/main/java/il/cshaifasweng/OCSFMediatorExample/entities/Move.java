package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;

public class Move implements Serializable {
    private int row;
    private int col;

    public Move(int row, int col) {
        this.row = row;   // ✅ store the row!
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }
}
