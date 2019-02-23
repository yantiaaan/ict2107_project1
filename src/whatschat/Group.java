package whatschat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.swing.DefaultListModel;
import javax.swing.ListModel;

public class Group {

	private DefaultListModel<String> groupsModel = new DefaultListModel<String>();
	private DefaultListModel<String> ipModel = new DefaultListModel<String>();
	private Map<String, String> groupIpMap = new HashMap<String, String>();
	private Map<String, List<String>> groupUserMap = new HashMap<String, List<String>>();
	private Map<String, List<String>> userGroupMap = new HashMap<String, List<String>>();

	private boolean groupNameExists = false;
	
	private Network network;
	
	public Group(Network nw) {
		network = nw;
	}
	
	public boolean isGroupNameTaken() {
		return groupNameExists;
	}
	
	public void setGroupNameTaken(boolean check) {
		groupNameExists = check;
	}
	
	// Creates a new group
	public void addGroup(String name, String ip, String id) {
		if (!groupsModel.contains(name)) {
			groupsModel.addElement(name);
			ipModel.addElement(ip);
			groupIpMap.put(name, ip);
			network.connectChat(ip);
			
			// Key: GroupName; Value: Users (List)
			List<String> userList = new ArrayList<>();
			groupUserMap.put(name, userList);
			
			// Key: UserID; Value: Groups (List)
			List<String> groupList = new ArrayList<>();
			groupList.add(name);
			userGroupMap.put(id, groupList);
		}
	}
	
	public void removeGroup(String name) {
		if (groupsModel.contains(name)) {
			groupsModel.removeElement(name);
		}
	} 
	
	// Add new member to the group
	public void addMember(String name, String id) {
		List<String> userList = groupUserMap.get(name);
		userList.add(id);
		groupUserMap.replace(name, userList);
	}
	
	public void removeMember(String name, String id) {
		
	}
	
	public boolean groupNameExists(String name) {
		if (groupsModel.contains(name)) {
			return true;
		} else {
			return false;
		}
	}
	
	public DefaultListModel<String> getAllGroups() {
		return groupsModel;
	}
	
	// Get all groups of a user
	public DefaultListModel<String> getAllGroupsByUserId(String id) {
		DefaultListModel<String> groupsModel = new DefaultListModel<String>();
		List<String> groupsList = userGroupMap.get(id);

		if (groupsList != null) {
			for (int i = 0; i < groupsList.size(); i++) {
				groupsModel.addElement(groupsList.get(i));
			}
		}
		
		return groupsModel;
		
	}
	
	public String generateRandomIp() {
		Random r = new Random();
		String ip = "";
		while (true) {
			ip = "230.1." + r.nextInt(256) + "." + r.nextInt(256);
			if (!ipModel.contains(ip)) {
				return ip;
			}
		}
	}
}
