package com.mediocrefireworks.realracer.upgrademenu;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
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
import com.mediocrefireworks.realracer.upgradeparts.TurboPart;
import com.mediocrefireworks.realracer.upgradeparts.UpgradePart;
import com.mediocrefireworks.realracer.upgradeparts.UpgradePart.Type;

public class TurboUpgrade implements IUpgradeMenu {


    private RealRacer game;
    private Car car;
    private TuneMenuUpgradeParts sTuneMenuUpgrade;
    private Menu menu;
    private CarUpgrader cU;
    private float screenHeight;
    private float screenWidth;

    public TurboUpgrade(RealRacer game, Car car,
                        TuneMenuUpgradeParts sTuneMenuUpgrade, Menu menu) {
        this.game = game;
        this.car = car;
        this.sTuneMenuUpgrade = sTuneMenuUpgrade;
        this.menu = menu;
        cU = car.getCarUpgrader();
        screenHeight = game.getScreenHeight();
        screenWidth = game.getScreenWidth();

    }


    @Override
    public Table getTable(UpgradePart upgradePart) {
        Table table = new Table();
        loadEngineUpgradeMenu((TurboPart) upgradePart, table);

        return table;
    }

    private void loadEngineUpgradeMenu(TurboPart p, Table table) {
        LabelStyle infoText = new LabelStyle(game.font, Color.WHITE);

        //TODO have this text in the car class so it can be differnt for diffenrt cars
        //perhaps just the stock
        String text = "";
        switch (p) {
            case None:
                break;
            case RacingIntercooler:
                break;
            case SportsIntercooler:
                break;
            case Stock:
                text = " The stock turbo is usually pretty shit.";
                break;
            case T25:
                text = "Has a smaller turbine getting to max boost easier.";
                break;
            case T3:
                text = "Bigger turbine than t25 slower spool speed\n" +
                        "but still relatively quick.";
                break;
            case T4:
                text = "This big turbo takes a while to spool up\n" +
                        "but provides impressive power once going.";
                break;
            default:
                break;


        }


        Label partDescriptionLabel = new Label(text, infoText);
        partDescriptionLabel.setWrap(true);
        table.add(partDescriptionLabel).width(screenWidth * 0.4f).padTop(game.gapMarginFactor * screenHeight);
        table.row();

        table.add(new Label("Current BHP: " + (int) car.getTorqueCurve().getMaxBhp(), infoText));
        table.row();

        //if purchased show fit or unfit button
        //if unpurchased show buy button
        if (cU.isPurchased(p)) {
            if (cU.isPartApplied(p)) {
                if (p.equals(TurboPart.SportsIntercooler) || p.equals(TurboPart.RacingIntercooler)) {
                    //we want an unfit button on the intercoolers
                    table.add(new Label("After BHP: " + (int) cU.getMaxBhpFromFittingPart(p), infoText));
                    table.row();
                    table.add(unfitPartButton(p));
                }
            } else {
                table.add(new Label("After BHP: " + (int) cU.getMaxBhpFromFittingPart(p), infoText));
                table.row();
                table.add(fitPartButton(p));
            }
        } else {

            table.add(new Label("After BHP: " + (int) cU.getMaxBhpFromFittingPart(p), infoText));
            table.row();
            table.add(buyButton(p));
        }


    }

    private TextButton buyButton(final TurboPart p) {
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
                sTuneMenuUpgrade.refreshUpgradeWindow(p, TurboUpgrade.this);

                menu.updatePlayerCurrents();
            }
        });

        return button;
    }


    private TextButton fitPartButton(final TurboPart p) {
        final TextButtonStyle unselectedstyle = new TextButtonStyle(game.newTDR("menubutton"), game.newTDR("menubuttonselected"), game.newTDR("menubuttonselected"), game.font);


        TextButton button = new TextButton("Fit Part", unselectedstyle);
        button.setName("Fit Part");
        button.pad(10);

        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float f, float v) {

                cU.fitPart(p);
                sTuneMenuUpgrade.refreshUpgradeWindow(p, TurboUpgrade.this);

            }
        });

        return button;
    }

    private TextButton unfitPartButton(final TurboPart p) {
        final TextButtonStyle unselectedstyle = new TextButtonStyle(game.newTDR("menubutton"), game.newTDR("menubuttonselected"), game.newTDR("menubuttonselected"), game.font);


        TextButton button = new TextButton("Remove Part", unselectedstyle);
        button.setName("Remove Part");
        button.pad(10);

        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float f, float v) {

                cU.unfitPart(p);
                sTuneMenuUpgrade.refreshUpgradeWindow(p, TurboUpgrade.this);

            }
        });

        return button;
    }


    @Override
    public Type getUpgradePartType() {

        return Type.Turbo;
    }

    @Override
    public UpgradePart[] getUpgradeParts() {

        return TurboPart.values();
    }
}
