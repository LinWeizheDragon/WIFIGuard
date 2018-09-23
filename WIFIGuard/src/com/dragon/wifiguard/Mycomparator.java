package com.dragon.wifiguard;
import java.util.*;
import android.util.Log;

/**
 * ≈≈–Ú¿‡(œ¬‘ÿ≈≈–Ú)
 * @author ¡÷Ïø’‹
 * @version 1.0.0.0
 */
public class Mycomparator implements Comparator{

    public int compare(Object o1,Object o2) {
    	HashMap<String, Object> p1=(HashMap<String, Object>)o1;
    	HashMap<String, Object> p2=(HashMap<String, Object>)o2;  
    	long a=0,b=0;
    	
    	a= Long.parseLong(p1.get("down").toString());
    	b= Long.parseLong(p2.get("down").toString());
    	 if(a<b){
           return 1;}
       else{
           return -1;}
       }

}