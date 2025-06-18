package intelligentcurtainwall.modelingcommunication.algorithm;

import intelligentcurtainwall.modelingcommunication.entity.ImageData;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AlgorithmTest {

    @Test
    void testFindClosestImageData_withinThreshold() {
        // 准备测试数据
        ImageData img1 = new ImageData(new BigDecimal("1.0"), new BigDecimal("2.0"), new BigDecimal("3.0"));
        ImageData img2 = new ImageData(new BigDecimal("5.0"), new BigDecimal("5.0"), new BigDecimal("5.0"));
        ImageData img3 = new ImageData(new BigDecimal("1.5"), new BigDecimal("2.1"), new BigDecimal("3.2"));

        List<ImageData> imageList = Arrays.asList(img1, img2, img3);

        // 点击点接近 img3
        BigDecimal clickX = new BigDecimal("1.6");
        BigDecimal clickY = new BigDecimal("2.0");
        BigDecimal clickZ = new BigDecimal("3.1");

        ImageData closest = DistanceCalculate.findClosestImageData(imageList, clickX, clickY, clickZ);

        assertNotNull(closest);
        assertEquals(img3, closest);
    }

    @Test
    void testFindClosestImageData_exceedsThreshold() {
        // 所有点都太远
        ImageData img1 = new ImageData(new BigDecimal("100"), new BigDecimal("100"), new BigDecimal("100"));
        ImageData img2 = new ImageData(new BigDecimal("200"), new BigDecimal("200"), new BigDecimal("200"));

        List<ImageData> imageList = Arrays.asList(img1, img2);

        BigDecimal clickX = new BigDecimal("0.0");
        BigDecimal clickY = new BigDecimal("0.0");
        BigDecimal clickZ = new BigDecimal("0.0");

        ImageData closest = DistanceCalculate.findClosestImageData(imageList, clickX, clickY, clickZ);

        assertNull(closest); // 超出阈值，应返回 null
    }

    @Test
    void testFindClosestImageData_exactMatch() {
        ImageData img1 = new ImageData(new BigDecimal("3"), new BigDecimal("3"), new BigDecimal("3"));

        List<ImageData> imageList = List.of(img1);

        ImageData closest = DistanceCalculate.findClosestImageData(imageList,
                new BigDecimal("3"), new BigDecimal("3"), new BigDecimal("3"));

        assertNotNull(closest);
        assertEquals(img1, closest); // 完全重合
    }

    @Test
    void testFindClosestImageData_emptyList() {
        List<ImageData> imageList = List.of();

        ImageData closest = DistanceCalculate.findClosestImageData(imageList,
                new BigDecimal("1"), new BigDecimal("1"), new BigDecimal("1"));

        assertNull(closest);
    }

    @Test
    void testTransformCoordinate_basicCase() {
        // 构造一个UTM坐标（靠近TRANSLATION_VECTOR，模拟简单场景）
        double[] utm = {329171.081, 3463416.823, 14.750}; // 相对位移为 [1,1,1]

        double[] result = UtmToUnityCoordinate.transformCoordinate(utm);

        // 手动计算前两个坐标变换：
        // x' = 0.6411659 * 1 + (-0.7674178) * 1 = -0.1262519
        // y' = 0.7674178 * 1 +  0.6411659 * 1 = 1.4085837
        // z' = 1
        assertEquals(-0.1262519, result[0], 1e-6);
        assertEquals(1.4085837, result[1], 1e-6);
        assertEquals(1.0, result[2], 1e-6);
    }

    @Test
    void testTransformCoordinate_invalidLength() {
        double[] invalid = {329171.081, 3463416.823}; // 只有2维
        assertThrows(IllegalArgumentException.class, () -> UtmToUnityCoordinate.transformCoordinate(invalid));
    }

}
