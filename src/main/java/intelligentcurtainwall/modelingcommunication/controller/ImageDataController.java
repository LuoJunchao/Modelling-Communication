package intelligentcurtainwall.modelingcommunication.controller;

import intelligentcurtainwall.modelingcommunication.dto.*;
import intelligentcurtainwall.modelingcommunication.entity.ImageData;
import intelligentcurtainwall.modelingcommunication.service.IImageDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/imageData")
public class ImageDataController {

    private final IImageDataService imageDataService;

    // 添加一张图片（含坐标提取、异常判断、面匹配等）
    @PostMapping
    public ResponseMessage<ImageData> add(@RequestBody ImageDataDto imageData){
        ImageData imageDataNew = imageDataService.add(imageData);
        return ResponseMessage.success(imageDataNew);
    }

    // 获取图片 URL
    @GetMapping("/{imageId}/url")
    public ResponseMessage<String> getImageUrl(@PathVariable Integer imageId) {
        return ResponseMessage.success(imageDataService.getImagePath(imageId));
    }

    // 获取所有有异常的图片
    @GetMapping("/all-abnormal-images")
    public ResponseMessage<List<ImageDataDto>> getAllCoordinates(){
        List<ImageDataDto> imageDataDtos = imageDataService.getAllAbnormalImages();
        return ResponseMessage.success(imageDataDtos);
    }

    // 获取点击位置的图片
    @GetMapping("/closest")
    public ResponseMessage<ImageData> closest(
            @RequestParam BigDecimal clickX,
            @RequestParam BigDecimal clickY,
            @RequestParam BigDecimal clickZ){
        ImageData imageDataCloset = imageDataService.getClosestImageData(clickX, clickY, clickZ);
        return ResponseMessage.success(imageDataCloset);
    }

    // 删除图片
    @DeleteMapping("/{imageId}")
    public ResponseMessage<ImageData> delete(@PathVariable Integer imageId){
        imageDataService.deleteImageData(imageId);
        return ResponseMessage.success(null);
    }

    // 获取某张图片的异常详情
    @GetMapping("/{imageId}/abnormalDetails")
    public ResponseMessage<List<AbnormalDetailDto>> getAbnormalDetails(@PathVariable Integer imageId) {
        return ResponseMessage.success(imageDataService.getAbnormalDetailsByImageId(imageId));
    }

}
