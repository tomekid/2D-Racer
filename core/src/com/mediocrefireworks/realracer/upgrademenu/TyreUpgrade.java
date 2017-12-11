package com.mediocrefireworks.realracer.upgrademenu;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mediocrefireworks.realracer.RealRacer;
import com.mediocrefireworks.realracer.cars.Car;
import com.mediocrefireworks.realracer.cars.CarUpgrader;
import com.mediocrefireworks.realracer.menu.Menu;
import com.mediocrefireworks.realracer.menu.TuneMenuUpgradeParts;
import com.mediocrefireworks.realracer.upgradeparts.TyrePart;
import com.mediocrefireworks.realracer.upgradeparts.UpgradePart;
import com.mediocrefireworks.realracer.upgradeparts.UpgradePart.Type;

public class TyreUpgrade implements IUpgradeMenu {

    private float screenWidth;
    private float screenHeight;
    private RealRacer game;
    private TuneMenuUpgradeParts tuneMenuUpgrade;
    private Menu menu;
    private Car car;
    private CarUpgrader cU;


    public TyreUpgrade(RealRacer game, Car car,
                       TuneMenuUpgradeParts tuneMenuUpgrade, Menu menu) {
        this.game = game;
        this.car = car;
        this.tuneMenuUpgrade = tuneMenuUpgrade;
        this.menu = menu;
        screenWidth = game.getScreenWidth();
        screenHeight = game.getScreenHeight();
        cU = car.getCarUpgrader();
    }


    @Override
    public Table getTable(UpgradePart upgradePart) {
        Table table = new Table();
        loadEngineUpgradeMenu((TyrePart) upgradePart, table);

        return table;
    }


    private void loadEngineUpgradeMenu(TyrePart p, Table table) {
        LabelStyle infoText = new LabelStyle(game.font, Color.WHITE);

        String text = "";
        switch (p) {
            case Racing:
                break;
            case Sports:
                break;
            case Stock:
                break;
            default:
                break;


        }

        Label partDescriptionLabel = new Label(text, infoText);
        partDescriptionLabel.setWrap(true);
        table.add(partDescriptionLabel).width(screenWidth * 0.4f).padTop(game.gapMarginFactor * screenHeight);
        table.row();

        //if purchased show fit or unfit button
        //if unpurchased show buy button
        if (cU.isPurchased(p)) {
            if (!cU.isPartApplied(p)) {
                table.add(fitPartButton(p));
            }
        } else {
            table.add(buyButton(p));
        }

    }

    private Button buyButton(final TyrePart p) {
        TextButtonStyle unselectedStyle = new TextButtonStyle(game.newTDR("menubutton"), game.newTDR("menubuttonselected"), game.newTDR("menubuttonselected"), game.font);

        if (game.player.getCashBalance() >= cU.getPrice(p)) {
            unselectedStyle.fontColor = game.greenColor;
        } else {
            unselectedStyle.fontColor = game.orangeColor;
        }

        TextButton button = new TextButton("$" + cU.getPrice(p), unselectedStyle);
        button.setName("Buy");
        button.pad(10);

			/*TODO possibly make this class implement button listener so can have one refesh
             *window. would need to make a anotehr class
			 *perhaps. 1 for the adjustments and one for buying
			 */
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float f, float v) {

                cU.purchasePart(p);

                //refreshes the window and if bought displays the tune window
                tuneMenuUpgrade.refreshUpgradeWindow(p, TyreUpgrade.this);

                menu.updatePlayerCurrents();
            }
        });

        return button;
    }

    private Button fitPartButton(final TyrePart p) {
        final TextButtonStyle unselectedstyle = new TextButtonStyle(game.newTDR("menubutton"), game.newTDR("menubuttonselected"), game.newTDR("menubuttonselected"), game.font);


        TextButton button = new TextButton("Fit Part", unselectedstyle);
        button.setName("Fit Part");
        button.pad(10);

        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float f, float v) {

                cU.fitPart(p);
                tuneMenuUpgrade.refreshUpgradeWindow(p, TyreUpgrade.this);

            }
        });

        return button;
    }


    @Override
    public Type getUpgradePartType() {

        return UpgradePart.Type.Tyres;
    }

    @Override
    public UpgradePart[] getUpgradeParts() {

        return TyrePart.values();
    }

}
