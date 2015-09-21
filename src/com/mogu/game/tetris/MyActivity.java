package com.mogu.game.tetris;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.liyu.pluginframe.util.GameService;
import com.liyu.pluginframe.util.IGameSync;
import com.liyu.pluginframe.util.MainDataTool;
import com.liyu.pluginframe.util.UserInfo;
import com.mogu.game.tetris.R;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MyActivity extends Activity {
//    private static String username = "aaa1111";
    private static String appcode = "com.mogu.game.tetris";
    private static String appcode_dt_id = "com.mogu.game.tetris";
    private static String roomid = "10199ae8-9ec2-4c70-b75c-ac81f2039252";
    private static String token = "gamedeveloptoken";

    private boolean inited=true;
    private boolean testing=true;

    private TextView txtlog=null;
    private UserInfo userInfo;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        txtlog = (TextView)findViewById(R.id.txtlog);

//        JSONObject j = new JSONObject();
//        try {
//            j.put("username", username);
//            j.put("nickname", "昵称");
//            j.put("jid", username+"@openfire.mmggoomm.com");
//            j.put("debug", true);
//
//            j.put("version", 2);
//            j.put("spaceid", roomid);
//            j.put("roomid", roomid);
//            j.put("token", token);
//            j.put("appcode_dt_id", appcode_dt_id);
//            j.put("appcode", appcode);
//            j.put("author", username) ;
//            JSONObject ju=null;
//            JSONObject jn=null;
//            ArrayList<JSONObject> ja=new ArrayList<JSONObject>();
//            ArrayList<JSONObject> jna=new ArrayList<JSONObject>();
//            ju=new JSONObject();
//            jn=new JSONObject();
//            ju.put("username", username);
//            ju.put("head", 1);
//
//            jn.put("username", username);
//            jn.put("nickname", "昵称");
//            ja.add(ju);
//            jna.add(jn);
//            j.put("userlist", ja);
//            j.put("nicklist", jna);
////            j.put("game_host", "192.168.1.18");
//            j.put("game_host", "mogu-gameserver.mmggoomm.com");
//            j.put("game_point", 3001);
//            j.put("token", token);
//
//        } catch (JSONException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        }



        MainDataTool.setIGameSync(new IGameSync() {
            @Override
            public void syncStartGame() {
                txtlog.post(new Runnable() {
                    @Override
                    public void run() {
                        txtlog.append("\n");
                        txtlog.append("开始游戏");
                    }
                });

            }

            @Override
            public void syncGamePoints(final String user, final String point) {
                txtlog.post(new Runnable() {
                    @Override
                    public void run() {
                        txtlog.append("\n");
                        txtlog.append(user+"：发送积分:"+point);
                    }
                });

            }

            @Override
            public void syncEndGamePoints(final String user, final String point) {
                txtlog.post(new Runnable() {
                    @Override
                    public void run() {
                        txtlog.append("\n");
                        txtlog.append(user+"：发送结束积分:"+point);
                    }
                });

            }

            @Override
            public void syncEndGame() {
                txtlog.post(new Runnable() {
                    @Override
                    public void run() {
                        txtlog.append("\n");
                        txtlog.append("退出游戏");
                    }
                });

            }

            @Override
            public void syncEndGame(final String from) {
                txtlog.post(new Runnable() {
                    @Override
                    public void run() {
                        txtlog.append("\n");
                        txtlog.append(from+"：把我踢出游戏");
                    }
                });

            }

            @Override
            public void syncGameData(final String from, final JSONObject jsonObject) {
                txtlog.post(new Runnable() {
                    @Override
                    public void run() {
                        txtlog.append("\n");
                        txtlog.append(from+"：发送数据："+jsonObject.toString());
                    }
                });

            }

            @Override
            public void syncGameData(final String from, final String[] to, final JSONObject jsonObject) {
                txtlog.post(new Runnable() {
                    @Override
                    public void run() {
                        for(String tuser : to){
                            txtlog.append("\n");
                            txtlog.append(from+"：向："+tuser+"发送数据："+jsonObject.toString());
                        }
                    }
                });

            }

            @Override
            public void syncGamePropertyInfo(final String from, final String property_flag) {
                txtlog.post(new Runnable() {
                    @Override
                    public void run() {
                        txtlog.append("\n");
                        txtlog.append(from+"：使用道具："+property_flag);
                    }
                });

            }

            @Override
            public void syncGamePropertyInfo(final String from, final String[] to, final String property_flag) {
                txtlog.post(new Runnable() {
                    @Override
                    public void run() {
                        for(String tuser : to){
                            txtlog.append("\n");
                            txtlog.append(from+"：向："+tuser+"使用道具："+property_flag);
                        }
                    }
                });

            }

            @Override
            public void syncChat(final String from, final String msg) {
                txtlog.post(new Runnable() {
                    @Override
                    public void run() {
                        txtlog.append("\n");
                        txtlog.append(from+"：发送信息："+msg);
                    }
                });

            }

            @Override
            public void syncChat(final String from, final String to, final String msg) {
                txtlog.post(new Runnable() {
                    @Override
                    public void run() {
                        txtlog.append("\n");
                        txtlog.append(from+"：向:"+to+"发送信息："+msg);
                    }
                });

            }

            @Override
            public void syncMemberChange(final String user, final boolean in, final JSONObject userinfo) {
                txtlog.post(new Runnable() {
                    @Override
                    public void run() {
                        if(in){
                            txtlog.append("\n");
                            txtlog.append(user+"：加入房间:"+ userinfo.toString());
                        }else{
                            txtlog.append("\n");
                            txtlog.append(user+"：退出房间:");
                        }
                    }
                });


            }


            @Override
            public void syncQuiteRoomByUser(final String user) {
                txtlog.post(new Runnable() {
                    @Override
                    public void run() {
                        txtlog.append("\n");
                        txtlog.append(user+"：退出房间");
                    }
                });

            }

            @Override
            public void syncRoomMembers(final List<String> usernames, final Map<String, UserInfo> usermap) {
                txtlog.post(new Runnable() {
                    @Override
                    public void run() {
                        for(String tuser : usernames){

                            txtlog.append("\n");
                            txtlog.append(tuser + "：个人信息：" + usermap.get(tuser).toString());
                        }
                    }
                });

            }

            @Override
            public void syncInRoom() {
                txtlog.post(new Runnable() {
                    @Override
                    public void run() {
                        txtlog.append("\n");
                        txtlog.append("进入房间");
                    }
                });

            }

            @Override
            public void syncQuiteRoom() {
                txtlog.post(new Runnable() {
                    @Override
                    public void run() {
                        txtlog.append("\n");
                        txtlog.append("：退出房间");
                    }
                });

            }

            @Override
            public void syncGameInfo(final JSONObject json) {
                txtlog.post(new Runnable() {
                    @Override
                    public void run() {
                        txtlog.append("\n");
                        txtlog.append("同步游戏数据："+json.toString());
                    }
                });

            }

            @Override
            public void syncUserPropertyInfo(final Map<String, Integer> propinfo) {
                txtlog.post(new Runnable() {
                    @Override
                    public void run() {
                        for(String tuser : propinfo.keySet()){
                            txtlog.append("\n");
                            txtlog.append(tuser + "：道具数量：" + propinfo.get(tuser).toString());
                        }
                    }
                });

            }

            @Override
            public void syncResultAddPropertyInfo(final String prop_flag, final int num, final boolean success) {
                txtlog.post(new Runnable() {
                    @Override
                    public void run() {
                        txtlog.append("\n");
                        txtlog.append(prop_flag + "：道具添加："+num +"效果："+ success);
                    }
                });

            }

            @Override
            public void syncUsedProperty(final String prop_flag, final int num, final boolean success) {
                txtlog.post(new Runnable() {
                    @Override
                    public void run() {
                        txtlog.append("\n");
                        txtlog.append(prop_flag + "：道具使用："+num +"效果："+ success);
                    }
                });

            }
        });


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
                MainDataTool.pushGameDataToUser(gamedata, userInfo.getUsername());
                break;
            case R.id.btn_pushGameDataToUsers:
                JSONObject gamedata1 = new JSONObject();
                try {
                    gamedata1.put("gamedata", 1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String[] u1 = {userInfo.getUsername()};
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
                MainDataTool.pushPropertyDataToUser("proper1", userInfo.getUsername());
                break;
            case R.id.btn_pushPropertyDataToUsers:
                String[] u2 = {userInfo.getUsername()};
                MainDataTool.pushPropertyDataToUsers("proper2", u2);
                break;
            case R.id.btn_pushPropertyDataToAllUser:
                MainDataTool.pushPropertyDataToAllUser("proper2");
                break;
            case R.id.btn_sendChatToUser:
                MainDataTool.sendChatToUser("some chat", userInfo.getUsername());
                break;
            case R.id.btn_sendChatToAllUser:
                MainDataTool.sendChatToAllUser("some chat");
                break;
            case R.id.btn_quiteRoom:
                MainDataTool.quiteRoom(userInfo.getUsername());
                break;
            case R.id.btn_getMembers:
                List<String> list = MainDataTool.getMembers();
                for(String tuser : list){
                    txtlog.append("\n");
                    txtlog.append(tuser + "：在房间");
                }
                break;
            case R.id.btn_quiteRoom2:
                MainDataTool.quiteRoom();
                break;
            case R.id.btn_isMaster:
                MainDataTool.isMaster();
                txtlog.append("\n");
                if(MainDataTool.isMaster()){
                    txtlog.append("我是房主");
                }else{
                    txtlog.append("我不是房主");
                }

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

    public void onResume(){
        super.onResume();
        MainDataTool.getUserInfoJSON(this);

        userInfo = MainDataTool.getUserInfo();
    }

    public void onPause(){
        super.onPause();
        GameService.getInstance().quite_dt();
    }
}
