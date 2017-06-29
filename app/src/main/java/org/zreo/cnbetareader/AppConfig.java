package org.zreo.cnbetareader;

import java.util.regex.Pattern;

/**
 * Created by zqh on 2015/7/30  09:01.
 * Email:zqhkey@163.com
 */
public class AppConfig {
public  static final String APP_NET_DEBUG="APP_NET_DEBUG";
    public static final String BASE_URL = "http://www.cnbeta.com";
    public static final String NEWS_LIST_URL = BASE_URL + "/more";
    public static final String ARTICLE_URL = BASE_URL + "/articles/%s.htm";
    public static final String TOPIC_NEWS_LIST = BASE_URL+"/topics/more";
    public static final String COMMENT_URL = BASE_URL + "/cmt";
    public static final String COMMENT_VIEW = BASE_URL + "/comment";
    public static final Pattern STANDRA_PATTERN = Pattern.compile("cnBeta\\.COM_中文业界资讯站");
    public static final Pattern SN_PATTERN = Pattern.compile("SN:\"(.{5})\"");
    public static final Pattern HOT_COMMENT_PATTERN = Pattern.compile("来自<strong>(.*)</strong>的(.*)对新闻:<a href=\"/articles/(.*).htm\" target=\"_blank\">(.*)</a>的评论");
    public static final String SECOND_VIEW = BASE_URL + "/captcha.htm";
}
