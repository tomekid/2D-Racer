package com.mediocrefireworks.realracer.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.mediocrefireworks.realracer.RealRacer;
import com.mediocrefireworks.realracer.RealRacer.Screens;
import com.mediocrefireworks.realracer.SimpleSpatial;
import com.mediocrefireworks.realracer.cars.Car;
import com.mediocrefireworks.realracer.sounndAndMusic.SoundManager;

import java.util.HashMap;
import java.util.Map;

public class GarageMenuScreen implements Screen {

    final RealRacer game;
    OrthographicCamera menuCamera;
    Screens menuName = Screens.GarageMenu;
    //rube  box2d stuff
    World world;
    Array<SimpleSpatial> mSpatials;
    Map<String, Texture> mTextureMap;
    OrthographicCamera carCamera;
    TextButton selectedButton;
    private Stage stage;
    private float screenWidth;
    private float screenHeight;
    private int FINALWIDTH = 10;


    public GarageMenuScreen(final RealRacer game) {

        this.game = game;
        final Car currentCar = game.player.getCurrentCar();


        stage = new Stage();

        Gdx.input.setInputProcessor(stage);
        Gdx.graphics.setVSync(false);
        //GLTexture.setEnforcePotImages(false);

        screenWidth = game.getScreenWidth();
        screenHeight = game.getScreenHeight();

        menuCamera = new OrthographicCamera();
        menuCamera.setToOrtho(false, screenWidth, screenHeight);

        carCamera = new OrthographicCamera(1, screenHeight / screenWidth);
        carCamera.setToOrtho(false, game.getScreenWidth(FINALWIDTH), game.getScreenHeight(FINALWIDTH));
        //	carCamera.position.set(-10,-10,0);
        carCamera.zoom = 0.5f;


        //load a new world with clears spatials and texturemap
        world = new World(new Vector2(0, -10), true);
        mSpatials = new Array<SimpleSpatial>();
        mTextureMap = new HashMap<String, Texture>();


        final Menu menu = new Menu(game, this, menuName);
        stage.addActor(menu.mainTable);

		
		
		/*
         * LEFTMENU CAR SELECTION LIST
		 */

        final TextButtonStyle unselectedstyle = new TextButtonStyle(newTDR("menubutton"), newTDR("menubuttonselected"), newTDR("menubuttonselected"), game.font);


        final ButtonGroup selectCarButtons = new ButtonGroup();
        final ButtonGroup carButtons = new ButtonGroup();

        if (currentCar == null) {
            carButtons.setMinCheckCount(-1);
            selectCarButtons.setMinCheckCount(-1);
        } else {
            carButtons.setMinCheckCount(-1);
            selectCarButtons.setMinCheckCount(-1);
        }

        for (final Car car : game.player.cars) {
            TextButton bs;
            String name = car.toString();
            //reduce long car name
            if (name.length() > 20) {
                name = name.substring(0, 20) + "...";
            }

            bs = new TextButton(name, unselectedstyle);
            carButtons.add(bs);
            bs.setName(car.getFullName());
            bs.pad(10);

            bs.addListener(new ClickListener() {

                @Override
                public void clicked(InputEvent event, float f, float v) {
					
					/*
					 * CAR AND MIDDLE MENU UPDATING
					 */
                    //clear box2d stuff and add new car

                    SoundManager.buttonClicked(SoundManager.click2, game.sounds);

                    final Car selectedCar = car;
                    world = new World(new Vector2(0, -10), true);
                    game.disposeList(mTextureMap.values());
                    mTextureMap.clear();
                    mSpatials.clear();
                    RealRacer.loadCarAndLevelFromJson(world, mSpatials, mTextureMap, car, game.showRoomTrack);
                    carCamera.position.set(car.body.getWorldCenter().x, car.body.getWorldCenter().y, 0);

                    LabelStyle infoText = new LabelStyle(game.font, Color.WHITE);

                    carButtons.setMinCheckCount(1);
                    menu.infoTable.clear();
                    menu.infoTable.add(new Label("Make: " + selectedCar.make, infoText)).left();
                    menu.infoTable.row();
                    menu.infoTable.add(new Label("Model: " + selectedCar.model, infoText)).left();
                    menu.infoTable.row();
                    menu.infoTable.add(new Label("Year: " + selectedCar.year, infoText)).left();
                    menu.infoTable.row();
                    menu.infoTable.add(new Label("\n" + selectedCar.description + "\n  ", infoText)).left();
                    menu.infoTable.row();
                    TextButton bt = new TextButton("Tune", unselectedstyle);
                    final TextButton bse = new TextButton("Select", unselectedstyle);
                    //this button group doesnt seem to work
                    selectCarButtons.add(bse);

                    if (game.player.getCurrentCar() != null && car == game.player.getCurrentCar()) {
                        System.out.println("car = currentCar" + car);
                        bse.setChecked(true);
                        bse.setText("Selected");
                    } else {
                        bse.setText("Select");
                    }
                    bt.pad(10);
                    bse.pad(10);
                    menu.infoTable.add(bt).left();
                    menu.infoTable.add(bse);
					/*
					 * select car button  added to buttongroup to make radio button with others
					 */
                    bse.addListener(new ClickListener() {

                        @Override
                        public void clicked(InputEvent event, float x, float y) {

                            System.out.println(" selected car " + selectedCar.getFullName());
                            SoundManager.buttonClicked(SoundManager.click2, game.sounds);
                            selectCarButtons.setMinCheckCount(1);
                            bse.setText("Selected");
                            game.player.selectCar(selectedCar);
                            menu.updatePlayerCurrents();

                        }

                    });

                    bt.addListener(new ClickListener() {

                        @Override
                        public void clicked(InputEvent event, float x, float y) {

                            SoundManager.buttonClicked(SoundManager.click1, game.sounds);
                            game.selectedCar = car;
                            game.changeScreen(Screens.TuneMenu, GarageMenuScreen.this);
                        }

                    });
					
					/*
					 * END 
					 */


                }
            });


            //add margin

            menu.leftListTable.add().height(screenHeight * .02f);
            menu.leftListTable.row();
            menu.leftListTable.add().width(screenWidth * .02f);
            menu.leftListTable.add(bs).width(150);
            menu.leftListTable.add().width(screenWidth * .02f);
            //create new row with margin
            menu.leftListTable.row();

        }
		
		
		
		
		/*
		 * END LEFTMENU CAR SELECTION LIST
		 */


    }

    public TextureRegionDrawable newTDR(String textureName) {
        return new TextureRegionDrawable(new TextureRegion(game.textures.get(textureName)));
    }


    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        carCamera.update();
        menuCamera.update();

        world.step(game.BOX_STEP, game.BOX_VELOCITY_ITERATIONS, game.BOX_POSITION_ITERATIONS);
        game.batch.setProjectionMatrix(menuCamera.combined);
        game.batch.begin();
        game.batch.draw(game.textures.get("background"), 0, 0, screenWidth, screenHeight);

        //car render
        RealRacer.realRacerBox2DDraw(mSpatials, carCamera, game);

        game.batch.setProjectionMatrix(menuCamera.combined);

        //String playerCurrents = "Current Vehicle: "+game.player.getCurrentCarName()+"       cr:"+game.player.balance;
        //game.font.draw(game.batch, playerCurrents, screenWidth*.4f, screenHeight*.98f);
        game.batch.end();
        //System.out.println(" isdraggin" +scrollPane +"  "+ scrollPane.isTouchable() );

        stage.draw();
        stage.act();

        //	Table.drawDebug(stage);
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
        // TODO Auto-generated method stub

        //dispose(game.textures.values());


        game.disposeList(mTextureMap.values());
        world.dispose();
        world = null;

    }


}
