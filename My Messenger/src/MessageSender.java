import java.io.DataOutputStream;
import java.net.Socket;

import javax.swing.JOptionPane;

public class MessageSender implements Runnable {

	private String receiverAddress;
	private String message;

	public MessageSender(String receiverAddress, String message) {
		this.receiverAddress = receiverAddress;
		this.message = message;
	}

	@Override
	public void run() {

		try {
			Socket socket = new Socket(receiverAddress, MyMessenger.messageReceiverPort);
			DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
			dataOutputStream.writeUTF(message);
			dataOutputStream.flush();
			socket.close();
			JOptionPane.showMessageDialog(null, "message successfully sent!");
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Problem occurs!");
		}
	}

}
