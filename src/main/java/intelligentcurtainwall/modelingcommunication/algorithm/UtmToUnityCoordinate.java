package intelligentcurtainwall.modelingcommunication.algorithm;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class UtmToUnityCoordinate {

    // 定义旋转矩阵 R
    private static final double[][] ROTATION_MATRIX = {
            {0.6411659, -0.7674178},
            {0.7674178, 0.6411659}
    };

    // 定义位移向量 T
    private static final double[] TRANSLATION_VECTOR = {329170.081, 3463415.823, 13.750};

    public static double[] transformCoordinate(double[] utmCoordinate) {
        if (utmCoordinate.length != 3) {
            throw new IllegalArgumentException("输入坐标点必须是一个三维数组");
        }

        double[] result = new double[3];

        // 应用旋转矩阵 ROTATION_MATRIX 和位移向量 TRANSLATION_VECTOR
        for (int i = 0; i < 2; i++) {
            result[i] = 0;
            for (int j = 0; j < 2; j++) {
                result[i] += ROTATION_MATRIX[i][j] * (utmCoordinate[j] - TRANSLATION_VECTOR[j]);
            }
        }
        result[2] = utmCoordinate[2] - TRANSLATION_VECTOR[2];
        return result;
    }

    public void extractAndSetCoordinates(ImageData imageData) {
        try {
            ExtractionLrfToUtm extractionLrfToUtm = new ExtractionLrfToUtm();
            ExtractionLrfToUtm.UtmCoordinate utmCoordinate = extractionLrfToUtm.convertLrfTargetToUtm(imageData);

            double[] utmArray = {utmCoordinate.easting(), utmCoordinate.northing(), utmCoordinate.altitude()};
            double[] unityCoordinate = UtmToUnityCoordinate.transformCoordinate(utmArray);

            imageData.setCenterX(BigDecimal.valueOf(unityCoordinate[0]).setScale(2, RoundingMode.HALF_UP));
            imageData.setCenterY(BigDecimal.valueOf(unityCoordinate[2]).setScale(2, RoundingMode.HALF_UP));
            imageData.setCenterZ(BigDecimal.valueOf(unityCoordinate[1]).setScale(2, RoundingMode.HALF_UP));
        } catch (Exception e) {
            throw new RuntimeException("坐标提取和转换失败：" + e.getMessage(), e);
        }
    }

}
