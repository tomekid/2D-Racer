package com.mediocrefireworks.realracer.upgrademenu;


import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.mediocrefireworks.realracer.upgradeparts.UpgradePart;


public interface IUpgradeMenu {

    Table getTable(UpgradePart upgradePart);

    UpgradePart.Type getUpgradePartType();

    /**
     * get list of the upgradeParts in this type of upgrade
     *
     * @return
     */
    UpgradePart[] getUpgradeParts();


    //TODO add in common methods
    //public void fitPartButton();
}
