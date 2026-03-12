package com.middleware.org.service;

import com.middleware.org.model.FileMetadata;

import java.io.InputStream;

/**
 * 文件上传与预处理接口
 * 负责原始数据文件的接入与初步处理
 */
public interface IFilePreprocessService {

    /**
     * 上传文件
     * @param inputStream 文件流
     * @param fileName 文件名
     * @return 文件元数据
     */
    FileMetadata uploadFile(InputStream inputStream, String fileName);

    /**
     * 验证文件格式
     * @param filePath 文件路径
     * @return 是否有效
     */
    boolean validateFileFormat(String filePath);

    /**
     * 获取文件元数据
     * @param filePath 文件路径
     * @return 文件元数据
     */
    FileMetadata getFileMetadata(String filePath);

    /**
     * 预处理文件（编码检测、格式识别等）
     * @param filePath 文件路径
     * @return 预处理后的文件路径
     */
    String preprocessFile(String filePath);
}
