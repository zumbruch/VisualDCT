package com.cosylab.vdct.util;

/**
 * This type was created in VisualAge.
 */
public class StringQuickSort extends QuickSort {
/**
 * lessThan method comment.
 */
public boolean lessThan(Object oFirst, Object oSecond) {
	String s1 = oFirst.toString();
	String s2 = oSecond.toString();
	if (s1.compareTo(s2) < 0) return true;
	else return false;
}
}
