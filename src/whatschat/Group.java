package whatschat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.swing.DefaultListModel;

public class Group {

	// List of all groups
	private DefaultListModel<String> groupsModel = new DefaultListModel<String>();
	// Used to hold the list of all groups of a user
	private DefaultListModel<String> userGroupsModel = new DefaultListModel<String>();
	// Used to hold the list of all members of a group
	private DefaultListModel<String> groupMembersModel = new DefaultListModel<String>();
	
	// Maps all users to their respective list of groups
	private Map<String, List<String>> userGroupMap = new HashMap<String, List<String>>();
	
	JedisDB jedis = new JedisDB();

	private boolean groupNameExists = false;
	
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
			
			jedis.addIpAddress(name, ip);
			jedis.addMember(name, id);
		
			List<String> groupList;
			if (!userGroupMap.containsKey(id)) {
				groupList = new ArrayList<>();
			} else {
				groupList = userGroupMap.get(id);
			}
			
			groupList.add(name);
			userGroupMap.put(id, groupList);
		}
	}
	
	public void removeGroup(String name) {
		if (groupsModel.contains(name)) {
			groupsModel.removeElement(name);
			
			List<String> userList = jedis.getMembers(name);
			if (userList != null) {
				for (int i = 0; i < userList.size(); i++) {
					List<String> groupList = userGroupMap.get(userList.get(i));
					groupList.remove(name);
					
					userGroupMap.remove(userList.get(i));
					userGroupMap.put(userList.get(i), groupList);
				}
			}
			
			jedis.removeKey(name);
		}
	} 
	
	public void updateGroup(String oldName, String newName, String id) {
		if (groupsModel.contains(oldName)) {
			groupsModel.removeElement(oldName);
			groupsModel.addElement(newName);

			jedis.updateKey(oldName, newName);
			
			List<String> updateGroupList = userGroupMap.get(id);
			if (updateGroupList != null) {
				if (updateGroupList.contains(oldName)) {
					updateGroupList.remove(oldName);
					updateGroupList.add(newName);
					
					userGroupMap.remove(id);
					userGroupMap.put(id, updateGroupList);
				}
			}
		}
	}
	
	// Add new member to the group
	public void addMember(String name, String id) {
		jedis.addMember(name, id);
		
		List<String> groupList;
		if (!userGroupMap.containsKey(id)) {
			groupList = new ArrayList<>();
		} else {
			groupList = userGroupMap.get(id);
		}
		
		groupList.add(name);
		userGroupMap.put(id, groupList);
	}
	
	public void removeMember(String name, String id) {
		jedis.removeMember(name, id);
		
		List<String> groupList = userGroupMap.get(id);
		groupList.remove(name);
		userGroupMap.put(id, groupList);
	}
	
	public void removeUserFromAllGroups(String id) {
		List<String> groupList = userGroupMap.get(id);
		if (groupList != null) {
			for (int i = 0; i < groupList.size(); i++) {
				jedis.removeMember(groupList.get(i), id);
			}
		}
	}
	
	public void updateMember(String oldId, String newId) {
		List<String> groupList = userGroupMap.get(oldId);
		userGroupMap.put(newId, groupList);
		userGroupMap.remove(oldId);
		
		// list of groups that the user is in
		if (groupList != null) {
			for (int i = 0; i < groupList.size(); i++) {
				jedis.removeMember(groupList.get(i), oldId);
				jedis.addMember(groupList.get(i), newId);
			}
		}
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
		userGroupsModel.clear();
		List<String> groupsList = userGroupMap.get(id);
		
		if (groupsList != null) {
			for (int i = 0; i < groupsList.size(); i++) {
				userGroupsModel.addElement(groupsList.get(i));
			}
		}
		return userGroupsModel;
	}
	
	public DefaultListModel<String> getAllUsersByGroup(String name) {
		groupMembersModel.clear();
		List<String> membersList = jedis.getMembers(name);
		if (membersList != null) {
			for (int i = 0; i < membersList.size(); i++) {
				groupMembersModel.addElement(membersList.get(i));
			}
		}
		return groupMembersModel;
	}
	
	public String generateRandomIp() {
		Random r = new Random();
		return "230.1." + r.nextInt(256) + "." + r.nextInt(256);
	}
	
	public String getIpAddress(String name) {
		return jedis.getIpAddress(name);
	}
}
