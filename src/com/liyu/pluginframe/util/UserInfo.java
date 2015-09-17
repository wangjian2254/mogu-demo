package com.liyu.pluginframe.util;

/**
 * Created with IntelliJ IDEA.
 * User: WangJian
 * Date: 13-11-1
 * Time: 上午9:43
 * To change this template use File | Settings | File Templates.
 */
public class UserInfo {
    private String username=null;
    private String nickname="新用户";
    private String jid=null;
    private boolean newroom=false;
    private boolean challengr=false;


    private int head;
    private String rank;
    private int point;




    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getJid() {
        return jid;
    }

    public void setJid(String jid) {
        this.jid = jid;
    }

    public boolean isNewroom() {
        return newroom;
    }

    public void setNewroom(boolean newroom) {
        this.newroom = newroom;
    }

    public boolean isChallengr() {
        return challengr;
    }

    public void setChallengr(boolean challengr) {
        this.challengr = challengr;
    }


    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }


    public int getHead() {
        return head;
    }

    public void setHead(int head) {
        this.head = head;
    }
}
