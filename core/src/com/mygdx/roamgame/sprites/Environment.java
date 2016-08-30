package com.mygdx.roamgame.sprites;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

/**
 * Created by Justin on 2016-08-24.
 */
public class Environment {

    public int gridHeight;
    public int gridWidth;
    public int pixelHeight;
    public int pixelWidth;

    private TiledMap map;
    public TiledMapRenderer renderer;

    public int[][] occupiedGrid;

    private int startingPosX;
    private int startingPosY;

    public int barrierLayerNum;

    // Location to leave environment (when player overlaps this rectangle)
    private Rectangle exitRectangle;

    private int gridUnit;

    // Obstacles
    public Array<Rectangle> obstacles;

    public Environment (String mapName, int grid_unit, int startX, int startY, int barrierLayerNo, int exitX, int exitY, int exitWidth)
    {

        gridUnit = grid_unit;
        startingPosX = startX*grid_unit;
        startingPosY = startY*grid_unit;

        map = new TmxMapLoader().load(mapName);
        renderer = new OrthogonalTiledMapRenderer(map, 2f);

        barrierLayerNum = barrierLayerNo;

        TiledMapTileLayer barrierLayer = (TiledMapTileLayer)(map.getLayers().get(barrierLayerNo));

        gridHeight = barrierLayer.getHeight();
        gridWidth = barrierLayer.getWidth();

        pixelHeight = gridHeight*grid_unit;
        pixelWidth = gridHeight*grid_unit;

        // create obstacles array
        obstacles = new Array<Rectangle>();

        // create occupied Grid to correspond to obstacles and other things
        occupiedGrid = new int[gridHeight][gridWidth];
        for (int row=0; row<gridHeight; row++)
        {
            for (int col=0; col<gridWidth; col++)
            {
                occupiedGrid[row][col] = -1;

                if ((barrierLayer.getCell(col, row) != null )) {
                    occupiedGrid[row][col] = -2;

                    Rectangle obstacle = new Rectangle();
                    obstacle.x = gridUnit*col;
                    obstacle.y = gridUnit*row;
                    obstacle.width = gridUnit;
                    obstacle.height = gridUnit;

                    obstacles.add(obstacle);
                }

            }
        }

        // special rectangle for exit area
        exitRectangle = new Rectangle();
        exitRectangle.x = gridUnit *exitX;
        exitRectangle.y = gridUnit *exitY;
        exitRectangle.width = exitWidth* gridUnit;
        exitRectangle.height = gridUnit;

        // set occupied grid too
        occupiedGrid[exitY][exitX] = -9;

    }

    public int getStartingPosX()
    {
        return startingPosX;
    }

    public int getStartingPosY()
    {
        return startingPosY;
    }

    public Rectangle getExitRectangle() {
        return exitRectangle;
    }



}
