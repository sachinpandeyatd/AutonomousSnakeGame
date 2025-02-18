package com.asg;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.Random;

public class AutonomousSnakeGame extends JPanel implements ActionListener {
    private final int WIDTH = 600, HEIGHT = 600, UNIT_SIZE = 20;
    private final int GRID_WIDTH = WIDTH / UNIT_SIZE, GRID_HEIGHT = HEIGHT / UNIT_SIZE;
    private final int DELAY = 100;
    private final int GAME_TIME = 60000; // 1 minutes in milliseconds

    private LinkedList<Point> greenSnake, redSnake;
    private int foodX, foodY;
    private int greenScore = 0, redScore = 0;
    private Timer timer;
    private long startTime;
    private Random random;

    public AutonomousSnakeGame() {
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        random = new Random();
        startGame();
    }

    private void startGame() {
        greenSnake = new LinkedList<>();
        redSnake = new LinkedList<>();
        greenSnake.add(new Point(random.nextInt(GRID_WIDTH) * UNIT_SIZE, random.nextInt(GRID_HEIGHT) * UNIT_SIZE));
        redSnake.add(new Point(random.nextInt(GRID_WIDTH) * UNIT_SIZE, random.nextInt(GRID_HEIGHT) * UNIT_SIZE));
        spawnFood();
        startTime = System.currentTimeMillis();
        timer = new Timer(DELAY, this);
        timer.start();
    }

    private void spawnFood() {
        do {
            foodX = random.nextInt(GRID_WIDTH) * UNIT_SIZE;
            foodY = random.nextInt(GRID_HEIGHT) * UNIT_SIZE;
        } while (isOccupied(foodX, foodY));
    }

    private boolean isOccupied(int x, int y) {
        for (Point p : greenSnake) {
            if (p.x == x && p.y == y) return true;
        }
        for (Point p : redSnake) {
            if (p.x == x && p.y == y) return true;
        }
        return false;
    }

    private void moveSnake(LinkedList<Point> snake, int targetX, int targetY, boolean isGreen) {
        if (snake.isEmpty()) return;

        Point head = snake.getFirst();
        int newX = head.x + (targetX > head.x ? UNIT_SIZE : (targetX < head.x ? -UNIT_SIZE : 0));
        int newY = head.y + (targetY > head.y ? UNIT_SIZE : (targetY < head.y ? -UNIT_SIZE : 0));

        if (isOccupied(newX, newY)) {
            // Try moving in an alternative direction
            if (!isOccupied(head.x + UNIT_SIZE, head.y)) {
                newX = head.x + UNIT_SIZE;
                newY = head.y;
            } else if (!isOccupied(head.x - UNIT_SIZE, head.y)) {
                newX = head.x - UNIT_SIZE;
                newY = head.y;
            } else if (!isOccupied(head.x, head.y + UNIT_SIZE)) {
                newX = head.x;
                newY = head.y + UNIT_SIZE;
            } else if (!isOccupied(head.x, head.y - UNIT_SIZE)) {
                newX = head.x;
                newY = head.y - UNIT_SIZE;
            } else {
                return; // No available moves, snake remains in place
            }
        }

        snake.addFirst(new Point(newX, newY));
        if (newX == foodX && newY == foodY) {
            if (isGreen) greenScore++;
            else redScore++;
            spawnFood();
        } else {
            snake.removeLast();
        }
    }

    private void checkGameEnd() {
        if (System.currentTimeMillis() - startTime >= GAME_TIME) {
            timer.stop();
            String winner = (greenScore > redScore) ? "Green Snake Wins!" : (redScore > greenScore) ? "Red Snake Wins!" : "It's a Tie!";
            JOptionPane.showMessageDialog(this, winner, "Game Over", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.YELLOW);
        g.fillRect(foodX, foodY, UNIT_SIZE, UNIT_SIZE);

        g.setColor(Color.GREEN);
        for (Point p : greenSnake) {
            g.fillRect(p.x, p.y, UNIT_SIZE, UNIT_SIZE);
        }

        g.setColor(Color.RED);
        for (Point p : redSnake) {
            g.fillRect(p.x, p.y, UNIT_SIZE, UNIT_SIZE);
        }

        g.setColor(Color.WHITE);
        g.drawString("Green: " + greenScore + "  Red: " + redScore, 10, 10);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        moveSnake(greenSnake, foodX, foodY, true);
        moveSnake(redSnake, foodX, foodY, false);
        checkGameEnd();
        repaint();
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Autonomous Snake Game");
        AutonomousSnakeGame gamePanel = new AutonomousSnakeGame();
        frame.add(gamePanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
