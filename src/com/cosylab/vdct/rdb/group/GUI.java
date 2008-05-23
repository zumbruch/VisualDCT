package com.cosylab.vdct.rdb.group;

import javax.swing.UIManager;

/** GUI:
 * Common GUI stuff
 */
public class GUI
{
    /** Call in every GUI tool to set Look & Feel
     */
    public static void init()
    {
        try
        {
            UIManager.setLookAndFeel(
                "javax.swing.plaf.metal.MetalLookAndFeel");
        }
        catch (Exception e) {}
    }
};
