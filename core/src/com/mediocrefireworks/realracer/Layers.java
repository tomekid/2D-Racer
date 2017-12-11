package com.mediocrefireworks.realracer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

public class Layers {

    float preCamX, preCamY;
    Array<Layer> layers;
    OrthographicCamera camera;

    /**
     * creates and handles all backLayers
     *
     * @param camera
     */
    public Layers(OrthographicCamera camera) {
        layers = new Array<Layers.Layer>();
        this.camera = camera;

        preCamX = camera.position.x;
        preCamY = camera.position.y;
    }

    public void addLayer(String[] texPath, float distance) {
        layers.add(new Layer(texPath, distance));
    }

    public void drawLayers(SpriteBatch batch) {

        for (int i = 0; i < layers.size; i++) {
            layers.get(i).draw(batch);
        }

        preCamX = camera.position.x;
        preCamY = camera.position.y;

    }

    public class Layer {

        Sprite sprites[];
        float viewWidth, viewHeight;
        SpriteHolder leftSprite, rightSprite, midSprite;
        float distance;


        public Layer(String[] texPath, float distance) {

            sprites = new Sprite[3];
            for (int i = 0; i < 3; i++) {
                sprites[i] = new Sprite(new Texture(Gdx.files.internal("data/img/" + texPath[i])));
                sprites[i].setSize(camera.viewportWidth, camera.viewportHeight);
            }
            viewWidth = camera.viewportWidth;
            viewHeight = camera.viewportHeight;

            sprites[0].setPosition(camera.position.x - 3 * viewWidth / 2, camera.position.y - viewHeight / 2);
            sprites[1].setPosition(camera.position.x - viewWidth / 2, camera.position.y - viewHeight / 2);
            sprites[2].setPosition(camera.position.x + viewWidth / 2, camera.position.y - viewHeight / 2);

            leftSprite = new SpriteHolder(sprites[0]);
            midSprite = new SpriteHolder(sprites[1]);
            rightSprite = new SpriteHolder(sprites[2]);

            if (distance < 1000) {
                this.distance = distance;
            } else {
                this.distance = 1000;
            }


            //	System.out.println(""+camera.position);

        }


        public void draw(SpriteBatch batch) {

            //	System.out.println(""+camera.position);
            setSprites();

            //	if(preCamX!=0){
            translateSprites();
            //	}


            for (int i = 0; i < 3; i++) {
                sprites[i].draw(batch);
            }


        }

        /**
         * changes the positions of sprites according to the distance and camera Displacement to create a fake parallax
         * effect
         */
        private void translateSprites() {
            float camDisplaceX = camera.position.x - preCamX;
            float camDisplaceY = camera.position.y - preCamY;


            leftSprite.getSprite().translate(camDisplaceX * distance / 1000, camDisplaceY);
            midSprite.getSprite().translate(camDisplaceX * distance / 1000, camDisplaceY);
            rightSprite.getSprite().translate(camDisplaceX * distance / 1000, camDisplaceY);


        }


        /**
         * transforms the leftSprite to rightSprite if camera has crossed it
         *
         * @param camera
         */
        private void setSprites() {
            if (camera.position.x > midSprite.getSprite().getX() + viewWidth / 2) {

                leftSprite.getSprite().setPosition(rightSprite.getX() + rightSprite.getWidht(), leftSprite.getY());

                Sprite temp = leftSprite.getSprite();
                leftSprite.setSprite(midSprite.getSprite());
                midSprite.setSprite(rightSprite.getSprite());
                rightSprite.setSprite(temp);
            } else if (camera.position.x < midSprite.getSprite().getX() + viewWidth / 2) {
                rightSprite.getSprite().setPosition(leftSprite.getX() + leftSprite.getWidht(), rightSprite.getY());

                Sprite temp = rightSprite.getSprite();
                rightSprite.setSprite(midSprite.getSprite());
                midSprite.setSprite(leftSprite.getSprite());
                leftSprite.setSprite(temp);
            }


        }


        private class SpriteHolder {

            Sprite sprite;

            public SpriteHolder(Sprite sprite) {
                this.sprite = sprite;
            }

            public float getX() {
                return getSprite().getX();
            }

            public float getY() {
                return getSprite().getY();
            }

            public float getWidht() {
                return getSprite().getWidth();
            }

            public float getHeight() {
                return getSprite().getHeight();
            }

            public Sprite getSprite() {
                return sprite;
            }

            public void setSprite(Sprite sprite) {
                this.sprite = sprite;
            }


        }


    }

}
