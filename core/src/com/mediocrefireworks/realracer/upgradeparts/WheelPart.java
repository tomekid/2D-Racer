package com.mediocrefireworks.realracer.upgradeparts;


public enum WheelPart implements UpgradePart {

    Stock, wheel1, wheel2, wheel3, wheel4;

    @Override
    public Type getPartType() {

        return Type.Wheels;
    }

    @Override
    public UpgradePart getFirstUpgradePart() {

        return WheelPart.Stock;
    }


    @Override
    public UpgradePart[] getExclusiveList(UpgradePart p) {

        return WheelPart.values();
    }

    @Override
    public boolean isTorquePart() {

        return false;
    }

}
