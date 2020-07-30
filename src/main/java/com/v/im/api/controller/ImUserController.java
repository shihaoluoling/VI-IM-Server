package com.v.im.api.controller;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.v.im.api.entity.Message;
import com.v.im.api.entity.SendInfo;
import com.v.im.common.utils.ChatUtils;
import com.v.im.message.entity.ImMessage;
import com.v.im.message.service.IImMessageService;
import com.v.im.tio.StartTioRunner;
import com.v.im.tio.TioServerConfig;
import com.v.im.tio.WsOnlineContext;
import com.v.im.user.entity.*;
import com.v.im.user.mapper.FileDescMapper;
import com.v.im.user.mapper.ImGroupMapper;
import com.v.im.user.mapper.ImUserFriendMapper;
import com.v.im.user.service.IImChatGroupService;
import com.v.im.user.service.IImChatGroupUserService;
import com.v.im.user.service.IImUserFriendService;
import com.v.im.user.service.IImUserService;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.tio.core.ChannelContext;
import org.tio.core.Tio;
import org.tio.server.ServerGroupContext;
import org.tio.websocket.common.WsResponse;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 前端控制器
 *
 * @author 皓天
 * @since 2020-7-07
 */
@RestController
@RequestMapping("/api/user")
@CrossOrigin
public class ImUserController {

    private final Logger logger = LoggerFactory.getLogger(ImUserController.class);

    @Resource
    private StartTioRunner startTioRunner;

    @Resource
    @Qualifier(value = "imUserService")
    private IImUserService imUserService;

    @Resource
    @Qualifier(value = "imUserFriendService")
    private IImUserFriendService imUserFriendService;

    @Resource
    @Qualifier(value = "iImMessageService")
    private IImMessageService iImMessageService;
    @Resource
    @Qualifier(value = "imChatGroupServiceImpl")
    private IImChatGroupService iImChatGroupService;
    @Resource
    @Qualifier(value = "imChatGroupUserService")
    private IImChatGroupUserService iImChatGroupUserService;
    @Autowired
    FileDescMapper fileDescMapper;
    @Autowired
    private ImGroupMapper imGroupMapper;
    @Autowired
    private ImUserFriendMapper imUserFriendMapper;
    /**
     * 用户信息初始化
     *
     * @param request request
     * @return json
     */
    @RequestMapping("init")
    public Map<String, Object> list(HttpServletRequest request) {
        logger.debug("init");
        Map<String, Object> objectMap = new HashMap<>();
        //获取好友信息
        String username = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        ImUser user = imUserService.getByLoginName(username);
        objectMap.put("friends", imUserFriendService.getUserFriends(user.getId()));

        //获取本人信息
        String host = ChatUtils.getHost(request);
        QueryWrapper<ImUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("login_name", username);
        user.setAvatar(user.getAvatar());
        user.setPassword(null);
        objectMap.put("me", user);

        //用户的群组信息
        objectMap.put("groups", imUserService.getChatGroups(user.getId()));
        return objectMap;
    }


    /**
     * 获取群组的用户
     *
     * @param chatId 群组id
     * @return 用户List
     */
    @RequestMapping("chatUserList")
    public List<ImUser> chatUserList(String chatId) {
        return imUserService.getChatUserList(chatId);
    }

    /**
     * 发送信息给用户
     * 注意：目前仅支持发送给在线用户
     *
     * @param userId 接收方id
     * @param msg    消息内容
     */
    @PostMapping("sendMsg")
    public void sendMsg(String userId, String msg, HttpServletRequest request) throws Exception {
        String host = ChatUtils.getHost(request);
        System.out.println("我是host"+host);
        ServerGroupContext serverGroupContext = startTioRunner.getAppStarter().getWsServerStarter().getServerGroupContext();
        System.out.println(msg+"发消息");
        SendInfo sendInfo = new SendInfo();
        sendInfo.setCode(ChatUtils.MSG_MESSAGE);
        Message message = new Message();
        message.setId("system");
        message.setFromid("system");
        message.setContent(msg);
        message.setMine(false);
        message.setTimestamp(System.currentTimeMillis());
        message.setType(ChatUtils.FRIEND);
        message.setAvatar(host + "/img/icon.png");
        message.setUsername("系统消息");
        sendInfo.setMessage(message);

        ChannelContext cc = WsOnlineContext.getChannelContextByUser(userId);
        if (cc != null && !cc.isClosed) {
            WsResponse wsResponse = WsResponse.fromText(new ObjectMapper().writeValueAsString(sendInfo), TioServerConfig.CHARSET);
            Tio.sendToUser(serverGroupContext, userId, wsResponse);
        } else {
            saveMessage(message, ChatUtils.UNREAD, userId);
        }
    }


    private void saveMessage(Message message, String readStatus, String userId) {
        ImMessage imMessage = new ImMessage();
        imMessage.setToId(userId);
        imMessage.setFromId(message.getFromid());
        imMessage.setSendTime(System.currentTimeMillis());
        imMessage.setContent(message.getContent());
        imMessage.setReadStatus(readStatus);
        imMessage.setType(message.getType());
        iImMessageService.saveMessage(imMessage);
    }

    @PostMapping("test")
    public void sendMsgee(FileDesc fileDesc) {
        System.out.println(fileDescMapper.insert(fileDesc));
    }
    @PostMapping("addGroupChat")
    public boolean testtest(String test,String userId,String url) {
        System.out.println(url+"我是url");
        ImChatGroup imChatGroup = new ImChatGroup();
        imChatGroup.setName(test);
        imChatGroup.setMaster(userId);
        imChatGroup.setAvatar(url);
        iImChatGroupService.save(imChatGroup);
        System.out.println(imChatGroup.getId()+"hah1");
        ImChatGroupUser imChatGroupUser = new ImChatGroupUser();
        imChatGroupUser.setChatGroupId(imChatGroup.getId());
        imChatGroupUser.setUserId(userId);
        imChatGroupUser.setCreateDate(new Date());
        boolean b = iImChatGroupUserService.save(imChatGroupUser);
        return b;
    }
    @RequestMapping(value = "search", method = RequestMethod.GET)
    public List<ImUser> search(String phone) {
        System.out.println(phone);

        return imUserService.getUsers(phone);
    }
    @RequestMapping(value = "selectGroup", method = RequestMethod.GET)
    public List<ImGroup> selectGroup(String userId) {
        Map<String,Object> map = new HashMap();
        map.put("user_id",userId);
        return imGroupMapper.selectByMap(map);
    }

    @RequestMapping(value = "addUser", method = RequestMethod.POST)
    public int addUser(String addUserId,String userGroupId,String beUserGroupId,String beAddUser,String state) {

        Map<String,Object> map = new HashMap<>();
        map.put("user_id",addUserId);
        map.put("friend_id",beAddUser);
        List<ImUserFriend> friends = imUserFriendMapper.selectByMap(map);
        System.out.println("我是fr"+friends);
        if (friends.size()==0){
            ImUserFriend imUserFriend = new ImUserFriend();
            imUserFriend.setUserId(addUserId);
            imUserFriend.setUserGroupId(userGroupId);

            imUserFriend.setFriendId(beAddUser);

            imUserFriend.setFriendGroupId(beUserGroupId);
            imUserFriend.setDelFlag(state);
            imUserFriendMapper.insert(imUserFriend);
        }
        return 0;
    }
    @RequestMapping(value = "agreedAddUser", method = RequestMethod.POST)
    public int agreedAddUser(String addUserId,String userGroupId,String beUserGroupId,String beAddUser,String state) {

        Map<String,Object> map = new HashMap<>();
        map.put("user_id",addUserId);
        map.put("friend_id",beAddUser);
        List<ImUserFriend> friends = imUserFriendMapper.selectByMap(map);
        if (friends.size()!=0){
            ImUserFriend imUserFriend = new ImUserFriend();
            imUserFriend.setFriendGroupId(beUserGroupId);
            imUserFriend.setDelFlag(state);

            UpdateWrapper<ImUserFriend> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("user_id",addUserId);
            updateWrapper.eq("friend_id",beAddUser);
            imUserFriendMapper.update(imUserFriend,updateWrapper);
            return 0;
        }
        return 1;
    }

    @RequestMapping(value = "selectUserFriend", method = RequestMethod.GET)
    public Map selectUserFriend(String userId) {
        System.out.println(userId+"username777");
        Map<String, Object> objectMap = new HashMap<>();
        //获取好友信息
        ImUser user = imUserService.getByUserId(userId);
        objectMap.put("friends", imUserFriendService.getUserFriends(user.getId()));

        return objectMap;
    }
}
