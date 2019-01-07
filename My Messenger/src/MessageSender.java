import java.io.DataOutputStream;
import java.net.Socket;

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
			new Thread(new Runnable() {
				public void run() {
					MyMessenger.showMessageToUser("Message sent!");
				}
			}).start();
		} catch (Exception e) {
			new Thread(new Runnable() {
				public void run() {
					MyMessenger.showMessageToUser("Message didn't sent!");
				}
			}).start();
		}
	}

}
