package intelligentcurtainwall.modelingcommunication.service;

import intelligentcurtainwall.modelingcommunication.entity.InteractionLog;
import intelligentcurtainwall.modelingcommunication.dto.InteractionLogDto;
import intelligentcurtainwall.modelingcommunication.repository.InteractionLogRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Service
public class InteractionLogService implements IInteractionLogService {

    private final InteractionLogRepository interactionLogRepository;

    @Autowired
    public InteractionLogService(InteractionLogRepository interactionLogRepository) {
        this.interactionLogRepository = interactionLogRepository;
    }

    @Override
    public InteractionLog add(InteractionLogDto interactionLog) {

        InteractionLog interactionLogPojo = new InteractionLog();

        BeanUtils.copyProperties(interactionLog, interactionLogPojo);

        // 获取当前时间
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Shanghai")); // 获取北京时间

        // 格式化为 "yyyy-MM-dd HH:mm:ss" 格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedNow = now.format(formatter);

        Timestamp timestamp = Timestamp.valueOf(formattedNow); // 转换为 Timestamp

        interactionLogPojo.setCreateTime(timestamp);

        return interactionLogRepository.save(interactionLogPojo);
    }
}
