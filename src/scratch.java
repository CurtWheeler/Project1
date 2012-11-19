import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import java.awt.*;
import java.awt.event.*;
import bsh.*;

public class Calculator extends JFrame implements ActionListener {
	private JTable logTable = new JTable();
	DefaultTableModel logModel = new DefaultTableModel();
	private String logCols[] = {"Log"};
	private String logVals[][] = {{"100"},{"200"},{"300"}};
	private JTextField displayText = new JTextField(30);
	private JButton[] button = new JButton[20];
	private String[] keys = { "", "", "", "C", "7", "8", "9", "/", "4", "5",
			"6", "*", "1", "2", "3", "-", "0", ".", "=", "+", };
	private int W, H;
	private String numStr = "";
	
	private void alignRight(JTable table, int column) {
        JLabel label = null;
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(label.RIGHT);
        rightRenderer.setVerticalAlignment(label.BOTTOM);
    }
	
	private void LogTable()
	{
		// Create a new table instance
		logModel = new DefaultTableModel(logVals, logCols);
		logTable = new JTable(logModel);
		logTable.setSize(200, 160);
		logTable.setLocation(10, 10);
		logTable.setBackground(Color.LIGHT_GRAY);
		//alignRight(logTable,0);
		this.add(logTable);
	}
	
	
	public Calculator() {
		
		W = 0;
		H = 0;
		
		//sizes
		setSize(235, 405);
		LogTable();
		displayText.setSize(201, 35);
		displayText.setLocation(10, 175);
		int x, y;
		x = 10;
		y = 210;
		
		//sets
		setTitle("My Calculator");
		Container pane = getContentPane();
		pane.setLayout(null); 
		displayText.setHorizontalAlignment(JTextField.RIGHT);
		pane.add(displayText);
		
		//add buttons
		for (int i = 0; i < 20; i++) {
			button[i] = new JButton(keys[i]);
			button[i].addActionListener(this);
			button[i].setSize(50, 30);
			button[i].setLocation(x, y);
			if(button[i].getText() == "")
			{
				button[i].setEnabled(false);
			}
			pane.add(button[i]);
			x = x + 50;

			if ((i + 1) % 4 == 0) {
				x = 10;
				y = y + 30;
			}
		}

		displayText.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				int key = e.getKeyCode();
				System.out.println(key);
				if (key == KeyEvent.VK_ENTER) {
					String resultStr = evaluate(displayText.getText());
					displayText.setText(resultStr);
					numStr = resultStr;
				}
				if (key == 127) {
					displayText.setText("");
				}
			}
		});

		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		setVisible(true);
	}
	
	public void actionPerformed(ActionEvent e) {
		String resultStr;
		String str = String.valueOf(e.getActionCommand());

		char ch = str.charAt(0);

		switch (ch)
		{
		case '=':
			resultStr = evaluate(displayText.getText());
			displayText.setText(resultStr);
			numStr = resultStr;
			break;
		case 'C':
			displayText.setText("");
			numStr = "";
			break;
		default:
			numStr = numStr + ch;
			displayText.setText(numStr);
		}
	}

	private String evaluate(String s) 
	{
    	s = s.replaceAll("[a-z]", "");
    	s = s.replaceAll("[A-Z]", "");
    	
    	logVals[0][0] = s;
    	
		Interpreter i = new Interpreter();
		try 
		{
			i.eval("result = " + s);
		} catch (EvalError e) 
		{
			e.printStackTrace();
		}
		try 
		{
			numStr = String.valueOf(i.get("result"));
		} catch (EvalError e) 
		{
			e.printStackTrace();
		}

		return numStr;
	}

	public static void main(String[] args) {
		Calculator C = new Calculator();
	}
}
