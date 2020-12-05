///CS.D,h846161@stud.u-szeged.hu
import game.gmk.GomokuAction;
import game.gmk.GomokuGame;
import game.gmk.GomokuPlayer;

import java.util.ArrayList;
import java.util.Random;

public class Agent extends GomokuPlayer {
    private ArrayList<Integer> startcells;
    public Agent(int color, int[][] board, Random random) {
        super(color, board, random);
    }

    public boolean isAvailableCells(int[][] board) {
        int sum = 0;
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j] == GomokuGame.EMPTY) {
                    sum++;
                }
            }
        }
        return sum != 0;
    }

    @Override
    public GomokuAction getAction(GomokuAction prevAction, long[] remainingTimes) {

        /*

            O = 1     X = 0


         */
        if (prevAction != null) {
            board[prevAction.i][prevAction.j] = 1-color;
            if(startcells!=null && startcells.contains(indexvalue(prevAction.i,prevAction.j))){
                startcells.remove(startcells.indexOf(indexvalue(prevAction.i,prevAction.j)));
            }
            startcells = getPossiblecells(startcells,board,prevAction.i,prevAction.j);
        } else {
            int i = 7;
            int j = 7;
            while(board[i][j] !=2){
                i = random.nextInt(4) + 4;
                j = random.nextInt(4) + 4;
            }

            startcells = getPossiblecells(new ArrayList<Integer>(),board,i,j);
            board[i][j] = color;
            return new GomokuAction(i,j);
        }
        int score = Integer.MIN_VALUE;
        int best_i = 0;
        int best_j = 0;


            /*int[][] copyboard = boardcopy(board);
            copyboard[i][j] = color;
            ArrayList<Integer> nextcells = getPossiblecells(startcells,copyboard,i,j);*/

        Result result = negamax(board,0,color,startcells);
        best_i = result.i;
        best_j = result.j;

        startcells.remove(startcells.indexOf(indexvalue(best_i,best_j)));
        board[best_i][best_j] = color;
        startcells = getPossiblecells(startcells,board,best_i,best_j);
        return new GomokuAction(best_i,best_j);
    }

    /**
     * Ez a függvény adja vissza azt a lépést, amit a legjobbnak talált.
     *
     *
     * @param board A tábla
     * @param depth Mélység
     * @param color Addot játékos
     * @param possiblecells Elérhető cellák listája
     * @return
     */
    public Result negamax(int[][] board, int depth,int color,ArrayList<Integer> possiblecells) {


        //Ebbe megy ha depth 0
        if(depth == 0){
            int bestScore = Integer.MIN_VALUE;
            int best_i = 0;
            int best_j = 0;
            for (int x = 0; x < possiblecells.size(); x++){
                int i = row(possiblecells.get(x));
                int j = column(possiblecells.get(x));

                int score = heuristic(get_area(board,i,j),color);
                if(score > bestScore){
                    bestScore = score;
                    best_i = i;
                    best_j = j;
                }
            }
            return new Result(best_i,best_j,bestScore);
        }

        //Ebbe megy ha depth nem 0
        Result bestResult = new Result(0,0,Integer.MIN_VALUE);
        for (int x = 0; x < possiblecells.size(); x++){
            int i = row(possiblecells.get(x));
            int j = column(possiblecells.get(x));
            int[][] copyboard = boardcopy(board);

            copyboard[i][j] = color;
            if(depth == 2 ){
                int heu = heuristic(get_area(copyboard,i,j),color);
                if(heu == 555555555){
                    return new Result(i,j,heu);
                }
            }

            ArrayList<Integer> nextcells = getPossiblecells(possiblecells,copyboard,i,j);

            Result currentresult = negamax(copyboard,depth-1,1-color,nextcells);
            if(-currentresult.score > bestResult.score) {
                bestResult = new Result(i,j,-currentresult.score);
            }
        }
        return bestResult;
    }

    /**
     * Elérhető cellák. Annak a pontnak amit épp vizsgálunk, megnézzük körben a környezetét és ha szabad,
     * akkor hozzáadjuk a listához.
     * @param previouscells Előző lépés.
     * @param board Az adott tábla állás.
     * @param i i koordinátája a pontnak.
     * @param j j koordinátája a pontnak.
     */

    public static ArrayList<Integer> getPossiblecells(ArrayList<Integer> previouscells, int[][] board,int i, int j) {
        if(previouscells == null){
            previouscells = new ArrayList<Integer>();
        }
        ArrayList<Integer> nextcells = new ArrayList<>(previouscells);
        for(int k = -1 ; k<=1; ++k){
            for( int l = -1 ; l<=1; ++l){
                int move_i = wallchecker(i+k);
                int move_j = wallchecker(j+l);
                if(board[move_i][move_j] == 2 && !nextcells.contains(((move_i)*100)+move_j) && !(k==0 && l==0)) {
                    nextcells.add(((move_i)*100)+move_j);
                }
            }
            if(nextcells.contains((i*100)+j)){
                nextcells.remove(nextcells.indexOf((i*100)+j));
            }
        }
        return nextcells;
    }

    /**
     * A paraméterben kapott táblát másolja le.
     * @param board A valós tábla állás.
     * @return
     */

    public static int[][] boardcopy(int[][] board) {

        int[][] newboard = new int[15][15];
        for (int i = 0; i < board.length; i++)
            newboard[i] = board[i].clone();

        return newboard;
    }

    /**
     * Tárolja a táblán a pontot int formában. pl. 3 5 pont -> 305 , 15 11 pont -> 1511.
     * @param i Az i értéke *100.
     * @param j A j értéke *1.
     * @return
     */

    public static int indexvalue(int i,int j){
        return  i*100 + j;
    }

    //sor i / 100
    public static int row(int koordinata){
        return koordinata / 100;
    }
    //oszlop j / 100
    public static int column(int koordinata){
        return koordinata % 100;
    }


    /**
     * Megnézi, hogy az a koordináta amit kap kívűl esik-e a táblából.
     * @param szam
     * @return
     */
    public static int wallchecker(int szam){
        if(szam < 0){
            szam = 15+szam;
        }
        if(szam > 14){
            szam = szam-15;
        }
        return szam;
    }


    /**
     * A kapott tábla és a pontok alapján létrehoz egy area-t, ami ahhoz kell, hogy
     * megvizsgáljuk az abból a pontbol szerezhető érték nagyságát.
     *
     * @param board
     * @param i
     * @param j
     * @return
     */
    public static int[][] get_area(int[][] board,int i,int j){
        int start_i = i-4;
        int start_j = j-4;

        int[][] area = new int[9][9];

        for (int a = 0; a < 9; ++a){
            for (int b = 0; b < 9; ++b){
                start_i = wallchecker(start_i);
                start_j = wallchecker(start_j);

                area[a][b] = board[start_i][start_j];
                start_j++;

            }
            start_j = j-4;
            start_i++;
        }
        return area;
    }

     /*
       1. S_darab*10 + (Sorban mennyi van belőle?) 10^S_darab +- (Levanezarva?) 40
       2. E_darab*10 + (Sorban mennyi van belőle?) 10^E_darab +- (Levanezarva?) 40

       Heurisztika : 1. + 2.
     */

    public static int pow(int darab){
        if(darab == 0){
         return 0;
        }
        return (int) Math.pow(10,darab);
    }

    /**
     * Ez a függvény számol egy pontot a saját jeleink, az ellenfél jelei,
     * és a két oldal zártsága alapján.
     * @param s_darab A mi jeleink darabszáma.
     * @param e_darab   Az ellenfél jeleinek darabszáma.
     * @param isLeftClosed  Balról zárt-e?
     * @param isRightClosed Jobbról zárt-e?
     * @return
     */

    public static int calculate(int s_darab,int e_darab,boolean isLeftClosed, boolean isRightClosed){
       /* if(s_darab == 0 && e_darab == 0 && isLeftClosed && isRightClosed){
            return -40;
        }*/
        int elso = (s_darab*10 + pow(s_darab));
        //best
        if(!isLeftClosed && !isRightClosed && s_darab > 0){
            elso +=40;
        }
        //worst
      /*  if(isLeftClosed && isRightClosed ){
            elso-=40;
        }
        if(isLeftClosed && !isRightClosed && s_darab > 0){
            elso+=20;
        }
        if(isRightClosed && !isLeftClosed && s_darab > 0){
            elso+=20;
        }*/


        int masodik = (e_darab*10 + pow(e_darab));
        if(!isLeftClosed && !isRightClosed && e_darab > 0){
            masodik +=40;
        }
       /* if(isLeftClosed && isRightClosed ){
            masodik-=40;
        }
        if(isLeftClosed && !isRightClosed && e_darab > 0){
            masodik+=20;
        }
        if(isRightClosed && !isLeftClosed && e_darab > 0){
            masodik+=20;
        }*/
        return elso + masodik;
    }

    /**
     * Vízszintesen vizsgálja miből mennyi van, illetve hogy le van-e zárva?
     * @param area  A kapott area közepétől vizsgálja meg.
     * @param color Adott játékos jele.
     * @return
     */


    //Horizontal
    //
    public static int horizontal(int[][] area, int color){
        int S_darab = 0;
        int E_darab = 0;
        boolean isRightClose = false;
        boolean isLeftClose = false;


        //Balra
        //
        for(int i = 1; i <=4; ++i){

            if(area[4][4-i] == 1-color && !isLeftClose){
                E_darab++;
            }
            if(area[4][4-i] == color && !isLeftClose){
                S_darab++;
            }
            if(i<4 && area[4][4-i] != area[4][4-(i+1)] && area[4][4-i] !=2 && area[4][4-(i+1)] != 2){
                isLeftClose = true;
            }
            if(i<3 && area[4][4-i] == 3 ){
                isLeftClose = true;
                break;
            }

        }
        //Jobbra
        //
        for(int i = 1; i <=4; ++i){
            if(area[4][4+i] == 1-color && !isRightClose){
                E_darab++;
            }
            if(area[4][4+i]  == color && !isRightClose){
                S_darab++;
            }
            if(i<4 && area[4][4+i] != area[4][4+i+1] && area[4][4+i] != 2 && area[4][4+i+1] != 2){
                isRightClose = true;
            }
            if(i<3 && area[4][4+i] == 3 ){
                isRightClose = true;
                break;
            }
        }
        boolean isClosed = isLeftClose || isRightClose;
        if(!isClosed && S_darab == 0 && E_darab == 0){
            return 0;
        }
        if(E_darab == 4 ){
            return 555555555;
        }
        if(S_darab == 4){
            return 555555555;
        }

        if(area[4][3] != area[4][5] && area[4][3] !=2 && area[4][5] != 2) {
            if(area[4][3] == color){
                isRightClose = true;
            } else if(area[4][5] == color){
                isLeftClose = true;
            } else {
                isLeftClose = true;
                isRightClose = true;
            }
        }

        if(E_darab == 3 && !isClosed && S_darab !=4){
            return 40000;
        }
        if(S_darab == 3 && !isClosed && E_darab !=4){
            return 40000;
        }

        return calculate(S_darab,E_darab,isLeftClose,isRightClose);
    }

    /**
     * Függölegesen vizsgálja miből mennyi van, illetve hogy le van-e zárva?
     * @param area  A kapott area közepétől vizsgálja meg.
     * @param color Adott játékos jele.
     * @return
     */

    //Vertical
    //
    //
    public static int vertical(int[][] area, int color){
        int S_darab = 0;
        int E_darab = 0;
        boolean isUpClose = false;
        boolean isDownClose = false;

        //Fel
        //
        for(int j = 1; j <=4; ++j){

            if(area[4-j][4] == 1-color && !isUpClose){
                E_darab++;
            }
            if(area[4-j][4] == color && !isUpClose){
                S_darab++;
            }
            if( j<3 &&  area[4-j][4] != area[4-(j+1)][4] && area[4-j][4]!=2 && area[4-(j+1)][4] != 2){
                isUpClose = true;
            }
            if(j< 3 && area[4-j][4] == 3 ){
                isUpClose = true;
                break;
            }
        }
        //Le
        //
        for(int j = 1; j <=4; ++j){
            if(area[4+j][4] == 1-color && !isDownClose){
                E_darab++;
            }
            if(area[4+j][4] == color && !isDownClose){
                S_darab++;
            }
            if( j<3 && area[4+j][4] != area[4+j+1][4] && area[4+j][4] !=2 && area[4+j+1][4] != 2){
                isDownClose = true;
            }
            if(j<3 && area[4+j][4] == 3 ){
                isDownClose = true;
                break;
            }
        }
        boolean isClosed = isDownClose || isUpClose;
        if(!isClosed && S_darab == 0 && E_darab == 0){
            return 0;
        }
        if(E_darab == 4 ){
            return 555555555;
        }
        if(S_darab == 4){
            return 555555555;
        }

        if(area[3][4] != area[5][4] && area[3][4] !=2 && area[5][4] != 2) {
            if(area[3][4] == color){
                isUpClose = true;
            } else if(area[5][4] == color){
                isDownClose = true;
            } else {
                isDownClose = true;
                isUpClose = true;
            }
        }

        if(E_darab == 3 && !isClosed && S_darab !=4){
            return 40000;
        }
        if(S_darab == 3 && !isClosed && E_darab !=4){
            return 40000;
        }

        return calculate(S_darab,E_darab,isDownClose,isUpClose);
    }

    /**
     * Átlósan vizsgálja miből mennyi van, illetve hogy le van-e zárva?
     * @param area  A kapott area közepétől vizsgálja meg.
     * @param color Adott játékos jele.
     * @return
     */

    //Diagonal
    //
    //
    public static int diagonal(int[][] area, int color){
        int S_darab = 0;
        int E_darab = 0;
        boolean isDDClose = false;
        boolean isDUClose = false;

        //Diagonal-Down
        //
        for(int i = 1; i <=4; ++i){

            if(area[4+i][4-i] == 1-color && !isDDClose){
                E_darab++;
            }
            if(area[4+i][4-i] == color && !isDDClose){
                S_darab++;
            }
            if(i<4 && area[4+i][4-i] != area[4+(i+1)][4-(i+1)] && area[4+i][4-i]!=2 && area[4+(i+1)][4-(i+1)] != 2){
                isDDClose = true;
            }
            if(i<3 && area[4+i][4-i] == 3 ){
                isDDClose = true;
                break;
            }
        }
        //Diagonal-Up
        //
        for(int i = 1; i <=4; ++i){

            if(area[4-i][4+i] == 1-color && !isDUClose){
                E_darab++;
            }
            if(area[4-i][4+i] == color && !isDUClose){
                S_darab++;
            }
            if(i<4 &&  area[4-i][4+i] != area[4-(i+1)][4+(i+1)] && area[4-i][4+i]!=2 && area[4-(i+1)][4+(i+1)] != 2){
                isDUClose = true;
            }
            if(i<3 && area[4-i][4+i] == 3 ){
                isDUClose = true;
                break;
            }
        }
        boolean isClosed = isDDClose || isDUClose;
        if(!isClosed && S_darab == 0 && E_darab == 0){
            return 0;
        }
        if(E_darab == 4 ){
            return 555555555;
        }
        if(S_darab == 4){
            return 555555555;
        }

        if(area[3][5] != area[5][3] && area[3][5] !=2 && area[5][3] != 2) {
            if(area[3][5] == color){
                isDUClose = true;
            } else if(area[5][3] == color){
                isDDClose = true;
            } else {
                isDDClose = true;
                isDUClose = true;
            }
        }

        if(E_darab == 3 && !isClosed && S_darab !=4){
            return 40000;
        }
        if(S_darab == 3 && !isClosed && E_darab !=4){
            return 40000;
        }

        return calculate(S_darab,E_darab,isDDClose,isDUClose);
    }

    /**
     * Anti-átlósan vizsgálja miből mennyi van, illetve hogy le van-e zárva?
     * @param area  A kapott area közepétől vizsgálja meg.
     * @param color Adott játékos jele.
     * @return
     */

    //Anti-Diagonal
    //
    //
    public static int antidiagonal(int[][] area, int color){
        int S_darab = 0;
        int E_darab = 0;
        boolean isADUClose = false;
        boolean isADDClose = false;

        //Anti-Diagonal-Up
        //
        for(int i = 1; i <=4; ++i){

            if(area[4-i][4-i] == 1-color && !isADUClose){
                E_darab++;
            }
            if(area[4-i][4-i] == color && !isADUClose){
                S_darab++;
            }
            if(i<4 &&  area[4-i][4-i] != area[4-(i+1)][4-(i+1)] && area[4-i][4-i]!=2 && area[4-(i+1)][4-(i+1)] != 2){
                isADUClose = true;
            }
            if(i<3 && area[4-i][4-i] == 3 ){
                isADUClose = true;
                break;
            }
        }

        //Anti-Diagonal-Down
        //
        for(int i = 1; i <=4; ++i) {

            if (area[4 + i][4 + i] == 1 - color && !isADDClose) {
                E_darab++;
            }
            if (area[4 + i][4 + i] == color && !isADDClose) {
                S_darab++;
            }
            if (i<4 &&  area[4 + i][4 + i] != area[4 + (i + 1)][4 + (i + 1)] && area[4 + i][4 + i]!=2 && area[4 + (i + 1)][4 + (i + 1)] != 2) {
                isADDClose = true;
            }
            if (i<3 && area[4 + i][4 + i] == 3) {
                isADDClose = true;
                break;
            }
        }
        boolean isClosed = isADDClose || isADUClose;
        if(!isClosed && S_darab == 0 && E_darab == 0){
            return 0;
        }
        if(E_darab == 4 ){
            return 555555555;
        }
        if(S_darab == 4){
            return 555555555;
        }

        if(area[3][3] != area[5][5] && area[3][3] !=2 && area[5][5] != 2) {
            if(area[3][3] == color){
                isADUClose = true;
            } else if(area[5][5] == color){
                isADDClose = true;
            } else {
                isADUClose = true;
                isADDClose = true;
            }
        }

        if(E_darab == 3 && !isClosed && S_darab !=4){
            return 40000;
        }
        if(S_darab == 3 && !isClosed && E_darab !=4){
            return 40000;
        }

        return calculate(S_darab,E_darab,isADDClose,isADUClose);
    }

    /**
     * Ad egy értéket vissza, hogy az adott pont mennyire lehet jó.
     * Meghívja rá a 4 irány-t. Horizontal vertical diag antidiag.
     * @param area Az area.
     * @param color A játékos jele.
     * @return
     */

    public static int heuristic(int[][] area,int color) {

        //Hori_heu
        //
        int horizontal = horizontal(area,color);
        if(horizontal == 555555555){
            return horizontal;
        }
        //Vertical
        //
        int vertical = vertical(area,color);
        if(vertical == 555555555){
            return vertical;
        }
        //Diagonal
        //
        int diagonal = diagonal(area,color);
        if(diagonal == 555555555){
            return diagonal;
        }
        //Antidiagonal
        //
        int antidiagonal = antidiagonal(area,color);
        if(antidiagonal == 555555555){
            return antidiagonal;
        }
        return (horizontal + vertical + diagonal + antidiagonal);
    }

}

class Result {
    public int i,j;
    public int score;

    public Result(int i, int j, int score) {
        this.i = i;
        this.j = j;
        this.score = score;
    }

}