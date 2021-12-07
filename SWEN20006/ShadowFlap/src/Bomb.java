import bagel.Image;

/**
 * This class contains information about the Bomb weapon type
 */

public class Bomb extends Weapon {

    /**
     * Creates an instance of a Bomb weapon
     */
    public Bomb(){
        super(new Image("res/level-1/bomb.png"), 50, 1);
    }
}
