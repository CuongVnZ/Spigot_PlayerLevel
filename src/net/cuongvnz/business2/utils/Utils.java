/*
 *	  ___  _   _                     ____________ _____
 *	 / _ \| | | |                    | ___ \ ___ \  __ \
 *	/ /_\ \ |_| |__   ___ _ __   __ _| |_/ / |_/ / |  \/
 *	|  _  | __| '_ \ / _ \ '_ \ / _` |    /|  __/| | __
 *	| | | | |_| | | |  __/ | | | (_| | |\ \| |   | |_\ \
 *	\_| |_/\__|_| |_|\___|_| |_|\__,_\_| \_\_|    \____/
 *
 */
package net.cuongvnz.business2.utils;

import java.util.*;
import java.util.Map.Entry;

public class Utils {
	public static ArrayList<Object> getPage(ArrayList<Object> list, int page) {
    	ArrayList<Object> pagetemp = new ArrayList<Object>();
    	int remain = 45*(page+1);
    	if(list.size() < 1) {
    		remain = 0;
    	}else if((page+1)*45 > list.size()) remain = list.size()%45 + 45*page;
    	for(int i = 45*page; i < remain; i++) {
    		pagetemp.add(list.get(i));
    	}
    	return pagetemp;
	}
	
    public static Map<Object, Integer> sortByComparator(Map<Object, Integer> unsortMap, final boolean order)
    {
        List<Entry<Object, Integer>> list = new LinkedList<Entry<Object, Integer>>(unsortMap.entrySet());
        // Sorting the list based on values
        Collections.sort(list, new Comparator<Entry<Object, Integer>>()
        {
            public int compare(Entry<Object, Integer> o1,
                    Entry<Object, Integer> o2)
            {
                if (order)
                {
                    return o1.getValue().compareTo(o2.getValue());
                }
                else
                {
                    return o2.getValue().compareTo(o1.getValue());
                }
            }
        });
        // Maintaining insertion order with the help of LinkedList
        Map<Object, Integer> sortedMap = new LinkedHashMap<Object, Integer>();
        for (Entry<Object, Integer> entry : list)
        {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }
}
