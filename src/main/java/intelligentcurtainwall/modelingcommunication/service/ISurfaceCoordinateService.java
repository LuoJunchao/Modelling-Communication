package intelligentcurtainwall.modelingcommunication.service;

import intelligentcurtainwall.modelingcommunication.entity.SurfaceCoordinate;


import java.math.BigDecimal;

public interface ISurfaceCoordinateService {

    // 判断点击是否有效
    boolean validateClick(BigDecimal clickX, BigDecimal clickY, BigDecimal clickZ);

    // 判断坐标是否有效
    boolean isClickInsideSurface(BigDecimal clickX, BigDecimal clickY, BigDecimal clickZ, SurfaceCoordinate surfaceCoordinate);

    // 判断图片的建筑面
    void whichSurface(ImageData imageData);

}
