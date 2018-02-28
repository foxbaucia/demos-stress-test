package co.softwarebox.test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class StressTest {

	// Quantity of requests to send
	// private final static Long SIZE = 500000L;
	//private final static Long SIZE = 1000000L;
	private final static Long SIZE = 2000L;
	
	// Number of requests at the same time. 
	private final static Integer RAMP = 60;
	
	// Number of expected results to show at the end.
	private final static Integer TOP = 5;
	
	
	List<String> domains;
	List<String> schemes;
	List<String> routes;
	
	
	public StressTest () throws FileNotFoundException, IOException {
		domains = loadDomains();
		schemes = Arrays.asList(new String[] {"http", "https"});
		routes = Arrays.asList(new String[] {"index.html", "foo/bar/", "page.jsp", "home.html?foo=bar", "route/page.jsp?param=value", "my/route/is/here/action/?param=value&other=value"});
	}
	
	
	
	private void run() {

        final ExecutorService executor = Executors.newFixedThreadPool(RAMP);
        
        ExpectedResult expectedResult = new ExpectedResult();
        		
		for (Long i = 0L ; i < SIZE ; i++) {
			
            
            final UrlTest urlTest = new UrlTest(
        			getRandomString(schemes),
        			getRandomString(domains),
        			getRandomString(routes),
        			expectedResult
        		);
            
            
            executor.execute(urlTest);

			
		}
		
        final boolean shutdownOk = this.shutdownAndAwaitTermination(executor);

        TreeMap<Long, String> orderedList = expectedResult.getOrderedList();
        
		System.out.println("---- EXPECTED RESULTS ----");

		int count = 0;
        for (Entry<Long, String> anStat : orderedList.descendingMap().entrySet()) {
	    		if (count >= TOP) {
	    			break;
	    		}
        		System.out.println(anStat.getValue() + " ---> " + anStat.getKey());
        		count++;
        }
        
        
        if (!shutdownOk) {
            System.out.println("StressTest: Some task didn't finished.");
            Thread.currentThread().interrupt();
        }

	}




	private String getRandomString(List<String> aList) {
		int randomNum = ThreadLocalRandom.current().nextInt(0, aList.size());
		return aList.get(randomNum);
	}
	
	
    private boolean shutdownAndAwaitTermination(final ExecutorService pool) {

        pool.shutdown(); // Disable new tasks from being submitted
        try {
        		System.out.println("=== All tasks loaded, wait to complete ===");
            // Wait a while for existing tasks to terminate
            if (!pool.awaitTermination(1, TimeUnit.HOURS)) {
                pool.shutdownNow(); // Cancel currently executing tasks
                // Wait a while for tasks to respond to being cancelled
                if (!pool.awaitTermination(15, TimeUnit.MINUTES)) {
                		System.out.println("=== StressTest: Pool did not terminate. ===");
                }
            }
        } catch (final InterruptedException ie) {
            // (Re-)Cancel if current thread also interrupted
            pool.shutdownNow();
            // Preserve interrupt status
            return false;
        }
        return true;
    }

	
	/**
	 * 
	 * @param first argument reperestn
	 * 
	 */
	public static void main(String [] args) {
		try {
			
			StressTest st = new StressTest();
			st.run();
		
		} catch (Exception e) {
			System.out.println("Error on application");
			e.printStackTrace();
		}
	}
	


	private List<String> loadDomains () throws FileNotFoundException, IOException {
		
		List<String> domains = new ArrayList<String>();

		// TODO 
		final InputStream resourceAsStream = StressTest.class.getResourceAsStream("urls.txt");
        final BufferedReader br = new BufferedReader(new InputStreamReader(resourceAsStream));

		// BufferedReader br = new BufferedReader(new FileReader("src/main/resources/co/softwarebox/test/urls.txt"));
		try {
			
		    String line = br.readLine();

		    while (line != null) {
		    		domains.add(line);
		    		line = br.readLine();
		    }
		    

		} finally {
		    br.close();
		}
		
		return domains;

	}
}
