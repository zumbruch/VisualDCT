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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.tree.DefaultTreeCellEditor;


/**
 * <code>EditorComponent</code> is a component with two JTextFields for editing
 * values in the ArchiverTree. Certain nodes from the tree need to present a
 * special value of  the object that it is holding. This value can be edited
 * with this editor. The name of the object in the node is displayed in the
 * left text field, while the value is displayed in the right text field (both
 * fields are editable). If the node is not obliged to present a special value
 * the value text field is hidden.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 * @version $Id$
 *
 * @since VERSION
 */
public class EditorComponent extends JPanel
{
	private JTextField nameField;
	private JTextField valueField;

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

		nameField = new JTextField();
		valueField = new JTextField();

		add(nameField,
		    new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER,
		        GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		add(valueField,
		    new GridBagConstraints(1, 0, 1, 1, 0.35, 1,
		        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
		        new Insets(0, 0, 0, 0), 0, 0));
	}

	/**
	 * Sets up this component according to the values of node's fields. If node
	 * hasValue the valueField of this editor is displayed, otherwise it is
	 * hidden and only nameField is displayed and operative.
	 *
	 * @param node the node that is being edited
	 * @param invoker editor that invoked this method
	 */
	public void setup(final ArchiverTreeNode node,
	    final DefaultTreeCellEditor invoker)
	{
		boolean hasValue = node.hasValue();
		valueField.setVisible(true);
		valueField.setText("");

		KeyListener[] kl = nameField.getKeyListeners();

		for (int i = 0; i < kl.length; i++) {
			nameField.removeKeyListener(kl[i]);
		}

		KeyListener t = new KeyAdapter() {
				public void keyPressed(KeyEvent e)
				{
					if (e.getKeyCode() == 10) {
						node.getTreeUserElement().setName(nameField.getText());
						invoker.stopCellEditing();
					}
				}
			};

		nameField.addKeyListener(t);
		nameField.setText(node.getTreeUserElement().getName());
		nameField.selectAll();

		if (hasValue) {
			kl = valueField.getKeyListeners();

			for (int i = 0; i < kl.length; i++) {
				valueField.removeKeyListener(kl[i]);
			}

			valueField.addKeyListener(t);
			valueField.setText(node.getValue());
			valueField.selectAll();
		} else {
			valueField.setVisible(false);
		}
	}
}

/* __oOo__ */
