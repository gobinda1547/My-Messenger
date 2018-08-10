import java.io.DataInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class MessageReceiver implements Runnable {

	public MessageReceiver() {
		// System.out.println("message receiver waiting....");
	}

	@Override
	public void run() {

		while (true) {
			try {
				ServerSocket serverSocket = new ServerSocket(MyMessenger.messageReceiverPort);
				Socket socket = serverSocket.accept();
				DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
				String message = dataInputStream.readUTF();
				MyMessenger.showMessageSet(message);
				serverSocket.close();
			} catch (Exception e) {
				System.exit(00);
			}
		}

	}

}
