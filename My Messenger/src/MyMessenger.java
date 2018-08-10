
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.LineBorder;

public class MyMessenger extends JFrame {

	public static int messageReceiverPort = 55555;
	public static int fileReceiverPort = 55556;
	public static String parentFolder;

	private static MyMessenger simple;

	private JTextArea textArea;
	private JProgressBar progressBar;
	private JComboBox<String> comboBox;

	private static final long serialVersionUID = 1L;

	private MyMessenger() {
		super();
		parentFolder = System.getProperty("user.home") + "/Documents/MyMessenger/";
		setSize(800, 500);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);

		DropTargetHandler dropTargetHandler = new DropTargetHandler();

		JPanel upperPanel = new JPanel();
		upperPanel.setLayout(new GridLayout(2, 3));

		JButton openTransferFolder = new JButton("Open");
		openTransferFolder.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					File dirToOpen = new File(MyMessenger.parentFolder);
					Desktop.getDesktop().open(dirToOpen);
				} catch (Exception e2) {
					JOptionPane.showMessageDialog(null, "Folder may not exist!");
				}
			}
		});
		upperPanel.add(openTransferFolder);

		comboBox = new JComboBox<>(UserListManger.getUserList());
		upperPanel.add(comboBox);

		JButton button = new JButton("send");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String receiver = comboBox.getSelectedItem().toString().split(" ")[0];
				String message = textArea.getText();
				new Thread(new MessageSender(receiver, message)).start();
			}
		});
		upperPanel.add(button);

		JButton copyButton = new JButton("copy");
		copyButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				StringSelection stringSelection = new StringSelection(textArea.getText());
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				clipboard.setContents(stringSelection, null);
			}
		});
		upperPanel.add(copyButton);

		JButton pasteButton = new JButton("paste");
		pasteButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Toolkit toolkit = Toolkit.getDefaultToolkit();
					Clipboard clipboard = toolkit.getSystemClipboard();
					String result = (String) clipboard.getData(DataFlavor.stringFlavor);
					textArea.setText(result);
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		});
		upperPanel.add(pasteButton);

		JButton clearButton = new JButton("clear");
		clearButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				textArea.setText("");
			}
		});
		upperPanel.add(clearButton);

		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());

		panel.add(upperPanel, BorderLayout.NORTH);

		textArea = new JTextArea();
		textArea.setBorder(new LineBorder(Color.BLACK, 2));
		textArea.setFont(new Font("arial", Font.PLAIN, 15));
		panel.add(textArea, BorderLayout.CENTER);

		textArea.setDropTarget(dropTargetHandler);

		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		panel.add(scrollPane);

		progressBar = new JProgressBar();
		progressBar.setStringPainted(true);
		panel.add(progressBar, BorderLayout.SOUTH);

		setContentPane(panel);

	}

	public static void initialize() {
		if (simple == null) {
			simple = new MyMessenger();
			new Thread(new MessageReceiver()).start();
			new Thread(new FileReceiver()).start();
		}
		simple.setVisible(true);
	}

	public static void showMessageSet(String message) {
		if (simple.isVisible() == false) {
			initialize();
		}
		simple.textArea.setText(message);
	}

	public static void showProgressMesseage(String message, int value) {
		simple.progressBar.setValue(value);
		simple.progressBar.setString(message);
	}

	public static String getSelectedUserIpAddress() {
		return simple.comboBox.getSelectedItem().toString().split(" ")[0];
	}

	class DropTargetHandler extends DropTarget {

		private static final long serialVersionUID = 1L;

		public synchronized void drop(DropTargetDropEvent evt) {

			try {
				evt.acceptDrop(DnDConstants.ACTION_COPY);

				@SuppressWarnings("unchecked")
				List<File> droppedFiles = (List<File>) evt.getTransferable()
						.getTransferData(DataFlavor.javaFileListFlavor);

				if (droppedFiles.size() == 0) {
					return;
				}
				FileSender.sendFiles(droppedFiles.get(0));

			} catch (Exception ex) {
				ex.printStackTrace();
			}

		}

	}

}
