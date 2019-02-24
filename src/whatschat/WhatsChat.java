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
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
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
		user.setCurrentGroup(null);
		
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
		
		JMenuItem addMember = new JMenuItem("Add Member(s)");
		groupMenu.add(addMember);
		
		JMenuItem deleteMember = new JMenuItem("Delete Member(s)");
		groupMenu.add(deleteMember);
		
		JMenuItem editGroup = new JMenuItem("Edit Group");
		groupMenu.add(editGroup);
		
		JMenuItem leaveGroup = new JMenuItem("Leave Group");
		groupMenu.add(leaveGroup);
		
		JMenuItem deleteGroup = new JMenuItem("Delete Group");
		groupMenu.add(deleteGroup);
		
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
		
		editGroup.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String newGroupName = JOptionPane.showInputDialog(main, "Rename group name for " + user.getCurrentGroup());
				
				if (!newGroupName.isEmpty()) {
					network.sendBroadcastMessage("CheckGroupName:" + newGroupName);
					
					try {
						Thread.sleep(100);
					} catch (InterruptedException ie) {
						ie.printStackTrace();
					} 
					
					if (!group.isGroupNameTaken()) {
						network.sendBroadcastMessage("UpdateGroupName:" + user.getCurrentGroup() + ":" + newGroupName);
						JOptionPane.showMessageDialog(main, "Group has been updated successfully", "Success", JOptionPane.PLAIN_MESSAGE);
					} else {
						JOptionPane.showMessageDialog(main, "Group name has been taken", "Error", JOptionPane.ERROR_MESSAGE);
					}		
					
				}
			}
		});
		
		leaveGroup.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int option = JOptionPane.showConfirmDialog(main, "Are you sure you want to leave " + user.getCurrentGroup() + "?", "Leave Group", JOptionPane.YES_NO_OPTION);
				if (option == JOptionPane.YES_OPTION) {
					network.sendBroadcastMessage("LeaveGroup:" + user.getCurrentGroup() + ":" + user.getUser());
					JOptionPane.showMessageDialog(main, "You have left the group successfully", "Success", JOptionPane.PLAIN_MESSAGE);
				
				}
				
			}
		});
		
		deleteGroup.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int option = JOptionPane.showConfirmDialog(main, "Are you sure you want to delete " + user.getCurrentGroup() + "?", "Delete Group", JOptionPane.YES_NO_OPTION);
				if (option == JOptionPane.YES_OPTION) {
					network.sendBroadcastMessage("DeleteGroup:" + user.getCurrentGroup());
					JOptionPane.showMessageDialog(main, "Group has been deleted successfully", "Success", JOptionPane.PLAIN_MESSAGE);
				}
			}
		});
		
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
		
		JLabel lblUserID = new JLabel("User ID");
		lblUserID.setBounds(132, 34, 173, 20);
		userPanel.add(lblUserID);
		
		JLabel lblUserDescription = new JLabel("User Description");
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
		
		JLabel lblOnlineUsers = new JLabel("1 Online Users");
		lblOnlineUsers.setBounds(15, 16, 255, 20);
		userTab.add(lblOnlineUsers);
		
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
		
		JPanel groupTab = new JPanel();
		groupTab.setBackground(Color.white);
		groupTab.setLayout(null);
		tabbedPane.add("Groups", groupTab);
		
		JList<String> listGroups = new JList<String>(group.getAllGroupsByUserId(user.getUser()));
		listGroups.setBounds(15, 16, 255, 315);
		groupTab.add(listGroups);
		
		listGroups.addMouseListener(new MouseAdapter() {
		    public void mouseClicked(MouseEvent evt) {
		        if (evt.getClickCount() == 2) {
		        	user.setCurrentGroup(listGroups.getSelectedValue());
		        	network.sendBroadcastMessage("RefreshGroup");
		        }
		    }
		});
		
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
		btnSend.setBounds(455, 535, 143, 30);
		chatPanel.add(btnSend);
		
		TitledBorder title = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.gray), "Members");
		title.setTitleJustification(TitledBorder.CENTER);
		JPanel memberPanel = new JPanel();
		memberPanel.setLayout(null);
		memberPanel.setBackground(Color.WHITE);
		memberPanel.setBounds(455, 44, 143, 479);
		memberPanel.setBorder(title);
		chatPanel.add(memberPanel);
		
		JList<String> listMembers = new JList<String>(group.getAllUsersByGroup(user.getCurrentGroup()));
		listMembers.setBounds(15, 32, 113, 431);
		memberPanel.add(listMembers);
		
		main.addWindowListener(new WindowAdapter() {
	        @Override
	        public void windowClosing(WindowEvent e) {
	        	network.sendBroadcastMessage("RemoveUser:" + user.getUser());
	        }
		});
		
		addMember.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				List<String> list = listUsers.getSelectedValuesList();
				if (!list.isEmpty()) {
					int option = JOptionPane.showConfirmDialog(main, "Are you sure you want to add the following members: " + list + "?", "Add Member(s)", JOptionPane.YES_NO_OPTION);
					if (option == JOptionPane.YES_OPTION) {
						
						for (int i = 0; i < list.size(); i++) {
							network.sendBroadcastMessage("AddMember:" + user.getCurrentGroup() + ":" + list.get(i));
						}
					}
				} else {
					JOptionPane.showMessageDialog(main, "No user(s) selected", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		deleteMember.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				List<String> list = listMembers.getSelectedValuesList();
				if (!list.isEmpty()) {
					int option = JOptionPane.showConfirmDialog(main, "Are you sure you want to delete the following members: " + list + "?", "Delete Member(s)", JOptionPane.YES_NO_OPTION);
					if (option == JOptionPane.YES_OPTION) {
						for (int i = 0; i < list.size(); i++) {
							network.sendBroadcastMessage("DeleteMember:" + user.getCurrentGroup() + ":" + list.get(i));
						}
					}
				} else {
					JOptionPane.showMessageDialog(main, "No user(s) selected", "Error", JOptionPane.ERROR_MESSAGE);
				}
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
			            	// [User ID]
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
			            		
			            	// [New User ID]
			            	case "AddNewUser":
			            		user.addUser(split[1], "");
			            		break;
			            		
			            	// [User ID] [User Description]
			            	case "AddUser":
			            		user.addUser(split[1], split[2]);
			            		break;
			            		
			            		// [User ID]
			            	case "RemoveUser":
			            		user.removeUser(split[1]);
			            		break;
			            	
			            	// [Old User ID] [New User ID] [New User Description]
			            	case "UpdateUser":
			            		user.updateUser(split[1], split[2], split[3]);
				            	if (user.getUser().equals(split[1])) {
				            		user.setUser(split[2]);
				            		
				            		group.updateMember(split[1], split[2]);
				            	}
				            	network.sendBroadcastMessage("RefreshGroup");
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
			            		
			            	// [Group Name] [User ID]
			            	case "AddMember":
			            		group.addMember(split[1], split[2]);
			            		if (user.getUser().equals(split[2])) {
			            			JOptionPane.showMessageDialog(main, "You have been added into " + split[1], "Added", JOptionPane.PLAIN_MESSAGE);
			            		}
			            		network.sendBroadcastMessage("RefreshGroup");
			            		break;
			            	
			            	// [Group Name] [User ID]
			            	case "DeleteMember":
			            		group.removeMember(split[1], split[2]);
			            		
			            		if (user.getUser().equals(split[2])) {
			            			user.setCurrentGroup(null);
			            			JOptionPane.showMessageDialog(main, "You have been removed from " + split[1], "Removed", JOptionPane.PLAIN_MESSAGE);
			            		}
			            		
			            		network.sendBroadcastMessage("RefreshGroup");
			            		break;
			            		
			            	// [Group Name]
			            	case "CheckGroupName":
			            		group.setGroupNameTaken(false);
				            	if (group.groupNameExists(split[1])) {
				            		group.setGroupNameTaken(true);
				            	}
			            		break;
			            		
			            	// [Group Name] [IP Address] [Creator's User ID]
			            	case "CreateGroup":
			            		group.addGroup(split[1], split[2], split[3]);
				            	if (user.getUser().equals(split[3])) {
				            		user.setCurrentGroup(split[1]);
				            	}
				            	network.sendBroadcastMessage("RefreshGroup");
			            		break;
			            		
			            	// [Old Group Name] [New Group Name]
			            	case "UpdateGroupName":
			            		group.updateGroup(split[1], split[2], user.getUser());
			            		if (user.getCurrentGroup().equals(split[1])) {
			            			user.setCurrentGroup(split[2]);
			            		}
			            		network.sendBroadcastMessage("RefreshGroup");
			        
			            		break;
			            		
			            	// [Group Name] [User ID]
			            	case "LeaveGroup":
			            		if (user.getUser().equals(split[2])) {
			            			user.setCurrentGroup(null);
			            		}
			            		group.removeMember(split[1], split[2]);
			            		network.sendBroadcastMessage("RefreshGroup");
			            		break;
			            		
			            	// [Group Name]
			            	case "DeleteGroup":
			            		if (user.getCurrentGroup().equals(split[1])) {
			            			user.setCurrentGroup(null);
			            		}
			            		
			            		group.removeGroup(split[1]);
			            		network.sendBroadcastMessage("RefreshGroup");
			            		break;
			            		
			            	case "RefreshGroup":
			            		group.getAllUsersByGroup(user.getCurrentGroup());
			            		group.getAllGroupsByUserId(user.getUser());
			            		break;
			            }
			            
			            lblUserID.setText(user.getUser());
			            lblUserDescription.setText(user.getDescription(user.getUser()));
			            lblOnlineUsers.setText(user.getAllUsers().getSize() + " Online Users");
			            	         
			            if (user.getCurrentGroup() != null) {
			            	addMember.setEnabled(true);
			            	deleteMember.setEnabled(true);
			            	editGroup.setEnabled(true);
			            	leaveGroup.setEnabled(true);
			            	deleteGroup.setEnabled(true);
			        		btnSend.setEnabled(true);
			        		
			        		lblCurrentGroup.setText("Group Name: " + user.getCurrentGroup());
			            } else {
			            	addMember.setEnabled(false);
			            	deleteMember.setEnabled(false);
			            	editGroup.setEnabled(false);
			            	leaveGroup.setEnabled(false);
			            	deleteGroup.setEnabled(false);
			        		btnSend.setEnabled(false);
			        		
			        		lblCurrentGroup.setText("Group Name: ");
			            }
			            
			            
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				}
			}
		}).start();
	}
}
