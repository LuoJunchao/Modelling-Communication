package intelligentcurtainwall.modelingcommunication.controller;

import intelligentcurtainwall.modelingcommunication.dto.AbnormalUploadRequestDto;
import intelligentcurtainwall.modelingcommunication.dto.ResponseMessage;
import intelligentcurtainwall.modelingcommunication.entity.BatchTaskStatus;
import intelligentcurtainwall.modelingcommunication.manager.TaskManager;
import intelligentcurtainwall.modelingcommunication.service.IImageDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/task")
public class ImageTaskController {

    private final IImageDataService imageDataService;

    private final TaskManager taskManager;

    // 批量上传图片
    @PostMapping("/submit")
    public ResponseEntity<String> submitTask(@RequestBody List<String> imagePaths) {
        String taskId = UUID.randomUUID().toString();
        taskManager.initTask(taskId, imagePaths.size());
        imageDataService.batchAddImagesAsync(imagePaths, taskId);
        return ResponseEntity.ok(taskId);
    }

    // 批量标注图片异常
    @PostMapping("/mark-abnormals")
    public ResponseEntity<ResponseMessage<String>> markAbnormal(@RequestBody AbnormalUploadRequestDto request) {
        if (CollectionUtils.isEmpty(request.getImagePaths())) {
            return ResponseEntity.badRequest()
                    .body(ResponseMessage.fail(HttpStatus.BAD_REQUEST.value(), "图片路径列表不能为空"));
        }
        if (!StringUtils.hasText(request.getAbnormalType())) {
            return ResponseEntity.badRequest()
                    .body(ResponseMessage.fail(HttpStatus.BAD_REQUEST.value(), "异常类型不能为空"));
        }
        // 业务逻辑处理
        try {
            String taskId = UUID.randomUUID().toString();
            taskManager.initTask(taskId, request.getImagePaths().size());
            imageDataService.markImagesAbnormal(request, taskId);
            return ResponseEntity.ok(ResponseMessage.success(taskId));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ResponseMessage.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(), "处理失败"));
        }
    }

    @GetMapping("/status/{taskId}")
    public ResponseEntity<BatchTaskStatus> getStatus(@PathVariable("taskId") String taskId) {
        BatchTaskStatus status = taskManager.getStatus(taskId);
        if (status == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(status);
    }

}
