package com.mygdx.roamgame.States;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.Timer;
import com.mygdx.roamgame.RoamGame;
import com.mygdx.roamgame.sprites.Environment;
import com.mygdx.roamgame.sprites.Food;
import com.mygdx.roamgame.sprites.Player;
import com.mygdx.roamgame.sprites.PoisonGrass;
import com.mygdx.roamgame.sprites.Zombie;


import java.sql.Time;
import java.util.Random;


/**
 * Created by Justin on 2016-02-21.
 */

public class PlayState extends State {



    public static final int SPRITE_SPACING = 16;
    public static final int GRID_UNIT = 32;

    // Environments
    private Environment startRoom;
    private Environment subMaze;

    // game state info
    private int score;
    public int healthBarVal;
    private long gameStartTime;
    private long gameDuration;
    private long lastZombieChangeTime;
    private long lastZombieSpawnTime;
    private int location;
    private boolean transitionFrame;
    private int bonusAmount =0;
    private int levelCounter = 0;
    private long lastBarrelScore;
    private long lastHealthAdded;
    private boolean scoreAnimation;
    private long scoreAnimationStart;
    private long healthAnimationStart;
    private boolean healthAnimation;
    private long lastPoisonedTime;
    private long lastHitTime;
    float totalElapsedTime = 0;
    private long inputRegistered = 0;
    private float inputFrequency;
    private float maxHealthLosable;
    private float maxDistance;
    private float maxDistanceSubMaze;
    private boolean isTouched = false;
    private int levelScore = 1;
    private long levelStartTime;
    private long levelDuration;

    // stats info
    private float realTimeFactor1;
    private float accumulatedFactor1;
    private float realTimeFactor2;
    private float accumulatedFactor2;
    private float currentFactor1;
    private float currentFactor2;
    FileHandle logFile;

    // game features / interactles
    private Player player;
    private Array<Rectangle> foodSupplies;
    private Food food;
    private Array<PoisonObject> redPoisonArr;
    private long lastRedPoisonSpawnTime;
    private PoisonGrass poisonGrass;
    private Array<Zombie> zombies;
    private Zombie zombie;
    public static final int MAX_ZOMBIES = 16;
    private Array<Rectangle> zombiesBounds;

    // Textures
    private Texture candyShop;
    private Texture dpadCross;
    private Sprite dpadCrossSprite;
    private Texture redArrowUp;
    Texture fb;
    Texture bb;
    Texture person;
    Texture gooTexture;

    float diffX = 0;
    float diffY = 0;

    // Rectangle for each direction
    private Rectangle leftPad;
    private Rectangle rightPad;
    private Rectangle upPad;
    private Rectangle downPad;
    private Rectangle leftUpPad;
    private Rectangle rightUpPad;
    private Rectangle leftDownPad;
    private Rectangle rightDownPad;

    // Music and Sounds
    private Music music;
    private Sound pickupItemSound;
    private Sound deathSound;
    private Music heartBeatSound;

    // Fonts
    BitmapFont font;
    private BitmapFont bfont;
    private FreeTypeFontGenerator generator;
    private FreeTypeFontGenerator.FreeTypeFontParameter parameter;

    private Random zRand;
    public static final int UP = 0, DOWN = 1, LEFT = 2, RIGHT = 3;
    private int NUM_POS = 4;
    public static final int SEC = 1000;

    Preferences prefs;

    // Animation related
    private long levelAnimationStart;
    private boolean levelAnimation;
    private long endLevelAnimationStart;
    private boolean endLevelAnimation;

    // Dialog
    Dialog barrelChooseDialog;
    Dialog endLevelDialog;

    //Tutorial Dialogs
    Dialog tut1Dialog;
    Dialog tut2Dialog;
    Dialog tut3Dialog;
    Dialog tut4Dialog;
    Dialog tut5Dialog;
    boolean tut1Complete;
    boolean tut2Complete;
    boolean tut3Complete;
    boolean tut4Complete;
    boolean tut5Complete;
    Skin skin;
    Stage stage;
    private boolean dialogShowing;

    // UI
    ShapeRenderer srender;
    Rectangle bloodScreen;

    // File
    FileHandle handle;

    public class PoisonObject
    {
        public Rectangle rect;
        public long timer;

        public PoisonObject()
        {
            rect = new Rectangle();
        }
    }

    public Sprite createScaledSprite(Texture texture) {

        float SCALE_RATIO =  Gdx.graphics.getWidth() / RoamGame.WIDTH;
        Sprite sprite = new Sprite(texture);
        sprite.getTexture().setFilter(Texture.TextureFilter.Linear,
                Texture.TextureFilter.Linear);
        sprite.setSize(sprite.getWidth() * SCALE_RATIO,
                sprite.getHeight() * SCALE_RATIO);

        float startX = 0.60f*Gdx.graphics.getWidth();
        float startY = 0.04f*Gdx.graphics.getHeight();

        leftPad = new Rectangle(startX - 0.04f*Gdx.graphics.getWidth(), startY + 50f*SCALE_RATIO, 80f*SCALE_RATIO + 0.04f*Gdx.graphics.getWidth(), 60f*SCALE_RATIO);
        rightPad = new Rectangle(startX + 80f*SCALE_RATIO,startY + 50f*SCALE_RATIO, 80f*SCALE_RATIO + 0.01f*Gdx.graphics.getWidth(), 60f*SCALE_RATIO);
        upPad = new Rectangle(startX + 50f*SCALE_RATIO, startY + 80f*SCALE_RATIO, 60f*SCALE_RATIO, 80f*SCALE_RATIO + 0.04f*Gdx.graphics.getHeight());
        downPad = new Rectangle(startX + 50f*SCALE_RATIO, 0, 60f*SCALE_RATIO, 80f*SCALE_RATIO + 0.02f*Gdx.graphics.getHeight());

        leftUpPad = new Rectangle(startX - 0.02f*Gdx.graphics.getWidth(), startY + 110f*SCALE_RATIO, 50f*SCALE_RATIO+ 0.02f*Gdx.graphics.getWidth(), 50f*SCALE_RATIO+ 0.02f*Gdx.graphics.getHeight());
        rightUpPad = new Rectangle(startX + 110f*SCALE_RATIO, startY + 110f*SCALE_RATIO, 50f*SCALE_RATIO+ 0.01f*Gdx.graphics.getWidth(), 50f*SCALE_RATIO+ 0.02f*Gdx.graphics.getHeight());
        leftDownPad = new Rectangle(startX - 0.02f*Gdx.graphics.getWidth(), 0, 50f*SCALE_RATIO+ 0.01f*Gdx.graphics.getWidth(), 50f*SCALE_RATIO + 0.02f*Gdx.graphics.getHeight());
        rightDownPad = new Rectangle(startX + 110f*SCALE_RATIO, 0, 50f*SCALE_RATIO + 0.01f*Gdx.graphics.getWidth(), 50f*SCALE_RATIO + 0.02f*Gdx.graphics.getHeight());

        return sprite;
    }


    protected PlayState(final GameStateManager gsm) {
        super(gsm);

        // file
        System.out.println(Gdx.files.getLocalStoragePath());
        handle = Gdx.files.local("gameInfoLog.txt");
        //handle.writeString("Hello!", true);
        // environments
        startRoom = new Environment("roguelike-pack/Map/transition_room.tmx", GRID_UNIT, 4, 1, 2, 4, 8, 2);
        subMaze = new Environment("roguelike-pack/Map/submaze_0.tmx", GRID_UNIT, 10, 1, 1, 10, 21, 2);

        // camera related info
        cam.setToOrtho(false, RoamGame.WIDTH, RoamGame.HEIGHT);

        // state related info
        score = 0;
        realTimeFactor1 = 0;
        realTimeFactor2 = 0;
        location = 0;
        transitionFrame = false;
        gameStartTime = TimeUtils.millis();
        maxHealthLosable = 200;//0.8f*Gdx.graphics.getWidth();
        maxDistance = 2720f;
        maxDistanceSubMaze = (22+10)*GRID_UNIT;
        prefs = Gdx.app.getPreferences("Stats");
        lastHitTime = 0;
        lastPoisonedTime = 0;

        // game features / interactables
        person = new Texture("steverogers.png");
        gooTexture = new Texture ("steverogers_goo.png");
        player = new Player(startRoom.getStartingPosX(), startRoom.getStartingPosY(), subMaze.pixelHeight, subMaze.pixelWidth, person, gooTexture);
        food = new Food();
        foodSupplies = new Array<Rectangle>();
        poisonGrass = new PoisonGrass();
        redPoisonArr = new Array<PoisonObject>();
        zombies = new Array<Zombie>();
        zombiesBounds = new Array<Rectangle>();
        spawnInitialBarrels();

        // font related info
        float screenScale = 1f*Gdx.graphics.getWidth()/(RoamGame.WIDTH);
        generator = new FreeTypeFontGenerator(Gdx.files.internal("bebas.ttf"));
        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = (int)(screenScale*( 20 ));//+ (int)((1f - alpha)*20f)));
        parameter.characters = "0123456789+-Gained!*pointshealthEnteringLevelCongratsReceivebonus ";
        bfont = generator.generateFont(parameter);
        generator.dispose();
        font = new BitmapFont();

        // music and sounds
        music = Gdx.audio.newMusic(Gdx.files.internal("animalcrossing.mp3"));
        music.setLooping(true);
        music.setVolume(0.1f);
        music.play();
        pickupItemSound = Gdx.audio.newSound(Gdx.files.internal("pickupSound.mp3"));
        deathSound = Gdx.audio.newSound(Gdx.files.internal("death.wav"));
        heartBeatSound = Gdx.audio.newMusic(Gdx.files.internal("heartbeat.wav"));

        // Textures
        candyShop = new Texture("candyshop.png");
        fb = new Texture("bar.png");
        bb = new Texture("bar_back.png");
        dpadCross = new Texture("transparentDark07.png");
        dpadCrossSprite = createScaledSprite(dpadCross);
        redArrowUp = new Texture(("redarrow.png"));

        // dialog setup
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        float scaleFactor = Gdx.graphics.getWidth()/ 1080f;
        skin.getFont("default-font").getData().setScale(scaleFactor, scaleFactor);
        //skin.add("default-font", bfont, BitmapFont.class);
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        dialogShowing = false;

        // UI
        bloodScreen = new Rectangle(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        srender = new ShapeRenderer();

        endLevelDialog = new Dialog("Finished Level " + levelCounter + "!", skin)
        {
            @Override
            public float getPrefWidth() {
                // force dialog width
                return Gdx.graphics.getWidth();
            }

            @Override
            public float getPrefHeight() {
                // force dialog height
                return 0.15f*Gdx.graphics.getHeight();
            }
            protected void result(Object object) {

                int selected = Integer.parseInt(object.toString());

                if (selected == 2 )
                {
                    gameDuration = TimeUtils.millis() - gameStartTime;
                    //accumulatedFactor1 = 1000*accumulatedFactor1/gameDuration;
                    //System.out.println(accumulatedFactor1);
                    prefs.putInteger("score", score);
                    prefs.putInteger("factor1", (int)currentFactor1);
                    prefs.putInteger("factor2", (int)currentFactor2);
                    prefs.flush();
                    handle.writeString("game " + score + " " + gameDuration + " " + String.valueOf((int)(inputFrequency)), true);
                    gsm.set(new ScoreScreenState(gsm));
                }
            }
        };

        endLevelDialog.getButtonTable().defaults().height(0.1f * Gdx.graphics.getHeight());
        endLevelDialog.getButtonTable().defaults().width(Gdx.graphics.getWidth() / 2);
        endLevelDialog.button("Continue at your own peril", 1L);
        endLevelDialog.button("Cash out!", 2L);

        Window.WindowStyle style = new Window.WindowStyle();
        style.titleFont = bfont;

        barrelChooseDialog = new Dialog("Points or health?", skin)
        {

            @Override
            public float getPrefWidth() {
                // force dialog width
                return 0.5f*Gdx.graphics.getWidth();
            }

            @Override
            public float getPrefHeight() {
                // force dialog height
                return 0.15f*Gdx.graphics.getHeight();
            }
            protected void result(Object object)
            {

                int baseScore = MathUtils.random(5,10);
                int scoreAmount = (levelCounter + 1)*baseScore;

                System.out.println("Option: " + object);

                int selected = Integer.parseInt(object.toString());

                // log event
                handle.writeString("event " + String.valueOf(TimeUtils.millis() - gameStartTime) + " " +  String.valueOf(levelCounter) + " 1 " + String.valueOf(maxHealthLosable - healthBarVal) + " " + String.valueOf(score) + " " + String.valueOf(levelScore) + " " + String.valueOf(selected) + " 0 0 0\n", true);

                if (selected == 1)
                {
                    healthBarVal -= 0.05f * maxHealthLosable;
                    player.boostPlayer(1.5f);
                    if (healthBarVal < 0)
                        healthBarVal = 0;
                    lastBarrelScore = scoreAmount;
                    scoreAnimationStart = TimeUtils.millis();
                    scoreAnimation = true;
                    // Play sound
                    pickupItemSound.play(0.5f);
                    levelScore *= scoreAmount;
                } else if (selected == 2)
                {
                    lastHealthAdded = 4*baseScore;
                    healthBarVal -= lastHealthAdded;
                    //player.boostPlayer(1.5f);
                    if (healthBarVal < 0)
                        healthBarVal = 0;
                    healthAnimationStart = TimeUtils.millis();
                    healthAnimation = true;
                    // Play sound
                    pickupItemSound.play(0.5f);
                }

                dialogShowing = false;


            }
        };

        //barrelChooseDialog.setSkin(skin);
        barrelChooseDialog.getButtonTable().defaults().height(0.1f * Gdx.graphics.getHeight());
        barrelChooseDialog.getButtonTable().defaults().width(Gdx.graphics.getWidth() / 4);

        barrelChooseDialog.button("Points", 1L);
        barrelChooseDialog.button("Health", 2L);

        setupTutorialDialogues();
    }

    public void setupTutorialDialogues() {

        Window.WindowStyle style = new Window.WindowStyle();
        style.titleFont = bfont;
        Gdx.input.setInputProcessor(stage);

        tut1Complete = false;
        tut2Complete = false;
        tut3Complete = false;
        tut4Complete = false;
        tut5Complete = false;

        tut1Dialog = new Dialog("Tutorial Dialog", skin)
        {
            @Override
            public float getPrefWidth() {
                // force dialog width
                return Gdx.graphics.getWidth();
            }
            @Override
            public float getPrefHeight() {
                // force dialog height
                return 0.25f*Gdx.graphics.getHeight();
            }
            protected void result(Object object) {

                int selected = Integer.parseInt(object.toString());
                dialogShowing = false;
            }
        };
        tut1Dialog.getButtonTable().defaults().height(0.1f * Gdx.graphics.getHeight());
        tut1Dialog.getButtonTable().defaults().width(Gdx.graphics.getWidth() / 2);
        tut1Dialog.button("Press to Continue", 1L);
        tut1Dialog.text(("Welcome to Roam Game! This is the loading room,\n" +
        "please walk forward through the exit to begin the game!"));

        tut2Dialog = new Dialog("Tutorial Dialog", skin)
        {
            @Override
            public float getPrefWidth() {
                // force dialog width
                return Gdx.graphics.getWidth();
            }
            @Override
            public float getPrefHeight() {
                // force dialog height
                return 0.25f*Gdx.graphics.getHeight();
            }
            protected void result(Object object) {

                int selected = Integer.parseInt(object.toString());
                dialogShowing = false;
            }
        };
        tut2Dialog.getButtonTable().defaults().height(0.1f * Gdx.graphics.getHeight());
        tut2Dialog.getButtonTable().defaults().width(Gdx.graphics.getWidth() / 2);
        tut2Dialog.button("Press to Continue", 1L);
        tut2Dialog.text(("Welcome to Roam Game! \n" +
                "Navigate the maze to find the exit!\n" +
                "Collect Barrels to gain points!\n" +
                "Avoid Reapers and Poison Grass!"));

        tut3Dialog = new Dialog("Tutorial Dialog", skin)
        {
            @Override
            public float getPrefWidth() {
                // force dialog width
                return Gdx.graphics.getWidth();
            }
            @Override
            public float getPrefHeight() {
                // force dialog height
                return 0.50f*Gdx.graphics.getHeight();
            }
            protected void result(Object object) {

                int selected = Integer.parseInt(object.toString());
                dialogShowing = false;
            }
        };
        tut3Dialog.getButtonTable().defaults().height(0.1f * Gdx.graphics.getHeight());
        tut3Dialog.getButtonTable().defaults().width(Gdx.graphics.getWidth() / 2);
        tut3Dialog.button("Press to Continue", 1L);
        tut3Dialog.text(("The Barrels are located in various parts of the submaze! \n" +
                "Your points will be multiplied for successive barrels that are collected!\n" +
                "Barrels can also be used for health, but doing so will break the\n" +
                "multiplicative points factor!"));

        tut4Dialog = new Dialog("Tutorial Dialog", skin)
        {
            @Override
            public float getPrefWidth() {
                // force dialog width
                return Gdx.graphics.getWidth();
            }
            @Override
            public float getPrefHeight() {
                // force dialog height
                return 0.25f*Gdx.graphics.getHeight();
            }
            protected void result(Object object) {

                int selected = Integer.parseInt(object.toString());
                dialogShowing = false;
            }
        };
        tut4Dialog.getButtonTable().defaults().height(0.1f * Gdx.graphics.getHeight());
        tut4Dialog.getButtonTable().defaults().width(Gdx.graphics.getWidth() / 2);
        tut4Dialog.button("Press to Continue", 1L);
        tut4Dialog.text(("Avoid the Reapers and Poison Grass! \n" +
                "Collisions with Reapers will greatly reduce health!\n" +
                "Collisions with Poison Grass will slow speed for a period of time!\n"));

        tut5Dialog = new Dialog("Tutorial Dialog", skin)
        {
            @Override
            public float getPrefWidth() {
                // force dialog width
                return Gdx.graphics.getWidth();
            }
            @Override
            public float getPrefHeight() {
                // force dialog height
                return 0.50f*Gdx.graphics.getHeight();
            }
            protected void result(Object object) {

                int selected = Integer.parseInt(object.toString());
                dialogShowing = false;
            }
        };
        tut5Dialog.getButtonTable().defaults().height(0.1f * Gdx.graphics.getHeight());
        tut5Dialog.getButtonTable().defaults().width(Gdx.graphics.getWidth() / 2);
        tut5Dialog.button("Press to Continue", 1L);
        tut5Dialog.text(("This is an intermittent room. \n" +
                "After successfully navigating a sub maze, you will have a choice!\n" +
                "To continue playing to get a higher score, or quit the game!\n" +
                "Beware! Barrels are of higher value in successive levels, but the \n" +
                "perils and threats are greater as well!"));

    } // function

    @Override
    public void resume() {
        Gdx.app.log("resume", startRoom.getStartingPosX() + " " + startRoom.getStartingPosY());
        // camera related info
        cam.setToOrtho(false, RoamGame.WIDTH, RoamGame.HEIGHT);
        //player = new Player(startRoom.getStartingPosX(), startRoom.getStartingPosY(), subMaze.pixelHeight, subMaze.pixelWidth, person, gooTexture);
        //player.resume();
    }

    @Override
    protected void handleInput() {

        //String logData = String.valueOf(TimeUtils.nanoTime() - gameStartTime) + " " ;
        //boolean isMoved = false;
        isTouched = false;


        if(Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) {
            //logData += "Up";
            player.moveUp();
            inputRegistered += 1;
            isTouched = true;
            //isMoved = true;
        }
        else if(Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) {
            //logData += "Down";
            player.moveDown();
            inputRegistered += 1;
            isTouched = true;
            //isMoved = true;
        }
        else if(Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
            //logData += "Left";
            player.moveLeft();
            inputRegistered += 1;
            isTouched = true;
            //isMoved = true;
        }
        else if(Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
            //logData += "Right";
            player.moveRight();
            inputRegistered += 1;
            isTouched = true;
            //isMoved = true;
        }

        if(Gdx.input.isTouched())
        {
            inputRegistered += 1;
            isTouched = true;
            Vector3 tmp=new Vector3(Gdx.input.getX(),Gdx.graphics.getHeight() - Gdx.input.getY(), 0);

            //System.out.println(tmp.x+ " " + tmp.y + " " + leftPad.x + " " + leftPad.y + " " + rightPad.x + " " + rightPad.y +  " " + upPad.x + " " + upPad.y +  " " + downPad.x + " " + downPad.y);
            if(leftPad.contains(tmp.x,tmp.y))
            {
                player.moveLeft();
            }
            else if(rightPad.contains(tmp.x,tmp.y))
            {
                player.moveRight();
            }
            else if(upPad.contains(tmp.x,tmp.y))
            {
                player.moveUp();
            }
            else if(downPad.contains(tmp.x,tmp.y))
            {
                player.moveDown();
            }
            else if (leftUpPad.contains(tmp.x, tmp.y))
            {
                player.moveLeftUp();
            }
            else if (rightUpPad.contains(tmp.x, tmp.y))
            {
                player.moveRightUp();
            }
            else if (leftDownPad.contains(tmp.x, tmp.y))
            {
                player.moveLeftDown();
            }
            else if (rightDownPad.contains(tmp.x, tmp.y))
            {
                player.moveRightDown();
            }
        }

        //logData += "\n";

        //if (isMoved == true)
        //    logFile.writeString(logData, true);

    }


    private void spawnInitialBarrels()
    {
        // Spawn 4 barrels
        int gridx = 0, gridy = 0;
        Rectangle food = new Rectangle();

        gridx = 1;
        gridy = 1;

        food.x = GRID_UNIT*gridx;
        food.y = GRID_UNIT*gridy;
        food.width = GRID_UNIT;
        food.height = GRID_UNIT;
        // check if location is occupied already
        if(subMaze.occupiedGrid[gridy][gridx] == -1) {
            foodSupplies.add(food);
            subMaze.occupiedGrid[gridy][gridx]  = 2;
        }

        food = new Rectangle();

        gridx = subMaze.gridWidth - 2;
        gridy = 1;

        food.x = GRID_UNIT*gridx;
        food.y = GRID_UNIT*gridy;
        food.width = GRID_UNIT;
        food.height = GRID_UNIT;
        // check if location is occupied already
        if(subMaze.occupiedGrid[gridy][gridx] == -1) {
            foodSupplies.add(food);
            subMaze.occupiedGrid[gridy][gridx] = 2;
        }

        food = new Rectangle();
        gridx = 1;
        gridy = subMaze.gridHeight - 2;

        food.x = GRID_UNIT*gridx;
        food.y = GRID_UNIT*gridy;
        food.width = GRID_UNIT;
        food.height = GRID_UNIT;
        // check if location is occupied already
        if(subMaze.occupiedGrid[gridy][gridx] == -1) {
            foodSupplies.add(food);
            subMaze.occupiedGrid[gridy][gridx]  = 2;
        }

        food = new Rectangle();
        gridx = subMaze.gridWidth - 2;
        gridy = subMaze.gridHeight - 2;

        food.x = GRID_UNIT*gridx;
        food.y = GRID_UNIT*gridy;
        food.width = GRID_UNIT;
        food.height = GRID_UNIT;
        // check if location is occupied already
        if(subMaze.occupiedGrid[gridy][gridx] == -1) {
            foodSupplies.add(food);
            subMaze.occupiedGrid[gridy][gridx]  = 2;
        }
    }

    private void updateFood(float dt)
    {
        // Check if player picked up food
        for (int index=0; index<foodSupplies.size; index++)
        {
            Rectangle item = foodSupplies.get(index);
            if (item.overlaps(player.getBounds())) {


                // health boost for high value
                //if (occupiedSubMazeGrid[Math.round(item.y/GRID_UNIT)][Math.round(item.x/GRID_UNIT)] == 2) {
                barrelChooseDialog.show(stage);
                dialogShowing = true;


                //barrelChooseDialog.setSize(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 3);


                // Clear item
                subMaze.occupiedGrid[Math.round(item.y/GRID_UNIT)][Math.round(item.x/GRID_UNIT)] = -1;
                foodSupplies.removeIndex(index);


            }
        }

        // Spawn new food every 2 seconds
        //if((TimeUtils.millis() - lastFoodSpawnTime) > 0.5f*SEC) {
        //    spawnFood();
        //}
    }

    private void updateRedPoison(float dt)
    {
        // Check if player hit poison
        for (int index=0; index<redPoisonArr.size; index++)
        {

            Rectangle item = redPoisonArr.get(index).rect;
            if (item.overlaps(player.getBounds()) && (TimeUtils.millis() - lastPoisonedTime) > 2*SEC) {
                deathSound.play(0.7f);
                healthBarVal+=5;
                player.slowPlayer();
                lastPoisonedTime = TimeUtils.millis();

                handle.writeString("event " + String.valueOf(TimeUtils.millis() - gameStartTime) + " " +  String.valueOf(levelCounter) + " 2 " + String.valueOf(maxHealthLosable - healthBarVal) + " " + String.valueOf(score) + " " + String.valueOf(levelScore) + " 0 2 0 0\n", true);


            }

            if(TimeUtils.millis() - redPoisonArr.get(index).timer > 6*SEC) {
                subMaze.occupiedGrid[Math.round(item.y/GRID_UNIT)][Math.round(item.x/GRID_UNIT)] = -1;
                redPoisonArr.removeIndex(index);
            }

        }

        // Spawn new red poison
        if((TimeUtils.millis() - lastRedPoisonSpawnTime) > Math.max(200, 2000 - levelCounter*100)) {
            spawnRedPoison();
        }
    }

    private void updateHazards(float dt)
    {

        // update zombie positions
        for (int i = 0; i < zombies.size; i++) {
            //Zombie z =
            zombie = zombies.get(i);


            //System.out.println(zombie.getDirection());
            if (zombie.getPosition().y <= 0) {
                zombie.setDirection(PlayState.UP);
            }
            else if (zombie.getPosition().y >= (subMaze.pixelHeight - zombie.getTexture().getRegionHeight())) {
                zombie.setDirection(PlayState.DOWN);
            }
            else if (zombie.getPosition().x <= 0) {
                zombie.setDirection(PlayState.RIGHT);
            }
            else if (zombie.getPosition().x >= (subMaze.pixelWidth - zombie.getTexture().getRegionWidth())) {
                zombie.setDirection(PlayState.LEFT);
            }

            boolean overlapped = false;

            for (int index = 0; index < subMaze.obstacles.size; index++) {
                Rectangle item = subMaze.obstacles.get(index);
                if (item.overlaps(zombie.getBounds())) {

                    overlapped = true;


                }
            }

            if (overlapped == true)
            {
                //System.out.println("overlapped");
                zombie.setPosition(zombie.oldPosition.x, zombie.oldPosition.y);

                if (zombie.getDirection() == PlayState.UP) {
                    //System.out.println(i + " up");
                    zombie.setDirection(PlayState.LEFT);
                }
                else if (zombie.getDirection() == PlayState.LEFT) {
                    //System.out.println(i + " left");
                    zombie.setDirection(PlayState.DOWN);
                }
                else if (zombie.getDirection() == PlayState.DOWN) {
                    //System.out.println(i + " down");
                    zombie.setDirection(PlayState.RIGHT);
                }
                else if (zombie.getDirection() == PlayState.RIGHT) {
                    //System.out.println(i + " right");
                    zombie.setDirection(PlayState.UP);
                }
            }

            //System.out.println(z.getDirection());
            zombie.move(zombie.getDirection());

            zombie.update(dt);



            zombies.set(i, zombie);
            if(zombie.getBounds().overlaps(player.getBounds())) {
                if ((TimeUtils.millis() - lastHitTime) > (int)(0.5f*(float)SEC)) {
                    healthBarVal+=20;
                    zombie.playSound();
                    lastHitTime = TimeUtils.millis();

                    // log event
                    handle.writeString("event " + String.valueOf(TimeUtils.millis() - gameStartTime) + " " +  String.valueOf(levelCounter) + " 2 " + String.valueOf(maxHealthLosable - healthBarVal) + " " + String.valueOf(score) + " " + String.valueOf(levelScore) + " 0 1 0 0\n", true);

                }
            }

            if((TimeUtils.millis() - lastZombieChangeTime) > 1*SEC)
            {
                int zombieDir = zRand.nextInt(NUM_POS);
                int chance = zRand.nextInt(3);
                if (chance == 0) {
                    zombies.get(i).setTurboOn(false);
                    zombies.get(i).setDirection(zombieDir);
                    zombies.get(i).setSpeedFaster();
                } else if (chance == 1) {
                    //System.out.println("turbo on");
                    zombies.get(i).setDirection(zombieDir);
                    zombies.get(i).setTurboOn(true);
                }

                lastZombieChangeTime = TimeUtils.millis();
            }
        }

        // Spawn new zombie every 2 seconds
        if((TimeUtils.millis() - lastZombieSpawnTime) > Math.max(1000, 3000 - levelCounter*150))
            spawnZombie();
    }

    private void updateGameInfo(float dt)
    {

        float distance;

        totalElapsedTime += dt;

        inputFrequency = (float)inputRegistered / (float)totalElapsedTime;


        // Update Tolerance Metric
        if (location == 0) {
            diffX = Math.abs(player.getPosition().x - startRoom.getStartingPosX());
            diffY = Math.abs(player.getPosition().y - startRoom.getStartingPosY());
            distance = maxDistance;

        } else
        {
            diffX = Math.abs(player.getPosition().x - subMaze.getStartingPosX());
            diffY = Math.abs(player.getPosition().y -subMaze.getStartingPosY());
            distance = maxDistanceSubMaze;
        }

        if (distance == 0)
            distance = 1;

        realTimeFactor1 =  15 + 60*(1 - (player.getSpeedSum()/180f));

        realTimeFactor2 = 12 + 1f*48*(1 - (healthBarVal/maxHealthLosable))*(1 - ((diffX + diffY)/distance));// + 0.5f*48*(1-((TimeUtils.millis() - gameStartTime)/(5*60000f)));
        //System.out.println(diffX+diffY);
        accumulatedFactor1 += realTimeFactor1*dt;
        accumulatedFactor2 += realTimeFactor2*dt;
        currentFactor1 = 1000f*accumulatedFactor1/(TimeUtils.millis() - gameStartTime);
        currentFactor2 = 1000f*accumulatedFactor2/(TimeUtils.millis() - gameStartTime);

        if (healthBarVal >= 0.5f*maxHealthLosable)
        {
            if (!heartBeatSound.isPlaying())
            {
                heartBeatSound.setLooping(true);
                heartBeatSound.play();
            }
            float volume =  2*((float)healthBarVal /maxHealthLosable - 0.5f);
            heartBeatSound.setVolume(volume);
        } else
        {
            if(heartBeatSound.isPlaying())
                heartBeatSound.pause();
        }
        //System.out.println(realTimeTolerance);

        // Health decays with time spent in room
        //if(TimeUtils.millis() - timeVal > SEC) {
        //    healthBarVal += 1;
        //    timeVal = TimeUtils.millis();
        //}

        // Game Over
        if (healthBarVal >= maxHealthLosable) {
            deathSound.play();
            score = score/2;
            gameDuration = TimeUtils.millis() - gameStartTime;
            //accumulatedFactor1 = 1000*accumulatedFactor1/gameDuration;
            prefs.putInteger("score", score);
            prefs.putInteger("factor1", (int)currentFactor1);
            prefs.putInteger("factor2", (int)currentFactor2);
            prefs.flush();

            handle.writeString("game " + score + " " + gameDuration + " " + String.valueOf((int)(inputFrequency)) + "\n", true);
            gsm.set(new ScoreScreenState(gsm));
        }
    }

    @Override
    public void update(float dt) {

        if (!dialogShowing) {
            handleInput();
            player.update(dt);

            // make sure this is called after player update
            checkPlayerBounds();
            Gdx.app.log("Position", player.getPosition().x + " " + player.getPosition().y);

            cam.position.x = Math.round(player.getPosition().x);
            cam.position.y = Math.round(player.getPosition().y);

            if (location == 0) {


            } else if (location == 1) {
                updateFood(dt);
                updateHazards(dt);
                updateRedPoison(dt);
                //updateSubMazeZombie(dt);
            }
            //updateHealthPack();


            updateGameInfo(dt);

        }

        cam.update();
    }

    private void checkPlayerBounds()
    {

        if (location == 0) {
            for (int index = 0; index < startRoom.obstacles.size; index++) {
                Rectangle item = startRoom.obstacles.get(index);
                if (item.overlaps(player.getBounds())) {
                    //System.out.println("overlapped");
                    player.setPosition(player.oldPosition);
                }
            }

            if (startRoom.getExitRectangle().overlaps(player.getBounds())) {
                location = 1;
                transitionFrame = true;

                levelStartTime = TimeUtils.millis();
                levelDuration = 0;

                levelAnimationStart = TimeUtils.millis();
                levelAnimation = true;

            }

        } else if (location == 1) {
            for (int index = 0; index < subMaze.obstacles.size; index++) {
                Rectangle item =subMaze.obstacles.get(index);
                if (item.overlaps(player.getBounds())) {
                    //System.out.println("overlapped");
                    player.setPosition(player.oldPosition);
                }
            }

            if (subMaze.getExitRectangle().overlaps(player.getBounds())) {
                location = 0;
                transitionFrame = true;

                levelDuration = TimeUtils.millis() - levelStartTime;

                // log info
                handle.writeString("event " + String.valueOf(TimeUtils.millis() - gameStartTime) + " " +  String.valueOf(levelCounter) + " 3 " + String.valueOf(maxHealthLosable - healthBarVal) + " " + String.valueOf(score) + " " + String.valueOf(levelScore) + " 0 0 0 " + String.valueOf(levelDuration) + "\n", true);

                // level up
                levelCounter += 1;

                bonusAmount = (levelCounter + 1)*levelScore;
                score += levelScore + bonusAmount;
                levelScore = 1;

                endLevelAnimation = true;
                endLevelAnimationStart = TimeUtils.millis();

                System.out.println("Reached Level " + String.valueOf(levelCounter));

                // load new map and reset grid

                subMaze = new Environment("roguelike-pack/Map/submaze_" + String.valueOf(levelCounter % 7) + ".tmx", GRID_UNIT, 10, 1, 1, 10, 21, 2);

                foodSupplies = new Array<Rectangle>();
                redPoisonArr = new Array<PoisonObject>();
                zombiesBounds = new Array<Rectangle>();
                lastHitTime = 0;
                lastPoisonedTime = 0;

                //zombie = new Zombie(0, 0, UP);
                zombies = new Array<Zombie>();

                spawnInitialBarrels();

                int timerVal = 0;
                if (tut5Complete)
                    timerVal = 1;
                else
                    timerVal = 8;
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        endLevelDialog.show(stage);
                        //endLevelDialog.setSize(0.8f * (float) Gdx.graphics.getWidth(), Gdx.graphics.getHeight() / 3);
                    }
                }, timerVal);

                if (!tut5Complete) {
                    tut5Dialog.show(stage);
                    tut5Complete = true;
                }



            }
        }

    }

    // Render game World
    private void mainRender(SpriteBatch sb)
    {
        if (location == 0) {
            startRoom.renderer.setView(cam);
            startRoom.renderer.render();

            sb.setProjectionMatrix(cam.combined);
            //sb.getTransformMatrix().idt();

            //sb.setProjectionMatrix(cam.combined);
            sb.begin();

            if (!tut2Complete) {
                sb.draw(redArrowUp, startRoom.getExitRectangle().getX() - 30, startRoom.getExitRectangle().getY() - 50, 100, 100);
            }

            if (transitionFrame == true) {
                System.out.println("Transitioned back to outside");
                Vector3 newPosition = new Vector3(5* GRID_UNIT, 1*GRID_UNIT, 0);
                player.setPosition(newPosition);
            }

            sb.draw(player.getTexture(), player.getPosition().x, player.getPosition().y);

            sb.end();
            transitionFrame = false;
        }
        else if (location == 1) {
            subMaze.renderer.setView(cam);
            subMaze.renderer.render();

            sb.setProjectionMatrix(cam.combined);
            sb.begin();

            if (!tut5Complete) {
                sb.draw(redArrowUp, subMaze.getExitRectangle().getX() - 20, subMaze.getExitRectangle().getY() - 50, 100, 100);
            }

            if (transitionFrame == true) {
                System.out.println("Transitioned");
                Vector3 newPosition = new Vector3(10 * GRID_UNIT, 1 * GRID_UNIT, 0);
                player.setPosition(newPosition);
            }

            //draw zombies
            for (Zombie z : zombies) {
                sb.draw(z.getTexture(), z.getPosition().x, z.getPosition().y);
            }

            // draw red poison
            for (PoisonObject redPoisonItem : redPoisonArr) {
                sb.draw(poisonGrass.getRedGrass(), redPoisonItem.rect.x, redPoisonItem.rect.y);
            }

            // Drawing food items
            for (Rectangle foodItem : foodSupplies) {
                sb.draw(food.getBarrel(), foodItem.x, foodItem.y);
                sb.draw(food.getFoodTexture(subMaze.occupiedGrid[Math.round(foodItem.y / GRID_UNIT)][Math.round(foodItem.x) / GRID_UNIT]), foodItem.x, foodItem.y);
            }

            sb.draw(player.getTexture(), player.getPosition().x, player.getPosition().y);
            sb.end();
            transitionFrame = false;

            if (!tut2Complete) {
                tut2Dialog.show(stage);
                tut2Complete = true;
                dialogShowing = true;

                int timerVal = 10;
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        tut3Dialog.show(stage);
                        tut3Complete = true;
                        dialogShowing = true;
                        //endLevelDialog.setSize(0.8f * (float) Gdx.graphics.getWidth(), Gdx.graphics.getHeight() / 3);
                    }
                }, timerVal);
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        tut4Dialog.show(stage);
                        tut4Complete = true;
                        dialogShowing = true;
                        //endLevelDialog.setSize(0.8f * (float) Gdx.graphics.getWidth(), Gdx.graphics.getHeight() / 3);
                    }
                }, timerVal*2);
            }
        }
    }

    // Render Game Info
    private void hudRender(SpriteBatch hb)
    {

        hb.begin();
        //Draw health bar
        Vector3 positionBar = new Vector3(0.20f*Gdx.graphics.getWidth(), 0.96f*Gdx.graphics.getHeight(), 0);

        NinePatch frontBar= new NinePatch(fb, 8, 8, 8, 8);
        NinePatch backBar = new NinePatch(bb, 8, 8, 8, 8);

        float fullBar = 0.75f*Gdx.graphics.getWidth();

        backBar.draw(hb, positionBar.x, positionBar.y, fullBar, 0.03f*Gdx.graphics.getHeight());
        float fbVal = (maxHealthLosable-healthBarVal)*fullBar/(maxHealthLosable);
        if (fbVal > 0)
            frontBar.draw(hb, positionBar.x, positionBar.y, fbVal, 0.03f*Gdx.graphics.getHeight());

        // Draw Score

        Vector3 positionScore = new Vector3(0.03f*Gdx.graphics.getWidth(),0.9625f*Gdx.graphics.getHeight(), 0);
        font.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        //font.draw(hb, "Score : " + String.valueOf(score), positionScore.x, positionScore.y);

        Label.LabelStyle textStyle;
        Label text;
        textStyle = new Label.LabelStyle();
        textStyle.font = font;
        text = new Label("Score : " + String.valueOf(score), textStyle);
        float screenScale = 1f*Gdx.graphics.getWidth()/(RoamGame.WIDTH);
        //System.out.println(RoamGame.WIDTH + " " + Gdx.graphics.getWidth());
        text.setFontScale(screenScale, screenScale);
        text.setPosition(positionScore.x, positionScore.y);
        text.draw(hb, 2f);

        Label textTolerance;

        textStyle = new Label.LabelStyle();
        textStyle.font = font;

        if (scoreAnimation) {

            float alpha = (1f - ((float)(TimeUtils.millis() - scoreAnimationStart)/3000f));

            bfont.setColor(new Color(255, 255, 0, alpha));
            bfont.draw(hb, "* "+lastBarrelScore + " points!", Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
            if (TimeUtils.millis() - scoreAnimationStart > 3000 )
            {
                scoreAnimation = false;
            }
        }


        if (healthAnimation) {

            float alpha = (1f - ((float)(TimeUtils.millis() - healthAnimationStart)/3000f));

            bfont.setColor(new Color(255, 255, 255, alpha));
            bfont.draw(hb, "+ "+lastHealthAdded + " health!", Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
            if (TimeUtils.millis() - healthAnimationStart > 3000 )
            {
                healthAnimation = false;
            }
        }

        if (levelAnimation) {
            //show second tutorial
            /*if(firstTutorial) {
                entranceDialog.show(stage);
                dialogShowing = true;
                firstTutorial = false;
            }*/

            float alpha = (1f - ((float)(TimeUtils.millis() - 500 - levelAnimationStart)/2500f));
            if ((TimeUtils.millis() - levelAnimationStart > 500) ) {
                bfont.setColor(new Color(255, 255, 255, alpha));
                bfont.draw(hb, "Entering Level " + (levelCounter + 1) + "!", (int)(0.35f*(float)Gdx.graphics.getWidth()) , Gdx.graphics.getHeight() / 3);
            }
            if ((TimeUtils.millis() - levelAnimationStart > 3000) )
            {
                levelAnimation = false;
            }
        }

        if (endLevelAnimation) {

            float alpha = (1f - ((float)(TimeUtils.millis() - 500 - endLevelAnimationStart)/2500f));
            if ((TimeUtils.millis() - endLevelAnimationStart > 500) ) {
                bfont.setColor(new Color(255, 255, 0, alpha));
                bfont.draw(hb, "Congrats! Receive " + bonusAmount + " points!", (int)(0.25f*(float)Gdx.graphics.getWidth()) , Gdx.graphics.getHeight() / 3);
            }
            if ((TimeUtils.millis() - endLevelAnimationStart > 3000) )
            {
                endLevelAnimation = false;
            }
        }

        if (!tut1Complete) {
            //cam.setToOrtho(false, RoamGame.WIDTH, RoamGame.HEIGHT);
            tut1Dialog.show(stage);
            //hb.draw(redArrowUp, startRoom.getExitRectangle().getX() - 30, startRoom.getExitRectangle().getY() - 50, 100, 100);
            //tut1Dialog.setPosition(0, 0);
            //dialogShowing = true;
            tut1Complete = true;
            //hb.draw(redArrowUp, startRoom.getExitRectangle().getX()-30, startRoom.getExitRectangle().getY()-50, 100, 100);
        }
//        textTolerance = new Label("Current Health: " + String.valueOf((int)(100*(1 - (healthBarVal/maxHealthLosable)))) , textStyle);
        textTolerance = new Label("Level Score: " + String.valueOf(levelScore) , textStyle);
        //System.out.println(RoamGame.WIDTH + " " + Gdx.graphics.getWidth());
        textTolerance.setFontScale(screenScale, screenScale);
        textTolerance.setPosition(positionScore.x, positionScore.y - 0.03f * Gdx.graphics.getHeight());

        textTolerance.draw(hb, 2f);

//        textTolerance = new Label("Touched?: " + String.valueOf(isTouched), textStyle);
//        //System.out.println(RoamGame.WIDTH + " " + Gdx.graphics.getWidth());
//        textTolerance.setFontScale(screenScale, screenScale);
//        textTolerance.setPosition(positionScore.x, positionScore.y - 0.06f*Gdx.graphics.getHeight());
//
//        textTolerance.draw(hb, 2f);
//
//        textTolerance = new Label("Input Frequency: " + String.valueOf((double)Math.round(inputFrequency*100)/100), textStyle);
//        //System.out.println(RoamGame.WIDTH + " " + Gdx.graphics.getWidth());
//        textTolerance.setFontScale(screenScale, screenScale);
//        textTolerance.setPosition(positionScore.x, positionScore.y - 0.09f*Gdx.graphics.getHeight());
//
//        textTolerance.draw(hb, 2f);


        // Draw direction pad
        Vector3 dPadPosition = new Vector3(0.60f * Gdx.graphics.getWidth(), 0.04f*Gdx.graphics.getHeight(), 0);
        //Color c = hb.getColor();
        //hb.setColor(c.r, c.g, c.b, .3f);//set alpha to 0.3
        //hb.draw(dpadCircle,dPadPosition.x, dPadPosition.y);
        dpadCrossSprite.setPosition(dPadPosition.x, dPadPosition.y);
        dpadCrossSprite.setAlpha(.3f);
        dpadCrossSprite.draw(hb);

        //hb.draw(dpadCrossSprite, dPadPosition.x, dPadPosition.y);
        //hb.setColor(c.r, c.g, c.b, 1f);
        hb.end();

        // draw blood screen
        if (healthBarVal >= 0.5f*maxHealthLosable)
        {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            srender.begin(ShapeRenderer.ShapeType.Filled);
            float alpha = 2*((float)healthBarVal/maxHealthLosable - 0.5f);
            System.out.println(alpha);
            Color color = new Color(1f, 0f, 0f, alpha);
            srender.setColor(color);
            srender.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            srender.end();
            Gdx.gl.glDisable(GL20.GL_BLEND);

        }

    }

    @Override
    public void render(SpriteBatch sb, SpriteBatch hb) {

        mainRender(sb);
        hudRender(hb);

        stage.act();
        stage.draw();

        /*ShapeRenderer sr;
        sr = new ShapeRenderer();
        sr.begin(ShapeRenderer.ShapeType.Line);
        sr.setColor(1, 0, 0, 0);
        sr.rect(5, 5, 100, 100);
        sr.end();*/
    }

//    private void spawnFood() {
//        int gridx = 0, gridy = 0;
//        Rectangle food = new Rectangle();
//
//        gridx = MathUtils.random(0, subMaze.gridWidth-1);
//        gridy = MathUtils.random(0, subMaze.gridHeight-1);
//
//        food.x = GRID_UNIT*gridx;
//        food.y = GRID_UNIT*gridy;
//        food.width = GRID_UNIT;
//        food.height = GRID_UNIT;
//        // check if location is occupied already
//        if(subMaze.occupiedGrid[gridy][gridx] == -1) {
//            foodSupplies.add(food);
//            subMaze.occupiedGrid[gridy][gridx]  = 2;
//        }
//        lastFoodSpawnTime = TimeUtils.millis();
//    }

    private void spawnRedPoison() {
        int gridx = 0, gridy = 0;
        Rectangle redPoisonRect = new Rectangle();

        gridx = MathUtils.random(0, subMaze.gridWidth-1);
        gridy = MathUtils.random(0, subMaze.gridHeight-1);

        redPoisonRect.x = GRID_UNIT*gridx;
        redPoisonRect.y = GRID_UNIT*gridy;
        redPoisonRect.width = GRID_UNIT;
        redPoisonRect.height = GRID_UNIT;

        PoisonObject newObject = new PoisonObject();
        newObject.rect = redPoisonRect;
        newObject.timer = TimeUtils.millis();

        // check if location is occupied already
        if(subMaze.occupiedGrid[gridy][gridx] == -1) {
            redPoisonArr.add(newObject);
            subMaze.occupiedGrid[gridy][gridx]  = -2;
        }
        lastRedPoisonSpawnTime = TimeUtils.millis();
    }

    private void spawnZombie() {
        //for (int i = 0; i < MAX_ZOMBIES; i++) {
        if (zombies.size < MAX_ZOMBIES) {
            zRand = new Random();
            Rectangle zombie = new Rectangle();
            // randomly pick direction and position
            int zombieDir = zRand.nextInt(NUM_POS);
            //System.out.println(zombieDir);
            int gridx = MathUtils.random(0, subMaze.gridWidth - 1);
            int gridy = MathUtils.random(0, subMaze.gridHeight - 1);
            zombie.x = GRID_UNIT * gridx;
            zombie.y = GRID_UNIT * gridy;
            zombie.width = GRID_UNIT;
            zombie.height = GRID_UNIT;

            Zombie newZombie = new Zombie(zombie.x, zombie.y, zombieDir, 0);

            boolean overlapped = false;

            for (int index = 0; index < subMaze.obstacles.size; index++) {
                Rectangle item =subMaze.obstacles.get(index);
                if (item.overlaps(newZombie.getBounds())) {
                    overlapped = true;

                }
            }
            // check if location is occupied already
            if ((subMaze.occupiedGrid[gridy][gridx] == -1) && overlapped == false) {
                zombiesBounds.add(zombie);
                subMaze.occupiedGrid[gridy][gridx] = zombieDir;
                zombies.add(newZombie);
                //Zombie z = zombies.get(i);
                // System.out.println(z.getDirection());
            }
        }

        lastZombieSpawnTime = TimeUtils.millis();
    }

    @Override
    public void dispose() {
        player.dispose();
        candyShop.dispose();
        if (food!=null)
            food.dispose();
        if(poisonGrass!=null)
            poisonGrass.dispose();
        if (zombie != null)
            zombie.dispose();
        music.dispose();
        font.dispose();
        fb.dispose();
        bb.dispose();
        stage.dispose();
        heartBeatSound.dispose();
        person.dispose();
        gooTexture.dispose();
    }

}
