package whatschat;

import java.util.HashMap;
import java.util.Map;

import javax.swing.DefaultListModel;

public class User {
	
	private DefaultListModel<String> usersModel = new DefaultListModel<String>();
	private DefaultListModel<String> friendsModel = new DefaultListModel<String>();
	private Map<String, String> usersMap = new HashMap<String, String>();
	private Map<String, String> friendsMap = new HashMap<String, String>();
	private String userId = "";
	private boolean userIdExists = false;
	
	private String groupName = "";
	private String CurrentGroupIP = "";
	private String Port;
	
	public void setUser(String id) {
		userId = id;
	}
	
	public String getUser() {
		return userId;
	}
	
	public boolean isIdFormatValid(String id) {
		return id.matches("^[a-zA-Z][.\\S]{0,7}");
	}
	
	public void setPort(String Port) {
		this.Port = Port;
	}
	
	public void setCurrentGroup(String name) {
		groupName = name;
	}
	
	public String getCurrentGroup() {
		return groupName;
	}
	
	public String getDescription(String id) {
		return usersMap.get(id);
	}

	public boolean isUserIdTaken() {
		return userIdExists;
	}
	
	public void setUserIdTaken(boolean check) {
		userIdExists = check;
	}
	
	public void addUser(String user, String description) {
		if (!usersModel.contains(user)) {
			usersModel.addElement(user);
			usersMap.put(user, description);
		}
	}
	
	public void removeUser(String user) {
		if (usersModel.contains(user)) {
			usersModel.removeElement(user);
		}
	}
	
	public void updateUser(String oldId, String newId, String newDescription) {
		if (usersModel.contains(oldId)) {
			usersModel.removeElement(oldId);
			usersModel.addElement(newId);
			
			usersMap.remove(oldId);
			usersMap.put(newId, newDescription);
		}
	}
	
	public void addFriend(String user, String description) {
		if (!friendsModel.contains(user)) {
			friendsModel.addElement(user);
			friendsMap.put(user, description);
		}
	}
	
	public void removeFriend(String user) {
		if (friendsModel.contains(user)) {
			friendsModel.removeElement(user);
		}
	}
	
	public DefaultListModel<String> getAllUsers() {
		return usersModel;
	}
	
	public DefaultListModel<String> getAllFriends() {
		return friendsModel;
	}
	
}
