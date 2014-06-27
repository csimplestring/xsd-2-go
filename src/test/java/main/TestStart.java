package main;

import org.junit.Test;

public class TestStart {

	@Test
	public void testStart() {
		String pkg = "generated";
		String folder = "/Users/yiwang/Desktop/marble-2404/generated";
		Start s = new Start();
		
		try {
			s.run(folder, pkg);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
