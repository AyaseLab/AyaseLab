package ayaselab.log.entity;

import ayaselab.log.entity.enums.EnumLogType;
import lombok.Data;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * 迷你日志，用于记录单独message
 */
@Data
public class MiniLogInfo extends BaseLogInfo{

    protected String message;// 日志记录信息

    public void init(String appId, String appName) {

    }
}
