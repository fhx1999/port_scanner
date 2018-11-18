
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import scanner.Port.Type;

public final class PortScanner {

	public static final int MAX_PORT_SIZE = 65536;
	private static final int DEFAULT_THREAD_SIZE = 16;
	private static final int TIME_OUT = 2000; // timeout for each connect to port
	private static InetAddress addr = null;
	private final List<Port> openPorts = new ArrayList<Port>();
	int chunkSize = DEFAULT_THREAD_SIZE; // chunkSize is the number of ports contained in each thread
	private int max_startPort = 0, max_endPort = MAX_PORT_SIZE;
	private int count_threads = 0; // the number of threads that has already completed
	private int[] count_ports; // the ports which had been scanned of certain thread
	public static String OS = ""; // the OS
	Thread[] tArr;

	public PortScanner(String addr_name, int startPort, int endPort) {
		if (startPort > 0 && endPort < MAX_PORT_SIZE && startPort <= endPort) {
			try {
				addr = InetAddress.getByName(addr_name);
				max_startPort = startPort;
				max_endPort = endPort;
				GUI.isScanning = true;
				scan();
			} catch (UnknownHostException ex) {
				GUI.addInfoText(addr_name + "is unknownHost !!! Please enter another Host IP and try again!\n");
			}

		} else {
			GUI.addInfoText("The port number is illegal ! Please select again!");
		}

	}

	public void setChunkSize(int chunkSize) {
		if (chunkSize <= 1024 && chunkSize > 1)
			this.chunkSize = chunkSize;
	}

	private synchronized void addOpenPort(Port p) {
		openPorts.add(p);
	}

	private void scan() {
		// Threading logic:
		int startPort = max_startPort, endPort = max_startPort;
		int num = (max_endPort - max_startPort + chunkSize) / chunkSize;
		tArr = new Thread[num];
		count_ports = new int[num];

		for (int i = 0; endPort < max_endPort; i++, startPort += chunkSize) {
			if (max_endPort - endPort < chunkSize) { // Last iteration
				endPort = max_endPort + 1;
			} else {
				endPort += chunkSize;
			}
//			GUI.addInfoText("\nScanning from\t" + startPort + "\tto\t" + (endPort - 1));
			tArr[i] = new Thread(new PortScanThread(startPort, endPort - 1, i));
			tArr[i].start();
		}
		if (!getTTL())
			GUI.addInfoText("Ping Failed!\n");
		if (null != GUI.store)
			GUI.store.saveIP(addr.toString() + "(OS:" + OS + ")\t");

		for (Thread t : tArr) {
			try {
//				GUI.addInfoText(t + "Join\n");
				t.join();
				count_threads++;
				GUI.setProgressBar(count_threads * 100 / num);
			} catch (InterruptedException ex) {
				GUI.addInfoText("JOIN failed\n");
			}
		}
		GUI.isScanning = false;
		GUI.isPaused = false;
		GUI.logfileto.setEditable(true);
		GUI.ipBox.setEditable(true);
		Collections.sort(openPorts);
		for (Port p : openPorts) { // log to file
			if (null != GUI.store) {
				GUI.store.SavePortAndService(p.getPort(),  isPortknown.getServiceByPort(p));
			}
		}
	}

	public void PauseScan() {
		for (Thread t : tArr) {
			t.interrupt();
		}
	}

	public void ContinueScan() {
		for (Thread t : tArr) {
			try {
				t.start();
			} catch (IllegalThreadStateException e) {
				GUI.addInfoText("Thread" + t.toString() + "Already Started!\n");
			}
		}
	}

	private class PortScanThread implements Runnable {
		private int startPort, endPort, index;

		PortScanThread(int startPort, int endPort, int index) {
			this.startPort = startPort;
			this.endPort = endPort;
		}

		@Override
		public void run() {

			for (int i = startPort + count_ports[index]; i <= endPort; i++) {
				try {
					Socket s = new Socket();
					s.connect(new InetSocketAddress(addr, i), TIME_OUT);
					GUI.addOpenPort(addr.toString() + "(OS:" + OS + ')', Integer.toString(i), 
							isPortknown.getServiceByPort(new Port(i,Type.TCP)));
					s.close();
					addOpenPort(new Port(i, Port.Type.TCP));

				} catch (IOException ex) {

				} finally {
					count_ports[index]++; //count the ports which has already scanned of this thread
					if (count_ports[index] > endPort - startPort + 1)
						count_ports[index]--;
				}

			}

		}

	}

	private boolean getTTL() {
		BufferedReader in = null;
		Runtime r = Runtime.getRuntime();
		String pingCommand = "ping " + GUI.ipname + " -n " + 1; 
		String line;
		String ttlFlag = "TTL=";
		Process process;
		GUI.addInfoText("\n" + pingCommand);
		try {
			process = r.exec(pingCommand);

			if (process == null) {
				return false;
			}
			in = new BufferedReader(new InputStreamReader(process.getInputStream()));

			while ((line = in.readLine()) != null) {	//get the TTL value
				GUI.addInfoText(line + '\n');
				if (line.indexOf(ttlFlag) > 0) {
					int index = line.indexOf(ttlFlag) + ttlFlag.length();
					while (index < line.length()) {
						if (line.charAt(index) < '0' || line.charAt(index) > '9')
							break;
						index++;
					}
					OS = getOSByTTL(Integer.parseInt(line.substring(line.indexOf(ttlFlag) + 4, index)));
					GUI.addInfoText("OS:" + OS + '\n');
				}
			}
			in.close();
		} catch (IOException e) {
			GUI.addInfoText("Fail to get TTL\n");
		}
		return true;
	}

	private String getOSByTTL(int ttl) {
		if (ttl > 128)
			return "Linux/Unix/Sun Solaris";
		else if (ttl <= 128 && ttl > 64)
			return "Windows_NT/2000/XP";
		else if (ttl <= 64 && ttl > 32)
			return "Linux_Kernel";
		else if (ttl < 32)
			return "Window_98";
		return "";

	}
}
