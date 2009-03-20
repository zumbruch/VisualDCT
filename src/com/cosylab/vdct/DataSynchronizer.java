/**
 * Copyright (c) 2009, Cosylab, Ltd., Control System Laboratory, www.cosylab.com
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

package com.cosylab.vdct;

import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;

import com.cosylab.vdct.graphics.DrawingSurface;
import com.cosylab.vdct.graphics.DsManager;
import com.cosylab.vdct.graphics.objects.Group;
import com.cosylab.vdct.vdb.VDBData;
import com.cosylab.vdct.vdb.VDBTemplate;

/**
 * @author ssah
 *
 */
public class DataSynchronizer {

	private static DataSynchronizer instance = null;
	
	public static DataSynchronizer getInstance() {
		if (instance == null) {
			instance = new DataSynchronizer();
		}
		return instance;
	}

	public VDBTemplate getTemplate(Object dsId, String templateId) {
		
		VDBTemplate template = (VDBTemplate)VDBData.getInstance(dsId).getTemplates().get(templateId);
		if (template == null) {
			// Try to find template in another drawing surface.
			Enumeration drawingSurfaces = DsManager.getAllDrawingSurfaces().elements();
			while (drawingSurfaces.hasMoreElements() && template == null) {
				Object sourceTemplateDsId = ((DrawingSurface)drawingSurfaces.nextElement()).getDsId();
				template = (VDBTemplate)VDBData.getInstance(sourceTemplateDsId).getTemplates().get(templateId);
			}
			if (template != null) {
				File file = new File(template.getFileName());
				boolean loadSuccessful = false;
				try {
					loadSuccessful = DsManager.getDrawingSurface(dsId).open(file, true);
				} catch (Exception exception) {
					Console.getInstance().println("Failed to load template file '" + template.getFileName() + "'.");	
				}
				if (loadSuccessful) {
					template = (VDBTemplate)VDBData.getInstance(dsId).getTemplates().get(templateId);
				}
			}
		}
		if (template == null) {
			Console.getInstance().println("Could not load template '" + templateId + "'.");
		}
		return template;
	}
}
