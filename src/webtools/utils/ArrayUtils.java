/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webtools.utils;

import java.util.Arrays;

/**
 *
 * @author Sh4D0W
 */
public class ArrayUtils {
    public static boolean useArraysBinarySearch(String[] arr, String targetValue) {	
	int a =  Arrays.binarySearch(arr, targetValue);
	if(a > 0)
		return true;
	else
		return false;
}
}


