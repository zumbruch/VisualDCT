package com.cosylab.vdct.rdb.group;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;


// Dialog for EpicsGroup:
// Shows EpicsGroupTree plus Textboxes for IOC and group
public class EpicsGroupDlg extends OkCancelDlg
{
    private EpicsGroupTree tree;
    private JTextField ioc;
    private JTextField group;
    
    public EpicsGroupDlg(JFrame parent)
    {
        super(parent, "Select Group");
    }

    public JComponent getContents()
    {
        JPanel contents = new JPanel();
        contents.setLayout (new GridBagLayout()); // Oh, no!
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(2, 2, 2, 2);

        tree = new EpicsGroupTree();
        // EpicsGroupListener informs about selected IOC and Group
        tree.addListener(new EpicsGroupTreeListener()
            {
                public void iocSelected(String ioc_id)
                {
                    ioc.setText(ioc_id);
                    group.setText(null);
                }
                public void groupSelected(String ioc_id, String group_id)
                {
                    ioc.setText(ioc_id);
                    group.setText(group_id);
                }
            });
        // Shortcut: Double-click = onOk  (if valid stuff selected)
        tree.addMouseListener(new MouseAdapter()
            {
                public void mouseClicked(MouseEvent e)
                {
                    if (e.getClickCount() == 2 && isValid())
                        onOk();
                }
            });

        tree.setVisibleRowCount(10);
        JScrollPane tree_pane = new JScrollPane(tree);

        // Two columns, as big as possible: tree_pane
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 10;  // really FILL in both directions!
        gbc.weighty = 10;
        contents.add(tree_pane, gbc);
        gbc.gridwidth = 1; // reset to tame filling
        gbc.weightx = 0;
        gbc.weighty = 0;

        // Each row:   :label: :textbox-------------:
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        JLabel label = new JLabel("IOC:");
        label.setDisplayedMnemonic('I');
        contents.add(label, gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        ioc = new JTextField();
        ioc.setToolTipText("Enter IOC or select from tree");
        label.setLabelFor(ioc);
        contents.add(ioc, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        label = new JLabel("Group:");
        label.setDisplayedMnemonic('G');
        contents.add(label, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        group = new JTextField();
        group.setToolTipText("Enter Group or select from tree");
        label.setLabelFor(group);
        contents.add(group, gbc);

        return contents;
    }

    public void onCancel()
    {
        ioc.setText(null);
        group.setText(null);
        super.onCancel();
    }

    private String checkText(String t)
    {    return StringUtil.isValid(t) ? t : null; }
    
    public String getIOC()
    {   return checkText(ioc.getText()); }

    public String getGroup()
    {   return checkText(group.getText()); }

    public boolean isValid()
    {   return getIOC()!=null  &&  getGroup()!=null; }
};

