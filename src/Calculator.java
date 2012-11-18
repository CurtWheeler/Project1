import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import bsh.*;

public class Calculator extends JFrame implements ActionListener {
	private JTextField displayText = new JTextField(30);
	private JButton[] button = new JButton[20];
	private String[] keys = { "", "", "", "C", "7", "8", "9", "/", "4", "5",
			"6", "*", "1", "2", "3", "-", "0", ".", "=", "+", };

	private String numStr = "";

	public Calculator() {

		setTitle("My Calculator");
		setSize(235, 235);
		Container pane = getContentPane();

		pane.setLayout(null); 
		
		displayText.setSize(200, 30);
		displayText.setLocation(10, 10);
		displayText.setHorizontalAlignment(JTextField.RIGHT);
		pane.add(displayText);

		int x, y;

		x = 10;
		y = 40;

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

	private String evaluate(String s) {
    	s = s.replaceAll("[a-z]", "");
    	s = s.replaceAll("[A-Z]", "");
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
		return numStr;
	}

	public static void main(String[] args) {
		Calculator C = new Calculator();
	}
}
