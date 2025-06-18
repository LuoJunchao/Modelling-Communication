package intelligentcurtainwall.modelingcommunication.repository;

import intelligentcurtainwall.modelingcommunication.entity.ImageData;
import intelligentcurtainwall.modelingcommunication.entity.SurfaceCoordinate;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ImageDataRepository extends CrudRepository<ImageData, Integer> {

    ImageData findByCenterXAndCenterYAndCenterZ(BigDecimal centerX, BigDecimal centerY, BigDecimal centerZ);

    List<ImageData> findBySurfaceCoordinate(SurfaceCoordinate surfaceCoordinate);
}
