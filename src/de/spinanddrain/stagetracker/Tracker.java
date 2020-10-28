package de.spinanddrain.stagetracker;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Stack;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

/**
 * The Tracker class represents a section in
 * the graphical user interface with a list
 * of Relations.
 * 
 * @author (c) SpinAndDrain 2020
 */
public class Tracker extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -9084347181556207344L;

	/**
	 * The default colors to mark course loops.
	 */
	private static final Color[] colors;
	
	static {
		colors = new Color[] {
				new Color(191, 205, 224),
				Color.GREEN,
				new Color(107, 142, 35),
				Color.ORANGE,
				Color.CYAN,
				Color.PINK,
				new Color(125, 255, 176),
				Color.YELLOW,
				new Color(0, 255, 191),
				new Color(0, 170, 255),
				Color.WHITE,
				new Color(255, 195, 125),
				new Color(173, 255, 47),
				new Color(0, 206, 209),
				new Color(70, 130, 180),
				new Color(100, 149, 237),
				new Color(106, 90, 205),
				new Color(186, 85, 211),
				new Color(221, 160, 221),
				new Color(205, 92, 92),
				new Color(238, 232, 170),
				new Color(46, 139, 87),
				new Color(178, 34, 34),
				new Color(205, 133, 63)
		};
	}
	
	private Relation[] relations;
	private int counter;
	private ArrayList<Course> usedA, usedB;
	private ArrayList<Relation[]> loops;
	
	/**
	 * Constructs a new Tracker instance with 24
	 * Relation placeholders.
	 * 
	 */
	public Tracker() {
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		counter = 0;
		loops = new ArrayList<>();
		usedA = new ArrayList<>();
		usedB = new ArrayList<>();
		relations = new Relation[24];
		for(int i = 0; i < relations.length; i++) {
			final int c = i;
			relations[i] = new Relation();
			relations[i].addMouseListener(new MouseListener() {
				@Override
				public void mouseReleased(MouseEvent e) {
					updateCounter(c);
				}
				@Override
				public void mousePressed(MouseEvent e) {
				}
				@Override
				public void mouseExited(MouseEvent e) {
				}
				@Override
				public void mouseEntered(MouseEvent e) {
				}
				@Override
				public void mouseClicked(MouseEvent e) {
				}
			});
			add(relations[i]);
		}
		updateRelation(23, Course.BITS, Course.BITS);
		relations[counter].setSelected();
	}
	
	/**
	 * Synchronizes the data of a external Tracker
	 * with this instance.
	 * 
	 * @param external the external Tracker
	 */
	protected void updateData(Tracker external) {
		blankAll();
		for(int i = 0; i < relations.length; i++) {
			Relation ext = external.relations[i];
			if(external.usedA.contains(ext.enter) && external.usedB.contains(ext.hiding))
				updateRelation(i, ext.enter, ext.hiding);
		}
		loops = external.loops;
		matchLoops();
	}
	
	/**
	 * Blanks all Relation placeholders and loops.
	 * 
	 */
	public void blankAll() {
		for(int i = 0; i < relations.length; i++) {
			blankRelation(i);
		}
		Main.getInstance().render();
	}
	
	/**
	 * Updates a Relation placeholder at the position
	 * to the specified courses.
	 * 
	 * @param id the position
	 * @param enter the entered course
	 * @param hiding the course that was actually behind the entered one
	 */
	public void updateRelation(int id, Course enter, Course hiding) {
		relations[id].setCourses(enter, hiding);
		relations[id].updateRelation();
		usedA.add(enter);
		usedB.add(hiding);
		matchLoops();
	}
	
	/**
	 * Blanks a Relation placeholder at a specific
	 * position.
	 * 
	 * @param id the position
	 */
	public void blankRelation(int id) {
		relations[id].removeAll();
		relations[id].add(relations[id].a = Relation.createField(" ", null));
		usedA.remove(relations[id].enter);
		usedB.remove(relations[id].hiding);
		matchLoops();
	}
	
	/**
	 * Updates the currently selected Relation placeholder
	 * to the specified courses.
	 * 
	 * @param enter the entered course
	 * @param hiding the course that was actually behind the entered one
	 */
	public void updateSelected(Course enter, Course hiding) {
		updateRelation(counter, enter, hiding);
		updateCounter(counter + 1);
	}
	
	/**
	 * Blanks the currently selected Relation placeholder and its loops.
	 * 
	 */
	public void blankSelected() {
		blankRelation(counter);
//		updateCounter(counter - 1);
	}
	
	/**
	 * Visualises all changes done on loops since
	 * the last call of this method or the initialization
	 * of the program.
	 * 
	 */
	public void drawLoops() {
		for(int i = 0; i < loops.size(); i++) {
			for(int x = 0; x < loops.get(i).length; x++) {
				loops.get(i)[x].setBackground(colors[i]);
			}
		}
	}
	
	/**
	 * Calculates and draws all loops for the
	 * current Relations.
	 * 
	 */
	public void matchLoops() {
		for(Relation r : relations)
			r.setBackground(Color.WHITE);
		HashSet<Relation> rels = new HashSet<>();
		for(int i = 0; i < relations.length; i++) {
			if(relations[i].getComponents().length > 1)
				rels.add(relations[i]);
		}
		loops.clear();
		Stack<Relation> loop = new Stack<>();
		for(Relation r : rels) {
			if(r.enter == r.hiding) {
				loops.add(new Relation[] {r});
				continue;
			}
			loop.clear();
			loop.push(r);
			for(Relation rs : rels) {
				if(r == rs) continue;
				if(loop.peek().hiding == rs.enter)
					loop.push(rs);
			}
			if(loop.firstElement().enter == loop.peek().hiding) {
				loops.add(loop.toArray(new Relation[loop.size()]));
			}
		}
		drawLoops();
	}
	
	/**
	 * 
	 * @return all courses that haven't been entered yet
	 */
	public Course[] getAvailableEnteringCourses() {
		Course[] origin = Course.values();
		Course[] available = new Course[origin.length - usedA.size()];
		for(int i = 0, c = 0; i < origin.length; i++) {
			if(!usedA.contains(origin[i]))
				available[c++] = origin[i];
		}
		return available;
	}
	
	/**
	 * 
	 * @return all courses that are not yet behind another
	 */
	public Course[] getAvailableHidingCourses() {
		Course[] origin = Course.values();
		Course[] available = new Course[origin.length - usedB.size()];
		for(int i = 0, c = 0; i < origin.length; i++) {
			if(!usedB.contains(origin[i]))
				available[c++] = origin[i];
		}
		return available;
	}
	
	/**
	 * Updates the counter and visualises the new
	 * selected Relation placeholder with a black
	 * border.
	 * 
	 * @param x the new counter position
	 */
	private void updateCounter(int x) {
		if(counter == x)
			return;
		if(counter < 0 || counter >= relations.length) {
			for(Relation r : relations)
				r.setNormal();
		} else
			relations[counter].setNormal();
		counter = x;
		if(counter >= relations.length)
			counter = 0;
		else if(counter < 0)
			counter = relations.length - 1;
		relations[counter].setSelected();
		Main.getInstance().render();
	}
	
	/**
	 * A visual placeholder representing a relation
	 * between a entered course and the course that
	 * was actually behind the entered one.
	 * The placeholder can visualize the relation or
	 * be blank.
	 */
	public static class Relation extends JPanel {

		/**
		 * 
		 */
		private static final long serialVersionUID = 2052108032143075994L;
		
		private Course enter, hiding;
		private JLabel a, b, c;
		
		{
			setNormal();
			setLayout(new BorderLayout());
		}
		
		/**
		 * Creates a new Relation placeholder.
		 * 
		 */
		public Relation() {
			add(a = createField(" ", null));
		}
		
		/**
		 * Creates and fills a new Relation placeholder
		 * with the specified courses.
		 * 
		 * @param enter the entered course
		 * @param hiding the course that was actually behind the entered one
		 */
		public Relation(Course enter, Course hiding) {
			this();
			setCourses(enter, hiding);
			updateRelation();
		}
		
		/**
		 * Resets the border.
		 * 
		 */
		public void setNormal() {
			setBorder(new LineBorder(Color.GRAY, 2));
		}
		
		/**
		 * Marks this Relation as selected
		 * with a black border.
		 * 
		 */
		public void setSelected() {
			setBorder(new LineBorder(Color.BLACK, 2));
		}
		
		/**
		 * Updates this Relation to the current
		 * enter and hiding course.
		 * 
		 */
		public void updateRelation() {
			removeAll();
			add(a = createField((enter.isSecret() ? "" : "(" + enter.getCourseID() + ") ") + enter.getFullName(),
					Color.BLACK), BorderLayout.LINE_START);
			add(b = createField("", null), BorderLayout.CENTER);
			add(c = createField((hiding.isSecret() ? "" : "(" + hiding.getCourseID() + ") ") + hiding.getFullName(),
					Color.red), BorderLayout.LINE_END);
		}
		
		/**
		 * Updates the enter and hiding course.
		 * 
		 * @param enter the new entered course
		 * @param hiding the new course that was actually behind the new entered one
		 */
		public void setCourses(Course enter, Course hiding) {
			this.enter = enter;
			this.hiding = hiding;
		}
		
		/**
		 * 
		 * @return the entered course
		 */
		public Course getEnter() {
			return enter;
		}
		
		/**
		 * 
		 * @return the course that was actually behind the entered one
		 */
		public Course getHiding() {
			return hiding;
		}
		
		/**
		 * 
		 * @return the JLabel for the enter course
		 */
		public JLabel getEnterText() {
			return a;
		}
		
		/**
		 * 
		 * @return the JLabel for whitespace
		 */
		public JLabel getArrow() {
			return b;
		}
		
		/**
		 * 
		 * @return the JLabel for the hiding course
		 */
		public JLabel getHidingText() {
			return c;
		}
		
		/**
		 * Constructs a JLabel with default settings.
		 * 
		 * @param text the text of the label
		 * @param c the color of the text
		 * @return the new constructed JLabel
		 */
		private static JLabel createField(String text, Color c) {
			JLabel label = new JLabel(text);
			label.setFont(new Font("Arial", Font.BOLD, 15));
			label.setForeground(c);
			return label;
		}
		
	}
	
	/**
	 * Enumeration for all accessible courses in
	 * Super Mario 64.
	 */
	public static enum Course {
		
		TPSS("The Princess's Secret Slide", 0),
		TSA("The Secret Aquarium", 0),
		BITDW("Bowser in the Dark World", 0),
		TOTWC("Tower of the Wing Cap", 0),
		COTMC("Cavern of the Metal Cap", 0),
		VCUTM("Vanish Cap Under the Moat", 0),
		BITFS("Bowser in the Fire Sea", 0),
		WMOTR("Wing Mario Over the Rainbow", 0),
		BITS("Bowser in the Sky", 0),
		BOB("Bob-omb Battlefield", 1),
		WF("Whomp's Fortress", 2),
		JRB("Jolly Roger Bay", 3),
		CCM("Cool, Cool Mountain", 4),
		BBH("Big Boo's Haunt", 5),
		HMC("Hazy Maze Cave", 6),
		LLL("Lethal Lava Land", 7),
		SSL("Shifting Sand Land", 8),
		DDD("Dire, Dire Docks", 9),
		SL("Snowman's Land", 10),
		WDW("Wet-Dry World", 11),
		TTM("Tall, Tall Mountain", 12),
		THI("Tiny-Huge Island", 13),
		TTC("Tick Tock Clock", 14),
		RR("Rainbow Ride", 15);
		
		private String name;
		private int level;
		
		private Course(String name, int level) {
			this.name = name;
			this.level = level;
		}
		
		/**
		 * 
		 * @return the original name of the course
		 * 			like in SM64
		 */
		public String getFullName() {
			return name;
		}
		
		/**
		 * 
		 * @return the original course id of the
		 * 			course like in SM64
		 */
		public int getCourseID() {
			return level;
		}
		
		/**
		 * 
		 * @return true if the course is a secret course,
		 * 			false if not
		 */
		public boolean isSecret() {
			return level == 0;
		}
		
		@Override
		public String toString() {
			return (isSecret() ? "" : "(" + level + ") ") + super.toString() + " (" + name + ")";
		}
		
	}
	
}
