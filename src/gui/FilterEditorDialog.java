package gui;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import alert.*;

public class FilterEditorDialog extends JDialog implements ActionListener, WindowListener, MouseListener {
	
	private JTabbedPane m_mainTabbedPane;
	private JScrollPane m_creditsScrollPane;
	private JPanel m_creditsPanel;
	private JCheckBox m_filterCreditsCheckBox;
	private JComboBox m_filterCreditsComparisonOperatorComboBox;
	private JLabel m_creditAmountLabel;
	private JTextField m_creditAmountTextField;
	private JScrollPane[] m_rewardScrollPanes;
	private JPanel m_rewardPanels[];
	private Vector<Vector<JCheckBox>> m_allRewardCheckBoxes;
	
	private JMenuBar m_menuBar;
	private JMenu m_fileMenu;
	private JMenuItem m_fileUpdateFiltersMenuItem;
	private JMenuItem m_fileDiscardFiltersMenuItem;
	private JMenu m_selectMenu;
	private JMenuItem m_selectAllMenuItem;
	private JMenuItem m_selectNoneMenuItem;
	private JMenuItem m_selectInvertMenuItem;
	
	private JPopupMenu m_popupMenu;
	private JMenuItem m_selectAllPopupMenuItem;
	private JMenuItem m_selectNonePopupMenuItem;
	private JMenuItem m_selectInvertPopupMenuItem;
	private JMenuItem m_updateFiltersPopupMenuItem;
	private JMenuItem m_discardFiltersPopupMenuItem;
	private JMenuItem m_cancelPopupMenuItem;
	
	private static final long serialVersionUID = 1936808464093046902L;
	
	public FilterEditorDialog(Frame parent) {
		super(parent, true);
		setTitle("Filter Editor");
		setMinimumSize(new Dimension(320, 240));
		setSize(640, 480);
		setResizable(true);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(this);
		
		initComponents();
		initMenu();
		initPopupMenu();
	}
	
	private void initComponents() {
		m_mainTabbedPane = new JTabbedPane();
		
		// initialize the credits tab
		m_creditsPanel = new JPanel();
		m_creditsPanel.addMouseListener(this);
		
		m_filterCreditsCheckBox = new JCheckBox("Filter Credits");
		m_filterCreditsComparisonOperatorComboBox = new JComboBox(ComparisonOperatorType.operatorValues);
		m_filterCreditsComparisonOperatorComboBox.setSelectedIndex(ComparisonOperatorType.defaultOperator.ordinal());
		m_creditAmountLabel = new JLabel("Credit Amount:");
		m_creditAmountTextField = new JTextField();
		m_creditAmountTextField.setPreferredSize(new Dimension(150, m_creditAmountTextField.getPreferredSize().height));
		
		m_creditsPanel.add(m_filterCreditsCheckBox);
		m_creditsPanel.add(m_filterCreditsComparisonOperatorComboBox);
		m_creditsPanel.add(m_creditAmountLabel);
		m_creditsPanel.add(m_creditAmountTextField);
		
		m_creditsScrollPane = new JScrollPane(m_creditsPanel);
		
		m_mainTabbedPane.addTab("Credits", null, m_creditsScrollPane, "Displays the credit filter settings for alerts.");
		
		setContentPane(m_mainTabbedPane);
	}
	
	// initialize the menu
	private void initMenu() {
		m_menuBar = new JMenuBar();
		
		m_fileMenu = new JMenu("File");
		m_fileUpdateFiltersMenuItem = new JMenuItem("Update Filters");
		m_fileDiscardFiltersMenuItem = new JMenuItem("Discard Filters");
		
		m_selectMenu = new JMenu("Select");
		m_selectAllMenuItem = new JMenuItem("All");
		m_selectNoneMenuItem = new JMenuItem("None");
		m_selectInvertMenuItem = new JMenuItem("Invert");
		
		m_fileUpdateFiltersMenuItem.addActionListener(this);
		m_fileDiscardFiltersMenuItem.addActionListener(this);
		m_selectAllMenuItem.addActionListener(this);
		m_selectNoneMenuItem.addActionListener(this);
		m_selectInvertMenuItem.addActionListener(this);
		
		m_fileMenu.add(m_fileUpdateFiltersMenuItem);
		m_fileMenu.add(m_fileDiscardFiltersMenuItem);
		
		m_selectMenu.add(m_selectAllMenuItem);
		m_selectMenu.add(m_selectNoneMenuItem);
		m_selectMenu.add(m_selectInvertMenuItem);
		
		m_menuBar.add(m_fileMenu);
		m_menuBar.add(m_selectMenu);
		
		setJMenuBar(m_menuBar);
	}
	
	private void initPopupMenu() {
		// initialize popup menu
		m_popupMenu = new JPopupMenu();
		
		m_selectAllPopupMenuItem = new JMenuItem("Select All");
		m_selectNonePopupMenuItem = new JMenuItem("Select None");
		m_selectInvertPopupMenuItem = new JMenuItem("Invert Selection");
		m_updateFiltersPopupMenuItem = new JMenuItem("Update Filters");
		m_discardFiltersPopupMenuItem = new JMenuItem("Discard Filters");
		m_cancelPopupMenuItem = new JMenuItem("Cancel");
		
		m_selectAllPopupMenuItem.addActionListener(this);
		m_selectNonePopupMenuItem.addActionListener(this);
		m_selectInvertPopupMenuItem.addActionListener(this);
		m_updateFiltersPopupMenuItem.addActionListener(this);
		m_discardFiltersPopupMenuItem.addActionListener(this);
		m_cancelPopupMenuItem.addActionListener(this);
		
		m_popupMenu.add(m_selectAllPopupMenuItem);
		m_popupMenu.add(m_selectNonePopupMenuItem);
		m_popupMenu.add(m_selectInvertPopupMenuItem);
		m_popupMenu.addSeparator();
		m_popupMenu.add(m_updateFiltersPopupMenuItem);
		m_popupMenu.add(m_discardFiltersPopupMenuItem);
		m_popupMenu.addSeparator();
		m_popupMenu.add(m_cancelPopupMenuItem);
	}
	
	public void initialize() {
		// clear old tabs
		while(m_mainTabbedPane.getComponentCount() > 1) {
			m_mainTabbedPane.remove(1);
		}
		
		// update credit filters
		m_filterCreditsCheckBox.setSelected(AlertNotifier.filters.getFilterCredits());
		m_creditAmountTextField.setText(Integer.toString(AlertNotifier.filters.getFilterCreditAmount()));
		m_filterCreditsComparisonOperatorComboBox.setSelectedIndex(AlertNotifier.filters.getFilterCreditsComparisonOperator().ordinal());
		
		// initialize reward category tabs
		m_rewardScrollPanes = new JScrollPane[RewardCategory.values().length];
		m_rewardPanels = new JPanel[RewardCategory.values().length];
		m_allRewardCheckBoxes = new Vector<Vector<JCheckBox>>();
		for(int i=0;i<RewardCategory.values().length;i++) {
			m_rewardPanels[i] = new JPanel();
			m_rewardPanels[i].setLayout(null);
			m_rewardPanels[i].addMouseListener(this);
			
			JCheckBox rewardCheckBox = null;
			Vector<JCheckBox> rewardCheckBoxes = new Vector<JCheckBox>();
			
			Reward r = null;
			for(int j=0;j<AlertNotifier.rewards.numberOfRewards();j++) {
				r = AlertNotifier.rewards.getReward(j);
				if(r.getCategory() == RewardCategory.values()[i]) {
					rewardCheckBox = new JCheckBox(r.getName());
					rewardCheckBox.setSelected(AlertNotifier.filters.hasRewardFilter(r));
					m_rewardPanels[i].add(rewardCheckBox);
					rewardCheckBoxes.add(rewardCheckBox);
				}
			}
			
			m_allRewardCheckBoxes.add(rewardCheckBoxes);
			
			m_rewardPanels[i].setLayout(new BoxLayout(m_rewardPanels[i], BoxLayout.PAGE_AXIS));
			
			m_rewardScrollPanes[i] = new JScrollPane(m_rewardPanels[i]);
			
			m_mainTabbedPane.addTab(RewardCategory.displayNames[i], null, m_rewardScrollPanes[i], "Displays rewards from the " + RewardCategory.values()[i].name() + " category.");
		}
	}
	
	public void submit() {
		// parse credit amount
		int creditAmount = -1;
		try { creditAmount = Integer.parseInt(m_creditAmountTextField.getText()); }
		catch(NumberFormatException e) {
			JOptionPane.showMessageDialog(this, "Invalid credit amount specified, please ensure credit amount is a valid integer.", "Invalid Credit Amount", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if(creditAmount < 0) {
			JOptionPane.showMessageDialog(this, "Negative credit amount specified, please ensure credit amount is a positive value.", "Negative Credit Amount", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		// update credit filters
		AlertNotifier.filters.setFilterCredits(m_filterCreditsCheckBox.isSelected());
		AlertNotifier.filters.setFilterCreditAmount(creditAmount);
		AlertNotifier.filters.setFilterCreditsComparisonOperator(ComparisonOperatorType.values()[m_filterCreditsComparisonOperatorComboBox.getSelectedIndex()]);
		
		// update reward filters
		AlertNotifier.filters.clearRewardFilters();
		JCheckBox rewardCheckBox = null;
		for(int i=0;i<m_allRewardCheckBoxes.size();i++) {
			for(int j=0;j<m_allRewardCheckBoxes.elementAt(i).size();j++) {
				rewardCheckBox = m_allRewardCheckBoxes.elementAt(i).elementAt(j);
				
				if(rewardCheckBox.isSelected()) {
					AlertNotifier.filters.addRewardFilter(AlertNotifier.rewards.getReward(rewardCheckBox.getText()));
				}
			}
		}
		
		close();
	}
	
	public void abort() {
		close();
	}
	
	public void close() {
		setVisible(false);
	}
	
	public void selectAll() {
		if(m_mainTabbedPane.getSelectedIndex() <= 0) { return; }
		
		Vector<JCheckBox> rewardCheckBoxes = m_allRewardCheckBoxes.elementAt(m_mainTabbedPane.getSelectedIndex() - 1);
		
		for(int i=0;i<rewardCheckBoxes.size();i++) {
			rewardCheckBoxes.elementAt(i).setSelected(true);
		}
	}
	
	public void selectNone() {
		if(m_mainTabbedPane.getSelectedIndex() <= 0) { return; }
		
		Vector<JCheckBox> rewardCheckBoxes = m_allRewardCheckBoxes.elementAt(m_mainTabbedPane.getSelectedIndex() - 1);
		
		for(int i=0;i<rewardCheckBoxes.size();i++) {
			rewardCheckBoxes.elementAt(i).setSelected(false);
		}
	}
	
	public void selectInvert() {
		if(m_mainTabbedPane.getSelectedIndex() <= 0) { return; }
		
		Vector<JCheckBox> rewardCheckBoxes = m_allRewardCheckBoxes.elementAt(m_mainTabbedPane.getSelectedIndex() - 1);
		
		for(int i=0;i<rewardCheckBoxes.size();i++) {
			rewardCheckBoxes.elementAt(i).setSelected(!rewardCheckBoxes.elementAt(i).isSelected());
		}
	}
	
	private void promptUpdateFilters() {
		int choice = JOptionPane.showConfirmDialog(this, "Update filters?", "Update Filters", JOptionPane.YES_NO_CANCEL_OPTION);
		
		if(choice == JOptionPane.YES_OPTION) {
			submit();
		}
		else if(choice == JOptionPane.NO_OPTION) {
			abort();
		}
	}
	
	public void display() {
		initialize();
		
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		
		setLocation((d.width / 2) - (getWidth() / 2), (d.height / 2) - (getHeight() / 2));
		
		setVisible(true);
	}
	
	public void windowActivated(WindowEvent e) { }
	public void windowClosed(WindowEvent e) { }
	public void windowDeactivated(WindowEvent e) { }
	public void windowDeiconified(WindowEvent e) { }
	public void windowIconified(WindowEvent e) { }
	public void windowOpened(WindowEvent e) { }
	
	public void windowClosing(WindowEvent e) {
		if(e.getSource() == this) {
			promptUpdateFilters();
		}
	}
	
	public void actionPerformed(ActionEvent e) {
		if(e == null) { return; }
		
		if(e.getSource() == m_selectAllPopupMenuItem || e.getSource() == m_selectAllMenuItem) {
			selectAll();
		}
		else if(e.getSource() == m_selectNonePopupMenuItem || e.getSource() == m_selectNoneMenuItem) {
			selectNone();
		}
		else if(e.getSource() == m_selectInvertPopupMenuItem || e.getSource() == m_selectInvertMenuItem) {
			selectInvert();
		}
		else if(e.getSource() == m_updateFiltersPopupMenuItem || e.getSource() == m_fileUpdateFiltersMenuItem) {
			submit();
		}
		else if(e.getSource() == m_discardFiltersPopupMenuItem || e.getSource() == m_fileDiscardFiltersMenuItem) {
			abort();
		}
		else if(e.getSource() == m_cancelPopupMenuItem) {
			return;
		}
	}
	
	public void mouseClicked(MouseEvent e) { }
	public void mousePressed(MouseEvent e) { }
	public void mouseEntered(MouseEvent e) { }
	public void mouseExited(MouseEvent e) { }
	
	public void mouseReleased(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON3) {
			m_popupMenu.show(m_mainTabbedPane.getSelectedComponent(), e.getX(), e.getY());
		}
	}
	
}
