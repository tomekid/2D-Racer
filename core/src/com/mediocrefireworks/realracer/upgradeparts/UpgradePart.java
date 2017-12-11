package com.mediocrefireworks.realracer.upgradeparts;


import com.mediocrefireworks.realracer.RealRacer;
import com.mediocrefireworks.realracer.cars.Car;
import com.mediocrefireworks.realracer.menu.Menu;
import com.mediocrefireworks.realracer.menu.TuneMenuUpgradeParts;
import com.mediocrefireworks.realracer.upgrademenu.EngineUpgrade;
import com.mediocrefireworks.realracer.upgrademenu.IUpgradeMenu;
import com.mediocrefireworks.realracer.upgrademenu.SuspensionUpgrade;
import com.mediocrefireworks.realracer.upgrademenu.TransmissionUpgrade;
import com.mediocrefireworks.realracer.upgrademenu.TurboUpgrade;
import com.mediocrefireworks.realracer.upgrademenu.TyreUpgrade;
import com.mediocrefireworks.realracer.upgrademenu.WheelUpgrade;


public interface UpgradePart {
    Type getPartType();

    /**
     * returns the first found applied part of a given parttype
     * if no part is applied ie engine where it is not
     * necesary to have a part applied we return first part.
     *
     * @return
     */
    UpgradePart getFirstUpgradePart();

    /**
     * returns wether or not this part should have a torque Curve
     * asscociated with it
     *
     * @return
     */
    boolean isTorquePart();

    /**
     * returns a collection of parts that
     * are mutually exclusive with this part.
     * if it is not exclusive will return a list with only
     * this part in it.
     *
     * @return
     */

    UpgradePart[] getExclusiveList(UpgradePart p);

    enum Type {
        Suspension, Tyres, Wheels, Turbo, Engine, Transmission;


        public UpgradePart getFirstUpgradePart() {

            switch (this) {
                case Engine:
                    return EnginePart.Bore;
                case Suspension:
                    return SuspensionPart.Stock;
                case Transmission:
                    return TransmissionPart.Stock;
                case Turbo:
                    return TurboPart.Stock;
                case Tyres:
                    return TyrePart.Stock;
                case Wheels:
                    return WheelPart.Stock;
                default:
                    throw new IllegalArgumentException("You tried to find the first Upgrade part" +
                            "of type " + this + " but it wasnt a switch case");

            }
        }

        public IUpgradeMenu getUpgradeMenu(RealRacer game, Car selectedCar,
                                           TuneMenuUpgradeParts sTuneMenuUpgrade, Menu menu) {


            switch (this) {

                case Engine:
                    return new EngineUpgrade(game, game.selectedCar, sTuneMenuUpgrade, menu);

                case Suspension:
                    return new SuspensionUpgrade(game, game.selectedCar, sTuneMenuUpgrade, menu);

                case Turbo:
                    return new TurboUpgrade(game, game.selectedCar, sTuneMenuUpgrade, menu);

                case Tyres:
                    return new TyreUpgrade(game, game.selectedCar, sTuneMenuUpgrade, menu);

                case Wheels:
                    return new WheelUpgrade(game, game.selectedCar, sTuneMenuUpgrade, menu);

                case Transmission:
                    return new TransmissionUpgrade(game, game.selectedCar, sTuneMenuUpgrade, menu);

            }

            throw new IllegalArgumentException("You have tried to create a new UpgradePartMenu which isnt " +
                    "a case in the switch. in TuneMenuScreen.java	");

        }


    }
}


