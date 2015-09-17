package com.liyu.pluginframe.util;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created with IntelliJ IDEA.
 * User: WangJian
 * Date: 13-11-1
 * Time: 上午9:43
 * To change this template use File | Settings | File Templates.
 */
public interface IGameDtSync {

    public static String ROOM_NORMAL="normal";
    public static String ROOM_PLAYING="playing";
    public static String ROOM_FULL="full";
    public static String ROOM_LOCK="lock";
    public static String ROOM_EMPTY="empty";

    /**
     * 加入大厅
     * by:王健 at:2015-08-09
     * @param username
     * @param appcode
     * @param dtid
     */
    public void syncJoinDtGame(String username, String appcode, String dtid, String url, int urlpoint);

    /**
     * 退出大厅
     * by:王健 at:2015-08-09
     * @param username
     * @param appcode
     * @param dtid
     */
    public void syncQuiteDtGame(String username, String appcode, String dtid);

    /**
     * 用户加入房间
     * by:王健 at:2015-08-09
     * @param username
     * @param roomid
     * @param user
     */
    public void syncUserJoinRoom(String username, String roomid, JSONObject user);

    /**
     * 用户退出房间
     * by:王健 at:2015-08-09
     * @param username
     * @param roomid
     */
    public void syncUserQuiteRoom(String username, String roomid);

    /**
     * 房间状态改变
     * by:王健 at:2015-08-09
     * @param roomid
     * @param status
     */
    public void syncChangeRoomStatus(String roomid, String status);

    /**
     * 大厅内房间列表
     * by:王健 at:2015-08-09
     * @param roomlist
     */
    public void syncRoomList(JSONArray roomlist);

    /**
     * 房间内信息
     * by:王健 at:2015-08-09
     * @param roomid
     * @param users
     */
    public void syncRoomInfoById(String roomid, JSONArray users);

    /**
     * 错误信息
     * by:王健 at:2015-08-09
     * @param message
     * @param status_code
     */
    public void syncError(String message, int status_code);
}
