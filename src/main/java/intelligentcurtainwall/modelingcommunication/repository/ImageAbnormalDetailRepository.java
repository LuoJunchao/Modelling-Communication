package intelligentcurtainwall.modelingcommunication.repository;

import intelligentcurtainwall.modelingcommunication.entity.ImageAbnormalDetail;
import intelligentcurtainwall.modelingcommunication.entity.ImageData;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ImageAbnormalDetailRepository extends CrudRepository<ImageAbnormalDetail, Integer> {

    List<ImageAbnormalDetail> findByImageData(ImageData imageData);

}
