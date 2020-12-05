import game.engine.Engine;

import java.util.concurrent.ThreadLocalRandom;

public class Main {
    public static void main(String[] args) {

        String[] args1 = {"1", "game.gmk.GomokuGame", "5646161", "15", "15", "0.1", "999999999", "Agent", "game.gmk.players.GreedyPlayer"};

        try {
            Engine.main(args1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}