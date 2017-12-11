package com.mediocrefireworks.realracer.cars;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.joints.DistanceJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;

public class Wheel {


    public Body body;
    public Fixture fixture;
    public RevoluteJoint axleJoint;
    public DistanceJoint susp;
    public float defaultRideHeight;
    public int contactPoints;

    /**
     * creates a wheel
     *
     * @param body
     * @param axleJoint
     * @param susp
     */
    Wheel(Body body, RevoluteJoint axleJoint, DistanceJoint susp) {

        this.body = body;

        this.fixture = body.getFixtureList().first();
        this.axleJoint = axleJoint;
        this.susp = susp;
        defaultRideHeight = susp.getLength();
        contactPoints = 0;

    }


    public void setFriction(float f) {
        fixture.setFriction(f);
    }

    public float getRideHeightOffset() {
        return defaultRideHeight - susp.getLength();
    }

    public void setRideHeightOffset(float offset) {
        susp.setLength(defaultRideHeight - offset);
    }

    public float getDampening() {
        return susp.getDampingRatio();
    }

    public void setDampening(float damp) {
        susp.setDampingRatio(damp);
    }

    public float getRigity() {
        return susp.getFrequency();
    }

    public void setRigity(float rig) {
        susp.setFrequency(rig);
    }

}
