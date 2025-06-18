package intelligentcurtainwall.modelingcommunication.manager;

import intelligentcurtainwall.modelingcommunication.entity.BatchTaskStatus;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TaskManager {

    private final Map<String, BatchTaskStatus> taskMap = new ConcurrentHashMap<>();

    public void initTask(String taskId, int total) {
        BatchTaskStatus status = new BatchTaskStatus();
        status.setTotal(total);
        status.setProcessed(0);
        status.setCompleted(false);
        taskMap.put(taskId, status);
    }

    public BatchTaskStatus getStatus(String taskId) {
        return taskMap.get(taskId);
    }

    public void markProcessed(String taskId, boolean success, String path) {
        BatchTaskStatus status = taskMap.get(taskId);
        if (status == null) return;
        status.setProcessed(status.getProcessed() + 1);
        if (!success) status.getFailedPaths().add(path);
        if (status.getProcessed() == status.getTotal()) {
            status.setCompleted(true);
        }
    }

}
