package com.mediocrefireworks.realracer.upgradeparts;


public enum TyrePart implements UpgradePart {

    Stock, Sports, Racing;


    public float getFriction() {
        switch (this) {
            case Racing:
                return 10000f;

            case Sports:
                return 4000f;

            case Stock:
                return 3000f;

            default:
                break;

        }
        return 3000f;
    }

    @Override
    public Type getPartType() {

        return Type.Tyres;
    }

    @Override
    public UpgradePart getFirstUpgradePart() {

        return TyrePart.Stock;
    }


    @Override
    public UpgradePart[] getExclusiveList(UpgradePart p) {

        return TyrePart.values();
    }

    @Override
    public boolean isTorquePart() {

        return false;
    }

}
