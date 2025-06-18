package intelligentcurtainwall.modelingcommunication.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Table(name="tb_image_data")
@Entity
public class ImageData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="image_id")
    private Integer imageId;

    @Column(name="image_path")
    private String imagePath;

    @ManyToOne
    @JoinColumn(name = "surface_id", referencedColumnName = "surface_id")
    private SurfaceCoordinate surfaceCoordinate;

    @Column(name = "center_x", precision = 10, scale = 2)
    private BigDecimal centerX;

    @Column(name = "center_y", precision = 10, scale = 2)
    private BigDecimal centerY;

    @Column(name = "center_z", precision = 10, scale = 2)
    private BigDecimal centerZ;

    @Column(name = "has_abnormal")
    private Boolean hasAbnormal;

    public ImageData(BigDecimal x, BigDecimal y, BigDecimal z) {
        this.centerX = x;
        this.centerY = y;
        this.centerZ = z;
    }

    public ImageData() {

    }
}
