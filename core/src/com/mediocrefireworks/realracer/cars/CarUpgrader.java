package com.mediocrefireworks.realracer.cars;

import com.mediocrefireworks.realracer.RealRacer;
import com.mediocrefireworks.realracer.upgradeparts.EnginePart;
import com.mediocrefireworks.realracer.upgradeparts.SuspensionPart;
import com.mediocrefireworks.realracer.upgradeparts.TransmissionPart;
import com.mediocrefireworks.realracer.upgradeparts.TurboPart;
import com.mediocrefireworks.realracer.upgradeparts.TyrePart;
import com.mediocrefireworks.realracer.upgradeparts.UpgradePart;
import com.mediocrefireworks.realracer.upgradeparts.WheelPart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class CarUpgrader {

    // contains currently applied upgrade
    public ArrayList<UpgradePart> currentUpgrades;

    //purchased upgrades key is the part and the value is a list of the purchased part types
    public ArrayList<UpgradePart> purchasedUpgrades;

    //partspricelist
    private HashMap<UpgradePart, Integer> priceList;

    private RealRacer game;

    private TorqueCurve tq;

    private Car car;

    public CarUpgrader(RealRacer game, Car car) {
        this.game = game;
        this.car = car;

        //TODO this setup should be in car or in seperate file
        LinkedHashMap<Integer, Float> tqMap = new LinkedHashMap<Integer, Float>();
        tqMap.put(0, 0f);
        tqMap.put(500, 100f);
        tqMap.put(1500, 178f);
        tqMap.put(2000, 171f);
        tqMap.put(2514, 172f);
        tqMap.put(3000, 176f);
        tqMap.put(3500, 178f);
        tqMap.put(3800, 180.4f);
        tqMap.put(4012, 180.6f);
        tqMap.put(4212, 185.1f);
        tqMap.put(4508, 185f);
        tqMap.put(5011, 195.4f);
        tqMap.put(5509, 205.2f);
        tqMap.put(5804, 215.4f);
        tqMap.put(6009, 225.8f);
        tqMap.put(6210, 240.4f);
        tqMap.put(6804, 260.4f);
        tqMap.put(7000, 270f);
        tqMap.put(8000, 150f);

        this.tq = new TorqueCurve(tqMap);

		/*tqMap.put(0, 0f);
        tqMap.put(500, 60f);
		tqMap.put(1500, 479f);
		tqMap.put(2000, 530f);
		tqMap.put(2514, 533f);
		tqMap.put(3000, 546f);
		tqMap.put(3500, 580f);
		tqMap.put(3800, 585f);
		tqMap.put(4012, 586f);
		tqMap.put(4212, 584f);
		tqMap.put(4508, 584f);
		tqMap.put(5011, 570f);
		tqMap.put(5509, 562f);
		tqMap.put(5804, 544f);
		tqMap.put(6009, 527f);
		tqMap.put(6210, 510f);
		tqMap.put(6804, 480f);
		tqMap.put(7000, 400f);
		tqMap.put(8000, 300f);*/
		
	
		
		/* !IMPORTANT! stock ones at $0 */
        priceList = new HashMap<UpgradePart, Integer>();
        priceList.put(EnginePart.Bore, 1500);
        priceList.put(EnginePart.PortPolish, 500);
        priceList.put(EnginePart.Tune, 2000);
        priceList.put(EnginePart.ECU, 700);
        priceList.put(EnginePart.EngineComponents, 5000);
        priceList.put(SuspensionPart.Stock, 0);
        priceList.put(SuspensionPart.Sports, 2000);
        priceList.put(SuspensionPart.Racing, 4500);
        priceList.put(TurboPart.None, 0);
        priceList.put(TurboPart.Stock, 0);
        priceList.put(TurboPart.T25, 4000);
        priceList.put(TurboPart.T3, 7000);
        priceList.put(TurboPart.T4, 10000);
        priceList.put(TurboPart.SportsIntercooler, 1000);
        priceList.put(TurboPart.RacingIntercooler, 5000);
        priceList.put(TransmissionPart.Stock, 0);
        priceList.put(TransmissionPart.Racing, 4500);
        priceList.put(TyrePart.Stock, 0);
        priceList.put(TyrePart.Sports, 1200);
        priceList.put(TyrePart.Racing, 6000);
        priceList.put(WheelPart.Stock, 0);

        //add all wheels to pricelist with same price
        for (int x = 1; x < WheelPart.values().length; x++) {
            priceList.put(WheelPart.values()[x], 4000);
        }

        currentUpgrades = new ArrayList<UpgradePart>();
        //TODO finish putting prices
        purchasedUpgrades = new ArrayList<UpgradePart>();
        //TODO finish purchaseing stock parts
        purchasePart(SuspensionPart.Stock);
        fitPart(SuspensionPart.Stock);
        purchasePart(TurboPart.Stock);
        purchasePart(TurboPart.None);
        purchasePart(TransmissionPart.Stock);
        fitPart(TransmissionPart.Stock);
        purchasePart(TyrePart.Stock);
        fitPart(TyrePart.Stock);
        purchasePart(WheelPart.Stock);
        fitPart(WheelPart.Stock);


    }


    /**
     * returns the first found applied part of a given parttype
     * if no part is applied ie engine where it is not
     * necesary to have a part applied we return first part.
     *
     * @param upgradePartType
     * @return
     */

    public UpgradePart getSelectedUpgradePart(UpgradePart.Type upgradePartType) {
		
		/*looks for the first upgrade part with the same type
		 * if found it returns that part
		 * else throws a nullpointerexception
		 */
        for (UpgradePart p : currentUpgrades) {
            if (upgradePartType.equals(p.getPartType())) {
                return p;
            }
        }

        return upgradePartType.getFirstUpgradePart();

    }

    /**
     * checks to see if the given part is applied
     *
     * @param upgradePart
     * @return
     */
    public boolean isPartApplied(UpgradePart upgradePart) {


        return currentUpgrades.contains(upgradePart);


    }


    public boolean isPurchased(UpgradePart upgradePart) {


        return purchasedUpgrades.contains(upgradePart);
    }


    public int getPrice(UpgradePart p) {
        if (priceList.get(p) != null) {
            return priceList.get(p);
        } else {
            return 9999999;
        }
    }


    /**
     * adds the part to the list of purchased parts
     *
     * @param p
     */
    public void purchasePart(UpgradePart p) {

        int cost = getPrice(p);

        if (game.player.getCashBalance() >= cost) {

            game.player.deductBalance(cost);


            purchasedUpgrades.add(p);

        }

    }


    /**
     * adds the part to the car
     * removes any mutually exclusive parts
     *
     * @param p
     */
    public void fitPart(UpgradePart p) {
	

			/*
			 * if there are parts in this parts exclusive list
			 * already added then find them and remove them
			 * also remove them from the torque curve if needed
			 */
        for (UpgradePart part : p.getExclusiveList(p)) {

            if (currentUpgrades.contains(part)) {
                currentUpgrades.remove(part);
                if (p.isTorquePart()) {
                    tq.removeTorqueCurve(getCurve(part));
                }
            }

        }
        currentUpgrades.add(p);

        switch (p.getPartType()) {
            case Engine:
                break;
            case Suspension:
                break;
            case Transmission:
                break;
            case Turbo:
                break;
            case Tyres:
                fitTyre((TyrePart) p);
                break;
            case Wheels://TODO
                break;
            default:
                break;

        }

			

		
		
		
		/*we check to find torque curve,
		 * if null then this should not affect
		 * the torque eg suspension
		 */

        if (p.isTorquePart()) {
            tq.applyTorqueCurve(getCurve(p));
        }

    }

    public float getMaxBhpFromFittingPart(UpgradePart p) {


        LinkedHashMap<Integer, Float> testmap = new LinkedHashMap<Integer, Float>();
        testmap.putAll(tq.getMap());

        TorqueCurve testtq = new TorqueCurve(testmap);
		/*
		 * remove any part that is apllied in the parts
		 * exclusive list including itself
		 */
        for (UpgradePart part : p.getExclusiveList(p)) {
            if (part.isTorquePart()) {
                if (isPartApplied(part)) {

                    testtq.removeTorqueCurve(getCurve(part));
                }

            }

        }
		
		/*
		 * if its a torque part and
		 * is not currently applied to real
		 * car then we need to apply the 
		 * curve to the test
		 */

        if (p.isTorquePart()) {
            if (!isPartApplied(p)) {
                testtq.applyTorqueCurve(getCurve(p));
            }

        }


        return testtq.getMaxBhp();
    }


    private TorqueCurve getCurve(UpgradePart p) {
        switch (p.getPartType()) {
            case Engine:
                return getEnginePartCurve((EnginePart) p);
            case Suspension:
                break;
            case Transmission:
                break;
            case Turbo:
                return getTurboPartCurve((TurboPart) p);
            case Tyres:
                break;
            case Wheels:
                break;
            default:
                break;

        }

        throw new NullPointerException("you have tried to retrieve a curve for part:" +
                p.getPartType() + " . " + p + "/n" +
                "but there isnt a case for that part");
    }


    /**
     * removes the part from the car
     *
     * @param p
     */
    public void unfitPart(UpgradePart p) {


        if (currentUpgrades.contains(p)) {
            currentUpgrades.remove(p);
			/*we check to find torque curve,
			 * if null then this should not affect
			 * the torque eg suspension
			 */

            if (getCurve(p) != null) {
                tq.removeTorqueCurve(getCurve(p));
            }

        }


    }


    public TorqueCurve getEnginePartCurve(EnginePart p) {

        LinkedHashMap<Integer, Float> tqMap = new LinkedHashMap<Integer, Float>();


        switch (p) {
            case Bore:

                tqMap.put(0, 0f);
                tqMap.put(500, 1f);
                tqMap.put(1500, 2f);
                tqMap.put(4000, 2f);
                tqMap.put(7000, 2f);
                tqMap.put(9000, 2f);
                tqMap.put(12000, 0f);


                break;
            case ECU:

                tqMap.put(0, 0f);
                tqMap.put(500, 2f);
                tqMap.put(1500, 3f);
                tqMap.put(4000, 3f);
                tqMap.put(7000, 5f);
                tqMap.put(9000, 4f);
                tqMap.put(12000, 0f);

                break;
            case EngineComponents:

                tqMap.put(0, 0f);
                tqMap.put(500, 3f);
                tqMap.put(1500, 8f);
                tqMap.put(4000, 10f);
                tqMap.put(7000, 12f);
                tqMap.put(9000, 10f);
                tqMap.put(12000, 0f);

                break;
            case PortPolish:
                tqMap.put(0, 0f);
                tqMap.put(500, 3f);
                tqMap.put(1500, 4f);
                tqMap.put(4000, 4f);
                tqMap.put(7000, 4f);
                tqMap.put(9000, 4f);
                tqMap.put(12000, 0f);
                break;
            case Tune:
                tqMap.put(0, 0f);
                tqMap.put(500, 3f);
                tqMap.put(1500, 4f);
                tqMap.put(4000, 4f);
                tqMap.put(7000, 5f);
                tqMap.put(9000, 7f);
                tqMap.put(12000, 0f);
                break;
            default:
                break;

        }

        return new TorqueCurve(tqMap);
    }


    public TorqueCurve getTurboPartCurve(TurboPart p) {
        LinkedHashMap<Integer, Float> tqMap = new LinkedHashMap<Integer, Float>();

        switch (p) {
            case None:
                tqMap.put(0, -10f);
                tqMap.put(12000, -10f);
                break;
            case RacingIntercooler:
                tqMap.put(0, 0f);
                tqMap.put(500, 4f);
                tqMap.put(1000, 10f);
                tqMap.put(2000, 10f);
                tqMap.put(3000, 11f);
                tqMap.put(4000, 15f);
                tqMap.put(5000, 19f);
                tqMap.put(7000, 25f);
                tqMap.put(9000, 24f);
                tqMap.put(12000, 0f);
                break;
            case SportsIntercooler:
                tqMap.put(0, 0f);
                tqMap.put(500, 4f);
                tqMap.put(1000, 8f);
                tqMap.put(2000, 7f);
                tqMap.put(3000, 8f);
                tqMap.put(4000, 10f);
                tqMap.put(5000, 12f);
                tqMap.put(7000, 14f);
                tqMap.put(9000, 10f);
                tqMap.put(12000, 0f);
                break;
            case Stock:
                tqMap.put(0, 0f);
                tqMap.put(12000, 0f);
                break;
            case T25:
                tqMap.put(0, 0f);
                tqMap.put(500, 4f);
                tqMap.put(1000, 10f);
                tqMap.put(2000, 35f);
                tqMap.put(3000, 50f);
                tqMap.put(4000, 60f);
                tqMap.put(5000, 64f);
                tqMap.put(7000, 66f);
                tqMap.put(9000, 67f);
                tqMap.put(12000, 0f);
                break;
            case T3:
                tqMap.put(0, 0f);
                tqMap.put(500, 2f);
                tqMap.put(1000, 10f);
                tqMap.put(2000, 25f);
                tqMap.put(3000, 27f);
                tqMap.put(4000, 45f);
                tqMap.put(5000, 65f);
                tqMap.put(7000, 80f);
                tqMap.put(9000, 100f);
                tqMap.put(12000, 0f);
                break;
            case T4:
                tqMap.put(0, 0f);
                tqMap.put(500, 1f);
                tqMap.put(1000, 3f);
                tqMap.put(2000, 10f);
                tqMap.put(3000, 15f);
                tqMap.put(4000, 30f);
                tqMap.put(5000, 60f);
                tqMap.put(6000, 220f);
                tqMap.put(7000, 250f);
                tqMap.put(9000, 300f);
                tqMap.put(12000, 0f);
                break;
            default:
                break;

        }

        return new TorqueCurve(tqMap);
    }


    public TorqueCurve getTorqueCurve() {

        return tq;
    }


    public void fitTyre(TyrePart p) {
        if (car.frontWheel != null) {
            System.out.println(" set friction " + p.getFriction());
            car.frontWheel.setFriction(p.getFriction());
            car.rearWheel.setFriction(p.getFriction());
        }


    }


}
