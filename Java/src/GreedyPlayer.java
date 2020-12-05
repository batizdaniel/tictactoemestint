// 
// Decompiled by Procyon v0.5.36
// 

package game.gmk.players;

import java.util.Iterator;
import java.util.List;
import java.util.Collections;
import java.util.Random;
import game.gmk.GomokuAction;
import java.util.ArrayList;
import game.gmk.GomokuPlayer;

public class GreedyPlayer extends GomokuPlayer
{
    protected ArrayList<GomokuAction> actions;
    
    public GreedyPlayer(final int color, final int[][] board, final Random random) {
        super(color, board, random);
        this.actions = new ArrayList<GomokuAction>();
        for (int i = 0; i < board.length; ++i) {
            for (int j = 0; j < board[i].length; ++j) {
                this.actions.add(new GomokuAction(i, j));
            }
        }
    }
    
    @Override
    public GomokuAction getAction(final GomokuAction prevAction, final long[] remainingTimes) {
        if (prevAction == null) {
            int i;
            int j;
            for (i = this.board.length / 2, j = this.board[i].length / 2; this.board[i][j] != 2; i = this.random.nextInt(this.board.length), j = this.random.nextInt(this.board[i].length)) {}
            this.board[i][j] = this.color;
            return new GomokuAction(i, j);
        }
        this.board[prevAction.i][prevAction.j] = 1 - this.color;
        Collections.shuffle(this.actions, this.random);
        GomokuAction action = null;
        int score = -1;
        for (final GomokuAction a : this.actions) {
            if (this.board[a.i][a.j] == 2) {
                final int s = this.score(a.i, a.j, this.color) + this.score(a.i, a.j, 1 - this.color);
                if (score >= s) {
                    continue;
                }
                score = s;
                action = a;
            }
        }
        this.board[action.i][action.j] = this.color;
        return action;
    }
    
    protected int score(final int i, final int j, final int c) {
        int result = 0;
        result += 1 << this.countDirection(i, j, 1, -1, c);
        result += 1 << this.countDirection(i, j, 1, 0, c);
        result += 1 << this.countDirection(i, j, 1, 1, c);
        result += 1 << this.countDirection(i, j, 0, 1, c);
        result += 1 << this.countDirection(i, j, -1, 1, c);
        result += 1 << this.countDirection(i, j, -1, 0, c);
        result += 1 << this.countDirection(i, j, -1, -1, c);
        result += 1 << this.countDirection(i, j, 0, -1, c);
        return result;
    }
    
    protected int countDirection(final int i, final int j, final int di, final int dj, final int c) {
        final int ni = (i + this.board.length + di) % this.board.length;
        final int nj = (j + this.board[ni].length + dj) % this.board[ni].length;
        if (this.board[ni][nj] != c) {
            return 0;
        }
        return 1 + this.countDirection(ni, nj, di, dj, c);
    }
}
