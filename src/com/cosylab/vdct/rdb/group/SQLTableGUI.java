package com.cosylab.vdct.rdb.group;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.cosylab.vdct.db.DBData;
import com.cosylab.vdct.rdb.RdbDataMapper;

/** SQLTableGUI
 * GUI for SQLTableModel,
 */
public class SQLTableGUI extends JDialog implements ActionListener {

	// Simple apps call run(), see below
	
	private boolean loadMode = true;
	private RdbDataMapper mapper = null;
	private String selectedIoc = null;
	private String selectedGroup = null;
	private DBData data = null;
	
	protected EpicsGroupTreeModel model;
	public EpicsGroupTree tree;
	private JButton groupAction = null;

	private static final String addNewString = "Add New";
	private static final String refreshString = "Refresh";
	private static final String storeChangesString = "Store Changes";
	private static final String loadString = "Load";
	private static final String saveString = "Save";
	private static final String loadGroupString = "Load Group";
	private static final String saveGroupString = "Save Group";
	private static final String cancelString = "Cancel";
	
	/**
	 * @param arg0
	 * @param arg1
	 * @throws HeadlessException
	 */
	public SQLTableGUI(RdbDataMapper mapper, JFrame guiContext) {
		super(guiContext, true);
		this.mapper = mapper;
        makeGUI(guiContext);
	}

	private void makeGUI(JFrame guiContext) {
		GUI.init();

		setTitle(loadMode ? loadGroupString : saveGroupString);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		getContentPane().add(createContentPanel());
		pack();
	}

	/**
	 * @param loadMode the loadMode to set
	 */
	public void setLoadMode(boolean loadMode) {
		this.loadMode = loadMode;
		setTitle(loadMode ? loadGroupString : saveGroupString);
		groupAction.setText(loadMode ? loadString : saveString);
		groupAction.setMnemonic(loadMode ? KeyEvent.VK_L : KeyEvent.VK_S);
	}
	
	/**
	 * @return the data
	 */
	public DBData getData() {
		return data;
	}

	/* (non-Javadoc)
	 * @see java.awt.Component#setVisible(boolean)
	 */
	public void setVisible(boolean arg0) {
		if (arg0) {
			createTableModel();
			setLocationRelativeTo(getParent());			
		}
		super.setVisible(arg0);
	}

	private JPanel createContentPanel() {
		
		JPanel contentPanel = new JPanel(new GridBagLayout());

		JPanel groupPanel = createGroupPanel();
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.weightx = 1.0;
		constraints.weighty = 1.0;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.insets = new Insets(4, 4, 4, 4);
		contentPanel.add(groupPanel, constraints);

		JPanel buttonsPanel = createButtonsPanel();
		constraints = new GridBagConstraints();
		constraints.gridy = 1;
		constraints.weightx = 1.0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.insets = new Insets(4, 4, 4, 4);
		contentPanel.add(buttonsPanel, constraints);

		return contentPanel;
	}
	
	private JPanel createGroupPanel() {

		JPanel groupPanel = new JPanel(new GridBagLayout());

		JPanel groupButtonsPanel = createGroupButtonsPanel();
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.EAST;
		constraints.insets = new Insets(4, 4, 4, 0);
		groupPanel.add(groupButtonsPanel, constraints);
		
		JScrollPane createTablePane = createTablePane();
		constraints = new GridBagConstraints();
		constraints.gridy = 1;
		constraints.weightx = 1.0;
		constraints.weighty = 1.0;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.insets = new Insets(4, 4, 4, 4);
		groupPanel.add(createTablePane, constraints);

		return groupPanel;
	}

	private JPanel createGroupButtonsPanel() {
		JPanel buttonsPanel = new JPanel();
		JButton button = new JButton(refreshString);
		button.setMnemonic(KeyEvent.VK_R);
		button.addActionListener(this);
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.insets = new Insets(4, 0, 4, 4);
		buttonsPanel.add(button, constraints);

		button = new JButton(addNewString);
		button.setMnemonic(KeyEvent.VK_A);
		button.addActionListener(this);
		constraints = new GridBagConstraints();
		constraints.gridx = 1;
		constraints.insets = new Insets(4, 4, 4, 4);
		buttonsPanel.add(button, constraints);

		button = new JButton(storeChangesString);
		button.setMnemonic(KeyEvent.VK_C);
		button.addActionListener(this);
		constraints = new GridBagConstraints();
		constraints.gridx = 2;
		constraints.insets = new Insets(4, 4, 4, 0);
		buttonsPanel.add(button, constraints);
		
		return buttonsPanel;
	}
	
	private JScrollPane createTablePane() {
		
		tree = new EpicsGroupTree();
		tree.addListener(new EpicsGroupTreeListener() {
			
			public void iocSelected(String ioc_id) {
				selectedIoc = ioc_id;
				selectedGroup = null;
				groupAction.setEnabled(false);
			}
			public void groupSelected(String ioc_id, String group_id) {
				selectedIoc = ioc_id;
				selectedGroup = group_id;
				groupAction.setEnabled(true);
			}
		});

		// Shortcut: Double-click = onOk  (if valid stuff selected)
		tree.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2 && selectedGroup != null && selectedIoc != null) {
					performGroupAction();
				}
			}
		});
		
		JScrollPane pane = new JScrollPane(tree);
		
		//TODO:REM
		/*
		table = new JTable();
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				refreshSelectedGroup();
			}
		});
		JScrollPane pane = new JScrollPane(table);
		*/
		
		
		pane.setPreferredSize(new Dimension(640, 384));
		return pane;
	}
	
	private JPanel createButtonsPanel() {
		JPanel buttonsPanel = new JPanel();
		groupAction = new JButton(loadMode ? loadString : saveString);
		groupAction.setMnemonic(loadMode ? KeyEvent.VK_L : KeyEvent.VK_S);
		groupAction.addActionListener(this);
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.EAST;
		constraints.insets = new Insets(4, 4, 4, 4);
		buttonsPanel.add(groupAction, constraints);

		JButton button = new JButton(cancelString);
		button.setMnemonic(KeyEvent.VK_C);
		button.addActionListener(this);
		constraints = new GridBagConstraints();
		constraints.gridx = 1;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.insets = new Insets(4, 4, 4, 4);
		buttonsPanel.add(button, constraints);

		return buttonsPanel;
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent event) {
		String action = event.getActionCommand();
		
		if (action.equals(refreshString)) {
			// TODO:REM
			/*
			if (model.wasEdited())
			{
				switch (JOptionPane.showConfirmDialog(
						this,
						"Table has been edited.\n" +
						"Replace with RDB, loose edited values?",
						"Confirm Reload",
						JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE))
						{
						case JOptionPane.YES_OPTION:
							model.load();
							model.fireTableDataChanged();
						}
			}
			*/
		} else if (action.equals(addNewString)) {
			// TODO: refresh the tree
			//model.addRecord();

		} else if (action.equals(storeChangesString)) {
			// TODO:REM
			/*
			if (model.save())
			{
				model.load();
				model.fireTableDataChanged();
			}
			*/

		} else if (action.equals(loadString) || action.equals(saveString)) {
			performGroupAction();
		} else if (action.equals(cancelString)) {
			data = null;
			setVisible(false);
		} 
	}
	
	public void createTableModel() {
        
		model = new EpicsGroupTreeModel(mapper);
		tree.setModel(model);
		
		/*
		boolean use_tree = false;
        
        Connection c = SQLHelper.create(mapper).getConnection();
        
        if (use_tree)
        {

            OkCancelDlg dlg = new EpicsGroupDlg(null);
            dlg.run();
            System.exit(0);
        }
        else
        {
            Vector headers = new Vector();
            headers.add(new String("Id"));
            headers.add(new String("Filename"));
            headers.add(new String("Version"));
            headers.add(new String("Description"));
            
            PreparedStatement select_all, select, update, insert, delete;
            try
            {
                int iocId = mapper.createAnIoc();
            	
            	select_all = c.prepareStatement("SELECT p_db_id, p_db_file_name, p_db_version, p_db_desc FROM p_db");
                select = c.prepareStatement("SELECT p_db_id FROM p_db WHERE p_db_id=?");
                update = c.prepareStatement("UPDATE p_db SET p_db_file_name=?, p_db_version=?, p_db_desc=? WHERE p_db_id=?");
                insert = c.prepareStatement("INSERT INTO p_db (p_db_id, ioc_id_FK, p_db_file_name, p_db_version, p_db_desc)"
                		+ "  VALUES (?," + String.valueOf(iocId) + ",?,?,?)");
                delete = c.prepareStatement("DELETE FROM p_db WHERE p_db_id=?");
                
                model = new SQLTableModel101(headers, select_all, select, update, insert, delete);
        		model.load();
        		TableSorter sorter = new TableSorter(model);
        		table.setModel(sorter);
        		sorter.addMouseListenerToHeaderInTable(table);
        		refreshSelectedGroup();        		
        		setTableColumnWidths();
            }
            catch (SQLException e)
            {
                System.err.println ("SQL Exception: " + e.getMessage());
                e.printStackTrace();
            }
        }
        */
	}
	
	private void performGroupAction() {
		try {
			if (loadMode) {
				data = mapper.loadDbGroup(selectedGroup);
			} else {
				mapper.saveDbGroup(selectedGroup);
			}
			setVisible(false);
		} catch (Exception exception) {
			exception.printStackTrace();

			JOptionPane.showMessageDialog(null, exception.getMessage(), "Database error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	// TODO:REM
	/*
	private void setTableColumnWidths() {
		// "delete" column is smaller than rest
		TableColumnModel cm = tree.getColumnModel();
		int num = cm.getColumnCount();
		if (num > 0)
			cm.getColumn(0).setMaxWidth(50);
		for (int i=1; i<num; ++i)
			cm.getColumn(i).setMinWidth(80);
	}
	
	private void refreshSelectedGroup() {
		int rows = tree.getSelectedRowCount();
		int selectedRow = tree.getSelectedRow();
		if (rows != 1 || selectedRow == -1) {
			selectedGroup = null;
			groupAction.setEnabled(false);
		} else {
			selectedGroup = tree.getValueAt(selectedRow, 1).toString();
			groupAction.setEnabled(true);
		}
	}
	*/
};
