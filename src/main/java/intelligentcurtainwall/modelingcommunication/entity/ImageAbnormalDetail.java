package intelligentcurtainwall.modelingcommunication.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Entity
@Table(name = "tb_image_abnormal_detail")
public class ImageAbnormalDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "image_id")
    private ImageData imageData;

    @ManyToOne
    @JoinColumn(name = "type_id")
    private AbnormalType abnormalType;

    @Column(name = "detected_time")
    private Timestamp detectedTime;

    @Column(name = "description")
    private String description; // 可选：补充描述

}
