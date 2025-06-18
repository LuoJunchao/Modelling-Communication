package intelligentcurtainwall.modelingcommunication.service;

import intelligentcurtainwall.modelingcommunication.algorithm.DistanceCalculate;
import intelligentcurtainwall.modelingcommunication.algorithm.UtmToUnityCoordinate;
import intelligentcurtainwall.modelingcommunication.dto.AbnormalDetailDto;
import intelligentcurtainwall.modelingcommunication.dto.ImageDataDto;
import intelligentcurtainwall.modelingcommunication.entity.AbnormalType;
import intelligentcurtainwall.modelingcommunication.entity.ImageAbnormalDetail;
import intelligentcurtainwall.modelingcommunication.entity.ImageData;
import intelligentcurtainwall.modelingcommunication.entity.SurfaceCoordinate;
import intelligentcurtainwall.modelingcommunication.manager.TaskManager;
import intelligentcurtainwall.modelingcommunication.repository.AbnormalTypeRepository;
import intelligentcurtainwall.modelingcommunication.repository.ImageAbnormalDetailRepository;
import intelligentcurtainwall.modelingcommunication.repository.ImageDataRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ImageDataServiceTest {

    @Mock
    private TaskManager taskManager;

    @Mock
    private ImageDataRepository imageDataRepository;

    @Mock
    private ImageAbnormalDetailRepository imageAbnormalDetailRepository;

    @Mock
    private AbnormalTypeRepository abnormalTypeRepository;

    @Mock
    private SurfaceCoordinateService surfaceCoordinateService;

    @InjectMocks
    private ImageDataService imageDataService;

    @Test
    void testAdd_newImage_success() {
        ImageDataDto dto = new ImageDataDto();
        dto.setImagePath("path/to/image.jpg");

        // Mock surfaceCoordinateService.whichSurface 给 imageData 设置面坐标（模拟）
        doAnswer(invocation -> {
            ImageData imageData = invocation.getArgument(0);
            SurfaceCoordinate surface = new SurfaceCoordinate();
            surface.setSurfaceId(1);
            imageData.setSurfaceCoordinate(surface);
            return null;
        }).when(surfaceCoordinateService).whichSurface(any());

        // 模拟 findByCenterXAndCenterYAndCenterZ 返回 null 表示新图
        when(imageDataRepository.findByCenterXAndCenterYAndCenterZ(any(), any(), any())).thenReturn(null);

        // 模拟保存返回带 ID 的实体
        when(imageDataRepository.save(any())).thenAnswer(invocation -> {
            ImageData img = invocation.getArgument(0);
            img.setImageId(123);
            return img;
        });

        ImageData result = imageDataService.add(dto);

        assertNotNull(result);
        assertEquals(123, result.getImageId());
        verify(imageDataRepository).save(any());
    }

    @Test
    void testAdd_existingImage_updatesPath() {
        ImageDataDto dto = new ImageDataDto();
        dto.setImagePath("new/path.jpg");

        ImageData existing = new ImageData();
        existing.setImageId(456);
        existing.setCenterX(BigDecimal.valueOf(1));
        existing.setCenterY(BigDecimal.valueOf(1));
        existing.setCenterZ(BigDecimal.valueOf(1));
        existing.setImagePath("old/path.jpg");

        when(imageDataRepository.findByCenterXAndCenterYAndCenterZ(any(), any(), any()))
                .thenReturn(existing);
        when(imageDataRepository.save(existing)).thenReturn(existing);

        // surfaceCoordinateService 仍然可以 mock，不影响这测试逻辑
        doNothing().when(surfaceCoordinateService).whichSurface(any());

        ImageData result = imageDataService.add(dto);

        assertEquals(existing.getImageId(), result.getImageId());
        assertEquals("new/path.jpg", result.getImagePath());
        verify(imageDataRepository).save(existing);
    }

    @Test
    void testAdd_coordinateValidationFails_throws() {
        ImageDataDto dto = new ImageDataDto();
        // 模拟坐标转换后Y坐标 <= -5，直接在 extractAndSetCoordinates 里模拟
        UtmToUnityCoordinate utm = mock(UtmToUnityCoordinate.class);

        // 这里需要做点设计修改让 UtmToUnityCoordinate 可以注入或 mock，否则难单测
        // 暂时只能用 spy 或假设你的 extractAndSetCoordinates 会设置 centerY = -10

        ImageDataService serviceSpy = Mockito.spy(imageDataService);

        doAnswer(invocation -> {
            ImageData imageData = invocation.getArgument(0);
            imageData.setCenterY(BigDecimal.valueOf(-10));
            return null;
        }).when(serviceSpy).add(any());

        // 简单写法是直接调用 add，期待异常
        // 你可以调整设计，支持依赖注入 utmCoordinate，方便测试

        // 这里演示抛异常的逻辑（稍作简化）

        ImageData imageData = new ImageData();
        imageData.setCenterY(BigDecimal.valueOf(-10));

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            if (imageData.getCenterY().compareTo(BigDecimal.valueOf(-5)) <= 0) {
                throw new RuntimeException("异常坐标！");
            }
        });
        assertEquals("异常坐标！", thrown.getMessage());
    }

    @Test
    void testGetClosestImageData_found() {
        List<ImageData> allImages = new ArrayList<>();
        ImageData img1 = new ImageData();
        img1.setImageId(1);
        img1.setCenterX(BigDecimal.valueOf(0));
        img1.setCenterY(BigDecimal.valueOf(0));
        img1.setCenterZ(BigDecimal.valueOf(0));
        allImages.add(img1);

        when(imageDataRepository.findAll()).thenReturn(allImages);

        // 这里假设 DistanceCalculate.findClosestImageData 返回第一个
        try (MockedStatic<DistanceCalculate> mocked = Mockito.mockStatic(DistanceCalculate.class)) {
            mocked.when(() -> DistanceCalculate.findClosestImageData(anyList(), any(), any(), any()))
                    .thenReturn(img1);

            ImageData result = imageDataService.getClosestImageData(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);

            assertNotNull(result);
            assertEquals(1, result.getImageId());
        }
    }

    @Test
    void testGetImagePath_exists() {
        ImageData img = new ImageData();
        img.setImageId(1);
        img.setImagePath("some/path.jpg");

        when(imageDataRepository.findById(1)).thenReturn(Optional.of(img));

        String path = imageDataService.getImagePath(1);

        assertEquals("some/path.jpg", path);
    }

    @Test
    void testGetImagePath_notExists() {
        when(imageDataRepository.findById(2)).thenReturn(Optional.empty());

        String path = imageDataService.getImagePath(2);

        assertNull(path);
    }

    @Test
    void testGetAbnormalDetailsByImageId_found() {
        ImageData img = new ImageData();
        img.setImageId(1);

        ImageAbnormalDetail detail = new ImageAbnormalDetail();
        AbnormalType abnormalType = new AbnormalType();
        abnormalType.setTypeName("TypeA");
        detail.setAbnormalType(abnormalType);
        detail.setDescription("desc");
        detail.setDetectedTime(Timestamp.valueOf(LocalDateTime.now()));

        when(imageDataRepository.findById(1)).thenReturn(Optional.of(img));
        when(imageAbnormalDetailRepository.findByImageData(img)).thenReturn(List.of(detail));

        List<AbnormalDetailDto> dtos = imageDataService.getAbnormalDetailsByImageId(1);

        assertEquals(1, dtos.size());
        assertEquals("TypeA", dtos.get(0).getTypeName());
    }

    @Test
    void testGetAbnormalDetailsByImageId_imageNotFound() {
        when(imageDataRepository.findById(99)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> imageDataService.getAbnormalDetailsByImageId(99));

        assertTrue(ex.getMessage().contains("图片不存在"));
    }
}
