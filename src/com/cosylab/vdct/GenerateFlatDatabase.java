package com.cosylab.vdct;

import java.io.File;
import java.io.IOException;

import com.cosylab.vdct.db.DBData;
import com.cosylab.vdct.db.DBResolver;
import com.cosylab.vdct.dbd.DBDResolver;
import com.cosylab.vdct.graphics.DrawingSurface;
import com.cosylab.vdct.graphics.objects.Group;
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
		
			com.cosylab.vdct.dbd.DBDData dbdData = null;
			dbdData = DataProvider.getInstance().getDbdDB();
			dbdData = DBDResolver.resolveDBD(dbdData, "/home/ilist/rf.dbd");
			DataProvider.getInstance().setDbdDB(dbdData);
			
			DBData dbData = null;
			
			try
			{
				dbData = DBResolver.resolveDB("/home/ilist/eclipse/VisualDCT-CapFast/autotune8.vdb");
			} 
			catch(Exception e)
			{
				System.err.println(e);
			}
			
			/*if ((dbData == null) || !dbdData.consistencyCheck(dbData))
			{
				return;
			}*/
			
			DBData.checkDTYPfield(dbData, dbdData);

			/*VDBData vdbData =*/ VDBData.generateVDBData(dbdData, dbData);
			
			VDBTemplate template = (VDBTemplate)VDBData.getTemplates().get(dbData.getTemplateData().getId());
			
			Group.setRoot(template.getGroup());
			
			try {
				Group.save(template.getGroup(), new File("/home/ilist/gen.db"), true);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} finally {
			//System.exit(0);
		}
	}
	
}
