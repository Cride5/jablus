package uk.co.crider.jablus.utils;

import javax.swing.LookAndFeel;
import javax.swing.UIManager;

public class UIManagerDefaults {
	public static void main(String[] args) {
		System.out.println("Default L&F:");
		System.out.println("  " + UIManager.getLookAndFeel().getName());

		UIManager.LookAndFeelInfo[] inst = UIManager.getInstalledLookAndFeels();
		System.out.println("Installed L&Fs: ");
		for (int i=0;i<inst.length;i++) {
			System.out.println("  " + inst[i].getName());
		}

		LookAndFeel[] aux = UIManager.getAuxiliaryLookAndFeels();
		System.out.println("Auxiliary L&Fs: ");
		if (aux != null) {
			for (int i=0;i<aux.length;i++) {
				System.out.println("  " + aux[i].getName());
			}
		}
		else {System.out.println("  <NONE>");}

		System.out.println("Cross-Platform:");
		System.out.println("  " + UIManager.getCrossPlatformLookAndFeelClassName());

		System.out.println("System:");
		System.out.println("  " + UIManager.getSystemLookAndFeelClassName());

		System.exit(0);
	}
}