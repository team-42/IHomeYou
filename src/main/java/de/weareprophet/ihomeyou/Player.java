package de.weareprophet.ihomeyou;

import javafx.scene.input.KeyCode;
import org.frice.anim.rotate.SimpleRotate;
import org.frice.obj.button.SimpleText;
import org.frice.obj.sub.ShapeObject;
import org.frice.resource.graphics.ColorResource;
import org.frice.util.shape.FRectangle;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

class Player {

    private final ShapeObject shape;
    private final SimpleText budgetDisplay;
    private final SimpleText skillDisplay;
    private int row = 0;
    private int column = 0;
    private int budget = 100;
    private int skillPoints = 1;

    Player(IHomeYouGame game) {
        shape = new ShapeObject(ColorResource.DARK_GRAY, new FRectangle(GameGrid.SIZE - 2 * GameGrid.BORDERS, GameGrid.SIZE - 2 * GameGrid.BORDERS));
        game.addObject(shape);
        shape.setX(GameGrid.BORDERS * 2);
        shape.setY(GameGrid.BORDERS * 2);
        SimpleRotate rotate = new SimpleRotate(2);
        shape.addAnim(rotate);
        game.addObject(new SimpleText("Cash: €", game.getXOfRightColumn(), 20));
        budgetDisplay = new SimpleText(String.valueOf(budget), game.getXOfRightColumn() + 70, 20);
        game.addObject(budgetDisplay);
        game.addObject(new SimpleText("Skill points:", game.getXOfRightColumn(), 40));
        skillDisplay = new SimpleText(String.valueOf(skillPoints), game.getXOfRightColumn() + 120, 40);
        game.addObject(skillDisplay);

        game.addKeyPressedEvent(KeyCode.RIGHT.getCode(), event -> {
            if (column < GameGrid.COLS - 1) {
                shape.move(GameGrid.SIZE, 0);
                column++;
            }
        });
        game.addKeyPressedEvent(KeyCode.LEFT.getCode(), event -> {
            if (column > 0) {
                shape.move(-GameGrid.SIZE, 0);
                column--;
            }
        });
        game.addKeyPressedEvent(KeyCode.UP.getCode(), event -> {
            if (row > 0) {
                shape.move(0, -GameGrid.SIZE);
                row--;
            }
        });
        game.addKeyPressedEvent(KeyCode.DOWN.getCode(), event -> {
            if (row < GameGrid.ROWS - 1) {
                shape.move(0, GameGrid.SIZE);
                row++;
            }
        });
    }

    int getRow() {
        return row;
    }

    int getColumn() {
        return column;
    }

    boolean canPay(int amount) {
        return amount <= budget;
    }

    boolean pay(int amount) {
        if (amount <= budget) {
            budget -= amount;
            budgetDisplay.setText(String.valueOf(budget));
            return true;
        } else {
            return false;
        }
    }

    void signalMistake(ScheduledExecutorService es) {
        shape.setColor(ColorResource.RED);
        es.schedule(() -> shape.setColor(ColorResource.DARK_GRAY), 400, TimeUnit.MILLISECONDS);
    }

    int getSkillPoints() {
        return skillPoints;
    }

    void spendSkillPoints(final int pointsToSpend) {
        if (this.skillPoints < pointsToSpend) {
            throw new IllegalArgumentException("Not enough skill points");
        }
        this.skillPoints -= pointsToSpend;
        this.skillDisplay.setText(String.valueOf(this.skillPoints));
    }

    void addSkillPoints(int pointsToAdd) {
        skillPoints += pointsToAdd;
        this.skillDisplay.setText(String.valueOf(this.skillPoints));
    }

    void addBudget(int amount) {
        budget += amount;
        budgetDisplay.setText(String.valueOf(budget));
    }

    void kill() {
        shape.setDied(true);
    }
}
