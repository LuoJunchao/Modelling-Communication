package intelligentcurtainwall.modelingcommunication.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import intelligentcurtainwall.modelingcommunication.dto.AbnormalUploadRequestDto;
import intelligentcurtainwall.modelingcommunication.entity.BatchTaskStatus;
import intelligentcurtainwall.modelingcommunication.manager.TaskManager;
import intelligentcurtainwall.modelingcommunication.service.IImageDataService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

@WebMvcTest(controllers = ImageTaskController.class)
public class ImageTaskControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IImageDataService imageDataService;

    @MockBean
    private TaskManager taskManager;

    @Autowired
    private ObjectMapper objectMapper;

    private List<String> sampleImagePaths;

    private AbnormalUploadRequestDto sampleAbnormalRequest;

    @BeforeEach
    void setUp() {
        sampleImagePaths = List.of("http://example.com/img1.jpg", "http://example.com/img2.jpg");

        sampleAbnormalRequest = new AbnormalUploadRequestDto();
        sampleAbnormalRequest.setImagePaths(sampleImagePaths);
        sampleAbnormalRequest.setAbnormalType("crack");
    }

    @Test
    void testSubmitTask() throws Exception {
        doNothing().when(taskManager).initTask(anyString(), anyInt());
        doNothing().when(imageDataService).batchAddImagesAsync(anyList(), anyString());

        mockMvc.perform(post("/task/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleImagePaths)))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.notNullValue()));

        verify(taskManager, times(1)).initTask(anyString(), eq(sampleImagePaths.size()));
        verify(imageDataService, times(1)).batchAddImagesAsync(eq(sampleImagePaths), anyString());
    }

    @Test
    void testMarkAbnormal_Success() throws Exception {
        doNothing().when(taskManager).initTask(anyString(), anyInt());
        doNothing().when(imageDataService).markImagesAbnormal(any(AbnormalUploadRequestDto.class), anyString());

        mockMvc.perform(post("/task/mark-abnormals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleAbnormalRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isNotEmpty());

        verify(taskManager, times(1)).initTask(anyString(), eq(sampleImagePaths.size()));
        verify(imageDataService, times(1)).markImagesAbnormal(any(AbnormalUploadRequestDto.class), anyString());
    }

    @Test
    void testMarkAbnormal_FailValidation() throws Exception {
        // 传入空图片路径列表
        AbnormalUploadRequestDto invalidRequest = new AbnormalUploadRequestDto();
        invalidRequest.setImagePaths(List.of());
        invalidRequest.setAbnormalType("crack");

        mockMvc.perform(post("/task/mark-abnormals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("图片路径列表不能为空"));

        // 传入空异常类型
        invalidRequest.setImagePaths(sampleImagePaths);
        invalidRequest.setAbnormalType("");

        mockMvc.perform(post("/task/mark-abnormals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("异常类型不能为空"));
    }

    @Test
    void testMarkAbnormal_InternalServerError() throws Exception {
        doThrow(new RuntimeException("error")).when(imageDataService).markImagesAbnormal(any(), anyString());

        mockMvc.perform(post("/task/mark-abnormals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleAbnormalRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("处理失败"));
    }

    @Test
    void testGetStatus_Found() throws Exception {
        BatchTaskStatus status = new BatchTaskStatus();
        // status.setTaskId("task123"); // BatchTaskStatus 没有 taskId 字段，注释掉
        status.setTotal(10);
        status.setProcessed(5);
        status.setCompleted(false);

        when(taskManager.getStatus("task123")).thenReturn(status);

        mockMvc.perform(get("/task/status/task123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(10))
                .andExpect(jsonPath("$.processed").value(5))
                .andExpect(jsonPath("$.completed").value(false));
    }


    @Test
    void testGetStatus_NotFound() throws Exception {
        when(taskManager.getStatus("task404")).thenReturn(null);

        mockMvc.perform(get("/task/status/task404"))
                .andExpect(status().isNotFound());
    }
}
