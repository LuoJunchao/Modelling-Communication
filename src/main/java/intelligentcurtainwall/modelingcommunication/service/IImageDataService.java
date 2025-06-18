package intelligentcurtainwall.modelingcommunication.service;

import intelligentcurtainwall.modelingcommunication.dto.AbnormalDetailDto;
import intelligentcurtainwall.modelingcommunication.dto.AbnormalUploadRequestDto;
import intelligentcurtainwall.modelingcommunication.entity.ImageData;
import intelligentcurtainwall.modelingcommunication.dto.ImageDataDto;

import java.math.BigDecimal;
import java.util.List;

public interface IImageDataService {

    //新增图片
    ImageData add(ImageDataDto imageData);

    //查找距离点击坐标最近的图片
    ImageData getClosestImageData(BigDecimal clickX, BigDecimal clickY, BigDecimal clickZ);

    //通过Id删除图片
    void deleteImageData(Integer imageId);

    //查询全部图片坐标
    List<ImageDataDto> getAllAbnormalImages();

    // 通过Id获取图片路径
    String getImagePath(Integer imageId);

    List<AbnormalDetailDto> getAbnormalDetailsByImageId(Integer imageId);

    void batchAddImagesAsync(List<String> imagePaths, String taskId);

    void markImagesAbnormal(AbnormalUploadRequestDto request, String taskId);
}
