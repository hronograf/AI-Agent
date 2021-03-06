package search.environment.pacman;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Board extends JPanel implements ActionListener {
    // HERE YOU COULD CHANGE DELAY BETWEEN MOVES
    private final int MOVE_DELAY    = 50;

    private       boolean   lastMoveEaten = false;

    private       Dimension d;
    private final Font      smallFont = new Font("Helvetica", Font.BOLD, 14);

    private       Image ii;
    private final Color dotColor = new Color(192, 192, 0);
    private       Color mazeColor;

    private final int BLOCK_SIZE        = 24;
    private final int N_BLOCKS          = 15;
    private final int SCREEN_SIZE       = N_BLOCKS * BLOCK_SIZE;
    private final int PAC_ANIM_DELAY    = 2;
    private final int PACMAN_ANIM_COUNT = 4;
    private final int PACMAN_SPEED      = 1;

    private int               pacAnimCount  = PAC_ANIM_DELAY;
    private int               pacAnimDir    = 1;
    private int               pacmanAnimPos = 0;
    private int /*pacsLeft,*/ score;

    private Image pacman1, pacman2up, pacman2left, pacman2right, pacman2down;
    private Image pacman3up, pacman3down, pacman3left, pacman3right;
    private Image pacman4up, pacman4down, pacman4left, pacman4right;

    private int pacmand_x, pacmand_y;
    private int pacman_x = 7 * BLOCK_SIZE;
    private int pacman_y = 7 * BLOCK_SIZE;
    private int req_dx, req_dy, view_dx, view_dy;


    private boolean keyPressed = false;
    private int     oldPacmanX, oldPacmanY;
    private final int REPAINT_DELAY = 40;

    private final short levelData[] =
            {
                    19,26,26,26,18,26,22,0,19,26,18,26,26,26,22,
                    21,0,0,0,21,0,21,0,21,0,21,0,0,0,21,
                    21,0,0,0,21,0,17,18,20,0,21,0,0,0,21,
                    49,18,26,18,24,18,24,24,24,18,24,18,26,18,20,
                    17,20,0,21,0,21,0,0,0,21,0,21,0,17,20,
                    25,20,0,21,0,17,22,0,19,20,0,21,0,17,28,
                    0,21,0,21,0,25,16,18,16,28,0,21,0,21,0,
                    0,17,26,20,0,0,17,0,20,0,0,17,26,20,0,
                    0,21,0,21,0,19,16,24,16,22,0,21,0,21,0,
                    19,20,0,21,0,17,28,0,25,20,0,21,0,17,54,
                    17,20,0,21,0,21,0,0,0,21,0,21,0,17,20,
                    17,24,26,24,18,24,18,18,18,24,18,24,26,24,20,
                    21,0,0,0,21,0,17,24,20,0,21,0,0,0,21,
                    21,0,0,0,21,0,21,0,21,0,21,0,0,0,21,
                    57,26,26,26,24,26,28,0,25,26,24,26,26,26,28
            };


    private short[] screenData;
    private Timer   timer;

    public Board() {
        loadImages();
        initVariables();
        initBoard();
        initGame();
    }


    public boolean moveUp() {
        req_dx = 0;
        req_dy = -1;
        moveAndCheck();
        return wasMoved();
    }

    public boolean moveDown() {
        req_dx = 0;
        req_dy = 1;
        moveAndCheck();
        return wasMoved();
    }

    public boolean moveLeft() {
        req_dx = -1;
        req_dy = 0;
        moveAndCheck();
        return wasMoved();
    }

    public boolean moveRight() {
        req_dx = 1;
        req_dy = 0;
        moveAndCheck();
        return wasMoved();
    }

    private boolean wasMoved() {
        return oldPacmanX != pacman_x || oldPacmanY != pacman_y;
    }

    public int getPacmanX() {
        return pacman_x;
    }

    public int getPacmanY() {
        return pacman_y;
    }

    public int getBlocksAmount() {
        return N_BLOCKS;
    }

    public int getBlockSize() {
        return BLOCK_SIZE;
    }


    private void initBoard() {

        addKeyListener(new TAdapter());

        setFocusable(true);

        setBackground(Color.black);
    }

    private void initVariables() {

        screenData = new short[N_BLOCKS * N_BLOCKS];
        mazeColor = new Color(5, 100, 5);
        d = new Dimension(400, 400);

        timer = new Timer(REPAINT_DELAY, this);
        timer.start();
    }

    @Override
    public void addNotify() {
        super.addNotify();

        initGame();
    }

    private void doAnim() {

        pacAnimCount--;

        if (pacAnimCount <= 0) {
            pacAnimCount = PAC_ANIM_DELAY;
            pacmanAnimPos = pacmanAnimPos + pacAnimDir;

            if (pacmanAnimPos == (PACMAN_ANIM_COUNT - 1) || pacmanAnimPos == 0) {
                pacAnimDir = -pacAnimDir;
            }
        }
    }

    private void playGame(Graphics2D g2d) {
        drawPacman(g2d);
    }

    private void moveAndCheck() {
        oldPacmanX = pacman_x;
        oldPacmanY = pacman_y;
        movePacman();
        checkMaze();
        repaint();
        try {
            Thread.sleep(MOVE_DELAY);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void showIntroScreen(Graphics2D g2d) {

        g2d.setColor(new Color(0, 32, 48));
        g2d.fillRect(50, SCREEN_SIZE / 2 - 30, SCREEN_SIZE - 100, 50);
        g2d.setColor(Color.white);
        g2d.drawRect(50, SCREEN_SIZE / 2 - 30, SCREEN_SIZE - 100, 50);

        String      s     = "Press s to start.";
        Font        small = new Font("Helvetica", Font.BOLD, 14);
        FontMetrics metr  = this.getFontMetrics(small);

        g2d.setColor(Color.white);
        g2d.setFont(small);
        g2d.drawString(s, (SCREEN_SIZE - metr.stringWidth(s)) / 2, SCREEN_SIZE / 2);
    }

    private void drawScore(Graphics2D g) {
        String s;

        g.setFont(smallFont);
        g.setColor(new Color(96, 128, 255));
        s = "Score: " + score;
        g.drawString(s, SCREEN_SIZE / 2 + 96, SCREEN_SIZE + 16);
    }

    private void checkMaze() {
        if (isFinished()) {
            score += 50;
        }
    }

    public boolean isFinished() {
        short   i        = 0;
        boolean finished = true;

        while (i < N_BLOCKS * N_BLOCKS && finished) {
            if ((screenData[i] & 32) != 0) {
                return false;
            }

            i++;
        }

        return finished;
    }

    public boolean isReached() {
        if (lastMoveEaten) {
            lastMoveEaten = false;
            return true;
        }
        return false;
    }

    public boolean isReached(int x, int y){
        int pos = x / BLOCK_SIZE + N_BLOCKS * (y / BLOCK_SIZE);
        short ch = screenData[pos];
        return (ch & 32) != 0;
    }

    private void movePacman() {
        pacmand_x = req_dx;
        pacmand_y = req_dy;
        view_dx = pacmand_x;
        view_dy = pacmand_y;

        if(!canMove(pacman_x, pacman_y, pacmand_x, pacmand_y)){
            pacmand_x = 0;
            pacmand_y = 0;
        }

        pacman_x = pacman_x + PACMAN_SPEED * pacmand_x * BLOCK_SIZE;
        pacman_y = pacman_y + PACMAN_SPEED * pacmand_y * BLOCK_SIZE;

        int pos = pacman_x / BLOCK_SIZE + N_BLOCKS * (pacman_y / BLOCK_SIZE);
        short ch = screenData[pos];

        if ((ch & 32) != 0) {
            lastMoveEaten = true;
        }

        if ((ch & 48) != 0) {
            screenData[pos] = (short) (ch & 15);
            score++;
        }
    }


    public boolean canMove(int x, int y, int directionX, int directionY){
        int pos = x / BLOCK_SIZE + N_BLOCKS * (y / BLOCK_SIZE);
        short ch = screenData[pos];

        if (x % BLOCK_SIZE == 0 && y % BLOCK_SIZE == 0) {

            // Check for standstill
            return !((directionX == -1 && directionY == 0 && (ch & 1) != 0) ||
                    (directionX == 1 && directionY == 0 && (ch & 4) != 0) ||
                    (directionX == 0 && directionY == -1 && (ch & 2) != 0) ||
                    (directionX == 0 && directionY == 1 && (ch & 8) != 0));
        }
        return true;
    }

    public double currentHeuristic(int x, int y) {
        return targetHeuristic(x, y, 0, 0);
    }

    public double targetHeuristic(int x, int y, int directionX, int directionY) {
        int xBlock = x / BLOCK_SIZE + directionX;
        int yBlock = y / BLOCK_SIZE + directionY;
        return calcHeuristic(xBlock, yBlock);
    }

    private double calcHeuristic(int x, int y) {
        Double min = null;
        for (int i = 0; i < N_BLOCKS; i++) {
            for (int k = 0; k < N_BLOCKS; k++) {
                int pos = i * N_BLOCKS + k;
                if ((screenData[pos] & 32) != 0) {
                    double curr = Math.abs(y - i) + Math.abs(x - k);
                    if (min == null || curr < min) {
                        min = curr;
                    }
                }
            }
        }
        return min == null ? 0 : min;
    }

    private void drawPacman(Graphics2D g2d) {

        if (view_dx == -1) {
            drawPacnanLeft(g2d);
        } else if (view_dx == 1) {
            drawPacmanRight(g2d);
        } else if (view_dy == -1) {
            drawPacmanUp(g2d);
        } else {
            drawPacmanDown(g2d);
        }
    }

    private void drawPacmanUp(Graphics2D g2d) {

        switch (pacmanAnimPos) {
            case 1:
                g2d.drawImage(pacman2up, pacman_x + 1, pacman_y + 1, this);
                break;
            case 2:
                g2d.drawImage(pacman3up, pacman_x + 1, pacman_y + 1, this);
                break;
            case 3:
                g2d.drawImage(pacman4up, pacman_x + 1, pacman_y + 1, this);
                break;
            default:
                g2d.drawImage(pacman1, pacman_x + 1, pacman_y + 1, this);
                break;
        }
    }

    private void drawPacmanDown(Graphics2D g2d) {

        switch (pacmanAnimPos) {
            case 1:
                g2d.drawImage(pacman2down, pacman_x + 1, pacman_y + 1, this);
                break;
            case 2:
                g2d.drawImage(pacman3down, pacman_x + 1, pacman_y + 1, this);
                break;
            case 3:
                g2d.drawImage(pacman4down, pacman_x + 1, pacman_y + 1, this);
                break;
            default:
                g2d.drawImage(pacman1, pacman_x + 1, pacman_y + 1, this);
                break;
        }
    }

    private void drawPacnanLeft(Graphics2D g2d) {

        switch (pacmanAnimPos) {
            case 1:
                g2d.drawImage(pacman2left, pacman_x + 1, pacman_y + 1, this);
                break;
            case 2:
                g2d.drawImage(pacman3left, pacman_x + 1, pacman_y + 1, this);
                break;
            case 3:
                g2d.drawImage(pacman4left, pacman_x + 1, pacman_y + 1, this);
                break;
            default:
                g2d.drawImage(pacman1, pacman_x + 1, pacman_y + 1, this);
                break;
        }
    }

    private void drawPacmanRight(Graphics2D g2d) {

        switch (pacmanAnimPos) {
            case 1:
                g2d.drawImage(pacman2right, pacman_x + 1, pacman_y + 1, this);
                break;
            case 2:
                g2d.drawImage(pacman3right, pacman_x + 1, pacman_y + 1, this);
                break;
            case 3:
                g2d.drawImage(pacman4right, pacman_x + 1, pacman_y + 1, this);
                break;
            default:
                g2d.drawImage(pacman1, pacman_x + 1, pacman_y + 1, this);
                break;
        }
    }

    private void drawMaze(Graphics2D g2d) {

        short i = 0;
        int   x, y;

        for (y = 0; y < SCREEN_SIZE; y += BLOCK_SIZE) {
            for (x = 0; x < SCREEN_SIZE; x += BLOCK_SIZE) {

                g2d.setColor(mazeColor);
                g2d.setStroke(new BasicStroke(2));

                if ((screenData[i] & 1) != 0) {
                    g2d.drawLine(x, y, x, y + BLOCK_SIZE - 1);
                }

                if ((screenData[i] & 2) != 0) {
                    g2d.drawLine(x, y, x + BLOCK_SIZE - 1, y);
                }

                if ((screenData[i] & 4) != 0) {
                    g2d.drawLine(x + BLOCK_SIZE - 1, y, x + BLOCK_SIZE - 1, y + BLOCK_SIZE - 1);
                }

                if ((screenData[i] & 8) != 0) {
                    g2d.drawLine(x, y + BLOCK_SIZE - 1, x + BLOCK_SIZE - 1, y + BLOCK_SIZE - 1);
                }

                if ((screenData[i] & 16) != 0) {
                    g2d.setColor(dotColor);
                    g2d.fillRect(x + 11, y + 11, 2, 2);
                }

                if ((screenData[i] & 32) != 0) {
                    g2d.setColor(dotColor);
                    g2d.fillRect(x + 11, y + 11, 9, 9);
                }

                i++;
            }
        }
    }

    private void initGame() {
        score = 0;
        initLevel();
    }

    private void initLevel() {

        int i;
        for (i = 0; i < N_BLOCKS * N_BLOCKS; i++) {
            screenData[i] = levelData[i];
        }

        continueLevel();
    }

    private void continueLevel() {
        pacmand_x = 0;
        pacmand_y = 0;
        req_dx = 0;
        req_dy = 0;
        view_dx = -1;
        view_dy = 0;
    }

    private void loadImages() {
        pacman1 = new ImageIcon("src/resources/images/pacman.png").getImage();
        pacman2up = new ImageIcon("src/resources/images/up1.png").getImage();
        pacman3up = new ImageIcon("src/resources/images/up2.png").getImage();
        pacman4up = new ImageIcon("src/resources/images/up3.png").getImage();
        pacman2down = new ImageIcon("src/resources/images/down1.png").getImage();
        pacman3down = new ImageIcon("src/resources/images/down2.png").getImage();
        pacman4down = new ImageIcon("src/resources/images/down3.png").getImage();
        pacman2left = new ImageIcon("src/resources/images/left1.png").getImage();
        pacman3left = new ImageIcon("src/resources/images/left2.png").getImage();
        pacman4left = new ImageIcon("src/resources/images/left3.png").getImage();
        pacman2right = new ImageIcon("src/resources/images/right1.png").getImage();
        pacman3right = new ImageIcon("src/resources/images/right2.png").getImage();
        pacman4right = new ImageIcon("src/resources/images/right3.png").getImage();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        doDrawing(g);
    }

    private void doDrawing(Graphics g) {

        Graphics2D g2d = (Graphics2D) g;

        g2d.setColor(Color.black);
        g2d.fillRect(0, 0, d.width, d.height);

        drawMaze(g2d);
        drawScore(g2d);
        doAnim();

        playGame(g2d);


        g2d.drawImage(ii, 5, 5, this);
        Toolkit.getDefaultToolkit().sync();
        g2d.dispose();
    }

    class TAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {

            int key = e.getKeyCode();
            keyPressed = true;

            if (key == KeyEvent.VK_LEFT) {
                req_dx = -1;
                req_dy = 0;
            } else if (key == KeyEvent.VK_RIGHT) {
                req_dx = 1;
                req_dy = 0;
            } else if (key == KeyEvent.VK_UP) {
                req_dx = 0;
                req_dy = -1;
            } else if (key == KeyEvent.VK_DOWN) {
                req_dx = 0;
                req_dy = 1;
            } else if (key == KeyEvent.VK_PAUSE) {
                if (timer.isRunning()) {
                    timer.stop();
                } else {
                    timer.start();
                }
            }
        }


        @Override
        public void keyReleased(KeyEvent e) {
            keyPressed = false;
        }
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }
}
