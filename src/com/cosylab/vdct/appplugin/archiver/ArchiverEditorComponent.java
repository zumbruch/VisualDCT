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

package com.cosylab.vdct.appplugin.archiver;

import com.cosylab.vdct.appplugin.AppTree;
import com.cosylab.vdct.appplugin.AppTreeElement;
import com.cosylab.vdct.appplugin.EditorComponent;
import com.cosylab.vdct.appplugin.Group;
import com.cosylab.vdct.appplugin.Property;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JOptionPane;
import javax.swing.tree.TreeCellEditor;


/**
 * <code>ArchiverEditorComponent</code> doesn't allow duplicate group names and
 * all properties' values have to be numbers.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 * @version $Id$
 *
 * @since VERSION
 */
public class ArchiverEditorComponent extends EditorComponent
{
	/**
	 * TODO DOCUMENT ME!
	 */
	public ArchiverEditorComponent()
	{
		super();
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
								double value = 1;
								try {
									value = Double.parseDouble(text);
								} catch (Exception ex) {
									JOptionPane.showMessageDialog(tree,
									    "Value of the " + element.getName()
									    + " should be a number.",
									    "Invalid data",
									    JOptionPane.WARNING_MESSAGE);
									valueField.selectAll();

									return;
								}
								
								if (element.getName().equals(ArchiverEngine.channelProperties[0])) {
								    if (value < 0) {
								        JOptionPane.showMessageDialog(tree,
											    "Value of the " + element.getName()
											    + " cannot be less than 0.",
											    "Invalid data",
											    JOptionPane.WARNING_MESSAGE);
											valueField.selectAll();
								        return;
								    }
								}
								
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
								if (element instanceof Group) {
									String text = valueField.getText();

									if (!text.equals(element.getName())) {
										if (!((ArchiverTree)tree)
										    .isGroupNameUnique(text)) {
											JOptionPane.showMessageDialog(tree,
											    "Group named " + text
											    + " already exists. Rename this group.",
											    "Invalid group name",
											    JOptionPane.WARNING_MESSAGE);
											valueField.selectAll();

											return;
										}
									}
								}

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
