package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class WhatsappRepository {

    //Assume that each user belongs to at most one group
    //You can use the below mentioned hashmaps or delete these and create your own.
    private HashMap<Group, List<User>> groupUserMap;
    private HashMap<Group, List<Message>> groupMessageMap;
    private HashMap<Message, User> senderMap;
    private HashMap<Group, User> adminMap;
    private HashSet<String> userMobile;
    private int customGroupCount;
    private int messageId;

    public WhatsappRepository(){
        this.groupMessageMap = new HashMap<Group, List<Message>>();
        this.groupUserMap = new HashMap<Group, List<User>>();
        this.senderMap = new HashMap<Message, User>();
        this.adminMap = new HashMap<Group, User>();
        this.userMobile = new HashSet<>();
        this.customGroupCount = 0;
        this.messageId = 0;
    }

    public String createUser(String name, String mobile) throws Exception {
        if(userMobile.contains(mobile)){
            throw new Exception("User already exists");
        }
        User user = new User(name, mobile);
        userMobile.add(mobile);
        return "SUCCESS";
    }

    public Group createGroup(List<User> users){
        String groupName = "";
        if(users.size()>2){
            customGroupCount++;
            groupName = "Group " + customGroupCount;
        }
        else {
            groupName = users.get(1).getName();
        }
        Group group = new Group(groupName, users.size());
        groupUserMap.put(group, users);
        adminMap.put(group, users.get(0));
        return group;
    }

    public int createMessage(String content){
        messageId++;
        Message message = new Message(messageId, content);
        return message.getId();
    }

    public int sendMessage(Message message, User sender, Group group) throws Exception{
        //check group exists
        if (!groupUserMap.containsKey(group)){
            throw new Exception("Group does not exist");
        }
        //check user present
        List<User> users = groupUserMap.get(group);
        boolean flag = false;
        for (User user : users){
            if (user.equals(sender)){
                flag = true;
                break;
            }
        }
        if (!flag){
            throw new Exception("You are not allowed to send message");
        }

        List<Message> msgList = new ArrayList<>();
        for (Group g : groupMessageMap.keySet()){
            msgList = groupMessageMap.get(group);
        }
        msgList.add(message);
        groupMessageMap.put(group, msgList);
        return msgList.size();

    }

    public String changeAdmin(User approver, User user, Group group) throws Exception{
        if (!groupUserMap.containsKey(group)){
            throw new Exception("Group does not exist");
        }
        User admin = adminMap.get(group);
        if (!admin.equals(approver)){
            throw new Exception("Approver does not have rights");
        }
        boolean flag = false;
        List<User> userList = groupUserMap.get(group);
        for (User user1 : userList){
            if (user1.equals(user)){
                flag = true;
                break;
            }
        }
        if (!flag){
            throw new Exception("User is not a participant");
        }
        User oldAdmin = adminMap.get(group);
        adminMap.replace(group, oldAdmin, user);
        return "SUCCESS";
    }


}
