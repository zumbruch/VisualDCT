package com.cosylab.vdct.graphics;

import java.beans.*;

public class WorkspacePanelBeanInfo extends SimpleBeanInfo {
public WorkspacePanelBeanInfo() {
	super();
}
public PropertyDescriptor[] getPropertyDescriptors() {
	PropertyDescriptor[] propertySet = new PropertyDescriptor[1];
	try {

		propertySet[0] = new PropertyDescriptor("component", com.cosylab.vdct.graphics.PanelDecorator.class, "getComponent", "setComponent");

	
	} catch (Exception e) {
		System.out.println("o) Introspection exception: " + e);
	}
	return propertySet;
}
}
