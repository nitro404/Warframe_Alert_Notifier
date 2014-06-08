package gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

public class SliderDialog extends JDialog implements ActionListener, KeyListener, ChangeListener {
	
	private JPanel m_mainPanel;
	private JPanel m_buttonPanel;
	private JLabel m_messageLabel;
	private JSlider m_slider;
	private JButton m_okButton;
	private JButton m_cancelButton;
	private String m_message;
	private String m_suffix;
	private int m_value;
	private boolean m_showPositive;
	private boolean m_submitted;
	final private static String defaultMessage = "Select a value";
	private static final long serialVersionUID = -860843467646998793L;
	
	public SliderDialog(Frame parent) {
		super(parent, true);
		setTitle("Slider");
		setSize(260, 150);
		setResizable(false);
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		
		m_value = 0;
		m_submitted = false;
		m_showPositive = false;
		m_suffix = "";
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				cancel();
			}
		});
		
		initComponents();
	}
	
	private void initComponents() {
		m_mainPanel = new JPanel();
		m_buttonPanel = new JPanel();
		
		m_messageLabel = new JLabel(defaultMessage);
		
		m_slider = new JSlider(0, 100, 0);
		m_slider.setMajorTickSpacing(10);
		m_slider.setPaintTicks(true);
		m_slider.setPaintLabels(true);
		m_slider.addKeyListener(this);
		m_slider.addChangeListener(this);
		
		m_okButton = new JButton("Ok");
		m_okButton.addKeyListener(this);
		m_okButton.addActionListener(this);
		
		m_cancelButton = new JButton("Cancel");
		m_cancelButton.addKeyListener(this);
		m_cancelButton.addActionListener(this);
		
		m_buttonPanel.add(m_okButton);
		m_buttonPanel.add(m_cancelButton);
		
		m_mainPanel.add(m_messageLabel);
		m_mainPanel.add(m_slider);
		m_mainPanel.add(m_buttonPanel);
		
		setContentPane(m_mainPanel);
		
		initLayout();
	}
	
	private void initLayout() {
		m_buttonPanel.setLayout(new FlowLayout());
		
		GridBagLayout layout = new GridBagLayout();
		m_mainPanel.setLayout(layout);
		GridBagConstraints c;
		Insets insets = new Insets(6, 6, 6, 6);
		
		c = new GridBagConstraints();
		c.insets = insets;
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 4;
		c.gridheight = 1;
		c.weightx = 1;
		c.weighty = 0;
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		layout.setConstraints(m_messageLabel, c);
		
		c = new GridBagConstraints();
		c.insets = insets;
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 4;
		c.gridheight = 1;
		c.weightx = 1;
		c.weighty = 0;
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.HORIZONTAL;
		layout.setConstraints(m_slider, c);
		
		c = new GridBagConstraints();
		c.insets = insets;
		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 4;
		c.gridheight = 1;
		c.weightx = 1;
		c.weighty = 0;
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.HORIZONTAL;
		layout.setConstraints(m_buttonPanel, c);
	}
	
	private void updateMessage() {
		m_messageLabel.setText(m_message + ": " + (m_showPositive && m_slider.getValue() > 0 ? "+" : "") + m_slider.getValue() + " " + m_suffix);
	}
	
	public int getValue() {
		return m_value;
	}
	
	public boolean userSubmitted() {
		return m_submitted;
	}
	
	public void submit() {
		m_value = m_slider.getValue();
		
		m_submitted = true;
		
		close();
	}
	
	public void cancel() {
		m_submitted = false;
		
		close();
	}
	
	public void close() {
		clear();
		
		setVisible(false);
	}
	
	public void clear() {
		setTitle("Slider");
		m_message = defaultMessage;
		m_suffix = "";
		updateMessage();
		m_slider.setMinimum(0);
		m_slider.setMaximum(100);
		m_slider.setValue(0);
		m_showPositive = false;
	}
	
	public void display(String title, String message, int minimum, int maximum) {
		display(title, message, minimum, maximum, minimum, "", false);
	}
	
	public void display(String title, String message, int minimum, int maximum, boolean showPositive) {
		display(title, message, minimum, maximum, minimum, "", showPositive);
	}
	
	public void display(String title, String message, int minimum, int maximum, String suffix) {
		display(title, message, minimum, maximum, minimum, suffix, false);
	}
	
	public void display(String title, String message, int minimum, int maximum, String suffix, boolean showPositive) {
		display(title, message, minimum, maximum, minimum, suffix, showPositive);
	}
	
	public void display(String title, String message, int minimum, int maximum, int initialValue) {
		display(title, message, minimum, maximum, initialValue, "", false);
	}
	
	public void display(String title, String message, int minimum, int maximum, int initialValue, boolean showPositive) {
		display(title, message, minimum, maximum, initialValue, "", showPositive);
	}
	
	public void display(String title, String message, int minimum, int maximum, int initialValue, String suffix) {
		display(title, message, minimum, maximum, initialValue, suffix, false);
	}
	
	public void display(String title, String message, int minimum, int maximum, int initialValue, String suffix, boolean showPositive) {
		if(title == null || title.trim().length() == 0 || maximum <= minimum) { return; }
		
		setTitle(title.trim());
		
		m_message = message == null || message.trim().length() == 0 ? defaultMessage : message.trim();
		m_suffix = suffix == null || suffix.trim().length() == 0 ? "" : suffix.trim();
		m_showPositive = showPositive;
		
		updateMessage();
		
		m_slider.setMinimum(minimum);
		m_slider.setMaximum(maximum);
		m_slider.setMajorTickSpacing((maximum - minimum) / 10);
		m_slider.setValue(initialValue < minimum ? minimum : initialValue > maximum ? maximum : initialValue);
		
		m_submitted = false;
		
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		
		setLocation((d.width / 2) - (getWidth() / 2), (d.height / 2) - (getHeight() / 2));
		
		setVisible(true);
	}
	
	public void keyTyped(KeyEvent e) { }
	public void keyReleased(KeyEvent e) { }
	
	public void keyPressed(KeyEvent e) {
		if(e.getSource() == m_cancelButton) {
			if(e.getKeyChar() == KeyEvent.VK_ENTER || e.getKeyChar() == KeyEvent.VK_SPACE) {
				cancel();
			}
		}
		
		if(e.getSource() == m_okButton) {
			if(e.getKeyChar() == KeyEvent.VK_ENTER || e.getKeyChar() == KeyEvent.VK_SPACE) {
				submit();
			}
		}
		
		if(e.getKeyChar() == KeyEvent.VK_ESCAPE) {
			cancel();
		}
		
	}
	
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == m_cancelButton) {
			cancel();
		}
		else if(e.getSource() == m_okButton) {
			submit();
		}
	}
	
	public void stateChanged(ChangeEvent e) {
		if(e.getSource() == m_slider) {
			updateMessage();
		}
	}
	
}
