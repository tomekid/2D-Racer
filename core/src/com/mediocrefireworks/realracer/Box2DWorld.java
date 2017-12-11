package com.mediocrefireworks.realracer;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.mediocrefireworks.realracer.cars.Car;

public class Box2DWorld implements ContactListener {

    World world;
    GameScreen gameScreen;
    Car car;

    /**
     * this class handles collisions and body userdata
     *
     * @param gameScreen
     */
    public Box2DWorld(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
        world = gameScreen.world;
        this.car = gameScreen.car;
    }

    @Override
    public void beginContact(Contact contact) {
        BodyName bodyA = (BodyName) contact.getFixtureA().getBody().getUserData();
        BodyName bodyB = (BodyName) contact.getFixtureB().getBody().getUserData();

        if ((bodyA == BodyName.frontWheel && bodyB == BodyName.ground) || (bodyA == BodyName.ground && bodyB == BodyName.frontWheel)) {
            car.frontWheel.contactPoints++;
            car.rearWheel.contactPoints++;
        }


    }

    @Override
    public void endContact(Contact contact) {
        BodyName bodyA = (BodyName) contact.getFixtureA().getBody().getUserData();
        BodyName bodyB = (BodyName) contact.getFixtureB().getBody().getUserData();

        if ((bodyA == BodyName.frontWheel && bodyB == BodyName.ground) || (bodyA == BodyName.ground && bodyB == BodyName.frontWheel)) {
            car.frontWheel.contactPoints--;
            car.rearWheel.contactPoints--;
        }

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }

    public enum BodyName {
        frontWheel, rearWheel, carShell, ground, finishSensor
    }


}
