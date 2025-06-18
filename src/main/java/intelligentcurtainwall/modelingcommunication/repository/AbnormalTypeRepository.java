package intelligentcurtainwall.modelingcommunication.repository;

import intelligentcurtainwall.modelingcommunication.entity.AbnormalType;
import org.springframework.data.repository.CrudRepository;

public interface AbnormalTypeRepository extends CrudRepository<AbnormalType, Integer> {
    AbnormalType findByTypeName(String abnormalType);
}
