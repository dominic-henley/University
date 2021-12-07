import bagel.Image;

/**
 * This class contains information about the Bomb weapon type
 */

public class Rock extends Weapon {

    /**
     * Creates an isntance of a Rock weapon type
     */
    public Rock(){
        super(new Image("res/level-1/rock.png"), 25, 0);
    }
}
