package com.mediocrefireworks.realracer.cars;


import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.joints.DistanceJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.gushikustudios.rube.RubeScene;
import com.mediocrefireworks.realracer.Box2DWorld.BodyName;
import com.mediocrefireworks.realracer.IGameObject;
import com.mediocrefireworks.realracer.RealRacer;
import com.mediocrefireworks.realracer.upgrademenu.CarSetting;

import java.util.HashMap;


public class Car implements IGameObject {

    //box2d parts
    public Body body;
    public Wheel frontWheel;
    public Wheel rearWheel;

    public boolean engineReleased = true;
    public boolean clutchPressed = false;
    public float currentTorque;
    public float throttlePercent = 0.1f;
    public String jsonPath;
    public HashMap<CarSetting, Float> carSettings;
    public Make make;
    public String model;
    public String description;
    public int year;
    public float airTime;
    private boolean inReverse = false;
    private boolean brakeReleased = true;
    private boolean handBrakeOn = false;
    private int gear = 2;
    private HashMap<CarSetting, Float> carDefaultSettings = new HashMap<CarSetting, Float>();
    private float[] gears;
    private float[] defaultGears;
    private TorqueCurve tq;
    private RealRacer game;
    private boolean firstCarLoad = true;
    private CarUpgrader carUpgrader;
    private boolean brakeOn;


    /**
     * creates a car but setUpBox2dCar(RubeScene scene)
     * needs to be called before use;
     *
     * @param i
     * @param name
     * @param jsonPath
     */
    public Car(Make make, String model, int year, String description, String jsonPath, RealRacer game) {


        this.jsonPath = jsonPath;
        this.make = make;
        this.model = model;
        this.year = year;
        this.description = description;
        this.game = game;

        int te = 10;
        //original settings TODO put in seperate file or pass in when creating the car like torquecurve
        this.gears = new float[]{4.105f / 6, -3.214f, 0, 3.153f, 1.842f, 1.400f, 1.30f, 1.028f};


        carUpgrader = new CarUpgrader(game, this);
        tq = carUpgrader.getTorqueCurve();
        carSettings = new HashMap<CarSetting, Float>();


    }

    public TorqueCurve getTorqueCurve() {
        return carUpgrader.getTorqueCurve();
    }

    public CarUpgrader getCarUpgrader() {
        return carUpgrader;
    }

    public HashMap<CarSetting, Float> getDefaultSettings() {
        return carDefaultSettings;
    }

    public void resetGears() {
        gears = defaultGears.clone();
    }

    public float[] getGears() {
        return gears;

    }

    public void runCar(float delta) {


        // TODO if rpm is less than 1000 while in gear stutter forward
        float revs = getRPM();

        //engineToAxleGearJoint.getBodyA().setAngularVelocity(revs);

        float ran = (float) Math.random() * 100;

        if (handBrakeOn) {

            rearWheel.axleJoint.setMotorSpeed(0);
            rearWheel.body.setAngularVelocity(0);
            rearWheel.axleJoint.setMaxMotorTorque(10000);
            rearWheel.axleJoint.setLimits(0, 10);

        } else {

            //drive torque = engine_torque * gear_ratio * differential_ratio * transmission_efficiency
            //reducegear by 1/8 (0.125 and increase 8 fold)
            currentTorque = throttlePercent * (tq.getTorque(revs) * gears[gear] * gears[0] * 0.8f) * 20;
            //		System.out.println(" torque"+ currentTorque + " get engine torque "+ tq.getTorque(revs)+ " revs " + revs);
            frontWheel.body.applyTorque(-currentTorque * 2, true);


        }

        //System.out.println(" engine is running : current torque:" + -currentTorque + " throttleperc"  + throttlePercent );
        //wheel.body.applyAngularImpulse(-nm, true);
        //	motor.setMaxMotorTorque(5000);
        //wheel.body.setAngularVelocity((float) (-2f*Math.PI*100f));

        calculateAirTime(delta);
    }

    private void calculateAirTime(float delta) {

        if ((frontWheel.contactPoints == 0) && (rearWheel.contactPoints == 0)) {
            airTime = airTime + delta;
        } else {
            airTime = 0;
        }

    }

    public void engageClutch() {
        clutchPressed = true;

    }

    public void releaseClutch() {
        clutchPressed = false;

    }

    /**
     * returns  wheel joint speed in m/s
     *
     * @return speed
     */
    public float getSpeed() {
        // this needs to use the engine wheel
        //TODO set this value when creating the car so dont need to get all that shit
        float r = frontWheel.fixture.getShape().getRadius();

        float speed = -frontWheel.axleJoint.getJointSpeed() * r;

        //	float speed = -
        //	System.out.println(" radius   " + r + "    speed " + speed);
        return speed;
    }

    /**
     * returns the real world body speed of the car
     * in m/s
     *
     * @return float
     */
    public float getRealWorldSpeed() {
        return (float) Math.sqrt(body.getLinearVelocity().x * body.getLinearVelocity().x +
                body.getLinearVelocity().y * body.getLinearVelocity().y);
    }

    public void acceleratorDown(float throttlePercent) {


        this.throttlePercent = throttlePercent;

    }

    public float getRPM() {

        //	System.out.println(" getrpm " + frontWheelJoint.getJointSpeed() +" gear rat" + gears[gear] + "dif "+ gears[0]);
        float rpm = (float) (-frontWheel.body.getAngularVelocity() * gears[gear] * gears[0] * 30 * Math.PI);

        if (rpm < 1000) rpm = 1000;

        return rpm;

    }

    public boolean isBrakeReleased() {
        return brakeReleased;
    }

    public void engageBrake(float pressure) {
        brakeReleased = false;
        if (!brakeOn) {
            frontWheel.axleJoint.setMotorSpeed(0);

        }
        frontWheel.axleJoint.setMaxMotorTorque(pressure * 4000);

        //	System.out.println(" pressure " + pressure  + " torque " + pressure*4000);
    }

    public void releaseBrake() {
        brakeReleased = true;
        frontWheel.body.setAngularDamping(0);
        // maybe make this if lower gear the higher the torque and
        //then negate it when using motor by adding extra force
        frontWheel.axleJoint.setMaxMotorTorque(0);
        frontWheel.axleJoint.setMotorSpeed(1000);
        //	System.out.println(" brake off");
    }

    public int getGear() {
        return gear;
    }

    /**
     * takes in an int, if int is higher than available gears than nothing happens
     * neutral is 2
     * revers is 1
     *
     * @param gear
     */
    public void changeGear(int gear) {

        if (gear < 1) {
            gear = 1;
        }
        if (gear >= gears.length - 1) {
            gear = gears.length - 1;
        }
        this.gear = gear;

        switch (gear) {
            case 2:
                inReverse = false;

                break;

            case 1:
                inReverse = true;
                break;

            default:
                inReverse = false;

                break;
        }


        //	System.out.println(" gear " + gear);

    }

    public void applyHandBrake() {
        handBrakeOn = true;
    }

    public void releaseHandBrake() {
        handBrakeOn = false;
    }

    public String getFullName() {
        return make + " " + model + " " + year;
    }

    @Override
    public String toString() {

        return make + " " + model + " " + year;
    }

    public void setUpBox2dCar(RubeScene scene) {


        frontWheel = new Wheel(scene.getNamed(Body.class, "bodyfrontwheel").first(),
                scene.getNamed(RevoluteJoint.class, "jointfrontrevolute").first(),
                scene.getNamed(DistanceJoint.class, "jointfrontdistance").first());
        frontWheel.body.setBullet(true);
        frontWheel.body.setSleepingAllowed(false);

        rearWheel = new Wheel(scene.getNamed(Body.class, "bodyrearwheel").first(),
                scene.getNamed(RevoluteJoint.class, "jointrearrevolute").first(),
                scene.getNamed(DistanceJoint.class, "jointreardistance").first());


        body = scene.getNamed(Body.class, "bodycar").first();


        setUserData();

		/* on the very first load we want to get the
         * carsettings from the rubescene file and save them
		 */
        if (firstCarLoad) {
            carDefaultSettings.put(CarSetting.FrontSusDamp, frontWheel.getDampening());
            carDefaultSettings.put(CarSetting.FrontSusRideHeightOffset, frontWheel.getRideHeightOffset());
            carDefaultSettings.put(CarSetting.FrontSusRigidity, frontWheel.getRigity());
            carDefaultSettings.put(CarSetting.RearSusDamp, rearWheel.getDampening());
            carDefaultSettings.put(CarSetting.RearSusRideHeightOffset, rearWheel.getRideHeightOffset());
            carDefaultSettings.put(CarSetting.RearSusRigidity, rearWheel.getRigity());

            defaultGears = gears.clone();


            //TODO will need to do for gears and maybe other things
            firstCarLoad = false;
        }


        //this.frontWheelJoint.setSpringFrequencyHz(6f);
        body.setSleepingAllowed(false);
        //wheel.body.setSleepingAllowed(false);

        if (carSettings.containsKey(CarSetting.FrontSusDamp)) {
            frontWheel.setDampening(carSettings.get(CarSetting.FrontSusDamp));
        }
        if (carSettings.containsKey(CarSetting.FrontSusRideHeightOffset)) {
            frontWheel.setRideHeightOffset(carSettings.get(CarSetting.FrontSusRideHeightOffset));
        }
        if (carSettings.containsKey(CarSetting.FrontSusRigidity)) {
            frontWheel.susp.setFrequency(carSettings.get(CarSetting.FrontSusRigidity));
        }
        if (carSettings.containsKey(CarSetting.RearSusDamp)) {
            rearWheel.setDampening(carSettings.get(CarSetting.RearSusDamp));
        }
        if (carSettings.containsKey(CarSetting.RearSusRideHeightOffset)) {
            rearWheel.setRideHeightOffset(carSettings.get(CarSetting.RearSusRideHeightOffset));
        }
        if (carSettings.containsKey(CarSetting.RearSusRigidity)) {
            rearWheel.susp.setFrequency(carSettings.get(CarSetting.RearSusRigidity));
        }

        int defaultTyreFriction = 1000;

        frontWheel.axleJoint.enableMotor(true);
        frontWheel.setFriction(defaultTyreFriction);
        frontWheel.axleJoint.setMotorSpeed(10000);

        rearWheel.axleJoint.enableMotor(true);
        rearWheel.setFriction(defaultTyreFriction);
        rearWheel.axleJoint.setMotorSpeed(0);
        rearWheel.axleJoint.setMaxMotorTorque(0);

        airTime = 0;


    }

    /**
     * setting names for all bodies
     * helpful in detecting collisions
     */
    private void setUserData() {
        frontWheel.body.setUserData(BodyName.frontWheel);
        rearWheel.body.setUserData(BodyName.rearWheel);
        body.setUserData(BodyName.carShell);

    }

    public enum Make {
        Homda, Madza, Subartu, Jepe
    }


}
