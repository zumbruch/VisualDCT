/**
 * Copyright (c) 2007, Cosylab, Ltd., Control System Laboratory, www.cosylab.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer. 
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation 
 * and/or other materials provided with the distribution. 
 * Neither the name of the Cosylab, Ltd., Control System Laboratory nor the names
 * of its contributors may be used to endorse or promote products derived 
 * from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, 
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.cosylab.vdct.inspector;

import java.awt.Dialog;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

/**
 * @author ssah
 *
 */
public class CustomSplitDialog extends JDialog {

	public CustomSplitDialog(Dialog dialog) {
        super(dialog, true);
		createGUI();
    }
	
	/** This method is called from within the constructor to
     * initialize the form.
     */
    private void createGUI() {

    	JButton okButton = new JButton(); 
    	okButton.setMnemonic('O');
    	okButton.setText("OK");
    	okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	setVisible(false);
            }
        });

    	JButton cancelButton = new JButton(); 
    	cancelButton.setMnemonic('C');
    	cancelButton.setText("Cancel");
    	cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	setVisible(false);
            }
        });
    	

    	JLabel helpLabel = new JLabel();
		helpLabel.setHorizontalAlignment(SwingConstants.LEFT);
		helpLabel.setHorizontalTextPosition(SwingConstants.CENTER);

		
    	
		JPanel panel = new JPanel(new GridBagLayout());
    	
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.insets = new Insets(0, 0, 4, 4);
		panel.add(okButton, constraints);

		constraints = new GridBagConstraints();
		constraints.gridx = 1;
		constraints.insets = new Insets(0, 0, 4, 4);
		panel.add(cancelButton, constraints);
		
		setTitle("Custom split");
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		getContentPane().add(panel);
		pack();
    }
}
