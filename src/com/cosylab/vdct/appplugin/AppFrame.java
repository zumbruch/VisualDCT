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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;


/**
 * <code>Archiver</code> is the frame of the Archiver application.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 * @version $Id$
 *
 * @since VERSION
 */
public abstract class AppFrame extends JFrame
{
	protected AppTree tree;
	protected AppList list;
	private JButton addToTreeButton;
	private JButton removeFromTreeButton;
	private JButton OKButton;
	private JButton cancelButton;
	private JMenuBar menuBar;
	protected JFileChooser fileChooser;
	protected File currentFile;
	protected Engine engine;

//	private boolean exitOnClose;
//	private WindowListener exitListener;
	private WindowListener disposeListener;
	    

	/**
	 * Creates a new ArchiverDialog object.
	 */
	public AppFrame()
	{
		super();
		initialize();
	}

	private void initialize()
	{
//	    exitListener = new WindowAdapter() {
//	        public void windowClosing(WindowEvent e) {
//		        if (askForSave()) {
//		            System.exit(0);
//		        } 
//	        }
//	    };
	    
	    disposeListener = new WindowAdapter() {
	        public void windowClosing(WindowEvent e) {
		        if (askForSave()) {
		            dispose();
		        } 
	        }
	    };
	    
		this.setContentPane(getPanel());
		this.setSize(500, 500);
		this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(disposeListener);
//		setExitOnClose(false);
		initialization();
	}

	protected abstract void initialization();

	public abstract AppTree getTree();

	public abstract AppList getList();

	private JPanel getPanel()
	{
		JPanel contentPane = new JPanel(new GridBagLayout());

		JPanel archiverPanel = new JPanel(new GridBagLayout());

		tree = getTree();
		list = getList();

		getTree().addTreeListener(new TreeListener() {
				public void channelRemoved(ChannelRemovedEvent e)
				{
					AppTreeChannelNode[] nodes = e.getChannelNode();

					for (int i = 0; i < nodes.length; i++) {
						getList().getDefaultModel().addElement(nodes[i]);
					}
				}
			});

		JScrollPane treePane = new JScrollPane(getTree());
		treePane.setPreferredSize(new Dimension(180, 100));

		JScrollPane listPane = new JScrollPane(getList());
		listPane.setPreferredSize(new Dimension(160, 100));

		archiverPanel.add(treePane,
		    new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER,
		        GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
		archiverPanel.add(listPane,
		    new GridBagConstraints(2, 0, 1, 1, 1, 1, GridBagConstraints.CENTER,
		        GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));

		//select buttons
		JPanel selectionButtonPanel = new JPanel(new GridBagLayout());
		addToTreeButton = new JButton(new ImageIcon(AppFrame.class.getClassLoader()
			        .getResource("images/Back24.gif")));
		addToTreeButton.setToolTipText("Add record to the tree");
		addToTreeButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e)
				{
					AppTreeChannelNode[] records = getList().getSelectedRecords();

					if (records.length != 0) {
						boolean completed = getTree().addRecords(records);

						if (completed) {
							for (int i = 0; i < records.length; i++) {
								getList().getDefaultModel().removeElement(records[i]);
							}
						}
					}
				}
			});
		selectionButtonPanel.add(addToTreeButton,
		    new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER,
		        GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		removeFromTreeButton = new JButton(new ImageIcon(
			        AppFrame.class.getClassLoader().getResource("images/Forward24.gif")));
		removeFromTreeButton.setToolTipText("Remove record from the tree");
		removeFromTreeButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e)
				{
					AppTreeChannelNode[] records = getTree().getSelectionRecords();

					if (records == null) {
						return;
					}

					if (records.length != 0) {
						for (int i = 0; i < records.length; i++) {
							getList().getDefaultModel().addElement(records[i]);
							getTree().getDefaultModel().removeNodeFromParent(records[i]);
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
		/*        JPanel buttonPanel = new JPanel(new GridBagLayout());
		        OKButton = new JButton("OK");
		        OKButton.addActionListener(new ActionListener() {
		                public void actionPerformed(ActionEvent e)
		                {
		                    saveData();

		                    //TODO ArchiverDialog.this.dispose();
		                    System.exit(0);
		                }
		            });
		        OKButton.setIcon(new ImageIcon(Archiver.class.getClassLoader()
		                .getResource("images/Bookshelf16.gif")));
		        OKButton.setPreferredSize(new Dimension(75, 30));
		        OKButton.setMinimumSize(new Dimension(75, 30));

		        cancelButton = new JButton("Cancel");
		        cancelButton.addActionListener(new ActionListener() {
		                public void actionPerformed(ActionEvent e)
		                {
		                    Archiver.this.dispose();
		                }
		            });
		        cancelButton.setIcon(new ImageIcon(Archiver.class.getClassLoader()
		                .getResource("images/cancel.gif")));
		        cancelButton.setPreferredSize(new Dimension(100, 30));
		        cancelButton.setMinimumSize(new Dimension(100, 30));

		        buttonPanel.add(OKButton,
		            new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER,
		                GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
		        buttonPanel.add(cancelButton,
		            new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.EAST,
		                GridBagConstraints.NONE, new Insets(2, 2, 2, 5), 0, 0));
		*/
		contentPane.add(archiverPanel,
		    new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.NORTH,
		        GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));

		/*    contentPane.add(buttonPanel,
		        new GridBagConstraints(0, 1, 1, 1, 1, 0.05,
		            GridBagConstraints.EAST, GridBagConstraints.NONE,
		            new Insets(5, 5, 5, 5), 0, 0));*/
		setJMenuBar(getMenu());

		return contentPane;
	}

	private JMenuBar getMenu()
	{
		if (menuBar == null) {
			menuBar = new JMenuBar();

			JMenu fileMenu = new JMenu("File");

			JMenuItem newItem = new JMenuItem("New");
			newItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e)
					{
						if (askForSave()) {
							getTree().reset();
							currentFile = null;
						}
					}
				});
			fileMenu.add(newItem);

			JMenuItem open = new JMenuItem("Open");
			open.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e)
					{
						if (askForSave()) {
							openFile();
						}
					}
				});
			fileMenu.add(open);

			JMenuItem save = new JMenuItem("Save");
			save.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e)
					{
						if (currentFile != null) {
							saveFile(currentFile, getTree().getRoot());
						} else {
							saveFileAs(getTree().getRoot());
						}
					}
				});
			fileMenu.add(save);

			JMenuItem saveas = new JMenuItem("Save as");
			saveas.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e)
					{
						saveFileAs(getTree().getRoot());
					}
				});
			fileMenu.add(saveas);

			JMenuItem exit = new JMenuItem("Close");
			exit.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e)
					{
					    if (askForSave()) {
					        dispose();
					    }
					}
				});

			fileMenu.add(exit);

			menuBar.add(fileMenu);
		}

		return menuBar;
	}

	/**
	 * Opens the file and returns the file.getName().
	 *
	 * @return
	 */
	public synchronized String openFile()
	{
		if (getFileChooser().showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
			return null;
		}

		getTree().reset();

		File f = getFileChooser().getSelectedFile();

		if (!fileApproved(f)) {
			return null;
		}

		AppTreeNode node = getEngine().openFromFile(f);

		if (node != null) {
			getTree().setRoot(node);
			getTree().getDefaultModel().reload();
			currentFile = f;

			return f.getName();
		}

		return null;
	}

	/**
	 * Returns true if the file is approved.
	 *
	 * @param f
	 *
	 * @return
	 */
	protected abstract boolean fileApproved(File f);

	/**
	 * If the file specified by JFileChooser doesn't have the extension this
	 * method should provide the extension. Either a new file with the same
	 * name + extension should be created or the same file should be returned
	 * if the appropriate extension already exists.
	 *
	 * @param f
	 *
	 * @return
	 */
	protected abstract File addExtension(File f);

	/**
	 * Method saves the data provided by AppTreeNode source to the file
	 * specified by the JFileChooser.
	 *
	 * @param source
	 *
	 * @return
	 */
	public File saveFileAs(AppTreeNode source)
	{
		if (getFileChooser().showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
			return null;
		}

		File f = getFileChooser().getSelectedFile();
		String name = f.getAbsolutePath();

		f = addExtension(f);

		if (saveFile(f, source)) {
			return f;
		}

		return null;
	}

	/**
	 * Saves the AppTreeNode source's data to the specified file.
	 *
	 * @param file destination file
	 * @param source data source
	 *
	 * @return true if successful
	 */
	public synchronized boolean saveFile(File file, AppTreeNode source)
	{
		boolean success = getEngine().saveToFile(file, source);

		if (success) {
			getTree().getDefaultModel().nodeChanged(getTree().getRoot());
			currentFile = file;
		}

		return success;
	}

	/**
	 * Returns the engine of this application.
	 *
	 * @return
	 */
	public abstract Engine getEngine();

	protected JFileChooser getFileChooser()
	{
		if (fileChooser == null) {
			fileChooser = new JFileChooser();
			fileChooser.setCurrentDirectory(new File("."));
		}

		return fileChooser;
	}

	/**
	 * Adds channels to this component. Channels are added as
	 * ArchiverTreeChannelNodes to the ArchiverList.
	 *
	 * @param channel added channel
	 */
	public void addChannel(Channel channel)
	{
		AppTreeChannelNode atcn = new AppTreeChannelNode(channel);
		getList().getDefaultModel().addElement(atcn);
	}
	
	/**
	 * Clears the list of this frame.
	 */
	public void clear()
	{
		getList().getDefaultModel().clear();
	}

	/**
	 * Returns currently edited file.
	 *
	 * @return the file
	 */
	public File getCurrentFile()
	{
		return currentFile;
	}

	/**
	 * Shows a confirm dialog asking user to save data before opening a new
	 * file. Returns true if the tree should be reset after the dialog has
	 * been confirmed. Tree is reset if the user choose YES or NO, but is not
	 * is he chooses CANCEL.
	 *
	 * @return true flag indicating whether changes have to be made (yes or no was pressed)
	 */
	protected synchronized boolean askForSave()
	{
		int i = JOptionPane.showConfirmDialog(this, "Save current data?",
			    "Caution", JOptionPane.YES_NO_CANCEL_OPTION,
			    JOptionPane.WARNING_MESSAGE);

		switch (i) {
		case JOptionPane.YES_OPTION: {
			if (currentFile != null) {
				saveFile(currentFile, getTree().getRoot());
			} else {
				saveFileAs(getTree().getRoot());
			}

			return true;
		}

		case JOptionPane.NO_OPTION:return true;

		case JOptionPane.CANCEL_OPTION:return false;

		default:return false;
		}
	}
	
//	public boolean getExitOnClose() {
//	    return exitOnClose;
//	}
//	
//	public void setExitOnClose(boolean exitOnClose) {
//	    if (exitOnClose == this.exitOnClose) {
//	        return;
//	    }
//	    this.exitOnClose = exitOnClose;
//	    if (exitOnClose) {
//	        this.addWindowListener(exitListener);
//			this.removeWindowListener(disposeListener);
//	    } else {
//	        this.removeWindowListener(exitListener);
//	        this.addWindowListener(disposeListener);
//	    }
//	}
}

/* __oOo__ */
