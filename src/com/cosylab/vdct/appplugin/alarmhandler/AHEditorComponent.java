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

package com.cosylab.vdct.appplugin.alarmhandler;

import com.cosylab.vdct.appplugin.AppTree;
import com.cosylab.vdct.appplugin.AppTreeElement;
import com.cosylab.vdct.appplugin.AppTreeNode;
import com.cosylab.vdct.appplugin.EditorComponent;
import com.cosylab.vdct.appplugin.Property;

import sun.awt.font.FontDesignMetrics;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JComboBox;
import javax.swing.tree.TreeCellEditor;


/**
 * <code>AHEditorComponent</code> supplies combo box for those properties that
 * have a priori defined allowed values.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 * @version $Id$
 *
 * @since VERSION
 */
public class AHEditorComponent extends EditorComponent
{
	private Dimension dimen = new Dimension();
	private FontMetrics fm;
	private JComboBox combo;

	/**
	 * TODO DOCUMENT ME!
	 */
	public AHEditorComponent()
	{
		super();
		fm = new FontDesignMetrics(valueField.getFont());
		combo = new JComboBox();
		combo.setEditable(false);

		add(combo,
		    new GridBagConstraints(1, 0, 1, 1, 0.35, 1,
		        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
		        new Insets(0, 0, 0, 0), 0, 0));
		combo.setVisible(false);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param node DOCUMENT ME!
	 * @param invoker DOCUMENT ME!
	 * @param tree DOCUMENT ME!
	 */
	public void setup(final AppTreeNode node, final TreeCellEditor invoker,
	    final AppTree tree)
	{
		super.setup(node, invoker, tree);

		final AppTreeElement element = node.getTreeUserElement();

		if (!(element instanceof Property)) {
			return;
		}

		valueField.setVisible(false);
		combo.setVisible(true);

		Dimension d = getDimension();
		combo.setPreferredSize(new Dimension(d.width + 35, d.height));
		combo.removeAllItems();

		KeyListener[] kl = combo.getKeyListeners();

		for (int i = 0; i < kl.length; i++) {
			combo.removeKeyListener(kl[i]);
		}

		combo.addKeyListener(new KeyAdapter() {
				public void keyPressed(KeyEvent e)
				{
					if (e.getKeyCode() == 10) {
						((Property)element).setValue((String)combo
						    .getSelectedItem());
						invoker.stopCellEditing();
					}
				}
			});

		//status
		if (((Property)element).getName().equals(AHEngine.propertyItems[11])) {
			for (int i = 0; i < AHEngine.statCommands.length; i++) {
				combo.addItem(AHEngine.statCommands[i]);
			}
		} // severity 
		else if (((Property)element).getName().equals(AHEngine.propertyItems[9])) {
			for (int i = 0; i < AHEngine.severityCommands.length; i++) {
				combo.addItem(AHEngine.severityCommands[i]);
			}
		} // beep severity 
		else if (((Property)element).getName().equals(AHEngine.inputFormat[20])) {
			for (int i = 0; i < AHEngine.beepSeverities.length; i++) {
				combo.addItem(AHEngine.beepSeverities[i]);
			}
		} else {
			combo.setVisible(false);
			valueField.setVisible(true);
		}
	}

	protected Dimension getDimension()
	{
		String text = valueField.getText();

		if (text != null) {
			int i = fm.stringWidth(text) + 10;

			if (i < 100) {
				i = 100;
			}

			dimen.width = i;
		} else {
			dimen.width = 100;
		}

		dimen.height = 18;

		return dimen;
	}
}

/* __oOo__ */
