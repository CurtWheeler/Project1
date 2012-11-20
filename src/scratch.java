import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import bsh.*;

public class scratch extends JFrame {
	private JMenuBar menu;
	private JMenu logger;
	private JMenuItem on, off;
	
	scratch()
	{
		setTitle("My Calculator");
		Container p = getContentPane();
		p.setLayout(null);
		
		menu = new JMenuBar();
		logger = new JMenu("Logger");
		on = new JMenuItem("On");
		off = new JMenuItem("Off");
		menu.add(logger);
		logger.add(on);
		logger.add(off);
		p.add(logger);
		setJMenuBar(menu);
		setSize(100,100);
		setVisible(true);
		setJMenuBar(menu);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	
	
	public static void main(String[] args) {
		
		scratch s = new scratch();
	}
}
