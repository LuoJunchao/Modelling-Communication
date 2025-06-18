package intelligentcurtainwall.modelingcommunication.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;

@Getter
public class InteractionLogDto {

    @Setter
    private Integer logId;
    @Setter
    private Timestamp createTime;
    private BigDecimal clickX;
    private BigDecimal clickY;
    private BigDecimal clickZ;

    public void setClickX(BigDecimal clickX) {
        this.clickX = clickX.setScale(2, RoundingMode.HALF_UP);
    }

    public void setClickY(BigDecimal clickY) {
        this.clickY = clickY.setScale(2, RoundingMode.HALF_UP);
    }

    public void setClickZ(BigDecimal clickZ) {
        this.clickZ = clickZ.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public String toString() {
        return "InteractionLogDto{" +
                "logId=" + logId +
                ", createTime=" + createTime +
                ", clickX=" + clickX +
                ", clickY=" + clickY +
                ", clickZ=" + clickZ +
                '}';
    }
}
