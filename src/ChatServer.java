import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatServer {
	private boolean isStarted = false;
	private ServerSocket ss = null;
	private List<ClientThread> clients = new ArrayList<>();
	private ClientThread c;

	public static void main(String[] args) {
		new ChatServer().start();
	}

	public void start() {
		Socket s = null;
		DataInputStream dis = null;
		try {
			ss = new ServerSocket(8686);
			isStarted = true;
		} catch (BindException e) {
			System.out.println("端口使用中");
			System.out.println("请关掉相关程序，并重新运行服务器！");
		} catch (Exception e) {
			try {
				ss.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}

		try {
			while (isStarted) {
				s = ss.accept();
				c = new ClientThread(s);
				new Thread(c).start();
				clients.add(c);
				System.out.println("a Client connected!");
				System.out.println("当前客户端数量：" + clients.size());
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (ss != null)
				try {
					ss.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

	class ClientThread implements Runnable {
		private Socket s;
		private DataInputStream dis = null;
		private DataOutputStream dos = null;
		private boolean isConnected = false;

		public ClientThread(Socket s) {
			this.s = s;
			try {
				dis = new DataInputStream(s.getInputStream());
				dos = new DataOutputStream(s.getOutputStream());
				isConnected = true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void send(String str) {
			try {
				dos.writeUTF(str);
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("发送失败");
			}
		}

		@Override
		public void run() {
			try {
				// 接收客户端传来的数据
				while (isConnected) {
					String str = dis.readUTF();
					for (ClientThread c : clients) {
						if (isConnected) {
							c.send(str);
						}
					}
					System.out.println(str);
				}
			} catch (EOFException e) {
				clients.remove(this);
				System.out.println("A client closed!");
				System.out.println("当前剩余客户端数量：" + clients.size());
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (dis != null)
						dis.close();
					if (dos != null)
						dos.close();
					if (s != null)
						s.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}

	}
}
