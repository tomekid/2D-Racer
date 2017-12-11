package com.mediocrefireworks.realracer.upgrademenu;


import com.badlogic.gdx.Gdx;
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
import com.mediocrefireworks.realracer.upgradeparts.SuspensionPart;
import com.mediocrefireworks.realracer.upgradeparts.UpgradePart;

import java.text.DecimalFormat;

public class SuspensionUpgrade extends ChangeListener implements IUpgradeMenu {


    DecimalFormat dfp = new DecimalFormat("#.00");
    private RealRacer game;
    private Car car;
    private Label frhlabel;
    private Label rrhlabel;
    private Label fsrlabel;
    private Label rsrlabel;
    private Label fdlabel;
    private Label rdlabel;
    private Table table;
    private TuneMenuUpgradeParts tuneMenuUpgrade;
    private Menu menu;
    private float screenWidth;
    private float screenHeight;
    private CarUpgrader cU;


    public SuspensionUpgrade(RealRacer game, Car car, TuneMenuUpgradeParts tuneMenuUpgrade, Menu menu) {
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

        return UpgradePart.Type.Suspension;
    }

    @Override
    public UpgradePart[] getUpgradeParts() {
        // TODO Auto-generated method stub
        return SuspensionPart.values();
    }

    @Override
    public Table getTable(UpgradePart upgradePart) {

        table = new Table();
        SuspensionPart sp = (SuspensionPart) upgradePart;

        if (cU.isPartApplied(upgradePart)) {


            switch (sp) {
                case Racing:
                    loadRacingSuspension();
                    break;
                case Sports:
                    loadSportsSuspension();
                    break;
                case Stock:
                    loadStockSuspension();
                    break;
                default:
                    break;
            }

        } else {
            loadUnboughtMenu(sp);
        }

        return table;
    }


    private void loadUnboughtMenu(SuspensionPart p) {
        LabelStyle infoText = new LabelStyle(game.font, Color.WHITE);

        //TODO have this text in the car class so it can be differnt for diffenrt cars
        //perhaps just the stock
        String text = "";
        switch (p) {
            case Racing:
                text = "Racing Suspension \n" +
                        "Fully adjustable suspenion. Ideal for fine tuning\n" +
                        "and getting the most out of your car.";
                break;
            case Sports:
                text = "Sports Suspension \n" +
                        "Ride height adjustable suspension.";

                break;
            case Stock:
                text = "Stock Suspension\n" +
                        "The standard suspension which\n" +
                        "comes with the car";
                break;
            default:
                break;

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
    private TextButton defaultButton(final SuspensionPart p) {
        final TextButtonStyle unselectedstyle = new TextButtonStyle(game.newTDR("menubutton"), game.newTDR("menubuttonselected"), game.newTDR("menubuttonselected"), game.font);


        final TextButton button = new TextButton("Default", unselectedstyle);
        button.setName("Default");
        button.pad(10);

        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float f, float v) {
                SoundManager.buttonClicked(SoundManager.click1, game.sounds);
                resetSettings();
                tuneMenuUpgrade.refreshUpgradeWindow(p, SuspensionUpgrade.this);
                uncheck();
            }

            private void uncheck() {
                button.setChecked(false);

            }

        });


        return button;
    }

    private TextButton fitPartButton(final SuspensionPart p) {
        final TextButtonStyle unselectedstyle = new TextButtonStyle(game.newTDR("menubutton"), game.newTDR("menubuttonselected"), game.newTDR("menubuttonselected"), game.font);


        TextButton button = new TextButton("Fit Part", unselectedstyle);
        button.setName("Fit Part");
        button.pad(10);

        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float f, float v) {
                resetSettings();
                cU.fitPart(p);
                tuneMenuUpgrade.refreshUpgradeWindow(p, SuspensionUpgrade.this);

            }
        });

        return button;
    }

    private TextButton buyButton(final SuspensionPart p) {
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
                tuneMenuUpgrade.refreshUpgradeWindow(p, SuspensionUpgrade.this);

                menu.updatePlayerCurrents();
            }
        });

        return button;

    }


    private void resetSettings() {
        for (CarSetting s : car.getDefaultSettings().keySet()) {
            float val = car.getDefaultSettings().get(s);
            car.carSettings.put(s, val);
            System.out.println(" part " + s + " val " + val);
            switch (s) {
                case FrontSusDamp:
                    car.frontWheel.setDampening(val);
                    break;
                case FrontSusRideHeightOffset:
                    car.frontWheel.setRideHeightOffset(val);
                    break;
                case FrontSusRigidity:
                    car.frontWheel.setRigity(val);
                    break;
                case RearSusDamp:
                    car.rearWheel.setDampening(val);
                    break;
                case RearSusRideHeightOffset:
                    car.rearWheel.setRideHeightOffset(val);
                    break;
                case RearSusRigidity:
                    car.rearWheel.setRigity(val);
                    break;
                default://do nothing for non suspension parts
                    break;

            }
        }

    }

    private void loadStockSuspension() {

        LabelStyle infoText = new LabelStyle(game.font, Color.WHITE);
        table.add(new Label("Stock Suspension", infoText)).padTop(game.gapMarginFactor * screenHeight);
        table.row();
    }

    private void loadSportsSuspension() {

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

        table.add(defaultButton(SuspensionPart.Sports));
        table.row();
        table.add(new Label("Ride Height", infoText)).padTop(game.gapMarginFactor * screenHeight);
        table.row();


        frhlabel = new Label(dfp.format(car.frontWheel.getRideHeightOffset()) + "", infoText);
        Slider frontRHSlider = new Slider(-0.15f, 0.15f, 0.01f, false, ss);
        frontRHSlider.addListener(this);
        frontRHSlider.setName("frontSusRideHeightOffset");
        frontRHSlider.setValue(car.frontWheel.getRideHeightOffset());
        frontRHSlider.addListener(stopTouchDown);
        table.add(new Label("F", infoText));
        table.add(frontRHSlider).width(screenWidth * .25f);
        table.add(frhlabel);

        table.row();

        rrhlabel = new Label(dfp.format(car.frontWheel.getRideHeightOffset()) + "", infoText);
        Slider rearRHSlider = new Slider(-0.15f, 0.15f, 0.01f, false, ss);
        rearRHSlider.addListener(this);
        rearRHSlider.setName("rearSusRideHeightOffset");
        rearRHSlider.setValue(car.rearWheel.getRideHeightOffset());
        rearRHSlider.addListener(stopTouchDown);
        table.add(new Label("R", infoText));
        table.add(rearRHSlider).pad(game.gapMarginFactor * screenHeight).width(screenWidth * .25f);
        table.add(rrhlabel);

    }


    private void loadRacingSuspension() {

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

        table.add(defaultButton(SuspensionPart.Racing));
        table.row();
        table.add(new Label("Ride Height", infoText)).padTop(game.gapMarginFactor * screenHeight);
        table.row();


        frhlabel = new Label(dfp.format(car.frontWheel.getRideHeightOffset()) + "", infoText);
        Slider frontRHSlider = new Slider(-0.15f, 0.15f, 0.01f, false, ss);
        frontRHSlider.addListener(this);
        frontRHSlider.setName("frontSusRideHeightOffset");
        frontRHSlider.setValue(car.frontWheel.getRideHeightOffset());
        frontRHSlider.addListener(stopTouchDown);
        table.add(new Label("F", infoText));
        table.add(frontRHSlider).width(screenWidth * .25f);
        table.add(frhlabel);

        table.row();

        rrhlabel = new Label(dfp.format(car.frontWheel.getRideHeightOffset()) + "", infoText);
        Slider rearRHSlider = new Slider(-0.15f, 0.15f, 0.01f, false, ss);
        rearRHSlider.addListener(this);
        rearRHSlider.setName("rearSusRideHeightOffset");
        rearRHSlider.setValue(car.rearWheel.getRideHeightOffset());
        rearRHSlider.addListener(stopTouchDown);
        table.add(new Label("R", infoText));
        table.add(rearRHSlider).pad(game.gapMarginFactor * screenHeight).width(screenWidth * .25f);
        table.add(rrhlabel);

        table.row();
        table.add(new Label("Rigidity", infoText)).padTop(game.gapMarginFactor * screenHeight);
        table.row();


        fsrlabel = new Label(dfp.format(car.frontWheel.getRigity()) + "", infoText);
        Slider frontSRSlider = new Slider(2.5f, 9, 0.1f, false, ss);
        frontSRSlider.addListener(this);
		/* tryna chagne the width of the scroll pane*/
        frontSRSlider.setSize(Gdx.graphics.getWidth() * 0.9f, frontSRSlider.getPrefHeight());
        frontSRSlider.setWidth(Gdx.graphics.getWidth() * 0.9f);
        frontSRSlider.setName("frontSusRigidity");
        frontSRSlider.setValue(car.frontWheel.getRigity());
        frontSRSlider.addListener(stopTouchDown);
        table.add(new Label("F", infoText));
        table.add(frontSRSlider).pad(game.gapMarginFactor * screenHeight).width(screenWidth * .25f);
        table.add(fsrlabel);

        table.row();

        rsrlabel = new Label(dfp.format(car.frontWheel.getRigity()) + "", infoText);
        Slider rearSRSlider = new Slider(2.5f, 9, 0.1f, false, ss);
        rearSRSlider.addListener(this);
        rearSRSlider.setName("rearSusRigidity");
        rearSRSlider.setValue(car.rearWheel.getRigity());
        rearSRSlider.addListener(stopTouchDown);
        table.add(new Label("R", infoText));
        table.add(rearSRSlider).pad(game.gapMarginFactor * screenHeight).width(screenWidth * .25f);
        table.add(rsrlabel);


        table.row();
        table.add(new Label("Dampening", infoText)).padTop(game.gapMarginFactor * screenHeight);
        table.row();


        fdlabel = new Label(dfp.format(car.frontWheel.getDampening()) + "", infoText);
        Slider frontDSlider = new Slider(0.01f, 0.9f, 0.01f, false, ss);
        frontDSlider.addListener(this);
        frontDSlider.setName("frontSusDamp");
        frontDSlider.setValue(car.frontWheel.getDampening());
        frontDSlider.addListener(stopTouchDown);
        table.add(new Label("F", infoText));
        table.add(frontDSlider).pad(game.gapMarginFactor * screenHeight).width(screenWidth * .25f);
        table.add(fdlabel);

        table.row();

        rdlabel = new Label(dfp.format(car.frontWheel.getDampening()) + "", infoText);
        Slider rearDSlider = new Slider(0.01f, 0.9f, 0.01f, false, ss);
        rearDSlider.addListener(this);
        rearDSlider.setName("rearSusDamp");
        rearDSlider.setValue(car.rearWheel.getDampening());
        rearDSlider.addListener(stopTouchDown);
        table.add(new Label("R", infoText));
        table.add(rearDSlider).pad(game.gapMarginFactor * screenHeight).width(screenWidth * .25f);
        table.add(rdlabel);


    }


    @Override
    public void changed(ChangeEvent event, Actor actor) {

        String name = event.getTarget().getName();

        //store the value into settings
        float val = ((Slider) event.getTarget()).getValue();


        if (name.equals("frontSusRigidity")) {
            car.frontWheel.setRigity(val);
            fsrlabel.setText(dfp.format(val));
            car.carSettings.put(CarSetting.FrontSusRigidity, val);
        } else if (name.equals("rearSusRigidity")) {
            car.rearWheel.setRigity(val);
            rsrlabel.setText(dfp.format(val));
            car.carSettings.put(CarSetting.RearSusRigidity, val);
        } else if (name.equals("frontSusRideHeightOffset")) {
            car.frontWheel.setRideHeightOffset(val);
            frhlabel.setText(dfp.format(val));
            car.carSettings.put(CarSetting.FrontSusRideHeightOffset, val);
        } else if (name.equals("rearSusRideHeightOffset")) {
            car.rearWheel.setRideHeightOffset(val);
            rrhlabel.setText(dfp.format(val));
            car.carSettings.put(CarSetting.RearSusRideHeightOffset, val);
        } else if (name.equals("frontSusDamp")) {
            car.frontWheel.setDampening(val);
            fdlabel.setText(dfp.format(val));
            car.carSettings.put(CarSetting.FrontSusDamp, val);
        } else if (name.equals("rearSusDamp")) {
            car.rearWheel.setDampening(val);
            rdlabel.setText(dfp.format(val));
            car.carSettings.put(CarSetting.RearSusDamp, val);
        }


    }


}


