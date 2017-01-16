package com.fattytour.www;

import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Junjie on 4/01/2017.
 */

public class NotificationCenter {
    private static NotificationCenter _instance;

    private HashMap<String, CustomObservable> observables;

    private NotificationCenter(){
        observables = new HashMap<String, CustomObservable>();
    }

    public static synchronized NotificationCenter defaultCenter(){
        if(_instance == null)
            _instance = new NotificationCenter();
        return _instance;
    }

    public synchronized void addObserver(String notification, Observer observer){
        CustomObservable observable = observables.get(notification);
        if (observable==null) {
            observable = new CustomObservable();
            observables.put(notification, observable);
        }
        observable.addObserver(observer);
    }

    public synchronized void removeObserver(String notification, Observer observer){
        CustomObservable observable = observables.get(notification);
        if (observable!=null) {
            observable.deleteObserver(observer);
        }
    }

    public synchronized void postNotification(String notification, Object object){
        CustomObservable observable = observables.get(notification);
        if (observable!=null) {
            observable.notifyObservers(object);
        }
    }
}

class CustomObservable extends Observable{
    public void notifyObservers(Object object){
        setChanged();
        super.notifyObservers(object);
    }
}
