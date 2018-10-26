package DataStructure;

import Evaluation.GameEvaluator;
import Field.ChessFieldHolder;
import Pieces.Color;

import java.util.ArrayList;
import java.util.List;

public class GameTree {
    List<GameTree> children;
    GameTree parent;
    ChessFieldHolder value;
    Color ownColor;
    Integer rating;

    public GameTree(ChessFieldHolder value, GameTree parent, Color ownColor) {
        this.value = value;
        this.parent = parent;
        this.ownColor = ownColor;
        this.children = new ArrayList<>();
        rating = null;
    }

    public GameTree getParent() {
        return parent;
    }

    public void setParent(GameTree parent) {
        this.parent = parent;
    }

    public List<GameTree> getChildren() {
        return children;
    }

    public void setChildren(List<GameTree> children) {
        this.children = children;
    }

    public void addChildren(List<GameTree> children) {
        this.children.addAll(children);
    }

    public void addChild(GameTree child) {
        this.children.add(child);
    }

    public ChessFieldHolder getValue() {
        return this.value;
    }

    public void setValue(ChessFieldHolder value) {
        this.value = value;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public void rateNode() {
        this.rating = (new GameEvaluator(value, ownColor)).rateGame();
    }


}
