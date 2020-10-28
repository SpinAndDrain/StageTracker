package de.spinanddrain.stagetracker;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import de.spinanddrain.stagetracker.Tracker.Course;

/**
 * The Navigation class represents a section in
 * the graphical user interface with a list
 * of Buttons to control the program.
 * 
 * @author (c) SpinAndDrain 2020
 */
public class Navigation extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3624369570880461373L;

	private JButton add, edit, clear, save, load, exit;
	
	/**
	 * Constructs a new Navigation instance with a
	 * add, edit, clear, save, load and exit JButton.
	 * 
	 */
	public Navigation() {
		setLayout(new BorderLayout());
		setBackground(Color.LIGHT_GRAY);

		add = createButton("+", (l) -> {
			Main main = Main.getInstance();
			Tracker t = main.getContentPane().getTracker();
			Course[] ca = t.getAvailableEnteringCourses();
			Course[] cb = t.getAvailableHidingCourses();
			if(ca.length <= 1) {
				Validate.showInfo("All courses awarded", "All courses were awarded. Delete relationships to form new ones.");
				return;
			}
			Course a = Validate.selectCourse("Select Course", "Please select the course you went to:", ca, Color.GREEN);
			if(a == null) return;
			Course b = Validate.selectCourse("Select Course", "Please select the course you ended up in:", cb, Color.RED);
			if(b == null) return;
			t.updateSelected(a, b);
			if(ca.length == 2) {
				t.updateSelected(t.getAvailableEnteringCourses()[0], t.getAvailableHidingCourses()[0]);
			}
			main.render();
		});
		edit = createButton("-", (l) -> {
			Main main = Main.getInstance();
			main.getContentPane().getTracker().blankSelected();
			main.render();
		});
		exit = createButton("Exit", (l) -> Validate.validateClosing());
		clear = createButton("Clear", (l) -> {
			if(Validate.askYesOrNo("Clear", "This will clear all relations.\nAre you sure?")) {
				Main.getInstance().getContentPane().getTracker().blankAll();
			}
		});
		save = createButton("Save", (l) -> {
			Validate.save("Save file", Main.getInstance().getContentPane().getTracker());
		});
		load = createButton("Load", (l) -> {
			Tracker t = Validate.open("Load file");
			if(t == null)
				return;
			Main main = Main.getInstance();
			main.getContentPane().getTracker().updateData(t);
			main.render();
		});
		
		JPanel left = new JPanel();
		left.setBackground(Color.LIGHT_GRAY);
		left.setLayout(new FlowLayout(FlowLayout.LEFT));
		left.add(add);
		left.add(edit);
		left.add(clear);
		left.add(save);
		left.add(load);
		
		JPanel right = new JPanel();
		right.setBackground(Color.LIGHT_GRAY);
		right.setLayout(new FlowLayout(FlowLayout.RIGHT));
		right.add(exit);
		
		add(left, BorderLayout.LINE_START);
		add(right, BorderLayout.LINE_END);
	}
	
	/**
	 * Constructs a JButton with default settings.
	 * 
	 * @param title the title of the JButton
	 * @param l the click listener of the JButton
	 * @return the new constructed JButton
	 */
	private JButton createButton(String title, ActionListener l) {
		JButton jbtn = new JButton(title);
		jbtn.setBackground(Color.WHITE);
		jbtn.setFocusPainted(false);
		jbtn.addActionListener(l);
		return jbtn;
	}
	
	/**
	 * 
	 * @return the add JButton of this instance
	 */
	public JButton getButtonAdd() {
		return add;
	}
	
	/**
	 * 
	 * @return the edit JButton of this instance
	 */
	public JButton getButtonEdit() {
		return edit;
	}
	
	/**
	 * 
	 * @return the exit JButton of this instance
	 */
	public JButton getButtonExit() {
		return exit;
	}
	
	/**
	 * 
	 * @return the clear JButton of this instance
	 */
	public JButton getButtonClear() {
		return clear;
	}
	
	/**
	 * 
	 * @return the save JButton of this instance
	 */
	public JButton getButtonSave() {
		return save;
	}
	
	/**
	 * 
	 * @return the load JButton of this instance
	 */
	public JButton getButtonLoad() {
		return load;
	}
	
}
