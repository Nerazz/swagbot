package mib;

import java.io.*;

public final class log {

	public static void print(String s) {
		try {
			File oFile = new File("./log.log");
			if (!oFile.exists()) {
				oFile.createNewFile();
				System.out.println("log created");
			}
			FileWriter fw = new FileWriter(oFile/*.getAbsoluteFile()*/);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.close();
			System.out.println("log fertig geschrieben");
			
		} catch(IOException e) {
			e.printStackTrace();
		}
		return;	
	}
}
