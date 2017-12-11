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
import com.mediocrefireworks.realracer.upgradeparts.UpgradePart;
import com.mediocrefireworks.realracer.upgradeparts.UpgradePart.Type;
import com.mediocrefireworks.realracer.upgradeparts.WheelPart;

public class WheelUpgrade implements IUpgradeMenu {


    private float screenWidth;
    private float screenHeight;
    private RealRacer game;
    private TuneMenuUpgradeParts tuneMenuUpgrade;
    private Menu menu;
    private Car car;
    private CarUpgrader cU;


    public WheelUpgrade(RealRacer game, Car car,
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
    public Type getUpgradePartType() {

        return Type.Wheels;
    }

    @Override
    public UpgradePart[] getUpgradeParts() {

        return WheelPart.values();
    }

    @Override
    public Table getTable(UpgradePart upgradePart) {
        Table table = new Table();
        loadEngineUpgradeMenu((WheelPart) upgradePart, table);

        return table;
    }

    private void loadEngineUpgradeMenu(WheelPart p, Table table) {
        LabelStyle infoText = new LabelStyle(game.font, Color.WHITE);

        String text = "";
        switch (p) {
            case Stock:
                break;
            case wheel1:
                break;
            case wheel2:
                break;
            case wheel3:
                break;
            case wheel4:
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

    private Button buyButton(final WheelPart p) {
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
                tuneMenuUpgrade.refreshUpgradeWindow(p, WheelUpgrade.this);

                menu.updatePlayerCurrents();
            }
        });

        return button;
    }

    private Button fitPartButton(final WheelPart p) {
        final TextButtonStyle unselectedstyle = new TextButtonStyle(game.newTDR("menubutton"), game.newTDR("menubuttonselected"), game.newTDR("menubuttonselected"), game.font);


        TextButton button = new TextButton("Fit Part", unselectedstyle);
        button.setName("Fit Part");
        button.pad(10);

        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float f, float v) {

                cU.fitPart(p);
                tuneMenuUpgrade.refreshUpgradeWindow(p, WheelUpgrade.this);

            }
        });

        return button;
    }


}
