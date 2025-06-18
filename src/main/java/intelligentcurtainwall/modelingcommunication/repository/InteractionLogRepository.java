package intelligentcurtainwall.modelingcommunication.repository;

import intelligentcurtainwall.modelingcommunication.entity.InteractionLog;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InteractionLogRepository extends CrudRepository<InteractionLog,Integer> {

}
