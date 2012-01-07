package edu.gricar.wekatime;

import android.app.Application;

public class ApplicationWekatime extends Application {
	public String[] shrani;

	public void onCreate() {
	super.onCreate();
	shrani = new String[3];
	}
}
