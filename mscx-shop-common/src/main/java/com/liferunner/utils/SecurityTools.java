package com.liferunner.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * SecurityTools for : 信息安全处理工具类
 *
 * @author <a href="mailto:magicianisaac@gmail.com">Isaac.Zhang | 若初</a>
 * @since 2019/11/16
 */
public class SecurityTools {

    /**
     * 最大替换隐藏字符数
     */
    private static final int MAX_HIDDEN_SIZE = 6;
    /**
     * 用于替换字符的特殊标志
     */
    private static final String SYMBOL = "*";

    /**
     * 通用脱敏 可用于： 用户名 手机号 邮箱 地址等
     *
     * @param value
     * @return
     */
    public static String hiddenPartString4SecurityDisplay(String value) {
        if (null == value || "".equals(value)) {
            return value;
        }

        int len = value.length();
        int pamaone = len / 2;
        int pamatwo = pamaone - 1;
        int pamathree = len % 2;
        StringBuilder stringBuilder = new StringBuilder();
        if (len <= 2) {
            if (pamathree == 1) {
                return SYMBOL;
            }
            stringBuilder.append(SYMBOL);
            stringBuilder.append(value.charAt(len - 1));
        } else {
            if (pamatwo <= 0) {
                stringBuilder.append(value.substring(0, 1));
                stringBuilder.append(SYMBOL);
                stringBuilder.append(value.substring(len - 1, len));

            } else if (pamatwo >= MAX_HIDDEN_SIZE / 2 && MAX_HIDDEN_SIZE + 1 != len) {
                int pamafive = (len - MAX_HIDDEN_SIZE) / 2;
                stringBuilder.append(value.substring(0, pamafive));
                for (int i = 0; i < MAX_HIDDEN_SIZE; i++) {
                    stringBuilder.append(SYMBOL);
                }
                if ((pamathree == 0 && MAX_HIDDEN_SIZE / 2 == 0) || (pamathree != 0 && MAX_HIDDEN_SIZE % 2 != 0)) {
                    stringBuilder.append(value.substring(len - pamafive, len));
                } else {
                    stringBuilder.append(value.substring(len - (pamafive + 1), len));
                }
            } else {
                int pamafour = len - 2;
                stringBuilder.append(value.substring(0, 1));
                for (int i = 0; i < pamafour; i++) {
                    stringBuilder.append(SYMBOL);
                }
                stringBuilder.append(value.substring(len - 1, len));
            }
        }
        return stringBuilder.toString();
    }

    /**
     * 检查手机号码
     *
     * @param mobile
     * @return
     */
    public static boolean checkMobile(String mobile) {
        String regex = "^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(17[013678])|(18[0,5-9]))\\d{8}$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(mobile);
        boolean isMatch = m.matches();
        return isMatch;
    }

    /**
     * 检查email
     *
     * @param email
     * @return
     */
    public static boolean checkEmail(String email) {
        boolean isMatch = true;
        if (!email.matches("[\\w\\.\\-]+@([\\w\\-]+\\.)+[\\w\\-]+")) {
            isMatch = false;
        }
        return isMatch;
    }
}
