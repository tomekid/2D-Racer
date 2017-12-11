package com.mediocrefireworks.realracer.menu;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.mediocrefireworks.realracer.Dealer;
import com.mediocrefireworks.realracer.RealRacer;
import com.mediocrefireworks.realracer.RealRacer.Screens;
import com.mediocrefireworks.realracer.Track;
import com.mediocrefireworks.realracer.Track.TrackType;
import com.mediocrefireworks.realracer.sounndAndMusic.SoundManager;

import java.util.HashMap;

public class RaceMenuScreen implements Screen {

    final RealRacer game;
    OrthographicCamera camera;
    Screens menuName = Screens.RaceMenu;
    private Dealer dealer;
    private float screenWidth;
    private float screenHeight;
    private Stage stage;
    private int FINALWIDTH = 10;
    private HashMap<TrackType, TextButton> trackButtons;

    public RaceMenuScreen(final RealRacer game) {

        this.game = game;

        screenWidth = game.getScreenWidth();
        screenHeight = game.getScreenHeight();

        stage = new Stage();

        Gdx.input.setInputProcessor(stage);
        //GLTexture.setEnforcePotImages(false);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, screenWidth, screenHeight);


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


        for (final Track track : game.getTrackList()) {
            TextButton leftListButton;
            String name = track.getName();
            //reduce long car name
            if (name.length() > 20) {
                name = name.substring(0, 20) + "...";
            }

            leftListButton = new TextButton(name, unselectedstyle);
            carButtons.add(leftListButton);
            leftListButton.setName(name);
            leftListButton.pad(10);

            leftListButton.addListener(new ClickListener() {

                @Override
                public void clicked(InputEvent event, float f, float v) {
					
					/*
					 * CAR AND MIDDLE MENU UPDATING
					 */
                    //clear box2d stuff and add new car

                    SoundManager.buttonClicked(SoundManager.click2, game.sounds);

                    LabelStyle infoText = new LabelStyle(game.font, Color.WHITE);

                    carButtons.setMinCheckCount(1);
                    menu.infoTable.clear();
                    menu.infoTable.add(new Label("Level " + "level name here", infoText)).left();

                    menu.infoTable.row();

                    menu.infoTable.add(getInfoTable(track));


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


    private Table getInfoTable(final Track track) {
        LabelStyle infoText = new LabelStyle(game.font, Color.WHITE);
        TextButtonStyle buttonStyle = new TextButtonStyle(game.newTDR("menubutton"), game.newTDR("menubuttonselected"), game.newTDR("menubuttonselected"), game.font);


        Table table = new Table();
        Table descriptionTable = new Table();
        //TODO maybe does this line below work ? output correct string
        descriptionTable.add(new Label(track.getDir(), infoText));
		/*
		 * RACE BUTTON
		 */
        final TextButton raceButton = new TextButton("Race", buttonStyle);
        raceButton.pad(10);
        raceButton.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.selectedTrack = track;
                game.changeScreen(Screens.GameScreen, RaceMenuScreen.this);
            }
        });
		/*
		 * END 
		 */

        descriptionTable.add(raceButton);
        table.add(descriptionTable).expandX();
        table.row();
		
		/* add in the tune/upgrade table main window!*/
        //this line should get the currently selected upgrademenu

        final ScrollPane mainUpgradeWindow = new ScrollPane(new Table());
        mainUpgradeWindow.setScrollX(0);
        table.add(mainUpgradeWindow).width(screenWidth * 0.5f).height(screenHeight * .5f);

        table.row();
        Table upgradeItems = new Table();
        ScrollPane upgradeItemScrollPane = new ScrollPane(upgradeItems);
        table.add(upgradeItemScrollPane).width(screenWidth * .5f);

        ButtonGroup bg = new ButtonGroup();
        trackButtons = new HashMap<TrackType, TextButton>();
		/*
		 * for each upgrade version button
		 * like the stock sports racing 
		 * 
		 */
        for (final TrackType trackType : TrackType.values()) {
            TextButton tb;

            tb = new TextButton(trackType + "", buttonStyle);

            trackButtons.put(trackType, tb);
            tb.pad(10);
            bg.add(tb);
            upgradeItems.add().width(10);
            upgradeItems.add(tb);
            upgradeItems.add().width(10);

            tb.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float f, float v) {

                    refreshUpgradeWindow(mainUpgradeWindow, trackType, track);

                }
            });

        }

        return table;

    }


    protected void refreshUpgradeWindow(ScrollPane mainUpgradeWindow, TrackType trackType, Track track) {
        //TODO load level image and description
        //mainUpgradeWindow.setWidget();


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

        game.batch.end();

        stage.draw();
        //Table.drawDebug(stage);
        if (Gdx.input.isTouched()) {

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
