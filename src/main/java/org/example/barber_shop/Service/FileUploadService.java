package org.example.barber_shop.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FileUploadService {
    private static final String IMGBB_URL = "https://api.imgbb.com/1/upload";
    private static final String API_KEY = "b8e9afffd619153124dfd857303e66a7";

    public JsonNode uploadToImgBB(MultipartFile file) throws IOException {
        File tempFile = convertMultiPartToFile(file);
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(IMGBB_URL);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addBinaryBody("image", tempFile);
        builder.addTextBody("key", API_KEY);
        builder.addTextBody("name", String.valueOf(System.currentTimeMillis()));
        httpPost.setEntity(builder.build());
        HttpResponse response = httpClient.execute(httpPost);
        HttpEntity entity = response.getEntity();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonResponse = objectMapper.readTree(EntityUtils.toString(entity));
        tempFile.delete();
        return jsonResponse;
    }

    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
        try (FileOutputStream fos = new FileOutputStream(convFile)) {
            fos.write(file.getBytes());
        }
        return convFile;
    }

    public JsonNode[] multipleFileUploadImgBB(List<MultipartFile> files) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(IMGBB_URL);
        JsonNode[] jsonNodes = new JsonNode[files.size()];
        for (int i = 0; i < files.size(); i++) {
            File tempFile = convertMultiPartToFile(files.get(i));
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addTextBody("key", API_KEY);
            builder.addBinaryBody("image", tempFile);
            builder.addTextBody("name", String.valueOf(System.currentTimeMillis()));
            httpPost.setEntity(builder.build());
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonResponse = objectMapper.readTree(EntityUtils.toString(entity));
            tempFile.delete();
            jsonNodes[i] = jsonResponse;
        }
        return jsonNodes;
    }
}
