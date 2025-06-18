package intelligentcurtainwall.modelingcommunication.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "tb_abnormal_type")
public class AbnormalType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "type_id")
    private Integer typeId;

    @Column(name = "type_name")
    private String typeName; // 裂缝、污渍、锈蚀等
}
