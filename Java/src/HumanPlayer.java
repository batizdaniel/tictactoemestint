// 
// Decompiled by Procyon v0.5.36
// 


import game.gmk.ui.GameGraphicsController;
import game.gmk.GomokuGame;
import game.gmk.GomokuAction;
import java.util.Random;
import java.util.Scanner;

import game.gmk.GomokuPlayer;

public class HumanPlayer extends GomokuPlayer
{
    public HumanPlayer(final int color, final int[][] board, final Random r) {
        super(color, board, r);
    }
    
    @Override
    public GomokuAction getAction(final GomokuAction prevAction, final long[] remainingTimes) {
        if (prevAction != null) {
            this.board[prevAction.i][prevAction.j] = 1 - this.color;
        }

        Scanner i = new Scanner(System.in);
        System.out.println("Add meg a kovetkezo lépést: i : ");
        i.hasNextLine();
        Scanner j = new Scanner(System.in);
        System.out.println("Add meg a kovetkezo lépést: j : ");
        j.hasNextLine();



        return new GomokuAction(i.nextInt(),j.nextInt());
    }
}
