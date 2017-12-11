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
import com.mediocrefireworks.realracer.Dealer;
import com.mediocrefireworks.realracer.RealRacer;
import com.mediocrefireworks.realracer.RealRacer.Screens;
import com.mediocrefireworks.realracer.SimpleSpatial;
import com.mediocrefireworks.realracer.cars.Car;
import com.mediocrefireworks.realracer.sounndAndMusic.SoundManager;

import java.util.HashMap;
import java.util.Map;

public class DealerMenuScreen implements Screen {

    final RealRacer game;
    OrthographicCamera camera;
    Screens menuName = Screens.DealerMenu;
    //rube  box2d stuff
    World world;
    Array<SimpleSpatial> mSpatials;
    Map<String, Texture> mTextureMap;
    OrthographicCamera carCamera;
    private Dealer dealer;
    private float screenWidth;
    private float screenHeight;
    private Stage stage;
    private int FINALWIDTH = 10;

    public DealerMenuScreen(final RealRacer game) {

        this.game = game;
        dealer = game.getDealer();
        screenWidth = game.getScreenWidth();
        screenHeight = game.getScreenHeight();

        stage = new Stage();

        Gdx.input.setInputProcessor(stage);
        //	GLTexture.setEnforcePotImages(false);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, screenWidth, screenHeight);


        carCamera = new OrthographicCamera(1, screenHeight / screenWidth);
        carCamera.setToOrtho(false, game.getScreenWidth(FINALWIDTH), game.getScreenHeight(FINALWIDTH));
        //	carCamera.position.set(-10,-10,0);
        carCamera.zoom = 0.5f;


        //load a new world with clears spatials and texturemap
        world = new World(new Vector2(0, -10), true);
        mSpatials = new Array<SimpleSpatial>();
        mTextureMap = new HashMap<String, Texture>();

        // new Menu
        final Menu menu = new Menu(game, this, menuName);

		/*
         * LEFTMENU CAR SELECTION LIST
		 */

        final TextButtonStyle unselectedstyle = new TextButtonStyle(newTDR("menubutton"), newTDR("menubuttonselected"), newTDR("menubuttonselected"), game.font);


        final ButtonGroup selectCarButtons = new ButtonGroup();
        final ButtonGroup carButtons = new ButtonGroup();


        carButtons.setMinCheckCount(-1);
        selectCarButtons.setMinCheckCount(-1);


        for (final Car car : dealer.getCarList()) {
            TextButton leftListButton;
            String name = car.toString();
            //reduce long car name
            if (name.length() > 20) {
                name = name.substring(0, 20) + "...";
            }

            leftListButton = new TextButton(name, unselectedstyle);
            carButtons.add(leftListButton);
            leftListButton.setName(car.toString());
            leftListButton.pad(10);

            leftListButton.addListener(new ClickListener() {

                @Override
                public void clicked(InputEvent event, float f, float v) {
                    SoundManager.buttonClicked(SoundManager.click2, game.sounds);
		    		
					/*
					 * CAR AND MIDDLE MENU UPDATING
					 */
                    //clear box2d stuff and add new car

                    world = new World(new Vector2(0, -10), true);
                    game.disposeList(mTextureMap.values());
                    mTextureMap.clear();
                    mSpatials.clear();
                    RealRacer.loadCarAndLevelFromJson(world, mSpatials, mTextureMap, car, game.showRoomTrack);
                    carCamera.position.set(car.body.getWorldCenter().x, car.body.getWorldCenter().y, 0);

                    LabelStyle infoText = new LabelStyle(game.font, Color.WHITE);

                    carButtons.setMinCheckCount(1);
                    menu.infoTable.clear();
                    menu.infoTable.add(new Label("Make: " + car.make, infoText)).left();
                    menu.infoTable.row();
                    menu.infoTable.add(new Label("Model: " + car.model, infoText)).left();
                    menu.infoTable.row();
                    menu.infoTable.add(new Label("Year: " + car.year, infoText)).left();
                    menu.infoTable.row();
                    menu.infoTable.add(new Label("\n" + car.description + "\n  ", infoText)).left();
                    menu.infoTable.row();

                    final TextButton buyButton = new TextButton("Buy", unselectedstyle);

                    selectCarButtons.add(buyButton);

                    buyButton.pad(10);
                    menu.infoTable.add(buyButton);
					/*
					 * select car button  added to buttongroup to make radio button with others
					 */
                    buyButton.addListener(new ClickListener() {

                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            System.out.println(" bought car " + car.getFullName());
                            SoundManager.buttonClicked(SoundManager.buy, game.sounds);

                            selectCarButtons.setMinCheckCount(1);
                            buyButton.setText("Purchased");
                            buyButton.setDisabled(true);
                            buyButton.clearListeners();
                            game.player.addCar(car);
                            dealer.removeCar(car);
                            menu.updatePlayerCurrents();
                        }
                    });

                    if (game.player.cars.contains(car)) {
                        buyButton.clearListeners();
                        buyButton.setText("Purchased");
                        buyButton.setDisabled(true);
                        buyButton.setChecked(true);
                    }
					
					/*
					 * END 
					 */


                }

            });

            //add margin

            menu.leftListTable.add().height(screenHeight * .02f);
            menu.leftListTable.row();
            menu.leftListTable.add().width(screenWidth * .02f);
            menu.leftListTable.add(leftListButton).width(150);
            menu.leftListTable.add().width(screenWidth * .02f);
            //create new row with margin
            menu.leftListTable.row();

        }
		/*
		 * END LEFTMENU CAR SELECTION LIST
		 */
        stage.addActor(menu.mainTable);

    }

    public TextureRegionDrawable newTDR(String textureName) {
        return new TextureRegionDrawable(new TextureRegion(game.textures.get(textureName)));
    }

    @Override
    public void render(float delta) {
        // TODO Auto-generated method stub
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        camera.update();


        game.batch.setProjectionMatrix(camera.combined);


        game.batch.begin();

        game.batch.draw(game.textures.get("background"), 0, 0, screenWidth, screenHeight);
	
		/*
		game.batch.draw(textures.get("homebtn"),screenWidth*.8f,screenHeight*.80f);
		game.batch.draw(textures.get("racebtn"),screenWidth*.8f,screenHeight*.62f);
		game.batch.draw(textures.get("garagebtn"),screenWidth*.8f,screenHeight*.44f);
		game.batch.draw(textures.get("dealerbtn"),screenWidth*.8f,screenHeight*.26f);
		game.batch.draw(textures.get("settingsbtn"),screenWidth*.8f,screenHeight*.08f);
		*/
        //game.font.draw(game.batch, "Real Racer ", 100, 150);
        //game.font.draw(game.batch, "Tap anywhere to begin!", 100, 100);
        game.batch.end();


        stage.draw();
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

    }

}
