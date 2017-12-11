package com.mediocrefireworks.realracer.upgradeparts;


public enum SuspensionPart implements UpgradePart {

    Stock, Sports, Racing;

    @Override
    public Type getPartType() {

        return Type.Suspension;
    }

    @Override
    public UpgradePart getFirstUpgradePart() {

        return SuspensionPart.Stock;
    }


    @Override
    public UpgradePart[] getExclusiveList(UpgradePart p) {

        return SuspensionPart.values();
    }

    @Override
    public boolean isTorquePart() {

        return false;
    }

}
