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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

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
import javax.swing.filechooser.FileFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * <code>Archiver</code> is the frame of the Archiver application.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 * @version $Id$
 *
 * @since VERSION
 */
public class Archiver extends JFrame
{
	private ArchiverTree tree;
	private ArchiverList list;
	private JButton addToTreeButton;
	private JButton removeFromTreeButton;
	private JButton OKButton;
	private JButton cancelButton;
	private JMenuBar menuBar;
	private JFileChooser fileChooser;
	private DocumentBuilder builder;
	private DocumentBuilderFactory factory;
	private Engine engine;
	private boolean parsingSuccessful = true;
	private File currentFile;

	/**
	 * Creates a new ArchiverDialog object.
	 */
	public Archiver()
	{
		super();
		initialize();
	}

	private void initialize()
	{
		this.setContentPane(getPanel());
		factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(true);
		factory.setIgnoringElementContentWhitespace(true);
		engine = new Engine(this);

		try {
			builder = factory.newDocumentBuilder();
			builder.setErrorHandler(new ErrorHandler() {
					public void error(SAXParseException exception)
						throws SAXException
					{
						if (parsingSuccessful) {
							int i = JOptionPane.showConfirmDialog(Archiver.this,
								    exception + " Shall I continue parsing?",
								    "Parsing Error", JOptionPane.YES_NO_OPTION,
								    JOptionPane.ERROR_MESSAGE);

							if (i == JOptionPane.NO_OPTION) {
								parsingSuccessful = false;
							}
						}
					}

					public void fatalError(SAXParseException exception)
						throws SAXException
					{
						JOptionPane.showMessageDialog(Archiver.this,
						    exception + " Parsing aborted!", "Fatal Error",
						    JOptionPane.ERROR_MESSAGE);
						parsingSuccessful = false;
					}

					public void warning(SAXParseException exception)
						throws SAXException
					{
						int i = JOptionPane.showConfirmDialog(Archiver.this,
							    exception + " Shall I continue parsing?",
							    "Warning", JOptionPane.YES_NO_OPTION,
							    JOptionPane.WARNING_MESSAGE);

						if (i == JOptionPane.NO_OPTION) {
							parsingSuccessful = false;
						}
					}
				});
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}

		//		this.setModal(true);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.setTitle("Archiver");
		this.setSize(500, 500);
		
	}

	private JPanel getPanel()
	{
		JPanel contentPane = new JPanel(new GridBagLayout());

		JPanel archiverPanel = new JPanel(new GridBagLayout());
		tree = new ArchiverTree(this);
		tree.addTreeListener(new TreeListener() {

            public void channelRemoved(ChannelRemovedEvent e) {
                ArchiverTreeChannelNode[] nodes = e.getChannelNode();
                for (int i = 0; i < nodes.length; i++) {
                    list.getDefaultModel().addElement(nodes[i]); 
                }
            }
		    
		});

		JScrollPane treePane = new JScrollPane(tree);
		treePane.setPreferredSize(new Dimension(180,100));
		
		list = new ArchiverList();
		JScrollPane listPane = new JScrollPane(list);
		listPane.setPreferredSize(new Dimension(160,100));
		
		archiverPanel.add(treePane,
		    new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER,
		        GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
		archiverPanel.add(listPane,
		    new GridBagConstraints(2, 0, 1, 1, 1, 1, GridBagConstraints.CENTER,
		        GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));

		//select buttons
		JPanel selectionButtonPanel = new JPanel(new GridBagLayout());
		addToTreeButton = new JButton(new ImageIcon(Archiver.class.getClassLoader()
			        .getResource("images/Back24.gif")));
		addToTreeButton.setToolTipText("Add record to the tree");
		addToTreeButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e)
				{
					ArchiverTreeChannelNode[] records = list.getSelectedRecords();

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
			        Archiver.class.getClassLoader().getResource("images/Forward24.gif")));
		removeFromTreeButton.setToolTipText("Remove record from the tree");
		removeFromTreeButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e)
				{
					ArchiverTreeChannelNode[] records = tree
						.getSelectionRecords();
					if (records == null) {
					    return;
					}
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
						tree.reset();
					}
				});
			fileMenu.add(newItem);

			JMenuItem open = new JMenuItem("Open");
			open.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e)
					{
						openFile();
					}
				});
			fileMenu.add(open);

			JMenuItem save = new JMenuItem("Save");
			save.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e)
					{
					    if (currentFile != null) {
						    saveFile(currentFile);
						} else {
						    saveFileAs();
						}
					}
				});
			fileMenu.add(save);
			
			JMenuItem saveas = new JMenuItem("Save as");
			saveas.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e)
					{
					    saveFileAs();
					}
				});
			fileMenu.add(saveas);

			JMenuItem exit = new JMenuItem("Close");
			exit.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e)
					{
						dispose();
					}
				});

			fileMenu.add(exit);

			menuBar.add(fileMenu);
		}

		return menuBar;
	}

	private void openFile()
	{
		if (getFileChooser().showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
			return;
		}
		tree.reset();
		parsingSuccessful = true;

		File f = getFileChooser().getSelectedFile();

		try {
			if (builder == null) {
				builder = factory.newDocumentBuilder();
			}
			
			Document doc = builder.parse(f);
			
			if (parsingSuccessful) {
				tree.setRoot(engine.parseFromDocument(doc));
				tree.getRoot().getArchiverTreeUserElement().setName(f.getName());
				tree.getDefaultModel().reload();
			}
			currentFile = f;
		} catch (IOException exception) {
			JOptionPane.showMessageDialog(this, exception);
		} catch (ParserConfigurationException exception) {
			JOptionPane.showMessageDialog(this, exception);
		} catch (SAXException exception) {
			JOptionPane.showMessageDialog(this, exception);
		}
	}

	
	private void saveFileAs()
	{
		if (getFileChooser().showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
			return;
		}

		File f = getFileChooser().getSelectedFile();
		String name = f.getAbsolutePath();

		if (!name.endsWith(".xml")) {
			f = new File(name + ".xml");
		}
		saveFile(f);
		
	}
	
	private void saveFile(File file) {
	    try {
			if (builder == null) {
				builder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			}

			Document doc = builder.newDocument();
				
			if (!engine.parseToDocument(doc,
			        (ArchiverTreeNode)tree.getRoot())) {
			    return;
			} 
//			if (engine.parseToDocument(doc,
//			        (ArchiverTreeNode)tree.getDefaultModel().getRoot())) {
				Transformer t = TransformerFactory.newInstance().newTransformer();
				t.setOutputProperty("standalone", "no");
				t.setOutputProperty("doctype-system", Engine.DTD_FILE);
				t.transform(new DOMSource(doc),
				    new StreamResult(new FileOutputStream(file)));
				tree.getRoot().getArchiverTreeUserElement().setName(file.getName());
				tree.getDefaultModel().nodeChanged(tree.getRoot());
				currentFile = file;
				
//			} else {
//				JOptionPane.showMessageDialog(this,
//				    "Check the structure of the document. \n Parsing aborted!",
//				    "Structure invalid", JOptionPane.WARNING_MESSAGE);
//			}
		} catch (TransformerConfigurationException e) {
			JOptionPane.showMessageDialog(this, e);
		} catch (TransformerFactoryConfigurationError e) {
			JOptionPane.showMessageDialog(this, e);
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(this, e);
		} catch (TransformerException e) {
			JOptionPane.showMessageDialog(this, e);
		} catch (ParserConfigurationException e) {
			JOptionPane.showMessageDialog(this, e);
		} catch (FactoryConfigurationError e) {
			JOptionPane.showMessageDialog(this, e);
		}
	}

	private JFileChooser getFileChooser()
	{
		if (fileChooser == null) {
			fileChooser = new JFileChooser();
			fileChooser.setCurrentDirectory(new File("."));

			fileChooser.setFileFilter(new FileFilter() {
					public boolean accept(File f)
					{
						return f.isDirectory()
						|| f.getName().toLowerCase().endsWith(".xml");
					}

					public String getDescription()
					{
						return "XML files";
					}
				});
		}

		return fileChooser;
	}

//	/**
//	 * Adds channels to this component. Channels are added as
//	 * ArchiverTreeChannelNodes to the ArchiverList.
//	 *
//	 * @param channel added record
//	 * @param scanMonitor scan or monitor property
//	 * @param period period property
//	 * @param disable indicator for disable property
//	 */
//	public void addChannel(Channel channel, Property scanMonitor,
//	    Property period, boolean disable)
//	{
//		ArchiverTreeChannelNode atcn = new ArchiverTreeChannelNode(channel);
//		atcn.add(new ArchiverTreeNode(period));
//		atcn.add(new ArchiverTreeNode(scanMonitor));
//
//		if (disable) {
//			atcn.add(new ArchiverTreeNode(new Property("disable", false)));
//		}
//
//		list.getDefaultModel().addElement(atcn);
//	}
	
	/**
	 * Adds channels to this component. Channels are added as
	 * ArchiverTreeChannelNodes to the ArchiverList.
	 *
	 * @param channel added channel
	 */
	public void addChannel(Channel channel) {
	    ArchiverTreeChannelNode atcn = new ArchiverTreeChannelNode(channel);
	    list.getDefaultModel().addElement(atcn);
	    
	}
	
	public void clear() {
	    list.getDefaultModel().clear();
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param args DOCUMENT ME!
	 */
	public static void main(String[] args)
	{
		Archiver dialog = new Archiver();

//		for (int i = 0; i < 15; i++) {
//			Channel ch = new Channel(i + " channel");
//			dialog.addChannel(ch);
//		}
		
//		Vector v = DataProvider.getInstance().getInspectable();
//		for (int i = 0; i < v.size(); i++) {
//		    System.out.println(v.getClass());
//		}
		dialog.show();
		
	}
}

/* __oOo__ */
