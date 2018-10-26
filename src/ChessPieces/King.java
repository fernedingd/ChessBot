package ChessPieces;

import ChessField.Move;
import ChessField.Position;
import Field.ChessFieldHolder;

import java.util.ArrayList;

public class King implements Piece {
    private boolean wasMoved;
    private Pieces.Color color;

    public King(Pieces.Color color) {
        this.color = color;
        this.wasMoved = false;
    }

    @Override
    public ArrayList<Position> getValidMoves(Position position, ChessFieldHolder chessField) {
        ArrayList<Position> validMoves = new ArrayList<>();

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Position destination = new Position(row, col);
                if (isMoveValid(new Move(position, destination), chessField)) {
                    validMoves.add(destination);
                }
            }
        }

        return validMoves;
    }

    @Override
    public ArrayList<Position> getPathToTakePiece(Position ownPosition, Position targetPosition, ChessFieldHolder chessField) {
        if (isMoveValid(new Move(ownPosition, targetPosition), chessField)) {
            return new ArrayList<>();
        } else {
            return null;
        }
    }

    @Override
    public boolean isMoveValid(Move move, ChessFieldHolder chessField) {
        if (move.getOldPosition().equals(move.getNewPosition())) {
            return false;
        }

        int oldCol = move.getOldPosition().getCol();
        int newCol = move.getNewPosition().getCol();
        int oldRow = move.getOldPosition().getRow();
        int newRow = move.getNewPosition().getRow();

        int distanceVertical = newRow > oldRow ? newRow - oldRow : oldRow - newRow;
        int distanceHorizontal = newCol > oldCol ? newCol - oldCol : oldCol - newCol;

        if ((distanceHorizontal == 1 || distanceVertical == 1)
                && distanceHorizontal <= 1 && distanceVertical <= 1) {
            return chessField.getPiece(move.getNewPosition()) == null
                    || chessField.getPiece(move.getNewPosition()).getColor() != color;
        } else {
            return false;
        }
    }

    @Override
    public boolean wasMoved() {
        return wasMoved;
    }

    @Override
    public void setWasMoved(boolean wasMoved) {
        this.wasMoved = wasMoved;
    }

    @Override
    public Pieces.Color getColor() {
        return color;
    }

    @Override
    public int getImportance() {
        return 100;
    }
}
