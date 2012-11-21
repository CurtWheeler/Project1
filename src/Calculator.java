import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

import bsh.*;

public class Calculator extends JFrame implements ActionListener {
	private Container p;
	private JTextField displayText = new JTextField(30);
	private JButton[] button = new JButton[20];
	private String[] keys = { "S", "L", "≡", "C", "7", "8", "9", "/", "4", "5",
			"6", "*", "1", "2", "3", "-", "0", ".", "=", "+", };
	private JTable logTable = new JTable();
	DefaultTableModel logModel = new DefaultTableModel();
	private String logCols[] = { "Log" };
	private String logVals[][] = { { "" } };
	private String numStr = "";
	private int btnX, btnY;

	// feature add-on
	// activated by ≡ button
	// deactivated by ≢ button
	private void LogTable() {

		// reset display text
		numStr = "";
		displayText.setText("");
		// set window size
		setSize(235, 405);
		btnX = 10;
		btnY = 210;
		// create table
		logModel = new DefaultTableModel(logVals, logCols);
		logTable = new JTable(logModel) {
			private static final long serialVersionUID = 1L;

			public boolean isCellEditable(int rowIndex, int colIndex) {
				// disable editing
				return false;
			}
		};
		// set table attributes
		logTable.setSize(200, 160);
		logTable.setLocation(10, 195);
		logModel.setRowCount(0);
		logTable.setToolTipText("Double Click to Recall || Right Click to Remove/All");
		alignRight(logTable, 0);
		p.add(logTable);

		// sets table value to display text on double click
		logTable.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					int[] row_indexes = logTable.getSelectedRows();
					for (int i = 0; i < row_indexes.length; i++) {
						String domain = logTable.getValueAt(row_indexes[i], 0)
								.toString();
						String[] split = domain.split("=");
						displayText.setText(split[0]);
						displayText.requestFocusInWindow();
					}
				}
			}
		});

		//set listener for pop-up context (right click) menu
		logTable.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				int r = logTable.rowAtPoint(e.getPoint());
				if (r >= 0 && r < logTable.getRowCount()) {
					logTable.setRowSelectionInterval(r, r);
				} else {
					logTable.clearSelection();
				}

				int row = logTable.getSelectedRow();
				if (row < 0)
					return;
				if (e.isPopupTrigger() && e.getComponent() instanceof JTable) {
					JMenuItem del = new JMenuItem("Remove");
					JMenuItem delAll = new JMenuItem("Remove All");
					JMenuItem clearLog = new JMenuItem("Clear Log");
					JPopupMenu popup = new JPopupMenu();
					popup.add(del);
					popup.add(delAll);
					popup.add(clearLog);
					popup.show(e.getComponent(), e.getX(), e.getY());
					del.addActionListener(new MenuActionListener());
					delAll.addActionListener(new MenuActionListener());
					clearLog.addActionListener(new MenuActionListener());
				}
			}
		});
	}

	//set actions for pop-up context (right click) menu
	class MenuActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand() == "Remove All") {
				logModel.setRowCount(0);
			}
			if (e.getActionCommand() == "Remove") {
				int i = logTable.getSelectedRow();
				logModel.removeRow(i);
			}
			if (e.getActionCommand() == "Clear Log") {
				ClearLog();
			}
		}
	}

	//align table text to right 
	private void alignRight(JTable table, int column) {
		JLabel label = null;
		DefaultTableCellRenderer r = new DefaultTableCellRenderer();
		r.setHorizontalAlignment(label.RIGHT);
		r.setVerticalAlignment(label.BOTTOM);
		table.getColumnModel().getColumn(column).setCellRenderer(r);
	}

	//save current table to log.txt
	private void SaveLog() {
		try {
			FileWriter fstream = new FileWriter("log.txt");
			BufferedWriter out = new BufferedWriter(fstream);
			for (int i = 0; i < logTable.getRowCount(); i++) {
				String s = (String) (logTable.getModel().getValueAt(i, 0));
				out.write(s + '\n');
			}
			out.close();
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}

	//read and load log.txt into table
	private void ReadLog() {
		File f = new File("log.txt");
		if (f.exists()) {
			try {
				FileInputStream fstream = new FileInputStream("log.txt");
				DataInputStream in = new DataInputStream(fstream);
				BufferedReader br = new BufferedReader(
						new InputStreamReader(in));
				String strLine;
				while ((strLine = br.readLine()) != null) {
					logModel.insertRow(0, new Object[] { strLine });
				}
				in.close();
			} catch (Exception e) {
				System.err.println("Error: " + e.getMessage());
			}
		} else {
			displayText.setToolTipText("--No Log File--");

		}

	}

	//empty log.txt file
	private void ClearLog() {
		try {
			FileWriter fstream = new FileWriter("log.txt");
			BufferedWriter out = new BufferedWriter(fstream);
			for (int i = 0; i < logTable.getRowCount(); i++) {
				out.write("");
			}
			out.close();
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}

	//constructs basic calculator
	public Calculator() {
		// sets up frame
		setTitle("The Logger");
		p = getContentPane();
		p.setLayout(null);

		// window sizes
		setSize(235, 235);
		displayText.setSize(200, 30);
		displayText.setLocation(10, 10);
		btnX = 10;
		btnY = 40;
		//right justify display text
		displayText.setHorizontalAlignment(JTextField.RIGHT);
		p.add(displayText);

		// add buttons
		for (int i = 0; i < 20; i++) {
			button[i] = new JButton(keys[i]);
			button[i].addActionListener(this);
			button[i].setSize(50, 30);
			button[i].setLocation(btnX, btnY);
			if (button[i].getText() == "") {
				button[i].setEnabled(false);
			}
			p.add(button[i]);
			btnX = btnX + 50;

			if ((i + 1) % 4 == 0) {
				btnX = 10;
				btnY = btnY + 30;
			}
		}

		// disable log buttons when log is closed
		button[0].setEnabled(false);
		button[1].setEnabled(false);

		//logger button listener. constructs / displays logger
		button[2].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String command = ((JButton) e.getSource()).getActionCommand();
				if (command == "≡") {
					button[2].setText("≢");
					LogTable();
					logTable.setVisible(true);
					p.validate();
					repaint();
				} else if (command == "≢") {
					button[2].setText("≡");
					setSize(235, 235);
					logTable.setVisible(false);
					p.validate();
					p.repaint();
				}
				numStr = "";
				displayText.setText("");
			}
		});

		//key listener to submit on enter
		displayText.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				int key = e.getKeyCode();
				if (key == KeyEvent.VK_ENTER) {
					String resultStr = evaluate(displayText.getText());
					displayText.setText(resultStr);
					numStr = resultStr;
				}
				if (key == 127) {
					displayText.setText("");
				}
				displayText.requestFocusInWindow();
			}
		});

		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		setVisible(true);
	}

	//check button pressed, and take action
	public void actionPerformed(ActionEvent e) {
		String resultStr;
		String str = String.valueOf(e.getActionCommand());
		char ch = str.charAt(0);

		switch (ch) {
		case 'S'://save log
			SaveLog();
			logModel.fireTableDataChanged();
			break;
		case 'L'://load log
			ReadLog();
			break;
		case '≡'://display logger
			displayText.setText("");
			button[0].setEnabled(true);
			button[1].setEnabled(true);
			break;
		case '≢'://hide logger
			displayText.setText("");
			button[0].setEnabled(false);
			button[1].setEnabled(false);
			break;
		case '='://evaluate expression
			resultStr = evaluate(displayText.getText());
			displayText.setText(resultStr);
			numStr = resultStr;
			break;
		case 'C'://clear display
			displayText.setText("");
			numStr = "";
			break;
		default://add text to display
			numStr = displayText.getText() + ch;
			displayText.setText(numStr);
		}
		//always focus back, for pressed keys
		displayText.requestFocusInWindow();
	}

	private String evaluate(String s) {
		//clear alpha characters from string (if you wish)
		// s = s.replaceAll("[a-z]", "");
		// s = s.replaceAll("[A-Z]", "");

		logVals[0][0] = s;
		Interpreter i = new Interpreter();
		try {
			i.eval("result = " + s);
		} catch (EvalError e) {
			// e.printStackTrace();
		}
		try {
			//evaluate expression
			numStr = String.valueOf(i.get("result"));
		} catch (EvalError e) {
			// e.printStackTrace();
		}

		if (isNotNullorEmpty(numStr)) {
			//insert row in logger
			logModel.insertRow(0, new Object[] { s + " = " + numStr });
		}
		//refocus
		displayText.requestFocusInWindow();
		return numStr;
	}

	public static boolean isNotNullorEmpty(final String string) {
		return string != null && !string.isEmpty() && !string.trim().isEmpty();
	}

	public static void main(String[] args) {
		Calculator C = new Calculator();
	}
}
