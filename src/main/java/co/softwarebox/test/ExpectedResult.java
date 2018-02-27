package co.softwarebox.test;

import java.util.Hashtable;
import java.util.Map;
import java.util.TreeMap;

public class ExpectedResult {

	private Map<String, Long> stats;

	public ExpectedResult() {
		stats = new Hashtable<String, Long>();
	}
	
	/**
	 * Add to table the count of sent urls 
	 * This method is thread-safe
	 * @param domain
	 */
	public synchronized void  addStat(String domain) { 
	
		Long count = stats.get(domain);
		
		if (count == null) {
			count = new Long(0);
		}
		
		count = count + 1;
		
		stats.put(domain, count);
		
	}
	
	public TreeMap<Long, String> getOrderedList() {
		final TreeMap<Long, String> orderedList = new TreeMap<Long, String>();
		
		stats.entrySet().forEach(anStat -> orderedList.put(anStat.getValue(), anStat.getKey()));
		return orderedList;
		
	}
	
}
