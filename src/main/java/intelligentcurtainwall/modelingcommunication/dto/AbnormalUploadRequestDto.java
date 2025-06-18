package intelligentcurtainwall.modelingcommunication.dto;

import lombok.Data;

import java.util.List;

@Data
public class AbnormalUploadRequestDto {
    private List<String> imagePaths;
    private String abnormalType;
}
