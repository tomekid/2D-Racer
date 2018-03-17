package com.mediocrefireworks.realracer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.mediocrefireworks.realracer.RealRacer.Screens;
import com.mediocrefireworks.realracer.cars.Car;
import com.mediocrefireworks.realracer.tweens.ActorAnimator;

import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;


import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenEquations;
import aurelienribon.tweenengine.TweenManager;


public class GameScreen implements InputProcessor, Screen {

    public static State state;
    final RealRacer game;
    //gameplay fields
    World world;
    Box2DRenderer worldRenderer;
    Array<Body> bodies = new Array<Body>();
    TweenManager tweenManager;
    Car car;
    Track track;
    float accelPedal;

    //rube loader fields
    float brakePedal;
    float accumulator;
    int frameCount = 1;
    int lagCounter = 1;

    //Sprite count1,count2,count3,countgo;
    float averageSpeed = 0;
    float totalSpeeds = 0;
    LinkedList<Float> avSpeedQueue = new LinkedList<Float>();
    LinkedList<Float> zoomLagQueue = new LinkedList<Float>();
    float ZOOMBUFFER = 200;
    Gl20Test gl20Test = new Gl20Test();
    boolean[] fingerDown = new boolean[5];
    float yPercent = 0;
    float xPercent = 0;
    private Stage stage;
    private Screen fuckyou = this;
    private OrthographicCamera camera;
    private OrthographicCamera hudCamera;
    private BitmapFont font;
    private float screenWidth;
    private float screenHeight;
    private int FINALWIDTH = 20;
    private Box2DDebugRenderer debugRenderer;
    private long startTime;
    private float elapsedTime;

    public GameScreen(final RealRacer game) {
        this.game = game;
        state = State.COUNTDOWN;
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        Gdx.graphics.setVSync(false);

        //changed to GL20 on update
        //  GLTexture.setEnforcePotImages(false);


/*
        Gdx.gl20.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl20.glEnable(GL20.GL_TEXTURE_2D);
        Gdx.gl20.glEnable(GL20.GL_BLEND);
        Gdx.gl20.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
	*/


        startTime = System.nanoTime();
        //loads the inputprocessor implementation methods
        //	Gdx.input.setInputProcessor(this);

        screenWidth = game.getScreenWidth();
        screenHeight = game.getScreenHeight();

        hudCamera = new OrthographicCamera(1, screenHeight / screenWidth);
        hudCamera.setToOrtho(false, screenWidth, screenHeight);

        camera = new OrthographicCamera(1, screenHeight / screenWidth);
        camera.setToOrtho(false, game.getScreenWidth(FINALWIDTH), game.getScreenHeight(FINALWIDTH));
        camera.position.set(0, 0, 0);
        camera.zoom = 0.6f;

        game.batch = new SpriteBatch();

        debugRenderer = new Box2DDebugRenderer();

        font = new BitmapFont(Gdx.files.internal("data/Calibri.fnt"), Gdx.files.internal("data/Calibri.png"), false);
        font.setColor(0, 0, 0, 100);

        tweenManager = new TweenManager();
        Tween.registerAccessor(Actor.class, new ActorAnimator());

        world = new World(new Vector2(0, -10), true);


        //loading all rube stuff
        track = game.selectedTrack;
        car = game.player.getCurrentCar();

        worldRenderer = new Box2DRenderer(world, camera, car, track);

        // this may be consuming too much space in GameScreen but

        String texPath[] = new String[3];

        // i kept all the three textures same for testing  should be changed to three different tilable textures

        texPath[0] = new String("leveltest/background.png");
        texPath[1] = new String("leveltest/background.png");
        texPath[2] = new String("leveltest/background.png");

        worldRenderer.addLayer(camera, texPath, 900);

        // i kept all the three textures same for testing  should be changed to three different tilable textures

        texPath[0] = new String("leveltest/back3.png");
        texPath[1] = new String("leveltest/back3.png");
        texPath[2] = new String("leveltest/back3.png");

        worldRenderer.addLayer(camera, texPath, 700);

        // i kept all the three textures same for testing  should be changed to three different tilable textures

        texPath[0] = new String("leveltest/back2.png");
        texPath[1] = new String("leveltest/back2.png");
        texPath[2] = new String("leveltest/back2.png");

        worldRenderer.addLayer(camera, texPath, 500);

        // i kept all the three textures same for testing  should be changed to three different tilable textures

        texPath[0] = new String("leveltest/back1.png");
        texPath[1] = new String("leveltest/back1.png");
        texPath[2] = new String("leveltest/back1.png");

        worldRenderer.addLayer(camera, texPath, 300);


        car.body.setTransform(0, 10, 0);

        world.setContactListener(new Box2DWorld(this));

        //*load queue with starting values
        for (int x = 0; x < ZOOMBUFFER; x++) {
            avSpeedQueue.add(0f);
            zoomLagQueue.add(0.3f);

        }

        Table hudTable = new Table();

        final TextButtonStyle unselectedstyle = new TextButtonStyle(game.newTDR("menubutton"), game.newTDR("menubuttonselected"), game.newTDR("menubuttonselected"), game.font);
        final TextButton button = new TextButton("x", unselectedstyle);
        button.setName("x");
        button.pad(20);

        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float f, float v) {

                state = State.GOTOHOME;
            }
        });
        hudTable.setFillParent(true);
        hudTable.add().expandX();
        hudTable.add(button);
        hudTable.row();
        hudTable.add().expandY();
        stage.addActor(hudTable);

        startCountDown();


        state = State.RUNNING;

    }

    private void startCountDown() {

        Image count1 = new Image(game.textures.get("count1"));
        Image count2 = new Image(game.textures.get("count2"));
        Image count3 = new Image(game.textures.get("count3"));
        Image countgo = new Image(game.textures.get("countgo"));

        count1.setPosition(screenWidth, screenHeight / 2);
        count2.setPosition(screenWidth, screenHeight / 2);
        count3.setPosition(screenWidth, screenHeight / 2);
        countgo.setPosition(screenWidth, screenHeight / 2);

        count1.setColor(1, 1, 1, 0);
        count2.setColor(1, 1, 1, 0);
        count3.setColor(1, 1, 1, 0);
        countgo.setColor(1, 1, 1, 0);

        stage.addActor(count1);
        stage.addActor(count2);
        stage.addActor(count3);
        stage.addActor(countgo);


        Timeline.createParallel()
                .push(Tween.to(count3, ActorAnimator.XandALPHA, 0.5f).target(screenWidth / 2 - count3.getWidth() / 2, 1).ease(TweenEquations.easeOutExpo))
                .push(Tween.to(count3, ActorAnimator.XandALPHA, 0.5f).target(-count3.getWidth(), 0).ease(TweenEquations.easeOutExpo).delay(1))
                .push(Tween.to(count2, ActorAnimator.XandALPHA, 0.5f).target(screenWidth / 2 - count2.getWidth() / 2, 1).ease(TweenEquations.easeOutExpo).delay(1))
                .push(Tween.to(count2, ActorAnimator.XandALPHA, 0.5f).target(-count2.getWidth(), 0).ease(TweenEquations.easeOutExpo).delay(2))
                .push(Tween.to(count1, ActorAnimator.XandALPHA, 0.5f).target(screenWidth / 2 - count1.getWidth() / 2, 1).ease(TweenEquations.easeOutExpo).delay(2))
                .push(Tween.to(count1, ActorAnimator.XandALPHA, 0.5f).target(-count1.getWidth(), 0).ease(TweenEquations.easeOutExpo).delay(3))
                .push(Tween.to(countgo, ActorAnimator.XandALPHA, 0.5f).target(screenWidth / 2 - countgo.getWidth() / 2, 1).ease(TweenEquations.easeOutExpo).delay(3)
                        .setCallback(new TweenCallback() {
                            @Override
                            public void onEvent(int type, BaseTween<?> source) {
                                state = State.RUNNING;
                            }
                        }))
                .push(Tween.to(countgo, ActorAnimator.SIZEandALPHA, 0.5f).target(7 * countgo.getWidth(), 0).ease(TweenEquations.easeOutExpo).delay(4))
                .start(tweenManager);
    }

    @Override
    public void render(float delta) {


        switch (state) {

            case COUNTDOWN:
                updateCamera();
                display();
                stepWorld(delta);
                drawStage(delta);
                tweenManager.update(delta);

                break;

            case RUNNING:
                updateCamera();
                if (state == State.RUNNING) {
                    elapsedTime = ((System.nanoTime() - startTime) / 1000000000.0f) - 5;
                }
                display();
                updateCar(delta);
                stepWorld(delta);
                drawStage(delta);
                tweenManager.update(delta);

                break;

            case SETTINGPAUSE:
                updateCamera();
                display();
                drawStage(delta);


                state = State.PAUSED;
                break;

            case PAUSED:
                updateCamera();
                display();
                drawStage(delta);

                break;

            case RESUMING:
                updateCamera();
                display();
                drawStage(delta);

                state = State.RUNNING;
                break;

            case FINISHED:
                updateCamera();
                display();
                updateCar(delta);
                stepWorld(delta);
                drawStage(delta);
                tweenManager.update(delta);

                break;

            case GOTOHOME:
                updateCamera();
                display();
                drawStage(delta);
                dispose();
                game.changeScreen(Screens.MainMenu, this);
                break;

            case RESTARTING:
                updateCamera();
                display();
                drawStage(delta);
                dispose();
                game.changeScreen(Screens.GameScreen, this);
                break;

            default:
                System.out.println("Unknown state " + state);
                break;
        }
    }

    private void drawStage(float delta) {
        stage.draw();
        stage.act();
        //debugRenderer.render(world, camera.combined);
    }

    private void stepWorld(float delta) {

        world.step(delta, game.BOX_VELOCITY_ITERATIONS, game.BOX_POSITION_ITERATIONS);
		 /*
		accumulator+= delta/100;
	    	while(accumulator>game.BOX_STEP){
	    	  world.step(game.BOX_STEP,game.BOX_VELOCITY_ITERATIONS,game.BOX_POSITION_ITERATIONS);
	      accumulator-=game.BOX_STEP;

	   } */
    }

    private void updateCamera() {

        //camera zoom
        camera.position.set(car.body.getWorldCenter().x + 0.8f, car.body.getWorldCenter().y - 0.5f, 0);
        totalSpeeds -= avSpeedQueue.poll();
        avSpeedQueue.add(car.getRealWorldSpeed());
        totalSpeeds += car.getRealWorldSpeed();
        float av = ((totalSpeeds * totalSpeeds) / ZOOMBUFFER) / 50000 + 0.8f;

        zoomLagQueue.add(av);
        camera.zoom = zoomLagQueue.poll();


        camera.update();


    }

    private void display() {

        game.batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl20.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClearColor(1, 1, 1, 1);
        // Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | (Gdx.graphics.getBufferFormat().coverageSampling ? GL20.GL_COVERAGE_BUFFER_BIT_NV : 0));

        worldRenderer.drawWorld(game.batch, camera);
        gl20Test.render(camera);

        game.batch.begin();

        DecimalFormat df = new DecimalFormat("#");
        DecimalFormat dfp = new DecimalFormat("#.##");

        //HUD display figures
        game.batch.setProjectionMatrix(hudCamera.combined);


        font.draw(game.batch, "Time:" + elapsedTime, screenWidth * .1f, screenHeight * .91f);
        font.draw(game.batch, "RPM " + df.format(car.getRPM()), screenWidth * .1f, screenHeight * .86f);
        font.draw(game.batch, "speed " + df.format(car.getSpeed() * 3.6) + " km/h" + " real:" + df.format(
                Math.sqrt(car.body.getLinearVelocity().x * car.body.getLinearVelocity().x +
                        car.body.getLinearVelocity().y * car.body.getLinearVelocity().y) * (60 * 60) / 1000)
                , screenWidth * .1f, screenHeight * .81f);
        font.draw(game.batch, "accel pedal " + dfp.format(accelPedal) + " --- currentTorque : " + car.currentTorque, screenWidth * .1f, screenHeight * .76f);
        font.draw(game.batch, "break pedal " + dfp.format(brakePedal), screenWidth * .1f, screenHeight * .71f);
        font.draw(game.batch, "gear " + (car.getGear() - 2), screenWidth * .1f, screenHeight * .65f);

        font.draw(game.batch, "ariTime " + car.airTime, screenWidth * .1f, screenHeight * .56f);

        game.batch.end();


    }

    private void updateCar(float delta) {
        //before count down disable car,TODO need to do an else
        if (elapsedTime < 0) {
            car.engageClutch();
        }
        //after crossed finish line
        if (car.body.getPosition().x > game.selectedTrack.finishLine.getPosition().x && state == State.RUNNING) {
            car.applyHandBrake();
            finish();
        }


        accelPedal = 0f;
        brakePedal = 0f;
        // each i is each finger touching
        for (int i = 0; i < 5; i++) {
            float x = Gdx.input.getX(i);
            float y = screenHeight - Gdx.input.getY(i) - 1;


            if (Gdx.input.isTouched(i)) {
                fingerDown[i] = true;
                //when screen is touched record position percent
                yPercent = y / screenHeight;
                xPercent = x / screenWidth;

                //right side of screen
                if (xPercent > 0.5) {


                    //top 75% of screen
                    if (yPercent > 0.25) {

                        if (yPercent > 0.9) {
                            car.acceleratorDown(1.1f);
                            accelPedal = 1.1f;
                        } else {
                            float speed = (yPercent - 0.25f) * 1.538f + 0.1f;
                            accelPedal = speed;
                            car.acceleratorDown(speed);
                        }
                    }//lower 25%
                    else {
                        //brake
                        if (yPercent < 0.1) {
                            car.engageBrake(1);
                            brakePedal = 1;
                        } else {
                            brakePedal = ((0.25f - yPercent) * 6.6666f);
                            car.engageBrake(brakePedal);
                        }
                    }
                }
                //left half of screen
                else {

                }

            }//end of is touched
            else {//if screen not being touched
			/*
			 * we need to have the on up calls inside this fingerDown check
			 * otherwise it will get called on every loop because it has multitouch
			 */
                if (fingerDown[i]) {
                    //on up
                    fingerDown[i] = false;

                    //release accelerator
                    car.acceleratorDown(0);

                    car.releaseBrake();

                    //if released on the left side of screen change gear
                    if (xPercent < 0.5) {
                        if (yPercent > 0.5) {
                            car.changeGear(car.getGear() + 1);
                        } else {
                            car.changeGear(car.getGear() - 1);
                        }
                    }

                }

            }
        }//end for

        car.runCar(delta);

    }

    private void finish() {
        state = State.FINISHED;

        new Timer().schedule(new TimerTask() {

            @Override
            public void run() {
                loadFinishMenu();
            }
        }, 2000);
    }

    public void loadFinishMenu() {


        Table tabel = new Table();
        tabel.setBackground(game.newTDR("menubackground"));

        tabel.setSize(screenWidth / 2 + 100, screenHeight / 2 + 100);
        tabel.setPosition(screenWidth / 2 - tabel.getWidth() / 2 - 20, screenHeight / 2 - tabel.getHeight() / 2);
        tabel.setColor(1, 1, 1, 0);

        LabelStyle style = new LabelStyle(game.font, Color.WHITE);

        Label raceCompleted = new Label("RaceCompleted", style);
        Label timeTaken = new Label("Time : ", style);
        Label bestTime = new Label("Best Time : ", style);
        Label moneyCollected = new Label("Money Earnt : ", style);

        tabel.add(raceCompleted).padRight(180).padBottom(20);
        tabel.row();
        tabel.add(timeTaken).padRight(120).padBottom(20);
        tabel.row();
        tabel.add(bestTime).padRight(150).padBottom(25);
        tabel.row();
        tabel.add(moneyCollected).padRight(160).padBottom(25);
        tabel.row();

        TextButtonStyle buttonStyle = new TextButtonStyle();
        buttonStyle.up = game.newTDR("menubutton");
        buttonStyle.down = game.newTDR("menubuttonselected");
        buttonStyle.font = game.font;

        TextButton home = new TextButton("Home", buttonStyle);

        home.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float f, float v) {
                state = State.GOTOHOME;
            }
        });


        TextButton restart = new TextButton("Restart", buttonStyle);

        restart.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float f, float v) {
                //game.changeScreen(Screens.GameScreen,fuckyou);
                state = State.RESTARTING;
            }
        });


        home.padRight(15).padLeft(15);
        restart.padRight(15).padLeft(15);

        tabel.add(home).padRight(130).padLeft(30);
        tabel.add(restart).padRight(50).padLeft(30);

        //tabel.debug();
        stage.addActor(tabel);


        //starting animation
        Tween.to(tabel, ActorAnimator.XandALPHA, 0.5f).target(screenWidth / 2 - tabel.getWidth() / 2, 1)
                .start(tweenManager);


    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void show() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void pause() {
        state = State.SETTINGPAUSE;
    }

    @Override
    public void resume() {
        state = State.RESUMING;
    }

    @Override
    public void dispose() {
        worldRenderer.dispose();

    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == 31) car.clutchPressed = true;

        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == 31) car.clutchPressed = false;
        if (keycode == 34) car.changeGear(car.getGear() + 1);
        if (keycode == 50) car.changeGear(car.getGear() - 1);
        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {


        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean scrolled(int amount) {

        camera.zoom += (amount * 0.1f);
        if (camera.zoom < 0.1f) {
            camera.zoom = 0.1f;
        }
        camera.update();


        return true;
    }

    public enum State {
        COUNTDOWN,
        RUNNING,
        SETTINGPAUSE,
        PAUSED,
        RESUMING,
        FINISHED,
        GOTOHOME,
        RESTARTING
    }


}
