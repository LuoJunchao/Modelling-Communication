package intelligentcurtainwall.modelingcommunication.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@Table(name="tb_interaction_log")
@Entity
public class InteractionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="log_id")
    private Integer logId;

    @Column(name="create_time")
    private Timestamp createTime;

    @Column(name="click_x", precision = 10, scale = 2)
    private BigDecimal clickX;

    @Column(name="click_y", precision = 10, scale = 2)
    private BigDecimal clickY;

    @Column(name="click_z", precision = 10, scale = 2)
    private BigDecimal clickZ;

}
