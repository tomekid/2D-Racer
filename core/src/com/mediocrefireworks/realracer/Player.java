package com.mediocrefireworks.realracer;


import com.mediocrefireworks.realracer.cars.Car;

import java.util.HashMap;
import java.util.LinkedList;


public class Player {

    public String playerName;
    public LinkedList<Car> cars = new LinkedList<Car>();
    public HashMap<String, Float> times = new HashMap<String, Float>();
    public Car currentCar;
    private int balance;

    public Player(String playerName) {
        balance = 50000;

        this.playerName = playerName;

    }

    public String getCurrentCarName() {

        if (currentCar != null) {
            return currentCar + "";
        }

        return "      ";
    }

    public void addCar(Car car) {
        cars.addFirst(car);
    }

    public void selectCar(Car car) {
        /*int i = cars.indexOf(car);
        if(i==-1){
			System.out.println(" this car isnt in the list of cars" + car);
		}else{
		currentCar = cars.get(i);
		cars.remove(i);
		cars.addFirst(currentCar);
		}*/
        currentCar = car;
    }

    public Car getCurrentCar() {
        return currentCar;
    }

    public String getPlayerCurrents() {

        return "Current Vehicle: " + getCurrentCarName() + "       cr:" + balance;
    }

    public int getCashBalance() {

        return balance;
    }

    public void deductBalance(int d) {
        balance -= d;
    }


}
