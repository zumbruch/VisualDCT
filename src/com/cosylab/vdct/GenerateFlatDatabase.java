package com.cosylab.vdct;

import java.io.File;
import java.io.IOException;

import javax.swing.JButton;

import com.cosylab.vdct.db.DBData;
import com.cosylab.vdct.db.DBResolver;
import com.cosylab.vdct.dbd.DBDResolver;
import com.cosylab.vdct.events.CommandManager;
import com.cosylab.vdct.events.commands.NullCommand;
import com.cosylab.vdct.events.commands.SetWorkspaceGroup;
import com.cosylab.vdct.graphics.DrawingSurface;
import com.cosylab.vdct.graphics.objects.Group;
import com.cosylab.vdct.undo.UndoManager;
import com.cosylab.vdct.vdb.VDBData;
import com.cosylab.vdct.vdb.VDBTemplate;

/**
 * @author ilist
 */
public class GenerateFlatDatabase {

	/**
	 * 
	 */
	public GenerateFlatDatabase() {
	}

	public static void main(String[] args) {
		
		try {
			DrawingSurface drawingSurface = new DrawingSurface();
			drawingSurface.open(new File("C:\\Matej\\IJS\\VisualDCT\\TheExample\\test.vdb"));
			Group.save(Group.getRoot(), new File("C:\\flat.db"), true); 
		}
		catch (Throwable t)
		{
			t.printStackTrace();
		} finally {
			System.exit(0);
		}
	}
	
}
