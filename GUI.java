
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class GUI extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3408389562186343101L;
	static boolean isScanning, isPaused;
	static String ipname;
	private static JButton startButton = new JButton("Start");
	private static JButton pauseButton = new JButton("Pause");
	static JButton endButton = new JButton("End");

	static JTextField logfileto = new JTextField(); // Defines where to fill in the file path.
	private static JButton choose_log = new JButton("Locate");
	private static JButton btnFile = new JButton("OpenFile");

	private static JButton btnSettings = new JButton("Settings");
	private static JButton btnHelp = new JButton("Help");
	private static JSpinner maxPort = new JSpinner();
	private static JSpinner minPort = new JSpinner();
	@SuppressWarnings("rawtypes")
	static JComboBox ipBox = new JComboBox();

	private static JTextArea iptextArea = new JTextArea();
	private static JTextArea porttextArea = new JTextArea();
	private static JTextArea servicetextArea = new JTextArea();
	private static JTextArea infotextArea = new JTextArea();

	private static Font font = new Font("Times New Roman", Font.BOLD, 16);
	// Define progress bar
	private static JProgressBar progressBar = new JProgressBar();

	static String savepath; // Save path

	// Instantiation of stored file classes
	static Storeinfile store;

	// There are two internal methods, which need to be used for formatting of files
	// (for each IP).；
	// The default name is Scanresult.txt.

	public GUI() {
		setBackground(Color.LIGHT_GRAY);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 785, 518);

		JPanel contentPane = new JPanel(); // General panel
		contentPane.setBackground(Color.LIGHT_GRAY);
		contentPane.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JPanel panel = new JPanel();
		panel.setBorder(new BevelBorder(BevelBorder.RAISED, Color.GRAY, Color.GRAY, Color.GRAY, Color.GRAY));
		panel.setBackground(Color.LIGHT_GRAY);
		panel.setBounds(15, 5, 744, 121);
		contentPane.add(panel);
		panel.setLayout(null);

		// Here is the place to fill in the IP address.
		ipBox.setFocusable(true);
		ipBox.setEditable(true);
		ipBox.setBounds(10, 34, 198, 22);
		ipBox.setFont(font);
		panel.add(ipBox);

		minPort.setBounds(235, 33, 117, 23); // Fill in the initial port number.
		minPort.setValue(1);
		minPort.setFont(font);
		panel.add(minPort);
		minPort.addChangeListener(new ChangeListener() {
			private int curValue;

			@Override
			public void stateChanged(ChangeEvent e) {
				curValue = Integer.parseInt(minPort.getValue().toString());
				if (curValue >= Integer.parseInt(maxPort.getValue().toString())) { // 不允许比右端大
					minPort.setValue(Integer.parseInt(maxPort.getValue().toString()));
				}
				if (curValue <= 0) {
					minPort.setValue("0");
				}
			}

		});

		maxPort.setBounds(351, 33, 133, 23);// Fill in termination port number
		maxPort.setValue(1);
		maxPort.setToolTipText("Cannot be exceeding " + PortScanner.MAX_PORT_SIZE);
		maxPort.setFont(font);
		panel.add(maxPort);
		maxPort.addChangeListener(new ChangeListener() {
			private int curValue;

			@Override
			public void stateChanged(ChangeEvent e) {
				curValue = Integer.parseInt(maxPort.getValue().toString());
				if (curValue >= PortScanner.MAX_PORT_SIZE) {
					maxPort.setValue(PortScanner.MAX_PORT_SIZE);
				} else if (curValue < Integer.parseInt(minPort.getValue().toString())) { // It is not allowed to be
																							// smaller than the left
																							// side.
					maxPort.setValue(Integer.parseInt(minPort.getValue().toString()));
				}
			}

		});

		startButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// Start scan button trigger
				if (isScanning)
					return;
				ipname = ipBox.getSelectedItem().toString().trim();
				infotextArea.append("Start scanning: " + ipname + "\n");
				Main.port_scanner = new PortScanner(ipname, Integer.parseInt(minPort.getValue().toString()),
						Integer.parseInt(maxPort.getValue().toString()));
				if (isScanning) {
					logfileto.setEditable(false);
					ipBox.setEditable(false);
				}

			}

		});
		// startButton.setSize(WIDTH, HEIGHT);
		startButton.setBounds(500, 32, 80, 23);
		startButton.setFont(font);
		panel.add(startButton);

		pauseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (isScanning) {
					isPaused = true;
					isScanning = false;
					Main.port_scanner.PauseScan();
					pauseButton.setName("Continue");
				} else if (isPaused) {
					isPaused = false;
					isScanning = true;
					Main.port_scanner.ContinueScan();
					pauseButton.setName("Pause");
				}
			}
		});
		pauseButton.setBounds(580, 32, 80, 23);
		pauseButton.setFont(font);
		panel.add(pauseButton);

		endButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				// Stop scanning
				if (!isScanning)
					return;
				Main.port_scanner.PauseScan();
				isScanning = false;
				isPaused = false;
				logfileto.setEditable(true);
				ipBox.setEditable(true);
			}
		});
		endButton.setBounds(660, 32, 80, 23);
		endButton.setFont(font);
		panel.add(endButton);

		logfileto.setFont(font); // Log file path text box
		logfileto.setText("D:\\");
		logfileto.addActionListener(new ActionListener() { // Monitoring carriage return in Text Domain
			public void actionPerformed(ActionEvent arg0) {
				savepath = logfileto.getText().trim();
				store = new Storeinfile(savepath);
				if (Storeinfile.isWritable())
					logfileto.setText(savepath + "\t is ready!");
				else
					logfileto.setText("Illegal path!\tPlease choose another savepath!");
			}
		});
		logfileto.setBounds(10, 62, 602, 25);
		logfileto.setColumns(10);
		panel.add(logfileto);

		choose_log.addActionListener(new ActionListener() { // Select File Save Location
			public void actionPerformed(ActionEvent e) {
				if (isScanning)
					return;
				JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int returnVal = chooser.showOpenDialog(choose_log);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					savepath = chooser.getSelectedFile().getPath() + "\\Scanresult.txt";
					store = new Storeinfile(savepath);
					if (Storeinfile.isWritable())
						logfileto.setText(savepath + "\t is ready!");
					else
						logfileto.setText("Illegal path!\tPlease choose another savepath!");
				}

			}
		});
		choose_log.setBounds(617, 62, 117, 23);
		choose_log.setFont(font);
		panel.add(choose_log);

		// The maximum value of the progress bar is 100 (default), the minimum is zero.
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
		progressBar.setBounds(10, 92, 601, 23);
		progressBar.setFont(font);
		panel.add(progressBar);

		btnFile.addActionListener(new ActionListener() { // Open file button in upper left corner
			public void actionPerformed(ActionEvent e) {
				if (isScanning)
					return;
				Readfile.ReadFile(savepath);
			}
		});
		btnFile.setBounds(10, 5, 103, 23);
		btnFile.setFont(font);
		panel.add(btnFile);

		btnSettings.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String str_chunkSize = JOptionPane.showInputDialog(
						"The present Thread-size is : " + Main.port_scanner.chunkSize + "	\nchange to : ");
				if (str_chunkSize != null)
					Main.port_scanner.setChunkSize(Integer.parseInt(str_chunkSize));
				// The logic of adding the settings button here.
			}
		});
		btnSettings.setBounds(115, 5, 93, 23);
		btnSettings.setFont(font);
		panel.add(btnSettings);

		btnHelp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String message = "How to use PortScanner:\n"
						+ "Enter the target website name, the start port number and end port number,\n"
						+ "you can optional push \"Locate\" button to set the path for saving scan results, "
						+ "then you can push \"OpenFile\" button to read the result\n"
						+ "then push \"start\" button to start  scanning\n"
						+ "During the scanning, the rate of progress will be showed\n"
						+ "Moreover, you can push \"Pause\" button to interupt the scanning, or \"End\" button to end the scanning\n"
						+ "The Scan Results will be show below,consists of the number of OpenPorts and their Service\n"
						+ "The OS version is on \"Host IP\" column\n"
						+ "In addtion, push \"Settings\" button to change the thread size,which relates to the speed of scanning\n";
				JOptionPane.showMessageDialog(contentPane, message, "Help", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		btnHelp.setBounds(617, 5, 117, 21);
		btnHelp.setFont(font);
		panel.add(btnHelp);

		JSeparator separator = new JSeparator();
		separator.setBackground(Color.GRAY);
		separator.setBounds(-24, 30, 768, 2);
		panel.add(separator);

		JSeparator separator_1 = new JSeparator();
		separator_1.setBackground(Color.GRAY);
		separator_1.setBounds(0, 88, 768, 2);
		panel.add(separator_1);

		JPanel panel_1 = new JPanel(); // Port information display board
		panel_1.setBounds(15, 129, 744, 23);
		contentPane.add(panel_1);
		panel_1.setLayout(null);

		JButton btnNewButton = new JButton("Host IP:");
		btnNewButton.setBounds(0, 0, 234, 23);
		btnNewButton.setFont(font);
		panel_1.add(btnNewButton);

		JButton btnNewButton_1 = new JButton("Open Port:");
		btnNewButton_1.setBounds(229, 0, 274, 23);
		btnNewButton_1.setFont(font);
		panel_1.add(btnNewButton_1);

		JButton btnNewButton_2 = new JButton("Service:");
		btnNewButton_2.setBounds(502, 0, 242, 23);
		btnNewButton_2.setFont(font);
		panel_1.add(btnNewButton_2);

		JSeparator separator_2 = new JSeparator();
		separator_2.setBackground(Color.GRAY);
		separator_2.setBounds(15, 287, 744, 2);
		contentPane.add(separator_2);

		JPanel panel_2 = new JPanel();
		panel_2.setLayout(new BoxLayout(panel_2, BoxLayout.X_AXIS));

		// display ip
		iptextArea.setEditable(false);
		iptextArea.setFont(font);
		panel_2.add(iptextArea);

		// display port
		porttextArea.setEditable(false);
		porttextArea.setFont(font);
		panel_2.add(porttextArea);

		// display service
		servicetextArea.setEditable(false);
		servicetextArea.setFont(font);
		panel_2.add(servicetextArea);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(15, 153, 744, 132);
		contentPane.add(scrollPane);
		scrollPane.setViewportView(panel_2);

		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(15, 299, 744, 167);
		contentPane.add(scrollPane_1);

		infotextArea.setEditable(false); // Display console information
		infotextArea.setBackground(Color.BLACK);
		infotextArea.setForeground(Color.WHITE);
		scrollPane_1.setViewportView(infotextArea);

	}

	public static void addInfoText(String str) {
		infotextArea.append(str);

	}

	public static void addOpenPort(String IP, String port, String service) {
		iptextArea.append(IP + '\n');
		porttextArea.append(port + '\n');
		servicetextArea.append(service + '\n');
	}

	public static void setProgressBar(int n) {
		if (n > 100)
			progressBar.setValue(100);
		else
			progressBar.setValue(n);
	}
}