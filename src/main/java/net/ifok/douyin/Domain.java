package net.ifok.douyin;

import java.io.File;
import java.io.IOException;

import static net.ifok.douyin.Analyze.analysis;
import static net.ifok.douyin.DownVideo.httpDownload;

public class Domain {

    public static final String SAVE_ADDRESS = "/Users/zhouhailin/Downloads/temp.mp4";

    public static void main(String[] args) throws IOException {

        String douyinUrl = " https://v.douyin.com/dRCJYrX/";
        String mp4Url = analysis(douyinUrl);
        System.out.println("解析后的无水印视频地址：\n" + mp4Url);
        boolean isdown = httpDownload(mp4Url, SAVE_ADDRESS);//路径不确定地址参数可用./temp.mp4暂时替代 视频则下载到当前目录

        if (isdown) {
            System.out.println("下载成功");
        } else {
            System.out.println("下载失败");
        }

    }

}
