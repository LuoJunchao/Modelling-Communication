package intelligentcurtainwall.modelingcommunication.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import intelligentcurtainwall.modelingcommunication.dto.AbnormalDetailDto;
import intelligentcurtainwall.modelingcommunication.dto.ImageDataDto;
import intelligentcurtainwall.modelingcommunication.entity.ImageData;
import intelligentcurtainwall.modelingcommunication.service.IImageDataService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

@WebMvcTest(controllers = ImageDataController.class)
public class ImageDataControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IImageDataService imageDataService;

    @Autowired
    private ObjectMapper objectMapper;

    private ImageDataDto sampleDto;
    private ImageData sampleImageData;

    @BeforeEach
    void setUp() {
        sampleDto = new ImageDataDto();
        sampleDto.setImageId(1);
        sampleDto.setImagePath("http://example.com/image.jpg");

        sampleImageData = new ImageData();
        sampleImageData.setImageId(1);
        sampleImageData.setImagePath("http://example.com/image.jpg");
    }

    @Test
    void testAddImage() throws Exception {
        when(imageDataService.add(any(ImageDataDto.class))).thenReturn(sampleImageData);

        mockMvc.perform(post("/imageData")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleDto)))
                .andExpect(status().isOk())
                // 这里用 imageId 而不是 id，因为返回 JSON 是 imageId
                .andExpect(jsonPath("$.data.imageId").value(sampleImageData.getImageId()))
                .andExpect(jsonPath("$.data.imagePath").value(sampleImageData.getImagePath()));

        verify(imageDataService, times(1)).add(any(ImageDataDto.class));
    }

    @Test
    void testGetImageUrl() throws Exception {
        when(imageDataService.getImagePath(1)).thenReturn("http://example.com/image.jpg");

        mockMvc.perform(get("/imageData/{imageId}/url", 1)) // 传入路径变量值
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("http://example.com/image.jpg"));

        verify(imageDataService, times(1)).getImagePath(1);
    }

    @Test
    void testGetAllAbnormalImages() throws Exception {
        when(imageDataService.getAllAbnormalImages()).thenReturn(List.of(sampleDto));

        mockMvc.perform(get("/imageData/all-abnormal-images"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].imageId").value(sampleDto.getImageId())); // 修改 id 为 imageId

        verify(imageDataService, times(1)).getAllAbnormalImages();
    }

    @Test
    void testGetClosest() throws Exception {
        when(imageDataService.getClosestImageData(any(), any(), any())).thenReturn(sampleImageData);

        mockMvc.perform(get("/imageData/closest")
                        .param("clickX", "1.1")
                        .param("clickY", "2.2")
                        .param("clickZ", "3.3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.imageId").value(sampleImageData.getImageId())); // 修改 id 为 imageId

        verify(imageDataService, times(1)).getClosestImageData(
                new BigDecimal("1.1"),
                new BigDecimal("2.2"),
                new BigDecimal("3.3"));
    }

    @Test
    void testDelete() throws Exception {
        doNothing().when(imageDataService).deleteImageData(1);

        mockMvc.perform(delete("/imageData/{imageId}", 1)) // 传入路径变量
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty());

        verify(imageDataService, times(1)).deleteImageData(1);
    }

    @Test
    void testGetAbnormalDetails() throws Exception {
        AbnormalDetailDto detail = new AbnormalDetailDto();
        // 如果有必要，可以给 detail 设置字段

        when(imageDataService.getAbnormalDetailsByImageId(1)).thenReturn(List.of(detail));

        mockMvc.perform(get("/imageData/{imageId}/abnormalDetails", 1)) // 传入路径变量
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());

        verify(imageDataService, times(1)).getAbnormalDetailsByImageId(1);
    }
}

