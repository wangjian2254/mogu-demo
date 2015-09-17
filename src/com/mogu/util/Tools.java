package com.mogu.util;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by fanjunwei on 15/8/9.
 */
public class Tools {
    public static String MD5(String s) {
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            byte[] strTemp = s.getBytes();
            MessageDigest mdTemp = MessageDigest.getInstance("MD5");
            mdTemp.update(strTemp);
            byte[] md = mdTemp.digest();
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            return null;
        }
    }

    public static void sign(JSONObject parms, String token, String[] keys) {

        try {
            parms.put("timeline", System.currentTimeMillis() / 1000);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        List<String> keys_list = new ArrayList<String>();
        Collections.addAll(keys_list, keys);

        keys_list.add("timeline");
        keys_list.add("token");
        Collections.sort(keys_list);
        StringBuilder buffer = new StringBuilder();
        int length = keys_list.size();
        for (int i = 0; i < length; i++) {
            String key = keys_list.get(i);
            try {
                String value = null;
                if (key.equals("token")) {
                    value = token;
                } else {
                    value = parms.get(key).toString();
                }
                buffer.append(key);
                buffer.append("=");
                buffer.append(value);
                if (i != length - 1) {
                    buffer.append("&");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        String str = buffer.toString();
        System.out.println(str);
        try {
            parms.put("sign", MD5(str));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
