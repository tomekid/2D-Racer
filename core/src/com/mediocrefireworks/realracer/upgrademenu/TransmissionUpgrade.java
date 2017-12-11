package com.mediocrefireworks.realracer.upgrademenu;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Slider.SliderStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mediocrefireworks.realracer.RealRacer;
import com.mediocrefireworks.realracer.cars.Car;
import com.mediocrefireworks.realracer.cars.CarUpgrader;
import com.mediocrefireworks.realracer.menu.Menu;
import com.mediocrefireworks.realracer.menu.TuneMenuUpgradeParts;
import com.mediocrefireworks.realracer.sounndAndMusic.SoundManager;
import com.mediocrefireworks.realracer.upgradeparts.TransmissionPart;
import com.mediocrefireworks.realracer.upgradeparts.UpgradePart;

import java.text.DecimalFormat;

public class TransmissionUpgrade extends ChangeListener implements IUpgradeMenu {


    DecimalFormat dfp = new DecimalFormat("#.00");
    private RealRacer game;
    private Car car;
    private Table table;
    private TuneMenuUpgradeParts tuneMenuUpgrade;
    private Menu menu;
    private float screenWidth;
    private float screenHeight;
    private CarUpgrader cU;
    private Label[] gearLabels;


    public TransmissionUpgrade(RealRacer game, Car car, TuneMenuUpgradeParts tuneMenuUpgrade, Menu menu) {
        this.game = game;
        this.car = car;
        this.tuneMenuUpgrade = tuneMenuUpgrade;
        this.menu = menu;
        screenWidth = game.getScreenWidth();
        screenHeight = game.getScreenHeight();
        cU = car.getCarUpgrader();
    }


    @Override
    public UpgradePart.Type getUpgradePartType() {

        return UpgradePart.Type.Transmission;
    }

    @Override
    public UpgradePart[] getUpgradeParts() {

        return TransmissionPart.values();
    }

    @Override
    public Table getTable(UpgradePart p) {

        TransmissionPart tp = (TransmissionPart) p;

        table = new Table();

        if (cU.isPartApplied(tp)) {
            switch (tp) {
                case Racing:
                    loadRacingTransmission();
                    break;
                case Stock:
                    loadStockTransmission();
                    break;
                default:
                    throw new IllegalArgumentException("You tried to switch on part" + p + " but it wasnt a case. in TransmissionUpgrade.java");
            }
        } else {
            loadUnboughtMenu(tp);
        }

        return table;


    }


    private void loadUnboughtMenu(TransmissionPart p) {
        LabelStyle infoText = new LabelStyle(game.font, Color.WHITE);

        //TODO have this text in the car class so it can be differnt for diffenrt cars
        //perhaps just the stock
        String text = "";
        switch (p) {
            case Racing:
                text = "Racing Transmission \n" +
                        "Fully customisable Transmission. \n" +
                        "Necessary when adding large power to your car.";
                break;

            case Stock:
                text = "Stock Transmission";
                break;

            default:
                throw new IllegalArgumentException("You tried to switch on part" + p + " but it wasnt a case.");

        }


        table.add(new Label(text, infoText)).padTop(game.gapMarginFactor * screenHeight);
        table.row();


        //if purchased show selct button
        //if unpurchased show buy button
        if (cU.isPurchased(p)) {
            TextButton selectButton = fitPartButton(p);
            table.add(selectButton);
        } else {
            TextButton buyButton = buyButton(p);
            table.add(buyButton);
        }


    }

    /**
     * returns a default button to reset the default suspension settings
     *
     * @param p
     * @return
     */
    private TextButton defaultButton(final TransmissionPart p) {
        final TextButtonStyle unselectedstyle = new TextButtonStyle(game.newTDR("menubutton"), game.newTDR("menubuttonselected"), game.newTDR("menubuttonselected"), game.font);


        final TextButton button = new TextButton("Default", unselectedstyle);
        button.setName("Default");
        button.pad(10);

        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float f, float v) {
                SoundManager.buttonClicked(SoundManager.click1, game.sounds);
                resetSettings();
                tuneMenuUpgrade.refreshUpgradeWindow(p, TransmissionUpgrade.this);
                uncheck();
            }

            private void uncheck() {
                button.setChecked(false);

            }

        });


        return button;
    }

    private TextButton fitPartButton(final TransmissionPart p) {
        final TextButtonStyle unselectedstyle = new TextButtonStyle(game.newTDR("menubutton"), game.newTDR("menubuttonselected"), game.newTDR("menubuttonselected"), game.font);


        TextButton button = new TextButton("Fit Part", unselectedstyle);
        button.setName("Fit Part");
        button.pad(10);

        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float f, float v) {
                resetSettings();
                cU.fitPart(p);
                tuneMenuUpgrade.refreshUpgradeWindow(p, TransmissionUpgrade.this);

            }
        });

        return button;
    }

    private TextButton buyButton(final TransmissionPart p) {
        TextButtonStyle unselectedStyle = new TextButtonStyle(game.newTDR("menubutton"), game.newTDR("menubuttonselected"), game.newTDR("menubuttonselected"), game.font);

        if (game.player.getCashBalance() >= cU.getPrice(p)) {
            unselectedStyle.fontColor = game.greenColor;
        } else {
            unselectedStyle.fontColor = game.orangeColor;
        }

        TextButton button = new TextButton("$" + cU.getPrice(p), unselectedStyle);
        button.setName("Buy");
        button.pad(10);

			/*TODO make own buy button/fit button classes or static methods
             *window. would need to make a anotehr class
			 *perhaps. 1 for the adjustments and one for buying
			 */
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float f, float v) {

                cU.purchasePart(p);

                //refreshes the window and if bought displays the tune window
                tuneMenuUpgrade.refreshUpgradeWindow(p, TransmissionUpgrade.this);

                menu.updatePlayerCurrents();
            }
        });

        return button;

    }


    private void resetSettings() {
        car.resetGears();

    }

    private void loadStockTransmission() {

        LabelStyle infoText = new LabelStyle(game.font, Color.WHITE);
        table.add(new Label("Stock Transmission", infoText)).padTop(game.gapMarginFactor * screenHeight);
        table.row();
    }


    private void loadRacingTransmission() {

        LabelStyle infoText = new LabelStyle(game.font, Color.WHITE);
        SliderStyle ss = new SliderStyle(game.newTDR("menubuttonselected"), game.newTDR("menuslider"));
		
		/*need to add this listener to make the slider slide without scrolling*/
        InputListener stopTouchDown = new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                event.stop();
                return false;
            }
        };

        table.add(defaultButton(TransmissionPart.Racing));
        table.row();
		
		/*loop over gears and display them 
		 * [0] is final gear
		 * [1] is reverse
		 * [2] is neutral
		 */
        //gears
        float[] g = car.getGears();
        //need to have a collection of the labels to refer to them when updating values
        gearLabels = new Label[g.length];
        for (int x = 3; x < car.getGears().length; x++) {
            table.add(new Label(x - 2 + "", infoText)).padTop(screenHeight * 0.05f);
            Slider gearSlider = new Slider(0f, 5f, 0.01f, false, ss);
            gearSlider.setName("" + x);
            gearSlider.setValue(g[x]);
            gearSlider.addListener(this);
            gearSlider.addListener(stopTouchDown);
            gearLabels[x] = new Label(dfp.format(g[x]), infoText);
            table.add(gearLabels[x]);
            table.row();
            table.add(gearSlider).width(screenWidth * .4f);
            table.row();
        }
        //final gear
        table.add(new Label("Final", infoText)).padTop(screenHeight * 0.05f);
        Slider gearSlider = new Slider(0f, 5f, 0.01f, false, ss);
        gearSlider.setName("" + 0);
        gearSlider.setValue(g[0]);
        gearSlider.addListener(this);
        gearSlider.addListener(stopTouchDown);
        gearLabels[0] = new Label(dfp.format(g[0]), infoText);
        table.add(gearLabels[0]);
        table.row();
        table.add(gearSlider).width(screenWidth * .4f);
        table.row();


    }


    @Override
    public void changed(ChangeEvent event, Actor actor) {

        int g = Integer.parseInt(event.getTarget().getName());

        //store the value into settings
        float val = ((Slider) event.getTarget()).getValue();
        //System.out.println(" fuck you " + val + "   "+car.getGears()[g]);
        car.getGears()[g] = val;
        gearLabels[g].setText(dfp.format(val));


    }


}


