package com.mediocrefireworks.realracer.upgradeparts;


public enum EnginePart implements UpgradePart {


    Bore, ECU, Tune, EngineComponents, PortPolish;


    @Override
    public Type getPartType() {

        return Type.Engine;

    }

    @Override
    public UpgradePart getFirstUpgradePart() {

        return EnginePart.Bore;
    }


    @Override
    public UpgradePart[] getExclusiveList(UpgradePart p) {

        return new UpgradePart[]{p};
    }

    @Override
    public boolean isTorquePart() {

        return true;
    }


}
