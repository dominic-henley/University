import bagel.*;
import bagel.Font;
import bagel.Image;
import bagel.Window;
import bagel.util.Rectangle;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

/**
 * SWEN20003 Project 2, Semester 2, 2021
 * This class handles the update (frame) logic of the game
 *
 * @author: Dominic Henley
 *
 * Code obtained from and modified from project 1 sample solution for SWEN20003 Assignment 1, 2021 Sem 2
 *
 * Note:
 * - Repeated division and multiplication leads to loss in precision, which may cause changing frame spawn rates when the time scale is repeatedly increased and decreased
 */
public class ShadowFlap extends AbstractGame {
    private Image backgroundImage;
    private final String INSTRUCTION_MSG = "PRESS SPACE TO START";
    private final String GAME_OVER_MSG = "GAME OVER!";
    private final String CONGRATS_MSG = "CONGRATULATIONS!";
    private final String SCORE_MSG = "SCORE: ";
    private final String FINAL_SCORE_MSG = "FINAL SCORE: ";
    private final String SHOOT_MSG = "PRESS 'S' TO SHOOT";
    private final int FONT_SIZE = 48;
    private final int LEVEL_UP_THRESHOLD = 10;
    private final Font FONT = new Font("res/font/slkscr.ttf", FONT_SIZE);
    private final int SCORE_MSG_OFFSET = 75;
    private final String LEVEL_UP_MSG = "LEVEL-UP!";
    private Bird bird;
    private ArrayList<Pipes> pipes = new ArrayList<>();
    private ArrayList<Weapon> weapons = new ArrayList<>();
    private int score;
    private boolean gameOn;
    private boolean collision;
    private boolean win;
    private boolean loading;
    private int pipeFrames = 0;
    private int weaponFrames = 0;
    private int loadFrames = 0;
    private Random random = new Random(System.currentTimeMillis());
    private static int level;
    private static int timescale;
    private double pipeSpawnRate = 100.0;
    private double weaponSpawnRate = 150.0;
    private final double INITIAL_PIPE_SPAWN_RATE = 100.0;
    private final double INITIAL_WEAPON_SPAWN_RATE = 150.0;

    /**
     * default constructor for ShadowFlap
     * Creates an instance of the game and initialises game values
     */

    public ShadowFlap() {
        super(1024, 768, "ShadowFlap");
        backgroundImage = new Image("res/level-0/background.png");
        bird = new Bird();
        score = 0;
        gameOn = false;
        collision = false;
        win = false;
        level = 0;
        timescale = 1;
        loading = false;
    }

    /**
     * The entry point for the program.
     */

    public static void main(String[] args) {
        ShadowFlap game = new ShadowFlap();
        game.run();
    }

    /**
     * Performs a state update.
     * allows the game to exit when the escape key is pressed.
     */

    @Override
    public void update(Input input) {

        backgroundImage.draw(Window.getWidth()/2.0, Window.getHeight()/2.0);

        // game won
        if(score == 30){
            win = true;
        }

        // Level-up
        if (score == LEVEL_UP_THRESHOLD && level == 0) {
            levelUp(input);
        }

        if(input.wasPressed(Keys.L) && timescale < 5){
            increaseTimescale();
        }

        if(input.wasPressed(Keys.K) && timescale > 1){
            decreaseTimescale();
        }

        if(bird.getLife() == 0){
            renderGameOverScreen();
        }

        if (input.wasPressed(Keys.ESCAPE)) {
            Window.close();
        }

        // game has not started
        if (!gameOn && !loading) {
            renderInstructionScreen(input);
        }

        // game is active
        if (gameOn && !win && !bird.isDead()) {

            if (birdOutOfBound()) {
                bird.takeDamage();
                bird.reset();
            }

            bird.update(input);
            Rectangle birdBox = bird.getBox();

            if(pipeFrames % pipeSpawnRate == 0){
                addPipes();
            }

            // Level 1 logic
            if(level == 1  && gameOn && (weaponFrames % weaponSpawnRate == 0) && !spawnCollision()){
                spawnWeapons();
            }

            if(bird.isArmed()) {
                Weapon weapon = bird.getWeapon();
                weapon.renderShooting();

                if (input.wasPressed(Keys.S)) {
                    weapon.shoot(bird.getBox().right(), bird.getY());
                }

                if(weapon.isShooting()){
                    checkWeaponCollision(weapon, weapon.getShotBox());
                }
            }

            iteratePipes(birdBox);
            iterateWeapons(birdBox);

            updateScore();
            pipeFrames++;
            weaponFrames++;
        }

        // game won
        if (win) {
            renderWinScreen();
        }
    }

    private boolean spawnCollision(){
        return weaponFrames % pipeSpawnRate == 0;
    }

    private void checkWeaponCollision(Weapon weapon, Rectangle shotBox){
        Iterator<Pipes> iter = pipes.iterator();

        while(iter.hasNext()){
            Pipes pipe = iter.next();

            if(shotBox.intersects(pipe.getTopBox()) || shotBox.intersects(pipe.getBottomBox())){
                weapon.collide();
                // Plastic pipe
                if(!pipe.isSteel()){
                    pipeDestroyed(iter);
                }else{
                    // Steel pipe
                    if(weapon.isBomb()){
                        pipeDestroyed(iter);
                    }
                }
            }
        }
    }

    private void pipeDestroyed(Iterator iter){
        iter.remove();
        score++;
    }

    private void spawnWeapons(){
        if(random.nextInt(2) == 0){
            weapons.add(new Rock());
        }else{
            weapons.add(new Bomb());
        }
    }

    private void levelUp(Input input){
        if(loadFrames < 19){
            loading = true;
            gameOn = false;
            renderLevelUpScreen();
            loadFrames++;
        }else{
            pipes.clear();
            timescale = 1;
            pipeSpawnRate = INITIAL_PIPE_SPAWN_RATE;
            weaponSpawnRate = INITIAL_WEAPON_SPAWN_RATE;
            renderLevelOneInstructions(input);
        }
    }

    private void iterateWeapons(Rectangle birdBox){
        Iterator<Weapon> iter = weapons.iterator();

        while(iter.hasNext()){
            Weapon weapon = iter.next();
            weapon.update();
            Rectangle box = weapon.getBox();
            collision = detectWeaponCollision(birdBox, box);

            if (collision && !bird.isArmed()) {
                Bird.equipWeapon(weapon);
                iter.remove();
            }
        }
    }

    private void iteratePipes(Rectangle birdBox){
        Iterator<Pipes> iter = pipes.iterator();

        while(iter.hasNext()){
            Pipes pipe = iter.next();
            pipe.update();
            Rectangle topPipeBox;
            Rectangle bottomPipeBox;
            Rectangle topFlameBox ;
            Rectangle bottomFlameBox;
            boolean flameCollision = false;

            if(pipe.getTopBox().right() < 0){
                iter.remove();
            }

            if(level == 0){
                topPipeBox = pipe.getTopBox();
                bottomPipeBox = pipe.getBottomBox();
            }else{
                topPipeBox = pipe.getTopBox();
                bottomPipeBox = pipe.getBottomBox();
            }

            if(pipe.isFlame()){
                topFlameBox = pipe.getTopFlameBox();
                bottomFlameBox = pipe.getBottomFlameBox();
                flameCollision = detectPipeCollision(birdBox, topFlameBox, bottomFlameBox);
            }

            collision = detectPipeCollision(birdBox, topPipeBox, bottomPipeBox) || flameCollision;

            if(collision){
                bird.takeDamage();
                iter.remove();
            }
        }
    }

    private boolean birdOutOfBound() {
        return (bird.getY() > Window.getHeight()) || (bird.getY() < 0);
    }

    private void renderInstructionScreen(Input input) {
        // paint the instruction on screen
        FONT.drawString(INSTRUCTION_MSG, (Window.getWidth()/2.0-(FONT.getWidth(INSTRUCTION_MSG)/2.0)), (Window.getHeight()/2.0-(FONT_SIZE/2.0)));
        if (input.wasPressed(Keys.SPACE)) {
            gameOn = true;
        }
    }

    private void renderLevelOneInstructions(Input input){
        setBackgroundImage(new Image("res/level-1/background.png"));
        FONT.drawString(INSTRUCTION_MSG, (Window.getWidth()/2.0-(FONT.getWidth(INSTRUCTION_MSG)/2.0)), (Window.getHeight()/2.0-(FONT_SIZE/2.0)));
        FONT.drawString(SHOOT_MSG, (Window.getWidth()/2.0-(FONT.getWidth(SHOOT_MSG)/2.0)), (Window.getHeight()/2.0-(FONT_SIZE/2.0))+SCORE_MSG_OFFSET);
        if (input.wasPressed(Keys.SPACE)) {
            gameOn = true;
            loading = false;
            level = 1;
            bird.setLife(6);
            bird.reset();
        }
    }

    private void renderGameOverScreen() {
        FONT.drawString(GAME_OVER_MSG, (Window.getWidth()/2.0-(FONT.getWidth(GAME_OVER_MSG)/2.0)), (Window.getHeight()/2.0-(FONT_SIZE/2.0)));
        String finalScoreMsg = FINAL_SCORE_MSG + score;
        FONT.drawString(finalScoreMsg, (Window.getWidth()/2.0-(FONT.getWidth(finalScoreMsg)/2.0)), (Window.getHeight()/2.0-(FONT_SIZE/2.0))+SCORE_MSG_OFFSET);
    }

    private void renderWinScreen() {
        FONT.drawString(CONGRATS_MSG, (Window.getWidth()/2.0-(FONT.getWidth(CONGRATS_MSG)/2.0)), (Window.getHeight()/2.0-(FONT_SIZE/2.0)));
        String finalScoreMsg = FINAL_SCORE_MSG + score;
        FONT.drawString(finalScoreMsg, (Window.getWidth()/2.0-(FONT.getWidth(finalScoreMsg)/2.0)), (Window.getHeight()/2.0-(FONT_SIZE/2.0))+SCORE_MSG_OFFSET);
    }

    private void renderLevelUpScreen(){
        FONT.drawString(LEVEL_UP_MSG, (Window.getWidth()/2.0-(FONT.getWidth(LEVEL_UP_MSG)/2.0)), (Window.getHeight()/2.0-(FONT_SIZE/2.0)));
    }

    private boolean detectPipeCollision(Rectangle birdBox, Rectangle topBox, Rectangle bottomBox) {
        // check for collision
        return birdBox.intersects(topBox) ||
                birdBox.intersects(bottomBox);
    }

    private boolean detectWeaponCollision(Rectangle birdBox, Rectangle weaponBox){
        return birdBox.intersects(weaponBox);
    }

    private void updateScore() {
        for(Pipes pipe: pipes){
            if (bird.getX() > pipe.getTopBox().right() && !pipe.isPassed()) {
                score += 1;
                pipe.setPassed(true);
            }
        }
        String scoreMsg = SCORE_MSG + score;
        FONT.drawString(scoreMsg, 100, 100);
    }

    private void setBackgroundImage(Image image){
        backgroundImage = image;
    }

    private void increaseTimescale(){
        timescale++;
        pipeSpawnRate = (int)(pipeSpawnRate / 1.5);
        weaponSpawnRate = (int)(weaponSpawnRate / 1.5);
    }

    private void decreaseTimescale(){
        timescale--;
        pipeSpawnRate = (int)(pipeSpawnRate * 1.5);
        weaponSpawnRate = (int)(weaponSpawnRate * 1.5);
    }

    /**
     * This returns the game level
     * @return int This returns the current game level
     */

    public static int getLevel(){
        return level;
    }

    /**
     * This returns the game's timescale
     * @return int This returns the current game timescale
     */

    public static int getTimescale(){
        return timescale;
    }

    private void addPipes(){
        int steel = random.nextInt(2);
        if(level == 0){
            pipes.add(new plasticPipe());
        }else{
            if(steel == 0){
                pipes.add(new plasticPipe());
            }else{
                pipes.add(new steelPipe());
            }
        }
    }
}
