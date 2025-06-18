package intelligentcurtainwall.modelingcommunication.service;

import intelligentcurtainwall.modelingcommunication.entity.InteractionLog;
import intelligentcurtainwall.modelingcommunication.dto.InteractionLogDto;

public interface IInteractionLogService {

    //新增日志
    InteractionLog add(InteractionLogDto interactionLog);

}
