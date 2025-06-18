package intelligentcurtainwall.modelingcommunication.algorithm;

import intelligentcurtainwall.modelingcommunication.entity.ImageData;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;

public class DistanceCalculate {

    public static ImageData findClosestImageData(List<ImageData> imageDataList, BigDecimal clickX, BigDecimal clickY, BigDecimal clickZ) {
        // 最小距离阈值
        final BigDecimal DISTANCE_THRESHOLD = BigDecimal.valueOf(5.0);

        ImageData closestImageData = null;
        BigDecimal minDistance = null;

        for (ImageData imageData : imageDataList) {
            BigDecimal distance = calculateEuclideanDistance(
                    clickX, clickY, clickZ,
                    imageData.getCenterX(), imageData.getCenterY(), imageData.getCenterZ()
            );

            if (minDistance == null || distance.compareTo(minDistance) < 0) {
                minDistance = distance;
                closestImageData = imageData;
            }
        }

        // 超出最小距离阈值时不返回图片
        if (minDistance != null && minDistance.compareTo(DISTANCE_THRESHOLD) > 0) {
            return null; // 距离太远，不匹配任何图片
        }

        return closestImageData;
    }

    private static BigDecimal calculateEuclideanDistance(BigDecimal x1, BigDecimal y1, BigDecimal z1,
                                                         BigDecimal x2, BigDecimal y2, BigDecimal z2) {
        BigDecimal dx = x1.subtract(x2).pow(2);
        BigDecimal dy = y1.subtract(y2).pow(2);
        BigDecimal dz = z1.subtract(z2).pow(2);

        return dx.add(dy).add(dz).sqrt(MathContext.DECIMAL64);
    }
}
