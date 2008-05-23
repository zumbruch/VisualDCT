package com.cosylab.vdct.rdb.group;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

// Base class for a modal Ok/cancel Dialog.
// Buttons are dispatched to onOk(), onCancel(),
// title, content etc. have to be provided.
public class OkCancelDlg extends JDialog
{
    private Frame parent;
    protected String ok_label, cancel_label;
    protected boolean result;
    
    public OkCancelDlg(Frame parent, String title)
    {
        super(parent, title, /* modal */ true);
        this.parent = parent;
        ok_label = "Ok";
        cancel_label = "Cancel";
    }

    //* Call this one to run the dialog.
    //* Will return true for "Ok".
    public boolean run()
    {
        JPanel buttons = new JPanel();
        JButton button = new JButton(ok_label);
        button.setMnemonic(KeyEvent.VK_O);
        button.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {   onOk(); }
            });
        buttons.add(button);
        button = new JButton(cancel_label);
        button.setMnemonic(KeyEvent.VK_C);
        button.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {   onCancel(); }
            });
        buttons.add (button);

        // Layout
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(getContents(), BorderLayout.CENTER);
        getContentPane().add(buttons, BorderLayout.SOUTH);
        pack();
        if (parent!=null)
            setLocation(parent.getLocation());
        show();
        return result;
    }
    
    //* Override this to fill the dialog
    public JComponent getContents()
    {
        return new JLabel("Hello World! Add components here...");
    }
    
    public void onOk()
    {
        result = true;
        dispose();
    }

    public void onCancel()
    {
        result = false;
        dispose();
    }
};

    

