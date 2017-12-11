package com.mediocrefireworks.realracer;

import com.badlogic.gdx.physics.box2d.Body;
import com.gushikustudios.rube.RubeScene;
import com.mediocrefireworks.realracer.Box2DWorld.BodyName;

public class Track {

    public Body finishLine;
    public Body ground;
    private String name;
    private String dir;

    public Track(String name, String dir) {
        // TODO Auto-generated constructor stub
        this.name = name;
        this.dir = dir;
    }

    public String getName() {
        return name;
    }

    public String getDir() {
        return dir;
    }

    public void setUpbox2dTrack(RubeScene scene) {
        finishLine = scene.getNamed(Body.class, "finishSensor").first();
        finishLine.setUserData(BodyName.finishSensor);

        ground = scene.getNamed(Body.class, "ground").first();
        ground.setUserData(BodyName.ground);

    }


    public enum TrackType {
        Time, Air, Small_Car
    }


}
