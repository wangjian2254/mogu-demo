package com.liyu.pluginframe.util;

import android.app.Activity;
import android.content.Context;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainDataTool {
//	private final static String RESULTDATA="resultData";
	private final static String DATA="data";

	public final static String START_GAME="start_game";
	public final static String END_GAME="end_game";
	public final static String UPLOAD_GAME_POINT="upload_game_point";
	public final static String UPLOAD_END_GAME_POINT="upload_end_game_point";
	public final static String PUSH_GAME_DATA="push_game_data";
	public final static String PUSH_PROPERTY_DATA="push_property_data";
	public final static String SEND_CHAT="send_chat";
	public final static String LET_USERNAME_OUT="let_username_out";
	public final static String IN_ROOM="in_room";
	public final static String OUT_ROOM="out_room";
	public final static String QUERY_MY_PROP="query_my_prop";
    private static Map<String, Long> api_timeout= new HashMap<String, Long>();
    private static DateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//默认应用中的时间格式化

    private static boolean debug=false;


    private static String appcode=null;
    private static String appcode_dt_id=null;
    private static String appname=null;
    private static String static_point=null;
    private static long timeline=0;


    private static int version=0;
    private static String roomid =null;
//    private static String gameroomurl =null;
    private static Map<String,Integer> userlist=new HashMap<String, Integer>();
    private static Map<String,String> nicklist=new HashMap<String, String>();

//    private static Map<Integer,String> shunxulist=new HashMap<Integer, String>();
    private static Map<Integer,Map<String,Integer>> weizhilist=new HashMap<Integer, Map<String, Integer>>();

    private static Context con;



    public static void setIGameSync(IGameSync iGameSync1){
        GameService.getInstance().setiGameSync(iGameSync1);

    }

    /**
     * 获取用户信息
     * @return
     */
    public static UserInfo getUserInfo() {
        return userInfo;
    }

    private static UserInfo userInfo=new UserInfo();

    public static enum Model{NORMAL,DAILY,WEEKLY,MONTHLY,YEAR};



    /**
     * 获取用户信息、包括房间信息
     * @param   mainactivity 句柄
     */
    public static void getUserInfoJSON(Activity mainactivity){
        con = mainactivity.getApplicationContext();
        appcode = con.getPackageName();
        appname =  con.getPackageManager().getApplicationLabel(con.getApplicationInfo()).toString();

        JSONObject j=null;
        String result = mainactivity.getIntent().getExtras().getString("user");
        if(result!=null){
            try {
                j=new JSONObject(result);
            } catch (JSONException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

        }
        if(j!=null){
            userInfo.setUsername(j.optString("username", null));
            userInfo.setNickname(j.optString("nickname", "蘑菇玩家"));
            userInfo.setJid(j.optString("jid", null));
            userInfo.setNewroom(j.optBoolean("newroom", false));
            userInfo.setChallengr(j.optBoolean("challenger", false));
            appcode_dt_id = j.optString("appcode_dt_id");
            appcode = j.optString("appcode", null);
            appname = "测试游戏";
            debug = j.optBoolean("debug",false);
            debug = false;
            if(j.has("version")&&j.optInt("version",1)==2){
                version = j.optInt("version");
                roomid = j.optString("spaceid","");
//

            }

//            GameService.getInstance().clear_users();

            JSONObject json = new JSONObject();
            try {
                json.put("appcode", appcode);
                json.put("appcode_dt_id", appcode_dt_id);
                json.put("username", userInfo.getUsername());
                json.put("roomid", roomid);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            GameService.getInstance().init_room_PomeloClient(j.optString("game_host"), j.optInt("game_point"), j.optString("token"), json);





        }

    }





    private static int syncGameRoom(String route, String json){
        long currentTimeMillis = System.currentTimeMillis();
        if(api_timeout.containsKey(route)) {
            if (currentTimeMillis - api_timeout.get(route) < 100) {
                return 1;
            }
        }
        api_timeout.put(route, currentTimeMillis);
        try {
            GameService.getInstance().syncGameRoom(appcode, userInfo.getUsername(), roomid, route, json);
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 2;
        }
    }

    /**
     * 开始游戏
     * @return 0成功，1间隔太短，2失败
     */
    public static void startGame(){
        GameService.getInstance().game_round_start_by_room(appcode, roomid);
//        return syncGameRoom(START_GAME, "{}");
    }

    /**
     * 上传积分，进度……游戏数据
     * @param point
     *  @return 0成功，1间隔太短，2失败
     */
    public static void uploadGamePoint(String point){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("point", point);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        GameService.getInstance().upload_point(appcode, roomid, point);
//        return syncGameRoom(UPLOAD_GAME_POINT, jsonObject.toString());
    }

    /**
     * 上传最终结果数据
     * @param point
     *  @return 0成功，1间隔太短，2失败
     */
    public static int uploadEndGamePoint(String point){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("point", point);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        GameService.getInstance().upload_end_point(appcode, roomid, point);
        return 0;
    }

    /**
     * 向用户发送自定义游戏数据
     * @param jsonObject
     * @param user
     *  @return 0成功，1间隔太短，2失败
     */
    public static int pushGameDataToUser(JSONObject jsonObject, String user){
        try {
            JSONArray users = new JSONArray();
            users.put(user);
            jsonObject.put("users", users);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return syncGameRoom(PUSH_GAME_DATA, jsonObject.toString());
    }

    /**
     * 向多个用户发送自定义游戏数据
     * @param jsonObject
     * @param users
     *  @return 0成功，1间隔太短，2失败
     */
    public static int pushGameDataToUsers(JSONObject jsonObject, String[] users){
        try {
            JSONArray jsonArray = new JSONArray();
            for(String user:users){
                jsonArray.put(user);
            }
            jsonObject.put("users", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return syncGameRoom(PUSH_GAME_DATA, jsonObject.toString());
    }

    /**
     * 向所有用户发送自定义游戏数据
     * @param jsonObject
     *  @return 0成功，1间隔太短，2失败
     */
    public static int pushGameDataToAllUser(JSONObject jsonObject){

        return syncGameRoom(PUSH_GAME_DATA, jsonObject.toString());
    }

    /**
     * 向用户发送道具数据
     * @param property_flag
     * @param user
     *  @return 0成功，1间隔太短，2失败
     */
    public static int pushPropertyDataToUser(String property_flag, String user){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("property_flag", property_flag);
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(user);
            jsonObject.put("users", jsonArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return syncGameRoom(PUSH_PROPERTY_DATA, jsonObject.toString());
    }

    /**
     * 向多个用户发送道具数据
     * @param property_flag
     * @param users
     *  @return 0成功，1间隔太短，2失败
     */
    public static int pushPropertyDataToUsers(String property_flag, String[] users){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("property_flag", property_flag);
            JSONArray jsonArray = new JSONArray();
            for(String user:users){
                jsonArray.put(user);
            }
            jsonObject.put("users", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return syncGameRoom(PUSH_PROPERTY_DATA, jsonObject.toString());
    }

    /**
     * 向所有用户发送道具数据
     * @param property_flag
     *  @return 0成功，1间隔太短，2失败
     */
    public static int pushPropertyDataToAllUser(String property_flag){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("property_flag", property_flag);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return syncGameRoom(PUSH_PROPERTY_DATA, jsonObject.toString());
    }

    /**
     * 发送私聊内容
     * @param msg
     * @param user
     *  @return 0成功，1间隔太短，2失败
     */
    public static int sendChatToUser(String msg, String user){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("msg", msg);
            jsonObject.put("user", user);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return syncGameRoom(SEND_CHAT, jsonObject.toString());
    }

    /**
     *发送谈话内容
     * @param msg 谈话内容
     *  @return 0成功，1间隔太短，2失败
     */
    public static int sendChatToAllUser(String msg){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("msg", msg);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return syncGameRoom(SEND_CHAT, jsonObject.toString());
    }

    /**
     *踢人
     * @param user 被踢的人
     *
     */
    public static void quiteRoom(String user){
        GameService.getInstance().quite_room(user);
    }

    /**
     * 获取当前用户列表，username列表
     * @return 用户列表
     */
    public static ArrayList<String> getMembers(){
        return GameService.getInstance().get_users();
    }


    /**
     * 退出当前房间，如果在游戏，则退出游戏
     */
    public static void quiteRoom(){
        GameService.getInstance().quite_room();
    }

    /**
     * 判断自己是否房主
     * @return
     */
    public static boolean isMaster(){
        if(GameService.getInstance().get_users().size()==0){

            return false;
        }
        if(GameService.getInstance().get_users().get(0).equals(getUserInfo().getUsername())){

            return true;

        }
        return false;
    }

    /**
     * 发出请求查询自己的道具
     * @return
     */
    public static void query_my_prop(){
        GameService.getInstance().get_user_prop_by_appcode_username(appcode);
    }

    /**
     * 增加道具
     * @return
     */
    public static void add_game_prop(String prop_flag, int num){
         GameService.getInstance().add_prop_by_appcode_username(appcode, prop_flag, num);
    }
    /**
     * 消耗道具
     * @return
     */
    public static void used_game_prop(String prop_flag, int num){
        GameService.getInstance().used_prop_by_appcode_username(appcode, prop_flag, num);
    }



    public static void used_game_prop(String prop_flag, int num, final String[] to, final GameService.UsedGameResult usedGameResult){
        GameService.getInstance().used_prop_by_appcode_username(appcode, prop_flag, num, to, new GameService.UsedGameResult() {
            @Override
            public void used_game_prop_result(boolean result, String prop_flag2, String msg) {
                if(result){
                    pushPropertyDataToUsers(prop_flag2,to);
                }
                usedGameResult.used_game_prop_result(result, prop_flag2, msg);
            }
        });
    }

    /**
     * 保存我的游戏信息
     * @param json
     */
    public static void save_game_info_by_appcode_username(String json){
        GameService.getInstance().save_game_info_by_appcode_username(appcode, json);
    }

    /**
     * 获取我的游戏信息
     */
    public static void get_game_info_by_appcode_username(){
        GameService.getInstance().get_game_info_by_appcode_username(appcode);
    }

    /**
     * 清空房间内积分信息
     */
    public static void clean_point(){
        GameService.getInstance().clean_point(appcode, roomid);
    }

}
