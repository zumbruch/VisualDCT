/*
 * Copyright (c) 2004 by Cosylab d.o.o.
 *
 * The full license specifying the redistribution, modification, usage and other
 * rights and obligations is included with the distribution of this project in
 * the file license.html. If the license is not included you may find a copy at
 * http://www.cosylab.com/legal/abeans_license.htm or may write to Cosylab, d.o.o.
 *
 * THIS SOFTWARE IS PROVIDED AS-IS WITHOUT WARRANTY OF ANY KIND, NOT EVEN THE
 * IMPLIED WARRANTY OF MERCHANTABILITY. THE AUTHOR OF THIS SOFTWARE, ASSUMES
 * _NO_ RESPONSIBILITY FOR ANY CONSEQUENCE RESULTING FROM THE USE, MODIFICATION,
 * OR REDISTRIBUTION OF THIS SOFTWARE.
 */

package com.cosylab.vdct.archiver;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;


/**
 * <code>ArchiverDialog</code> is a dialog window of the Archiver component.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 * @version $Id$
 *
 * @since VERSION
 */
public class ArchiverDialog extends JDialog
{
	private ArchiverTree tree;
	private ArchiverList list;
	private JButton addToTreeButton;
	private JButton removeFromTreeButton;
	private JButton OKButton;
	private JButton cancelButton;

	/**
	 * Creates a new ArchiverDialog object.
	 */
	public ArchiverDialog()
	{
		super();
		initialize();
	}

	private void initialize()
	{
		this.setContentPane(getPanel());
		this.setModal(true);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.setSize(500, 500);
	}

	private JPanel getPanel()
	{
		JPanel contentPane = new JPanel(new GridBagLayout());

		JPanel archiverPanel = new JPanel(new GridBagLayout());
		tree = new ArchiverTree();

		JScrollPane treePane = new JScrollPane(tree);
		list = new ArchiverList();

		JScrollPane listPane = new JScrollPane(list);

		archiverPanel.add(treePane,
		    new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.WEST,
		        GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
		archiverPanel.add(listPane,
		    new GridBagConstraints(2, 0, 1, 1, 1, 1, GridBagConstraints.EAST,
		        GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));

		//select buttons
		JPanel selectionButtonPanel = new JPanel(new GridBagLayout());
		addToTreeButton = new JButton(new ImageIcon(ArchiverDialog.class.getClassLoader()
			        .getResource("images/Back24.gif")));
		addToTreeButton.setToolTipText("Add record to the tree");
		addToTreeButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e)
				{
					ArchiverTreeRecordNode[] records = list.getSelectedRecords();

					if (records.length != 0) {
						boolean completed = tree.addRecords(records);

						if (completed) {
							for (int i = 0; i < records.length; i++) {
								list.getDefaultModel().removeElement(records[i]);
							}
						}
					}
				}
			});
		selectionButtonPanel.add(addToTreeButton,
		    new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER,
		        GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		removeFromTreeButton = new JButton(new ImageIcon(
			        ArchiverDialog.class.getClassLoader().getResource("images/Forward24.gif")));
		removeFromTreeButton.setToolTipText("Remove record from the tree");
		removeFromTreeButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e)
				{
					ArchiverTreeRecordNode[] records = tree.getSelectionRecords();

					if (records.length != 0) {
						for (int i = 0; i < records.length; i++) {
							list.getDefaultModel().addElement(records[i]);
							tree.getDefaultModel().removeNodeFromParent(records[i]);
						}
					}
				}
			});
		selectionButtonPanel.add(removeFromTreeButton,
		    new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.CENTER,
		        GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));

		archiverPanel.add(selectionButtonPanel,
		    new GridBagConstraints(1, 0, 1, 1, 0, 1, GridBagConstraints.CENTER,
		        GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));

		// OK & Cancel buttons
		JPanel buttonPanel = new JPanel(new GridBagLayout());
		OKButton = new JButton("OK");
		OKButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e)
				{
					saveData();

					//TODO ArchiverDialog.this.dispose();
					System.exit(0);
				}
			});
		OKButton.setIcon(new ImageIcon(ArchiverDialog.class.getClassLoader()
		        .getResource("images/Bookshelf16.gif")));
		OKButton.setPreferredSize(new Dimension(75, 30));
		OKButton.setMinimumSize(new Dimension(75, 30));

		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e)
				{
					ArchiverDialog.this.dispose();
				}
			});
		cancelButton.setIcon(new ImageIcon(ArchiverDialog.class.getClassLoader()
		        .getResource("images/cancel.gif")));
		cancelButton.setPreferredSize(new Dimension(100, 30));
		cancelButton.setMinimumSize(new Dimension(100, 30));

		buttonPanel.add(OKButton,
		    new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER,
		        GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
		buttonPanel.add(cancelButton,
		    new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.EAST,
		        GridBagConstraints.NONE, new Insets(2, 2, 2, 5), 0, 0));

		contentPane.add(archiverPanel,
		    new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.NORTH,
		        GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
		contentPane.add(buttonPanel,
		    new GridBagConstraints(0, 1, 1, 1, 1, 0.05,
		        GridBagConstraints.EAST, GridBagConstraints.NONE,
		        new Insets(5, 5, 5, 5), 0, 0));

		return contentPane;
	}

	/**
	 * Saves all data to an XML file.
	 */
	private void saveData()
	{
		System.out.println("Saving data");

		//TODO implement parsing data to XML file
	}

	/**
	 * Adds records to this component. Records are added as
	 * ArchiverTreeRecordNodes to the ArchiverList (null is not permitted)
	 *
	 * @param records added records
	 */
	public void addRecords(Record[] records)
	{
		for (int i = 0; i < records.length; i++) {
			ArchiverTreeRecordNode trn = new ArchiverTreeRecordNode(records[i]);
			list.getDefaultModel().addElement(trn);
		}
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param args DOCUMENT ME!
	 */
	public static void main(String[] args)
	{
		ArchiverDialog dialog = new ArchiverDialog();
		Record[] record = new Record[15];

		for (int i = 0; i < record.length; i++) {
			record[i] = new Record(i + " record");
		}

		dialog.addRecords(record);
		dialog.show();
	}
}

/* __oOo__ */
