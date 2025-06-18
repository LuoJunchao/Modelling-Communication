package intelligentcurtainwall.modelingcommunication.service;

import intelligentcurtainwall.modelingcommunication.algorithm.DistanceCalculate;
import intelligentcurtainwall.modelingcommunication.algorithm.UtmToUnityCoordinate;
import intelligentcurtainwall.modelingcommunication.dto.AbnormalDetailDto;
import intelligentcurtainwall.modelingcommunication.dto.AbnormalUploadRequestDto;
import intelligentcurtainwall.modelingcommunication.entity.ImageAbnormalDetail;
import intelligentcurtainwall.modelingcommunication.entity.ImageData;
import intelligentcurtainwall.modelingcommunication.dto.ImageDataDto;
import intelligentcurtainwall.modelingcommunication.manager.TaskManager;
import intelligentcurtainwall.modelingcommunication.repository.AbnormalTypeRepository;
import intelligentcurtainwall.modelingcommunication.repository.ImageAbnormalDetailRepository;
import intelligentcurtainwall.modelingcommunication.repository.ImageDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class ImageDataService implements IImageDataService {

    private final TaskManager taskManager;

    private final ImageDataRepository imageDataRepository;
    private final ImageAbnormalDetailRepository imageAbnormalDetailRepository;
    private final AbnormalTypeRepository abnormalTypeRepository;

    private final SurfaceCoordinateService surfaceCoordinateService;

    /**
     * 添加或更新图片信息
     * 包括：DTO 转换、坐标提取、坐标转换、墙面判断、保存图片
     *
     * @param imageDataDto 图片数据传输对象
     * @return 保存后的图片实体
     */
    @Override
    public ImageData add(ImageDataDto imageDataDto) {

        // 1. Dto 转换为 Entity
        ImageData imageData = new ImageData();
        BeanUtils.copyProperties(imageDataDto, imageData);

        // 2. 坐标提取与转换
        UtmToUnityCoordinate utmCoordinate = new UtmToUnityCoordinate();
        utmCoordinate.extractAndSetCoordinates(imageData);

        // 3. 坐标校验
        if (imageData.getCenterY().compareTo(BigDecimal.valueOf(-5)) <= 0) {
            throw new RuntimeException("异常坐标！");
        }

        // 4. 确定所在墙面
        surfaceCoordinateService.whichSurface(imageData);

        // 5. 查重：有相同坐标则更新，否则新增
        ImageData existingImage = imageDataRepository.findByCenterXAndCenterYAndCenterZ(
                imageData.getCenterX(),
                imageData.getCenterY(),
                imageData.getCenterZ()
        );

        if (existingImage != null) {
            existingImage.setImagePath(imageData.getImagePath());
            return imageDataRepository.save(existingImage);
        }

        return imageDataRepository.save(imageData);
    }

    /**
     * 根据点击坐标，返回最近的一张图片数据
     *
     * @param clickX 点击点X
     * @param clickY 点击点Y
     * @param clickZ 点击点Z
     * @return 最近的图片数据
     */
    @Override
    public ImageData getClosestImageData(BigDecimal clickX, BigDecimal clickY, BigDecimal clickZ) {
        List<ImageData> allImageData = (List<ImageData>) imageDataRepository.findAll();
        ImageData closestImageData = DistanceCalculate.findClosestImageData(allImageData, clickX, clickY, clickZ);
        if (closestImageData == null) {
            throw new RuntimeException("点击坐标匹配范围内无图片！");
        }
        return closestImageData;
    }


    /**
     * 根据图片ID获取图片的存储路径（OSS 存储路径）
     *
     * @param imageId 图片唯一标识符
     * @return 图片的路径（如果存在），否则返回 null
     */
    @Override
    public String getImagePath(Integer imageId) {
        Optional<ImageData> imageData = imageDataRepository.findById(imageId);
        return imageData.map(ImageData::getImagePath).orElse(null);
    }

    /**
     * 根据图片 ID 获取对应的所有异常信息详情
     *
     * @param imageId 图片ID
     * @return 该图片的异常详情列表
     */
    @Override
    public List<AbnormalDetailDto> getAbnormalDetailsByImageId(Integer imageId) {

        // 1. 获取图片实体
        Optional<ImageData> imageDataOptional = imageDataRepository.findById(imageId);
        if (imageDataOptional.isEmpty()) {
            throw new RuntimeException("图片不存在，ID：" + imageId);
        }
        ImageData imageData = imageDataOptional.get();

        // 2. 查询与该图片关联的所有异常详情
        List<ImageAbnormalDetail> abnormalDetails = imageAbnormalDetailRepository.findByImageData(imageData);

        // 3. 如果没有异常，返回空列表
        if (abnormalDetails.isEmpty()) {
            return Collections.emptyList();
        }

        // 4. 转换为 DTO 列表
        return abnormalDetails.stream()
                .map(detail -> {
                    AbnormalDetailDto abnormalDetailDto = new AbnormalDetailDto();
                    abnormalDetailDto.setTypeName(detail.getAbnormalType().getTypeName());
                    abnormalDetailDto.setDescription(detail.getDescription());
                    abnormalDetailDto.setDetectedTime(detail.getDetectedTime());
                    return abnormalDetailDto;
                })
                .collect(Collectors.toList());
    }


    @Override
    public void batchAddImagesAsync(List<String> imagePaths, String taskId) {
        for (String imagePath : imagePaths) {
            CompletableFuture.runAsync(() -> {
                boolean success = true;
                try {
                    ImageDataDto dto = new ImageDataDto();
                    dto.setImagePath(imagePath);
                    add(dto);
                } catch (Exception e) {
                    success = false;
                }
                taskManager.markProcessed(taskId, success, imagePath);
            });
        }
    }

    /**
     * 批量标记图片异常信息字段
     *
     * @param request 包括批量标记的图片 url 和异常信息类型
     */
    @Override
    public void markImagesAbnormal(AbnormalUploadRequestDto request, String taskId) {

        List<String> imagePaths = request.getImagePaths();

        // 异步处理每个图片
        for (String imagePath : imagePaths) {
            CompletableFuture.runAsync(() -> {
                boolean success = true;
                try {
                    // 1. 根据 OSS 存储路径获取该图片
                    ImageData imageData = new ImageData();
                    imageData.setImagePath(imagePath);

                    // 2. 坐标提取与转换
                    UtmToUnityCoordinate utmCoordinate = new UtmToUnityCoordinate();
                    utmCoordinate.extractAndSetCoordinates(imageData);

                    // 3. 确定所在墙面
                    surfaceCoordinateService.whichSurface(imageData);

                    // 4. 获取该墙面所有图片
                    List<ImageData> candidates = imageDataRepository.findBySurfaceCoordinate(imageData.getSurfaceCoordinate());

                    // 5. 获取该墙面最近的图片
                    ImageData closestImageData = DistanceCalculate.findClosestImageData(candidates, imageData.getCenterX(), imageData.getCenterY(), imageData.getCenterZ());

                    // 6. 标记异常图片
                    if (closestImageData != null) {
                        closestImageData.setHasAbnormal(true);
                        imageDataRepository.save(closestImageData);

                        ImageAbnormalDetail imageAbnormalDetail = new ImageAbnormalDetail();
                        imageAbnormalDetail.setImageData(closestImageData);
                        imageAbnormalDetail.setAbnormalType(abnormalTypeRepository.findByTypeName(request.getAbnormalType()));

                        imageAbnormalDetailRepository.save(imageAbnormalDetail);
                    }
                } catch (Exception e) {
                    success = false;
                }
                taskManager.markProcessed(taskId, success, imagePath);
            });
        }
    }

    /**
     * 根据图片ID删除图片数据
     *
     * @param imageId 图片ID
     */
    @Override
    public void deleteImageData(Integer imageId) {
        imageDataRepository.deleteById(imageId);
    }

    @Override
    public List<ImageDataDto> getAllAbnormalImages() {
        Iterable<ImageData> imageDataIterable = imageDataRepository.findAll();

        return StreamSupport.stream(imageDataIterable.spliterator(), false)
                .filter(imageData -> Boolean.TRUE.equals(imageData.getHasAbnormal()))
                .filter(imageData -> isInsideBuildingLShape(imageData.getCenterX(), imageData.getCenterZ()))
                .map(imageData -> {
                    ImageDataDto imageDataDto = new ImageDataDto();
                    imageDataDto.setImageId(imageData.getImageId());
                    imageDataDto.setImagePath(imageData.getImagePath());
                    imageDataDto.setSurfaceId(imageData.getSurfaceCoordinate().getSurfaceId());
                    imageDataDto.setCenterX(imageData.getCenterX());
                    imageDataDto.setCenterY(imageData.getCenterY());
                    imageDataDto.setCenterZ(imageData.getCenterZ());
                    imageDataDto.setHasAbnormal(imageData.getHasAbnormal());

                    // 重点：查询该图像的所有异常详情
                    List<ImageAbnormalDetail> details = imageAbnormalDetailRepository.findByImageData(imageData);
                    List<AbnormalDetailDto> detailDtos = details.stream()
                            .map(AbnormalDetailDto::convertAbnormalDetailToDto)
                            .collect(Collectors.toList());
                    imageDataDto.setAbnormalDetailList(detailDtos);

                    return imageDataDto;
                })
                .collect(Collectors.toList());
    }

    private boolean isInsideBuildingLShape(BigDecimal x, BigDecimal z) {
        BigDecimal tolerance = new BigDecimal("2");

        // 区域1：长条区域 (0 <= x <= 55, -52.51 <= z <= 0)，加容错后变为：
        // (-2 <= x <= 57, -54.51 <= z <= 2)
        BigDecimal xMin1 = BigDecimal.ZERO.subtract(tolerance);             // -2
        BigDecimal xMax1 = new BigDecimal("55").add(tolerance);         // 57
        BigDecimal zMin1 = new BigDecimal("-52.51").subtract(tolerance);// -54.51
        BigDecimal zMax1 = BigDecimal.ZERO.add(tolerance);                  // 2

        // 区域2：横向部分 (55 <= x <= 108.28, -100.5 <= z <= -52.51)，加容错后变为：
        // (53 <= x <= 110.28, -102.5 <= z <= -50.51)
        BigDecimal xMin2 = new BigDecimal("55").subtract(tolerance);          // 53
        BigDecimal xMax2 = new BigDecimal("108.28").add(tolerance);           // 110.28
        BigDecimal zMin2 = new BigDecimal("-100.5").subtract(tolerance);      // -102.5
        BigDecimal zMax2 = new BigDecimal("-52.51").add(tolerance);           // -50.51

        boolean inRect1 =
                x.compareTo(xMin1) >= 0 && x.compareTo(xMax1) <= 0 &&
                        z.compareTo(zMin1) >= 0 && z.compareTo(zMax1) <= 0;

        boolean inRect2 =
                x.compareTo(xMin2) >= 0 && x.compareTo(xMax2) <= 0 &&
                        z.compareTo(zMin2) >= 0 && z.compareTo(zMax2) <= 0;

        return inRect1 || inRect2;
    }


}

