package com.cosylab.vdct.util;

/**
 * This type was created in VisualAge.
 */
public class IntegerQuickSort extends QuickSort {
/**
 * lessThan method comment.
 */
public boolean lessThan(Object oFirst, Object oSecond) {
	int i1 = ((Integer)oFirst).intValue();
	int i2 = ((Integer)oSecond).intValue();
	if (i1 < i2) return true;
	else return false;
}
}
