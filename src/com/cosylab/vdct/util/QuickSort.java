package com.cosylab.vdct.util;

import java.util.*;

/**
 * QuickSort algorithm (divide & conquer)
 * !!! replace qs alg with other !!!
 */

public abstract class QuickSort
{
public abstract boolean lessThan(Object oFirst, Object oSecond);      
public void sort(Object[] table) {
	sort(table, 0, table.length - 1);
}      
private void sort(Object[] table, int nLow0, int nHigh0) {
  int nLow = nLow0;
  int nHigh = nHigh0;
  Object pivot;
	  
  if (nHigh0 > nLow0) {  
	pivot = table[(nLow0 + nHigh0)/2];

   while(nLow <= nHigh) {
	 while ((nLow < nHigh0) && lessThan(table[nLow], pivot)) nLow++;
	 while ((nLow0 < nHigh) && lessThan(pivot, table[nHigh])) nHigh--;
	   
	 if (nLow <= nHigh) swap(table, nLow++, nHigh--);
   }
	  
   if (nLow0 < nHigh) sort(table, nLow0, nHigh);
   if (nLow < nHigh0) sort(table, nLow, nHigh0);
  } 
}
/**
 * This method was created in VisualAge.
 * @param e Enumeration
 */
public Object[] sortEnumeration(Enumeration e) {
	Vector tmp = new Vector();
	while (e.hasMoreElements())
		tmp.addElement(e.nextElement());

	Object[] items = new Object[tmp.size()];
	tmp.copyInto(items);
	sort(items);

	return items;
}
private static void swap(Object[] table, int i, int j) {
  Object temp = table[i]; 
  table[i] = table[j];
  table[j] = temp;
}
}
