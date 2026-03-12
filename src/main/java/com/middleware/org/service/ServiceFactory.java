package com.middleware.org.service;

import com.middleware.org.service.impl.*;

/**
 * 服务工厂
 * 负责创建和管理所有服务实例
 */
public class ServiceFactory {

    private static ServiceFactory instance;

    private final ILogService logService;
    private final IFilePreprocessService filePreprocessService;
    private final IDataParseService dataParseService;
    private final IDataCleanService dataCleanService;
    private final IDataOutputService dataOutputService;
    private final ITaskFlowControlService taskFlowControlService;

    private ServiceFactory() {
        // 初始化所有服务
        this.logService = new LogServiceImpl();
        this.filePreprocessService = new FilePreprocessServiceImpl();
        this.dataParseService = new DataParseServiceImpl();
        this.dataCleanService = new DataCleanServiceImpl();
        this.dataOutputService = new DataOutputServiceImpl();
        this.taskFlowControlService = new TaskFlowControlServiceImpl(
                filePreprocessService,
                dataParseService,
                dataCleanService,
                dataOutputService,
                logService
        );
    }

    public static synchronized ServiceFactory getInstance() {
        if (instance == null) {
            instance = new ServiceFactory();
        }
        return instance;
    }

    public ILogService getLogService() {
        return logService;
    }

    public IFilePreprocessService getFilePreprocessService() {
        return filePreprocessService;
    }

    public IDataParseService getDataParseService() {
        return dataParseService;
    }

    public IDataCleanService getDataCleanService() {
        return dataCleanService;
    }

    public IDataOutputService getDataOutputService() {
        return dataOutputService;
    }

    public ITaskFlowControlService getTaskFlowControlService() {
        return taskFlowControlService;
    }
}
