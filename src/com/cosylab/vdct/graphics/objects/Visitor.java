package com.cosylab.vdct.graphics.objects;

/**
 * Visitor pattern (contains abstract methods for each visitable object;
 * methods are implemented in concrete visitor class)
 * e.g.
 * 		public abstract visitElementA(ElementA element)
 *
 * Creation date: (19.12.2000 18:04:28)
 * @author: Matej Sekoranja
 */
public abstract class Visitor {
/**
 * Visitor constructor comment.
 */
protected Visitor() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (21.12.2000 21:14:49)
 */
public abstract void visitGroup();
}
