package intelligentcurtainwall.modelingcommunication.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ImageDataDto {

    private Integer imageId;
    private String imagePath;
    private Integer surfaceId;
    private BigDecimal centerX;
    private BigDecimal centerY;
    private BigDecimal centerZ;
    private Boolean hasAbnormal;

    private List<AbnormalDetailDto> abnormalDetailList;

}
