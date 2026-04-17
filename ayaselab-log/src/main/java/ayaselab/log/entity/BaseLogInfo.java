package ayaselab.log.entity;

import ayaselab.log.entity.enums.EnumLogLevelType;
import ayaselab.log.entity.enums.EnumLogType;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 日志基础信息
 */
@Data
public class BaseLogInfo {

    protected String appId;// AppID

    protected String appName;// App名称

    protected EnumLogType logType;// 日志类型：迷你日志、链式日志、异常日志

    protected EnumLogLevelType logLevel;// 日志level

    protected LocalDateTime logTime;// 日志记录时间

}
