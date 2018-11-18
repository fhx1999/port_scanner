
import java.io.BufferedWriter; //load in the  libraries;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;

public class Storeinfile {
	private static File file;

	// constructor,set filename as parameter;
	public Storeinfile(String filename) {
		file = new File(filename);
		if (!file.exists()) { // Detection, if error happened in creating, tell the user;
			try {
				file.createNewFile();
			} catch (IOException e) {
				GUI.addInfoText("\nFailed to create log file!");
			}
		}
	}

	// return boolean for next detection;
	public static boolean isWritable() {
		if (file != null && file.exists() && file.canWrite())
			return true;
		return false;
	}

	// saveIP as string;
	void saveIP(String numberstring) {
		if (!isWritable()) { // detection, judge that user can visit or not;
			GUI.addInfoText("Failed to write file!\n");
			return;
		}

		try {
			Calendar c = Calendar.getInstance(); // record the time of saving;
			int year, month, date, hour, minute, second;
			year = c.get(Calendar.YEAR); // get the system time;
			month = c.get(Calendar.MONTH);
			date = c.get(Calendar.DATE);
			hour = c.get(Calendar.HOUR_OF_DAY);
			minute = c.get(Calendar.MINUTE);
			second = c.get(Calendar.SECOND);
			// create and open the inputstream;
			BufferedWriter bw = new BufferedWriter(new FileWriter(file.getAbsoluteFile(), true));
			bw.write("# " + numberstring + " " + "(" + " " + // write data in the file for a special format;
					year + "/" + (month > 9 ? month : ("0" + month)) + "/" + (date > 9 ? date : ("0" + date)) + "   "
					+ (hour > 9 ? hour : ("0" + hour)) + ":" + (minute > 9 ? minute : ("0" + minute)) + ":"
					+ (second > 9 ? second : ("0" + second)) + " " + ")" + "\r\n");
			bw.write("Port	Service\r\n");
			bw.close(); // close stream;
		} catch (Exception e) {
			e.printStackTrace();
		}
	};

	// save portnumber and what kind of service the port provide;
	void SavePortAndService(int port, String service) {
		if (!isWritable())
			return;
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(file.getAbsoluteFile(), true));
			if (port < 10) {
				bw.write(port + "	"); // a special format to save data;
			} else if (port < 100) {
				bw.write(port + "	");
			} else if (port < 1000) {
				bw.write(port + "	");
			} else if (port < 10000) {
				bw.write(port + "	");
			} else
				bw.write(port + "	");
			bw.write(service + "\r\n");
			bw.close(); // close;
		} catch (Exception e) {
			GUI.addInfoText("\nFailed to write file!");
		}
	};

}