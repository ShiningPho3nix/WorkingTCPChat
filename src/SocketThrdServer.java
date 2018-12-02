import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import java.util.Date;
import java.util.HashMap;

/**
 * TODO Kommentare schreiben
 * 
 * @author Steffen Dworsky, Ram�n Schultz
 *
 */
class SocketThrdServer extends JFrame {

	private static final long serialVersionUID = -5401973340023457066L;
	private JLabel label = new JLabel("Text received over socket:");
	private JPanel panel;
	private JTextArea textArea = new JTextArea();
	private ServerSocket server = null;

	// Collect all ClientWorker in a HashSet
	public static HashMap<String, ClientWorker> clients = new HashMap<String, ClientWorker>();
	public static HashMap<InetAddress, Date> timestamp = new HashMap<InetAddress, Date>();

	SocketThrdServer() { // Begin Constructor
		panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.setBackground(Color.white);
		getContentPane().add(panel);
		panel.add("North", label);
		panel.add("Center", textArea);
	} // End Constructor

	public void listenSocket() {
		try {
			server = new ServerSocket(4444);
		} catch (IOException e) {
			System.out.println("Could not listen on port 4444");
			System.exit(-1);
		}
		while (true) {
			ClientWorker w;
			try {
				w = new ClientWorker(server.accept(), textArea);
				Thread t = new Thread(w);
				t.start();
			} catch (IOException e) {
				System.out.println("Accept failed: 4444");
				System.exit(-1);
			}
		}
	}

	protected void finalize() {
		// Objects created in run method are finalized when
		// program terminates and thread exits
		try {
			server.close();
		} catch (IOException e) {
			System.out.println("Could not close socket");
			System.exit(-1);
		}
	}

	public static void main(String[] args) {
		SocketThrdServer frame = new SocketThrdServer();
		frame.setTitle("Server Program");
		WindowListener l = new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		};
		frame.addWindowListener(l);
		frame.pack();
		frame.setVisible(true);
		frame.listenSocket();

	}
}