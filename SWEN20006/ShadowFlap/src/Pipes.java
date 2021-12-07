import bagel.DrawOptions;
import bagel.Image;
import bagel.Window;
import bagel.util.Point;
import bagel.util.Rectangle;

import java.util.Random;

/**
 * This class contains information about pipes and the logic needed for them to function
 *
 * Code obtained from and modified from project 1 sample solution for SWEN20003 Assignment 1, 2021 Sem 2
 *
 * Notes:
 * - pipe speed was adjusted to 5 pixels/frame (spec specified 3) due to FPS reasons
 */

public abstract class Pipes {
    protected double pipeX = Window.getWidth();
    protected final int PIPE_GAP = 168;
    protected final DrawOptions ROTATOR = new DrawOptions().setRotation(Math.PI);
    private final Image FLAME_IMAGE = new Image("res/level-1/flame.png");
    private final int FLAME_OFFSET = 10;
    private final Image image;
    private final int HIGH_GAP = 500;
    private final int MID_GAP = 300;
    private final int LOW_GAP = 100;
    private final int PIPE_SPEED = 5;
    private final double TOP_PIPE_Y = -PIPE_GAP / 2.0;
    private final double BOTTOM_PIPE_Y = Window.getHeight() + PIPE_GAP / 2.0;
    private boolean passed;
    private Random random = new Random(System.currentTimeMillis());
    protected int pipeYRandom = random.nextInt(500 - 100) + 100;
    private int type = random.nextInt(3);


    /**
     * Creates an instance of a pipe
     * @param image This determines which image should be rendered
     */
    public Pipes(Image image) {
        passed = false;
        this.image = image;
    }

    private void renderPipeSet() {
        image.draw(pipeX, TOP_PIPE_Y - calculateOffset());
        image.draw(pipeX, BOTTOM_PIPE_Y - calculateOffset(), ROTATOR);
    }

    private double calculateOffset(){
        double offset;

        if(ShadowFlap.getLevel() == 0){
            if(type == 0) {
                // low-gap
                offset = Window.getHeight() / 2 - LOW_GAP;
            }else if(type == 1) {
                // mid-gap
                offset = Window.getHeight() / 2 - MID_GAP;
            }else{
                // high-gap
                offset = Window.getHeight() / 2 - HIGH_GAP;
            }
        }else{
            offset = Window.getHeight() / 2 - pipeYRandom;
        }
        return offset;
    }

    /**
     * Updates the pipes when a frame is rendered
     */
    public void update() {
        renderPipeSet();
        pipeX -= PIPE_SPEED + (PIPE_SPEED * (ShadowFlap.getTimescale() - 1) * 50 / 100);
    }

    /**
     * @return Rectangle returns the top bounding box
     */
    public Rectangle getTopBox() {
        return image.getBoundingBoxAt(new Point(pipeX, TOP_PIPE_Y - calculateOffset()));
    }

    /**
     * @return Rectangle returns the bottom bounding box
     */
    public Rectangle getBottomBox() {
        return image.getBoundingBoxAt(new Point(pipeX, BOTTOM_PIPE_Y - calculateOffset()));
    }

    /**
     * Updates the current state of the pipe
     * @param b This is the new boolean value that the pipes will take
     */
    public void setPassed(boolean b){
        passed = b;
    }

    /**
     * @return This returns whether the pipes had been passed or not
     */
    public boolean isPassed(){
        return passed;
    }

    /**
     * @return Returns whether or not the pipes are steel
     */
    public boolean isSteel() {
        return false;
    }

    /**
     * @return boolean Returns whether or not the current pipe set is spawning flames or not
     */
    public boolean isFlame(){
        return false;
    }

    /**
     * @return Rectangle returns the top flame bounding box
     */
    public Rectangle getTopFlameBox(){
        return FLAME_IMAGE.getBoundingBoxAt(new Point(pipeX, pipeYRandom - (PIPE_GAP / 2.0 - FLAME_OFFSET)));
    }

    /**
     * @return Rectangle returns the bottom flame bounding box
     */
    public Rectangle getBottomFlameBox() {
        return FLAME_IMAGE.getBoundingBoxAt(new Point(pipeX, pipeYRandom + (PIPE_GAP / 2.0 - FLAME_OFFSET)));
    }
}
