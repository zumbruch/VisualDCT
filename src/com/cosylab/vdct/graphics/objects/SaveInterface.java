package com.cosylab.vdct.graphics.objects;

import java.io.*;

/**
 * Insert the type's description here.
 * Creation date: (22.4.2001 21:45:53)
 * @author: Matej Sekroanja
 */
public interface SaveInterface {
/**
 * Insert the method's description here.
 * Creation date: (22.4.2001 21:46:41)
 * @param file java.io.DataOutputStream
 * @param path2remove java.lang.String
 * @exception java.io.IOException The exception description.
 */
void writeObjects(DataOutputStream file, String path2remove) throws java.io.IOException;
/**
 * Insert the method's description here.
 * Creation date: (22.4.2001 21:47:00)
 * @param file java.io.DataOutputStream
 * @param path2remove java.lang.String
 * @exception java.io.IOException The exception description.
 */
void writeVDCTData(DataOutputStream file, String path2remove) throws java.io.IOException;
}
