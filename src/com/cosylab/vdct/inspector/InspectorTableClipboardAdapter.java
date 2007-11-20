/**
 * Copyright (c) 2002, Cosylab, Ltd., Control System Laboratory, www.cosylab.com
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
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.TransferHandler;

import com.cosylab.vdct.Console;
import com.cosylab.vdct.undo.UndoManager;

/**
 * TableClipboardAdapter enables Cut-Copy-Paste Clipboard functionality on JTables.
 * The clipboard data format used by the adapter is compatible with
 * the clipboard format used by Excel/OpenOffice Calc. This provides for clipboard
 * interoperability between enabled JTables and Excel/OpenOffice Calc.
 * @author <a href="mailto:matej.sekoranjaATcosylab.com">Matej Sekoranja</a>
 * @version $id$
 */
public class InspectorTableClipboardAdapter extends TransferHandler implements ActionListener {

    /**
     * System clipboard.
     */
    private Clipboard clipboardSystem;

    /**
     * Managed table.
     */
    private JTable table;
   
    /**
     * Stored selection data.
     */
    private int numOfSelRows = 0; 
    private int numOfSelCols = 0; 
    private int[] selRows = null; 
    private int[] selCols = null; 
        
    /**
     * Cut action keystroke.
     */ 
    private static final KeyStroke CUT_KEYSTROKE =
        	KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK, false);
    
    /**
     * Copy action keystroke.
     */ 
    private static final KeyStroke COPY_KEYSTROKE =
        	KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK, false);
    
    /**
     * Paste action keystroke.
     */ 
    private static final KeyStroke PASTE_KEYSTROKE =
        KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK, false);

    /**
     * Paste action keystroke.
     */ 
    private static final KeyStroke DEL_KEYSTROKE =
        KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0, false);
    
    /**
     * Cut action name.
     */
    private static final String CUT_ACTION_NAME = "Cut";

    /**
     * Copy action name.
     */
    private static final String COPY_ACTION_NAME = "Copy";

    /**
     * Paste action name.
     */
    private static final String PASTE_ACTION_NAME = "Paste";

    /**
     * Delete action name.
     */
    private static final String DEL_ACTION_NAME = "Delete";

    private static final String EMPTY_STRING = "";
    
    /**
     * The adapter is constructed with a JTable on which it enables
     * Cut-Copy-Paste and acts as a clipboard listener.
     * @param table table on which to enable to Cut-Copy-Paste actions.
     */
    public InspectorTableClipboardAdapter(JTable table) {
        this.table = table;
        
        // register to the table
        int c = JComponent.WHEN_FOCUSED;
        table.registerKeyboardAction(this, CUT_ACTION_NAME, CUT_KEYSTROKE, c);
        table.registerKeyboardAction(this, COPY_ACTION_NAME, COPY_KEYSTROKE, c);
        table.registerKeyboardAction(this, PASTE_ACTION_NAME, PASTE_KEYSTROKE, c);
        table.registerKeyboardAction(this, DEL_ACTION_NAME, DEL_KEYSTROKE, c);
        // cache clipboard system
        clipboardSystem = Toolkit.getDefaultToolkit().getSystemClipboard();
    }

    /**
     * Accessor to the table on which this adapter acts.
     * @return	managed table
     */
    public JTable getTable() {
        return table;
    }

    /**
     * This method is activated on the keystrokes we are listening to in this
     * implementation. Here it listens for Copy and Paste, and Cut commands.
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e)
    {
        if (e.getActionCommand().equals(CUT_ACTION_NAME)) {
            performCopy();
            performDelete();
        } else if (e.getActionCommand().equals(COPY_ACTION_NAME)) {
            performCopy();
        } else if (e.getActionCommand().equals(PASTE_ACTION_NAME)) {
            performPaste();
        } else if (e.getActionCommand().equals(DEL_ACTION_NAME)) {
            performDelete();
        }
    }
    
    protected Transferable createTransferable(JComponent c) {
		Console.getInstance().println("create transferable");
        return new StringSelection(selectionToString());
    }
    
    public int getSourceActions(JComponent c) {
        return COPY_OR_MOVE;
    }
    
    public boolean importData(JComponent c, Transferable t) {
		Console.getInstance().println("import data");
        if (canImport(c, t.getTransferDataFlavors())) {
            try {
                String string = (String)t.getTransferData(DataFlavor.stringFlavor);
                stringToSelection(string);
                return true;
            } catch (UnsupportedFlavorException ufe) {
            } catch (IOException ioe) {
            }
        }
        return false;
    }
    
    protected void exportDone(JComponent c, Transferable data, int action) {
		Console.getInstance().println("export done");
        //cleanup(c, action == MOVE);
    }
    
    public boolean canImport(JComponent c, DataFlavor[] flavors) {

		// when dropping data, use single cell selections
    	if (!table.getColumnSelectionAllowed()) {
	    	table.setColumnSelectionAllowed(true);
		}

    	for (int i = 0; i < flavors.length; i++) {
            if (DataFlavor.stringFlavor.equals(flavors[i])) {
                return true;
            }
        }
        return false;
    }
    
	/* (non-Javadoc)
	 * @see javax.swing.TransferHandler#exportToClipboard(javax.swing.JComponent, java.awt.datatransfer.Clipboard, int)
	 */
	public void exportToClipboard(JComponent comp, Clipboard clip, int action)
			throws IllegalStateException {
		Console.getInstance().println("export to clipboard");
		super.exportToClipboard(comp, clip, action);
	}

	private void refreshSelectionData() {
    	numOfSelRows = table.getSelectedRowCount(); 
        selRows = table.getSelectedRows();
        numOfSelCols = table.getSelectedColumnCount();
        selCols = table.getSelectedColumns();

    	// converts all types of selections to intersection description
    	if (!table.getRowSelectionAllowed() && numOfSelCols > 0) {
    		numOfSelRows = table.getRowCount();
            selRows = new int[numOfSelRows];
            for (int i = 0; i < numOfSelRows; i++) {
            	selRows[i] = i;
            }
    	}
    	if (!table.getColumnSelectionAllowed() && numOfSelRows > 0) {
    		numOfSelCols = table.getColumnCount();
            selCols = new int[numOfSelCols];
            for (int i = 0; i < numOfSelCols; i++) {
            	selCols[i] = i;
            }
    	}
    }

    /**
     * Perform copy/cut action.
     * @param	cut		perform cut.
     */
    private void performCopy() {
    	String string = selectionToString();  
    	if (string.equals("")) {
    		return;
    	}
    	
        StringSelection stringSelection = new StringSelection(string);
        clipboardSystem = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboardSystem.setContents(stringSelection, stringSelection);
    }
    
    /**
     * Perform paste action.
     */
    private void performPaste() {

        try {
        	String string = (String)clipboardSystem.getContents(this).getTransferData(DataFlavor.stringFlavor);
        	stringToSelection(string);
        } catch (UnsupportedFlavorException ufe) {
        } catch (IOException ioe) {
        }
    }
    /**
     * Perform delete action.
     * Deletes the content of selection.
     */
    private void performDelete() {
        
        refreshSelectionData();
        // noop check
        if (numOfSelRows == 0 || numOfSelCols == 0) {
            return;
        }
        boolean deleted = false;
        
        // undo support (to pack all into one action)
    	try {
            for (int i = 0; i < numOfSelRows; i++) {
                for (int j = 0; j < numOfSelCols; j++) {
                	int row = selRows[i];
                	int col = selCols[j];
                    if (table.isCellEditable(row, col)) {
                        if (!deleted) {
                        	deleted = true;
                			UndoManager.getInstance().startMacroAction();
                        }
                    	table.setValueAt(EMPTY_STRING, row, col);
                    }    
                }
            }
    	} finally {
   		    if (deleted) {
   		        UndoManager.getInstance().stopMacroAction();
   		    }
   		}
    }

    private String selectionToString() {

    	refreshSelectionData();
        
        // noop check
        if (numOfSelRows == 0 || numOfSelCols == 0) {
            return "";
        }

        StringBuffer buffer = new StringBuffer();

        // construct string
        for (int i = 0; i < numOfSelRows; i++) {
        	for (int j = 0; j < numOfSelCols; j++) {
        		int row = selRows[i];
        		int col = selCols[j];

        		// the first column does not contain values in inspector and spreadheet
        		if (col == 0) {
        			continue;
        		}

        		buffer.append(table.getValueAt(row, col));
        		if (j < numOfSelCols - 1) {
        			buffer.append("\t");
        		}
        	}
        	buffer.append("\n");
        }

    	Console.getInstance().println("exporting:" + buffer.toString());
        
        return buffer.toString();
    }

    /**
     * Selections comprising non-adjacent cells result in invalid selection and
     * then copy action cannot be performed. Paste is done by aligning the upper
     * left corner of the selection with the 1st element in the current
     * selection of the JTable.
     */
    private void stringToSelection(String string) {

    	Console.getInstance().println("importing:" + string);
    	refreshSelectionData();

        // Check whether selection exists. If it does not, currently do nothing.
        if (numOfSelRows == 0 || numOfSelCols == 0) {
            return;
        }

    	// skip pasting in the first column, empty with inspector and spreadsheet
        int startCol = Math.max(selCols[0], 1);
        int startRow = selRows[0];
        
        int rowCount = table.getRowCount();
        int columnCount = table.getColumnCount();

        boolean pasted = false;
        
        // w/ packed undo support
        try {
            // acknowledge trailing empty lines; the last is explicitly ignored later    
        	String[] rowStrings = string.split("\\n", -1);

            /* Calculates the maximum fields length to foil Excel's random omission of trailing empty spaces. 
             */ 
            int maxCols = 0;
            for (int i = 0; i < rowStrings.length - 1; i++) {
            	maxCols = Math.max(maxCols, rowStrings[i].split("\\t", -1).length);
            }
            
            int i = 0;
            int j = 0;
            int row = 0;
            int col = 0;
            String value = null;
            for (i = 0; i < rowStrings.length - 1; i++) {
                String[] fields = rowStrings[i].split("\\t", maxCols);
                row = i + startRow; 

                for (j = 0; j < maxCols; j++) {
                	col = j + startCol;
                	
	                if (row >= rowCount || col >= columnCount) {
	                	continue;
	                }
               	    if (!pasted) {
               	        pasted = true;
               			UndoManager.getInstance().startMacroAction();
               	    }
               	    value = j < fields.length ? fields[j] : EMPTY_STRING; 
                    table.setValueAt(value, row, col);
                }
            }
    	} finally {
    	    if (pasted) {
    	        UndoManager.getInstance().stopMacroAction();
    	    }
    	}
    }
}
