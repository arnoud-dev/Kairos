package main;

import cui.KairosApplication;
import domein.DomeinController;

public class StartUp {
	public static void main(String[] args) {
		DomeinController dc = new DomeinController();
		KairosApplication app = new KairosApplication(dc);
		app.start();
	}
}
