package net.ifok.douyin;

import com.jayway.jsonpath.JsonPath;
import net.ifok.common.net.HttpClientUtils;
import net.ifok.common.net.model.HttpResponse;
import net.ifok.common.string.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Analyze {

    static List<Map<String,String>> headers=new ArrayList<>();
    static {
        Map<String,String > hd=new HashMap<>();
        hd.put("referer","https://www.iesdouyin.com/");

        hd.put("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.90 Safari/537.36 Edg/89.0.774.54");
        headers.add(hd);

        hd.put("User-Agent","Mozilla/5.0 (Windows NT 7.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4389.90 Safari/537.36 Edg/87.0.774.54");
        headers.add(hd);

        hd.put("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:78.0) Gecko/20100101 Firefox/78.0 ");
        headers.add(hd);

        hd.put("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:78.0) Gecko/20100101 Firefox/78.0 ");
        headers.add(hd);

        hd.put("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:78.0) Gecko/20100101 Firefox/78.0 ");
        headers.add(hd);

    }
    static Pattern DOUYIN_SHARE_URL=Pattern.compile("(https://v.douyin.com/[^/]+)");
    final static String URL_PARAMS_SPLIT="?";
    final static Pattern VIDEO_PATTERN=Pattern.compile("/share/video/(\\d+)/?");
    /**
     * 随机UA
     * @return
     */
    public static Map<String,String> getRandomUAMap(){
        int size = headers.size();
        int index=(int)(Math.random()*size);
        Map<String, String> header = headers.get(index);
        return header;
    }
    /**
     * 抖音解析无水印
     * @param url
     * @return
     * @throws IOException
     */
    public static String analysis(String url) throws IOException {
        //解析地址
        Matcher matcher = DOUYIN_SHARE_URL.matcher(url);
        if (matcher.find()){
            url=matcher.group(1);
        }

        int size = headers.size();
        int index=(int)(Math.random()*size);
        Map<String, String> header = headers.get(index);

        String realUrl = HttpClientUtils.getRedirectURL(url,header);
        HashMap<String, String> hashMap = dealUrlParams(realUrl);
        String videoId=hashMap.get("videoId");
        String douyinDetailUrl="https://www.iesdouyin.com/web/api/v2/aweme/iteminfo/?item_ids="+videoId;
        HttpResponse response = HttpClientUtils.doGet(douyinDetailUrl, null, header);
        if (!Objects.equals(response.getCode(),200)){
            throw new RuntimeException("解析抖音失败，step 01");
        }
        String vid = JsonPath.parse(response.getResult()).read("$.item_list[0].video.vid", String.class);
        String realVideoUrl="https://aweme.snssdk.com/aweme/v1/play/?video_id="+vid+"&ratio=720p&line=0";
        String realVideo = HttpClientUtils.getRedirectURL(realVideoUrl,header);
        return realVideo;
    }


    /**
     * 获取url参数键值对
     * @param url
     * @return
     */
    public static HashMap<String,String> dealUrlParams(String url){
        if (StringUtils.isEmpty(url)){
            return new HashMap<>();
        }
        if (!url.contains(URL_PARAMS_SPLIT)){
            return new HashMap<>();
        }
        HashMap<String, String> map=new HashMap<>();
        String paramsStr=url.substring(url.indexOf(URL_PARAMS_SPLIT)+1);
        String[] arr = paramsStr.split("&");
        for (int i = 0; i <arr.length; i++) {
            String key=arr[i].substring(0, arr[i].indexOf("="));
            String value=arr[i].substring( arr[i].indexOf("=")+1);
            map.put(key, value);
        }
        //处理video id
        Matcher matcher = VIDEO_PATTERN.matcher(url);
        if (matcher.find()){
            String group = matcher.group(1);
            map.put("videoId",group);
        }else{
            map.put("videoId","");
        }
        return map;
    }

//    public static void main(String[] args) throws IOException {
//        String douyinUrl=" https://v.douyin.com/dRCJYrX/";
//        String mp4Url = analysis(douyinUrl);
//        System.out.println("解析后的无水印视频地址：\n"+mp4Url);
//    }


}
