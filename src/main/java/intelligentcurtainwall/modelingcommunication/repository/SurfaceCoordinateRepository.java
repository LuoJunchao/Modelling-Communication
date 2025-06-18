package intelligentcurtainwall.modelingcommunication.repository;

import intelligentcurtainwall.modelingcommunication.entity.SurfaceCoordinate;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SurfaceCoordinateRepository extends CrudRepository<SurfaceCoordinate, Integer> {

}
