package com.cosylab.vdct.rdb.group;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumnModel;

// TODO: incorporate with group selection
/*
	private static final String selectGroupString = "Select Group";
	private static final String loadGroupString = "Load Group";
	private static final String saveGroupString = "Save Group";


    private JPanel createGroupPanel() {
    	
    	JPanel groupPanel = new JPanel(new GridBagLayout());
    	groupPanel.setBorder(new TitledBorder("Connect to database"));
    	
    	JLabel groupLabel = new JLabel(groupString);
    	GridBagConstraints constraints = new GridBagConstraints();
		constraints.weightx = .0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.insets = new Insets(0, 4, 4, 4);
		groupPanel.add(groupLabel, constraints);

    	groupField = new JTextField();
        constraints = new GridBagConstraints();
		constraints.gridx = 1;
		constraints.weightx = 1.0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.insets = new Insets(0, 4, 4, 4);
		groupPanel.add(groupField, constraints);
		
    	JButton groupButton = new JButton(selectGroupString); 
    	groupButton.setMnemonic('S');
    	groupButton.addActionListener(this);
    	
		constraints = new GridBagConstraints();
		constraints.gridx = 1;
		constraints.weightx = .5;
		constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(4, 4, 4, 4);
        groupPanel.add(groupButton, constraints);
		
		return groupPanel;
    }

    	actionButton = new JButton(saveMode ? saveGroupString : loadGroupString); 
    	actionButton.setMnemonic('L');
    	actionButton.addActionListener(this);
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.weightx = .5;
		constraints.anchor = GridBagConstraints.EAST;
        constraints.insets = new Insets(4, 4, 4, 4);
        buttonPanel.add(actionButton, constraints);

    	JPanel groupPanel = createGroupPanel();
		constraints = new GridBagConstraints();
		constraints.gridy = 1;
		constraints.weightx = 1.0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.insets = new Insets(4, 4, 4, 4);
		contentPanel.add(groupPanel, constraints);

		
		} else if (action.equals(loadGroupString)) {
			mapper.setConnectionParameters(getHost(), getDatabase(), getUser(), getPassword());
			try {
				data = mapper.loadDbGroup(getGroup());
				saveSettings();
				setVisible(false);
			} catch (Exception exception) {
				JOptionPane.showMessageDialog(null, "Database error", exception.getMessage(),
						JOptionPane.ERROR_MESSAGE);
			}
		} else if (action.equals(saveGroupString)) {
			mapper.setConnectionParameters(getHost(), getDatabase(), getUser(), getPassword());
			try {
				mapper.saveDbGroup(getGroup());
				saveSettings();
				setVisible(false);
			} catch (Exception exception) {
				 JOptionPane.showMessageDialog(null, "Database error", exception.getMessage(),
						 JOptionPane.ERROR_MESSAGE);
			}

		if (action.equals(connectString)) {
			group group = new group(mapper, this);
			String groupName = group.getGroup();
	    	System.out.println("Gotten:" + groupName);
	    	
			if (groupName != null && !groupName.equals("")) {
				groupField.setText(groupName);
			}
		} else if (action.equals(cancelString)) {
        	groupField.setText(null);
        	data = null;
        	setVisible(false);
		} else if (action.equals(connectString)) {
			// TODO: retrieve group data
		} 
	public void setSaveMode(boolean saveMode) {
		this.saveMode = saveMode;
    	actionButton.setText(saveMode ? saveGroupString : loadGroupString); 
	}
	

*/



/** SQLTableGUI
 * GUI for SQLTableModel,
 * has Reload/Add/Save/Exit buttons
 */
public class SQLTableGUI
{
    protected JDialog top;
    protected SQLTableModel model;
    public JTable table;
    private String selectedGroup = null;

    // Simple apps call run(), see below
    
    public void makeGUI(SQLTableModel _model, String title, JDialog guiContext)
    {
        boolean sort = true; // "compile-time" option

        GUI.init();
        model = _model;
        model.load();

        if (sort)
        {
            TableSorter sorter = new TableSorter(model);
            table = new JTable(sorter);
            sorter.addMouseListenerToHeaderInTable(table);
        }
        else
            table = new JTable(model);

        // "delete" column is smaller than rest
        TableColumnModel cm = table.getColumnModel();
        int num = cm.getColumnCount();
        if (num > 0)
            cm.getColumn(0).setMaxWidth(50);
        for (int i=1; i<num; ++i)
            cm.getColumn(i).setMinWidth(80);

        JScrollPane table_pane = new JScrollPane(table);
        
        JPanel buttons = new JPanel();
        JButton button;
        button = new JButton("Re-Load");
        button.setMnemonic(KeyEvent.VK_R);
        button.addActionListener(new ActionListener()
            {   public void actionPerformed(ActionEvent e)
                {
                    if (model.wasEdited())
                    {
                        switch (JOptionPane.showConfirmDialog(
                            top,
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
                }
            });
        buttons.add(button);

        button = new JButton("Add");
        button.setMnemonic(KeyEvent.VK_A);
        button.addActionListener(new ActionListener()
            {   public void actionPerformed(ActionEvent e)
                {   model.addRecord(); }
            });
        buttons.add(button);

        button = new JButton("Save");
        button.setMnemonic(KeyEvent.VK_S);
        button.addActionListener(new ActionListener()
            {   public void actionPerformed(ActionEvent e)
                {
                    if (model.save())
                    {
                        model.load();
                        model.fireTableDataChanged();
                    }
                }
            });
        buttons.add(button);

        button = new JButton("Exit");
        button.setMnemonic(KeyEvent.VK_X);
        button.addActionListener(new ActionListener()
            {   public void actionPerformed(ActionEvent e)
                {
                    if (model.wasEdited())
                    {
                        switch (JOptionPane.showConfirmDialog(
                            top,
                            "Table has been changed.\nSave changes?",
                            "Confirm Save",
                            JOptionPane.YES_NO_CANCEL_OPTION,
                            JOptionPane.QUESTION_MESSAGE))
                        {
                            case JOptionPane.CANCEL_OPTION:
                                return;
                            case JOptionPane.YES_OPTION:
                                if (model.save() == false)
                                    return; // error in save, don't quit
                        }
                    }

                    int selectedRow = table.getSelectedRow();
                	if (selectedRow == -1) {
                		selectedGroup = null;
                	} else {
                		selectedGroup = table.getValueAt(selectedRow, 1).toString();
                	}
                	System.out.println("Returning:" + getSelectedGroup());
                    top.dispose();
                	System.out.println("Returning:" + getSelectedGroup());

                }
            });
        buttons.add(button);

        top = new JDialog(guiContext, true);
        top.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        top.getContentPane().setLayout(new BorderLayout());

        Dimension size = new Dimension(800, 500);
        table_pane.setPreferredSize(size);
        top.getContentPane().add(table_pane, BorderLayout.CENTER);
        top.getContentPane().add(buttons, BorderLayout.SOUTH);
        top.pack();
    }

    // Might be called between makeGUI and execute:
    public void setColumnEditor(int column, TableCellEditor editor)
    {
        table.getColumnModel().getColumn(column).setCellEditor(editor);
    }
    
    public void execute()
    {
        top.setVisible(true);
    }

    public void run(SQLTableModel _model, String title, JDialog guiContext)
    {
        makeGUI(_model,  title, guiContext);
        execute();
    }

	/**
	 * @return the selectedGroup
	 */
	public String getSelectedGroup() {
		return selectedGroup;
	}
};


