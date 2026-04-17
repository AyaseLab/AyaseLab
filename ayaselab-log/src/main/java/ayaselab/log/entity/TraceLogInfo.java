package ayaselab.log.entity;

import lombok.Data;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 链式日志信息，用于记录单次请求全部信息，串联调用链路
 */
@Data
public class TraceLogInfo extends BaseLogInfo {

    protected LocalDateTime beginTime;

    protected LocalDateTime endTime;

    protected Duration elapsed;

    protected List<TraceLogInfo> traceSteps;

    /**
     * 记录链路信息
     * @param message
     */
    public void recStep(String message){
        TraceStepInfo traceStepInfo = new TraceStepInfo();
        traceStepInfo.setStepMessage(message);
        traceStepInfo.setStepTime(LocalDateTime.now());
    }

    /**
     * 日志开始记录
     */
    public void startRec() {
        beginTime = LocalDateTime.now();
    }

    /**
     * 日志结束记录
     */
    public void stopRec() {
        endTime = LocalDateTime.now();
        elapsed = Duration.between(beginTime, endTime);
    }
}
