import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class ChatClient extends Frame {
	Socket s;
	DataOutputStream dos;
	DataInputStream dis;
	private boolean isConnected;
	TextField tfTxt = new TextField();
	TextArea taContent = new TextArea();

	public static void main(String[] args) {
		new ChatClient().launchFrame();
	}

	public void launchFrame() {
		setLocation(400, 300);
		setSize(300, 300);
		add(tfTxt, BorderLayout.SOUTH);
		add(taContent, BorderLayout.NORTH);
		pack();
		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				disconnect();
				System.exit(0);
			}

		});
		tfTxt.addActionListener(new TFListener());
		setVisible(true);
		connect();

		new Thread(new ReceiveThread()).start();
	}

	private void connect() {
		try {
			// s = new Socket("luhui1hao.vicp.net", 22201);
			s = new Socket("139.129.47.100", 8686);
			System.out.println("Connected!");
			isConnected = true;
			dos = new DataOutputStream(s.getOutputStream());
			dis = new DataInputStream(s.getInputStream());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void disconnect() {
		try {
			dos.close();
			s.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private class TFListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			String str = tfTxt.getText().trim();
			// taContent.setText(str);
			tfTxt.setText("");
			// ���͵���������
			try {
				dos.writeUTF(str);
				dos.flush();
				// dos.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

	}

	private class ReceiveThread implements Runnable {

		@Override
		public void run() {
			try {
				while (isConnected) {
					String str = dis.readUTF();
					// System.out.println(str);
					taContent.append(str + "\n");
				}
			} catch (SocketException e) {
				System.out.println("退出了，bye!");
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					isConnected = false;
					if (dos != null)
						dos.close();
					if (dis != null)
						dis.close();
					if (s != null)
						s.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}
}
