package com.tangdeng.hssystem.controller;


import com.tangdeng.hssystem.pojo.vo.ImageVO;
import com.tangdeng.hssystem.result.Result;
import com.tangdeng.hssystem.utils.AliOssUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;

@RestController
@RequestMapping("/oss")
public class OssContorller {
    @Autowired
    AliOssUtil aliOssUtil;

    @PostMapping("/upload/{userId}")
    public Result upload(@PathVariable String userId, @RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return Result.error();
        }
        // 读取文件并转换为字节数组
        byte[] imageBytes = file.getBytes();

        // 输出字节数组长度来验证
        System.out.println("图片字节流大小: " + imageBytes.length);
        String upload = aliOssUtil.upload(imageBytes, userId.toString() +".jpg");
        System.out.println(upload);
        ImageVO imageVO = new ImageVO();
        imageVO.setImageUrl(upload);
       return Result.success(imageVO);

        // 处理文件上传（如保存到服务器、云存储等）
//        try {
//            // 例如保存文件到本地
//            String filename = file.getOriginalFilename();
//            file.transferTo(new File("/path/to/upload/directory/" + filename));
//
//            // 返回成功结果
//            return ResponseEntity.ok(new Result("上传成功"));
//        } catch (Exception e) {
//            return ResponseEntity.status(500).body(new Result("上传失败：" + e.getMessage()));
//        }
    }

}
