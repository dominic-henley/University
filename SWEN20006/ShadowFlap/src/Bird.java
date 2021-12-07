import bagel.*;
import bagel.util.Point;
import bagel.util.Rectangle;
import java.lang.Math;

/**
 * This class creates an isntance of a Bird and contains logic needed for the bird to function
 *
 * Code obtained from and modified from project 1 sample solution for SWEN20003 Assignment 1, 2021 Sem 2
 */


public class Bird {
    private final Image WING_DOWN_IMAGE_LVL0 = new Image("res/level-0/birdWingDown.png");
    private final Image WING_UP_IMAGE_LVL0 = new Image("res/level-0/birdWingUp.png");
    private final Image WING_DOWN_IMAGE_LVL1 = new Image("res/level-1/birdWingDown.png");
    private final Image WING_UP_IMAGE_LVL1 = new Image("res/level-1/birdWingUp.png");
    private final Image FULL_LIFE = new Image("res/level/fullLife.png");
    private final Image NO_LIFE = new Image("res/level/noLife.png");
    private final double X = 200;
    private final double FLY_SIZE = 6;
    private final double FALL_SIZE = 0.4;
    private final double INITIAL_Y = 350;
    private final double Y_TERMINAL_VELOCITY = 10;
    private final double SWITCH_FRAME = 10;
    private final Point LIFE_POINT = new Point(100, 15);
    private int frameCount = 0;
    private double y;
    private double yVelocity;
    private Rectangle boundingBox;
    private int life;
    private int maxLife;
    private static Weapon weapon;

    /**
     * default constructor for Bird, creates an instance of Bird and initialises Bird values
     */
    public Bird() {
        y = INITIAL_Y;
        yVelocity = 0;
        boundingBox = WING_DOWN_IMAGE_LVL0.getBoundingBoxAt(new Point(X, y));
        maxLife = 3;
        life = 3;
        weapon = null;
    }

    /**
     * Updates the Bird instance by one frame and handles Bird's logic
     * @param input This is the input the user gives (keys pressed)
     * @return Rectangle This returns the bird's bounding box for collision checks
     */
    public Rectangle update(Input input) {
        frameCount += 1;
        renderLife();

        if(isArmed() && !weapon.isShooting()){
            renderWeapon();
        }

        if(ShadowFlap.getLevel() == 1){
            maxLife = 6;
        }

        if (input.wasPressed(Keys.SPACE)) {
            yVelocity = -FLY_SIZE;
            if(ShadowFlap.getLevel() == 0) {
                WING_DOWN_IMAGE_LVL0.draw(X, y);
            }else{
                WING_DOWN_IMAGE_LVL1.draw(X, y);
            }
        }
        else {
            yVelocity = Math.min(yVelocity + FALL_SIZE, Y_TERMINAL_VELOCITY);
            if (frameCount % SWITCH_FRAME == 0) {
                if(ShadowFlap.getLevel() == 0) {
                    WING_UP_IMAGE_LVL0.draw(X, y);
                }else{
                    WING_UP_IMAGE_LVL1.draw(X, y);
                }
                boundingBox = WING_UP_IMAGE_LVL0.getBoundingBoxAt(new Point(X, y));
            }
            else {
                if(ShadowFlap.getLevel() == 0) {
                    WING_DOWN_IMAGE_LVL0.draw(X, y);
                }else{
                    WING_DOWN_IMAGE_LVL1.draw(X, y);
                }
                boundingBox = WING_DOWN_IMAGE_LVL0.getBoundingBoxAt(new Point(X, y));
            }
        }
        y += yVelocity;

        return boundingBox;
    }

    /**
     * @return double This returns the bird's Y coordinate
     */
    public double getY() {
        return y;
    }

    /**
     * @return double This returns the bird's X coordinate
     */
    public double getX() {
        return X;
    }

    /**
     * @return Rectangle This returns the bird's bounding box
     */
    public Rectangle getBox() {
        return boundingBox;
    }

    /**
     * This makes the bird take damage and decreases its life by 1
     */
    public void takeDamage(){
        setLife(getLife()-1);
    }

    /**
     * @param life This is the value the bird's life should be
     */
    public void setLife(int life){
        this.life = life;
    }

    /**
     * @return int This returns the bird's current life
     */
    public int getLife(){
        return this.life;
    }

    private void renderLife(){
        int offset = 0;
        for(int i = 0; i < life; i++){
            FULL_LIFE.drawFromTopLeft(LIFE_POINT.x + offset * 50, LIFE_POINT.y);
            offset++;
        }

        for(int i = 0; i < maxLife - getLife(); i++){
            NO_LIFE.drawFromTopLeft(LIFE_POINT.x + offset * 50, LIFE_POINT.y);
            offset++;
        }
    }

    /**
     * @return Weapon This returns the bird's currently equipped weapon
     */
    public Weapon getWeapon(){
        return weapon;
    }

    /**
     * This equips the bird with a weapon
     */
    public static void equipWeapon(Weapon w){
        weapon = w;
    }

    /**
     * This resets the bird's values to all the initial values
     */
    public void reset(){
        WING_DOWN_IMAGE_LVL1.draw(X, INITIAL_Y);
        yVelocity = 0;
        y = INITIAL_Y;
    }

    private void renderWeapon(){
        weapon.getImage().draw(boundingBox.right(), getY());
    }

    /**
     * @return boolean This returns whether the bird is armed or not
     */
    public boolean isArmed(){
        return weapon != null;
    }

    /**
     * @return boolean This returns whether the bird is dead or not
     */
    public boolean isDead(){
        return life == 0;
    }
}