package com.mediocrefireworks.realracer.menu;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mediocrefireworks.realracer.RealRacer;
import com.mediocrefireworks.realracer.cars.Car;
import com.mediocrefireworks.realracer.upgrademenu.IUpgradeMenu;
import com.mediocrefireworks.realracer.upgradeparts.UpgradePart;

import java.util.HashMap;


public class TuneMenuUpgradeParts {

    RealRacer game;
    Table table;
    Car car;

    float screenWidth;
    float screenHeight;
    HashMap<UpgradePart, TextButton> upgradePartButtons;
    private ScrollPane mainUpgradeWindow;
    private TextButtonStyle buttonStyle;
    private TextButtonStyle greenButtonStyle;

    public TuneMenuUpgradeParts(RealRacer game, Car car) {

        this.game = game;
        this.car = car;
        screenWidth = game.getScreenWidth();
        screenHeight = game.getScreenHeight();

    }

    private void createTable(final IUpgradeMenu upgrade) {

        LabelStyle infoText = new LabelStyle(game.font, Color.WHITE);
        buttonStyle = new TextButtonStyle(game.newTDR("menubutton"), game.newTDR("menubuttonselected"), game.newTDR("menubuttonselected"), game.font);
        greenButtonStyle = new TextButtonStyle(game.newTDR("menubutton"), game.newTDR("menubuttonselected"), game.newTDR("menubuttonselected"), game.font);
        greenButtonStyle.fontColor = game.greenColor;

        table = new Table();
        Table descriptionTable = new Table();
        //TODO maybe does this line below work ? output correct string
        descriptionTable.add(new Label(upgrade.getUpgradePartType() + "", infoText));
        table.add(descriptionTable).expandX();
        table.row();

		/* add in the tune/upgrade table main window!*/
        //this line should get the currently selected upgrademenu

        mainUpgradeWindow = new ScrollPane(upgrade.getTable(car.getCarUpgrader().getSelectedUpgradePart(upgrade.getUpgradePartType())));
        mainUpgradeWindow.setScrollX(0);
        table.add(mainUpgradeWindow).width(screenWidth * 0.5f).height(screenHeight * .5f);

        table.row();
        Table upgradeItems = new Table();
        ScrollPane upgradeItemScrollPane = new ScrollPane(upgradeItems);
        table.add(upgradeItemScrollPane).width(screenWidth * .5f);

        ButtonGroup bg = new ButtonGroup();
        upgradePartButtons = new HashMap<UpgradePart, TextButton>();
        /*
		 * for each upgrade version button
		 * like the stock sports racing 
		 * 
		 */
        for (final UpgradePart upgradePart : upgrade.getUpgradeParts()) {
            TextButton tb;

            if (car.getCarUpgrader().isPartApplied(upgradePart)) {


                tb = new TextButton(upgradePart.toString(), greenButtonStyle);
                tb.setChecked(true);
            } else {
                tb = new TextButton(upgradePart.toString(), buttonStyle);
            }
            upgradePartButtons.put(upgradePart, tb);
            tb.pad(10);
            bg.add(tb);
            upgradeItems.add().width(10);
            upgradeItems.add(tb);
            upgradeItems.add().width(10);

            tb.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float f, float v) {

                    refreshUpgradeWindow(upgradePart, upgrade);

                }
            });

        }

    }

    public Table getTable(IUpgradeMenu upgrade) {

        createTable(upgrade);
        return table;

    }

    public void refreshUpgradeWindow(UpgradePart upgradePartx, IUpgradeMenu upgrade) {
        mainUpgradeWindow.setWidget(upgrade.getTable(upgradePartx));

		/*upgrade the upgradepart buttons to make each green
		 * straight after clicking 
		 */
        for (UpgradePart upgradePart : upgradePartButtons.keySet()) {
            if (car.getCarUpgrader().isPartApplied(upgradePart)) {
                //setting style deletes padding
                upgradePartButtons.get(upgradePart).setStyle(greenButtonStyle);
                upgradePartButtons.get(upgradePart).pad(10);
            } else {
                //upgradePartButtons.get(upgradePart).setColor(Color.WHITE);
                upgradePartButtons.get(upgradePart).setStyle(buttonStyle);
                upgradePartButtons.get(upgradePart).pad(10);
            }
        }

    }


}
