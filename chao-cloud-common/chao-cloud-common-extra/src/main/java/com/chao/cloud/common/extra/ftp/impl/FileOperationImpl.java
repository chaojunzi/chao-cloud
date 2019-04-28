package com.chao.cloud.common.extra.ftp.impl;

import java.awt.Color;
import java.awt.Font;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import com.chao.cloud.common.exception.BusinessException;
import com.chao.cloud.common.extra.ftp.IFileOperation;
import com.chao.cloud.common.extra.ftp.annotation.FtpConfig;

import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;

public class FileOperationImpl implements IFileOperation {

    private FtpConfig ftpConfig;

    public FileOperationImpl(FtpConfig ftpConfig) {
        this.ftpConfig = ftpConfig;
    }

    @Override
    public String uploadImg(InputStream is, String fileName) throws Exception {
        // 操作流
        try (InputStream fileStream = is; ByteArrayOutputStream out = new ByteArrayOutputStream();) {
            String mimeType = FileUtil.getMimeType(fileName);
            if (!mimeType.startsWith("image")) {
                throw new BusinessException("无效的图片类型:" + mimeType);
            }
            // 加水印
            ImgUtil.pressText(is, out, //
                    ftpConfig.getLogo(), Color.WHITE, // 文字
                    new Font(Font.SERIF, Font.BOLD, 20), // 字体
                    0, // x坐标修正值。 默认在中间，偏移量相对于中间偏移
                    0, // y坐标修正值。 默认在中间，偏移量相对于中间偏移
                    0.7f// 透明度：alpha 必须是范围 [0.0, 1.0] 之内（包含边界值）的一个浮点数字
            );
            // 将outputStream转成inputstream
            byte[] array = out.toByteArray();
            ByteArrayInputStream stream = IoUtil.toStream(array);
            // 上传文件
            return this.uploadImg(stream, fileName);
        }
    }

    @Override
    public String uploadInputStream(InputStream is, String fileName) throws Exception {
        try (InputStream fileStream = is) {
            String name = this.genFileName(fileName);
            String path = this.genFilePath(ftpConfig.getPath());
            boolean upload = ftpConfig.getFtp().upload(path, name, fileStream);
            if (upload) {
                return path + name;
            }
        }
        throw new BusinessException("文件上传失败:" + fileName);
    }

    @Override
    public void downLoad(String filePath, OutputStream out) throws Exception {
        try (OutputStream os = out) {
            final String fileName = FileUtil.getName(filePath);
            final String path = StrUtil.removeSuffix(filePath, fileName);
            ftpConfig.getFtp().download(path, fileName, os);
        }
    }

    @Override
    public boolean delete(String filePath) {
        return ftpConfig.getFtp().delFile(filePath);
    }

}
