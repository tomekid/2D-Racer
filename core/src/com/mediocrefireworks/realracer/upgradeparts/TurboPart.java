package com.mediocrefireworks.realracer.upgradeparts;


public enum TurboPart implements UpgradePart {

    None, Stock, T25, T3, T4, SportsIntercooler, RacingIntercooler;


    @Override
    public UpgradePart getFirstUpgradePart() {

        return TurboPart.Stock;
    }


    @Override
    public Type getPartType() {

        return Type.Turbo;
    }

    @Override
    public UpgradePart[] getExclusiveList(UpgradePart p) {

        //if we have no turbo then we need to remove everything
        switch ((TurboPart) p) {
            case None:
                return TurboPart.values();

            //if its one of the turbos then remove all the turbos
            case Stock:
            case T25:
            case T3:
            case T4:
                return new UpgradePart[]{TurboPart.None, TurboPart.Stock, TurboPart.T25, TurboPart.T3, TurboPart.T4};

            //if its an intercooler than remove all the intercoolers
            case RacingIntercooler:
            case SportsIntercooler:
                return new UpgradePart[]{TurboPart.RacingIntercooler, TurboPart.SportsIntercooler};
            default:
                break;


        }

        return TurboPart.values();
    }


    @Override
    public boolean isTorquePart() {

        return true;
    }


}
