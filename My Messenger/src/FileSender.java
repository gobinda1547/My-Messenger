import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.Socket;

public class FileSender implements Runnable {

	private static FileSender fileSender;

	private String receiverIpAddress;
	private File file;

	private FileSender(File file) {
		this.receiverIpAddress = MyMessenger.getSelectedUserIpAddress();
		this.file = file;
	}

	@Override
	public void run() {

		synchronized (fileSender) {

			try {
				Socket socket = new Socket(receiverIpAddress, MyMessenger.fileReceiverPort);
				MyMessenger.showProgressMesseage("connected!", 0);

				DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

				// sending file name
				String absultePath = file.getAbsolutePath();
				int initialCutLength = absultePath.length() - file.getName().length();
				dataOutputStream.writeUTF(absultePath.substring(initialCutLength, absultePath.length()));
				dataOutputStream.flush();

				// sending file length
				long nowFileSize = file.length();
				dataOutputStream.writeLong(nowFileSize);
				dataOutputStream.flush();

				// sending file data
				FileInputStream fis = new FileInputStream(file);
				byte[] buffer = new byte[4096];

				int read = 0;
				long remaining = nowFileSize;

				double perCycleCopy = 100.00 / (nowFileSize / 4096);
				double incrementor = 0.0;
				int value = (int) incrementor;

				while ((read = fis.read(buffer, 0, (int) Math.min(buffer.length, remaining))) > 0) {
					value = Math.min(100, (int) incrementor);
					MyMessenger.showProgressMesseage("sent : " + String.valueOf(value), value);
					dataOutputStream.write(buffer, 0, read);
					dataOutputStream.flush();
					remaining -= read;
					incrementor += perCycleCopy;
				}
				fis.close();
				socket.close();

				MyMessenger.showProgressMesseage("sent successfully!", 100);

			} catch (Exception e) {
				e.printStackTrace();
				MyMessenger.showProgressMesseage("sent error!", 100);
			}

		}

	}

	public static void sendFiles(File file) {

		if (fileSender == null) {
			fileSender = new FileSender(file);
			new Thread(fileSender).start();
			return;
		}
		synchronized (fileSender) {
			fileSender = new FileSender(file);
			new Thread(fileSender).start();
		}
	}
}
