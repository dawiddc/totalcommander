package com.dawiddc.totalcommander.helpers;

import java.util.Locale;
import java.util.Observable;

public class BundleManager extends Observable {
    private Locale currentLocale;

    public BundleManager(Locale locale) {
        this.currentLocale = locale;
    }

    public void changeAndNotify(Locale locale) {
        this.currentLocale = locale;
        this.setChanged();
        this.notifyObservers(currentLocale);
    }
}
