package Bot;

import ChessField.ChessFieldHelper;
import ChessField.Move;
import ChessField.Position;
import ChessPieces.Piece;
import DataStructure.GameTree;
import Field.ChessFieldHolder;
import Player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class IntelligentBot extends Player {
    ChessFieldHolder chessField;
    GameTree tree;

    public IntelligentBot() {
        chessField = new ChessFieldHolder();
    }

    @Override
    public void getNextMove(ChessFieldHelper chessFieldHelper, Move lastMove) {
        if (lastMove != null) {
            chessField.doMove(lastMove);
        }

        Move bestMove = null;
        int nullCalculationCounter = 0;

        while (bestMove == null) {
            if (++nullCalculationCounter > 1) {
                System.out.println("Calulated null. Trying again [" + nullCalculationCounter + "]");
            }

            loadTree(5);

            bestMove = getNextMove();
        }

        chessField.doMove(bestMove);
        chessFieldHelper.nextMoveCallback(bestMove);
    }

    private Move getNextMove() {
        List<GameTree> deepestLevel = getLowestLevelAsList();
        if (deepestLevel.size() > 0) {
            for (GameTree tree : deepestLevel) {
                tree.rateNode();
            }

            for (GameTree tree : deepestLevel) {
                if (tree.getParent() != null && tree.getParent().getRating() != null) {
                    continue;
                }

                while (tree.getParent() != null) {
                    tree = tree.getParent();

                    if (calculateLevel(tree) % 2 == 0) {
                        tree.setRating(getMaxRating(tree.getChildren()));
                    } else {
                        tree.setRating(getMinRating(tree.getChildren()));
                    }
                }
            }
        }

        List<GameTree> bestTrees = new ArrayList<>();
        for (GameTree child : this.tree.getChildren()) {
            if (child.getRating().equals(this.tree.getRating())) {
                bestTrees.add(child);
            }
        }

        if (bestTrees.size() > 1) {
            return bestTrees.get(new Random().nextInt(bestTrees.size())).getValue().getLastMove();
        } else {
            return bestTrees.get(0).getValue().getLastMove();
        }
    }

    private int calculateLevel(GameTree tree) {
        int level = 0;

        while (tree.getParent() != null) {
            tree = tree.getParent();
            level++;
        }

        return level;
    }

    private Integer getMaxRating(List<GameTree> children) {
        if (children.size() > 0) {
            int maxRating = children.get(0).getRating();

            for (int i = 1; i < children.size(); i++) {
                GameTree child = children.get(i);
                if (child.getRating() == null) {
                    continue;
                }
                if (child.getRating() > maxRating) {
                    maxRating = child.getRating();
                }
            }

            return maxRating;
        }

        return null;
    }

    private Integer getMinRating(List<GameTree> children) {
        if (children.size() > 0) {
            int minRating = children.get(0).getRating();

            for (int i = 1; i < children.size(); i++) {
                GameTree child = children.get(i);
                if (child.getRating() == null) {
                    continue;
                }
                if (child.getRating() < minRating) {
                    minRating = child.getRating();
                }
            }

            return minRating;
        }

        return null;
    }

    private List<GameTree> getLowestLevelAsList() {
        List<GameTree> lowestLevel = new ArrayList<>();

        addLowestLevelToList(tree, lowestLevel);

        return lowestLevel;
    }

    private void addLowestLevelToList(GameTree tree, List<GameTree> lowestLevel) {
        for (GameTree childTree : tree.getChildren()) {
            addLowestLevelToList(childTree, lowestLevel);
        }

        if (getTreeDepth(tree) == 0) {
            lowestLevel.add(tree);
        }
    }

    private int getTreeDepth(GameTree tree) {
        int depth = 0;

        if (tree.getChildren().size() > 0) {
            depth++;
            GameTree childTree = tree.getChildren().get(0);

            while (childTree.getChildren().size() > 0) {
                depth++;

                childTree = childTree.getChildren().get(0);
            }
        }

        return depth;
    }

    private void loadTree(int depth) {
        tree = new GameTree(chessField, null, color);

        loadChildren(tree, chessField, true);

        loadTreeRec(depth - 1, tree, false);
    }

    private void loadTreeRec(int depth, GameTree tree, boolean isOwnMove) {
        if (depth >= 1) {
            for (GameTree childTree : tree.getChildren()) {
                ChessFieldHolder childTreeValue = childTree.getValue();

                loadChildren(childTree, childTreeValue, isOwnMove);

                loadTreeRec(depth - 1, childTree, !isOwnMove);
            }
        }
    }

    private void loadChildren(GameTree tree, ChessFieldHolder chessField, boolean isOwnMove) {
        for (Position position : chessField.getAll().keySet()) {
            Piece piece = chessField.getPiece(position);
            if ((isOwnMove && piece.getColor() == color) || (!isOwnMove && piece.getColor() != color)) {
                for (Position targetPosition : piece.getValidMoves(position, chessField)) {
                    Move move = new Move(position, targetPosition);
                    ChessFieldHolder chessFieldCopy = chessField.getCopy();
                    chessFieldCopy.doMove(move);
                    tree.addChild(new GameTree(chessFieldCopy, tree, color));
                }
            }
        }
    }

    @Override
    public boolean isHuman() {
        return false;
    }
}
