package de.spinanddrain.stagetracker;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * The ContentPane class holds all components
 * for the graphical user interface.
 * 
 * @author (c) SpinAndDrain 2020
 */
public class ContentPane extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6285086942122323001L;

	private Navigation nav;
	private Tracker tracker;
	private JLabel copyinfo;
	
	/**
	 * Constructs a new ContentPane with a new Navigation,
	 * Tracker and JLabel component.
	 * 
	 */
	public ContentPane() {
		setLayout(new BorderLayout());
		add(nav = new Navigation(), BorderLayout.NORTH);
		add(tracker = new Tracker(), BorderLayout.CENTER);
		add(copyinfo = new JLabel("(c) SpinAndDrain 2020"), BorderLayout.SOUTH);
	}
	
	/**
	 * 
	 * @return the Navigation component of this instance
	 */
	public Navigation getNavigation() {
		return nav;
	}
	
	/**
	 * 
	 * @return the Tracker component of this instance
	 */
	public Tracker getTracker() {
		return tracker;
	}
	
	/**
	 * 
	 * @return the JLabel component of this instance
	 */
	public JLabel getCopyrightInformation() {
		return copyinfo;
	}
	
}
