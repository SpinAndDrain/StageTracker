package de.spinanddrain.stagetracker;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

/**
 * The Main class of StageTracker.
 * 
 * @author (c) SpinAndDrain 2020
 */
public class Main extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4668240083538498312L;

	private static Main instance;
	
	/**
	 * Creates a new instance and opens the main frame
	 * of the StageTracker application. The static field
	 * {@link Main#instance} is updated to this new instance.
	 * 
	 * @param open a file which gets automatically open with the
	 * 				program (should be in .st format)
	 */
	public Main(File open) {
		instance = this;
		setContentPane(new ContentPane());
		setTitle("StageTracker");
		setIconImage(loadIcon());
		setMinimumSize(new Dimension(500, 800));
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setLocationRelativeTo(null);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				Validate.validateClosing();
			}
		});
		if(open != null) {
			Tracker t = Validate.decompile(open);
			if(t == null)
				return;
			getContentPane().getTracker().updateData(t);
			render();
		}
	}

	public ContentPane getContentPane() {
		return (ContentPane) super.getContentPane();
	}
	
	/**
	 * Visualises all changes done on the content pane since
	 * the last call of this method or the initialization of the
	 * program.
	 * 
	 */
	public void render() {
		revalidate();
		repaint();
	}
	
	/**
	 * Loads the default application icon.
	 * 
	 * @return the default application icon
	 */
	private Image loadIcon() {
		try {
			return ImageIO.read(getClass().getResourceAsStream("res/icon.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 
	 * @return the last created instance of this class
	 */
	public static Main getInstance() {
		return instance;
	}
	
	public static void main(String[] args) {
		File open = null;
		if(args.length > 0) {
			File f = new File(args[0]);
			if(f.exists()) {
				open = f;
			}
		}
		new Main(open).setVisible(true);
	}
	
}
