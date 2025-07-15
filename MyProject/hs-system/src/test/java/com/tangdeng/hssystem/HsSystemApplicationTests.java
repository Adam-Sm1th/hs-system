package com.tangdeng.hssystem;

import com.tangdeng.hssystem.utils.AliOssUtil;
import com.tangdeng.hssystem.utils.JWTUtils;
import com.tangdeng.hssystem.utils.MailUtils;
import com.tangdeng.hssystem.utils.RedisUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;

@SpringBootTest
class HsSystemApplicationTests {

    @Test
    void contextLoads() {

    }

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    MailUtils mailUtils;

    @Autowired
    AliOssUtil aliOssUtil;

    @Test
    void testRedis() {
        HashMap map = new HashMap();
        map.put("ww", "11");
        System.out.println(JWTUtils.getToken(map));
    }

    @Test
    void testOss() throws IOException {
        // 图片文件路径
        File file = new File("C:\\Users\\Adam\\Desktop\\1.jpg");

        // 读取文件并转换为字节数组
        byte[] imageBytes = Files.readAllBytes(file.toPath());

        // 输出字节数组长度来验证
        System.out.println("图片字节流大小: " + imageBytes.length);
        String upload = aliOssUtil.upload(imageBytes, "1.jpg");
        System.out.println(upload);
    }

}
