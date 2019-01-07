import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JOptionPane;

public class FileReceiver implements Runnable {

	public FileReceiver() {
		// System.out.println("file receiver waiting...");
	}

	@Override
	public void run() {

		while (true) {
			try {
				MyMessenger.showProgressMesseage("waiting..!", 0);
				String fileWhereToSave = MyMessenger.parentFolder;

				ServerSocket serverSocket = new ServerSocket(MyMessenger.fileReceiverPort);
				Socket socket = serverSocket.accept();

				MyMessenger.showProgressMesseage("connected!", 0);

				DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());

				// receiving file name
				String nowFileName = dataInputStream.readUTF();

				// receiving file size
				long nowFileSize = dataInputStream.readLong();

				File nowFile = new File(fileWhereToSave + nowFileName);
				nowFile.getParentFile().mkdirs();
				FileOutputStream fos = new FileOutputStream(nowFile);
				byte[] buffer = new byte[4096];

				int read = 0;
				long remaining = nowFileSize;

				double perCycleCopy = 100.00 / (nowFileSize / 4096);
				double incrementor = 0.0;
				int value = (int) incrementor;

				while ((read = dataInputStream.read(buffer, 0, (int) Math.min(buffer.length, remaining))) > 0) {

					value = Math.min(100, (int) incrementor);
					MyMessenger.showProgressMesseage("received : " + String.valueOf(value), value);
					remaining -= read;
					fos.write(buffer, 0, read);

					incrementor += perCycleCopy;
				}

				fos.close();
				MyMessenger.showProgressMesseage("file sent!", 100);

				socket.close();
				serverSocket.close();

				MyMessenger.showMessageToUser("File Received!");

			} catch (Exception e) {
				e.printStackTrace();
				MyMessenger.showMessageToUser("File received error!");
			}

			MyMessenger.showProgressMesseage("disconnected!", 0);
		}

	}

}
