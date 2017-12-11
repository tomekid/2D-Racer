package com.mediocrefireworks.realracer.upgradeparts;


public enum TransmissionPart implements UpgradePart {

    Stock, Racing;


    @Override
    public UpgradePart getFirstUpgradePart() {

        return TransmissionPart.Stock;
    }

    @Override
    public Type getPartType() {

        return UpgradePart.Type.Transmission;
    }

    @Override
    public UpgradePart[] getExclusiveList(UpgradePart p) {

        return TransmissionPart.values();
    }


    @Override
    public boolean isTorquePart() {

        return false;
    }


}
