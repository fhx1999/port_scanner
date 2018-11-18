
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import scanner.Port.Type;

public class isPortknown {
	private static File file;
	private static BufferedReader br;
	public static String getServiceByPort (Port p){
		file = new File("port_list.txt");
		try {
			InputStreamReader isr = new InputStreamReader(new FileInputStream(file));
			br = new BufferedReader(isr);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
				//System.out.println("FileNotFound");
			
			return "";
		} 
		String s = null;
		try {
			while ((s = br.readLine()) != null) {
				String[] a = s.split("\\s+");
				int b;
				b = Integer.valueOf(a[0]);
				if(p.getPort() == b) {
					return a[1];
				}
				else if(p.getPort() < b ){
					return "Undefined";
				}
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			br.close();
		} catch (IOException e) {
			//System.out.println("Close");
			e.printStackTrace();
		}
		return "Undefined";
	}
}
