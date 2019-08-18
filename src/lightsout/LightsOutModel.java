package lightsout;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * @author Ping Cheng Chung
 *
 */
/**
 * Represents the state of a Connect Four board on which multiple games can be played. The board consists of a
 * rectangular grid of squares in which playing pieces can be placed. Squares are identified by their zero-based row and
 * column numbers, where rows are numbered starting with the bottom row, and columns are numbered by starting from the
 * leftmost column.
 * 
 * Multiple games can be played on a single board. Whenever a new game begins, the board is empty. There are two
 * players, identified by the constants P1 (Player 1) and P2 (Player 2). P1 moves first in the first game, P2 moves
 * first in the second game, and so on in alternating fashion.
 * 
 * A C4Board also keeps track of the outcomes of the games that have been played on it. It tracks the number of wins by
 * P1, the number of wins by P2, and the number of ties. It does not track games that were abandoned before being
 * completed.
 */

public class LightsOutModel
{
    /**
     * the answer model
     */
    private int[][] aimboard;
    /** the turn of each game */
    private int turncount = 0;
    /**
     * if it is AI moving return true
     */
    private boolean AImoving = false;
    /**
     * restore the question for restart
     */
    private int[][] SavethefirstBoard;
    /** The number used to indicate light */
    public final int TurnOn = 1;

    /** The number used to indicate dark */
    public final int TurnOff = 0;

    public final int WIN = 1;

    /**
     * columns of the board
     */
    private int Boardcol;
    /** rows of the board */
    private int Boardrow;
    /** a two dimension arrays to simulate the board */
    private int[][] recoBoard;
    /** how many times p1 won */
    private int p1win = 0;
    /**
     * help to find the solution
     */
    private ArrayList<int[]> KeyArray;
    /**
     * help to find the solution
     */
    private ArrayList<String> supportKey;
    /**
     * the minimum moves to solve the question
     */
    private int Minumsteps;
    /**
     * the solution for solving the question faster
     */
    private int[] FasterSolution;



    /**
     * Creates a Lights out board with the specified number of rows and columns. There should be no pieces on the board
     * and it should be player 1's turn to move.
     * 
     * 
     */
    public LightsOutModel (int rows, int cols)
    {

        if (rows < 2 || cols < 2)
        {
            throw new IllegalArgumentException("the boundaries are too small");
        }

        Boardcol = cols;
        Boardrow = rows;
        aimboard = new int[rows][cols];
        Minumsteps = 999;//initialize

        // get the all probable move in 1st row
        KeyArray = new ArrayList<>();
        supportKey = new ArrayList<>();
        KeyFirstRow(cols);
        recoBoard = CreatVaildBoard();
    }

    /**
     * Sets up the board to play a new game, whether or not the current game is complete.
     */
    public void newGame ()
    {

        Minumsteps = 999;
        turncount = 0;
        // create a random board
        recoBoard = CreatVaildBoard();

    }

    /**
     * Renew the board with the same question
     */
    public void RestartGame ()
    {
        turncount = 0;
        // create a random board
        recoBoard = deepCopyIntMatrix(SavethefirstBoard);

    }

    /**
     * Records a move, by the player whose turn it is to move, in the first open square in the specified column. Returns
     * P1 if the move resulted in a win for player 1, returns P2 if the move resulted in a win for player 2, returns TIE
     * if the move resulted in a tie, and returns 0 otherwise.
     * 
     * If the column is full, or if the game is over because a win or a tie has previously occurred, does nothing but
     * return zero.
     * 
     * If a non-existent column is specified, throws an IllegalArgumentException.
     */
    public int moveTo (int row, int col)
    {

        Turn(row, col);
        turncount++;
        if (checkwin(recoBoard) && !AImoving)
        {
            p1win++;
            return WIN;
        }

        else
        {

            return 0;
        }
    }

    /**
     * in setting mode, change the light on the board independently
     */
    public void setlight (int row, int col)
    {
        recoBoard[row][col] = Math.abs(recoBoard[row][col] - 1);
    }

    /**
     * when exit setting mode, save the board for restart, and check if the board is solvable or not
     */
    public void Exitset ()
    {
        SavethefirstBoard = deepCopyIntMatrix(recoBoard);
        turncount = 0;
        FasterSolution = null;
        hasSolution(recoBoard);

    }

    /**
     * @param row
     * @param col change the tokens in the recoBoard
     */
    private void Turn (int row, int col)
    {
        ArrayList<Point> surrendingPosition = findSurroundingSquares(row, col);
        for (Point p : surrendingPosition)
        {
            recoBoard[p.x][p.y] = switchLight(recoBoard[p.x][p.y]);

        }
    }

    /**
     * @param light
     * @return if the number is 1 return 0, if the number is 0 return 1
     */
    private int switchLight (int light)
    {
        return (light == 0) ? 1 : 0;
    }

    /**
     * @param row
     * @param col
     * @return the position list which need to be swtich
     */
    private  ArrayList<Point> findSurroundingSquares (int row, int col)
    {
        ArrayList<Point> surroundingPoints = new ArrayList<>();
        surroundingPoints.add(new Point(row, col));
        if (row > 0)
        {
            surroundingPoints.add(new Point(row - 1, col));
        }
        if (row < Boardrow - 1)
        {
            surroundingPoints.add(new Point(row + 1, col));
        }
        if (col > 0)
            surroundingPoints.add(new Point(row, col - 1));
        if (col < Boardcol - 1)
            surroundingPoints.add(new Point(row, col + 1));

        return surroundingPoints;
    }

    /**
     * @return true if the palyer won
     */
    public boolean checkwin (int[][] board)
    {

        return (Arrays.deepEquals(board, aimboard)) ? true : false;
    }

    /**
     * Reports the occupant of the board square at the specified row and column. If the row or column doesn't exist,
     * throws an IllegalArgumentException. If the square is unoccupied, returns 0. Otherwise, returns P1 (if the square
     * is occupied by player 1) or P2 (if the square is occupied by player 2).
     */
    public int getOccupant (int row, int col)
    {
        // return 1;
        return recoBoard[row][col];
    }

    /**
     * @param board
     * @return whether the board is solvable or not
     */
    protected  boolean hasSolution (int[][] board)
    {
        Minumsteps = 999;
        for (int[] c : KeyArray)
        {
            int[][] fakeaim = deepCopyIntMatrix(board);
            int firstcounter = 0;
            for (int i = 0; i < Boardcol; i++)
            {
                if (c[i] == 1)
                {

                    fakeaim = Turn_2(Boardrow - 1, i, fakeaim);
                    firstcounter++;
                }
            }
            //
            int steps = HowManySteps(fakeaim, firstcounter);
            if (steps != -1)
            {
                if (steps < Minumsteps)
                {
                    Minumsteps = steps;
                    FasterSolution = c.clone();
                }
            }

        }
        System.out.println("Minsteps: " + Minumsteps);
        if (Minumsteps != 999)
        {
            for (int i = 0; i < Boardcol; i++)
            {
                System.out.print(FasterSolution[i] + " ");
            }
        }
        System.out.println("print board");
        for (int i = 0; i < Boardrow; i++)
        {
            for (int j = 0; j < Boardcol; j++)
            {
                System.out.print(board[i][j]);
            }
            System.out.println("");
        }

        return (Minumsteps == 999) ? false : true;
    }

    /**
     * @param row
     * @param col
     * @param board
     * @return turn on or off for testboard
     */
    private int[][] Turn_2 (int row, int col, int[][] board)
    {
        ArrayList<Point> surrendingPosition = findSurroundingSquares(row, col);
        for (Point p : surrendingPosition)
        {
            board[p.x][p.y] = switchLight(board[p.x][p.y]);
        }
        return board;
    }

    /**
     * @param board
     * @return -1 is insolvable, other number means how many steps.
     */
    private int HowManySteps (int[][] board, int firststeps)
    {
        int steps = 0;
        for (int i = Boardrow - 2; i >= 0; i--)
        {
            for (int j = 0; j < Boardcol; j++)
            {
                if (board[i + 1][j] == 1)
                {
                    board = Turn_2(i, j, board);
                    steps++;
                }
            }
        }

        return (Arrays.deepEquals(board, new int[Boardrow][Boardcol])) ? steps + firststeps : -1;
    }

    /**
     * @param input
     * @return a new object of the copy
     */
    protected int[][] deepCopyIntMatrix (int[][] input)
    {
        if (input == null)
            return null;
        int[][] result = new int[input.length][];
        for (int r = 0; r < input.length; r++)
        {
            result[r] = input[r].clone();
        }
        return result;
    }

    /**
     * check it is solvable or not
     */
    private void KeyFirstRow (int col)
    {

        int[] c = new int[col];
        for (int i = 0; i < col; i++)
        {
            c[i] = Math.abs(c[i] - 1);

            for (int j = 0; j < col; j++)
            {
                c[j] = Math.abs(c[j] - 1);

                for (int k = 0; k < col; k++)
                {
                    c[k] = Math.abs(c[k] - 1);
                    for (int l = 0; l < col; l++)
                    {
                        c[l] = Math.abs(c[l] - 1);
                        for (int m = 0; m < col; m++)
                        {
                            c[m] = Math.abs(c[m] - 1);
                            if (isNew(c, col))
                            {
                                KeyArray.add(c.clone());
                            }
                        }
                    }
                }
            }
        }

    }

    /**
     * if the array is a new one return true
     */
    private boolean isNew (int[] array, int col)
    {
        String ans = "";
        for (int i = 0; i < col; i++)
        {
            if (array[i] == 1)
            {
                ans = ans + i;
            }
        }
        if (supportKey.contains(ans))
        {
            return false;
        }

        else
        {
            supportKey.add(ans);
            return true;
        }
    }

    /**
     * @return a board with valid question
     */
    private int[][] CreatVaildBoard ()
    {
        Random random = new Random();
        int[][] board = new int[Boardrow][Boardcol];

        for (int i = 0; i < Boardrow; i++)
        {
            for (int j = 0; j < Boardcol; j++)
            {
                if (random.nextInt(2) == 1)
                {
                    board = Turn_2(i, j, board);
                }
            }
        }
        hasSolution(board);
        SavethefirstBoard = deepCopyIntMatrix(board);
        return board;
    }

    /**
     * report the faster solution
     */
    public int[] getFasterSolution ()
    {

        return FasterSolution.clone();
    }

    /**
     * report the record board
     */
    public int[][] getRecordBoard ()
    {

        return deepCopyIntMatrix(recoBoard);
    }

    /**
     * report the minimum steps to solve the question
     */
    public String getTargetMove ()
    {

        return (Minumsteps == 999) ? "-" : Minumsteps + "";
    }

    /**
     * Reports how many games have been won by player 1 since this board was created.
     */
    public int getWinsForP1 ()
    {
        return p1win;
    }

    /**
     * Reports how many games have been won by player 2 since this board was created.
     */
    public int getHowmanyMoves ()
    {
        return turncount;
    }

    public void startAImoving (boolean status)
    {
        AImoving = status;
    }

}
