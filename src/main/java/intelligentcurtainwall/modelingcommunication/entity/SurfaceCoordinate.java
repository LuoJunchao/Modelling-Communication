package intelligentcurtainwall.modelingcommunication.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(name="tb_surface_coordinate")
public class SurfaceCoordinate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="surface_id")
    private Integer surfaceId;

    @Column(name = "surface_name")
    private String surfaceName;

    @Column(name="min_x", precision=10, scale=2)
    private BigDecimal minX;

    @Column(name="min_y", precision=10, scale=2)
    private BigDecimal minY;

    @Column(name="min_z", precision=10, scale=2)
    private BigDecimal minZ;

    @Column(name="max_x", precision=10, scale=2)
    private BigDecimal maxX;

    @Column(name="max_y", precision=10, scale=2)
    private BigDecimal maxY;

    @Column(name="max_z", precision=10, scale=2)
    private BigDecimal maxZ;

}
