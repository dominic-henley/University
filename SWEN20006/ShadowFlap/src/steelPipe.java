import bagel.Image;

/**
 * This class contains information about the steel Pipe type
 */

public class steelPipe extends Pipes {

    private final Image FLAME_IMAGE = new Image("res/level-1/flame.png");
    private final int FLAME_OFFSET = 10;
    private int flameFrames = 0;
    private int flameSpawn = 0;
    private boolean flame;

    /**
     * default constructor for steel pipes, creates an instance of a pipe set with steelPipe.png set as its parameter
     */
    public steelPipe(){
        super(new Image("res/level-1/steelPipe.png"));
    }

    /**
     * Updates the pipes when a frame is rendered
     */
    @Override
    public void update(){
        super.update();

        // flame spawning logic
        if (flameSpawn % 20 == 0) {
            flame = true;
        }

        if (flame) {
            if (flameFrames < 30) {
                spawnFlame();
                flameFrames++;
            }else{
                flameFrames = 0;
                flame = false;
            }
        }
        flameSpawn++;
    }

    /**
     * @return Returns whether or not the pipes are steel
     */
    @Override
    public boolean isFlame(){
        return flame;
    }

    /**
     * @return boolean Returns whether or not the current pipe set is spawning flames or not
     */
    @Override
    public boolean isSteel(){
        return true;
    }

    private void spawnFlame(){
        FLAME_IMAGE.draw(pipeX, pipeYRandom - (PIPE_GAP / 2.0 - FLAME_OFFSET));
        FLAME_IMAGE.draw(pipeX, pipeYRandom + (PIPE_GAP / 2.0 - FLAME_OFFSET), ROTATOR);
    }
}
