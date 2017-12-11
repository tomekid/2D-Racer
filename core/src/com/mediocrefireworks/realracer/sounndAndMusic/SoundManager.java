package com.mediocrefireworks.realracer.sounndAndMusic;

import com.badlogic.gdx.audio.Sound;
import com.mediocrefireworks.realracer.RealRacer.Screens;
import com.mediocrefireworks.realracer.upgradeparts.UpgradePart;

import java.util.Map;

public class SoundManager {

    public final static int click1 = 1;
    public final static int click2 = 2;
    public final static int buy = 3;
    public final static int carStart = 4;

    /**
     * sound will be played accroding to the type
     * <p>
     * ex:- SoundManager.buttonClicked(SoundManager.simpleButtonClick,game.sounds)
     *
     * @param type
     * @param sounds game.sounds!!
     */
    public static void buttonClicked(int type, Map<String, Sound> sounds) {
        switch (type) {
            case click1:
                sounds.get("click1").play();
                break;
            case click2:
                sounds.get("click2").play();
                break;
            case buy:
                sounds.get("buy").play();
            default:

                break;
        }
    }


    public static void buttonClicked(UpgradePart.Type type, Map<String, Sound> sounds) {
        try {
            sounds.get("" + type.toString()).play();
        } catch (Exception e) {
            System.out.println("connot find Sound :" + type.toString());
        }
    }

    public static void buttonClicked(Screens screen, Map<String, Sound> sounds) {
        sounds.get("" + screen.toString()).play();
    }


}
