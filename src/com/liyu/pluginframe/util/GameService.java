package com.liyu.pluginframe.util;

import com.mogu.util.Tools;
import com.netease.pomelo.DataCallBack;
import com.netease.pomelo.DataEvent;
import com.netease.pomelo.DataListener;
import com.netease.pomelo.PomeloClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: WangJian
 * Date: 13-11-1
 * Time: 上午9:43
 * To change this template use File | Settings | File Templates.
 */
public class GameService {

    private static GameService gameService = null;

    private String url;
    private int point;
    private IGameDtSync iGameDtSync;
    private IGameSync iGameSync;

    private String username, appcode, appcode_dt_id, roomid;

    private static ArrayList<String> shunxulist = new ArrayList<String>();
    private static Map<String, UserInfo> usermap = new HashMap<String, UserInfo>();

    private static PomeloClient pomeloClient;

    /**
     * 获取实例，单例模式
     * by:王健 at:2015-08-09
     * @return
     */
    public static GameService getInstance() {
        if (gameService == null) {

            gameService = new GameService();
        }

        return gameService;

    }


    /**
     * 大厅内被动接口绑定
     * by:王健 at:2015-08-09
     * @param iGameDtSync
     */
    public void setiGameDtSync(IGameDtSync iGameDtSync) {
        this.iGameDtSync = iGameDtSync;
    }

    public void removeiGameDtSync(){
        this.iGameDtSync = null;
    }

    private boolean is_Dt_sync(){
        if(iGameDtSync==null){
            return false;

        }else{
            return true;
        }
    }

    /**
     * 大厅内被动接口绑定
     * by:王健 at:2015-08-09
     * @param iGameSync
     */
    public void setiGameSync(IGameSync iGameSync) {
        this.iGameSync = iGameSync;
    }


    public void removeiGameSync(){
        this.iGameSync = null;
    }

    private boolean is_Room_sync(){
        if(iGameSync==null){
            return false;

        }else{
            return true;
        }
    }

    /**
     * 结果初步处理，错误和result 返回
     * by:王健 at:2015-08-09
     * @param jsonObject
     * @return
     */
    private Object doResult(JSONObject jsonObject) {
        boolean r = jsonObject.optBoolean("success", true);
        if (!r) {
            if(iGameDtSync==null){
                iGameDtSync.syncError(jsonObject.optString("message", "服务器错误"), jsonObject.optInt("status_code", 500));
            }
            if(iGameSync==null){
                iGameSync.syncError(jsonObject.optString("message", "服务器错误"), jsonObject.optInt("status_code", 500));
            }

            return null;
        }
        JSONObject result = jsonObject.optJSONObject("result");
        if (result != null) {

            return result;
        }
        JSONArray result_arr = jsonObject.optJSONArray("result");
        if (result_arr != null) {
            return result_arr;
        }
        return new JSONObject();
    }

    /**
     * 进入大厅接口
     * by:王健 at:2015-08-09
     * 增加token 参数，进行md5 加密判断
     * by:王健 at:2015-08-09
     * @param host
     * @param hostpoint
     * @param token
     * @param param
     */
    public void init_dt_PomeloClient(String host, int hostpoint, final String token, final JSONObject param) {
        // pomelo demo
        PomeloClient p = new PomeloClient(host, hostpoint);
        p.init();
        final String[] keys = {"appcode", "appcode_dt_id", "username"};
        Tools.sign(param, token, keys);
        p.request("gate.gateHandler.queryEntry", param, new DataCallBack() {
            @Override
            public void responseData(JSONObject msg) {
                try {
                    if (msg.getInt("code") == 200) {
                        if (pomeloClient != null && pomeloClient.hasConnect() && msg.getString("host").equals(url) && msg.getInt("port") == point) {
                            return;
                        }
                        pomeloClient = new PomeloClient(msg.getString("host"), msg.getInt("port"));
                        pomeloClient.init();
                        url = msg.getString("host");
                        point = msg.getInt("port");

                        Tools.sign(param, token, keys);
                        /**
                         * 进入大厅
                         * by:王健 at:2015-08-09
                         * 进入大厅，获取房间列表
                         * by:王健 at:2015-08-09
                         */
                        pomeloClient.request("connector.gameHandler.add_game_dt", param, new DataCallBack() {

                            @Override
                            public void responseData(JSONObject message) {
                                if(!is_Dt_sync()){
                                    return;
                                }
                                JSONArray result = (JSONArray) doResult(message);
                                if (result != null) {
                                    username = param.optString("username");
                                    appcode = param.optString("appcode");
                                    appcode_dt_id = param.optString("appcode_dt_id");
                                    iGameDtSync.syncJoinDtGame(username, appcode, appcode_dt_id, url, point);
                                    iGameDtSync.syncRoomList(result);
                                }

                            }
                        });

                        /**
                         * 掉线
                         * by:王健 at:2015-08-09
                         */
                        pomeloClient.on("disconnect", new DataListener() {
                            @Override
                            public void receiveData(DataEvent event) {
                                if(!is_Dt_sync()){
                                    return;
                                }
                                iGameDtSync.syncQuiteDtGame(username, appcode, appcode_dt_id);
                            }
                        });

                        /**
                         * 房间用户发生变动的接口
                         * by:王健 at:2015-08-09
                         * 人员变动包括，人离开大厅
                         * by:王健 at:2015-08-09
                         */
                        pomeloClient.on("memberChanged", new DataListener() {
                            @Override
                            public void receiveData(DataEvent event) {
                                if(!is_Dt_sync()){
                                    return;
                                }
                                JSONObject result = (JSONObject) doResult(event.getMessage());
                                if (result != null) {

                                    String roomid = result.optString("roomid");
                                    String username = result.optString("user");
                                    JSONObject userinfo = result.optJSONObject("userinfo");
                                    String changed = result.optString("changed");
                                    if ("exit".equals(changed)) {
                                        pomeloClient.disconnect();
                                    }else if("in".equals(changed)){
                                        iGameDtSync.syncUserJoinRoom(username, roomid, userinfo);
                                    } else {
                                        iGameDtSync.syncUserQuiteRoom(username, roomid);
                                    }
                                }
                            }
                        });

                        /**
                         * 房间状态变化：普通、在玩、满员
                         * by:王健 at:2015-08-09
                         */
                        pomeloClient.on("roomStatusChanged", new DataListener() {
                            @Override
                            public void receiveData(DataEvent event) {
                                if(!is_Dt_sync()){
                                    return;
                                }
                                JSONObject result = (JSONObject) doResult(event.getMessage());
                                if (result != null) {

                                    String roomid = result.optString("roomid");
                                    String status = result.optString("status");
                                    iGameDtSync.syncChangeRoomStatus(roomid, status);
                                }
                            }
                        });


                    }
                } catch (Exception e) {

                }

            }
        });
    }

    /**
     * 发出获取大厅内所有房间的接口， route不正确，待改动
     * by:王健 at:2015-08-09
     * @param param
     */
    public void query_room_dt_by_appcode(JSONObject param) {
        pomeloClient.request("connector.gameHandler.query_dt_rooms_by_appcode", param, new DataCallBack() {

            @Override
            public void responseData(JSONObject message) {
                if(!is_Dt_sync()){
                    return;
                }
                JSONArray result = (JSONArray) doResult(message);

                if (result != null) {
                    iGameDtSync.syncRoomList(result);
                }

            }
        });
    }

    /**
     * 获取房间内用户，根据id， route不正确，待改动
     * by:王健 at:2015-08-09
     * @param param
     */
    public void query_room_members_info_by_roomid(JSONObject param) {
        pomeloClient.request("connector.gameHandler.query_room_members_info_by_roomid", param, new DataCallBack() {

            @Override
            public void responseData(JSONObject message) {
                if(!is_Dt_sync()){
                    return;
                }
                JSONObject result = (JSONObject) doResult(message);
                if (result != null) {

                    JSONArray user_arr= result.optJSONArray("users");
                    JSONObject room_info_map = result.optJSONObject("room_info_map");
                    Iterator keys = room_info_map.keys();
                    String roomid;
                    while (keys.hasNext()){

                        roomid = (String)keys.next();
                        JSONArray users = room_info_map.optJSONArray(roomid);
                        JSONArray members = new JSONArray();

                        if(user_arr!=null){
                            for(int k=0;k<users.length();k++){
                                for(int m=0;m<user_arr.length();m++){
                                    try {
                                        if(users.getString(k).equals(user_arr.getJSONObject(m).optString("username"))){
                                            members.put(user_arr.getJSONObject(m));
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        iGameDtSync.syncRoomInfoById(roomid, members);
                    }


                }

            }
        });
    }

    /**
     *  退出大厅
     * by:王健 at:2015-08-09
     */
    public void quite_dt() {
        if(pomeloClient==null||!pomeloClient.hasConnect()){
            return;
        }
        pomeloClient.disconnect();
    }

    public ArrayList<String> get_users(){

        return shunxulist;
    }

    public UserInfo getUserInfo(String username){
        if(usermap.containsKey(username)){
            return usermap.get(username);
        }
        return null;
    }

    public void clear_users(){
        shunxulist.clear();
        usermap.clear();
    }

    public void add_user(String uname){
        shunxulist.add(uname);
    }


    /**
     * 进入大厅接口
     * by:王健 at:2015-08-09
     * 增加token 参数，进行md5 加密判断
     * by:王健 at:2015-08-09
     * @param host
     * @param hostpoint
     * @param token
     * @param param
     */
    public void init_room_PomeloClient(String host, int hostpoint, final String token, final JSONObject param) {
        // pomelo demo


        pomeloClient = new PomeloClient(host, hostpoint);
        pomeloClient.init();
        final String[] keys = {"appcode", "appcode_dt_id", "username"};

        Tools.sign(param, token, keys);
        /**
         * 进入大厅
         * by:王健 at:2015-08-09
         * 进入大厅，获取房间列表
         * by:王健 at:2015-08-09
         */
        pomeloClient.request("connector.gameHandler.add_game_room", param, new DataCallBack() {

            @Override
            public void responseData(JSONObject message) {
                if(!is_Room_sync()){
                    return;
                }
                JSONObject result = (JSONObject) doResult(message);
                if (result != null) {
                    username = param.optString("username");
                    appcode = param.optString("appcode");
                    appcode_dt_id = param.optString("appcode_dt_id");
                    roomid = param.optString("roomid");
                    shunxulist.clear();
                    usermap.clear();
                    UserInfo userInfo = null;
                    JSONObject user = null;
                    JSONArray user_arr= result.optJSONArray("users");
                    for(int i=0;i<user_arr.length();i++){
                        try {
                            user = user_arr.getJSONObject(i);
                            userInfo = new UserInfo();
                            userInfo.setUsername(user.optString("username"));
                            userInfo.setNickname(user.optString("nickname"));
                            userInfo.setHead(user.optInt("head"));
                            userInfo.setIcon_url(user.optString("icon_url"));
                            userInfo.setPoint(user.optInt("point"));
                            userInfo.setRank(user.optString("rank"));

                            usermap.put(userInfo.getUsername(), userInfo);
                            shunxulist.add(userInfo.getUsername());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    iGameSync.syncRoomMembers(shunxulist, usermap);
                    iGameSync.syncInRoom();

                }

            }
        });

        /**
         * 掉线
         * by:王健 at:2015-08-09
         */
        pomeloClient.on("disconnect", new DataListener() {
            @Override
            public void receiveData(DataEvent event) {
                if(!is_Room_sync()){
                    return;
                }
                iGameSync.syncQuiteRoom();
            }
        });

        /**
         * 房间用户发生变动的接口
         * by:王健 at:2015-08-09
         * 人员变动包括，人离开大厅
         * by:王健 at:2015-08-09
         */
        pomeloClient.on("roomAddUser", new DataListener() {
            @Override
            public void receiveData(DataEvent event) {
                if(!is_Room_sync()){
                    return;
                }
                JSONObject result = (JSONObject) doResult(event.getMessage());
                if (result != null) {

                    String roomid = result.optString("roomid");
                    String username = result.optString("user");
                    JSONObject user = result.optJSONObject("userinfo");
                    UserInfo userInfo = new UserInfo();
                    userInfo.setUsername(user.optString("username"));
                    userInfo.setNickname(user.optString("nickname"));
                    userInfo.setHead(user.optInt("head"));
                    userInfo.setIcon_url(user.optString("icon_url"));
                    userInfo.setPoint(user.optInt("point"));
                    userInfo.setRank(user.optString("rank"));

                    usermap.put(userInfo.getUsername(), userInfo);
                    shunxulist.add(userInfo.getUsername());
                    
                    iGameSync.syncMemberChange(username, true, user);
                }
            }
        });

        /**
         * 房间用户发生变动的接口
         * by:王健 at:2015-08-09
         * 人员变动包括，人离开大厅
         * by:王健 at:2015-08-09
         */
        pomeloClient.on("onLeave", new DataListener() {
            @Override
            public void receiveData(DataEvent event) {
                if(!is_Room_sync()){
                    return;
                }
                JSONObject result = (JSONObject) doResult(event.getMessage());
                if (result != null) {

                    String roomid = result.optString("roomid");
                    String username = result.optString("user");
                    usermap.remove(username);
                    shunxulist.remove(username);
                    iGameSync.syncMemberChange(username, false, null);
                }
            }
        });

        pomeloClient.on("onChat", new DataListener() {
            @Override
            public void receiveData(DataEvent event) {
                if(!is_Room_sync()){
                    return;
                }
                JSONObject result = (JSONObject) doResult(event.getMessage());
                if (result != null) {

                    String from = result.optString("from");
                    String to = result.optString("to");
                    String msg = result.optString("msg");
                    if(to==null){
                        iGameSync.syncChat(from, msg);
                    }else{
                        iGameSync.syncChat(from, to, msg);
                    }
                }
            }
        });

        pomeloClient.on("onEndPoint", new DataListener() {
            @Override
            public void receiveData(DataEvent dataEvent) {
                if(!is_Room_sync()){
                    return;
                }
                JSONObject result = (JSONObject) doResult(dataEvent.getMessage());
                if (result != null) {
                    JSONObject endpoints = result.optJSONObject("endpoints");

                    Iterator iterator = endpoints.keys();
                    String u = null;
                    while (iterator.hasNext()){
                        u = iterator.next().toString();
                        iGameSync.syncEndGamePoints(u, endpoints.optString(u));
                    }

                }

            }
        });

        pomeloClient.on("quickGame", new DataListener() {
            @Override
            public void receiveData(DataEvent dataEvent) {
                if(!is_Room_sync()){
                    return;
                }
                iGameSync.syncQuiteRoom();

            }
        });

        pomeloClient.on(MainDataTool.START_GAME, new DataListener() {
            @Override
            public void receiveData(DataEvent dataEvent) {
                if(!is_Room_sync()){
                    return;
                }
                JSONObject result = (JSONObject) doResult(dataEvent.getMessage());
                if (result != null) {
                    iGameSync.syncStartGame();
                }

            }
        });
        pomeloClient.on(MainDataTool.END_GAME, new DataListener() {
            @Override
            public void receiveData(DataEvent dataEvent) {
                if(!is_Room_sync()){
                    return;
                }
                JSONObject result = (JSONObject) doResult(dataEvent.getMessage());
                if (result != null) {
                    String from = result.optString("from");
                    if(from==null){
                        iGameSync.syncEndGame();
                    }else{
                        iGameSync.syncEndGame(from);
                    }

                }

            }
        });
        pomeloClient.on(MainDataTool.UPLOAD_GAME_POINT, new DataListener() {
            @Override
            public void receiveData(DataEvent dataEvent) {
                if(!is_Room_sync()){
                    return;
                }
                JSONObject result = (JSONObject) doResult(dataEvent.getMessage());
                if (result != null) {
                    String from = result.optString("from");
                    String point = result.optString("point");
                    iGameSync.syncGamePoints(from, point);
                }

            }
        });
        pomeloClient.on(MainDataTool.UPLOAD_END_GAME_POINT, new DataListener() {
            @Override
            public void receiveData(DataEvent dataEvent) {
                if(!is_Room_sync()){
                    return;
                }
                JSONObject result = (JSONObject) doResult(dataEvent.getMessage());
                if (result != null) {
                    String from = result.optString("from");
                    String point = result.optString("point");
                    iGameSync.syncEndGamePoints(from, point);
                }

            }
        });pomeloClient.on(MainDataTool.PUSH_GAME_DATA, new DataListener() {
            @Override
            public void receiveData(DataEvent dataEvent) {
                if(!is_Room_sync()){
                    return;
                }
                JSONObject result = (JSONObject) doResult(dataEvent.getMessage());
                if (result != null) {
                    String from = result.optString("from");
                    JSONArray users = result.optJSONArray("users");
                    if(users!=null&&users.length()>0){
                        String[] us = new String[users.length()];
                        iGameSync.syncGameData(from, us, result);
                    }else{
                        iGameSync.syncGameData(from, result);
                    }

                }

            }
        });pomeloClient.on(MainDataTool.PUSH_PROPERTY_DATA, new DataListener() {
            @Override
            public void receiveData(DataEvent dataEvent) {

                if(!is_Room_sync()){
                    return;
                }

                JSONObject result = (JSONObject) doResult(dataEvent.getMessage());
                if (result != null) {
                    String from = result.optString("from");
                    String property_flag = result.optString("property_flag");
                    JSONArray users = result.optJSONArray("users");
                    if(users!=null&&users.length()>0){
                        String[] us = new String[users.length()];
                        iGameSync.syncGamePropertyInfo(from, us, property_flag);
                    }else{
                        iGameSync.syncGamePropertyInfo(from, property_flag);
                    }

                }

            }
        });

        pomeloClient.on(MainDataTool.SEND_CHAT, new DataListener() {
            @Override
            public void receiveData(DataEvent dataEvent) {
                if(!is_Room_sync()){
                    return;
                }
                JSONObject result = (JSONObject) doResult(dataEvent.getMessage());
                if (result != null) {

                    String from = result.optString("from");
                    String to = result.optString("to");
                    String msg = result.optString("msg");
                    if(to==null){
                        iGameSync.syncChat(from, msg);
                    }else{
                        iGameSync.syncChat(from, to, msg);
                    }
                }

            }
        });
//
//        pomeloClient.on("in_room", new DataListener() {
//            @Override
//            public void receiveData(DataEvent dataEvent) {
//
//                cblist(dataEvent.getMessage());
//
//            }
//        });
//
//        pomeloClient.on("out_room", new DataListener() {
//            @Override
//            public void receiveData(DataEvent dataEvent) {
//
//                cblist(dataEvent.getMessage());
//
//            }
//        });


    }

    public void quite_room() {
        JSONObject c= new JSONObject();
        try {
            c.put("appcode",appcode);
            c.put("roomid",roomid);
            c.put("username", username);
            c.put("to_username", username);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        pomeloClient.request("connector.gameHandler.quit_game_room", c);
    }

    public void quite_room(String user) {
        JSONObject c= new JSONObject();
        try {
            c.put("appcode",appcode);
            c.put("roomid",roomid);
            c.put("username", username);
            c.put("to_username", user);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        pomeloClient.request("connector.gameHandler.quit_game_room", c);
    }

    public void change_room_status(String appcode, String roomid, String status){
        JSONObject c= new JSONObject();
        try {
            c.put("appcode",appcode);
            c.put("roomid",roomid);
            c.put("status", status);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        pomeloClient.request("connector.gameHandler.change_room_status", c);
    }

    public void syncGameRoom(String appcode, String username, String roomid, String route, String json){
        try {
            JSONObject c= new JSONObject(json);
            c.put("appcode",appcode);
            c.put("roomid",roomid);
            c.put("route",route);
            c.put("username",username);
            c.put("from",username);
            pomeloClient.request("connector.gameHandler.send_game_info_in_room", c);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void upload_point(String appcode, String roomid, String point){
        try {
            JSONObject c= new JSONObject();
            c.put("appcode",appcode);
            c.put("roomid",roomid);
            c.put("from",username);
            c.put("point",point);
            pomeloClient.request("connector.gameHandler.upload_point", c);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void upload_end_point(String appcode, String roomid, String point){
        try {
            JSONObject c= new JSONObject();
            c.put("appcode",appcode);
            c.put("roomid",roomid);
            c.put("from",username);
            c.put("point",point);
            pomeloClient.request("connector.gameHandler.upload_end_point", c);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void game_round_start_by_room(String appcode, String roomid){
        try {
            JSONObject c= new JSONObject();
            c.put("appcode",appcode);
            c.put("roomid",roomid);
            pomeloClient.request("connector.gameHandler.game_round_start_by_room", c);
            change_room_status(appcode, roomid, "playing");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void game_round_end_by_room(String appcode, String roomid){
        try {
            JSONObject c= new JSONObject();
            c.put("appcode",appcode);
            c.put("roomid",roomid);
            pomeloClient.request("connector.gameHandler.game_round_end_by_room", c);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void get_game_info_by_appcode_username(String appcode){
        try {
            JSONObject c= new JSONObject();
            c.put("appcode",appcode);
            pomeloClient.request("connector.gameHandler.get_game_info_by_appcode_username", c, new DataCallBack() {
                @Override
                public void responseData(JSONObject message) {
                    if(!is_Room_sync()){
                        return;
                    }
                    JSONObject result = (JSONObject) doResult(message);
                    if(result!=null){
                        iGameSync.syncGameInfo(result);
                    }

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void save_game_info_by_appcode_username(String appcode, String info){
        try {
            JSONObject c= new JSONObject();
            c.put("appcode",appcode);
            c.put("info",info);
            pomeloClient.request("connector.gameHandler.save_game_info_by_appcode_username", c, new DataCallBack() {
                @Override
                public void responseData(JSONObject message) {
                    if(!is_Room_sync()){
                        return;
                    }
                    JSONObject result = (JSONObject) doResult(message);
                    if(result!=null){
                        iGameSync.syncGameInfo(result);
                    }

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void used_prop_by_appcode_username(String appcode, final String prop_flag, final int count){
        try {
            JSONObject c= new JSONObject();
            c.put("appcode",appcode);
            c.put("prop_flag",prop_flag);
            c.put("count",count);
            pomeloClient.request("connector.gameHandler.used_prop_by_appcode_username", c, new DataCallBack() {
                @Override
                public void responseData(JSONObject message) {
                    if(!is_Room_sync()){
                        return;
                    }
                    JSONObject result = (JSONObject) doResult(message);
                    if(result!=null){
//                        iGameSync.syncG
                        iGameSync.syncUsedProperty(prop_flag,count,true);
                    }else{
                        iGameSync.syncUsedProperty(prop_flag,count,false);
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void get_user_prop_by_appcode_username(String appcode){
        try {
            JSONObject c= new JSONObject();
            c.put("appcode",appcode);
            pomeloClient.request("connector.gameHandler.get_user_prop_by_appcode_username", c, new DataCallBack() {
                @Override
                public void responseData(JSONObject message) {
                    if(!is_Room_sync()){
                        return;
                    }
                    JSONArray result = (JSONArray) doResult(message);

                    Map<String,Integer> map = new HashMap<String, Integer>();
                    JSONObject obj = null;
                    if(result!=null){
                        for(int i=0;i<result.length();i++){
                            try {
                                obj = result.getJSONObject(i);
                                map.put(obj.optString("flag"), obj.optInt("count"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    iGameSync.syncUserPropertyInfo(map);

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void add_prop_by_appcode_username(String appcode, final String prop_flag, final int count){
        try {
            JSONObject c= new JSONObject();
            c.put("appcode",appcode);
            c.put("prop_flag",prop_flag);
            c.put("count",count);
            pomeloClient.request("connector.gameHandler.add_prop_by_appcode_username", c, new DataCallBack() {
                @Override
                public void responseData(JSONObject message) {
                    if(!is_Room_sync()){
                        return;
                    }
                    JSONObject result = (JSONObject) doResult(message);

                    if(result==null){
                        iGameSync.syncResultAddPropertyInfo(prop_flag, count, false);
                    }
                    iGameSync.syncResultAddPropertyInfo(prop_flag, count, true);

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void clean_point(String appcode, String roomid){
        try {
            JSONObject c= new JSONObject();
            c.put("appcode",appcode);
            c.put("roomid",roomid);
            pomeloClient.request("connector.gameHandler.clean_point", c);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
