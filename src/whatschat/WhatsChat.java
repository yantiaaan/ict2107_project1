package whatschat;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextArea;

public class WhatsChat extends JFrame {
	
	User user = new User();
	Network network = new Network();
	Group group = new Group(network);
	
	JLabel lblUserID;
	JLabel lblUserDescription;
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {		
					WhatsChat whatsChat = new WhatsChat();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public WhatsChat() {
		
		network.connectBroadcast();
		MulticastSocket broadcastSocket = network.getBroadcastSocket();
		network.sendBroadcastMessage("GetOnlineUsers");
		
		/* ------------------------------------------ START OF MAIN ------------------------------------------ */
		
		JFrame main = new JFrame();
		main.setVisible(false);
		main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		main.setBounds(100, 100, 1000, 700);
		
		JPanel mainPanel = new JPanel();
		mainPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		main.setContentPane(mainPanel);
		mainPanel.setLayout(null);
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.setBounds(0, 0, 978, 31);
		mainPanel.add(menuBar);
		
		JMenu userMenu = new JMenu("User");
		menuBar.add(userMenu);
		
		JMenuItem editUser = new JMenuItem("Edit User");
		userMenu.add(editUser);
		
		editUser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFrame editFrame = new JFrame();
				editFrame.setVisible(true);
				editFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
				editFrame.setBounds(100, 100, 500, 300);
				
				JPanel editPanel = new JPanel();
				editPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
				editFrame.setContentPane(editPanel);
				editPanel.setLayout(null);
				
				JLabel lblEditName = new JLabel("Name");
				lblEditName.setBounds(15, 16, 102, 20);
				editPanel.add(lblEditName);
				
				JLabel lblEditDescription = new JLabel("Description");
				lblEditDescription.setBounds(15, 66, 102, 20);
				editPanel.add(lblEditDescription);
				
				JLabel lblAddPhoto = new JLabel("Upload Photo");
				lblAddPhoto.setBounds(15, 120, 102, 20);
				editPanel.add(lblAddPhoto);
				
				JTextField txtEditName = new JTextField();
				txtEditName.setText(user.getUser());
				txtEditName.setBounds(132, 13, 331, 26);
				editPanel.add(txtEditName);
				txtEditName.setColumns(10);
				
				JTextField txtEditDescription = new JTextField();
				txtEditDescription.setText(user.getDescription(user.getUser()));
				txtEditDescription.setBounds(132, 63, 331, 26);
				editPanel.add(txtEditDescription);
				txtEditDescription.setColumns(10);
				
				JButton btnBrowse = new JButton("Browse");
				btnBrowse.setBounds(132, 150, 115, 29);
				editPanel.add(btnBrowse);
				
				JLabel lblImageUrl = new JLabel("");
				lblImageUrl.setBounds(132, 121, 331, 20);
				editPanel.add(lblImageUrl);
				
				btnBrowse.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						JFileChooser fc = new JFileChooser();
						FileNameExtensionFilter filter = new FileNameExtensionFilter("JPG & PNG Images", "jpg", "png");
						fc.setFileFilter(filter);
						int returnVal = fc.showOpenDialog(editPanel);
						if (returnVal == JFileChooser.APPROVE_OPTION) {
							System.out.println("You chose to open this file: " + fc.getSelectedFile().getPath());
						}
						lblImageUrl.setText(fc.getSelectedFile().getAbsolutePath());
					}
				});
				
				JButton btnSave = new JButton("Save");
				btnSave.setBounds(132, 200, 158, 29);
				editPanel.add(btnSave);
				
				btnSave.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						
						String des = " ";
						if (!txtEditDescription.getText().isEmpty()) {
							des = txtEditDescription.getText();
						}
						
						network.sendBroadcastMessage("UpdateUser:" + user.getUser() + ":" + txtEditName.getText() + ":" + des);
						
						lblUserID.setText(txtEditName.getText());
						lblUserDescription.setText(txtEditDescription.getText());
						editFrame.dispose();
					}
				});
				
				JButton btnCancel = new JButton("Cancel");
				btnCancel.setBounds(302, 200, 158, 29);
				editPanel.add(btnCancel);
				
				btnCancel.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						editFrame.dispose();
					}
				});
				
				editFrame.addWindowListener(new WindowAdapter() {
			        @Override
			        public void windowClosing(WindowEvent e) {
			        	editFrame.dispose();
			        }
				});
			}
		});
		
		JMenu groupMenu = new JMenu("Group");
		menuBar.add(groupMenu);
		
		JMenuItem addGroup = new JMenuItem("Create Group");
		groupMenu.add(addGroup);
		
		addGroup.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String groupName = JOptionPane.showInputDialog(main, "Enter group name");
				
				if (!groupName.isEmpty()) {
					network.sendBroadcastMessage("CheckGroupName:" + groupName);
					
					try {
						Thread.sleep(100);
					} catch (InterruptedException ie) {
						ie.printStackTrace();
					} 
					
					if (!group.isGroupNameTaken()) {
						String ipAddress = group.generateRandomIp();
						network.sendBroadcastMessage("CreateGroup:" + groupName + ":" + ipAddress + ":" + user.getUser());
						JOptionPane.showMessageDialog(main, "Group has been created successfully", "Success", JOptionPane.PLAIN_MESSAGE);
					} else {
						JOptionPane.showMessageDialog(main, "Group name has been taken", "Error", JOptionPane.ERROR_MESSAGE);
					}			
				}
			}
		});
		
		JMenuItem editGroup = new JMenuItem("Edit Group");
		groupMenu.add(editGroup);
		
		JMenuItem deleteGroup = new JMenuItem("Delete Group");
		groupMenu.add(deleteGroup);
		
		JPanel userPanel = new JPanel();
		userPanel.setBounds(15, 47, 320, 581);
		userPanel.setBackground(Color.white);
		userPanel.setLayout(null);
		mainPanel.add(userPanel);		
		
		JLabel lblUserImage = new JLabel("");
		String dir = "img/default.jpg";
		ImageIcon img = new ImageIcon(new ImageIcon(dir).getImage().getScaledInstance(102, 102, Image.SCALE_DEFAULT));
		lblUserImage.setIcon(img);
		lblUserImage.setBounds(15, 34, 102, 102);
		userPanel.add(lblUserImage);
		
		lblUserID = new JLabel("User ID");
		lblUserID.setBounds(132, 34, 173, 20);
		userPanel.add(lblUserID);
		
		lblUserDescription = new JLabel("User Description");
		lblUserDescription.setBounds(132, 70, 173, 66);
		userPanel.add(lblUserDescription);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(15, 184, 290, 381);
		userPanel.add(tabbedPane);
		
		JPanel userTab = new JPanel();
		userTab.setBackground(Color.white);
		userTab.setLayout(null);
		tabbedPane.addTab("Users", null, userTab, null);
		
		JList<String> listUsers = new JList<String>(user.getAllUsers());
		listUsers.setBounds(15, 51, 255, 280);
		userTab.add(listUsers);
		
		listUsers.addMouseListener(new MouseAdapter() {
		    public void mouseClicked(MouseEvent evt) {
		        if (evt.getClickCount() == 2) {
		            JFrame profileFrame = new JFrame();
		            profileFrame.setVisible(true);
		            profileFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		            profileFrame.setBounds(100, 100, 300, 400);
					
					JPanel profilePanel = new JPanel();
					profilePanel.setBorder(new EmptyBorder(5, 5, 5, 5));
					profileFrame.setContentPane(profilePanel);
					profilePanel.setLayout(null);
					
					JLabel profileImg = new JLabel("Image");
					String dir = "img/default.jpg";
					ImageIcon img = new ImageIcon(new ImageIcon(dir).getImage().getScaledInstance(102, 102, Image.SCALE_DEFAULT));
					profileImg.setIcon(img);
					profileImg.setBounds(80, 31, 102, 102);
					profilePanel.add(profileImg);
					
					JLabel lblProfileName = new JLabel(listUsers.getSelectedValue());
					lblProfileName.setBounds(90, 149, 248, 20);
					profilePanel.add(lblProfileName);
					
					JLabel lblProfileDescription = new JLabel(user.getDescription(listUsers.getSelectedValue()));
					lblProfileDescription.setBounds(15, 180, 248, 20);
					profilePanel.add(lblProfileDescription);
					
					profileFrame.addWindowListener(new WindowAdapter() {
				        @Override
				        public void windowClosing(WindowEvent e) {
				        	profileFrame.dispose();
				        }
					});
		            
		        }
		    }
		});
		
		JLabel lblOnlineUsers = new JLabel("1 Online Users");
		lblOnlineUsers.setBounds(15, 16, 255, 20);
		userTab.add(lblOnlineUsers);
		
		JPanel groupTab = new JPanel();
		groupTab.setBackground(Color.white);
		groupTab.setLayout(null);
		tabbedPane.add("Groups", groupTab);
		
		JList<String> listGroups = new JList<String>(group.getAllGroupsByUserId(user.getUser()));
		listGroups.setBounds(15, 16, 255, 315);
		groupTab.add(listGroups);
		
		JPanel chatPanel = new JPanel();
		chatPanel.setBackground(Color.white);
		chatPanel.setBounds(350, 47, 613, 581);
		chatPanel.setLayout(null);
		mainPanel.add(chatPanel);
		
		JLabel lblCurrentGroup = new JLabel("Group Name: ");
		lblCurrentGroup.setBounds(15, 16, 583, 20);
		chatPanel.add(lblCurrentGroup);
		
		JTextArea textArea = new JTextArea();
		textArea.setBorder(BorderFactory.createLineBorder(Color.gray));
		textArea.setEditable(false);
		textArea.setBounds(15, 52, 425, 469);
		chatPanel.add(textArea);
		
		JTextField txtMessage = new JTextField();
		txtMessage.setBounds(15, 535, 425, 30);
		chatPanel.add(txtMessage);
		txtMessage.setColumns(10);
		
		JButton btnSend = new JButton("Send");
		btnSend.setEnabled(false);
		btnSend.setBounds(455, 535, 143, 30);
		chatPanel.add(btnSend);
		
		TitledBorder title = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.gray), "Members");
		title.setTitleJustification(TitledBorder.CENTER);
		JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.setBackground(Color.WHITE);
		panel.setBounds(455, 44, 143, 479);
		panel.setBorder(title);
		chatPanel.add(panel);
		
		JList listMembers = new JList();
		listMembers.setBounds(15, 32, 113, 431);
		panel.add(listMembers);
		
		main.addWindowListener(new WindowAdapter() {
	        @Override
	        public void windowClosing(WindowEvent e) {
	        	network.sendBroadcastMessage("RemoveUser:" + user.getUser());
	        }
		});
		
		/* ------------------------------------------ START OF REGISTER ------------------------------------------ */
		
		JFrame register = new JFrame();
		register.setVisible(true);
		register.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		register.setBounds(100, 100, 300, 330);
		
		JPanel registerPanel = new JPanel();
		registerPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		register.setContentPane(registerPanel);
		registerPanel.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("WhatsChat");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 32));
		lblNewLabel.setBounds(15, 16, 248, 68);
		lblNewLabel.setHorizontalAlignment(JTextField.CENTER);
		registerPanel.add(lblNewLabel);
		
		JLabel lblEnterId = new JLabel("Please enter User ID:");
		lblEnterId.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblEnterId.setBounds(15, 100, 248, 26);
		registerPanel.add(lblEnterId);
		
		JTextField txtUserId = new JTextField();
		txtUserId.setBounds(15, 142, 248, 26);
		registerPanel.add(txtUserId);
		txtUserId.setColumns(10);		
		
		JLabel lblRegError = new JLabel("");
		lblRegError.setForeground(Color.RED);
		lblRegError.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblRegError.setBounds(15, 171, 248, 20);
		registerPanel.add(lblRegError);
		
		JButton btnRegister = new JButton("REGISTER");
		btnRegister.setBounds(15, 216, 248, 42);
		registerPanel.add(btnRegister);
		
		btnRegister.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String userId = txtUserId.getText();

				if (userId.isEmpty()) {
					lblRegError.setText("Please enter User ID");
				} else {
					network.sendBroadcastMessage("CheckUserID:" + userId);
					
					try {
						Thread.sleep(100);
					} catch (InterruptedException ie) {
						ie.printStackTrace();
					} 
					
					if (user.isUserIdTaken()) {
						lblRegError.setText("User ID has been taken");
					} else {
						if (userId.matches("^[a-zA-Z][.\\S]{1,8}")) {
							user.setUser(userId);
							lblRegError.setText("Success!");
							network.sendBroadcastMessage("AddNewUser:" + userId);
	
							lblUserID.setText(userId);
							lblUserDescription.setText(user.getDescription(userId));
							
							main.setVisible(true);
							register.setVisible(false);
						} else {
							lblRegError.setText("Incorrect User ID format");
						}
					}
				}
			}
		});
		
		/* ------------------------------------------ START OF THREAD ------------------------------------------ */
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				byte buf[] = new byte[1000];
				DatagramPacket dgpReceived = new DatagramPacket(buf, buf.length);
				while (true) {
					try {
						broadcastSocket.receive(dgpReceived);
						byte[] receivedData = dgpReceived.getData();
						int length = dgpReceived.getLength();
						String msg = new String(receivedData, 0, length);
			            String[] split = msg.split("\\:");
			            
			            switch (split[0]) {
			            	case "CheckUserID":
			            		user.setUserIdTaken(false);
				            	if (user.getUser().equals(split[1])) {
				            		network.sendBroadcastMessage("DefaultUserID");
				            	}
			            		break;
			            	
			            	case "DefaultUserID":
			            		if (user.getUser().equals("")) {
				            		user.setUserIdTaken(true);
				            	}
			            		break;
			            		
			            	case "AddNewUser":
			            		user.addUser(split[1], "");
				            	lblOnlineUsers.setText(user.getAllUsers().getSize() + " Online Users");
			            		break;
			            		
			            	case "AddUser":
			            		user.addUser(split[1], split[2]);
				            	lblOnlineUsers.setText(user.getAllUsers().getSize() + " Online Users");
			            		break;
			            		
			            	case "GetOnlineUsers":
			            		if (!user.getUser().isEmpty()) {
				            		String des = " ";
				            		if (!user.getDescription(user.getUser()).isEmpty()) {
				            			des = user.getDescription(user.getUser());
				            		}
				            		network.sendBroadcastMessage("AddUser:" + user.getUser() + ":" + des);
				            	}
			            		break;
			            		
			            	case "CheckGroupName":
			            		group.setGroupNameTaken(false);
				            	if (group.groupNameExists(split[1])) {
				            		group.setGroupNameTaken(true);
				            	}
			            		break;
			            		
			            	case "CreateGroup":
			            		group.addGroup(split[1], split[2], split[3]);
				            	if (user.getUser().equals(split[3])) {
				            		user.setCurrentGroup(split[1]);
				            		group.addMember(split[1], split[3]);
				            		lblCurrentGroup.setText("Group Name: " + split[1]);
				            	}
			            		break;
			            		
			            	case "RemoveUser":
			            		user.removeUser(split[1]);
				            	lblOnlineUsers.setText(user.getAllUsers().getSize() + " Online Users");
			            		break;
			            		
			            	case "UpdateUser":
			            		user.updateUser(split[1], split[2], split[3]);
				            	if (user.getUser().equals(split[1])) {
				            		user.setUser(split[2]);
				            	}
			            		break;
			            }
			            
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				}
			}
		}).start();
	}
}
