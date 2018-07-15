/*
 * Class: Main.java
 * Author: Lukas Voetsch
 * Created: 03.05.2018
 * Last Change: 03.05.2018
 * 
 * Description: Checks the args[] for language parameter
 * and starts the selected HoneypotFarmTool
 *  -g = GUI , -s = Service
 * */
package main;

import subprograms.*;
import watcher.Controll;

import java.util.Locale;
import java.util.ResourceBundle;

public class Main {
	private static String options = "Options: \t\n[-g GUI]\t\n[-s Service] ";
	private static Boolean work = true; 
	
	public static void main(String[] args) {
		
		//Get language package "en" --> no other options at the time
		String language = new String ("en");		
		Locale currentLocale;
		ResourceBundle messages;		
		currentLocale = new Locale(language);
		messages = ResourceBundle.getBundle("MessageBundle",currentLocale);
		
		System.out.println("/------------------------------------------------------------\\");
		System.out.println("|              HIRSCHMANN - INDUSTRIAL HONEYNET              |");
		System.out.println("|                        TOOLS v1.0                          |");
		System.out.println("|- - - - - - - - - - - - - - -- - - - - - - - - - - - - - - -|");
		System.out.println("|          AUTOREN: Lukas Voetsch und Janek Pelzer           |");
		System.out.println("|            Unter der Leitung von Andreas Kompter           |");
		System.out.println("|          Mit Unterstuetzung von:                           |");
		System.out.println("|           - Alex Pangui                                    |");
		System.out.println("|           - Fabio Oehme                                    |");
		System.out.println("|           - Max Rink                                       |");
		System.out.println("|- - - - - - - - - - - - - - -- - - - - - - - - - - - - - - -|");
		System.out.println("|        Betreuender Professor: Prof. Dr. Tobias Heer        |");
		System.out.println("|- - - - - - - - - - - - - - -- - - - - - - - - - - - - - - -|");
		System.out.println("|          In zusammenarbeit mit: HIRSCHMANN GMBH            |");
		System.out.println("|- - - - - - - - - - - - - - -- - - - - - - - - - - - - - - -|");
		System.out.println("| Enstanden im Rahmen des Projekstudiums: Industrialhoneynet |");
		System.out.println("| der Hochschule Albstadt-Sigmaringen im SS 2018.            |");
		System.out.println("| Alle Rechte vorbehalten.                                   |");
		System.out.println("X------------------------------------------------------------X");
		
		if (args.length == 1) {
			if (args[0].equals("-g")) {
				StartGUI startGUI = new StartGUI(messages);
				startGUI.open();		
			} else if (args[0].equals("-s")) {
				new Controll(work);			
			} else {
				System.out.println(options);
			}
		} else {
			System.out.println(options);
		}
	}
}
