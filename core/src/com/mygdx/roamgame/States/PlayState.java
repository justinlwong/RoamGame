package com.mygdx.roamgame.States;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.Timer;
import com.mygdx.roamgame.RoamGame;
import com.mygdx.roamgame.sprites.AbilityButton;
import com.mygdx.roamgame.sprites.Environment;
import com.mygdx.roamgame.sprites.Food;
import com.mygdx.roamgame.sprites.Player;
import com.mygdx.roamgame.sprites.PoisonGrass;
import com.mygdx.roamgame.sprites.Zombie;


import java.sql.Time;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;


/**
 * Created by Justin on 2016-02-21.
 */

public class PlayState extends State {

    class Node {
        public int x;
        public int y;
        public Node parent;
        public int G;
        public int H;
        List<Node> children;
    }

    InputMultiplexer im = new InputMultiplexer();

    public static final int SPRITE_SPACING = 16;
    public static final int GRID_UNIT = 32;
    public static final int VERSION = 3;

    // Environments
    private Environment startRoom;
    private Environment subMaze;

    // game state info
    private String movementLog;
    private int lastNewDirection;
    private boolean isDemo;
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
    private boolean abilityAnimation;
    private long abilityAnimationStart;
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
    private int levelScore = 0;
    private long levelStartTime;
    private long levelDuration;
    private float lastUpdateTime = 0f;

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
    private Array<Rectangle> abilityBoxes;
    private Food food;
    private Array<PoisonObject> redPoisonArr;
    private long lastRedPoisonSpawnTime;
    private PoisonGrass poisonGrass;
    private Array<Zombie> zombies;
    private Zombie zombie;
    public static final int MAX_ZOMBIES = 25;
    private Array<Rectangle> zombiesBounds;
    private boolean abilityBoxPickedUp = false;
    private int zombiePivot;

    // Textures
    AssetManager manager = new AssetManager();
    private Texture candyShop;
    private Texture dpadCross;
    private Sprite dpadCrossSprite;
    private Texture redArrowUp;
    Texture fb;
    Texture bb;
    Texture person;
    Texture gooTexture;
    AbilityButton abilityButton;
    Texture buttonTexture;
    Texture invincibleButtonTexture;
    Texture freezeButtonTexture;
    Texture fastButtonTexture;
    Sprite invincibleButton;
    Sprite freezeButton;
    Sprite fastButton;
    Sprite healthLossButton;
    Sprite slowMotionButton;
    Sprite timerLossButton;
    Texture questionMarkSmall;
    Texture xscreen;
    Texture healthLossButtonTexture;
    Texture slowMotionButtonTexture;
    Texture timerLossButtonTexture;

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

    private int lastTouchedPointX;
    private int lastTouchedPointY;
    private int [] direction_filter = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    // Music and Sounds
    private Music music;
    private Sound pickupItemSound;
    private Sound deathSound;
    private Music heartBeatSound;
    private Sound pickupAbilitySound;
    private Music invincibilityMusic;
    private Sound reaperFreezeModeMusic;
    private Sound reaperFastModeMusic;
    private Music timerSound;
    private Music hauntSound;
    private Sound clinkSound;

    private float pitch = 1f;

    // Fonts
    BitmapFont font;
    private BitmapFont bfont;
    private BitmapFont bfont_small;
    private FreeTypeFontGenerator generator;
    private FreeTypeFontGenerator.FreeTypeFontParameter parameter;
    private GlyphLayout layout;

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
    Dialog exitDialog;
    Skin skin;
    Stage stage;
    private boolean dialogShowing;
    private boolean endDialogShowing;

    //Tutorial Dialogs
    Dialog tut1Dialog;
    Dialog tut2Dialog;
    Dialog tut3Dialog;
    Dialog tut3BDialog;
    Dialog tut4Dialog;
    Dialog tut5Dialog;
    Dialog tut6Dialog;
    Dialog tut6BDialog;
    Dialog tut6CDialog;
    boolean tut1Complete;
    boolean tut2Complete;
    boolean tut3Complete;
    boolean tut4Complete;
    boolean tut5Complete;
    boolean tut6Complete;

    // UI
    ShapeRenderer srender;
    Rectangle bloodScreen;

    // Abilities
    private boolean invincibleMode = false;
    private boolean reaperFreezeMode = false;
    private boolean reaperFastMode = false;
    private boolean playerSlowMode = false;
    private boolean healthLossMode = false;
    private boolean timerLossMode = false;

    private int abilityActive = 0;
    private float abilityBox_X;
    private float abilityBox_Y;
    private int numBoxes = 0;

    int [][]touchArray = new int[][]
            {{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                    {4, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2},
                    {4, 4, 1, 1, 1, 1, 1, 1, 1, 2, 2},
                    {4, 4, 4, 1, 1, 1, 1, 1, 2, 2, 2},
                    {4, 4, 4, 4, 1, 1, 1, 2, 2, 2, 2},
                    {4, 4, 4, 4, 4, 0, 2, 2, 2, 2, 2},
                    {4, 4, 4, 4, 3, 3, 3, 2, 2, 2, 2},
                    {4, 4, 4, 3, 3, 3, 3, 3, 2, 2, 2},
                    {4, 4, 3, 3, 3, 3, 3, 3, 3, 2, 2},
                    {4, 3, 3, 3, 3, 3, 3, 3, 3, 3, 2},
                    {3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3}};

    int [][] heatmap_snow_0 = new int [][]
            {{0,0,0,0,0,0,0,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0},
            {0,24,23,0,9,8,7,6,5,4,3,2,0,40,39,38,37,36,35,34,35,0},
            {0,23,22,0,10,9,8,7,6,5,4,3,0,39,38,37,36,35,34,33,34,0},
            {0,22,21,0,11,10,0,0,0,0,5,4,0,40,39,0,0,0,0,32,33,0},
            {0,21,20,0,12,11,0,9,8,7,6,5,0,41,40,0,28,29,30,31,32,0},
            {0,20,19,0,13,12,0,10,9,8,7,6,0,42,41,0,27,28,29,30,31,0},
            {0,19,18,0,14,13,0,11,10,0,0,0,0,43,42,0,26,27,0,31,32,0},
            {0,18,17,16,15,14,0,12,11,0,47,46,45,44,43,0,25,26,0,32,33,0},
            {0,19,18,17,16,15,0,13,12,0,48,47,46,45,44,0,24,25,0,33,34,0},
            {0,0,0,0,0,0,0,14,13,0,0,0,0,0,0,0,23,24,0,34,35,0},
            {0,79,78,77,76,77,0,15,14,15,16,17,18,19,20,21,22,23,0,35,36,0},
            {0,78,77,76,75,76,0,16,15,16,17,18,19,20,21,22,23,24,0,36,37,0},
            {0,0,0,0,74,75,0,0,0,0,0,0,0,0,0,0,0,0,0,37,38,0},
            {0,72,71,72,73,74,75,76,77,78,79,80,83,82,83,0,41,40,39,38,39,0},
            {0,71,70,71,72,73,74,75,76,77,78,79,80,81,82,0,42,41,40,39,40,0},
            {0,70,69,0,0,0,0,0,0,0,0,0,0,0,0,0,43,42,0,0,0,0},
            {0,69,68,0,62,61,60,59,58,57,56,55,0,47,46,45,44,43,0,57,58,0},
            {0,70,67,0,61,60,59,58,57,56,55,54,0,48,47,46,45,44,0,56,57,0},
            {0,67,66,0,62,61,0,0,0,0,54,53,0,49,48,0,0,0,0,55,56,0},
            {0,66,65,64,63,62,63,64,65,0,53,52,51,50,49,50,51,52,53,54,55,0},
            {0,67,66,65,64,63,64,65,66,0,54,53,52,51,50,51,52,53,54,57,56,0},
            {0,0,0,0,0,0,0,0,0,0,55,54,0,0,0,0,0,0,0,0,0,0}};

    int [][] heatmap_snow_1 = new int [][]
            {{0,0,0,0,0,0,0,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0},
            {0,74,73,0,15,14,0,6,5,4,3,2,3,4,5,6,7,8,9,10,11,0},
            {0,73,72,0,14,13,0,7,6,5,4,3,4,5,6,7,8,9,10,11,12,0},
            {0,72,71,0,13,12,0,8,7,0,0,0,0,0,0,0,9,10,0,12,13,0},
            {0,71,70,0,12,11,10,9,8,9,10,11,12,13,14,0,10,11,0,13,14,0},
            {0,70,69,0,13,12,11,10,9,10,11,12,13,14,15,0,11,12,0,14,15,0},
            {0,69,68,0,0,0,0,0,0,0,0,0,0,15,16,0,12,13,0,0,0,0},
            {0,68,67,66,65,64,63,62,61,0,19,18,17,16,17,0,13,14,15,16,17,0},
            {0,67,66,65,64,63,62,61,60,0,20,19,18,17,18,0,14,15,16,17,18,0},
            {0,0,0,0,0,0,0,60,59,0,21,20,0,0,0,0,0,0,0,18,19,0},
            {0,69,68,69,70,71,0,59,58,0,22,21,22,23,24,25,26,27,0,19,20,0},
            {0,68,67,68,69,70,0,58,57,0,23,22,23,24,25,26,27,28,0,20,21,0},
            {0,67,66,0,0,0,0,57,56,0,0,0,0,0,0,0,0,0,0,21,22,0},
            {0,66,65,64,63,64,0,56,55,0,43,42,41,40,39,38,37,36,0,22,23,0},
            {0,65,64,63,62,63,0,55,54,0,42,41,40,39,38,37,36,35,0,23,24,0},
            {0,0,0,0,61,62,0,54,53,0,43,42,0,0,0,0,35,34,0,24,25,0},
            {0,59,58,59,60,61,0,53,52,0,44,43,0,37,36,0,34,33,0,25,26,0},
            {0,58,57,58,59,60,0,52,51,0,45,44,0,36,35,0,33,32,0,26,27,0},
            {0,57,56,0,0,0,0,51,50,0,46,45,0,35,34,0,32,31,0,27,28,0},
            {0,56,55,54,53,52,51,50,49,48,47,46,0,34,33,32,31,30,29,28,29,0},
            {0,57,56,55,54,53,52,51,50,49,48,47,0,35,34,33,32,31,30,29,30,0},
            {0,0,0,0,0,0,0,0,0,0,49,48,0,0,0,0,0,0,0,0,0,0}};

    int [][] heatmap_snow_2 = new int [][]
            {{0,0,0,0,0,0,0,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0},
            {0,34,33,0,9,8,7,6,5,4,3,2,0,34,33,32,31,30,31,32,33,0},
            {0,33,32,0,10,9,8,7,6,5,4,3,0,33,32,31,30,29,30,31,32,0},
            {0,32,31,0,0,0,0,8,7,0,5,4,0,34,33,0,29,28,0,32,33,0},
            {0,31,30,29,28,29,0,9,8,0,6,5,0,35,34,0,28,27,0,33,34,0},
            {0,30,29,28,27,28,0,10,9,0,7,6,0,36,35,0,27,26,0,34,35,0},
            {0,0,0,0,26,27,0,11,10,0,8,7,0,37,36,0,26,25,0,0,0,0},
            {0,24,23,24,25,26,0,12,11,0,9,8,0,38,37,0,25,24,23,22,23,0},
            {0,23,22,23,24,25,0,13,12,0,10,9,0,39,38,0,24,23,22,21,22,0},
            {0,22,21,0,0,0,0,14,13,0,11,10,0,0,0,0,0,0,0,20,21,0},
            {0,21,20,19,18,17,16,15,14,0,12,11,12,13,14,15,16,17,18,19,20,0},
            {0,22,21,20,19,18,17,16,15,0,13,12,13,14,15,16,17,18,19,20,21,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,21,22,0},
            {0,60,59,58,57,58,59,60,61,62,63,64,65,66,67,68,69,70,0,22,23,0},
            {0,59,58,57,56,57,58,59,60,61,62,63,64,65,68,67,68,69,0,23,24,0},
            {0,0,0,0,55,56,0,60,61,0,0,0,0,0,0,0,0,0,0,24,25,0},
            {0,53,52,53,54,55,0,61,62,0,40,39,38,37,36,0,28,27,26,25,26,0},
            {0,52,51,52,53,54,0,62,63,0,39,38,37,36,35,0,29,28,27,26,27,0},
            {0,51,50,0,0,0,0,0,0,0,40,39,0,35,34,0,30,29,0,0,0,0},
            {0,50,49,48,47,46,45,44,43,42,41,40,0,34,33,32,31,30,31,32,33,0},
            {0,51,50,49,48,47,46,45,44,43,42,41,0,35,34,33,32,31,32,33,34,0},
            {0,0,0,0,0,0,0,0,0,0,43,42,0,0,0,0,0,0,0,0,0,0}};

    int [][] heatmap_desert_0 = new int [][]
            {{0,0,0,0,0,0,0,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0},
                    {0,34,33,32,31,32,0,6,5,4,3,2,3,4,5,6,7,8,9,10,11,0},
                    {0,33,32,31,30,31,0,7,6,5,4,3,4,5,6,7,8,9,10,11,12,0},
                    {0,0,0,0,29,30,0,8,7,0,5,4,0,0,0,0,0,0,0,12,13,0},
                    {0,27,26,27,28,29,0,9,8,0,6,5,0,83,84,85,86,87,0,13,14,0},
                    {0,26,25,26,27,28,0,10,9,0,7,6,0,82,83,84,85,86,0,14,15,0},
                    {0,25,24,0,0,0,0,0,0,0,8,7,0,81,82,0,0,0,0,15,16,0},
                    {0,24,23,22,21,20,19,18,17,0,9,8,0,80,81,0,19,18,17,16,17,0},
                    {0,23,22,21,20,19,18,17,16,0,10,9,0,79,80,0,20,19,18,17,18,0},
                    {0,0,0,0,0,0,0,16,15,0,11,10,0,78,79,0,21,20,0,0,0,0},
                    {0,67,66,65,64,65,0,15,14,13,12,11,0,77,78,0,22,21,22,23,24,0},
                    {0,66,65,64,63,64,0,16,15,14,13,12,0,76,77,0,23,22,23,24,25,0},
                    {0,0,0,0,62,63,0,0,0,0,0,0,0,75,76,0,0,0,0,25,26,0},
                    {0,60,59,60,61,62,63,64,65,68,67,68,0,74,75,0,29,28,27,26,27,0},
                    {0,59,58,59,60,61,62,63,64,65,66,67,0,73,74,0,30,29,28,27,28,0},
                    {0,58,57,0,0,0,0,0,0,0,67,68,0,72,73,0,31,30,0,0,0,0},
                    {0,57,56,55,54,53,52,51,50,0,68,69,70,71,72,0,32,31,32,33,34,0},
                    {0,58,55,54,53,52,51,50,49,0,69,70,71,72,73,0,33,32,33,34,35,0},
                    {0,57,56,0,0,0,0,51,48,0,0,0,0,0,0,0,0,0,0,35,36,0},
                    {0,58,59,58,59,60,0,48,47,46,45,44,43,42,41,40,39,38,37,36,37,0},
                    {0,61,60,59,60,61,0,49,48,47,46,45,44,43,42,41,40,39,38,37,38,0},
                    {0,0,0,0,0,0,0,0,0,0,47,46,0,0,0,0,0,0,0,0,0,0}};

    int [][] heatmap_desert_1 = new int [][]
            {{0,0,0,0,0,0,0,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0},
                    {0,90,89,0,9,8,7,6,5,4,3,2,0,10,11,12,13,14,15,16,17,0},
                    {0,89,88,0,10,9,8,7,6,5,4,3,0,9,10,11,12,13,14,15,16,0},
                    {0,88,87,0,0,0,0,0,0,0,5,4,0,8,9,0,0,0,0,16,17,0},
                    {0,87,86,85,84,83,82,83,82,0,6,5,6,7,8,9,10,11,0,17,18,0},
                    {0,86,85,84,85,82,81,80,83,0,7,6,7,8,9,10,11,12,0,18,19,0},
                    {0,0,0,0,0,0,0,79,80,0,8,7,0,0,0,0,0,0,0,19,20,0},
                    {0,70,69,0,79,76,77,78,79,0,9,8,9,10,11,0,23,22,21,20,21,0},
                    {0,69,68,0,76,75,76,77,78,0,10,9,10,11,12,0,24,23,22,21,22,0},
                    {0,68,67,0,75,74,0,0,0,0,0,0,0,12,13,0,25,24,0,22,23,0},
                    {0,67,66,0,74,73,72,71,72,0,16,15,14,13,14,0,26,25,0,23,24,0},
                    {0,66,65,0,73,72,71,70,71,0,17,16,15,14,15,0,27,26,0,24,25,0},
                    {0,65,64,0,0,0,0,71,70,0,18,17,0,0,0,0,28,27,0,0,0,0},
                    {0,64,65,0,69,66,67,68,69,0,19,18,19,20,21,0,29,28,29,30,31,0},
                    {0,63,62,0,64,65,66,67,68,0,20,19,20,21,22,0,30,29,30,31,32,0},
                    {0,62,61,0,63,64,0,0,0,0,0,0,0,0,0,0,0,0,0,32,33,0},
                    {0,61,60,61,62,63,0,53,52,49,48,47,48,45,44,0,38,35,34,33,34,0},
                    {0,60,59,60,63,62,0,52,49,48,47,46,45,44,43,0,37,36,35,34,35,0},
                    {0,59,58,0,0,0,0,0,0,0,48,47,0,43,42,0,40,37,0,35,36,0},
                    {0,58,57,56,55,54,53,52,51,50,49,48,0,42,41,40,39,38,0,36,37,0},
                    {0,59,58,57,56,55,54,53,52,53,50,49,0,43,42,41,40,39,0,37,38,0},
                    {0,0,0,0,0,0,0,0,0,0,51,50,0,0,0,0,0,0,0,0,0,0}};

    int [][] heatmap_desert_2 = new int [][]
            {{0,0,0,0,0,0,0,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0},
                    {0,72,71,0,9,8,7,6,5,4,3,2,3,4,5,6,7,8,9,10,11,0},
                    {0,71,70,0,10,9,8,7,6,5,4,3,4,5,6,7,8,9,10,11,12,0},
                    {0,70,69,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,12,13,0},
                    {0,69,68,67,66,65,64,63,64,0,22,21,20,19,18,17,16,15,14,13,14,0},
                    {0,68,67,66,65,64,63,62,63,0,23,22,21,20,19,18,17,16,15,14,15,0},
                    {0,0,0,0,0,0,0,61,62,0,24,23,0,0,0,0,0,0,0,15,16,0},
                    {0,62,61,0,59,58,59,60,61,0,25,24,25,26,27,0,25,24,0,16,17,0},
                    {0,61,60,0,58,57,58,59,60,0,26,25,26,27,28,0,24,23,0,17,18,0},
                    {0,60,59,0,57,56,0,0,0,0,27,26,0,28,29,0,23,22,0,18,19,0},
                    {0,59,58,0,56,55,54,53,52,0,28,27,0,29,30,0,22,21,20,19,20,0},
                    {0,58,57,0,55,54,53,52,51,0,29,28,0,30,31,0,23,22,21,20,21,0},
                    {0,57,56,0,0,0,0,51,50,0,30,29,0,31,32,0,24,23,0,0,0,0},
                    {0,56,55,56,57,58,0,50,49,0,31,30,0,32,33,0,25,24,0,38,39,0},
                    {0,55,54,55,56,57,0,49,48,0,32,31,0,33,34,0,26,25,0,37,38,0},
                    {0,54,53,0,0,0,0,48,47,0,33,32,0,34,35,0,27,26,0,36,37,0},
                    {0,53,52,51,50,49,0,47,46,0,34,33,0,35,36,0,28,27,0,35,36,0},
                    {0,52,51,50,49,48,0,46,45,0,35,34,0,36,37,0,29,28,0,34,35,0},
                    {0,55,52,0,48,47,0,45,44,0,0,0,0,37,38,0,30,29,0,33,34,0},
                    {0,54,53,0,47,46,45,44,43,42,41,40,39,38,39,0,31,30,31,32,33,0},
                    {0,57,54,0,48,47,46,45,44,43,42,41,40,39,40,0,32,31,32,33,34,0},
                    {0,0,0,0,0,0,0,0,0,0,43,42,0,0,0,0,0,0,0,0,0,0}};

    int [][] heatmap_mars_0 = new int [][]
            {{0,0,0,0,0,0,0,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0},
                    {0,34,33,0,9,8,7,6,5,4,3,2,3,4,5,6,7,8,9,10,11,0},
                    {0,33,32,0,10,9,8,7,6,5,4,3,4,5,6,7,8,9,10,11,12,0},
                    {0,32,31,0,0,0,0,8,7,0,5,4,0,0,0,0,0,0,0,12,13,0},
                    {0,31,30,29,28,27,0,9,8,0,6,5,6,7,8,0,16,15,14,13,14,0},
                    {0,30,29,28,27,26,0,10,9,0,7,6,7,8,9,0,17,16,15,14,15,0},
                    {0,0,0,0,26,25,0,0,0,0,0,0,0,9,10,0,18,17,0,0,0,0},
                    {0,76,75,0,25,24,0,16,15,14,13,12,11,10,11,0,19,18,19,20,21,0},
                    {0,75,74,0,24,23,0,17,16,15,14,13,12,11,12,0,20,19,20,21,22,0},
                    {0,74,73,0,23,22,0,18,17,0,0,0,0,0,0,0,0,0,0,22,23,0},
                    {0,73,72,0,22,21,20,19,18,0,64,63,0,35,34,33,32,31,0,23,24,0},
                    {0,72,71,0,23,22,21,20,19,0,63,62,0,34,33,32,31,30,0,24,25,0},
                    {0,71,70,0,0,0,0,0,0,0,62,61,0,35,34,0,30,29,0,25,26,0},
                    {0,70,69,68,67,66,65,64,63,62,61,60,0,36,35,0,29,28,27,26,27,0},
                    {0,69,68,67,66,65,64,63,62,61,60,59,0,37,36,0,30,29,28,27,28,0},
                    {0,72,69,0,0,0,0,0,0,0,59,58,0,38,37,0,0,0,0,0,0,0},
                    {0,73,70,73,74,75,0,81,82,0,58,57,0,39,38,39,40,41,42,45,44,0},
                    {0,72,71,74,73,74,0,80,81,0,57,56,0,42,39,40,41,42,43,44,45,0},
                    {0,0,0,0,76,77,0,79,80,0,56,55,0,0,0,0,0,0,0,45,46,0},
                    {0,78,77,76,77,76,77,78,79,0,55,54,53,52,51,50,49,48,47,46,47,0},
                    {0,79,78,77,80,77,80,79,80,0,56,55,54,53,52,53,50,49,48,47,48,0},
                    {0,0,0,0,0,0,0,0,0,0,57,56,0,0,0,0,0,0,0,0,0,0}};


    int [][] heatmap_mars_1 = new int [][]
            {{0,0,0,0,0,0,0,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0},
                    {0,12,11,10,9,8,7,6,5,4,3,2,0,10,11,12,13,14,15,16,17,0},
                    {0,13,12,11,10,9,8,7,6,5,4,3,0,9,10,11,12,13,14,15,16,0},
                    {0,0,0,0,0,0,0,0,0,0,5,4,0,8,9,0,0,0,0,16,17,0},
                    {0,73,72,71,70,69,68,67,68,0,6,5,6,7,8,0,76,77,0,19,18,0},
                    {0,72,71,70,69,68,67,66,67,0,7,6,7,8,9,0,75,76,0,18,19,0},
                    {0,73,72,0,0,0,0,65,66,0,0,0,0,0,0,0,76,75,0,19,20,0},
                    {0,74,73,0,65,62,63,64,65,66,67,68,69,70,71,74,75,74,0,20,21,0},
                    {0,75,76,0,62,61,62,65,64,65,66,67,68,69,70,73,72,73,0,23,22,0},
                    {0,76,75,0,63,60,0,0,0,0,0,0,0,0,0,0,0,0,0,22,23,0},
                    {0,77,78,0,60,59,58,57,56,55,54,53,52,51,50,49,48,49,0,23,24,0},
                    {0,78,77,0,59,58,57,56,55,54,53,52,51,50,49,48,47,48,0,24,27,0},
                    {0,79,78,0,0,0,0,0,0,0,0,0,0,0,0,0,46,47,0,25,26,0},
                    {0,80,83,80,81,82,0,102,101,104,105,104,107,106,109,0,45,46,0,26,27,0},
                    {0,85,80,83,82,83,0,99,100,101,102,105,106,105,108,0,44,45,0,27,28,0},
                    {0,0,0,0,83,84,0,98,99,0,103,104,0,0,0,0,43,44,0,28,29,0},
                    {0,87,88,85,84,85,0,97,98,0,104,105,0,41,40,41,42,43,0,29,30,0},
                    {0,88,87,86,85,86,0,98,97,0,109,108,0,40,39,40,41,46,0,30,31,0},
                    {0,89,90,0,0,0,0,95,98,0,108,107,0,41,38,0,0,0,0,31,32,0},
                    {0,90,91,90,93,92,93,96,95,0,107,108,0,38,37,36,35,34,33,32,35,0},
                    {0,91,90,93,94,93,98,95,102,0,108,109,0,39,38,37,36,37,34,33,34,0},
                    {0,0,0,0,0,0,0,0,0,0,109,112,0,0,0,0,0,0,0,0,0,0}};

    int [][] heatmap_mars_2 = new int [][]
            {{0,0,0,0,0,0,0,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0},
                    {0,52,51,0,9,8,7,6,5,4,3,2,3,4,5,0,55,54,53,52,53,0},
                    {0,51,50,0,10,9,8,7,6,5,4,3,4,5,6,0,54,53,52,51,52,0},
                    {0,50,49,0,0,0,0,8,7,0,0,0,0,6,7,0,0,0,0,50,51,0},
                    {0,49,48,47,46,47,0,9,8,0,28,27,0,7,8,0,48,47,48,49,50,0},
                    {0,48,47,46,45,46,0,10,9,0,27,26,0,8,9,0,47,46,47,48,49,0},
                    {0,0,0,0,44,45,0,11,10,0,26,25,0,9,10,0,46,45,0,0,0,0},
                    {0,42,41,42,43,44,0,12,11,0,25,24,0,10,11,0,45,44,43,42,43,0},
                    {0,41,40,41,42,43,0,13,12,0,24,23,0,11,12,0,44,43,42,41,42,0},
                    {0,40,39,0,0,0,0,14,13,0,23,22,0,12,13,0,0,0,0,40,41,0},
                    {0,39,38,0,18,17,16,15,14,0,22,21,0,13,14,0,38,37,38,39,40,0},
                    {0,38,37,0,19,18,17,16,15,0,21,20,0,14,15,0,37,36,37,38,39,0},
                    {0,37,36,0,0,0,0,17,16,0,20,19,0,15,16,0,36,35,0,0,0,0},
                    {0,36,35,0,21,20,19,18,17,0,19,18,17,16,17,0,35,34,33,32,33,0},
                    {0,35,34,0,22,21,20,19,18,0,20,19,18,17,18,0,34,33,32,31,32,0},
                    {0,34,33,0,23,22,0,0,0,0,21,20,0,0,0,0,0,0,0,30,31,0},
                    {0,33,32,0,24,23,0,47,46,0,22,21,22,23,24,25,26,27,28,29,30,0},
                    {0,32,31,0,25,24,0,46,45,0,23,22,23,24,25,26,27,28,29,30,31,0},
                    {0,31,30,0,26,25,0,45,44,0,0,0,0,0,0,0,0,0,0,31,32,0},
                    {0,30,29,28,27,26,0,44,43,42,41,40,39,38,37,36,35,34,33,32,33,0},
                    {0,31,30,29,28,27,0,45,44,43,42,41,40,39,38,37,36,35,34,33,34,0},
                    {0,0,0,0,0,0,0,0,0,0,43,42,0,0,0,0,0,0,0,0,0,0}};


    // File
    FileHandle handle;

    private boolean resumeFlag = false;
    private int barrelStreak;
    private int levelBarrelStreak = 0;
    private int lastLevelScore;
    private Sound splashSound;
    private long maxTimerVal;
    private long curTimerVal;
    private long timerCounter;




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
//
//    public void loadAssets()
//    {
//        manager.load("steverogers.png", Texture.class);
//        manager.load("steverogers_goo.png", Texture.class);
//        manager.load("bar.png", Texture.class);
//        manager.load("bar_back.png", Texture.class);
//        manager.load("transparentDark07.png", Texture.class);
//    }


    public PlayState(final GameStateManager gsm, boolean isD) {
        super(gsm);

        isDemo = isD;

        Gdx.input.setCatchBackKey(true);

        // file
        handle = Gdx.files.local("gameInfoLog_temp.txt");

        // state related info
        score = 0;
        realTimeFactor1 = 0;
        realTimeFactor2 = 0;
        location = 0;
        transitionFrame = false;
        gameStartTime = TimeUtils.millis();
        maxHealthLosable = 200;//0.8f*Gdx.graphics.getWidth();
        if (isDemo)
        {
            maxHealthLosable = 600;
        }
        maxDistance = 2720f;
        maxDistanceSubMaze = (22+10)*GRID_UNIT;
        prefs = Gdx.app.getPreferences("Stats");
        lastHitTime = 0;
        lastPoisonedTime = 0;
        maxTimerVal = 48;
        if (isDemo)
        {
            maxTimerVal = 100;
        }
        lastUpdateTime = 0;
        startRoom = new Environment("roguelike-pack/Map/transition_room.tmx", GRID_UNIT, heatmap_snow_0, 4, 1, 2, 3, 8, 3, 2f);
        subMaze = new Environment("roguelike-pack/Map/submaze_snow_0.tmx", GRID_UNIT, heatmap_snow_0, 10, 1, 1, 10, 21, 2, 1f);

        person = new Texture("steverogers.png");
        gooTexture = new Texture ("steverogers_goo.png");
        xscreen = new Texture ("xscreen.png");
        player = new Player(startRoom.getStartingPosX(), startRoom.getStartingPosY(), subMaze.pixelHeight, subMaze.pixelWidth, person, gooTexture);

        buttonTexture = new Texture("questionbox.png");
        invincibleButtonTexture = new Texture("invincible.png");
        freezeButtonTexture  = new Texture("ice.png");
        fastButtonTexture  = new Texture("danger.png");
        healthLossButtonTexture = new Texture("skull.png");
        slowMotionButtonTexture = new Texture("slow.png");
        timerLossButtonTexture = new Texture("timer.png");

        float SCALE_RATIO = 1080f / Gdx.graphics.getWidth();

        invincibleButton = new Sprite(invincibleButtonTexture);
        invincibleButton.getTexture().setFilter(Texture.TextureFilter.Linear,
                Texture.TextureFilter.Linear);
        invincibleButton.setSize(invincibleButton.getWidth() / SCALE_RATIO,
                invincibleButton.getHeight() / SCALE_RATIO);

        freezeButton = new Sprite(freezeButtonTexture);
        freezeButton.getTexture().setFilter(Texture.TextureFilter.Linear,
                Texture.TextureFilter.Linear);
        freezeButton.setSize(freezeButton.getWidth() / SCALE_RATIO,
                freezeButton.getHeight() / SCALE_RATIO);

        fastButton = new Sprite(fastButtonTexture);
        fastButton.getTexture().setFilter(Texture.TextureFilter.Linear,
                Texture.TextureFilter.Linear);
        fastButton.setSize(fastButton.getWidth() / SCALE_RATIO,
                fastButton.getHeight() / SCALE_RATIO);

        slowMotionButton = new Sprite(slowMotionButtonTexture);
        slowMotionButton.getTexture().setFilter(Texture.TextureFilter.Linear,
                Texture.TextureFilter.Linear);
        slowMotionButton.setSize(slowMotionButton.getWidth() / SCALE_RATIO,
                slowMotionButton.getHeight() / SCALE_RATIO);

        timerLossButton = new Sprite(timerLossButtonTexture);
        timerLossButton.getTexture().setFilter(Texture.TextureFilter.Linear,
                Texture.TextureFilter.Linear);
        timerLossButton.setSize(timerLossButton.getWidth() / SCALE_RATIO,
                timerLossButton.getHeight() / SCALE_RATIO);

        healthLossButton = new Sprite(healthLossButtonTexture);
        healthLossButton.getTexture().setFilter(Texture.TextureFilter.Linear,
                Texture.TextureFilter.Linear);
        healthLossButton.setSize(healthLossButton.getWidth() / SCALE_RATIO,
                healthLossButton.getHeight() / SCALE_RATIO);

        abilityButton = new AbilityButton(buttonTexture);
        questionMarkSmall = new Texture("questionbox_small.png");

        initTextures();

        zombiesBounds = new Array<Rectangle>();
        redPoisonArr = new Array<PoisonObject>();
        foodSupplies = new Array<Rectangle>();
        abilityBoxes = new Array<Rectangle>();
        zombiePivot = 6;

        spawnInitialBarrels();
//        MovementGestureDetector mgd = new MovementGestureDetector(new MovementGestureDetector.DirectionListener() {
//            //            @Override
////            public void onTouched(float x, float y)
////            {
////                player.move(x, y);
////            }
//            @Override
//            public void onUp() {
//                player.moveUp();
//                player.keepMoving(Player.dir.up);
//            }
//
//            @Override
//            public void onRight() {
//                player.moveRight();
//                player.keepMoving(Player.dir.right);
//
//            }
//
//            @Override
//            public void onLeft() {
//                player.moveLeft();
//                player.keepMoving(Player.dir.left);
//            }
//
//            @Override
//            public void onDown() {
//                player.moveDown();
//                player.keepMoving(Player.dir.down);
//            }
//
//            @Override
//            public void onLeftUp() {
//                player.moveLeftUp();
//                player.keepMoving(Player.dir.leftup);
//            }
//
//            @Override
//            public void onLeftDown() {
//                player.moveLeftDown();
//                player.keepMoving(Player.dir.leftdown);
//            }
//
//            @Override
//            public void onRightUp() {
//                player.moveRightUp();
//                player.keepMoving(Player.dir.rightup);
//            }
//
//            @Override
//            public void onRightDown() {
//                player.moveRightDown();
//                player.keepMoving(Player.dir.rightdown);
//            }
//
//        });
//        im.addProcessor(mgd);
        Gdx.input.setInputProcessor(im);
    }

    public void initTextures()
    {
        lastNewDirection = 0;
        movementLog = "";
        //handle.writeString("Hello!", true);
        // environments

        startRoom = new Environment("roguelike-pack/Map/transition_room.tmx", GRID_UNIT, heatmap_snow_0, 4, 1, 2, 3, 8, 3, 2f);
        subMaze = new Environment("roguelike-pack/Map/submaze_snow_0.tmx", GRID_UNIT, heatmap_snow_0, 10, 1, 1, 10, 21, 2, 1f);
        // camera related info
        cam.setToOrtho(false, RoamGame.WIDTH*3/4, RoamGame.HEIGHT*3/4);

        // game features / interactables
        person = new Texture("steverogers.png");
        gooTexture = new Texture ("steverogers_goo.png");
        player.loadTextures(person, gooTexture);

        food = new Food();
        poisonGrass = new PoisonGrass();
        zombies = new Array<Zombie>();

        // font related info
        float screenScale = 1f*Gdx.graphics.getWidth()/(RoamGame.WIDTH);
        generator = new FreeTypeFontGenerator(Gdx.files.internal("bebas.ttf"));
        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        layout = new GlyphLayout();

        parameter.size = (int)(screenScale*( 30 ));//+ (int)((1f - alpha)*20f)));
        parameter.borderColor = Color.BLACK;
        parameter.borderWidth = 2;
        //parameter.characters = "0123456789+-Gained!*pointshealthEnteringLevelCongratsReceivebonus ";
        bfont = generator.generateFont(parameter);
        parameter.size = (int) (screenScale*(20));
        parameter.borderWidth = 1;
        bfont_small = generator.generateFont(parameter);
        generator.dispose();
        //System.out.println("space " + bfont.getSpaceWidth());

        font = new BitmapFont();

        // music and sounds
        music = Gdx.audio.newMusic(Gdx.files.internal("animalcrossing.mp3"));
        music.setLooping(true);
        music.setVolume(0.1f);
        music.play();
        pickupItemSound = Gdx.audio.newSound(Gdx.files.internal("pickupSound.mp3"));
        pickupAbilitySound = Gdx.audio.newSound(Gdx.files.internal("abilityBox.mp3"));
        invincibilityMusic = Gdx.audio.newMusic(Gdx.files.internal("invincible.wav"));
        reaperFastModeMusic = Gdx.audio.newSound(Gdx.files.internal("laugh.wav"));
        clinkSound = Gdx.audio.newSound(Gdx.files.internal("clinksound.mp3"));
        reaperFreezeModeMusic = Gdx.audio.newSound(Gdx.files.internal("freeze.wav"));
        deathSound = Gdx.audio.newSound(Gdx.files.internal("death.wav"));
        splashSound = Gdx.audio.newSound(Gdx.files.internal("splash.wav"));
        heartBeatSound = Gdx.audio.newMusic(Gdx.files.internal("heartbeat.wav"));
        timerSound = Gdx.audio.newMusic(Gdx.files.internal("tick.mp3"));
        hauntSound = Gdx.audio.newMusic(Gdx.files.internal("haunting.mp3"));

        // Textures
        fb = new Texture("bar.png");
        bb = new Texture("bar_back.png");
        dpadCross = new Texture("transparentDark07.png");
        dpadCrossSprite = createScaledSprite(dpadCross);
        redArrowUp = new Texture(("redarrow.png"));

        // dialog setup
        //skin = new Skin(Gdx.files.internal("star-soldier-ui.json"));
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        float scaleFactor = Gdx.graphics.getWidth()/ 480f;
        skin.getFont("font-button").getData().setScale(scaleFactor, scaleFactor);
        skin.getFont("font-label").getData().setScale(scaleFactor, scaleFactor);
        skin.getFont("font-title").getData().setScale(scaleFactor, scaleFactor);


        //skin.add("default-font", bfont, BitmapFont.class);
        stage = new Stage();
        im.addProcessor(stage);
        //Gdx.input.setInputProcessor(stage);
        dialogShowing = false;
        endDialogShowing = false;

        // UI
        bloodScreen = new Rectangle(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        srender = new ShapeRenderer();

        exitDialog = new Dialog("\t Leave Game? ", skin)
        {
            @Override
            public float getPrefWidth() {
                // force dialog width
                return Gdx.graphics.getWidth();
            }

            @Override
            public float getPrefHeight() {
                // force dialog height
                return 0.2f*Gdx.graphics.getHeight();
            }
            protected void result(Object object) {

                int selected = Integer.parseInt(object.toString());

                if (selected == 1 )
                {
                    gameDuration = TimeUtils.millis() - gameStartTime;
                    //accumulatedFactor1 = 1000*accumulatedFactor1/gameDuration;
                    //System.out.println(accumulatedFactor1);
                    prefs.putInteger("score", score);
                    prefs.putInteger("factor1", (int)currentFactor1);
                    prefs.putInteger("factor2", (int)currentFactor2);
                    prefs.flush();

                    SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd;HH:mm:ss");//dd/MM/yyyy
                    Date now = new Date();
                    String strDate = sdfDate.format(now);

                    String compl = computeComplexity();
                    handle.writeString("game " + score + " " + gameDuration + " " + String.valueOf((int)(inputFrequency)) + " " + strDate + " " + String.valueOf(isDemo ? 1 : 0)+ " " + compl + "\n", true);
                    FileHandle actualLog = Gdx.files.local("gameInfoLog_"+String.valueOf(VERSION)+".txt");
                    Gdx.files.local("gameInfoLog_temp.txt").moveTo(actualLog);
                    gsm.set(new ScoreScreenState(gsm));
                    //Gdx.app.exit();
                }

                //endDialogShowing = false;
            }
        };
        exitDialog.getButtonTable().defaults().height(0.1f * Gdx.graphics.getHeight());
        exitDialog.getButtonTable().defaults().width(0.5f*Gdx.graphics.getWidth());
        exitDialog.button("Yes", 1L);
        exitDialog.button("Continue", 2L);


        endLevelDialog = new Dialog("", skin)
        {
            @Override
            public float getPrefWidth() {
                // force dialog width
                return Gdx.graphics.getWidth();
            }

            @Override
            public float getPrefHeight() {
                // force dialog height
                return 0.65f*Gdx.graphics.getHeight();
            }
            protected void result(Object object) {

                int selected = Integer.parseInt(object.toString());

                if (selected == 2 )
                {
                    exitDialog.show(stage);
                }

                endDialogShowing = false;
            }
        };

        endLevelDialog.getButtonTable().defaults().height(0.1f * Gdx.graphics.getHeight());
        endLevelDialog.getButtonTable().defaults().width(0.5f*Gdx.graphics.getWidth());
        endLevelDialog.button("Continue at\nyour own peril", 1L);
        endLevelDialog.button("Cash out!", 2L);
        endLevelDialog.setBackground(new Image(xscreen).getDrawable());
        /*endLevelDialog.text(("Congrats on successfully navigating the submaze!\n" +
                "Continue playing to get a higher score, or quit the game!\n" +
                "Beware! Barrels are of higher value in successive levels, but the \n" +
                "perils and threats are greater as well!"));*/

        Window.WindowStyle style = new Window.WindowStyle();
        style.titleFont = bfont;

        barrelChooseDialog = new Dialog("", skin)
        {

            @Override
            public float getPrefWidth() {
                // force dialog width
                return Gdx.graphics.getWidth();
            }

            @Override
            public float getPrefHeight() {
                // force dialog height
                return 0.12f*Gdx.graphics.getHeight();
            }
            protected void result(Object object)
            {

                int baseScore = MathUtils.random(6,21);
                int scoreAmount =  (int)(((float)(levelCounter+1)/3f)*(float)baseScore);

                System.out.println("Option: " + object);

                int selected = Integer.parseInt(object.toString());

                // log event
                handle.writeString("event " + String.valueOf(TimeUtils.millis() - gameStartTime) + " " +  String.valueOf(levelCounter) + " 1 " + String.valueOf(maxHealthLosable - healthBarVal) + " " + String.valueOf(score) + " " + String.valueOf(levelScore) + " " + String.valueOf(selected) + " 0 0 0" +  " " + String.valueOf(closestZombieDistance()) + " " + String.valueOf(exitDistance()) + " " + String.valueOf(barrelStreak) + " " + String.valueOf(curTimerVal) + " " + "0 "+String.valueOf(abilityActive)+"\n", true);

                if (selected == 1)
                {

                    //pickupItemSound.setPitch(pickupItemSound.play(),pitch);
                    barrelStreak += 1;
                    levelBarrelStreak += 1;
                    healthBarVal -= 0.08f * maxHealthLosable;
                    player.boostPlayer(1.25f);
                    if (healthBarVal < 0)
                        healthBarVal = 0;
                    lastBarrelScore = scoreAmount;
                    scoreAnimationStart = TimeUtils.millis();
                    scoreAnimation = true;
                    // Play sound
                    //System.out.println(pitch);
                    pickupItemSound.play(0.5f, pitch, 0);
                    pitch *= 1.1f;
                    if (pitch > 2f)
                        pitch = 2f;
                    levelScore += scoreAmount* barrelStreak;
                } else if (selected == 2)
                {
                    barrelStreak = 0;
                    pitch = 1f;
                    levelBarrelStreak = 0;
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

                // chance of ability box
                Random rand = new Random();
                int num = rand.nextInt(2);
                System.out.println("num: " + num);
                if (num == 0) {
                    Timer.schedule(new Timer.Task() {

                        @Override
                        public void run() {
                            spawnAbilityBox(abilityBox_X, abilityBox_Y);

                            //endLevelDialog.setSize(0.8f * (float) Gdx.graphics.getWidth(), Gdx.graphics.getHeight() / 3);
                        }
                    }, 1);
                }

                dialogShowing = false;


            }
        };

        //barrelChooseDialog.setSkin(skin);


        barrelChooseDialog.getButtonTable().defaults().height(0.1f * Gdx.graphics.getHeight());
        barrelChooseDialog.getButtonTable().defaults().width(Gdx.graphics.getWidth() / 2);

        //barrelChooseDialog.getContentTable().defaults().height(0.1f* Gdx.graphics.getHeight());

        barrelChooseDialog.button("Points", 1L);
        barrelChooseDialog.button("Health", 2L);

        if (isDemo == true) {
            setupTutorialDialogues();
        } else
        {
            tut1Complete = true;
            tut2Complete = true;
            tut3Complete = true;
            tut4Complete = true;
            tut5Complete = true;
            tut6Complete = true;
        }
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
        tut6Complete = false;

        tut1Dialog = new Dialog("\t  Welcome to Roam Game!", skin)
        {
            @Override
            public float getPrefWidth() {
                // force dialog width
                return 0.94f*Gdx.graphics.getWidth();
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
        tut1Dialog.getButtonTable().defaults().height(0.1f * Gdx.graphics.getHeight());
        tut1Dialog.getButtonTable().defaults().width(0.94f*Gdx.graphics.getWidth());
        tut1Dialog.button("Press to Continue", 1L);
        Label txt = new Label("Please walk forward through the exit to begin the game!\n\n" +
                "To move, swipe on the screen in the direction you wish to travel.\n\n" +
                "You can either continuously drag or swipe to move!", skin);
        txt.setWrap(true);
        tut1Dialog.getContentTable().add(txt).prefWidth(0.88f*Gdx.graphics.getWidth());



        tut2Dialog = new Dialog("", skin)
        {
            @Override
            public float getPrefWidth() {
                // force dialog width
                return 0.94f*Gdx.graphics.getWidth();
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
        tut2Dialog.getButtonTable().defaults().height(0.1f * Gdx.graphics.getHeight());
        tut2Dialog.getButtonTable().defaults().width(0.94f*Gdx.graphics.getWidth());
        tut2Dialog.button("Press to Continue", 1L);
        Label txt2 = new Label("Collect Barrels to gain points!\n\nAvoid Reapers and Poison Grass!\n\n" +
                "Naviate to exit before timer expires or you will die!", skin);
        txt2.setWrap(true);
        tut2Dialog.getContentTable().add(txt2).prefWidth(0.88f*Gdx.graphics.getWidth());

        tut3Dialog = new Dialog("", skin)
        {
            @Override
            public float getPrefWidth() {
                // force dialog width
                return 0.94f*Gdx.graphics.getWidth();
            }
            @Override
            public float getPrefHeight() {
                // force dialog height
                return 0.50f*Gdx.graphics.getHeight();
            }
            protected void result(Object object) {

                int selected = Integer.parseInt(object.toString());
                //tut3Complete = true;
                dialogShowing = false;

                //barrelChooseDialog.show(stage);
                tut3BDialog.show(stage);
                dialogShowing = true;
            }
        };
        tut3Dialog.getButtonTable().defaults().height(0.1f * Gdx.graphics.getHeight());
        tut3Dialog.getButtonTable().defaults().width(0.94f*Gdx.graphics.getWidth());
        tut3Dialog.button("Press to Continue", 1L);
        Label txt3 = new Label("Good job! Collect Barrels to gain points!\n\n" +
                "Collecting successive barrels will create a streak" +
                " and will dramatically increase points!\n\n" , skin);
        txt3.setWrap(true);
        tut3Dialog.getContentTable().add(txt3).prefWidth(0.88f*Gdx.graphics.getWidth());

        tut3BDialog = new Dialog("", skin)
        {
            @Override
            public float getPrefWidth() {
                // force dialog width
                return 0.94f*Gdx.graphics.getWidth();
            }
            @Override
            public float getPrefHeight() {
                // force dialog height
                return 0.50f*Gdx.graphics.getHeight();
            }
            protected void result(Object object) {

                int selected = Integer.parseInt(object.toString());
                tut3Complete = true;
                dialogShowing = false;

                barrelChooseDialog.show(stage);
                dialogShowing = true;
            }
        };
        tut3BDialog.getButtonTable().defaults().height(0.1f * Gdx.graphics.getHeight());
        tut3BDialog.getButtonTable().defaults().width(0.94f*Gdx.graphics.getWidth());
        tut3BDialog.button("Press to Continue", 1L);
        Label txt3B = new Label(
                "Barrels can also be used for health, but doing so will break the streak!\n\n" +
                "Collecting all 4 barrels in each submaze will lead to even greater points!", skin);
        txt3B.setWrap(true);
        tut3BDialog.getContentTable().add(txt3B).prefWidth(0.88f*Gdx.graphics.getWidth());

        tut4Dialog = new Dialog("", skin)
        {
            @Override
            public float getPrefWidth() {
                // force dialog width
                return 0.94f*Gdx.graphics.getWidth();
            }
            @Override
            public float getPrefHeight() {
                // force dialog height
                return 0.35f*Gdx.graphics.getHeight();
            }
            protected void result(Object object) {

                int selected = Integer.parseInt(object.toString());
                dialogShowing = false;
            }
        };
        tut4Dialog.getButtonTable().defaults().height(0.1f * Gdx.graphics.getHeight());
        tut4Dialog.getButtonTable().defaults().width(0.94f*Gdx.graphics.getWidth());
        tut4Dialog.button("Press to Continue", 1L);
        Label txt4 = new Label("OOPS! Avoid collisions with Reapers!\n\n" +
                "Collisions with Reapers will greatly reduce health!", skin);
        txt4.setWrap(true);
        tut4Dialog.getContentTable().add(txt4).prefWidth(0.88f*Gdx.graphics.getWidth());

        tut5Dialog = new Dialog("", skin)
        {
            @Override
            public float getPrefWidth() {
                // force dialog width
                return 0.94f*Gdx.graphics.getWidth();
            }
            @Override
            public float getPrefHeight() {
                // force dialog height
                return 0.35f*Gdx.graphics.getHeight();
            }
            protected void result(Object object) {

                int selected = Integer.parseInt(object.toString());
                dialogShowing = false;
            }
        };
        tut5Dialog.getButtonTable().defaults().height(0.1f * Gdx.graphics.getHeight());
        tut5Dialog.getButtonTable().defaults().width(0.94f*Gdx.graphics.getWidth());
        tut5Dialog.button("Press to Continue", 1L);
        Label txt5 = new Label("OOPS! Avoid collisions with Poison Grass!\n\n" +
                "Collisions with Poison Grass will slow speed for a period of time!", skin);
        txt5.setWrap(true);
        tut5Dialog.getContentTable().add(txt5).prefWidth(0.88f*Gdx.graphics.getWidth());

        tut6Dialog = new Dialog("", skin)
        {
            @Override
            public float getPrefWidth() {
                // force dialog width
                return 0.94f*Gdx.graphics.getWidth();
            }
            @Override
            public float getPrefHeight() {
                // force dialog height
                return 0.65f*Gdx.graphics.getHeight();
            }
            protected void result(Object object) {

                int selected = Integer.parseInt(object.toString());
                dialogShowing = false;
                tut6BDialog.show(stage);
                dialogShowing = true;
            }
        };
        tut6Dialog.getButtonTable().defaults().height(0.1f * Gdx.graphics.getHeight());
        tut6Dialog.getButtonTable().defaults().width(0.94f*Gdx.graphics.getWidth());
        tut6Dialog.button("Press to Continue", 1L);
        Label txt6 = new Label("Congratulations on collecting an item box! Click on the item box to use it!\n\n" +
                "Item boxes can be good or bad, but mostly good!\n\n" , skin);
        txt6.setWrap(true);
        tut6Dialog.getContentTable().add(txt6).prefWidth(0.88f*Gdx.graphics.getWidth());


        tut6BDialog = new Dialog("", skin)
        {
            @Override
            public float getPrefWidth() {
                // force dialog width
                return 0.94f*Gdx.graphics.getWidth();
            }
            @Override
            public float getPrefHeight() {
                // force dialog height
                return 0.65f*Gdx.graphics.getHeight();
            }
            protected void result(Object object) {

                int selected = Integer.parseInt(object.toString());
                dialogShowing = false;
                tut6CDialog.show(stage);
                dialogShowing = true;
            }
        };
        tut6BDialog.getButtonTable().defaults().height(0.1f * Gdx.graphics.getHeight());
        tut6BDialog.getButtonTable().defaults().width(0.94f*Gdx.graphics.getWidth());
        tut6BDialog.button("Press to Continue", 1L);
        Label txt6B = new Label(
                "The Good Outcomes include:\n\n" +
                "1) Ice: Reapers freeze and can be destroyed for 5 seconds\n\n" +
                "2) Invincibility: Lasts for 5 seconds\n\n" , skin);
        txt6B.setWrap(true);
        tut6BDialog.getContentTable().add(txt6B).prefWidth(0.88f*Gdx.graphics.getWidth());

        tut6CDialog = new Dialog("", skin)
        {
            @Override
            public float getPrefWidth() {
                // force dialog width
                return 0.94f*Gdx.graphics.getWidth();
            }
            @Override
            public float getPrefHeight() {
                // force dialog height
                return 0.65f*Gdx.graphics.getHeight();
            }
            protected void result(Object object) {

                int selected = Integer.parseInt(object.toString());
                dialogShowing = false;
            }
        };
        tut6CDialog.getButtonTable().defaults().height(0.1f * Gdx.graphics.getHeight());
        tut6CDialog.getButtonTable().defaults().width(0.94f*Gdx.graphics.getWidth());
        tut6CDialog.button("Press to Continue", 1L);
        Label txt6C = new Label(
                "The Negative Outcomes include:\n\n" +
                        "1) Halt!: Player slows down for 5 seconds\n\n" +
                        "2) Time Cut: Timer is reduced by 10 seconds\n\n" +
                        "3) Silent Strike: Health is reduced\n\n" +
                        "You can use the box any time, but use it wisely!" , skin);
        txt6C.setWrap(true);
        tut6CDialog.getContentTable().add(txt6C).prefWidth(0.88f*Gdx.graphics.getWidth());

    } // function

    @Override
    public void pause() {
        this.dispose();
    }


    @Override
    public void resume() {

        // camera related info
        cam.setToOrtho(false, RoamGame.WIDTH, RoamGame.HEIGHT);

        // Reload Textures
        initTextures();

        Gdx.app.log("resume", startRoom.getStartingPosX() + " " + startRoom.getStartingPosY() + " " + player.getPosition().x + " " + player.getPosition().y);
        resumeFlag = true;
        //player = new Player(startRoom.getStartingPosX(), startRoom.getStartingPosY(), subMaze.pixelHeight, subMaze.pixelWidth, person, gooTexture);
        //player.resume();
    }

    @Override
    protected void handleInput() {

        //String logData = String.valueOf(TimeUtils.nanoTime() - gameStartTime) + " " ;
        //boolean isMoved = false;
        isTouched = false;

        if (Gdx.input.isKeyPressed(Input.Keys.BACK))
        {
            exitDialog.show(stage);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) {
            //logData += "Up";
            player.moveUp();
            inputRegistered += 1;
            isTouched = true;

            if (lastNewDirection != 1)
            {
                lastNewDirection = 1;
                movementLog += "00";
                //System.out.println(movementLog);
            }
            //isMoved = true;
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) {
            //logData += "Down";
            player.moveDown();
            inputRegistered += 1;
            isTouched = true;


            if (lastNewDirection != 2)
            {
                lastNewDirection = 2;
                movementLog += "01";
                //System.out.println(movementLog);
            }
            //isMoved = true;
        } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
            //logData += "Left";
            player.moveLeft();
            inputRegistered += 1;
            isTouched = true;


            if (lastNewDirection != 3)
            {
                lastNewDirection = 3;
                movementLog += "10";
                //System.out.println(movementLog);
            }
            //isMoved = true;
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
            //logData += "Right";
            player.moveRight();
            inputRegistered += 1;
            isTouched = true;


            if (lastNewDirection != 4)
            {
                lastNewDirection = 4;
                movementLog += "11";
                //System.out.println(movementLog);
            }
            //isMoved = true;
        }

        if (Gdx.input.justTouched()) {
            System.out.println("just touched");
            lastTouchedPointX = Gdx.input.getX();
            lastTouchedPointY = Gdx.input.getY();

            for (int i=0; i<12; i++)
                direction_filter[i] = 0;
        } else if (Gdx.input.isTouched()) {
            inputRegistered += 1;
            isTouched = true;

            int currentTouchedPointX = Gdx.input.getX();
            int currentTouchedPointY = Gdx.input.getY();

            int deltaX = currentTouchedPointX - lastTouchedPointX;
            int deltaY = currentTouchedPointY - lastTouchedPointY;

            for (int i=1; i<12; i++)
            {
                direction_filter[i] = direction_filter[i-1];
            }



            //if (Math.abs(deltaX) > 2 || Math.abs(deltaY) > 2) {

            if (Math.abs(deltaX) > Math.abs(deltaY)) {
                //System.out.println("changing direction right left");
                if (deltaX > 0) {
                    //System.out.println("moving right");
                    direction_filter[0] = 1;


                } else {
                    direction_filter[0] = 2;


                }


            } else if (Math.abs(deltaY) > Math.abs(deltaX)) {
                //System.out.println("changing direction up down");
                if (deltaY > 0) {
                    direction_filter[0] = 3;


                } else {
                    direction_filter[0] = 4;


                }
            }

            int dir = getPopularElement(direction_filter);
            String curDir = "";

            if(dir == 1)
            {
                //movementLog += "00";
                player.moveRight();
                player.keepMoving(Player.dir.right);
                curDir = "00";
            } else if (dir == 2)
            {
                //movementLog += "01";
                player.moveLeft();
                player.keepMoving(Player.dir.left);
                curDir = "01";
            } else if (dir == 3)
            {
                //movementLog += "10";
                player.moveDown();
                player.keepMoving(Player.dir.down);
                curDir = "10";
            } else if (dir == 4)
            {
                //movementLog += "11";
                player.moveUp();
                player.keepMoving(Player.dir.up);
                curDir = "11";
            }

            if (lastNewDirection != dir)
            {
                lastNewDirection = dir;

                movementLog += curDir;
            }


            // }
            lastTouchedPointX = currentTouchedPointX;
            lastTouchedPointY = currentTouchedPointY;
        }


//            //System.out.println(Gdx.input.getX() + " " + Gdx.input.getY());

//            int index_row = (int)(11f*(float)Gdx.input.getY() / (float)Gdx.graphics.getHeight());
//            int index_col = (int)(11f*(float)Gdx.input.getX() / (float)Gdx.graphics.getWidth());
//            //System.out.println(index_col+ " " + index_row);
//            if (index_row >= 0 && index_row < 11 && index_col >= 0 && index_col < 11) {
//                if (touchArray[index_row][index_col] == 1) {
//                    player.moveUp();
//                }
//
//                if (touchArray[index_row][index_col] == 2) {
//                    player.moveRight();
//                }
//
//                if (touchArray[index_row][index_col] == 3) {
//                    player.moveDown();
//                }
//
//                if (touchArray[index_row][index_col] == 4) {
//                    player.moveLeft();
//                }
//            }
//


        //logData += "\n";

        //if (isMoved == true)
        //    logFile.writeString(logData, true);

    }

    public String computeComplexity()
    {
        int n = movementLog.length();
        int c = 1;
        int l = 1;

        int i = 0;
        int k = 1;
        int k_max = 1;
        int stop = 0;
        double b;
        double complexity;
        System.out.println("length of string:" + String.valueOf(n));

        while (stop == 0)
        {
            //System.out.println("analyzing");
            if (movementLog.charAt(i+k) != movementLog.charAt(l+k))
            {
                if (k > k_max)
                    k_max = k;
                i = i + 1;

                if (i==1)
                {
                    c = c+1;
                    l = l+k_max;
                    if (l+1 > (n-1))
                    {
                        stop = 1;
                    } else
                    {
                        i = 0;
                        k = 1;
                        k_max = 1;
                    }
                } else
                {
                    k = 1;
                }
            } else
            {
                k = k+1;
                if (l + k > (n-1))
                {
                    c = c+1;
                    stop = 1;
                }
            }
        }

        double log2_res = Math.log(n) / Math.log(2);
        b = n/log2_res;

        complexity = c/b;

        DecimalFormat df = new DecimalFormat("#.00");
        String complexityFormated = df.format(complexity);

        System.out.println(complexityFormated);

        return complexityFormated;
    }

    public int getPopularElement(int[] a)
    {
        int count = 1, tempCount;
        int popular = a[0];
        int temp = 0;
        for (int i = 0; i < (a.length - 1); i++)
        {
            temp = a[i];
            tempCount = 0;
            for (int j = 1; j < a.length; j++)
            {
                if (temp == a[j])
                    tempCount++;
            }
            if (tempCount > count)
            {
                popular = temp;
                count = tempCount;
            }
        }
        return popular;
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

    private void updateAbilityBoxes(float dt)
    {
        // Check if player picked up ability boxes
        for (int index=0; index<abilityBoxes.size; index++)
        {
            Rectangle item = abilityBoxes.get(index);
            if (item.overlaps(player.getBounds())) {


                if (!tut6Complete) {
                    tut6Dialog.show(stage);
                    tut6Complete = true;
                    dialogShowing = true;
                }

                if (numBoxes<3) {
                    // health boost for high value
                    //if (occupiedSubMazeGrid[Math.round(item.y/GRID_UNIT)][Math.round(item.x/GRID_UNIT)] == 2) {
                    //barrelChooseDialog.setSize(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 3);
                    // Clear item
                    subMaze.occupiedGrid[Math.round(item.y / GRID_UNIT)][Math.round(item.x / GRID_UNIT)] = -1;
                    numBoxes++;
                    //if (abilityActive != true && abilityBoxPickedUp != true) {
                    //abilityBoxPickedUp = true;
                    //abilityButton.setTexture(buttonTexture);
                    pickupAbilitySound.play(0.5f);
                    // log event
                    handle.writeString("event " + String.valueOf(TimeUtils.millis() - gameStartTime) + " " +  String.valueOf(levelCounter) + " 4 " + String.valueOf(maxHealthLosable - healthBarVal) + " " + String.valueOf(score) + " " + String.valueOf(levelScore) + " 0 0 0 0" +  " " + String.valueOf(closestZombieDistance()) + " " + String.valueOf(exitDistance()) + " " + String.valueOf(barrelStreak) + " " + String.valueOf(curTimerVal) + " " + "0 "+String.valueOf(abilityActive)+"\n", true);
                    abilityBoxes.removeIndex(index);
                }
                //}




            }
        }
    }

    private void updateFood(float dt)
    {
        // Check if player picked up food
        for (int index=0; index<foodSupplies.size; index++)
        {
            Rectangle item = foodSupplies.get(index);
            if (item.overlaps(player.getBounds())) {

                abilityBox_X = foodSupplies.get(index).getX();
                abilityBox_Y = foodSupplies.get(index).getY();

                if (!tut3Complete) {
                    tut3Dialog.show(stage);
                    //tut3Complete = true;
                    dialogShowing = true;
                } else {
                    // A chance to get a question mark barrel replacing location of food barrel
                    float x = foodSupplies.get(index).getX();
                    float y = foodSupplies.get(index).getY();
                    barrelChooseDialog.show(stage);

                    dialogShowing = true;
                }


                // health boost for high value
                //if (occupiedSubMazeGrid[Math.round(item.y/GRID_UNIT)][Math.round(item.x/GRID_UNIT)] == 2) {
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

    public void spawnAbilityBox(float x, float y)
    {
        Rectangle box = new Rectangle();

        box.x = x;
        box.y = y;
        box.width = GRID_UNIT;
        box.height = GRID_UNIT;

        abilityBoxes.add(box);
    }

    private void updateRedPoison(float dt)
    {
        // Check if player hit poison
        for (int index=0; index<redPoisonArr.size; index++)
        {

            Rectangle item = redPoisonArr.get(index).rect;
            if (invincibleMode == false && item.overlaps(player.getBounds()) && (TimeUtils.millis() - lastPoisonedTime) > 2*SEC) {
                splashSound.play(0.7f);
                healthBarVal+=5;
                player.slowPlayer();
                lastPoisonedTime = TimeUtils.millis();
                if (!tut5Complete) {
                    tut5Dialog.show(stage);
                    tut5Complete = true;
                    dialogShowing = true;
                }

                handle.writeString("event " + String.valueOf(TimeUtils.millis() - gameStartTime) + " " +  String.valueOf(levelCounter) + " 2 " + String.valueOf(maxHealthLosable - healthBarVal) + " " + String.valueOf(score) + " " + String.valueOf(levelScore) + " 0 2 0 0" + " " + String.valueOf(closestZombieDistance()) + " " + String.valueOf(exitDistance()) + " " + String.valueOf(barrelStreak) + " " + String.valueOf(curTimerVal) + " " + "0 " + String.valueOf(abilityActive)+"\n", true);


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
            zombie.move(zombie.getDirection());
            zombie.update(dt);

            //System.out.println(zombie.getDirection());
            if (zombie.getPosition().y <= 0) {
                zombie.setPosition(zombie.oldPosition.x, zombie.oldPosition.y);
                zombie.setDirection(PlayState.UP);
            }
            else if (zombie.getPosition().y >= (subMaze.pixelHeight - zombie.getTexture().getRegionHeight())) {
                zombie.setPosition(zombie.oldPosition.x, zombie.oldPosition.y);
                zombie.setDirection(PlayState.DOWN);
            }
            else if (zombie.getPosition().x <= 0) {
                zombie.setPosition(zombie.oldPosition.x, zombie.oldPosition.y);
                zombie.setDirection(PlayState.RIGHT);
            }
            else if (zombie.getPosition().x >= (subMaze.pixelWidth - zombie.getTexture().getRegionWidth())) {
                zombie.setPosition(zombie.oldPosition.x, zombie.oldPosition.y);
                zombie.setDirection(PlayState.LEFT);
            }

            boolean overlapped = false;

            for (int index = 0; index < subMaze.obstacles.size; index++) {
                Rectangle item = subMaze.obstacles.get(index);
                if (item.overlaps(zombie.getBounds())) {

                    overlapped = true;


                }
            }

            if (overlapped == true) {
                //System.out.println("overlapped (curpos : " + zombie.getPosition().x + " " +  zombie.getPosition().y + " " + zombie.oldPosition.x + " " + zombie.oldPosition.y + ")");
                zombie.setPosition(zombie.oldPosition.x, zombie.oldPosition.y);

                int oldDir = zombie.getDirection();
                Random rand = new Random();
                int newDir = rand.nextInt(4);

                while (newDir == oldDir)
                {
                    newDir = rand.nextInt(4);
                }

                zombie.setDirection(newDir);

                //System.out.println("fixing pos (curpos : " + zombie.getPosition().x + " " +  zombie.getPosition().y + " " + zombie.oldPosition.x + " " + zombie.oldPosition.y + ")");

//                if (zombie.getDirection() == PlayState.UP) {
//                    //System.out.println(i + " up");
//                    zombie.setDirection(PlayState.LEFT);
//                }
//                else if (zombie.getDirection() == PlayState.LEFT) {
//                    //System.out.println(i + " left");
//                    zombie.setDirection(PlayState.DOWN);
//                }
//                else if (zombie.getDirection() == PlayState.DOWN) {
//                    //System.out.println(i + " down");
//                    zombie.setDirection(PlayState.RIGHT);
//                }
//                else if (zombie.getDirection() == PlayState.RIGHT) {
//                    //System.out.println(i + " right");
//                    zombie.setDirection(PlayState.UP);
//                }
            }
            //System.out.println(z.getDirection());

            zombies.set(i, zombie);
            if(zombie.getBounds().overlaps(player.getBounds())) {
                if ((invincibleMode == false) && (reaperFreezeMode == false) &&(TimeUtils.millis() - lastHitTime) > (int)(0.5f*(float)SEC)) {
                    if (zombie.zType == 2) {
                        healthBarVal+=40;
                    }
                    else {
                        healthBarVal += 20;
                    }
                    zombie.playSound();
                    lastHitTime = TimeUtils.millis();

                    if (!tut4Complete) {
                        tut4Dialog.show(stage);
                        tut4Complete = true;
                        dialogShowing = true;
                    }

                    // log event
                    handle.writeString("event " + String.valueOf(TimeUtils.millis() - gameStartTime) + " " +  String.valueOf(levelCounter) + " 2 " + String.valueOf(maxHealthLosable - healthBarVal) + " " + String.valueOf(score) + " " + String.valueOf(levelScore) + " 0 1 0 0" +  " " + String.valueOf(closestZombieDistance()) + " " + String.valueOf(exitDistance()) + " " + String.valueOf(barrelStreak) + " " + String.valueOf(curTimerVal) + " " + "0 "+String.valueOf(abilityActive)+"\n", true);

                }

                if (reaperFreezeMode == true)
                {
                    // remove zombie
                    zombies.removeIndex(i);
                    clinkSound.play();
                }
            }

            if((TimeUtils.millis() - lastZombieChangeTime) > 1*SEC)
            {
                int zombieDir = zRand.nextInt(NUM_POS);
                int chance = zRand.nextInt(1);
                //if (chance == 0) {
                //    zombies.get(i).setTurboOn(false);
                //    zombies.get(i).setDirection(zombieDir);
                //    zombies.get(i).setSpeedFaster();
                //} else
                if (chance == 0) {
                    //System.out.println("turbo on");
                    zombies.get(i).setDirection(zombieDir);
                    //zombies.get(i).setTurboOn(true);
                }

                lastZombieChangeTime = TimeUtils.millis();
            }
        }

        // Spawn new zombie every 2 seconds
        if((TimeUtils.millis() - lastZombieSpawnTime) > Math.max(1000, 9000 - levelCounter*100))
            spawnZombie();
    }

    public int closestZombieDistance()
    {
        float min = 1000;
        for (Zombie z : zombies)
        {
            float diff = Math.abs(z.getPosition().x - player.getPosition().x) + Math.abs(z.getPosition().y - player.getPosition().y);
            if (diff < min)
            {
                min = diff;
            }
        }

        return (int)min;
    }

    public int exitDistance()
    {
        int distance = subMaze.heatgrid[subMaze.heatgrid.length-1-(((int)(player.centerY)/GRID_UNIT))][(int)(player.centerX/GRID_UNIT)];
        System.out.println(distance);
        return distance;//subMaze.heatgrid[subMaze.heatgrid.length-1-(((int)(player.centerY)/GRID_UNIT))][(int)(player.centerX/GRID_UNIT)];
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
            float volume =  100*((float)healthBarVal /maxHealthLosable);
            heartBeatSound.setVolume(volume);
            music.pause();
        } else
        {
            if(heartBeatSound.isPlaying()) {
                heartBeatSound.pause();
                music.play();
            }
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


            SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd;HH:mm:ss");//dd/MM/yyyy
            Date now = new Date();
            String strDate = sdfDate.format(now);

            String compl = computeComplexity();

            handle.writeString("game " + score + " " + gameDuration + " " + String.valueOf((int)(inputFrequency)) + " " + strDate + " " + String.valueOf(isDemo ? 1 : 0)+ " " +compl+ "\n", true);
            FileHandle actualLog = Gdx.files.local("gameInfoLog_"+String.valueOf(VERSION)+".txt");
            Gdx.files.local("gameInfoLog_temp.txt").moveTo(actualLog);
            gsm.set(new ScoreScreenState(gsm));
        }
    }

    @Override
    public void update(float dt) {


        if (!dialogShowing && !endDialogShowing) {
            handleInput();
            player.update(dt);
            if (resumeFlag == true)
            {
                Gdx.app.log("resumed before", player.getPosition().x + " " + player.getPosition().y);
            }
            // make sure this is called after player update
            checkPlayerBounds();

            //int size = subMaze.heatgrid.length - 1;
            //System.out.println("size: " + size);

            //System.out.println(subMaze.heatgrid[size-(((int)(player.centerY)/GRID_UNIT))][(int)(player.centerX/GRID_UNIT)]);

            if (resumeFlag == true)
            {
                Gdx.app.log("resumed after check bounds", player.getPosition().x + " " + player.getPosition().y);
            }
            //Gdx.app.log("Position", player.getPosition().x + " " + player.getPosition().y);

            cam.position.x = Math.round(player.getPosition().x);
            cam.position.y = Math.round(player.getPosition().y);

            if (location == 0) {


            } else if (location == 1) {
                updateFood(dt);
                updateAbilityBoxes(dt);
                updateHazards(dt);
                updateRedPoison(dt);
                //System.out.println(dt);
                lastUpdateTime += dt;

                if (lastUpdateTime > 1.0f )
                {
                    System.out.println("periodic log");
                    // log info
                    handle.writeString("event " + String.valueOf(TimeUtils.millis() - gameStartTime) + " " +  String.valueOf(levelCounter) + " 6 " + String.valueOf(maxHealthLosable - healthBarVal) + " " + String.valueOf(score) + " " + String.valueOf(levelScore) + " 0 0 0 " + String.valueOf(levelDuration) + " " + String.valueOf(closestZombieDistance()) + " " + String.valueOf(exitDistance()) + " " + String.valueOf(barrelStreak) + " " + String.valueOf(curTimerVal) + " " + "0 "+String.valueOf(abilityActive)+"\n", true);

                    lastUpdateTime = 0;
                }
                //updateSubMazeZombie(dt);
            }
            //updateHealthPack();


            updateGameInfo(dt);
            if (resumeFlag == true)
            {
                Gdx.app.log("resumed after gameinfo", player.getPosition().x + " " + player.getPosition().y);
            }
        }

        cam.update();
        if (resumeFlag == true)
        {
            Gdx.app.log("resumed after cam update", player.getPosition().x + " " + player.getPosition().y);
        }

        long msTime = System.currentTimeMillis();
        if ((msTime - timerCounter > 1500) && (location == 1) && (dialogShowing == false)) {
            curTimerVal--;
            timerCounter = TimeUtils.millis();
        }
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

                //update timer
                curTimerVal = maxTimerVal;
                if (maxTimerVal>10)
                    maxTimerVal -= 2;
                else
                    maxTimerVal = 10;
                timerCounter = System.currentTimeMillis();
                timerSound.stop();
                hauntSound.stop();
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
                timerSound.stop();
                hauntSound.stop();

                levelDuration = TimeUtils.millis() - levelStartTime;

                // log info
                handle.writeString("event " + String.valueOf(TimeUtils.millis() - gameStartTime) + " " +  String.valueOf(levelCounter) + " 3 " + String.valueOf(maxHealthLosable - healthBarVal) + " " + String.valueOf(score) + " " + String.valueOf(levelScore) + " 0 0 0 " + String.valueOf(levelDuration) + " " + String.valueOf(closestZombieDistance()) + " " + String.valueOf(exitDistance()) + " " + String.valueOf(barrelStreak) + " " + String.valueOf(curTimerVal) + " " + "0 "+String.valueOf(abilityActive)+"\n", true);

                // level up
                levelCounter += 1;

                //bonusAmount = (levelCounter + 1)*levelScore;
                score += levelScore; // + bonusAmount;

                lastLevelScore = levelScore;
                levelScore = 0;
                if (levelBarrelStreak !=4) {
                    barrelStreak = 0;
                    pitch = 1f;
                }
                levelBarrelStreak = 0;
                //barrelStreak = 0;

                endLevelAnimation = true;
                endLevelAnimationStart = TimeUtils.millis();

                System.out.println("Reached Level " + String.valueOf(levelCounter));

                // load new map and reset grid
                int settingType = levelCounter % 3;
                int iteration = (levelCounter / 3)%3;
                String prefix = "snow";
                int [][] heatmap;
                if (iteration == 0) {
                    heatmap = heatmap_snow_0;
                } else if (iteration == 1)
                {
                    heatmap = heatmap_snow_1;
                } else {
                    heatmap = heatmap_snow_2;
                }
                if (settingType == 1) {
                    if (iteration == 0) {
                        heatmap = heatmap_desert_0;
                    } else if (iteration == 1)
                    {
                        heatmap = heatmap_desert_1;
                    } else {
                        heatmap = heatmap_desert_2;
                    }
                    prefix = "desert";
                }
                else if (settingType == 2) {
                    if (iteration == 0) {
                        heatmap = heatmap_mars_0;
                    } else if (iteration == 1)
                    {
                        heatmap = heatmap_mars_1;
                    } else {
                        heatmap = heatmap_mars_2;
                    }
                    prefix = "mars";
                }
                subMaze = new Environment("roguelike-pack/Map/submaze_" + prefix+"_"+String.valueOf(iteration) + ".tmx", GRID_UNIT, heatmap, 10, 1, 1, 10, 21, 2, 1f);

                foodSupplies = new Array<Rectangle>();
                abilityBoxes = new Array<Rectangle>();
                redPoisonArr = new Array<PoisonObject>();
                zombiesBounds = new Array<Rectangle>();
                lastHitTime = 0;
                lastPoisonedTime = 0;

                //zombie = new Zombie(0, 0, UP);
                zombies = new Array<Zombie>();

                spawnInitialBarrels();



//                Timer.schedule(new Timer.Task() {
//
//                    @Override
//                    public void run() {
//                        endLevelDialog.show(stage);
//                        endDialogShowing = true;
//                        //endLevelDialog.setSize(0.8f * (float) Gdx.graphics.getWidth(), Gdx.graphics.getHeight() / 3);
//                    }
//                }, 2);

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
                sb.draw(redArrowUp, startRoom.getExitRectangle().getX(), startRoom.getExitRectangle().getY() - 50, 100, 100);
            }

            if (transitionFrame == true) {
                System.out.println("Transitioned back to outside");
                Vector3 newPosition = new Vector3(5* GRID_UNIT, 1*GRID_UNIT, 0);
                player.setPosition(newPosition);

                if (levelCounter != 0) {
                    /*int timerVal = 1;
                    Timer.schedule(new Timer.Task() {
                        @Override
                        public void run() {
                            endLevelDialog.show(stage);
                            //endLevelDialog.setSize(0.8f * (float) Gdx.graphics.getWidth(), Gdx.graphics.getHeight() / 3);
                        }
                    }, timerVal);*/
                    //endLevelDialog.show(stage);
                    endLevelDialog.show(stage);
                    endDialogShowing = true;
                }
            }
            if (resumeFlag == true)
            {
                Gdx.app.log("resumed before player draw", player.getPosition().x + " " + player.getPosition().y);
            }
            sb.draw(player.getTexture(), player.getPosition().x, player.getPosition().y);
            if (resumeFlag == true)
            {
                Gdx.app.log("resumed after player draw", player.getPosition().x + " " + player.getPosition().y);

            }

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
                Vector3 newPosition = new Vector3(10 * GRID_UNIT, 1*GRID_UNIT, 0);
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
                sb.draw(food.getFoodTexture(2), foodItem.x, foodItem.y);
            }

            // Drawing ability boxes
            for (Rectangle abilityItem : abilityBoxes) {
                sb.draw(questionMarkSmall, abilityItem.x, abilityItem.y);
            }

            sb.draw(player.getTexture(), player.getPosition().x, player.getPosition().y);

            sb.end();
            transitionFrame = false;

            if (!tut2Complete) {
                tut2Dialog.show(stage);
                tut2Complete = true;
                dialogShowing = true;
            }
        }

    }

    // Render Game Info
    private void hudRender(SpriteBatch hb)
    {

        hb.begin();
        //Draw health bar
        Vector3 positionBar = new Vector3(0.04f*Gdx.graphics.getWidth(), 0.96f*Gdx.graphics.getHeight(), 0);

        NinePatch frontBar= new NinePatch(fb, 8, 8, 8, 8);
        NinePatch backBar = new NinePatch(bb, 8, 8, 8, 8);

        float fullBar = 0.92f*Gdx.graphics.getWidth();

        backBar.draw(hb, positionBar.x, positionBar.y, fullBar, 0.03f*Gdx.graphics.getHeight());
        float fbVal = (maxHealthLosable-healthBarVal)*fullBar/(maxHealthLosable);
        if (fbVal > 0)
            frontBar.draw(hb, positionBar.x, positionBar.y, fbVal, 0.03f*Gdx.graphics.getHeight());

        // Draw Score
        if (resumeFlag == true)
        {
            Gdx.app.log("resumed in hud", player.getPosition().x + " " + player.getPosition().y);

        }
//        Vector3 positionScore = new Vector3(0.03f*Gdx.graphics.getWidth(),0.9625f*Gdx.graphics.getHeight(), 0);
//        font.setColor(1.0f, 1.0f, 1.0f, 1.0f);
//        //font.draw(hb, "Score : " + String.valueOf(score), positionScore.x, positionScore.y);
//        Label.LabelStyle textStyle;
//        Label text;
//        textStyle = new Label.LabelStyle();
//        textStyle.font = font;
//        text = new Label("Score : " + String.valueOf(score), textStyle);
//        float screenScale = 1f*Gdx.graphics.getWidth()/(RoamGame.WIDTH);
//        //System.out.println(RoamGame.WIDTH + " " + Gdx.graphics.getWidth());
//        text.setFontScale(screenScale, screenScale);
//        text.setPosition(positionScore.x, positionScore.y);
//        text.draw(hb, 2f);

        //update timer
        if((curTimerVal <= 0 ) && (location == 1)){
            timerSound.stop();
            hauntSound.stop();
            deathSound.play();
            score = score/2;
            gameDuration = TimeUtils.millis() - gameStartTime;
            prefs.putInteger("score", score);
            prefs.putInteger("factor1", (int)currentFactor1);
            prefs.putInteger("factor2", (int)currentFactor2);
            prefs.flush();

            SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd;HH:mm:ss");//dd/MM/yyyy
            Date now = new Date();
            String strDate = sdfDate.format(now);

            String compl = computeComplexity();
            handle.writeString("game " + score + " " + gameDuration + " " + String.valueOf((int)(inputFrequency)) + " " + strDate + " " + String.valueOf(isDemo ? 1 : 0)+ " " + compl+ "\n", true);
            FileHandle actualLog = Gdx.files.local("gameInfoLog_"+String.valueOf(VERSION)+".txt");
            Gdx.files.local("gameInfoLog_temp.txt").moveTo(actualLog);
            gsm.set(new ScoreScreenState(gsm));
        } else if((curTimerVal < 20) && (location == 1)) {
            bfont.setColor(255,0,0,1);
            timerSound.setLooping(true);
            timerSound.setVolume(0.3f);
            timerSound.play();
            hauntSound.setLooping(true);
            hauntSound.setVolume(0.3f);
            hauntSound.play();
        } else {
            bfont.setColor(new Color(255, 255, 0, 1));
        }
        String timerText = "Timer : " + String.valueOf(curTimerVal);
        layout.setText(bfont,  timerText);
        float twidth = layout.width;
        bfont.draw(hb, "Timer : " + String.valueOf(curTimerVal), (Gdx.graphics.getWidth() - 0.03f*Gdx.graphics.getHeight()) - (twidth), 0.94f*Gdx.graphics.getHeight());

//        Label textTolerance;
//        textStyle = new Label.LabelStyle();
//        textStyle.font = font;
        if (resumeFlag == true)
        {
            Gdx.app.log("resumed in hud2", player.getPosition().x + " " + player.getPosition().y);

        }
        if (scoreAnimation) {
            //String suffix = "th";
            float alpha = (1f - ((float)(TimeUtils.millis() - scoreAnimationStart)/5000f));
//            if (barrelStreak == 1)
//                suffix = "st";
//            else if (barrelStreak == 2)
//                suffix = "nd";
//            else if (barrelStreak == 3)
//                suffix = "rd";
            bfont.setColor(new Color(255, 255, 0, alpha));
            bfont.draw(hb, "+   " + barrelStreak + "-Streak * "+lastBarrelScore + "   =   " + barrelStreak *lastBarrelScore+ "   points!", Gdx.graphics.getWidth() / 8, Gdx.graphics.getHeight() / 2);
            if (TimeUtils.millis() - scoreAnimationStart > 5000 )
            {
                scoreAnimation = false;
            }
        }


        if (healthAnimation) {

            float alpha = (1f - ((float)(TimeUtils.millis() - healthAnimationStart)/5000f));

            bfont.setColor(new Color(255, 255, 255, alpha));
            String healthText = "+ "+lastHealthAdded + " health!";
            layout.setText(bfont, healthText);
            float width = layout.width;
            bfont.draw(hb, healthText, (Gdx.graphics.getWidth() / 2) - (width/2), Gdx.graphics.getHeight() / 2);
            if (TimeUtils.millis() - healthAnimationStart > 5000 )
            {
                healthAnimation = false;
            }
        }

        if (abilityAnimation)
        {

            float alpha = (1f - ((float)(TimeUtils.millis() - abilityAnimationStart)/3000f));

            if (reaperFreezeMode)
            {
                bfont.setColor(new Color(0, 255,0, alpha));
                layout.setText(bfont, "Freeze!");
                float width = layout.width;

                bfont.draw(hb, "Freeze!", (Gdx.graphics.getWidth() / 2) - (width/2), 0.4f*Gdx.graphics.getHeight() );
            } else if (playerSlowMode)
            {
                bfont.setColor(new Color(255, 0, 0, alpha));
                layout.setText(bfont, "Slowed!");
                float width = layout.width;
                bfont.draw(hb, "Slowed!", (Gdx.graphics.getWidth() / 2) - (width/2), 0.4f*Gdx.graphics.getHeight() );
            } else if (invincibleMode)
            {
                bfont.setColor(new Color(0, 255,0, alpha));
                layout.setText(bfont, "Can't touch this!");
                float width = layout.width;
                bfont.draw(hb, "Can't touch this!", (Gdx.graphics.getWidth() / 2) - (width/2), 0.4f*Gdx.graphics.getHeight());
            } else if (timerLossMode)
            {
                bfont.setColor(new Color(255, 0, 0, alpha));
                layout.setText(bfont, "- 5 seconds");
                float width = layout.width;
                bfont.draw(hb, "- 5 seconds", (Gdx.graphics.getWidth() / 2) - (width/2), 0.4f*Gdx.graphics.getHeight());
            } else if (healthLossMode)
            {
                bfont.setColor(new Color(255, 0, 0, alpha));
                layout.setText(bfont, "- 25% Health!");
                float width = layout.width;
                bfont.draw(hb, "- 25% Health!", (Gdx.graphics.getWidth() / 2) - (width/2), 0.4f*Gdx.graphics.getHeight());
            }
            if (TimeUtils.millis() - abilityAnimationStart > 3000 )
            {
                abilityAnimation = false;
            }
        }

        if (levelAnimation) {

            float alpha = (1f - ((float)(TimeUtils.millis() - 500 - levelAnimationStart)/2500f));
            if ((TimeUtils.millis() - levelAnimationStart > 500) ) {
                bfont.setColor(new Color(255, 255, 255, alpha));
                String lText = "Entering   Level   " + (levelCounter + 1) + "!";
                layout.setText(bfont, lText);
                float width = layout.width;
                bfont.draw(hb, lText, (Gdx.graphics.getWidth() / 2) - (width/2) , Gdx.graphics.getHeight() / 3);
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
                String eText = "Congrats!   Received   " + lastLevelScore + "   points!";
                layout.setText(bfont, eText);
                float width = layout.width;
                bfont.draw(hb, eText, (Gdx.graphics.getWidth() / 2) - (width/2) , Gdx.graphics.getHeight() / 3);
            }
            if ((TimeUtils.millis() - endLevelAnimationStart > 3000) )
            {
                endLevelAnimation = false;
            }
        }

        if (!tut1Complete) {
            tut1Dialog.show(stage);
            tut1Complete = true;
        }

        if (resumeFlag == true)
        {
            Gdx.app.log("resumed in hud3", player.getPosition().x + " " + player.getPosition().y);

        }

        bfont_small.setColor(new Color(255, 255, 255, 1));
        String sText = "Score : " + score;

        bfont_small.draw(hb, sText, 0.03f*Gdx.graphics.getHeight() , 0.94f*Gdx.graphics.getHeight());
        sText = "Level Score : " + levelScore;

        bfont_small.draw(hb, sText, 0.03f*Gdx.graphics.getHeight() , 0.90f*Gdx.graphics.getHeight());

        sText = "Streak : " + barrelStreak;

        bfont_small.draw(hb, sText, 0.03f*Gdx.graphics.getHeight(), 0.86f*Gdx.graphics.getHeight());

//        textTolerance = new Label("Current Health: " + String.valueOf((int)(100*(1 - (healthBarVal/maxHealthLosable)))) , textStyle);

//        textTolerance = new Label("Level " + String.valueOf(levelCounter+1) + " Score: " + String.valueOf(levelScore) , textStyle);
//        //System.out.println(RoamGame.WIDTH + " " + Gdx.graphics.getWidth());
//        textTolerance.setFontScale(screenScale, screenScale);
//        textTolerance.setPosition(positionScore.x, positionScore.y - 0.03f * Gdx.graphics.getHeight());
//
//        textTolerance.draw(hb, 2f);
//
//        textTolerance = new Label("Streak: " + String.valueOf(barrelStreak) , textStyle);
//        //System.out.println(RoamGame.WIDTH + " " + Gdx.graphics.getWidth());
//        textTolerance.setFontScale(screenScale, screenScale);
//        textTolerance.setPosition(positionScore.x, positionScore.y - 0.06f * Gdx.graphics.getHeight());
//
//        textTolerance.draw(hb, 2f);

        if (resumeFlag == true)
        {
            Gdx.app.log("resumed in hud4", player.getPosition().x + " " + player.getPosition().y);

        }
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
//        Vector3 dPadPosition = new Vector3(0.60f * Gdx.graphics.getWidth(), 0.04f*Gdx.graphics.getHeight(), 0);
//        //Color c = hb.getColor();
//        //hb.setColor(c.r, c.g, c.b, .3f);//set alpha to 0.3
//        //hb.draw(dpadCircle,dPadPosition.x, dPadPosition.y);
//        dpadCrossSprite.setPosition(dPadPosition.x, dPadPosition.y);
//        dpadCrossSprite.setAlpha(.3f);
//        dpadCrossSprite.draw(hb);

        //hb.draw(dpadCrossSprite, dPadPosition.x, dPadPosition.y);
        //hb.setColor(c.r, c.g, c.b, 1f);

        abilityButton.update(hb, numBoxes);


            if (Gdx.input.isTouched()) {
                int pointX = Gdx.input.getX();
                int pointY = Gdx.input.getY();
                if ((numBoxes > 0) && (abilityActive == 0) && abilityButton.checkIfClicked(pointX, Gdx.graphics.getHeight() - pointY))
                {
                    abilityActive = 1;
                    // choose an ability randomly
                    Random rand = new Random();
                    int mode = rand.nextInt(3);

                    System.out.println("ability mode: "+mode);


                        numBoxes--;

                    if (mode == 0) {
                        setInvincibilityMode();
                    } else if (mode == 1) {
                        setZombieFreezeMode();
                    }
                    else if (mode == 2) {
                        abilityActive = 2;
                        //update negative consequence
                        Random negRand = new Random();
                        int negOutcome = negRand.nextInt(3);
                        setNegOutCome(negOutcome);
                        //setZombieFastMode();
                    }

                    abilityAnimationStart = TimeUtils.millis();
                    abilityAnimation = true;
                    scoreAnimation = false;

                    // log event
                    handle.writeString("event " + String.valueOf(TimeUtils.millis() - gameStartTime) + " " +  String.valueOf(levelCounter) + " 5 " + String.valueOf(maxHealthLosable - healthBarVal) + " " + String.valueOf(score) + " " + String.valueOf(levelScore) + " 0 0 0 0" +  " " + String.valueOf(closestZombieDistance()) + " " + String.valueOf(exitDistance()) + " " + String.valueOf(barrelStreak) + " " + String.valueOf(curTimerVal) + " " + String.valueOf(mode)+ " " +String.valueOf(abilityActive)+"\n", true);
                }
            }

        if (reaperFreezeMode)
        {
            freezeButton.setPosition(0.03f*Gdx.graphics.getWidth() , 0.03f*Gdx.graphics.getHeight() );
            freezeButton.draw(hb);

        } else if (invincibleMode)
        {
            invincibleButton.setPosition(0.03f*Gdx.graphics.getWidth() , 0.03f*Gdx.graphics.getHeight() );
            invincibleButton.draw(hb);
        } else if (reaperFastMode)
        {
            fastButton.setPosition(0.03f*Gdx.graphics.getWidth(), 0.03f*Gdx.graphics.getHeight() );
            fastButton.draw(hb);
        } else if (playerSlowMode)
        {
            slowMotionButton.setPosition(0.03f*Gdx.graphics.getWidth(), 0.03f*Gdx.graphics.getHeight() );
            slowMotionButton.draw(hb);
        } else if (healthLossMode)
        {
            healthLossButton.setPosition(0.03f*Gdx.graphics.getWidth(), 0.03f*Gdx.graphics.getHeight() );
            healthLossButton.draw(hb);
        } else if (timerLossMode)
        {
            timerLossButton.setPosition(0.03f*Gdx.graphics.getWidth(), 0.03f*Gdx.graphics.getHeight() );
            timerLossButton.draw(hb);
        }

        //}

        hb.end();

        // draw blood screen
        if (healthBarVal >= 0.5f*maxHealthLosable)
        {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            srender.begin(ShapeRenderer.ShapeType.Filled);
            float alpha = 1.25f*((float)healthBarVal/maxHealthLosable - 0.5f);
            //System.out.println(alpha);
            Color color = new Color(1f, 0f, 0f, alpha);
            srender.setColor(color);
            srender.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            srender.end();
            Gdx.gl.glDisable(GL20.GL_BLEND);

        }



    }

    public void setInvincibilityMode()
    {
        //abilityButton.setTexture(invincibleButton);

        // reaper  hits won't hit
        player.boostPlayer(1.5f);
        invincibilityMusic.setLooping(true);
        invincibilityMusic.play();
        invincibleMode = true;

        // last for 5 seconds
        Timer.schedule(new Timer.Task() {

                    @Override
                    public void run() {
                        //abilityBoxPickedUp = false;
                        abilityActive = 0;
                        invincibleMode = false;
                        invincibilityMusic.stop();

                    }
                }, 5);
    }

    public void setZombieFastMode()
    {
        //abilityButton.setTexture(fastButton);

        // all reapers speed up
        for (Zombie z : zombies)
        {
            z.setTurboOn(true);
        }

        //reaperFastModeMusic.setLooping(true);
        reaperFastModeMusic.play();
        reaperFastMode = true;

        // last for 5 seconds
        Timer.schedule(new Timer.Task() {

            @Override
            public void run() {
                //abilityBoxPickedUp = false;
                abilityActive = 0;
                reaperFastMode = false;
                //reaperFastModeMusic.stop();

                // all reapers back to normal
                for (Zombie z : zombies)
                {
                    z.setTurboOn(false);
                }

            }
        }, 5);
    }

    public void setNegOutCome(int mode)
    {
        reaperFastModeMusic.play();
        if (mode == 0){
            //lose health
            healthBarVal+=50;
            healthLossMode = true;
        } else if (mode == 1){
            //reduce timer
            //if (curTimerVal > 5) {
                curTimerVal-=5;
            //}
            timerLossMode = true;
        } else if (mode == 2) {
            //slow player
            player.slowPlayer();
            playerSlowMode = true;
        }
        // last for 5 seconds
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                //abilityBoxPickedUp = false;
                abilityActive = 0;
                healthLossMode = false;
                playerSlowMode = false;
                timerLossMode = false;
            }
        }, 5);
    }

    public void setZombieFreezeMode()
    {
        //abilityButton.setTexture(freezeButton);

        // all reapers speed up
        for (Zombie z : zombies)
        {
            z.setFreezeOn(true);
        }

       // reaperFreezeModeMusic.setLooping(true);
        reaperFreezeModeMusic.play();
        reaperFreezeMode = true;

        // last for 5 seconds
        Timer.schedule(new Timer.Task() {

            @Override
            public void run() {
                //abilityBoxPickedUp = false;
                abilityActive = 0;
                reaperFreezeMode = false;
                //reaperFreezeModeMusic.stop();

                // all reapers back to normal
                for (Zombie z : zombies)
                {
                    z.setFreezeOn(false);
                }
            }
        }, 5);
    }


    @Override
    public void render(SpriteBatch sb, SpriteBatch hb) {

        mainRender(sb);
        hudRender(hb);

        stage.act();
        stage.draw();

        if (resumeFlag == true)
        {
            Gdx.app.log("resumed in render", player.getPosition().x + " " + player.getPosition().y);
            resumeFlag = false;
        }
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
            Random rand = new Random();
            int gridx = rand.nextInt(subMaze.gridWidth - 1);
            int gridy = rand.nextInt(subMaze.gridHeight - 1);
            zombie.x = GRID_UNIT * gridx;
            zombie.y = GRID_UNIT * gridy;
            zombie.width = GRID_UNIT;
            zombie.height = GRID_UNIT;

            Zombie newZombie = new Zombie(zombie.x, zombie.y, zombieDir, 0);
            if (levelCounter >= 1) {
                Random sRand = new Random();
                int zTypeRand = sRand.nextInt(10);
                if (zTypeRand >= zombiePivot) {
                    newZombie = new Zombie(zombie.x, zombie.y, zombieDir, 2);
                }
                if (zombiePivot <= 0)
                    zombiePivot = 0;
                else
                    zombiePivot-=2;
            }

            boolean overlapped = false;

            int num_iterations = 0;

            for (int index = 0; index < subMaze.obstacles.size; index++) {
                Rectangle item = subMaze.obstacles.get(index);
                if (item.overlaps(newZombie.getBounds())) {
                    overlapped = true;

                }
            }

            while (num_iterations < 30 && overlapped == true)
            {
                overlapped = false;
                gridx = rand.nextInt(subMaze.gridWidth - 1);
                gridy = rand.nextInt(subMaze.gridHeight - 1);
                zombie.x = GRID_UNIT * gridx;
                zombie.y = GRID_UNIT * gridy;
                newZombie = new Zombie(zombie.x, zombie.y, zombieDir, 0);
                for (int index = 0; index < subMaze.obstacles.size; index++) {
                    Rectangle item = subMaze.obstacles.get(index);
                    if (item.overlaps(newZombie.getBounds())) {
                        overlapped = true;

                    }
                }

                num_iterations++;
             }
            // check if location is occupied already
            if ((subMaze.occupiedGrid[gridy][gridx] == -1) && overlapped == false) {
                zombiesBounds.add(zombie);
                subMaze.occupiedGrid[gridy][gridx] = zombieDir;
                if (reaperFreezeMode)
                {
                    newZombie.setFreezeOn(true);
                }
                if (reaperFastMode)
                {
                    newZombie.setTurboOn(true);
                }
                zombies.add(newZombie);
                //Zombie z = zombies.get(i);
                // System.out.println(z.getDirection());
            }
        }

        lastZombieSpawnTime = TimeUtils.millis();
    }

    @Override
    public void dispose() {
        //player.dispose();
        Gdx.app.log("paused", "disposing");
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
        redArrowUp.dispose();
        xscreen.dispose();
        if (invincibleButtonTexture != null)
            invincibleButtonTexture.dispose();
        if (freezeButtonTexture != null)
            freezeButtonTexture.dispose();
        if (fastButtonTexture != null)
            fastButtonTexture.dispose();
        if (timerSound != null)
            timerSound.dispose();

    }

}
