package intelligentcurtainwall.modelingcommunication.service;

import intelligentcurtainwall.modelingcommunication.entity.SurfaceCoordinate;
import intelligentcurtainwall.modelingcommunication.repository.SurfaceCoordinateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class SurfaceCoordinateService implements ISurfaceCoordinateService {

    @Autowired
    SurfaceCoordinateRepository surfaceCoordinateRepository;

    public boolean validateClick(BigDecimal clickX, BigDecimal clickY, BigDecimal clickZ) {
        Iterable<SurfaceCoordinate> allSurfaces = surfaceCoordinateRepository.findAll();

        for (SurfaceCoordinate surfaceCoordinate : allSurfaces) {
            if(isClickInsideSurface(clickX, clickY, clickZ, surfaceCoordinate)){
                return true;
            }
        }

        return false;
    }

    public boolean isClickInsideSurface(BigDecimal clickX, BigDecimal clickY, BigDecimal clickZ, SurfaceCoordinate surfaceCoordinate) {
        // 设置浮动范围 ±0.5m
        BigDecimal tolerance = new BigDecimal("0.5");

        return clickX.compareTo(surfaceCoordinate.getMinX().subtract(tolerance)) >= 0 &&
                clickX.compareTo(surfaceCoordinate.getMaxX().add(tolerance)) <= 0 &&
                clickY.compareTo(surfaceCoordinate.getMinY().subtract(tolerance)) >= 0 &&
                clickY.compareTo(surfaceCoordinate.getMaxY().add(tolerance)) <= 0 &&
                clickZ.compareTo(surfaceCoordinate.getMinZ().subtract(tolerance)) >= 0 &&
                clickZ.compareTo(surfaceCoordinate.getMaxZ().add(tolerance)) <= 0;
    }

    @Override
    public void whichSurface(ImageData imageData) {

        Map<Integer, BigDecimal> faceReferenceCoordinates = new HashMap<>();

        //东立面
        faceReferenceCoordinates.put(1, new BigDecimal("0.01"));
        faceReferenceCoordinates.put(2, new BigDecimal("-52.50"));
        faceReferenceCoordinates.put(3, new BigDecimal("-52.50"));
        faceReferenceCoordinates.put(4, new BigDecimal("-17.06"));

        //北立面
        faceReferenceCoordinates.put(5, new BigDecimal("-0.01"));
        faceReferenceCoordinates.put(6, new BigDecimal("-0.01"));
        faceReferenceCoordinates.put(7, new BigDecimal("-5.40"));

        //西立面
        faceReferenceCoordinates.put(8, new BigDecimal("-100.50"));
        faceReferenceCoordinates.put(9, new BigDecimal("-100.50"));
        faceReferenceCoordinates.put(10, new BigDecimal("-100.50"));

        //南立面
        faceReferenceCoordinates.put(11, new BigDecimal("108.29"));
        faceReferenceCoordinates.put(12, new BigDecimal("84.01"));
        faceReferenceCoordinates.put(13, new BigDecimal("55.01"));

        BigDecimal imageX = imageData.getCenterX();
        BigDecimal imageZ = imageData.getCenterZ();


        BigDecimal minDifference = new BigDecimal("9999");
        int closestFaceId = -1;

        // 确定最近的面
        for (Map.Entry<Integer, BigDecimal> entry : faceReferenceCoordinates.entrySet()) {
            int faceId = entry.getKey();
            BigDecimal referenceValue = entry.getValue();

            BigDecimal difference;
            if (faceId <= 4) {
                // 东立面，固定Z
                difference = imageZ.subtract(referenceValue).abs();
            } else if (faceId <= 7) {
                // 北立面，固定X
                difference = imageX.subtract(referenceValue).abs();
            } else if (faceId <= 10) {
                // 西立面，固定Z
                difference = imageZ.subtract(referenceValue).abs();
            } else {
                // 南立面，固定X
                difference = imageX.subtract(referenceValue).abs();
            }

            if (difference.compareTo(minDifference) < 0) {
                minDifference = difference;
                closestFaceId = faceId;
            }
        }

        // 查找对应的 SurfaceCoordinate 对象
        Optional<SurfaceCoordinate> surfaceCoordinateOpt = surfaceCoordinateRepository.findById(closestFaceId);
        if (surfaceCoordinateOpt.isPresent()) {
            SurfaceCoordinate surfaceCoordinate = surfaceCoordinateOpt.get();
            imageData.setSurfaceCoordinate(surfaceCoordinate);

            // 更新 X 或 Z 坐标，消除误差
            if (closestFaceId <= 4 || (closestFaceId >= 8 && closestFaceId <= 10)) {
                // 东、西立面固定Z
                imageData.setCenterZ(faceReferenceCoordinates.get(closestFaceId));
            } else {
                // 北、南立面固定X
                imageData.setCenterX(faceReferenceCoordinates.get(closestFaceId));
            }
        } else {
            throw new RuntimeException("未找到对应的 SurfaceCoordinate.");
        }
    }
}
