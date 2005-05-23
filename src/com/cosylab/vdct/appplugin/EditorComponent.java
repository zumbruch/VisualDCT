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

package com.cosylab.vdct.appplugin;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.tree.TreeCellEditor;


/**
 * <code>EditorComponent</code> is a component for editing values in the
 * ArchiverTree. Certain nodes from the tree need to present a special value
 * of the Property that they contain. This value can be edited with this
 * editor. The name of the property in the node is displayed by a JLabel,
 * while the value is displayed in the text field on the right. If the node is
 * not obliged to present a special value the label is hidden.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 * @version $Id$
 *
 * @since VERSION
 */
public class EditorComponent extends JPanel
{
	protected JLabel nameLabel;
	protected JTextField valueField;
	private Dimension dim = new Dimension(30, 15);
	protected KeyListener listener;

	/**
	 * Creates a new EditorComponent object.
	 */
	public EditorComponent()
	{
		super();
		initializeComponent();
	}

	private void initializeComponent()
	{
		this.setLayout(new GridBagLayout());

		nameLabel = new JLabel();
		valueField = new JTextField();

		add(nameLabel,
		    new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER,
		        GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		add(valueField,
		    new GridBagConstraints(1, 0, 1, 1, 0.35, 1,
		        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
		        new Insets(0, 0, 0, 0), 0, 0));
	}

	/**
	 * Set up the Editor according to the type of the node that the editor
	 * receive.
	 *
	 * @param node TreeNode to be edited
	 * @param invoker Editor that invoked this method
	 * @param tree DOCUMENT ME!
	 */
	public void setup(final AppTreeNode node, final TreeCellEditor invoker,
	    final AppTree tree)
	{
	    tree.appFrame.setIsFileModified(true);
		final AppTreeElement element = node.getTreeUserElement();

		boolean property = element instanceof Property;
		boolean hasValue = property ? ((Property)element).hasValue() : false;

		KeyListener[] kl = valueField.getKeyListeners();

		for (int i = 0; i < kl.length; i++) {
			valueField.removeKeyListener(kl[i]);
		}

		valueField.addKeyListener(getKeyListener(invoker, tree, element,
		        hasValue));

		if (!hasValue) {
			valueField.setVisible(true);
			valueField.setText(element.getName());
			valueField.setPreferredSize(null);
			nameLabel.setVisible(false);
		} else {
			valueField.setVisible(true);
			valueField.setText(((Property)element).getValue());
			valueField.setPreferredSize(getDimension());
			nameLabel.setText(element.getName());
			nameLabel.setVisible(true);
		}

		valueField.selectAll();
	}

	protected Dimension getDimension()
	{
		return dim;
	}

	protected KeyListener getKeyListener(final TreeCellEditor invoker,
	    final AppTree tree, final AppTreeElement element, boolean hasValue)
	{
		if (hasValue) {
			listener = new KeyAdapter() {
						public void keyPressed(KeyEvent e)
						{
							if (e.getKeyCode() == 10) {
								String text = valueField.getText();

								((Property)element).setValue(text);
								invoker.stopCellEditing();
							}
						}
					};
		} else {
			listener = new KeyAdapter() {
						public void keyPressed(KeyEvent e)
						{
							if (e.getKeyCode() == 10) {
								element.setName(valueField.getText());
								invoker.stopCellEditing();
							}
						}
					};
		}

		return listener;
	}
}

/* __oOo__ */
