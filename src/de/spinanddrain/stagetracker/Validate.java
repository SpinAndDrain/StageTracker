package de.spinanddrain.stagetracker;

import java.awt.Color;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import de.spinanddrain.stagetracker.Tracker.Course;

/**
 * The Validate class interacts with
 * the user and handles operations
 * to load or save files.
 * 
 * @author (c) SpinAndDrain 2020
 */
public final class Validate {
	
	// Static class
	private Validate() {
	}
	
	/**
	 * Opens a YES/NO confirm dialog with the title and message.
	 * 
	 * @param title the title
	 * @param message the message
	 * @return true if the user clicked on "YES"
	 * 
	 * @see JOptionPane#showConfirmDialog
	 */
	public static boolean askYesOrNo(String title, String message) {
		return JOptionPane.showConfirmDialog(null, message, title, JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE, new ImageIcon()) == 0;
	}
	
	/**
	 * Asks the user whether the application should be
	 * closed and does so if the user confirms it.
	 * 
	 */
	public static void validateClosing() {
		if(askYesOrNo("Exit", "Are you sure you want to quit?\nEverything unsaved will be lost."))
			System.exit(0);
	}
	
	/**
	 * Opens a dialog where the user can select a course out
	 * of a selection.
	 * 
	 * @param title the title of the dialog frame
	 * @param message the header message of the dialog frame
	 * @param selection the available course selection
	 * @return the selected course or null if the user closed
	 * 			the frame or selected no course
	 * 
	 * @deprecated This method shows the selection in a
	 * 				impractical way and has been replaced
	 * 				by a improved method.
	 */
	public static Course selectCourse0(String title, String message, Course[] selection) {
		boolean b = selection == null || selection.length == 0;
		return (Course) JOptionPane.showInputDialog(null, message, title, JOptionPane.QUESTION_MESSAGE, new ImageIcon(),
				b ? Course.values() : selection, b ? Course.TPSS : selection[0]);
	}
	
	/**
	 * Opens a dialog where the user can select a course out
	 * of a selection.
	 * 
	 * @param title the title of the dialog frame
	 * @param message the header message of the dialog frame
	 * @param selection the available course selection
	 * @param marker the color that indicates which course
	 * 					is currently selected
	 * @return the selected course or null if the user closed
	 * 			the frame or selected no course
	 */
	public static Course selectCourse(String title, String message, Course[] selection, Color marker) {
		if(selection == null) {
			selection = Course.values();
		}
		Course[] courses = Course.values();
		final JComponent[] in = new JComponent[courses.length + 1];
		in[0] = new JLabel(message);
		AtomicInteger sa = new AtomicInteger(-1);
		for(int i = 1; i < in.length; i++) {
			boolean b = false;
			for(Course c : selection) {
				if(c == courses[i-1]) {
					b = true;
					break;
				}
			}
			in[i] = new JButton(courses[i-1].toString());
			in[i].setBackground(Color.WHITE);
			((JButton) in[i]).setFocusPainted(false);
			if(b) {
				final int x = i;
				((JButton) in[i]).addActionListener((l) -> {
					if(sa.get() == x) {
						in[x].setBackground(Color.WHITE);
						sa.set(-1);
					} else {
						if(sa.get() > 0 && sa.get() < in.length)
							in[sa.get()].setBackground(Color.WHITE);
						in[x].setBackground(marker);
						sa.set(x);
					}
				});
			} else {
				in[i].setEnabled(false);
			}
		}
		if(JOptionPane.showConfirmDialog(null, in, title, JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE, new ImageIcon()) == 0 && sa.get() > 0 && sa.get() < in.length) {
			return courses[sa.get() - 1];
		}
		return null;
	}
	
	/**
	 * Opens a dialog where the user can select a course out
	 * of a selection.
	 * 
	 * @param title the title of the dialog frame
	 * @param message the header message of the dialog frame
	 * @param selection the available course selection
	 * @return the selected course or null if the user closed
	 * 			the frame or selected no course
	 * 
	 * @deprecated This method shows the selection in a
	 * 				impractical way and has been replaced
	 * 				by a improved method.
	 */
	public static Course selectCourse0(String title, String message) {
		return selectCourse0(title, message, null);
	}
	
	public static void showInfo(String title, String info) {
		JOptionPane.showMessageDialog(null, info, title, JOptionPane.INFORMATION_MESSAGE, new ImageIcon());
	}
	
	/**
	 * Opens a save dialog and saves a Tracker session
	 * to the chosen file. This method can open a info dialog
	 * if an error has occured.
	 * 
	 * @param title the title of the save dialog
	 * @param t the Tracker session
	 */
	public static void save(String title, Tracker t) {
		FileNameExtensionFilter fnef = new FileNameExtensionFilter("Stage Tracker Files (.st)", "st", ".");
		JFileChooser jfc = new JFileChooser(System.getProperty("user.dir"));
		jfc.removeChoosableFileFilter(jfc.getFileFilter());
		jfc.addChoosableFileFilter(fnef);
		jfc.setDialogTitle(title);
		if(jfc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
			File file = jfc.getSelectedFile();
			try {
				if(!file.exists())
					file.createNewFile();
				ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
				oos.writeObject(t);
				oos.close();
				showInfo("Saving finished", "File sucessfully saved!");
			} catch (IOException e) {
				e.printStackTrace();
				showInfo("Error", "Something went wrong while saving.\n(" + e.getMessage() + ")");
			}
		}
	}
	
	/**
	 * Opens a open dialog and decompiles the chosen
	 * file.
	 * 
	 * @param title the title of the open dialog
	 * @return the decompiled Tracker session
	 */
	public static Tracker open(String title) {
		FileNameExtensionFilter fnef = new FileNameExtensionFilter("Stage Tracker Files (.st)", "st", ".");
		JFileChooser jfc = new JFileChooser(System.getProperty("user.dir"));
		jfc.removeChoosableFileFilter(jfc.getFileFilter());
		jfc.addChoosableFileFilter(fnef);
		jfc.setDialogTitle(title);
		if(jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			return decompile(jfc.getSelectedFile());
		}
		return null;
	}
	
	/**
	 * Decompiles a .st (StageTracker) file and opens
	 * a info dialog if an error has occured.
	 * 
	 * @param file
	 * @return the decompiled file
	 */
	public static Tracker decompile(File file) {
		try {
			ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)));
			Object o = ois.readObject();
			ois.close();
			if(o instanceof Tracker) {
				return (Tracker) o;
			}
			showInfo("Error", "Invalid or corrupted file.");
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
			showInfo("Error", "Something went wrong while opening.\n(" + e.getMessage() + ")");
		}
		return null;
	}
	
}
