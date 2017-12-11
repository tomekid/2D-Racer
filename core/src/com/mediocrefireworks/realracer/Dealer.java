package com.mediocrefireworks.realracer;

import com.mediocrefireworks.realracer.cars.Car;

import java.util.LinkedList;


public class Dealer {

    private LinkedList<Car> cars;

    public Dealer() {

        cars = new LinkedList<Car>();

    }

    public Dealer(LinkedList<Car> car) {

        this.cars = car;

    }

    public LinkedList<Car> getCarList() {

        return cars;
    }

    public void removeCar(Car car) {

        cars.remove(car);
    }

    public void addCar(Car car) {
        cars.add(car);
    }

}
