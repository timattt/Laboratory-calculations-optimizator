/**
 * 
 */
package main;

import gui.GUI;

/**
 * @author timat
 *
 */
public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			GUI.init();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
