package intelligentcurtainwall.modelingcommunication.algorithm;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;

import intelligentcurtainwall.modelingcommunication.entity.ImageData;
import org.locationtech.proj4j.*;

public class ExtractionLrfToUtm {
    private static final String UTM_ZONE_51N = "EPSG:32651";

    public UtmCoordinate convertLrfTargetToUtm(ImageData imageDataNew) throws Exception{
        Process process = null;
        File temFile = null;

        BigDecimal lrfLon = BigDecimal.ZERO;
        BigDecimal lrfLat = BigDecimal.ZERO;
        BigDecimal lrfAlt = BigDecimal.ZERO;

        try{

            String temImagePath = ImageDownload.downloadImage(imageDataNew.getImagePath());
            temFile = new File(temImagePath);

            String command = "exiftool -n -LRFTargetLon -LRFTargetLat -LRFTargetAbsAlt " + temImagePath;
            process = new ProcessBuilder(command.split(" ")).start();

            BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;

            while ((line = br.readLine()) != null){
                line = line.trim();

                if(line.startsWith("LRF Target Lon")){
                    lrfLon = parseDecimalDegrees(line.split(":")[1].trim());
                }else if(line.startsWith("LRF Target Lat")){
                    lrfLat = parseDecimalDegrees(line.split(":")[1].trim());
                }else if(line.startsWith("LRF Target Abs Alt")){
                    lrfAlt = parseDecimalDegrees(line.split(":")[1].trim());
                }
            }

            process.waitFor();
        }catch (Exception e){
            throw new Exception("提取LRF信息失败：" + e.getMessage(), e);
        }finally {
            if(process != null){
                process.destroy();
            }
            if (temFile != null) {
                try {
                    Files.deleteIfExists(temFile.toPath());
                } catch (IOException e) {
                    System.err.println("临时文件删除失败：" + temFile.getAbsolutePath() + "，原因：" + e.getMessage());
                }
            }
        }

        //将WGS84坐标转换为UTM坐标
        ProjCoordinate utmCoord = convertToUtm(lrfLat, lrfLon);

        return new UtmCoordinate(utmCoord.x, utmCoord.y, lrfAlt.doubleValue());
    }

    private BigDecimal parseDecimalDegrees(String decimalDegrees){
        return new BigDecimal(decimalDegrees).setScale(7, RoundingMode.HALF_UP);
    }

    private ProjCoordinate convertToUtm(BigDecimal latitude, BigDecimal longitude){
        //创建坐标参考系统工厂
        CRSFactory crsFactory = new CRSFactory();

        //WGS84坐标系
        CoordinateReferenceSystem srcCrs = crsFactory.createFromName("EPSG:4326");

        //UTM坐标系
        CoordinateReferenceSystem dstCrs = crsFactory.createFromName(UTM_ZONE_51N);

        //创建坐标转换工厂
        CoordinateTransformFactory transformFactory = new CoordinateTransformFactory();

        //创建转换对象
        CoordinateTransform transform = transformFactory.createTransform(srcCrs, dstCrs);

        //创建源坐标
        ProjCoordinate srcCoord = new ProjCoordinate(longitude.doubleValue(), latitude.doubleValue());

        //存储转换后的UTM坐标
        ProjCoordinate destCoord = new ProjCoordinate();

        //执行坐标转换
        transform.transform(srcCoord, destCoord);

        return destCoord;
    }

    public record UtmCoordinate(double easting, double northing, double altitude){

        @Override
        public double easting() {
            return easting;
        }

        @Override
        public double northing() {
            return northing;
        }

        @Override
        public double altitude() {
            return altitude;
        }

        @Override
        public String toString() {
            return "UtmCoordinate{" +
                    "easting=" + easting +
                    ", northing=" + northing +
                    ", altitude=" + altitude +
                    '}';
        }
    }
}
