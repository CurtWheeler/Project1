import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import bsh.*;

public class Calculator extends JFrame implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTable logTable = new JTable();
	DefaultTableModel logModel = new DefaultTableModel();
	private String logCols[] = { "Log" };
	private String logVals[][] = { { "" } };
	private JTextField displayText = new JTextField(30);
	private JButton[] button = new JButton[20];
	private String[] keys = { "", "", "≡", "C", "7", "8", "9", "/", "4", "5",
			"6", "*", "1", "2", "3", "-", "0", ".", "=", "+", };
	private int W, H;
	private String numStr = "";
	private boolean isLogMode;
	private JMenuBar menu;
	private JMenu logger;
	private JMenuItem on, off;
	private Container p;
	private int x, y;

	private void alignRight(JTable table, int column) {
		JLabel label = null;
		DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
		rightRenderer.setHorizontalAlignment(label.RIGHT);
		rightRenderer.setVerticalAlignment(label.BOTTOM);
		table.getColumnModel().getColumn(column).setCellRenderer(rightRenderer);
	}

	private void LogTable() {
		numStr = "";
		displayText.setText("");

		setSize(235, 405);
		x = 10;
		y = 210;

		// Create a new table instance
		logModel = new DefaultTableModel(logVals, logCols);
		logTable = new JTable(logModel) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public boolean isCellEditable(int rowIndex, int colIndex) {
				return false; // Disallow the editing of any cell
			}
		};
		logTable.setSize(200, 160);
		logTable.setLocation(10, 195);
		logModel.setRowCount(0);
		// logTable.setBackground(Color.PINK);
		alignRight(logTable, 0);
		this.add(logTable);

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
					JMenuItem del = new JMenuItem("Delete");
					JMenuItem delAll = new JMenuItem("Delete All");
					JPopupMenu popup = new JPopupMenu();
					popup.add(del);
					popup.add(delAll);
					popup.show(e.getComponent(), e.getX(), e.getY());
					del.addActionListener(new MenuActionListener());
					delAll.addActionListener(new MenuActionListener());
				}
			}
		});
	}

	class MenuActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			// System.out.println("Selected: " + e.getActionCommand());
			if (e.getActionCommand() == "Delete All") {
				logModel.setRowCount(0);
			}
			if (e.getActionCommand() == "Delete") {
				int i = logTable.getSelectedRow();
				logModel.removeRow(i);
			}
		}
	}

	public Calculator() {
		// sets
		setTitle("My Calculator");
		p = getContentPane();
		p.setLayout(null);

		// sizes
		setSize(235, 235);
		displayText.setSize(200, 30);
		displayText.setLocation(10, 10);
		x = 10;
		y = 40;

		displayText.setHorizontalAlignment(JTextField.RIGHT);
		p.add(displayText);

		// add buttons
		for (int i = 0; i < 20; i++) {
			button[i] = new JButton(keys[i]);
			button[i].addActionListener(this);
			button[i].setSize(50, 30);
			button[i].setLocation(x, y);
			if (button[i].getText() == "") {
				button[i].setEnabled(false);
			}
			p.add(button[i]);
			x = x + 50;

			if ((i + 1) % 4 == 0) {
				x = 10;
				y = y + 30;
			}
		}

		button[2].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String command = ((JButton) e.getSource()).getActionCommand();
				// System.out.println(command);
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

		displayText.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				int key = e.getKeyCode();
				// System.out.println(key);
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

	public void actionPerformed(ActionEvent e) {
		String resultStr;
		String str = String.valueOf(e.getActionCommand());

		char ch = str.charAt(0);

		switch (ch) {
		case '≡':
			displayText.setText("");
			break;
		case '≢':
			displayText.setText("");
			break;
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
			numStr = displayText.getText() + ch;
			displayText.setText(numStr);
		}
		displayText.requestFocusInWindow();
	}

	private String evaluate(String s) {
		s = s.replaceAll("[a-z]", "");
		s = s.replaceAll("[A-Z]", "");

		logVals[0][0] = s;

		Interpreter i = new Interpreter();
		try {
			i.eval("result = " + s);
		} catch (EvalError e) {
			e.printStackTrace();
		}
		try {
			numStr = String.valueOf(i.get("result"));
		} catch (EvalError e) {
			e.printStackTrace();
		}

		if (isNotNullorEmpty(numStr)) {
			logModel.insertRow(0, new Object[] { s + " = " + numStr });
		}
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
