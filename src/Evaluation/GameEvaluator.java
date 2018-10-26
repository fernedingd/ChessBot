package Evaluation;

import ChessField.Move;
import ChessField.Position;
import ChessPieces.King;
import ChessPieces.Piece;
import Field.ChessFieldHolder;
import Pieces.Color;

import java.util.ArrayList;

public class GameEvaluator {
    ChessFieldHolder chessField;
    Color color;

    public GameEvaluator(ChessFieldHolder chessField, Color color) {
        this.chessField = chessField;
        this.color = color;
    }

    public int rateGame() {
        if (isInCheckmate(color)) {
            return Integer.MIN_VALUE;
        } else if (isInCheckmate(color == Color.BLACK ? Color.WHITE : Color.BLACK)) {
            return Integer.MAX_VALUE;
        }

        int rating = 0;

        for (Position position : chessField.getAll().keySet()) {
            Piece piece = chessField.getPiece(position);

            if (piece.getColor() == color) {
                rating += piece.getImportance();
            } else {
                rating -= piece.getImportance();
            }
        }

        if (isInCheck(getKingPosition(color), color, chessField)) {
            rating -= 250;
        }

        if (isInCheck(getKingPosition(color == Color.BLACK ? Color.WHITE : Color.BLACK),
                color == Color.BLACK ? Color.WHITE : Color.BLACK,
                chessField)) {
            rating += 250;
        }

        return rating;
    }

    private boolean isInCheckmate(Color kingColor) {
        Color ownColor = kingColor == Color.BLACK ? Color.WHITE : Color.BLACK;
        Position kingPosition = getKingPosition(kingColor);

        if (kingPosition == null) { //The King has been taken
            return true;
        }

        if (isInCheck(kingPosition, ownColor, chessField)) {
            //we know it's in check so we have to check if we can get out by moving the king
            ChessFieldHolder checkChessField;
            for (Position position : chessField.getPiece(kingPosition).getValidMoves(kingPosition, chessField)) {
                checkChessField = chessField.getCopy();

                checkChessField.doMove(new Move(kingPosition, position));

                if (!isInCheck(position, ownColor, checkChessField)) {
                    return false;
                }
            }

            //we can't take it by moving away so we have to check if we can take the checking opponent or get in the way
            ArrayList<Position> checkingOpponents = getCheckingOpponents(kingPosition, ownColor);
            if (checkingOpponents.size() == 1) { //taking it and blocking it only work if there's just one checking opponent
                if (isInCheck(checkingOpponents.get(0), kingColor, chessField)) {
                    ArrayList<Position> kingSaviors = getCheckingOpponents(checkingOpponents.get(0), kingColor);
                    if (kingSaviors.size() == 1 && chessField.getPiece(kingSaviors.get(0)) instanceof King) {
                        ChessFieldHolder chessFieldCopy = chessField.getCopy();
                        chessFieldCopy.doMove(new Move(kingPosition, checkingOpponents.get(0)));
                        if (!isInCheck(checkingOpponents.get(0), ownColor, chessFieldCopy)) {
                            return false;
                        }
                    } else {
                        return false;
                    }
                } else {
                    ArrayList<Position> pathToKing = chessField.getPiece(checkingOpponents.get(0))
                            .getPathToTakePiece(checkingOpponents.get(0), kingPosition, chessField);

                    for (Position position : pathToKing) {
                        if (isInCheck(position, kingColor, chessField)) {
                            return false;
                        }
                    }
                }
            }
        } else {
            return false;
        }

        return true;
    }

    private Position getKingPosition(Color color) {
        //get King position
        Position kingPosition = null;
        for (Position position : chessField.getAll().keySet()) {
            if (chessField.getPiece(position).getColor() == color && chessField.getPiece(position) instanceof King) {
                kingPosition = position;
                break;
            }
        }

        return kingPosition;
    }

    private boolean isInCheck(Position kingPosition, Color kingColor, ChessFieldHolder chessField) {
        for (Position position : chessField.getAll().keySet()) {
            if (chessField.getPiece(position).getColor() != kingColor) {
                if (chessField.getPiece(position).getValidMoves(position, chessField).contains(kingPosition)) {
                    return true;
                }
            }
        }

        return false;
    }

    private ArrayList<Position> getCheckingOpponents(Position ownPosition, Color opponentColor) {
        ArrayList<Position> opponents = new ArrayList<>();

        for (Position position : chessField.getAll().keySet()) {
            if (chessField.getPiece(position).getColor() == opponentColor) {
                if (chessField.getPiece(position).getValidMoves(position, chessField).contains(ownPosition)) {
                    opponents.add(position);
                }
            }
        }

        return opponents;
    }
}
