package intelligentcurtainwall.modelingcommunication.dto;

import intelligentcurtainwall.modelingcommunication.entity.ImageAbnormalDetail;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class AbnormalDetailDto {

    private String typeName;         // 异常名称，如：裂缝、污渍
    private Timestamp detectedTime;  // 检测时间
    private String description;      // 异常详情（可选说明）

    public static AbnormalDetailDto convertAbnormalDetailToDto(ImageAbnormalDetail abnormalDetail) {
        AbnormalDetailDto dto = new AbnormalDetailDto();

        dto.setTypeName(abnormalDetail.getAbnormalType().getTypeName());
        dto.setDetectedTime(abnormalDetail.getDetectedTime());
        dto.setDescription(abnormalDetail.getDescription());

        return dto;
    }

}
