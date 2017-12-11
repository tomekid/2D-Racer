package com.mediocrefireworks.realracer.tweens;


import com.badlogic.gdx.scenes.scene2d.Actor;

import aurelienribon.tweenengine.TweenAccessor;


public class ActorAnimator implements TweenAccessor<Actor> {

    public static final int Y = 0;
    public static final int X = 1;
    public static final int XY = 2;

    /**
     * changes width and height without changing width/height
     * enter the preferred width(not height)
     */
    public static final int SIZE = 3;
    public static final int ALPHA = 4;
    public static final int XandALPHA = 5;

    /**
     * changes width and height without changing width/height
     * enter the preferred width(not height)
     */
    public static final int SIZEandALPHA = 6;


    @Override
    public int getValues(Actor target, int tweenType, float[] returnValues) {
        // TODO Auto-generated method stub
        switch (tweenType) {
            case Y:
                returnValues[0] = target.getY();
                return 1;
            case X:
                returnValues[0] = target.getX();
                return 1;
            case XY:
                returnValues[0] = target.getX();
                returnValues[1] = target.getY();
                return 2;
            case SIZE:
                returnValues[0] = target.getWidth();
                return 1;
            case ALPHA:
                returnValues[0] = target.getColor().a;
                return 1;
            case XandALPHA:
                returnValues[0] = target.getX();
                returnValues[1] = target.getColor().a;
                return 2;
            case SIZEandALPHA:
                returnValues[0] = target.getWidth();
                returnValues[1] = target.getColor().a;
                return 2;
            default:
                assert false;
                return -1;
        }
    }

    @Override
    public void setValues(Actor target, int tweenType, float[] newValues) {
        // TODO Auto-generated method stub
        switch (tweenType) {
            case Y:
                target.setPosition(target.getX(), newValues[0]);
                break;
            case X:
                target.setPosition(newValues[0], target.getY());
                break;
            case XY:
                target.setPosition(newValues[0], newValues[1]);
                break;
            case SIZE:
                target.setSize(newValues[0], newValues[0] * target.getHeight() / target.getWidth());
                break;
            case ALPHA:
                target.setColor(1, 1, 1, newValues[0]);
                break;
            case XandALPHA:
                target.setPosition(newValues[0], target.getY());
                target.setColor(1, 1, 1, newValues[1]);
                break;
            case SIZEandALPHA:
                target.setPosition(target.getX() - newValues[0] / 2 + target.getWidth() / 2, target.getHeight() / 2 + target.getY() - (newValues[0] / 2 * target.getHeight() / target.getWidth()));
                target.setSize(newValues[0], newValues[0] * target.getHeight() / target.getWidth());
                target.setColor(1, 1, 1, newValues[1]);
                break;
            default:
                assert false;
                break;
        }
    }

}