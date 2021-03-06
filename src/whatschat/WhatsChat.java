package whatschat;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
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
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

public class WhatsChat extends JFrame {
	
	User user = new User();
	Network network = new Network();
	Group group = new Group();
	JedisDB jedis = new JedisDB();
	
	public DefaultListModel<String> friendsModel = new DefaultListModel<String>();
	private ArrayList<User> listOfMyFriends = new ArrayList<User>();
	private ArrayList<User> fullListOfMyFriends = new ArrayList<User>();
	JTextArea textArea;
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {		
					WhatsChat main = new WhatsChat();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/** -------------------------------------------------------- SWITCH CASE VALIDATE ACTION- -------------------------------------------------------- **/
	public WhatsChat() {
		
		network.connectBroadcast();
		MulticastSocket broadcastSocket = network.getBroadcastSocket();
		network.sendBroadcastMessage("GetOnlineUsers");
		user.setCurrentGroup(null);
		
		/* ---------------------------------------------------------------- START OF MAIN ---------------------------------------------------------------- */
		
		// Start of Main Frame
		JFrame main = new JFrame();
		main.setVisible(false);
		main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		main.setBounds(100, 100, 1000, 700);
		
		JPanel mainPanel = new JPanel();
		mainPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		main.setContentPane(mainPanel);
		mainPanel.setLayout(null);
		
		// Start of Menu Bar
		JMenuBar menuBar = new JMenuBar();
		menuBar.setBounds(0, 0, 978, 31);
		mainPanel.add(menuBar);
		
		// User Menu
		JMenu userMenu = new JMenu("User");
		menuBar.add(userMenu);
		
		JMenuItem editUser = new JMenuItem("Edit User");
		userMenu.add(editUser);
		
		
		JMenuItem addFriend = new JMenuItem("Add Friend(s)");
		userMenu.add(addFriend);
		
		JMenuItem removeFriend = new JMenuItem("Remove Friend(s)");
		userMenu.add(removeFriend);
		
		// Group Menu
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
		
		// More Menu
		JMenu moreMenu = new JMenu("More");
		menuBar.add(moreMenu);
		
		JMenuItem searchMsg = new JMenuItem("Search Message");
		moreMenu.add(searchMsg);
		
		
		// User Menu - Edit User Frame
		JFrame editFrame = new JFrame();
		editFrame.setVisible(false);
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
		txtEditName.setBounds(132, 13, 331, 26);
		txtEditName.setColumns(10);
		editPanel.add(txtEditName);
		
		JLabel lblNameError = new JLabel();
		lblNameError.setForeground(Color.RED);
		lblNameError.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblNameError.setBounds(132, 32, 331, 26);
		editPanel.add(lblNameError);
		
		JTextField txtEditDescription = new JTextField();
		txtEditDescription.setBounds(132, 63, 331, 26);
		txtEditDescription.setColumns(10);
		editPanel.add(txtEditDescription);
		
		JButton btnBrowse = new JButton("Browse");
		btnBrowse.setBounds(132, 150, 115, 29);
		editPanel.add(btnBrowse);
		
		JLabel lblImageUrl = new JLabel();
		lblImageUrl.setBounds(132, 121, 331, 20);
		editPanel.add(lblImageUrl);
		
		JButton btnSave = new JButton("Save");
		btnSave.setBounds(132, 200, 158, 29);
		editPanel.add(btnSave);
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.setBounds(302, 200, 158, 29);
		editPanel.add(btnCancel);
		
		// Add friend menu
		JFrame addFriendFrame = new JFrame();
		addFriendFrame.setVisible(false);
		addFriendFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addFriendFrame.setBounds(100, 100, 300, 300);
		
		JPanel addFriendPanel = new JPanel();
		addFriendPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		addFriendFrame.setContentPane(addFriendPanel);
		addFriendPanel.setLayout(null);
		
		JLabel lblUsers = new JLabel("User List:");
		lblUsers.setBounds(15, 16, 102, 20);
		addFriendFrame.getContentPane().add(lblUsers);
		
		JList<String> listofUsersNotFriends = new JList<String>(user.getAllUsers());
		JScrollPane listFriendsScr = new JScrollPane(listofUsersNotFriends, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		listFriendsScr.setBounds(100, 16, 102, 150);
		addFriendFrame.getContentPane().add(listFriendsScr);
		JButton btnAddFriend = new JButton("Add");
		btnAddFriend.setBounds(60, 180, 143, 30);
		addFriendFrame.getContentPane().add(btnAddFriend);
		
		// Display add friend pop up menu
		addFriend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addFriendFrame.setVisible(true);
				
				addFriendFrame.addWindowListener(new WindowAdapter() {
			        @Override
			        public void windowClosing(WindowEvent e) {
			        	addFriendFrame.dispose();
			        }
				});
			}
		});
		
		
			
		// Start of User Profile
		JPanel userPanel = new JPanel();
		userPanel.setBounds(15, 47, 320, 581);
		userPanel.setBackground(Color.white);
		userPanel.setLayout(null);
		mainPanel.add(userPanel);		
		
		JLabel lblUserImage = new JLabel();
		ImageIcon img = new ImageIcon(new ImageIcon("img/default.jpg").getImage().getScaledInstance(102, 102, Image.SCALE_DEFAULT));
		lblUserImage.setIcon(img);
		lblUserImage.setBounds(15, 34, 102, 102);
		userPanel.add(lblUserImage);
		
		JLabel lblUserID = new JLabel("User ID");
		lblUserID.setBounds(132, 34, 173, 20);
		userPanel.add(lblUserID);
		
		JLabel lblUserDescription = new JLabel("User Description");
		lblUserDescription.setBounds(132, 70, 173, 66);
		userPanel.add(lblUserDescription);
		
		// Tab
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(15, 184, 290, 381);
		userPanel.add(tabbedPane);
		
		// Friend Tab
		JPanel friendTab = new JPanel();
		friendTab.setBackground(Color.white);
		friendTab.setLayout(null);
		tabbedPane.add("Friends", friendTab);
		
		JList<String> listFriends = new JList<String>(friendsModel);
		listFriends.setBounds(15, 51, 255, 280);
		friendTab.add(listFriends);
		
		JLabel lblOnlineFriends = new JLabel("0 Online Friends");
		lblOnlineFriends.setBounds(15, 16, 255, 20);
		friendTab.add(lblOnlineFriends);
		
		// Group Tab
		JPanel groupTab = new JPanel();
		groupTab.setBackground(Color.white);
		groupTab.setLayout(null);
		tabbedPane.add("Groups", groupTab);
		
		JList<String> listGroups = new JList<String>(group.getAllGroupsByUserId(user.getUser()));
		listGroups.setBounds(15, 16, 255, 315);
		groupTab.add(listGroups);
		
		// User Tab
		JPanel userTab = new JPanel();
		userTab.setBackground(Color.white);
		userTab.setLayout(null);
		tabbedPane.addTab("Users", userTab);
		
		JList<String> listUsers = new JList<String>(user.getAllUsers());
		listUsers.setBounds(15, 51, 255, 280);
		userTab.add(listUsers);
		
		JLabel lblOnlineUsers = new JLabel("1 Online Users");
		lblOnlineUsers.setBounds(15, 16, 255, 20);
		userTab.add(lblOnlineUsers);
		
		JToggleButton tglbtnStatus= new JToggleButton("Status: Online");
		tglbtnStatus.setBounds(132, 136, 163, 29);
		userPanel.add(tglbtnStatus);
   
		// User Tab - User Profile Frame 
		JFrame profileFrame = new JFrame();
        profileFrame.setVisible(false);
        profileFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        profileFrame.setBounds(100, 100, 300, 400);
		
		JPanel profilePanel = new JPanel();
		profilePanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		profileFrame.setContentPane(profilePanel);
		profilePanel.setLayout(null);
		
		JLabel profileImg = new JLabel();
		profileImg.setBounds(80, 31, 102, 102);
		profilePanel.add(profileImg);
		
		JLabel lblProfileName = new JLabel();
		lblProfileName.setBounds(90, 149, 248, 20);
		profilePanel.add(lblProfileName);
		
		JLabel lblProfileDescription = new JLabel();
		lblProfileDescription.setBounds(15, 180, 248, 20);
		profilePanel.add(lblProfileDescription);
		
		// Start of Group Chat
		JPanel chatPanel = new JPanel();
		chatPanel.setBackground(Color.white);
		chatPanel.setBounds(350, 47, 613, 581);
		chatPanel.setLayout(null);
		mainPanel.add(chatPanel);
		
		JLabel lblCurrentGroup = new JLabel("Group Name: ");
		lblCurrentGroup.setBounds(15, 16, 583, 20);
		chatPanel.add(lblCurrentGroup);
		
		// Chat Message Panel
		JPanel messagePanel = new JPanel();
		messagePanel.setBackground(Color.white);
		messagePanel.setBounds(15, 52, 425, 469);
		messagePanel.setBorder(BorderFactory.createLineBorder(Color.gray));
		messagePanel.setLayout(null);
		chatPanel.add(messagePanel);
		
		textArea = new JTextArea();
		textArea.setEditable(false);
		textArea.setBounds(25, 65, 400, 444);
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
	        	group.removeUserFromAllGroups(user.getUser());
	        	network.sendBroadcastMessage("RemoveUser:" + user.getUser());
	        	if (user.getAllUsers().getSize() == 0) {
	        		jedis.flush();
	        		System.out.println("Flushed!");
	        	}
	        }
		});
		
		/** -------------------------------------------------------- SEARCH MESSAGE ------------------------------------------------------------ **/
		searchMsg.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ArrayList<String> searchList = new ArrayList<String>();
				
				String word = JOptionPane.showInputDialog("Search Word");
				
				String conversation = textArea.getText();
				
				String [] msg = conversation.split("\\\n");
				
				for(int i =0; i< msg.length; i++) {
					if(msg[i].contains(word)) {
						searchList.add(msg[i]);
					}
				}
				
				String searchResult = "";
				for(String c : searchList) {
					searchResult += c + "\n";
				}
				
				if(!searchResult.isEmpty()) {
					JOptionPane.showMessageDialog(null, searchResult, "SEARCH RESULT", JOptionPane.INFORMATION_MESSAGE);
					
				}
				else {
					JOptionPane.showMessageDialog(null, "No Search Found.", "SEARCH RESULT", JOptionPane.INFORMATION_MESSAGE);
				}
			
				
				System.out.println("search list:" + Arrays.toString(searchList.toArray()));
				
			}
		});
		
		/** -------------------------------------------------------- EDIT USER ------------------------------------------------------------ **/
		editUser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				editFrame.setVisible(true);
				txtEditName.setText(user.getUser());
				txtEditDescription.setText(user.getDescription(user.getUser()));
				
				btnBrowse.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						JFileChooser fc = new JFileChooser();
						FileNameExtensionFilter filter = new FileNameExtensionFilter("JPG & PNG Images", "jpg", "png");
						fc.setFileFilter(filter);
						int returnVal = fc.showOpenDialog(editPanel);
						if (returnVal == JFileChooser.APPROVE_OPTION) {
							lblImageUrl.setText(fc.getSelectedFile().getAbsolutePath());
						}
					}
				});
				
				btnSave.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						if (txtEditName.getText().isEmpty()) {
							lblNameError.setText("User ID cannot be blank");
						} else {
							if (!user.getUser().equals(txtEditName.getText())) {
								network.sendBroadcastMessage("CheckUserID:" + txtEditName.getText());
								sleep();
							}
							
							if (user.isUserIdTaken()) {
								lblNameError.setText("User ID has been taken");
							} else {
								if (user.isIdFormatValid(txtEditName.getText())) {
									String des = " ";
									if (!txtEditDescription.getText().isEmpty()) {
										des = txtEditDescription.getText();
									}
									BufferedImage bImage = null;
									if (!lblImageUrl.getText().isEmpty()) {
										try {
											File img = new File(lblImageUrl.getText());
											bImage = ImageIO.read(img);
											ImageIO.write(bImage, "png", new File(System.getProperty("user.dir") + "\\img\\" + txtEditName.getText() + ".png"));
										} catch (IOException ie) {
											System.out.println("Exception occured :" + ie.getMessage());
										}
									}
									network.sendBroadcastMessage("UpdateUser:" + user.getUser() + ":" + txtEditName.getText() + ":" + des);
									editFrame.dispose();
								} else {
									lblNameError.setText("Invalid User ID format");
								}
							}
						}
					}
				});
				
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
		
		
		/** -------------------------------------------------------- ADD GROUP ------------------------------------------------------------ **/
		addGroup.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String groupName = JOptionPane.showInputDialog(main, "Enter group name");
				
				if (groupName != null) {
					network.sendBroadcastMessage("CheckGroupName:" + groupName);
					sleep();
					
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
		
		
		/** -------------------------------------------------------- EDIT GROUP ----------------------------------------------------------- **/
		editGroup.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String oldGroupName = user.getCurrentGroup();
				String newGroupName = JOptionPane.showInputDialog(main, "Rename group name for " + oldGroupName);
				
				if (newGroupName != null) {
					network.sendBroadcastMessage("CheckGroupName:" + newGroupName);
					sleep();
					
					if (!group.isGroupNameTaken()) {
						network.sendBroadcastMessage("UpdateGroupName:" + oldGroupName + ":" + newGroupName + ":" + user.getUser());
						JOptionPane.showMessageDialog(main,  "Group has been updated successfully", "Success", JOptionPane.PLAIN_MESSAGE);
					} else {
						JOptionPane.showMessageDialog(main, "Group name has been taken", "Error", JOptionPane.ERROR_MESSAGE);
					}			
				}
			}
		});
		
		/** -------------------------------------------------------- LEAVE GROUP ---------------------------------------------------------- **/
		leaveGroup.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String grp = user.getCurrentGroup();
				int option = JOptionPane.showConfirmDialog(main, "Are you sure you want to leave " + grp + "?", 
						"Leave Group", JOptionPane.YES_NO_OPTION);
				if (option == JOptionPane.YES_OPTION) {
					network.sendBroadcastMessage("LeaveGroup:" + grp + ":" + user.getUser());
					JOptionPane.showMessageDialog(main, "You have left the group successfully", "Success", JOptionPane.PLAIN_MESSAGE);
					clearChat();
				}
			}
		});
		
		/** -------------------------------------------------------- DELETE GROUP --------------------------------------------------------- **/
		deleteGroup.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int option = JOptionPane.showConfirmDialog(main, "Are you sure you want to delete " + user.getCurrentGroup() + "?",
						"Delete Group", JOptionPane.YES_NO_OPTION);
				if (option == JOptionPane.YES_OPTION) {
					network.sendBroadcastMessage("DeleteGroup:" + user.getCurrentGroup() + ":" + user.getUser());
					JOptionPane.showMessageDialog(main, "Group has been deleted successfully", "Success", JOptionPane.PLAIN_MESSAGE);
				}
			}
		});
		
		/** -------------------------------------------------------- ADD MEMBER ----------------------------------------------------------- **/
		addMember.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				List<String> list = listUsers.getSelectedValuesList();
				if(list.contains(user.getUser())) {
					JOptionPane.showMessageDialog(main, "Unable to add yourself into the group", "Error", JOptionPane.ERROR_MESSAGE);
				} else if (!list.isEmpty()) {
					int option = JOptionPane.showConfirmDialog(main, "Are you sure you want to add the following members: " + list + "?",
							"Add Member(s)", JOptionPane.YES_NO_OPTION);
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
		
		/** -------------------------------------------------------- DELETE MEMBER -------------------------------------------------------- **/
		deleteMember.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				List<String> list = listMembers.getSelectedValuesList();
				if (!list.isEmpty()) {
					int option = JOptionPane.showConfirmDialog(main, "Are you sure you want to delete the following members: " + list + "?",
							"Delete Member(s)", JOptionPane.YES_NO_OPTION);
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
		
		/** -------------------------------------------------------- ADD FRIENDS - -------------------------------------------------------- **/
		btnAddFriend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnSend.setEnabled(false);
	            String list = listofUsersNotFriends.getSelectedValue();
				if (verifyIfUserExistInMyFriendList(list)) {
					JOptionPane.showMessageDialog(null, "Already added as friend!");
					btnSend.setEnabled(true);
				} else if (list.equals(user.getUser())) {
					JOptionPane.showMessageDialog(null, "You can't add yourself!");
					btnSend.setEnabled(true);
				} else {
					// AddFriend command with current user and adding user
					String msg = ("AddFriend:" + user.getUser() + ":" + list);
					network.sendBroadcastMessage(msg);
					Timer buttonTimer = new Timer();
					buttonTimer.schedule(new TimerTask() {
						@Override
						public void run() {
							btnSend.setEnabled(true);
				        	addFriendFrame.dispose();
						}
					}, 3000);
				}
			}
		});
		
		
		/** -------------------------------------------------------- REMOVE FRIENDS- -------------------------------------------------------- **/
		removeFriend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (listFriends.getSelectedValue() == null) {
					JOptionPane.showMessageDialog(null, "Please select a friend from friend list to delete",
							"Message Dialog", JOptionPane.PLAIN_MESSAGE);
				}
				
				else {
					String msg = "DeleteFriend";
					// to delete from user's name from the friend list of the deleted friend
					// format is "DeleteFriend:deletedFriendName:myName"
					msg = msg + ":" + listFriends.getSelectedValue() + ":" + user.getUser();
					int index = findIndexOfFriend(listFriends.getSelectedValue());
					int onlineListIndex = findIndexOfFriendInOnlineList(listFriends.getSelectedValue());
					if (onlineListIndex > -1) {
						listOfMyFriends.remove(onlineListIndex);
					}
					System.out.println(listFriends.getSelectedValue());
					System.out.println("index of friend is" + onlineListIndex);
					fullListOfMyFriends.remove(index);
					friendsModel.removeElement(listFriends.getSelectedValue());
					network.sendBroadcastMessage(msg);

				}
				
				List<String> list = listFriends.getSelectedValuesList();
				if (!list.isEmpty()) {
					int option = JOptionPane.showConfirmDialog(main, "Are you sure you want to delete the following friend: " + list + "?",
							"Delete Friends(s)", JOptionPane.YES_NO_OPTION);
					if (option == JOptionPane.YES_OPTION) {
						for (int i = 0; i < list.size(); i++) {
							// DeleteFriend:deletedfriendname:myusername
							network.sendBroadcastMessage("DeleteFriend:" + list.get(i) + ":" + user.getUser());
							System.out.println();
						}
					}
				 else {
					JOptionPane.showMessageDialog(main, "No friend(s) selected", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	});
		/** -------------------------------------------------------- TOGGLE ONLINE/OFFLINE- -------------------------------------------------------- **/

		tglbtnStatus.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					tglbtnStatus.setText("Status: Offline");
					
					// format "O/OnlineOrOffline/myName/myPort"
					String msg = "O?";
					//msg = msg + "/" + "Offline" + "/" + user.getName() + "/" + user.getPort();
					//performSendToMain(msg);
				} else if (e.getStateChange() == ItemEvent.DESELECTED) {
					tglbtnStatus.setText("Online");

					// format "O/OnlineOrOffline/myName/myPort"
					String msg = "O?";
					//msg = msg + "/" + "Online" + "/" + user.getName() + "/" + user.getPort();
					//performSendToMain(msg);
				}

			}

		});
		
		/** -------------------------------------------------------- SEND MESSAGE- -------------------------------------------------------- **/
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!txtMessage.getText().isEmpty()) {
					String msg = user.getUser() + ": " + txtMessage.getText();
					network.sendChatMessage(msg, user.getCurrentGroup());
					txtMessage.setText("");
					getChat();
				}
			}
		});
		
		
		/** ----------------------------------- ONCLICK LISTFRIENDS ---------------------------------- **/
		
		listFriends.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				List<String> list = listFriends.getSelectedValuesList();
				if (evt.getClickCount() == 2 && list != null) {
					// Double-click detected
					txtMessage.setText("/m " + list.toString() + " ");
				}
			}
		});
					
		
		/** ----------------------------------- ONCLICK LISTGROUPS ---------------------------------- **/
		listGroups.addMouseListener(new MouseAdapter() {
		    public void mouseClicked(MouseEvent evt) {
		        if (evt.getClickCount() == 2) {
		        	user.setCurrentGroup(listGroups.getSelectedValue());
		        	network.connectChatGroup(group.getIpAddress(listGroups.getSelectedValue()));
		        	network.sendBroadcastMessage("RefreshGroup");
		        	getChat();
		        	getAllMessages();
		        }
		    }
		});
		
		/** ----------------------------------- ONCLICK LISTUSERS ----------------------------------- **/
		listUsers.addMouseListener(new MouseAdapter() {
		    public void mouseClicked(MouseEvent evt) {
		        if (evt.getClickCount() == 2) {
		            profileFrame.setVisible(true);
					ImageIcon img = getUserPicture(listUsers.getSelectedValue());
            		profileImg.setIcon(img);
					lblProfileName.setText(listUsers.getSelectedValue());
					lblProfileDescription.setText(user.getDescription(listUsers.getSelectedValue()));
            		
					profileFrame.addWindowListener(new WindowAdapter() {
				        @Override
				        public void windowClosing(WindowEvent e) {
				        	profileFrame.dispose();
				        }
					});
		        }
		    }
		});
		
		/* -------------------------------------------------------------- START OF REGISTER -------------------------------------------------------------- */
		
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
					sleep();
					
					if (user.isUserIdTaken()) {
						lblRegError.setText("User ID has been taken");
					} else {
						if (user.isIdFormatValid(userId)) {
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
			            		user.setUserIdTaken(true);
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
			            		network.sendBroadcastMessage("RefreshGroup");
			            		break;
			            	
			            	// [Old User ID] [New User ID] [New User Description]
			            	case "UpdateUser":
			            		user.updateUser(split[1], split[2], split[3]);
				            	if (user.getUser().equals(split[1])) {
				            		user.setUser(split[2]);
				            		
				            		group.updateMember(split[1], split[2]);
				            		network.sendBroadcastMessage("UpdateUserPicture");
				            		
				            	}
				            	network.sendBroadcastMessage("RefreshGroup");
			            		break;
			            		
			            	
			            	case "UpdateUserPicture":
			            		ImageIcon img = getUserPicture(user.getUser());
			            		lblUserImage.setIcon(img);
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
			            		if (user.getUser().equals(split[2])) {
				            		group.addMember(split[1], split[2]);
			            			JOptionPane.showMessageDialog(main, "You have been added into " + split[1], "Added", JOptionPane.PLAIN_MESSAGE);
			            		}
			            		network.sendBroadcastMessage("RefreshGroup");
			            		break;
			            	
			            	// [Group Name] [User ID]
			            	case "DeleteMember":
			            		if (user.getUser().equals(split[2])) {
			            			group.removeMember(split[1], split[2]);
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
			            		if (user.getUser().equals(split[3])) {
			            			group.addGroup(split[1], split[2], split[3]);
			            		}
				            	network.sendBroadcastMessage("RefreshGroup");
			            		break;
			            		
			            	// [Old Group Name] [New Group Name] [User ID]
			            	case "UpdateGroupName":
			            		if (user.getCurrentGroup() != null && user.getCurrentGroup().equals(split[1])) {
			            			group.updateGroup(split[1], split[2], user.getUser());
			            			user.setCurrentGroup(split[2]);
			            		}
			            		if (user.getUser().equals(split[3])) {
			            			group.updateKey(split[1], split[2]);
			            		}
			            		network.sendBroadcastMessage("RefreshGroup");
			        
			            		break;
			            		
			            	// [Group Name] [User ID]
			            	case "LeaveGroup":
			            		if (user.getUser().equals(split[2])) {
			            			user.setCurrentGroup(null);
				            		group.removeMember(split[1], split[2]);
			            		}
			            		network.sendBroadcastMessage("RefreshGroup");
			            		break;
			            		
			            	// [Group Name] [User ID]
			            	case "DeleteGroup":
			            		if (user.getCurrentGroup().equals(split[1])) {
			            			user.setCurrentGroup(null);
			            			group.removeGroup(split[1], user.getUser());
			            			clearChat();
			            		}
			            		if (user.getUser().equals(split[2])) {
			            			group.removeKey(split[1]);
			            		}
			            		
			            		network.sendBroadcastMessage("RefreshGroup");
			            		break;
			            		
			            	case "RefreshGroup":
			            		group.getAllUsersByGroup(user.getCurrentGroup());
			            		group.getAllGroupsByUserId(user.getUser());
			            		break;
			            	
			            	// [Current user : User being added]
			            	case "AddFriend":
			            		// Addfriend:current user: user being added
			            		if (split[2].equals(user.getUser())) {
			            			String replyRequest;
			            			int answer = JOptionPane.showConfirmDialog(null, "Do you want to want accept friend invitation from "+split[1],
			            					"Hi "+user.getUser(), JOptionPane.YES_NO_OPTION);
			            			if (answer == 0) {
			            				replyRequest = "Accepted";
			            				User tempUser = new User();
			            				tempUser.setUser(split[1]);
			            				listOfMyFriends.add(tempUser);
			            				fullListOfMyFriends.add(tempUser);
			            				int pos = friendsModel.getSize();
			            				friendsModel.add(pos, tempUser.getUser());
			            			} else {
			            				replyRequest = "Rejected";
			            			}
			            			replyRequest = "A!:" + replyRequest + ":" + user.getUser() + ":" + split[1];
			            			network.sendBroadcastMessage(replyRequest);
			            			
			            		}
			            		
			            		
			            		break;
			            		
			            	case "A!":
			            		// Once accepted 
			            		// Addfriend:current user: user being added
			            		// reply format "A!/acceptOrReject/FriendName/myName"

			            					if (split[3].equals(user.getUser())) {
			            						switch (split[1]) {
			            						case "Accepted":
			            							// if accepted, add to list. Not appended straight to text area
			            							// to prevent cases of friends in the middle quitting
			            							User tempUser = new User();
			            							tempUser.setUser(split[2]);
			            							listOfMyFriends.add(tempUser);
			            							fullListOfMyFriends.add(tempUser);
			            							int pos = friendsModel.getSize();
			            							friendsModel.add(pos, tempUser.getUser());
			            							break;
			            						case "Rejected":
			            							JOptionPane.showMessageDialog(null, "Your friend Request Was Rejected!", "Hi "+user.getUser(),
			            									JOptionPane.PLAIN_MESSAGE);
			            							break;
			            						}
			            					}		
			            		break;

				            	
				            	// [FriendName:MyName]	
				            	case "DeleteFriend":
				            		if (split[1].equals(user.getUser())) {
				            							            		
				        				int index = findIndexOfFriend(split[2]);
				        				// different index in online list as sequence might be different
				        				int onlineListIndex = findIndexOfFriendInOnlineList(split[2]);
				        				if (onlineListIndex > -1) {
				        					listOfMyFriends.remove(onlineListIndex);
				        				}
				        				fullListOfMyFriends.remove(index);
				        				friendsModel.removeElement(split[2]);
				            		}
				            		break;
			            		
			            }
			            
			            lblUserID.setText(user.getUser());
			            lblUserDescription.setText(user.getDescription(user.getUser()));
			            lblOnlineUsers.setText(user.getAllUsers().getSize() + " Online Users");
			            lblOnlineFriends.setText(friendsModel.getSize() + " Online Friends");
	         
			            if (user.getCurrentGroup() != null && friendsModel != null) {
			            	addMember.setEnabled(true);
			            	deleteMember.setEnabled(true);
			            	editGroup.setEnabled(true);
			            	leaveGroup.setEnabled(true);
			            	deleteGroup.setEnabled(true);
			            	txtMessage.setEnabled(true);
			        		btnSend.setEnabled(true);
			        		searchMsg.setEnabled(true);
			        		
			        		lblCurrentGroup.setText("Group Name: " + user.getCurrentGroup());
			            } else {
			            	addMember.setEnabled(false);
			            	deleteMember.setEnabled(false);
			            	editGroup.setEnabled(false);
			            	leaveGroup.setEnabled(false);
			            	deleteGroup.setEnabled(false);
			            	txtMessage.setEnabled(false);
			        		btnSend.setEnabled(false);
			        		searchMsg.setEnabled(false);
			        		
			        		lblCurrentGroup.setText("Group Name: ");
			            }
			            
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				}
			}
		}).start();
	}
	
	public ImageIcon getUserPicture(String id) {
		String dir = System.getProperty("user.dir") + "\\img\\" + id + ".png";
		File tmp = new File(dir);
		if (!tmp.exists()) {
			dir = "img/default.jpg";
		}
		
		return new ImageIcon(new ImageIcon(dir).getImage().getScaledInstance(102, 102, Image.SCALE_DEFAULT));	
	}
	
	public void sleep() {
		try {
			Thread.sleep(100);
		} catch (InterruptedException ie) {
			ie.printStackTrace();
		}
	}
	
	public void getChat() {
		MulticastSocket chatSocket = network.getChatSocket();
		new Thread(new Runnable() {
			@Override
			public void run() {
				byte buf[] = new byte[1000];
				DatagramPacket dgpReceived = new DatagramPacket(buf, buf.length);
				while (true) {
					try {
						chatSocket.receive(dgpReceived);
						byte[] receivedData = dgpReceived.getData();
						int length = dgpReceived.getLength();
						String msg = new String(receivedData, 0, length);
						textArea.append(msg + "\n");
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				}
			}
		}).start();
	}
	
	public int findIndexOfFriend(String username) {

		for (int i = 0; i < fullListOfMyFriends.size(); i++) {
			if (fullListOfMyFriends.get(i).getUser().equals(username)) {
				return i;
			}
		}
		return -1;

	}
	
	public int findIndexOfFriendInOnlineList(String username) {

		for (int i = 0; i < listOfMyFriends.size(); i++) {
			if (listOfMyFriends.get(i).getUser().equals(username)) {
				return i;
			}
		}
		return -1;

	}
	
	public boolean verifyIfUserExistInMyFriendList(String username) {

		for (int i = 0; i < friendsModel.size(); i++) {
			if (friendsModel.get(i).contains(username)) {
				return true;
			}
		}
		return false;

	}
		
	public void getAllMessages() {
		List<String> messageList = jedis.getMessages(user.getCurrentGroup());
		if (messageList != null) {
			clearChat();
			for (int i = 0; i < messageList.size(); i++ ) {
				textArea.append(messageList.get(i) + "\n");
			}
		}
	}
	
	public void debugMsg(String msg)// Purpose is to help you view msg easier by
	// appending it to the chat group/s msg
	{
		textArea.append("Console Msg " + msg + "\n");
	}
	
	public void clearChat() {
		textArea.setText("");
	}
}
