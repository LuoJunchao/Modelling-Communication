package intelligentcurtainwall.modelingcommunication.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
public class SurfaceCoordinateDto {

    @Setter
    private Integer surfaceId;
    @Setter
    private String surfaceName;
    private BigDecimal minX;
    private BigDecimal minY;
    private BigDecimal minZ;
    private BigDecimal maxX;
    private BigDecimal maxY;
    private BigDecimal maxZ;

    public void setMinX(BigDecimal minX) {
        this.minX = minX.setScale(2, RoundingMode.HALF_DOWN);
    }

    public void setMinY(BigDecimal minY) {
        this.minY = minY.setScale(2, RoundingMode.HALF_DOWN);
    }

    public void setMinZ(BigDecimal minZ) {
        this.minZ = minZ.setScale(2, RoundingMode.HALF_DOWN);
    }

    public void setMaxX(BigDecimal maxX) {
        this.maxX = maxX.setScale(2, RoundingMode.HALF_DOWN);
    }

    public void setMaxY(BigDecimal maxY) {
        this.maxY = maxY.setScale(2, RoundingMode.HALF_DOWN);
    }

    public void setMaxZ(BigDecimal maxZ) {
        this.maxZ = maxZ.setScale(2, RoundingMode.HALF_DOWN);
    }

    @Override
    public String toString() {
        return "SurfaceCoordinateDto{" +
                "surfaceId=" + surfaceId +
                ", surfaceName='" + surfaceName + '\'' +
                ", minX=" + minX +
                ", minY=" + minY +
                ", minZ=" + minZ +
                ", maxX=" + maxX +
                ", maxY=" + maxY +
                ", maxZ=" + maxZ +
                '}';
    }
}
