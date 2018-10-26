package ChessPieces;

import ChessField.Move;
import ChessField.Position;
import Field.ChessFieldHolder;

import java.util.ArrayList;

public interface Piece {
    boolean isMoveValid(Move move, ChessFieldHolder chessField);

    ArrayList<Position> getValidMoves(Position position, ChessFieldHolder chessField); //TODO: refactor in all classes

    ArrayList<Position> getPathToTakePiece(Position ownPosition, Position targetPosition, ChessFieldHolder chessField); //TODO MAYBE: implement pathfinding

    boolean wasMoved();

    void setWasMoved(boolean wasMoved);

    Pieces.Color getColor();

    /**
     * @return the importance of the piece for the game on a scale of 1 to 100
     */
    int getImportance();
}
