package com.mediocrefireworks.realracer;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.gushikustudios.rube.RubeScene;
import com.gushikustudios.rube.loader.RubeSceneLoader;
import com.gushikustudios.rube.loader.serializers.utils.RubeImage;
import com.mediocrefireworks.realracer.cars.Car;
import com.mediocrefireworks.realracer.cars.Car.Make;
import com.mediocrefireworks.realracer.menu.DealerMenuScreen;
import com.mediocrefireworks.realracer.menu.GarageMenuScreen;
import com.mediocrefireworks.realracer.menu.MainMenuScreen;
import com.mediocrefireworks.realracer.menu.RaceMenuScreen;
import com.mediocrefireworks.realracer.menu.SettingsMenuScreen;
import com.mediocrefireworks.realracer.menu.TuneMenuScreen;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class RealRacer extends Game {
    static boolean worldFunctional = true;
    public final float BOX_STEP = 1 / 60f;
    public final int BOX_VELOCITY_ITERATIONS = 6;
    public final int BOX_POSITION_ITERATIONS = 2;
    //written in bibucket test
    public SpriteBatch batch;
    public BitmapFont font;
    public Color greenColor = Color.valueOf("18CF00");
    public Color orangeColor = Color.valueOf("ED570C");
    public Map<String, Texture> textures;
    public Map<String, Sound> sounds;
    public Player player;
    public Track selectedTrack;
    public float gapMarginFactor = 0.02f;
    public Car selectedCar;
    public World world;
    public Track showRoomTrack;
    Json json;
    Preferences prefs;
    private int screenWidth;
    private int screenHeight;
    private Dealer dealer;
    private ArrayList<Track> trackList;

    /**
     * creates Spatials From Rube Images and
     * loads a level json file and a car to the given world
     * if spatials and textureMap are null then you will need
     * to use the returned obejct array and assign then to the
     * variables in the calling class
     *
     * @param world
     * @param spatials
     * @param textureMap
     * @param car        param levelPath
     * @return returns an array r[0] is spatials r[1] is textureMap
     */
    public static Object[] loadCarAndLevelFromJson(World world, Array<SimpleSpatial> spatials, Map<String, Texture> textureMap, Car car, Track track) {


        if (car == null) {
            System.out.println(" tried to make car that isnt there ");
            return null;
        }

        RubeSceneLoader loader = new RubeSceneLoader(world);
        System.out.println(" about to add sceene to loader and crash: " + track.getDir());
        loader.addScene(Gdx.files.internal(track.getDir()));

        RubeScene scene = loader.addScene(Gdx.files.internal(car.jsonPath));
        System.out.println("here");
        car.setUpBox2dCar(scene);
        track.setUpbox2dTrack(scene);


        if (spatials == null) {
            spatials = new Array<SimpleSpatial>();
        }

        if (textureMap == null) {
            textureMap = new HashMap<String, Texture>();
        }


        Vector2 mTmp = new Vector2(); // shared by all objects

        Array<RubeImage> images = new Array<RubeImage>();


        images.addAll(scene.getImages());

        //removing ground image and adding again so that it will be rendered in front of the car
        //  images.removeValue(scene.getNamed(RubeImage.class, "image0").first(), true);
        //    images.add(scene.getNamed(RubeImage.class, "image0").first());


        if ((images != null) && (images.size > 0)) {

            for (int i = 0; i < images.size; i++) {
                RubeImage image = images.get(i);

                mTmp.set(image.width, image.height);
                String textureFileName = image.file;


                Texture texture = textureMap.get(textureFileName);
                if (texture == null) {
                    texture = new Texture(textureFileName);
                    //smoothout textures!!
                    texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
                    textureMap.put(textureFileName, texture);
                }
                SimpleSpatial spatial = new SimpleSpatial(texture, image.flip, image.body, image.color, mTmp, image.center,
                        image.angleInRads * MathUtils.radiansToDegrees);
                spatials.add(spatial);
            }
        }


        scene.clear();


        Object[] r = new Object[2];
        r[0] = spatials;
        r[1] = textureMap;

        System.out.println(" loaded car and level from json");
        return r;


    }

    /**
     * Renders the given spatials in the given camera
     * sets its own projection matrix to camera
     *
     * @param mSpatials
     * @param camera
     * @param game
     */
    public static void realRacerBox2DDraw(Array<SimpleSpatial> mSpatials, OrthographicCamera camera, RealRacer game) {

        if ((mSpatials != null) && (mSpatials.size > 0)) {
            game.batch.setProjectionMatrix(camera.combined);

            for (int i = 0; i < mSpatials.size; i++) {

                mSpatials.get(i).render(game.batch);
            }


        }


    }

    @Override
    public void create() {


        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();

        //turn off for gl20
        //Texture.setEnforcePotImages(false);
        textures = new HashMap<String, Texture>();

        textures.put("title", new Texture(Gdx.files.internal("data/img/menu/menuvroomtitle.png")));
        textures.put("background", new Texture(Gdx.files.internal("data/img/menu/menubackground.png")));
        textures.put("homebtn", new Texture(Gdx.files.internal("data/img/menu/menuhome.png")));
        textures.put("homebtnselected", new Texture(Gdx.files.internal("data/img/menu/menuhomeselect.png")));
        textures.put("racebtn", new Texture(Gdx.files.internal("data/img/menu/menurace.png")));
        textures.put("racebtnselected", new Texture(Gdx.files.internal("data/img/menu/menuraceselect.png")));
        textures.put("garagebtn", new Texture(Gdx.files.internal("data/img/menu/menugarage.png")));
        textures.put("garagebtnselected", new Texture(Gdx.files.internal("data/img/menu/menugarageselect.png")));
        textures.put("dealerbtn", new Texture(Gdx.files.internal("data/img/menu/menudealer.png")));
        textures.put("dealerbtnselected", new Texture(Gdx.files.internal("data/img/menu/menudealerselect.png")));
        textures.put("settingsbtn", new Texture(Gdx.files.internal("data/img/menu/menusettings.png")));
        textures.put("settingsbtnselected", new Texture(Gdx.files.internal("data/img/menu/menusettingsselect.png")));
        textures.put("menubackground", new Texture(Gdx.files.internal("data/img/menu/menulistbackground.png")));
        textures.put("menubuttonselected", new Texture(Gdx.files.internal("data/img/menu/menulistbuttonbackgroundselected.png")));
        textures.put("menubutton", new Texture(Gdx.files.internal("data/img/menu/menulistbuttonbackground.png")));
        textures.put("menuslider", new Texture(Gdx.files.internal("data/img/menu/menuslider.png")));

        textures.put("count1", new Texture(Gdx.files.internal("data/img/countDown/1.png")));
        textures.put("count2", new Texture(Gdx.files.internal("data/img/countDown/2.png")));
        textures.put("count3", new Texture(Gdx.files.internal("data/img/countDown/3.png")));
        textures.put("countgo", new Texture(Gdx.files.internal("data/img/countDown/go.png")));

        textures.put("tree", new Texture(Gdx.files.internal("data/img/leveltest/tree.png")));


        //load sounds
        sounds = new HashMap<String, Sound>();

        sounds.put("click1", Gdx.audio.newSound(Gdx.files.internal("data/sounds/click.ogg")));
        sounds.put("click2", Gdx.audio.newSound(Gdx.files.internal("data/sounds/click2.ogg")));
        sounds.put("buy", Gdx.audio.newSound(Gdx.files.internal("data/sounds/buy.wav")));


        sounds.put("MainMenu", Gdx.audio.newSound(Gdx.files.internal("data/sounds/click.ogg")));
        sounds.put("RaceMenu", Gdx.audio.newSound(Gdx.files.internal("data/sounds/race.ogg")));
        sounds.put("GarageMenu", Gdx.audio.newSound(Gdx.files.internal("data/sounds/garage.wav")));
        sounds.put("DealerMenu", Gdx.audio.newSound(Gdx.files.internal("data/sounds/click.ogg")));
        sounds.put("SettingsMenu", Gdx.audio.newSound(Gdx.files.internal("data/sounds/click.ogg")));


        batch = new SpriteBatch();
        // Use LibGDX's default Arial font.
        //font = new BitmapFont(Gdx.files.internal("data/Calibri.fnt"),Gdx.files.internal("data/Calibri.png"),false);
        font = new BitmapFont();

        //loadup json
        json = new Json();


        //loading player
        prefs = Gdx.app.getPreferences("RealRacerPreferencesv1");
        String playerJson = prefs.getString("player", "no player");
        //no saved player so create new one
        if (playerJson.equals("no player")) {
            System.out.println("player created ");
            player = new Player("player");
        } else {
            //have a player so load up player

            player = json.fromJson(Player.class, playerJson);

        }


        //	player.addCar(new Car(1,"Subaru","Legacy",2014,"AWD consumer rally car","data/json/subarulegacy14.json"));
        player.addCar(new Car(Make.Madza, "323", 1986, "FWD", "data/json/cars/jeepcheroke12.json", this));
        player.selectCar(player.cars.getFirst());

        //create a new Dealer
        dealer = new Dealer();
        //	dealer.addCar(new Car(Make.Madza, "3233", 1986,"FWD","data/json/cars/mazda32386.json",this));
        dealer.addCar(new Car(Make.Jepe, "Cheroke", 2012, "FWD", "data/json/cars/jeepcheroke12.json", this));
        dealer.addCar(new Car(Make.Homda, "Accord", 1997, "FWD", "data/json/cars/hondaaccord97.json", this));

        showRoomTrack = new Track("rat", "data/json/flatLevel.json");
        trackList = new ArrayList<Track>();


        trackList.add(new Track("hill and drop", "data/json/hildrop.json"));
        trackList.add(new Track("leveltest3", "data/json/leveltest3.json"));
        trackList.add(new Track("leveltest4", "data/json/leveltest4.json"));
        //trackList.add(new Track("jumplevel","data/json/jumplevel.json"));
        selectedTrack = trackList.get(0);
        //	this.setScreen(new MainMenuScreen(this));
        changeScreen(Screens.MainMenu, new GarageMenuScreen(this));
    }

    @Override
    public void render() {
        super.render(); // important!j
    }

    @Override
    public void dispose() {
        //add texture dispsoe loop
        batch.dispose();
        font.dispose();
    }

    public void changeScreen(Screens menuName, Screen screen) {
        System.out.println(" btn clicked " + menuName);
        switch (menuName) {
            case DealerMenu:
                setScreen(new DealerMenuScreen(this));
                break;
            case GarageMenu:
                setScreen(new GarageMenuScreen(this));
                break;
            case MainMenu:
                setScreen(new MainMenuScreen(this));
                break;
            case RaceMenu:

                setScreen(new RaceMenuScreen(this));

                break;
            case SettingsMenu:
                setScreen(new SettingsMenuScreen(this));
                break;
            case TuneMenu:
                setScreen(new TuneMenuScreen(this));
                break;
            case GameScreen:
                setScreen(new GameScreen(this));
            default:
                break;

        }


        screen.dispose();

    }

    public Dealer getDealer() {
        return dealer;
    }

    public void savePlayer() {
        String playerJson = json.toJson(player, Player.class);
        prefs.putString("player", playerJson);
        prefs.flush();
    }

    public TextureRegionDrawable newTDR(String textureName) {
        return new TextureRegionDrawable(new TextureRegion(textures.get(textureName)));
    }

    public float getScreenWidth() {

        return Gdx.graphics.getWidth();

    }
    /* in box2d renderer
    public static void drawGround(Mesh mesh,OrthographicCamera camera,Texture texture){

		camera.update();
		camera.apply(Gdx.gl10);

		Gdx.graphics.getGL20().glEnable(GL20.GL_TEXTURE_2D);
		texture.bind();
		mesh.render(GL20.GL_TRIANGLE_STRIP, 0, mesh.getNumIndices());

	}*/

    public float getScreenHeight() {
        System.out.println(" screenwidth     fdsf sdf sd" + screenWidth);
        //return Gdx.graphics.getHeight();
        return screenHeight;
    }

    public float getScreenWidth(float finalWidth) {
        System.out.println(" screen width " + screenWidth + " game width " + screenWidth / (screenWidth / 20));
        //is always finalWidth
        //return   screenWidth/(screenWidth/finalWidth);
        return finalWidth;
    }

    public float getScreenHeight(float finalWidth) {

        System.out.println(" screen height " + screenHeight + "  game heigh" + screenHeight / (screenWidth / 20));
        //will be 12 if 800x480 otherwise will be aprox 12 but correct aspect ratio
        return screenHeight / (screenWidth / finalWidth);

    }

    /**
     * iterates the collection and disposes each item
     *
     * @param texturesIterable
     */
    public void disposeList(Collection<Texture> texturesIterable) {
        for (Texture t : texturesIterable) {
            t.dispose();
        }
    }

    public ArrayList<Track> getTrackList() {
        // TODO Auto-generated method stub
        return trackList;
    }

    public enum Screens {
        MainMenu, RaceMenu, GarageMenu, DealerMenu, SettingsMenu, TuneMenu, GameScreen
    }


}
