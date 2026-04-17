package ayaselab.log.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 请求调用链记录信息
 */
@Data
public class TraceStepInfo {

    protected LocalDateTime stepTime;

    protected String stepMessage;
}
