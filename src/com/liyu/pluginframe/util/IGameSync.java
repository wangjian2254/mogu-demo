package com.liyu.pluginframe.util;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: WangJian
 * Date: 13-11-1
 * Time: 上午9:43
 * To change this template use File | Settings | File Templates.
 */
public interface IGameSync {
    /**
     * 接收到开始游戏
     * by:王健 at:2015-5-19
     */
    public void syncStartGame();

    /**
     * 接收游戏进度、积分、当前得分
     * by:王健 at:2015-5-19
     * @param user 玩家
     * @param point  得分
     */
    public void syncGamePoints(String user, String point);

    /**
     * 将游戏的最终得分，以Map的形式，同步
     * by:王健 at:2015-5-19
     * @param user  玩家
     * @param point  得分
     */
    public void syncEndGamePoints(String user, String point);

    /**
     * 服务器端发来游戏结束的信号
     * by:王健 at:2015-5-19
     */
    public void syncEndGame();

    /**
     * 服务器端发来游戏结束的信号,某人结束
     * by:王健 at:2015-5-20
     * @param from 结束游戏的人
     */
    public void syncEndGame(String from);

    /**
     * 自定义游戏数据的同步，同步给本客户端的
     * by:王健 at:2015-5-19
     * @param from   发起人
     * @param jsonObject 自定义信息
     */
    public void syncGameData(String from, JSONObject jsonObject);

    /**
     * 自定义游戏数据的同步，同步给一部分人
     * by:王健 at:2015-5-19
     * @param from 发起人
     * @param to  接收人数组
     * @param jsonObject 自定义数据
     */
    public void syncGameData(String from, String[] to, JSONObject jsonObject);

    /**
     * 同步游戏道具的使用，本客户端作为受体
     * by:王健 at:2015-5-19
     * @param from 发起人
     * @param property_flag 道具标示
     */
    public void syncGamePropertyInfo(String from, String property_flag);

    /**
     * 同步游戏道具的使用，针对一批用户的道具
     * by:王健 at:2015-5-19
     * @param from 发起人
     * @param to 接收人数组
     * @param property_flag 道具标示
     */
    public void syncGamePropertyInfo(String from, String[] to, String property_flag);

    /**
     * 向所有人发送聊天内容
     * by:王健 at:2015-5-20
     * @param from 发起人
     * @param msg 聊天内容
     */
    public void syncChat(String from, String msg);


    /**
     * 向某人发送私聊
     * by:王健 at:2015-5-20
     * @param from 发起人
     * @param to 接收人数组
     * @param msg 聊天内容
     */
    public void syncChat(String from, String to, String msg);

    /**
     * 人员变动
     * by:王健 at:2015-5-20
     * @param user 用户id
     * @param in  true为加入，false为推出
     * @param userinfo json格式用户信息，如果是用户离开房间，参数为空
     */
    public void syncMemberChange(String user, boolean in, JSONObject userinfo);

//    /**
//     * 被提出房间
//     * @param user 用户id，被用户 user剔除房间
//     */
//    public void syncQuiteRoomByUser(String user);

    /**
     * 获取当前房间内用户信息
     * by:王健 at:2015-5-20
     * @param usernames
     * @param usermap
     */
    public void syncRoomMembers(List<String> usernames, Map<String, UserInfo> usermap);

    /**
     * 成功进入房间的回调函数
     * by:王健 at:2015-5-20
     */
    public void syncInRoom();

    /**
     * 掉线
     *
     */
    public void syncQuiteRoom();


    /**
     * 获取用户的游戏信息，游戏自行定义，满足json格式即可
     * by:王健 at:2015-5-20
     * @param json
     */
    public void syncGameInfo(JSONObject json);

    /**
     *
     * 用户在本游戏中各个道具数量
     * by:王健 at:2015-5-20
     * @param propinfo
     */
    public void syncUserPropertyInfo(Map<String, Integer> propinfo);

    /**
     * 用户在本游戏中增加的道具数量
     * by:王健 at:2015-5-20
     * @param prop_flag   道具标示
     * @param num         道具变动数量
     * @param success     道具变动结果，true：变动成功；false：变动失败
     */
    public void syncResultAddPropertyInfo(String prop_flag, int num, boolean success);

    /**
     *
     * 用户使用道具记录
     * by:王健 at:2015-5-20
     * @param prop_flag  道具标示
     * @param num        道具使用数量
     * @param success    道具使用成功或失败
     */
    public void syncUsedProperty(String prop_flag, int num, boolean success);


    /**
     * 错误信息
     * by:王健 at:2015-08-09
     * @param message        错误信息
     * @param status_code    错误码
     */
    public void syncError(String message, int status_code);

}
