import Bot.IntelligentBot;
import Pieces.Color;

public class Debugger {
    public static void main(String[] args) {
        IntelligentBot intelligentBot = new IntelligentBot();
        intelligentBot.initialize(null, "Test", Color.WHITE);
        intelligentBot.getNextMove(null, null);
    }
}
