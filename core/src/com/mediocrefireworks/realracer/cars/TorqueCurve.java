package com.mediocrefireworks.realracer.cars;

import com.mediocrefireworks.realracer.upgradeparts.UpgradePart;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

public class TorqueCurve {

    private LinkedHashMap<Integer, Float> originalTQ;
    private LinkedHashMap<Integer, Float> tq;
    //TODOholds all the torque curves of all the upgrades
    private HashMap<UpgradePart, TorqueCurve> upgradeCurves;

    /**
     * creates a torque curve that you
     * can access intermediate values
     *
     * @param Map<Integer,Integer> tq
     */
    public TorqueCurve(LinkedHashMap<Integer, Float> tq) {

        this.tq = tq;
        originalTQ = new LinkedHashMap<Integer, Float>();
        originalTQ.putAll(tq);
        upgradeCurves = new HashMap<UpgradePart, TorqueCurve>();

    }

    public LinkedHashMap<Integer, Float> getMap() {
        return tq;
    }

    /**
     * takes an x (rpm) value and gives the estimated
     * value for y (maxtorque) on the torque curve
     *
     * @param int rpm
     * @return int maxTorque
     */
    public float getTorque(float rpm) {
        rpm = Math.abs(rpm);
        float maxTorque = 0;

        //keep the last value
        int lastKey = 0;
        float lastValue = 0;
        //key is the rpm and the value is the torque
        /*
         * go through all the entries and find the one
		 * closest 2 values and interpolate to find torque
		 */
        for (Entry<Integer, Float> torque : tq.entrySet()) {

            //	System.out.println(" rpm " +rpm + "  torque find " + torque + " torque rpm "+torque.getKey());
            //if the rpm
            if (rpm < torque.getKey()) {
                int rpmDif = torque.getKey() - lastKey;
                float torqueDif = torque.getValue() - lastValue;


                //y = mx+c
                //		System.out.println(" max " + ((float) torqueDif)/(rpmDif) *(rpm- lastKey) + lastValue);
                return torqueDif / (rpmDif) * (rpm - lastKey) + lastValue;

            }

            lastKey = torque.getKey();
            lastValue = torque.getValue();
        }
        return maxTorque;
    }

    public float getBHPFromRPM(float rpm) {

        float bhp = (rpm * getTorque(rpm)) / 7120;

        return bhp;
    }


    /**
     * go through the values in the torque graph and
     * find the biggest  BHP value
     *
     * @return
     */
    public float getMaxBhp() {

        float bhp = 0;

        for (Integer rpm : tq.keySet()) {

            float currentBHP = getBHPFromRPM(rpm);
            if (currentBHP > bhp) {
                bhp = currentBHP;
            }

        }

        return bhp;
    }

    /**
     * find the max BHP from torque curve
     * and given torque curve
     *
     * @param c
     * @return
     */
    public float getMaxBhp(boolean remove, TorqueCurve curve) {
        LinkedHashMap<Integer, Float> map = new LinkedHashMap<Integer, Float>();
        map.putAll(tq);
        TorqueCurve newTQ = new TorqueCurve(map);
        if (remove) {
            newTQ.removeTorqueCurve(curve);
        } else {
            newTQ.applyTorqueCurve(curve);
        }
        return newTQ.getMaxBhp();
    }

    /**
     * returns the highest torque value
     *
     * @return
     */
    public float getMaxTorque() {

        float maxTorque = 0;

        for (float torque : tq.values()) {
            if (torque > maxTorque) {
                maxTorque = torque;
            }

        }
        return maxTorque;
    }

    public float getMaxTorqueRPM() {

        float maxTorque = 0;
        float RPM = 0;
        for (Entry<Integer, Float> entry : tq.entrySet()) {

            if (entry.getValue() > maxTorque) {
                maxTorque = entry.getValue();
                RPM = entry.getKey();
            }

        }
        return RPM;

    }

    /**
     * adds an upgrade to the torque curve
     * and adds the curve to the list of upgrades
     *
     * @param part
     * @param upgradeCurve
     */
    public void applyTorqueCurve(TorqueCurve curve) {

        for (Entry<Integer, Float> entry : tq.entrySet()) {
            int rpm = entry.getKey();
            Float torque = entry.getValue();
			/*TODO here we are just adding the torque curves
			 * but will need switch for different parts
			 * or may have car spacific upgrades 
			 */

            torque = torque + curve.getTorque(rpm);
            entry.setValue(torque);

        }

    }

    /**
     * removes upgrade from the torque curve
     * maybe need start from original and re add all parts
     * so user cant repeatedly click remove add and cause
     * rounding to increase or decrease hp
     *
     * @param part
     */
    public void removeTorqueCurve(TorqueCurve curve) {
        if (curve != null) {
            for (Entry<Integer, Float> entry : tq.entrySet()) {
                int rpm = entry.getKey();
                Float torque = entry.getValue();
				/*TODO here we are just adding the torque curves
				 * but will need switch for different parts
				 * or may have car spacific upgrades 
				 */

                entry.setValue(torque - curve.getTorque(rpm));


            }
        }
    }


}
