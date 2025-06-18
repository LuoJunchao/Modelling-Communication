package intelligentcurtainwall.modelingcommunication.algorithm;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

public class ImageDownload {

    // 临时文件目录
    private static final String TEMP_DIR = System.getProperty("java.io.tmpdir");

    // 从OSS下载图片并返回临时存储路径
    public static String downloadImage(String imageUrl) throws IOException {
        // 创建临时文件名
        String tempFileName = UUID.randomUUID() + ".jpg";
        Path tempFilePath = Paths.get(TEMP_DIR, tempFileName);

        // 使用HttpClient从OSS下载图片
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(imageUrl);
            try (CloseableHttpResponse response = client.execute(request)) {
                HttpEntity entity = response.getEntity();
                try (InputStream inputStream = entity.getContent()) {
                    // 将OSS图片内容写入到临时文件
                    Files.copy(inputStream, tempFilePath, StandardCopyOption.REPLACE_EXISTING);
                }
            }
        } catch (IOException e) {
            throw new IOException("下载图片失败: " + e.getMessage(), e);
        }

        return tempFilePath.toString();
    }
}
