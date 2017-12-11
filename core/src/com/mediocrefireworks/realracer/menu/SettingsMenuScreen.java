package com.mediocrefireworks.realracer.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.mediocrefireworks.realracer.GameScreen;
import com.mediocrefireworks.realracer.RealRacer;
import com.mediocrefireworks.realracer.RealRacer.Screens;

public class SettingsMenuScreen implements Screen {

    final RealRacer game;
    OrthographicCamera camera;
    Screens menuName = Screens.SettingsMenu;
    private float screenWidth;
    private float screenHeight;
    private Stage stage;

    public SettingsMenuScreen(final RealRacer game) {

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
        stage.addActor(menu.mainTable);


    }

    public void changeScreen(Screens menuName) {
        System.out.println(" btn clicked " + menuName);
        if (menuName == Screens.RaceMenu) {
            game.setScreen(new GameScreen(game));
        }
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
