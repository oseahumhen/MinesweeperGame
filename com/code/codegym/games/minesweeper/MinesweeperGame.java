package com.codegym.games.minesweeper;

import com.codegym.engine.cell.Color;
import com.codegym.engine.cell.Game;

import java.util.ArrayList;
import java.util.List;

public class MinesweeperGame extends Game {
    private static final int SIDE = 9;
    private GameObject[][] gameField = new GameObject[SIDE][SIDE];
    private int countMinesOnField;
    private static final String MINE = "\uD83D\uDCA3";
    private static final String FLAG = "\uD83D\uDEA9";
    private int countFlags;
    private boolean isGameStopped = false;
    private int countClosedTiles = SIDE * SIDE;
    private int score = 0;
    private boolean isFirstPlay  = true;

    @Override
    public void initialize() {
        setScreenSize(SIDE, SIDE);
        createGame();
    }

    private void createGame() {
        for (int y = 0; y < SIDE; y++) {
            for (int x = 0; x < SIDE; x++) {
                boolean isMine = getRandomNumber(10) < 1;
                if (isMine) {
                    countMinesOnField++;
                }
                gameField[y][x] = new GameObject(x, y, isMine);
                setCellColor(x, y, Color.ORANGE);
                setCellValue(x, y, "");
            }
        }
        countMineNeighbors();
        countFlags = countMinesOnField;
    }

    private List<GameObject> getNeighbors(GameObject gameObject) {
        List<GameObject> result = new ArrayList<>();
        for (int y = gameObject.y - 1; y <= gameObject.y + 1; y++) {
            for (int x = gameObject.x - 1; x <= gameObject.x + 1; x++) {
                if (y < 0 || y >= SIDE) {
                    continue;
                }
                if (x < 0 || x >= SIDE) {
                    continue;
                }
                if (gameField[y][x] == gameObject) {
                    continue;
                }
                result.add(gameField[y][x]);
            }
        }
        return result;
    }
    
    private void countMineNeighbors() {
        List<GameObject> totalNeighbors = new ArrayList<>();
        for (int y = 0; y < SIDE; y++) {
            for (int x = 0; x < SIDE; x++) {
                if (!gameField[y][x].isMine){
                    totalNeighbors = getNeighbors(gameField[y][x]);
                    for (GameObject Neighbor: totalNeighbors) {
                        if (Neighbor.isMine) {
                            gameField[y][x].countMineNeighbors++;
                        }
                    }
                }
            }
        }
    }
    
    
    private void openTile(int x, int y) {
        if (isGameStopped == false) {
            GameObject tile = gameField[y][x];
            if (isFirstPlay) {
                if (tile.isMine) {
                    tile.isMine = false;
                    countMinesOnField--;
                    countFlags = countMinesOnField;
                    countMineNeighbors();
                }
                isFirstPlay = false;
            }
            if (tile.isOpen == false && tile.isFlag == false) {
                int numMineNeighbors = tile.countMineNeighbors;
                List<GameObject> totalNeighbors = new ArrayList<>();
                tile.isOpen = true;
                countClosedTiles--;
                setCellColor(x, y, Color.GREEN);
                if (tile.isMine) {
                    setCellValueEx(x, y, Color.RED, MINE);
                    gameOver();
                }
                else {
                    score += 5;
                    setScore(score);
                    if (countClosedTiles == countMinesOnField){
                        win();
                        return;
                    }
                    
                    if (numMineNeighbors == 0) {
                        totalNeighbors = getNeighbors(tile);
                        setCellValue(x, y, "");
                        for (GameObject Neighbor: totalNeighbors) {
                            if (!Neighbor.isOpen) {
                                openTile(Neighbor.x, Neighbor.y);
                            }
                        }
                    }
                    else {
                        setCellNumber(x, y, tile.countMineNeighbors);
                    }
                }
            }
        }
    }
    
    @Override
    public void onMouseLeftClick(int x, int y){
        if (isGameStopped == true) {
            restart();
        }
        else {
            openTile(x, y);
        }
    }
    
    private void markTile(int x, int y) {
        if (isGameStopped == false) {
            GameObject tile = gameField[y][x];
            if (tile.isOpen == false){
                if (tile.isFlag == false && countFlags > 0){
                    tile.isFlag = true;
                    countFlags--;
                    setCellValue(x, y, FLAG);
                    setCellColor(x, y, Color.YELLOW);
                }
                else if (tile.isFlag == true && countFlags != 0){
                    tile.isFlag = false;
                    countFlags++;
                    setCellValue(x, y, "");
                    setCellColor(x, y, Color.ORANGE);
                }
            }
        }
    }
    
    @Override
    public void onMouseRightClick(int x, int y) {
        markTile(x, y);
    }
    
    private void gameOver() {
        isGameStopped = true;
        showMessageDialog(Color.NONE, "GAME OVER \n SCORE: "+ score, Color.BLACK, 30);
    }
    
    private void win() {
        isGameStopped = true;
        showMessageDialog(Color.NONE, "YOU WIN \n SCORE: "+ score, Color.BLACK, 30);
    }
    
    private void restart() {
        isFirstPlay  = true;
        isGameStopped = false;
        countClosedTiles = SIDE * SIDE;
        score = 0;
        countMinesOnField = 0;
        setScore(score); 
        createGame();
    }
    
}