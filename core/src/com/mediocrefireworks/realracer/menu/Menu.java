package com.mediocrefireworks.realracer.menu;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.mediocrefireworks.realracer.RealRacer;
import com.mediocrefireworks.realracer.RealRacer.Screens;
import com.mediocrefireworks.realracer.sounndAndMusic.SoundManager;

public class Menu {

    public Label playerCurrentsLabel;
    public Table mainTable;
    public Table rightTable;
    public Table leftTable;
    public Table middleTable;
    public Table infoTable;
    public Table leftListTable;
    public ScrollPane scrollPane;
    private Screens menuName;
    private Table rightBtnsTable;
    private RealRacer game;
    private Screens currentMenuName;
    private float screenWidth;
    private float screenHeight;

    /**
     * creates a menu screen with the given screen menu button selected
     * creates containers for adding things into
     * <p>
     * infoTable is the centre table which should hold the main content
     * leftListTable is a scrollable list on the left
     *
     * @param game
     * @param screen
     * @param currentMenuName
     */
    public Menu(final RealRacer game, final Screen screen, Screens currentMenuName) {
        this.game = game;
        this.currentMenuName = currentMenuName;

        screenWidth = game.getScreenWidth();
        screenHeight = game.getScreenHeight();


        //create rightaligned table for buttons
        mainTable = new Table();
        rightTable = new Table();
        rightBtnsTable = new Table();
        leftTable = new Table();
        leftListTable = new Table();
        middleTable = new Table();
        infoTable = new Table();
        scrollPane = new ScrollPane(leftListTable);

        rightTable.right();
        //rightTable.add(rightBtnsTable);


        leftTable.left();
        //leftTable.add(scrollPaneTable);
        int margin = 15;
        String playerCurrents = game.player.getPlayerCurrents();
        playerCurrentsLabel = new Label(playerCurrents, new LabelStyle(game.font, Color.WHITE));
        mainTable.add();
        mainTable.add();
        mainTable.add();
        mainTable.add(playerCurrentsLabel).padTop(10);
        mainTable.row();
        mainTable.add().width(margin);
        mainTable.add(leftTable).left().expandY();
        mainTable.add().width(margin);
        mainTable.add(middleTable).expand().left();
        mainTable.add().width(margin);
        mainTable.add(rightTable).right();
        mainTable.add().width(margin);
        mainTable.row();
        mainTable.add().height(margin);
        mainTable.setFillParent(true);


        leftTable.add(scrollPane);
        middleTable.add(infoTable);
        rightTable.add(rightBtnsTable);


        leftTable.setBackground(newTDR("menubackground"));
        middleTable.setBackground(newTDR("menubackground"));
        infoTable.pad(30);

		
		

		
		
		/*
         * END LEFTMENU CAR SELECTION LIST
		 */
		
		/*
		 * RIGHT MENU SELECTION LIST
		 */
        for (int x = 0; x < 5; x++) {
            ButtonStyle homebtnstyle = new ButtonStyle();
            String textureName = "";

            switch (x) {
                case 0:
                    textureName = (currentMenuName == Screens.MainMenu) ? "homebtnselected" : "homebtn";
                    menuName = Screens.MainMenu;
                    break;
                case 1:
                    textureName = (currentMenuName == Screens.RaceMenu) ? "racebtnselected" : "racebtn";
                    menuName = Screens.RaceMenu;
                    break;
                case 2:
                    textureName = (currentMenuName == Screens.GarageMenu) ? "garagebtnselected" : "garagebtn";
                    menuName = Screens.GarageMenu;
                    break;
                case 3:
                    textureName = (currentMenuName == Screens.DealerMenu) ? "dealerbtnselected" : "dealerbtn";
                    menuName = Screens.DealerMenu;
                    break;
                case 4:
                    textureName = (currentMenuName == Screens.SettingsMenu) ? "settingsbtnselected" : "settingsbtn";
                    menuName = Screens.SettingsMenu;
                    break;
            }


            homebtnstyle.up = newTDR(textureName);
            final Button b = new Button(homebtnstyle);

            b.addListener(new ChangeListener() {
                Screens btnMenu = menuName;

                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    SoundManager.buttonClicked(btnMenu, game.sounds);
                    game.changeScreen(btnMenu, screen);
                }
            });

            rightBtnsTable.add(b);
            //if we are not on the last
            if (x < 4) {
                rightBtnsTable.row();
                rightBtnsTable.add().height(screenHeight * .09f);
                rightBtnsTable.row();
            } else {
                rightBtnsTable.add().width(screenWidth * .02f);
            }

        }
		
		/*
		 * END RIGHT MENU SELECTION LIST
		 */

    }


    public void updatePlayerCurrents() {

        playerCurrentsLabel.setText(game.player.getPlayerCurrents());
    }

    public TextureRegionDrawable newTDR(String textureName) {
        return new TextureRegionDrawable(new TextureRegion(game.textures.get(textureName)));
    }

    public Actor getMainTable() {
        // TODO Auto-generated method stub
        return null;
    }

}
