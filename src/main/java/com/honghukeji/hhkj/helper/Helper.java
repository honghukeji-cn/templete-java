package com.honghukeji.hhkj.helper;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.honghukeji.hhkj.dao.AdminDAO;
import com.honghukeji.hhkj.dao.AdminLogDAO;
import com.honghukeji.hhkj.dao.UploadSetDAO;
import com.honghukeji.hhkj.entity.AdminLog;
import com.honghukeji.hhkj.entity.UploadSet;
import com.honghukeji.hhkj.objs.AliOssEntity;
import com.honghukeji.hhkj.objs.QiniuEntity;
import com.honghukeji.hhkj.objs.TxCosEntity;
import lombok.SneakyThrows;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Helper {
    public static String JWTKEY="honghukeji";
    public static int NotLoginCode=999;
    public static int NoAuthCode=888;
    public static int ErrorCode=0;
    public static String getJWTToken(String userId,String password){
        String token="";
        token= JWT.create().withAudience(userId)
                .sign(Algorithm.HMAC256(password));
        return token;
    }

    /**
     * 将json数组以特定符号连接起来
     * @param separator
     * @param jarr
     * @return
     */
    public static String implodeJsonArray(String separator,JSONArray jarr){
        String res="";
        for (int i = 0; i < jarr.size(); i++) {
            if(i<jarr.size()-1){
                res += jarr.getString(i)+separator;
            }else{
                res += jarr.getString(i);
            }

        }
        return res;
    }

    /**
     * 获取系统当前时间，字符串类型
     * @return String
     */
    public static String getStrDate(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //设置为东八区
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        Date newDate = new Date();
        String dateStr = sdf.format(newDate);
        return dateStr;
    }
    public static String getStrDate(String pattern){
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        //设置为东八区
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        Date newDate = new Date();
        String dateStr = sdf.format(newDate);
        return dateStr;
    }
    /**
     * 获取系统当前时间Date类型
     * @return Date
     */
    public static Date getDaDate(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //设置为东八区
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        Date date = new Date();
        String dateStr = sdf.format(date);
        //将字符串转成时间
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date newDate=null;
        try {
            newDate = df.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return newDate;
    }
    /**
     * 获取某个日期多少秒后的日期
     * @param date
     * @param seconds
     * @return
     */
    public static Date addSeconds(Date date, int seconds) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.SECOND, seconds);

        return calendar.getTime();

    }
    /**
     * 验证密码正确性
     * @param checkPass 输入的密码
     * @param hashPassword hash密文
     * @return
     */
    public static boolean hashpasswordVerify(String checkPass,String hashPassword){
        BCrypt.Result res = BCrypt.verifyer().verify(checkPass.toCharArray(), hashPassword);
        return res.verified;
    }
    //生成密码
    public static String createHashPassword(String password){
        String hash = BCrypt.with(BCrypt.Version.VERSION_2Y).hashToString(10, password.toCharArray());
        return hash;
    }
    //判断jsonarray中是否包含某个值
    public static boolean InJsonArray(Object a,JSONArray jarr){
        for(Object o : jarr){
            if(o.equals(a)){
                return true;
            }
        }
        return false;
    }

    public static String  httpRequest(String url, HttpMethod method, MultiValueMap<String ,String > params){
        RestTemplate client=new RestTemplate();
        HttpHeaders headers=new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<MultiValueMap<String, String>>(params, headers);
        //  执行HTTP请求
        ResponseEntity<String> response = client.exchange(url, method, requestEntity, String.class);
        return response.getBody();
    }
    public static String getIp(HttpServletRequest request){
        // 优先取 X-Real-IP
        String ip = request.getHeader("X-Real-IP");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)){
            ip = request.getHeader("x-forwarded-for");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)){
            ip = request.getRemoteAddr();
            if ("0:0:0:0:0:0:0:1".equals(ip))
            {
                ip = "unknown";
            }
        }
        if ("unknown".equalsIgnoreCase(ip)){
            ip = "unknown";
            return ip;
        }
        int index = ip.indexOf(',');
        if (index >= 0){
            ip = ip.substring(0, index);
        }
        return ip;
    }

    /**
     * 校验请求参数是否有误
     * @param result
     */
    public static void checkError(BindingResult result){
        if(result.hasErrors()){
            for(ObjectError error : result.getAllErrors()){
                throw  new RuntimeException(error.getDefaultMessage());
            }
        }
    }
    public static String getRadomStr(int length){
        String str="9876543210123456789";
        StringBuilder sb=new StringBuilder(4);
        for(int i=0;i<length;i++)
        {
            char ch=str.charAt(new Random().nextInt(str.length()));
            sb.append(ch);
        }
        return sb.toString();
    }
    /**
     * 字符串是否包含中文
     *
     * @param str 待校验字符串
     * @return true 包含中文字符 false 不包含中文字符
     */
    public static boolean isContainChinese(String str) {
        Pattern p = Pattern.compile("[\u4E00-\u9FA5|\\！|\\，|\\。|\\（|\\）|\\《|\\》|\\“|\\”|\\？|\\：|\\；|\\【|\\】]");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }

    /**
     * 是否是英文字符串
     * @param charaString
     * @return
     */
    public static boolean isEnglishStr(String charaString){
        return charaString.matches("^[a-zA-Z]*");
    }

    /**
     * 发送post请求
     * @param url  路径
     * @param jsonObject  参数(json类型)
     * @return
     * @throws ParseException
     * @throws IOException
     */
    public static String send(String url, JSONObject jsonObject) throws ParseException, IOException {
        String body = "";

        //创建httpclient对象
        CloseableHttpClient client = HttpClients.createDefault();
        //创建post方式请求对象
        HttpPost httpPost = new HttpPost(url);

        //装填参数
        StringEntity s = new StringEntity(jsonObject.toString(), "utf-8");
        s.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,
                "application/json"));
        //设置参数到请求对象中
        httpPost.setEntity(s);
        System.out.println("请求地址："+url);
//        System.out.println("请求参数："+nvps.toString());

        //设置header信息
        //指定报文头【Content-type】、【User-Agent】
//        httpPost.setHeader("Content-type", "application/x-www-form-urlencoded");
        httpPost.setHeader("Content-type", "application/json");
        httpPost.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

        //执行请求操作，并拿到结果（同步阻塞）
        CloseableHttpResponse response = client.execute(httpPost);
        //获取结果实体
        org.apache.http.HttpEntity entity = response.getEntity();
        if (entity != null) {
            //按指定编码转换结果实体为String类型
            body = EntityUtils.toString(entity, "UTF-8");
        }
        EntityUtils.consume(entity);
        //释放链接
        response.close();
        return body;
    }
    public static boolean isSpecialChar(String str) {
        String regEx = "[_`~!@#$%^&*()+=|{}':;'\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]|\n|\r|\t";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.find();
    }



    public static String convertMD5(String inStr)
    {
        char[] a = inStr.toCharArray();
        for (int i = 0; i < a.length; i++){
            a[i] = (char) (a[i] ^ 't');
        }
        String s = new String(a);
        return s;
    }

    public static Integer dateDifference(Date sDate,Date eDate) {
        LocalDate date1 =sDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        LocalDate date2 = eDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        long daysBetween = ChronoUnit.DAYS.between(date1, date2);
        return Math.toIntExact(daysBetween);
    }

    public static Double DoubleToFixed(Double aDouble, String pattern) {
        DecimalFormat df = new DecimalFormat(pattern);
        return Double.valueOf(df.format(aDouble));
    }

    public static File multipartFileToFile(MultipartFile multipartFile) throws IOException {
        // 获取文件名
        String fileName = multipartFile.getOriginalFilename();
        // 创建临时文件
        Path path = Paths.get(fileName);
        File tempFile = path.toFile();
        // 将MultipartFile内容写入临时文件
        multipartFile.transferTo(tempFile);
        return tempFile;
    }
}
