
import java.io.FileInputStream;                       //load in the  libraries;
import java.util.Scanner;

public class Readfile {
	//constructor,set file's location as parameter;
	static void ReadFile(String filelocate) {
		GUI.addOpenPort("", "", "");
		try {
			String IP, number, service;
			Scanner scanner = new Scanner(new FileInputStream(filelocate));       //open the stream;
			number = scanner.next();
			while (number.equals("#")) {                                          // '#' is setted as a identifier;
				IP = scanner.next();
				number = scanner.nextLine();
				number = scanner.nextLine();
				while (!(number = scanner.next()).equals("#")) {         
					service = scanner.next();
					GUI.addOpenPort(IP, number, service);                   //print the information to GUI;
					if (!scanner.hasNext())                    						   //end the reading for none to read;
						break;
				}
			}
			scanner.close();                                        						//close;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
