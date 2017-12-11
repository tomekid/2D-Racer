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
import com.mediocrefireworks.realracer.cars.TorqueCurve;
import com.mediocrefireworks.realracer.menu.Menu;
import com.mediocrefireworks.realracer.menu.TuneMenuUpgradeParts;
import com.mediocrefireworks.realracer.upgradeparts.EnginePart;
import com.mediocrefireworks.realracer.upgradeparts.UpgradePart;
import com.mediocrefireworks.realracer.upgradeparts.UpgradePart.Type;

public class EngineUpgrade implements IUpgradeMenu {


    private RealRacer game;
    private Car car;
    private CarUpgrader cU;
    private float screenHeight;
    private float screenWidth;
    private TuneMenuUpgradeParts tuneMenuUpgrade;
    private Menu menu;


    public EngineUpgrade(RealRacer game, Car car,
                         TuneMenuUpgradeParts tuneMenuUpgrade, Menu menu) {
        this.game = game;
        this.car = car;
        this.tuneMenuUpgrade = tuneMenuUpgrade;
        this.menu = menu;
        cU = car.getCarUpgrader();
        screenHeight = game.getScreenHeight();
        screenWidth = game.getScreenWidth();
    }


    /**
     * when adding new engine upgrades we need to add each one here
     */
    @Override
    public Table getTable(UpgradePart upgrade) {

        Table table = new Table();
        loadEngineUpgradeMenu((EnginePart) upgrade, table);

        return table;
    }


    private void loadEngineUpgradeMenu(EnginePart p, Table table) {
        LabelStyle infoText = new LabelStyle(game.font, Color.WHITE);

        //TODO have this text in the car class so it can be differnt for diffenrt cars
        //perhaps just the stock
        String text = "";
        switch (p) {
            case Bore:
                text = " It is possible to increase the engine displacement by boring out the cylinder block and using larger pistons";
                break;
            case ECU:
                text = "While great care and attention is made to make these devices fully " +
                        "functional and well set up from the factory, the manufacturers goals will most likely" +
                        " not have performance as the top of their agenda and it is possible to increase both torque" +
                        " and bhp with aftermarket or re programmable ECU chips.";
                break;
            case EngineComponents:
                break;
            case PortPolish:
                break;
            case Tune:
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

        TorqueCurve curve = cU.getEnginePartCurve(p);

		/*calculate the after bhp if remove is false then show the would be 
         * result from adding.
		 */
        boolean remove = false;
        if (cU.isPartApplied(p)) {
            remove = true;
        }
        table.add(new Label("After BHP: " + (int) car.getTorqueCurve().getMaxBhp(remove, curve), infoText));


        table.row();

        //if purchased show fit or unfit button
        //if unpurchased show buy button
        if (cU.isPurchased(p)) {
            if (cU.isPartApplied(p)) {
                table.add(unfitPartButton(p));
            } else {

                table.add(fitPartButton(p));
            }
        } else {
            TextButton buyButton = buyButton(p);
            table.add(buyButton);
        }


    }

    private TextButton buyButton(final EnginePart p) {
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
                tuneMenuUpgrade.refreshUpgradeWindow(p, EngineUpgrade.this);

                menu.updatePlayerCurrents();
            }
        });

        return button;
    }

    private TextButton fitPartButton(final EnginePart p) {
        final TextButtonStyle unselectedstyle = new TextButtonStyle(game.newTDR("menubutton"), game.newTDR("menubuttonselected"), game.newTDR("menubuttonselected"), game.font);


        TextButton button = new TextButton("Fit Part", unselectedstyle);
        button.setName("Fit Part");
        button.pad(10);

        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float f, float v) {

                cU.fitPart(p);
                tuneMenuUpgrade.refreshUpgradeWindow(p, EngineUpgrade.this);

            }
        });

        return button;
    }

    private TextButton unfitPartButton(final EnginePart p) {
        final TextButtonStyle unselectedstyle = new TextButtonStyle(game.newTDR("menubutton"), game.newTDR("menubuttonselected"), game.newTDR("menubuttonselected"), game.font);


        TextButton button = new TextButton("Remove Part", unselectedstyle);
        button.setName("Remove Part");
        button.pad(10);

        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float f, float v) {

                cU.unfitPart(p);
                tuneMenuUpgrade.refreshUpgradeWindow(p, EngineUpgrade.this);

            }
        });

        return button;
    }

    @Override
    public Type getUpgradePartType() {

        return UpgradePart.Type.Engine;
    }

    @Override
    public UpgradePart[] getUpgradeParts() {

        return EnginePart.values();
    }


}
