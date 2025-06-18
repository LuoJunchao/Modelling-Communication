package intelligentcurtainwall.modelingcommunication.controller;

import intelligentcurtainwall.modelingcommunication.entity.InteractionLog;
import intelligentcurtainwall.modelingcommunication.dto.InteractionLogDto;
import intelligentcurtainwall.modelingcommunication.dto.ResponseMessage;
import intelligentcurtainwall.modelingcommunication.service.IInteractionLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/interactionLog")
public class InteractionLogController {

    @Autowired
    IInteractionLogService interactionLogService;

    //增加
    @PostMapping()
    public ResponseMessage<InteractionLog> add(@RequestBody InteractionLogDto interactionLog){
        InteractionLog interactionLogNew = interactionLogService.add(interactionLog);
        return ResponseMessage.success(interactionLogNew);
    }

}
