package com.mediocrefireworks.realracer.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
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
import com.mediocrefireworks.realracer.upgrademenu.IUpgradeMenu;
import com.mediocrefireworks.realracer.upgradeparts.UpgradePart;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


public class TuneMenuScreen implements Screen {

    final RealRacer game;
    OrthographicCamera menuCamera;
    Screens menuName = Screens.TuneMenu;
    //rube  box2d stuff
    World world;
    Array<SimpleSpatial> mSpatials;
    Map<String, Texture> mTextureMap;
    OrthographicCamera carCamera;
    TextButton selectedButton;
    private float screenWidth;
    private float screenHeight;
    private Stage stage;
    private int FINALWIDTH = 10;

    private Car car;


    public TuneMenuScreen(final RealRacer game) {

        this.game = game;


        screenWidth = game.getScreenWidth();
        screenHeight = game.getScreenHeight();

        stage = new Stage();

        Gdx.input.setInputProcessor(stage);
        Gdx.graphics.setVSync(false);
        //GLTexture.setEnforcePotImages(false);


        menuCamera = new OrthographicCamera();
        menuCamera.setToOrtho(false, screenWidth, screenHeight);

        carCamera = new OrthographicCamera(1, screenHeight / screenWidth);
        carCamera.setToOrtho(false, game.getScreenWidth(FINALWIDTH), game.getScreenHeight(FINALWIDTH));

        //	carCamera.position.set(-10,-10,0);
        carCamera.zoom = 0.6f;


		/* how to do labels
        LabelStyle lStyle = new LabelStyle();
	    lStyle.font = game.font;
	    new Label("rat",lStyle)
	    	*/

        // Add widgets to the table here.


        // new Menu
        final Menu menu = new Menu(game, this, menuName);
        stage.addActor(menu.mainTable);


        //load up car to tune
        //clear box2d stuff and add new car
        car = game.selectedCar;
        System.out.println(car);

        mSpatials = new Array<SimpleSpatial>();
        mTextureMap = new HashMap<String, Texture>();
        world = new World(new Vector2(0, -10), true);
        game.world = world;
        dispose(mTextureMap.values());
        mTextureMap.clear();
        mSpatials.clear();
        Object[] o = RealRacer.loadCarAndLevelFromJson(world, mSpatials, mTextureMap, car, game.showRoomTrack);
        carCamera.position.set(car.body.getWorldCenter().x, car.body.getWorldCenter().y, 0);
        //TODO perhaps Make a class containing both
        mSpatials = (Array<SimpleSpatial>) o[0];
        mTextureMap = (Map<String, Texture>) o[1];

        System.out.println(" m" + mSpatials.size);
	    
		
		/*
		 * LEFTMENU CAR SELECTION LIST
		 */

        final TextButtonStyle unselectedstyle = new TextButtonStyle(newTDR("menubutton"), newTDR("menubuttonselected"), newTDR("menubuttonselected"), game.font);


        final ButtonGroup selectCarButtons = new ButtonGroup();
        final ButtonGroup carButtons = new ButtonGroup();

		/* why is this here???
		if(currentCar==null){
			carButtons.setMinCheckCount(-1);
			selectCarButtons.setMinCheckCount(-1);
		}
		else{*/
        carButtons.setMinCheckCount(-1);
        selectCarButtons.setMinCheckCount(-1);
		/*}*/

        for (final UpgradePart.Type upgradeType : UpgradePart.Type.values()) {


            TextButton bs;
            String name = upgradeType.toString();
            //reduce long car name
            if (name.length() > 20) {
                name = name.substring(0, 20) + "...";
            }

            bs = new TextButton(name, unselectedstyle);
            carButtons.add(bs);
            bs.setName(name);
            bs.pad(10);

            bs.addListener(new ClickListener() {

                private Table upgradeItemsTable;

                @Override
                public void clicked(InputEvent event, float f, float v) {
                    SoundManager.buttonClicked(upgradeType, game.sounds);
					
					/*
					 * CAR AND MIDDLE MENU UPDATING
					 */


                    carButtons.setMinCheckCount(1);
                    //clear the infomenu for the new menu
                    menu.infoTable.clear();


                    TuneMenuUpgradeParts sTuneMenuUpgrade = new TuneMenuUpgradeParts(game, game.selectedCar);
                    IUpgradeMenu sUpgradeMenu;


                    sUpgradeMenu = upgradeType.getUpgradeMenu(game, game.selectedCar, sTuneMenuUpgrade, menu);


                    upgradeItemsTable = sTuneMenuUpgrade.getTable(sUpgradeMenu);

                    menu.infoTable.add(upgradeItemsTable);


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


        //set debug
		/*
		mainTable.debug();
		rightBtnsTable.debug();
		//leftListTable.debug();
		leftTable.debug();
		rightTable.debug();
		scrollPaneTable.debug();
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

        carCamera.position.set(car.body.getWorldCenter().x, car.body.getWorldCenter().y, 0);


        world.step(game.BOX_STEP, game.BOX_VELOCITY_ITERATIONS, game.BOX_POSITION_ITERATIONS);

        game.batch.setProjectionMatrix(menuCamera.combined);

        game.batch.begin();
        game.batch.draw(game.textures.get("background"), 0, 0, screenWidth, screenHeight);


        //car render
        RealRacer.realRacerBox2DDraw(mSpatials, carCamera, game);
        //stop changing this when you mean the other static method


        game.batch.setProjectionMatrix(menuCamera.combined);

        //String playerCurrents = "Current Vehicle: "+game.player.getCurrentCarName()+"       cr:"+game.player.balance;
        //game.font.draw(game.batch, playerCurrents, screenWidth*.4f, screenHeight*.98f);
        game.batch.end();
        //System.out.println(" isdraggin" +scrollPane +"  "+ scrollPane.isTouchable() );

        stage.draw();
        stage.act();

        //Table.drawDebug(stage);
        if (Gdx.input.isTouched()) {
            //	game.setScreen(new GameScreen(game));
            //	dispose();
        }
    }

    @Override
    public void resize(int width, int height) {


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

        dispose(mTextureMap.values());
        world.dispose();
        world = null;

    }

    /**
     * iterates the collection and disposes each item
     *
     * @param texturesIterable
     */
    public void dispose(Collection<Texture> texturesIterable) {
        for (Texture t : texturesIterable) {
            t.dispose();
        }
    }

}
