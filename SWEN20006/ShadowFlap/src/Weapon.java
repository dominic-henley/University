import bagel.Image;
import bagel.Window;
import bagel.util.Point;
import bagel.util.Rectangle;

import java.util.Random;

/**
 * This class creates an instance of a Weapon that can be a rock or a bomb and contains logic for the weapons
 *
 *  Notes:
 *  - weapon speed was adjusted in line with pipe speed
 *  - a weapon type of 0 identifies rock whereas 1 identifies a bomb
 */

public abstract class Weapon {

    private Random random = new Random(System.currentTimeMillis());
    private final int WEAPON_SPEED = 5;
    private final int WEAPON_FIRE_SPEED = 5;
    private double weaponX = Window.getWidth();
    private int weaponY = 100 + random.nextInt(500 - 100);
    private Image image;
    private int range;
    private int type;
    private boolean shooting;
    private double startingY;
    private double startingX;
    private boolean collided;
    private int shootFrames = 0;

    /**
     * This creates an instance of a weapon
     * @param image This is the image the weapon will have
     * @param range This is the range that the weapon will travel when shot
     * @param type This is the type of weapon (Bomb or Rock)
     */
    public Weapon(Image image, int range, int type){
        this.image = image;
        this.range = range;
        this.type = type;
        shooting = false;
        collided = false;
    }

    /**
     * This draws the specified image on the screen
     */
    public void render(){
        image.draw(weaponX, weaponY);
    }

    /**
     * This updates the pipes every frame that is rendered
     */
    public void update(){
        render();
        weaponX -= WEAPON_SPEED + (WEAPON_SPEED * (ShadowFlap.getTimescale()-1) * 50/100);
    }

    /**
     * @return Rectangle This returns the weapon's bounding box
     */
    public Rectangle getBox(){
        return image.getBoundingBoxAt(new Point(this.weaponX, this.weaponY));
    }

    /**
     * @return Image returns the image of the current weapon
     */
    public Image getImage(){
        return image;
    }

    /**
     * Shoots the weapon that is currently equipped
     * @param startingX This is the X coordinate at the point the weapon is shot
     * @param startingY This is the Y coordinate at the point the weapon is shot
     */
    public void shoot(double startingX, double startingY){
        shooting = true;
        this.startingX = startingX;
        this.startingY = startingY;
    }

    /**
     * This handles the logic of weapon shooting and animating
     */
    public void renderShooting(){
        if(shooting){
            if(shootFrames < range && !collided){
                image.draw(startingX + WEAPON_FIRE_SPEED * (shootFrames + 1), startingY);
                shootFrames++;
            }else{
                Bird.equipWeapon(null);
                shooting = false;
                shootFrames = 0;
            }
        }
    }

    /**
     * @return This returns whether the current weapon is being shot or not
     */
    public boolean isShooting(){
        return shooting;
    }

    /**
     * @return This returns the shot weapon's bounding box
     */
    public Rectangle getShotBox(){
        return image.getBoundingBoxAt(new Point(startingX + WEAPON_FIRE_SPEED * (shootFrames + 1), startingY));
    }

    /**
     * This updates the state of the weapon if it has hit a pipe
     */
    public void collide(){
        collided = true;
    }

    /**
     * @return This returns whether the current weapon is a bomb or not
     */
    public boolean isBomb(){
        return type == 1;
    }
}
