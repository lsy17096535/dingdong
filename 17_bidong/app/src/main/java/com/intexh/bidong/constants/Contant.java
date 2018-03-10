package com.intexh.bidong.constants;


public class Contant {
    /**
     * 水印本地路径，文件必须为rgba格式的PNG图片
     */
    public static  String WATER_MARK_PATH ="assets://Qupai/watermark/qupai-logo.png";


    /**
     * 默认最大时长
     */
    public static int DEFAULT_DURATION_MAX_LIMIT = 8;
    public static int DEFAULT_DURATION_LIMIT_MIN = 2;

    /**
     * 默认时长
     */
    public static  int DEFAULT_DURATION_LIMIT = 10;

    /**
     * 默认码率
     */
    public static int DEFAULT_BITRATE = 800 * 1024;
    /**
     * 默认CRF参数
     */
    public static int DEFAULT_VIDEO_RATE_CRF = 6;

    /**
     * VideoPreset
     */
    public static String DEFAULT_VIDEO_Preset = "faster";
    /**
     * VideoLevel
     */
    public static int DEFAULT_VIDEO_LEVEL = 30;

    /**
     * VideoTune
     */
    public static String DEFAULT_VIDEO_TUNE = "zerolatency";
    /**
     * movflags_KEY
     */
    public static String DEFAULT_VIDEO_MOV_FLAGS_KEY = "movflags";

    /**
     * movflags_VALUE
     */
    public static String DEFAULT_VIDEO_MOV_FLAGS_VALUE = "+faststart";

    /**
     * 水印位置1为右上，2为右下
     */
    public static int WATER_MARK_POSITION = 2;

    public static String appkey = "204bae311588acf";
    public static String appsecret = "b43129d04e8c4c68b98ea5feafb78915";
    public static String space = "yiqilai"; //存储目录 建议使用uid cid之类的信息
}
