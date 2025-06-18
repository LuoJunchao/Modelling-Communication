package intelligentcurtainwall.modelingcommunication.dto;

import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
public class ImageCoordinateDto {

    private BigDecimal centerX;
    private BigDecimal centerY;
    private BigDecimal centerZ;

    public ImageCoordinateDto(BigDecimal centerX, BigDecimal centerY, BigDecimal centerZ) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.centerZ = centerZ;
    }

    public void setCenterX(BigDecimal centerX) {
        this.centerX = centerX.setScale(2, RoundingMode.HALF_UP);
    }

    public void setCenterY(BigDecimal centerY) {
        this.centerY = centerY.setScale(2, RoundingMode.HALF_UP);
    }

    public void setCenterZ(BigDecimal centerZ) {
        this.centerZ = centerZ.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public String toString() {
        return "ImageCoordinateDto{" +
                "centerX=" + centerX +
                ", centerY=" + centerY +
                ", centerZ=" + centerZ +
                '}';
    }
}
