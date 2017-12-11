package com.mediocrefireworks.realracer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Shape.Type;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.gushikustudios.rube.RubeScene;
import com.gushikustudios.rube.loader.RubeSceneLoader;
import com.gushikustudios.rube.loader.serializers.utils.RubeImage;
import com.mediocrefireworks.realracer.cars.Car;

import java.util.HashMap;
import java.util.Map;

public class Box2DRenderer {


    World world;
    Array<SimpleSpatial> spatials;
    Map<String, Texture> textureMap;
    Car car;
    Track track;
    Mesh groundMesh, surfaceMesh;
    Texture grdTexture, surTexture;
    Layers layers;
    private ShaderProgram shader;

    /**
     * THIS class is to render car,ground,surface and also all backGround layers
     *
     * @param world
     * @param car
     * @param track
     */


    public Box2DRenderer(World world, OrthographicCamera camera, Car car, Track track) {


        shader = new ShaderProgram(Gdx.files.internal("shader/vertex.glsl").readString(),
                Gdx.files.internal("shader/fragment.glsl").readString());


        this.world = world;
        this.spatials = new Array<SimpleSpatial>();
        textureMap = new HashMap<String, Texture>();
        this.car = car;
        this.track = track;
        layers = new Layers(camera);
        loadLevel(camera);
    }

    public void addLayer(OrthographicCamera camera, String[] texPath, float distance) {
        layers.addLayer(texPath, distance);
    }


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
     * @param car
     * @param levelPath
     * @return returns an array r[0] is spatials r[1] is textureMap
     */
    private void loadLevel(OrthographicCamera camera) {

        if (car == null) {
            System.out.println(" tried to make car that isnt there ");
        }


        RubeSceneLoader loader = new RubeSceneLoader(world);

        loader.addScene(Gdx.files.internal(track.getDir()));
        RubeScene scene = loader.addScene(Gdx.files.internal(car.jsonPath));


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
        createMesh(scene, camera);

        scene.clear();
    }


    /**
     * creates a mesh each for ground and surface for the vertcies from a Fixture named "groundFixture"
     *
     * @param scene
     * @param camera
     */
    private void createMesh(RubeScene scene, OrthographicCamera camera) {
        Fixture fixture = scene.getNamed(Fixture.class, "groundFixture").first();


        Array<Vector2> vertices = new Array<Vector2>();

        if (fixture.getShape().getType() == Type.Chain) {
            ChainShape chain = (ChainShape) fixture.getShape();

            int count = chain.getVertexCount();

            for (int i = 0; i < count; i++) {
                Vector2 temp = new Vector2();
                vertices.add(temp);
                chain.getVertex(count - i - 1, vertices.get(i));
            }

            float yMax = 0;
            // to find the least value of vertices.y
            for (int i = 0; i < vertices.size; i++) {
                if (vertices.get(i).y > yMax) yMax = vertices.get(i).y;
            }


            if (groundMesh == null) {

                float[] temp = new float[vertices.size * 2 * 5];


                int j = 0;
                for (int i = 0; i < vertices.size * 2 * 5; i = i + 10) {
                    temp[i] = vertices.get(j).x;
                    temp[i + 1] = vertices.get(j).y - 10;
                    temp[i + 2] = 0;
                    temp[i + 3] = 1 * (temp[i] - temp[0]) / 10;
                    temp[i + 4] = 1 * (yMax - temp[i + 1]) / 10;
                    temp[i + 5] = vertices.get(j).x;
                    temp[i + 6] = vertices.get(j).y;
                    temp[i + 7] = 0;
                    temp[i + 8] = temp[i + 3];
                    temp[i + 9] = 1 * (yMax - temp[i + 6]) / 10;
                    j++;
                }

                // creating a mesh with maxVertices set to vertices,size*3
                groundMesh = new Mesh(true, vertices.size * 3, vertices.size * 3,
                        new VertexAttribute(Usage.Position, 3, ShaderProgram.POSITION_ATTRIBUTE),
                        new VertexAttribute(Usage.TextureCoordinates, 2, ShaderProgram.TEXCOORD_ATTRIBUTE));


                groundMesh.setVertices(temp);

                short[] indices = new short[vertices.size * 2];
                for (int i = 0; i < vertices.size * 2; i++) {
                    indices[i] = (short) i;
                }
                groundMesh.setIndices(indices);
            }

            if (surfaceMesh == null) {
                // creating a mesh with maxVertices set to vertices,size*3
                surfaceMesh = new Mesh(true, vertices.size * 3, vertices.size * 3,
                        new VertexAttribute(Usage.Position, 3, ShaderProgram.POSITION_ATTRIBUTE),
                        new VertexAttribute(Usage.TextureCoordinates, 2, ShaderProgram.TEXCOORD_ATTRIBUTE));


                float temp[] = new float[vertices.size * 2 * 5];

                float width = 0.25f;

                int j = 0;
                for (int i = 0; i < vertices.size * 2 * 5; i = i + 10) {

                    temp[i] = vertices.get(j).x;
                    temp[i + 1] = vertices.get(j).y - width;
                    temp[i + 2] = 0;
                    temp[i + 3] = 3 * (temp[i] - temp[0]) / 10;
                    temp[i + 4] = 1;
                    temp[i + 5] = temp[i];
                    temp[i + 6] = vertices.get(j).y + width - width / 2;
                    temp[i + 7] = 0;
                    temp[i + 8] = temp[i + 3];
                    temp[i + 9] = 0;
                    j++;

                }

                surfaceMesh.setVertices(temp);

                short indices[] = new short[vertices.size * 2];

                for (int i = 0; i < vertices.size * 2; i++) {
                    indices[i] = (short) i;
                }

                surfaceMesh.setIndices(indices);

            }


            grdTexture = new Texture(Gdx.files.internal("data/img/leveltest/ground.png"));

            grdTexture.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
            grdTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
            shader.setUniformi("u_texture", 0);

            surTexture = new Texture(Gdx.files.internal("data/img/leveltest/surface.png"));

            surTexture.setWrap(TextureWrap.Repeat, TextureWrap.ClampToEdge);
            //TODO change these filters for better quality
            surTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
            shader.setUniformi("u_texture", 0);
        }


    }


    /**
     * Renders the given spatials in the given camera
     * sets its own projection matrix to camera
     *
     * @param mSpatials
     * @param camera
     * @param game
     */
    public void drawWorld(SpriteBatch batch, OrthographicCamera camera) {


        batch.begin();

        batch.setProjectionMatrix(camera.combined);

        layers.drawLayers(batch);


        if ((spatials != null) && (spatials.size > 0)) {
            for (int i = 0; i < spatials.size; i++) {
                spatials.get(i).render(batch);
            }
        }

        batch.end();
        drawGround(camera);

    }


    private void drawGround(OrthographicCamera camera) {


        Gdx.gl20.glEnable(GL20.GL_TEXTURE_2D);
        Gdx.gl20.glEnable(GL20.GL_BLEND);
        Gdx.gl20.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shader.begin();
        shader.setUniformMatrix("u_projTrans", camera.combined);

        grdTexture.bind(0);
        shader.setUniformi("u_texture", 0);
        groundMesh.render(shader, GL20.GL_TRIANGLE_STRIP);

        surTexture.bind(0);
        shader.setUniformi("u_texture", 0);
        surfaceMesh.render(shader, GL20.GL_TRIANGLE_STRIP);


        shader.end();


    }

    public void dispose() {
        System.out.println("disposed");


    }


}










