package com.mediocrefireworks.realracer.menu;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import com.mediocrefireworks.realracer.RealRacer;
import com.mediocrefireworks.realracer.RealRacer.Screens;
import com.mediocrefireworks.realracer.SimpleSpatial;
import com.mediocrefireworks.realracer.cars.Car;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MainMenuScreen implements Screen {

    final RealRacer game;
    //rube  box2d stuff
    World world;
    Array<SimpleSpatial> mSpatials;
    Map<String, Texture> mTextureMap;
    private OrthographicCamera carCamera;
    private OrthographicCamera menuCamera;
    private float screenWidth;
    private float screenHeight;
    private Stage stage;
    private Screens menuName = Screens.MainMenu;
    private int FINALWIDTH = 10;


    public MainMenuScreen(final RealRacer game) {

        this.game = game;


        screenWidth = game.getScreenWidth();
        screenHeight = game.getScreenHeight();

        stage = new Stage();

        Gdx.input.setInputProcessor(stage);

        //GLTexture.setEnforcePotImages(false);

        menuCamera = new OrthographicCamera();

        menuCamera.setToOrtho(false, screenWidth, screenHeight);

        carCamera = new OrthographicCamera(1, screenHeight / screenWidth);
        carCamera.setToOrtho(false, game.getScreenWidth(FINALWIDTH), game.getScreenHeight(FINALWIDTH));
        carCamera.position.set(-10, -10, 0);
        carCamera.zoom = 0.5f;


        //loading all rube box2d stuff

        world = new World(new Vector2(0, -10), true);

        mSpatials = new Array<SimpleSpatial>();
        mTextureMap = new HashMap<String, Texture>();


        Car car = game.player.getCurrentCar();
        if (car != null) {
            System.out.println(" loaded car ");


            game.disposeList(mTextureMap.values());
            mTextureMap.clear();
            mSpatials.clear();
            RealRacer.loadCarAndLevelFromJson(world, mSpatials, mTextureMap, car, game.showRoomTrack);
            carCamera.position.set(car.body.getWorldCenter().x, car.body.getWorldCenter().y, 0);


        }


        // Add widgets to the table here.


        Menu menu = new Menu(game, this, menuName);
        stage.addActor(menu.mainTable);

        menu.leftTable.setBackground((Drawable) null);
        menu.middleTable.setBackground((Drawable) null);

        menu.leftListTable.add().width(screenWidth * .04f);
        menu.leftListTable.add().width(150);


        //create new row with margin
        menu.leftListTable.row();


        //set debug
    /*
        menu.mainTable.debug();
		menu.leftListTable.debug();
		menu.infoTable.debug();
		menu.rightTable.debug();
		menu.rightTable.debug();
	*/
    }


    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        carCamera.update();
        menuCamera.update();

        world.step(game.BOX_STEP, game.BOX_VELOCITY_ITERATIONS, game.BOX_POSITION_ITERATIONS);

        game.batch.begin();
        game.batch.draw(game.textures.get("background"), 0, 0, screenWidth, screenHeight);

        RealRacer.realRacerBox2DDraw(mSpatials, carCamera, game);

        game.batch.setProjectionMatrix(menuCamera.combined);
        game.batch.draw(game.textures.get("title"), screenWidth * .1f, screenHeight * .1f);

        //car render


        game.batch.setProjectionMatrix(menuCamera.combined);

        //String playerCurrents = "Current Vehicle: "+game.player.getCurrentCarName()+"       cr:"+game.player.balance;
        //game.font.draw(game.batch, playerCurrents, screenWidth*.4f, screenHeight*.98f);
        game.batch.end();
        //System.out.println(" isdraggin" +scrollPane +"  "+ scrollPane.isTouchable() );

        stage.draw();
        stage.act();

        //render

        //Table.drawDebug(stage);
        if (Gdx.input.isTouched()) {
            //	game.setScreen(new GameScreen(game));
            //	dispose();
        }
    }

    @Override
    public void resize(int width, int height) {
        // TODO Auto-generated method stub

    }

    @Override
    public void show() {
        // TODO Auto-generated method stub

    }

    @Override
    public void hide() {
        // TODO Auto-generated method stub

    }

    @Override
    public void pause() {
        // TODO Auto-generated method stub

    }

    @Override
    public void resume() {
        // TODO Auto-generated method stub

    }

    @Override
    public void dispose() {


        Collection<Texture> texturesIterable = mTextureMap.values();
        for (Texture t : texturesIterable) {
            t.dispose();
        }
    }

}
