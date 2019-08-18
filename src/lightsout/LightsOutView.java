package lightsout;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import minesweeper.MinesweeperView;
import static lightsout.LightsOutModel.*;
import static lightsout.LightsOutView.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

/**
 * Implements a Lights out game with a GUI interface.
 * 
 * @author Ping Cheng Chung
 */
@SuppressWarnings("serial")
public class LightsOutView extends JFrame implements ActionListener
{
    /**
     * the time for AI-moving
     */
    private final int delaytime = 1000;
    private JLabel Pointer;
    private JButton AIbutton;
    private JButton Restart;
    private JButton newGame;
    // -1 means no pressing
    private int PressingRow = -1;
    private int PressingCol = -1;
    private Color controler_background = new Color(125, 204, 241);
    private JButton Setting;
    private JPanel p3;
    private JPanel root;
    /**
     * the minimum moves to solve the question
     */
    private JLabel Tmoves;
    /**
     * the size of the board e.g. 5 x 5  or 5x6
     */
    private JLabel Sizes;
    // Some formatting constants
    private final int WIDTH = 750;
    private final int HEIGHT = 700;
    public int ROWS = 5;
    public int COLS = 5;
    public final Color Line_COLOR = Color.BLACK;
    public final Color TurnOn_COLOR = Color.WHITE;
    public final Color TurnOff_COLOR = new Color(110, 111, 109);

    /**
     * 0 is not end game 1 is the game end 2 is setting or system processing
     */
    public int Game_state = 0;
    //
    public final Color BACKGROUND_COLOR = Color.GRAY;

    public final String P1_COLOR_NAME = "Red";

    public final String P2_COLOR_NAME = "Yellow";
    public final Color BOARD_COLOR = Color.white;
    public final Color TIE_COLOR = Color.WHITE;
    public final int BORDER = 5;
    public final int FONT_SIZE = 20;

    /** The "smarts" of the game **/
    private LightsOutModel model;

    /** The number of games that player 1 has won **/
    private JLabel p1Wins;

    /** The number of games that player 2 has won **/
    private JLabel turnMove;

    /** The portion of the GUI that contains the playing surface **/
    private Board board;

    /**
     * Creates and initializes the game.
     */
    public LightsOutView ()
    {
        // Set the title that appears at the top of the window
        setTitle("CS1410 Lights Out");
        // Cause the application to end when the windows is closed
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Give the window its initial dimensions
        setSize(WIDTH, HEIGHT);

        // The root panel contains everything
        root = new JPanel();
        root.setLayout(new BorderLayout());
        setContentPane(root);

        // The center portion contains the playing board
        model = new LightsOutModel(ROWS, COLS);
        board = new Board(model, this);
        root.add(board, "Center");

        // The top portion contains the scores
        JPanel scores = new JPanel();
        scores.setLayout(new BorderLayout());
        root.add(scores, "North");

        // Score and indicator for the first player
        JPanel p1 = new JPanel();
        p1.setBackground(BACKGROUND_COLOR);
        p1Wins = new JLabel("Wins: 0");
        p1Wins.setFont(new Font("SansSerif", Font.BOLD, FONT_SIZE));
        p1Wins.setForeground(TurnOn_COLOR);
        p1Wins.setBorder(new EmptyBorder(0, BORDER, 0, BORDER));
        p1.add(p1Wins);
        // Target moves label
        Tmoves = new JLabel("Target moves: 0");
        Tmoves.setFont(new Font("SansSerif", Font.BOLD, FONT_SIZE));
        Tmoves.setForeground(TurnOn_COLOR);
        Tmoves.setBorder(new EmptyBorder(0, BORDER, 0, BORDER));
        p1.add(Tmoves);
        scores.add(p1, "West");

        // setting button

        Setting = new JButton("Enter Manual Setup");
        Setting.setFont(new Font("SansSerif", Font.BOLD, FONT_SIZE));
        Setting.setForeground(Color.red);
        Setting.setBackground(Color.BLACK);
        Setting.addActionListener(this);
        Setting.setActionCommand("set");
        scores.add(Setting, "Center");

        // Score and indicator for the second player
        JPanel p2 = new JPanel();
        p2.setBackground(BACKGROUND_COLOR);
        // p2Indicator = new MoveIndicator();
        // p2.add(p2Indicator);
        turnMove = new JLabel("Moves: 0");
        turnMove.setFont(new Font("SansSerif", Font.BOLD, FONT_SIZE));
        turnMove.setForeground(Color.BLACK);
        turnMove.setBorder(new EmptyBorder(0, BORDER, 0, BORDER));
        p2.add(turnMove);
        scores.add(p2, "East");

        // The bottom portion contains the New Game button
        newGame = new JButton("New Game");
        newGame.setFont(new Font("SansSerif", Font.BOLD, FONT_SIZE));
        newGame.setForeground(TIE_COLOR);
        newGame.setBackground(BACKGROUND_COLOR);
        newGame.addActionListener(this);
        newGame.setActionCommand("new");
        // we are gonnaput two button in this panel then put in root.
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 3));
        buttonPanel.add(newGame);

        // Restart Button
        Restart = new JButton("Restart");
        Restart.setFont(new Font("SansSerif", Font.BOLD, FONT_SIZE));
        Restart.setForeground(Color.red);
        Restart.setBackground(Color.black);
        Restart.addActionListener(this);
        Restart.setActionCommand("Restart");
        buttonPanel.add(Restart);

        // AI help button
        AIbutton = new JButton("AI Support");
        AIbutton.setFont(new Font("SansSerif", Font.BOLD, FONT_SIZE));
        AIbutton.setForeground(Color.red);
        AIbutton.setBackground(Color.black);
        AIbutton.addActionListener(this);
        AIbutton.setActionCommand("AIhelp");
        buttonPanel.add(AIbutton);

        //
        root.add(buttonPanel, "South");

        // add button on right and left side
        p3 = new JPanel();
        p3.setLayout(new GridLayout(7, 1));
        Image imgup = null;
        Image imgdown = null;
        Image imgR = null;
        Image imgL = null;
        try
        {
            imgup = ImageIO.read(getClass().getResource("up.png"));
            imgdown = ImageIO.read(getClass().getResource("down.png"));
            imgR = ImageIO.read(getClass().getResource("right.png"));
            imgL = ImageIO.read(getClass().getResource("left.png"));

        }
        catch (IOException e)
        {
            System.out.println("image has problem");
        }
        JButton uprow = new JButton(scaleImage(imgup, 2));
        uprow.setBackground(controler_background);
        uprow.addActionListener(this);
        uprow.setActionCommand("uprow");
        p3.add(uprow);

        JButton downrow = new JButton(scaleImage(imgdown, 2));
        downrow.setBackground(controler_background);
        downrow.addActionListener(this);
        downrow.setActionCommand("downrow");
        p3.add(downrow);

        JButton expandcol = new JButton(scaleImage(imgL, 2));
        expandcol.setBackground(controler_background);
        expandcol.addActionListener(this);
        expandcol.setActionCommand("expandcol");
        p3.add(expandcol);

        JButton reducecol = new JButton(scaleImage(imgR, 2));
        reducecol.setBackground(controler_background);
        reducecol.addActionListener(this);
        reducecol.setActionCommand("reducecol");
        p3.add(reducecol);

        Sizes = new JLabel("5 x 5");
        Sizes.setFont(new Font("SansSerif", Font.BOLD, 18));
        Sizes.setForeground(Color.BLACK);
        Sizes.setBorder(new EmptyBorder(19, 10, 19, 5));
        p3.add(Sizes);

        Pointer = new JLabel("!");
        Pointer.setFont(new Font("SansSerif", Font.BOLD, 50));
        Pointer.setForeground(Color.RED);

        Pointer.setBorder(new EmptyBorder(10, 10, 10, 10));
        // p3.add(Pointer);

        // Refresh the display and we're ready
        board.refresh();
        setVisible(true);
    }

    /**
     * resize the image
     */
    private ImageIcon scaleImage (Image image, double scale)
    {
        image = image.getScaledInstance((int) (15 * scale), (int) (15 * scale), java.awt.Image.SCALE_SMOOTH);
        return new ImageIcon(image);
    }

    /**
     * Sets the label that displays target moves
     */
    public void setSziestext ()
    {
        Sizes.setText(ROWS + " x " + COLS);

    }

    /**
     * Sets the label that displays target moves
     */
    public void setTargetmoves (String n)
    {
        Tmoves.setText("Target moves: " + n);

    }

    /**
     * Sets the label that displays player 1's win count
     */
    public void setWinsForP1 (int n)
    {
        p1Wins.setText("Wins: " + n);

    }

    /**
     * Sets the label that displays player 2's win count
     */
    public void setMovesForP2 (int n)
    {
        turnMove.setText("Moves: " + n);
    }

    /**
     * Called when the New Game button is clicked. Tells the model to begin a new game, then refreshes the display.
     */
    @Override
    public void actionPerformed (ActionEvent e)
    {
        if (Game_state == 0 || Game_state == 1)
        {
            if (e.getActionCommand().equals("new"))
            {

                model.newGame();
                Game_state = 0;
                board.refresh();
            }
            else if (e.getActionCommand().equals("goback"))
            {

                Game_state = 0;

                root.remove(board);
                root.remove(p3);
                ROWS = 3;
                COLS = 3;
                model = new LightsOutModel(ROWS, COLS);
                board = new Board(model, this);
                root.add(board, "Center");
                // root.add(p3,"West");
                root.validate();
                board.refresh();

                // model = new LightsOutModel(ROWS, COLS);
                // board = new Board(model, this);
                // board.refresh();
                // model.undo();
                // board.refresh();
            }
            else if (e.getActionCommand().equals("Restart"))
            {
                model.RestartGame();
                Game_state = 0;
                board.refresh();

            }
            else if (e.getActionCommand().equals("AIhelp"))
            {

                if (model.hasSolution(model.getRecordBoard()))
                {
                    model.startAImoving(true);
                    Game_state = 3;
                    new Thread(new AIMOVE(model)).start();
                    newGame.setEnabled(false);
                    Restart.setEnabled(false);
                    AIbutton.setEnabled(false);
                    Setting.setEnabled(false);

                }
                else
                {
                    JOptionPane.showMessageDialog(this, "The Solution does not exist.");
                }

            }
            // setting mode
            else if (e.getActionCommand().equals("set"))
            {

                Game_state = 2;
                newGame.setEnabled(false);
                Restart.setEnabled(false);
                AIbutton.setEnabled(false);
                Setting.setText("Exit Manual Set");
                root.add(p3, "West");
                root.validate();
                // board.refresh();

            }
        }

        else if (Game_state == 2)
        {// leave setting mode
            if (e.getActionCommand().equals("set"))
            {
                Game_state = 0;
                Setting.setText("Enter Manual Setup");
                model.Exitset();
                root.remove(p3);
                newGame.setEnabled(true);
                Restart.setEnabled(true);
                AIbutton.setEnabled(true);
                board.refresh();
            }
            else if (e.getActionCommand().equals("uprow"))
            {
                if (ROWS < 13)
                {
                    ROWS++;
                    root.remove(board);
                    model = new LightsOutModel(ROWS, COLS);
                    board = new Board(model, this);
                    root.add(board);
                    // root.validate();
                    board.refresh();
                }
                else
                {
                    JOptionPane.showMessageDialog(this, "the rows should be less than 13");

                }

            }
            else if (e.getActionCommand().equals("downrow"))
            {
                if (ROWS > 2)
                {
                    ROWS--;
                    root.remove(board);
                    model = new LightsOutModel(ROWS, COLS);
                    board = new Board(model, this);
                    root.add(board);
                    board.refresh();
                    // JOptionPane.showMessageDialog(this,ex.getMessage());
                    // root.validate();
                }
                else
                {
                    JOptionPane.showMessageDialog(this, "the rows should be more than 1");
                }

            }
            else if (e.getActionCommand().equals("reducecol"))
            {
                if (COLS > 2)
                {
                    COLS--;
                    root.remove(board);
                    model = new LightsOutModel(ROWS, COLS);
                    board = new Board(model, this);
                    root.add(board);
                    board.refresh();
                    // JOptionPane.showMessageDialog(this,ex.getMessage());
                    // root.validate();
                }
                else
                {
                    JOptionPane.showMessageDialog(this, "the columns should be more than 1");
                }

            }

            else if (e.getActionCommand().equals("expandcol"))
            {
                if (COLS < 13)
                {
                    COLS++;
                    root.remove(board);
                    model = new LightsOutModel(ROWS, COLS);
                    board = new Board(model, this);
                    root.add(board);
                    // root.validate();
                    board.refresh();
                }
                else
                {
                    JOptionPane.showMessageDialog(this, "the columns should be less than 13");

                }

            }

        }

    }

    /**
     * The playing surface of the game.
     */
    @SuppressWarnings("serial")
    class Board extends JPanel implements MouseListener
    {
        /** The "smarts" of the game */
        private LightsOutModel model;

        /** The top level GUI for the game */
        private LightsOutView display;

        /**
         * Creates the board portion of the GUI.
         */
        public Board (LightsOutModel model, LightsOutView display)
        {
            // Record the model and the top-level display
            this.model = model;
            this.display = display;

            // Set the background color and the layout
            // setBackground(BOARD_COLOR);
            setLayout(new GridLayout(ROWS, COLS));

            // Create and lay out the grid of squares that make up the game.
            for (int i = ROWS - 1; i >= 0; i--)
            {
                for (int j = 0; j < COLS; j++)
                {
                    Square s = new Square(i, j);
                    s.addMouseListener(this);
                    add(s);
                }
            }
        }

        /**
         * Refreshes the display. This should be called whenever something changes in the model.
         */
        public void refresh ()
        {
            // Iterate through all of the squares that make up the grid
            Component[] squares = getComponents();
            for (Component c : squares)
            {
                Square s = (Square) c;
                if (s.getRow() != PressingRow || s.getCol() != PressingCol)
                {
                    // Set the color of the squares appropriately
                    int status = model.getOccupant(s.getRow(), s.getCol());
                    if (status == 1)
                    {
                        s.setColor(TurnOn_COLOR);
                    }
                    else if (status == 0)
                    {
                        s.setColor(TurnOff_COLOR);
                    }
                    else
                    {
                        // s.setColor(BACKGROUND_COLOR);
                        System.out.println("unexpected error");
                    }
                }
                else
                {// special case, display ! in the grid which will be clicked
                    int status = model.getOccupant(s.getRow(), s.getCol());

                    if (status == 1)
                    {
                        s.setColor(TurnOn_COLOR);
                    }
                    else if (status == 0)
                    {
                        s.setColor(TurnOff_COLOR);
                    }

                    s.add(Pointer, "Center");
                    root.validate();
                    repaint();
                    try
                    {
                        Thread.sleep(delaytime);
                    }
                    catch (InterruptedException ex)
                    {
                    }
                    s.remove(Pointer);
                    root.validate();
                }

            }

            // Set the indicators according to whose move it is

            // Update the win and tie counts
            display.setWinsForP1(model.getWinsForP1());
            display.setMovesForP2(model.getHowmanyMoves());
            display.setTargetmoves(model.getTargetMove());
            display.setSziestext();
            // Ask that this board be repainted
            repaint();
        }

        /**
         * when AI click the board, call this method 
         */
        public void AIclick (int row, int col)
        {
            PressingRow = row;
            PressingCol = col;
            refresh();
            PressingRow = -1;
            PressingCol = -1;
            model.moveTo(row, col);
            refresh();

        }

        /**
         * Called whenever a Square is clicked. Notifies the model that a move has been attempted.
         */
        @Override
        public void mouseClicked (MouseEvent e)
        {

        }

        @Override
        public void mousePressed (MouseEvent e)
        {
            if (Game_state == 0)
            {
                Square s = (Square) e.getSource();
                Game_state = model.moveTo(s.getRow(), s.getCol());
                refresh();
                if (Game_state == 1)
                {
                    JOptionPane.showMessageDialog(this, " wins!");
                }
            }
            else if (Game_state == 1)
            {
                System.out.println("the game is done, plz start with a new game");
                JOptionPane.showMessageDialog(this, "the game is done, plz start with a new game");
                // do nothing JOptionPane.showMessageDialog(this, "do nothiing");
            }
            else if (Game_state == 2)
            {
                Square s = (Square) e.getSource();

                model.setlight(s.getRow(), s.getCol());
                refresh();
            }
            /*
             * Square s = (Square) e.getSource(); PressingCol=s.getCol(); PressingRow=s.getRow(); refresh();
             */

        }

        @Override
        public void mouseReleased (MouseEvent e)
        {

        }

        @Override
        public void mouseEntered (MouseEvent e)
        {
        }

        @Override
        public void mouseExited (MouseEvent e)
        {
        }
    }

    /**
     * A single square on the board where a move can be made
     */
    @SuppressWarnings("serial")
    class Square extends JPanel
    {
        /**
         * The row within the board of this Square. Rows are numbered from zero; row zero is at the bottom of the board.
         */
        private int row;

        /** The column within the board of this Square. Columns are numbered from zero; column zero is at the left */
        private int col;

        /** The current Color of this Square */
        private Color color;

        /**
         * Creates a square and records its row and column
         */
        public Square (int row, int col)
        {

            this.row = row;
            this.col = col;
            this.color = TurnOn_COLOR;
        }

        /**
         * Returns the row of this Square
         */
        public int getRow ()
        {
            return row;
        }

        /**
         * Returns the column of this Square
         */
        public int getCol ()
        {
            return col;
        }

        /**
         * Sets the color of this square
         */
        public void setColor (Color color)
        {
            this.color = color;
        }

        /**
         * Paints this Square onto g
         */
        @Override
        public void paintComponent (Graphics g)
        {

            ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            // g.setColor(BOARD_COLOR);
            // g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(color);
            g.fillRect(0, 0, getWidth(), getHeight());

            g.setColor(Line_COLOR);
            g.drawRect(0, 0, getWidth(), getHeight());

        }
    }

    /**
     * @author benny
     * use the second thread to update the repaint
     */
    public class AIMOVE implements Runnable
    {

        /** The "smarts" of the game */
        private LightsOutModel model;

        public AIMOVE (LightsOutModel model)
        {
            this.model = model;
        }

        @Override
        public void run ()
        {

            int[] firstkey;
            int[][] recoboard;
            recoboard = model.getRecordBoard();

            firstkey = model.getFasterSolution();
            // Step1
            for (int i = 0; i < firstkey.length; i++)
            {
                if (firstkey[i] == 1)
                {
                    board.AIclick(ROWS - 1, i);
                }
            }
            // Step2
            recoboard = model.getRecordBoard();
            for (int i = ROWS - 2; i >= 0; i--)
            {
                for (int j = 0; j < COLS; j++)
                {
                    if (model.getOccupant(i + 1, j) == 1)
                    {
                        board.AIclick(i, j);
                        // recoboard=model.getRecordBoard();
                    }
                }
            }

            newGame.setEnabled(true);
            Restart.setEnabled(true);
            AIbutton.setEnabled(true);
            Setting.setEnabled(true);
            Game_state = 1;
            model.startAImoving(false);

            SwingUtilities.invokeLater(new Runnable()
            {
                @Override
                public void run ()
                {
                    board.refresh();
                }
            });

        }

    }

    /**
     * A move indicator circle for use in the user interface.
     */
    @SuppressWarnings("serial")
    class MoveIndicator extends JPanel
    {
        // The color of this MoveIndicator
        private Color color;

        /**
         * Creates a MoveIndicator
         */
        public MoveIndicator ()
        {
            setPreferredSize(new Dimension(FONT_SIZE, FONT_SIZE));
            this.color = BACKGROUND_COLOR;
        }

        /**
         * Changes the color of this indicator
         */
        public void setColor (Color color)
        {
            this.color = color;
            repaint();
        }

        /**
         * Paints this MoveIndicator onto g
         */
        @Override
        public void paintComponent (Graphics g)
        {

            ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(BACKGROUND_COLOR);
            g.fillRect(0, 0, getHeight(), getHeight());
            g.setColor(color);
            g.fillOval(0, 0, getHeight(), getHeight());
        }
    }
}
