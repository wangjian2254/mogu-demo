package com.example.myapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import com.liyu.pluginframe.util.IGameSync;
import com.liyu.pluginframe.util.MainDataTool;
import com.liyu.pluginframe.util.UserInfo;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MyActivity extends Activity {
    private static String username = "aaa1111";
    private static String appcode = "com.mogu.game.tetris";
    private static String appcode_dt_id = "com.mogu.game.tetris";
    private static String roomid = "10199ae8-9ec2-4c70-b75c-ac81f2039252";
    private static String token = "gamedeveloptoken";

    private boolean inited=true;
    private boolean testing=true;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        JSONObject j = new JSONObject();
        try {
            j.put("username", username);
            j.put("nickname", "昵称");
            j.put("jid", username+"@openfire.mmggoomm.com");
            j.put("debug", true);

            j.put("version", 2);
            j.put("spaceid", roomid);
            j.put("roomid", roomid);
            j.put("token", token);
            j.put("appcode_dt_id", appcode_dt_id);
            j.put("appcode", appcode);
            j.put("author", username) ;
            JSONObject ju=null;
            JSONObject jn=null;
            ArrayList<JSONObject> ja=new ArrayList<JSONObject>();
            ArrayList<JSONObject> jna=new ArrayList<JSONObject>();
            ju=new JSONObject();
            jn=new JSONObject();
            ju.put("username", username);
            ju.put("head", 1);

            jn.put("username", username);
            jn.put("nickname", "昵称");
            ja.add(ju);
            jna.add(jn);
            j.put("userlist", ja);
            j.put("nicklist", jna);
            j.put("game_host", "192.168.1.18");
//            j.put("game_host", "mogu-gameserver.mmggoomm.com");
            j.put("game_point", 3001);
            j.put("token", token);

        } catch (JSONException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }



        MainDataTool.setIGameSync(new IGameSync() {
            @Override
            public void syncStartGame() {

            }

            @Override
            public void syncGamePoints(String user, String point) {

            }

            @Override
            public void syncEndGamePoints(String user, String point) {

            }

            @Override
            public void syncEndGame() {

            }

            @Override
            public void syncEndGame(String from) {

            }

            @Override
            public void syncGameData(String from, JSONObject jsonObject) {

            }

            @Override
            public void syncGameData(String from, String[] to, JSONObject jsonObject) {

            }

            @Override
            public void syncGamePropertyInfo(String from, String property_flag) {

            }

            @Override
            public void syncGamePropertyInfo(String from, String[] to, String property_flag) {

            }

            @Override
            public void syncChat(String from, String msg) {

            }

            @Override
            public void syncChat(String from, String to, String msg) {

            }

            @Override
            public void syncMemberChange(String user, boolean in, JSONObject userinfo) {

            }


            @Override
            public void syncQuiteRoomByUser(String user) {

            }

            @Override
            public void syncRoomMembers(List<String> usernames, Map<String, UserInfo> usermap) {

            }

            @Override
            public void syncInRoom() {

            }

            @Override
            public void syncQuiteRoom() {

            }

            @Override
            public void syncGameInfo(JSONObject json) {

            }

            @Override
            public void syncUserPropertyInfo(Map<String, Integer> propinfo) {

            }

            @Override
            public void syncResultAddPropertyInfo(String prop_flag, int num, boolean success) {

            }

            @Override
            public void syncUsedProperty(String prop_flag, int num, boolean success) {

            }
        });

        MainDataTool.getUserInfoJSON(j.toString());

    }

    public void sendData(View btn){
        switch (btn.getId()){
            case R.id.btn_startGame:
                MainDataTool.startGame();
                break;
            case R.id.btn_uploadGamePoint:
                MainDataTool.uploadGamePoint("10");
                break;
            case R.id.btn_uploadEndGamePoint:
                MainDataTool.uploadEndGamePoint("100");
                break;
            case R.id.btn_pushGameDataToUser:
                JSONObject gamedata = new JSONObject();
                try {
                    gamedata.put("gamedata", 1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                MainDataTool.pushGameDataToUser(gamedata, username);
                break;
            case R.id.btn_pushGameDataToUsers:
                JSONObject gamedata1 = new JSONObject();
                try {
                    gamedata1.put("gamedata", 1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String[] u1 = {username};
                MainDataTool.pushGameDataToUsers(gamedata1, u1);
                break;
            case R.id.btn_pushGameDataToAllUser:
                JSONObject gamedata2 = new JSONObject();
                try {
                    gamedata2.put("gamedata", 2);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                MainDataTool.pushGameDataToAllUser(gamedata2);
                break;
            case R.id.btn_pushPropertyDataToUser:
                MainDataTool.pushPropertyDataToUser("proper1", username);
                break;
            case R.id.btn_pushPropertyDataToUsers:
                String[] u2 = {username};
                MainDataTool.pushPropertyDataToUsers("proper2", u2);
                break;
            case R.id.btn_pushPropertyDataToAllUser:
                MainDataTool.pushPropertyDataToAllUser("proper2");
                break;
            case R.id.btn_sendChatToUser:
                MainDataTool.sendChatToUser("some chat", username);
                break;
            case R.id.btn_sendChatToAllUser:
                MainDataTool.sendChatToAllUser("some chat");
                break;
            case R.id.btn_quiteRoom:
                MainDataTool.quiteRoom(username);
                break;
            case R.id.btn_getMembers:
                MainDataTool.getMembers();
                break;
            case R.id.btn_quiteRoom2:
                MainDataTool.quiteRoom();
                break;
            case R.id.btn_isMaster:
                MainDataTool.isMaster();
                break;
            case R.id.btn_query_my_prop:
                MainDataTool.query_my_prop();
                break;
            case R.id.btn_add_game_prop:
                MainDataTool.add_game_prop("proper1", 3);
                break;
            case R.id.btn_used_game_prop:
                MainDataTool.add_game_prop("proper1", 1);
                break;

        }
    }
}
