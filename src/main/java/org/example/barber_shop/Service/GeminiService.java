package org.example.barber_shop.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.barber_shop.DTO.AI.ChangeHairStyle;
import org.example.barber_shop.DTO.AI.ProRequest;
import org.example.barber_shop.DTO.AI.Request;
import org.example.barber_shop.Exception.LocalizedException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GeminiService {
    @Value("${gemini.api.key}")
    private String apiKey;
    @Value("${gpt.api.key}")
    private String gptApiKey;
    @Value("${ailabtools.api.key}")
    private String ailabToolsApiKey;
    private final FileUploadService fileUploadService;
    private final RestTemplate restTemplate;
    public String askAI(Request request) {
        String askAI;
        if (request.gender == null){
            throw new LocalizedException("server.error");
        }
        if (request.gender.isEmpty()) {
            throw new LocalizedException("server.error");
        }
        if (!request.gender.equals("male") && !request.gender.equals("female")) {
            throw new LocalizedException("server.error");
        }
        if (request.language.equals("vi")) {
            askAI = "Tôi là "+ (request.gender.equals("male") ? "nam" : "nữ") +". Mặt tôi có những đặc điểm như sau:" + request.characteristics.toString() + ", bạn có thể gọi ý cho tôi 1 vài kiểu tóc phù hợp được không, đưa cho tôi câu trả lời bằng 1 mảng các kiểu tóc (json array, gồm có style và description)";
        } else if (request.language.equals("ko")) {
            askAI = "저는 " + (request.gender.equals("male") ? "남성" : "여성") + "입니다"+".내 얼굴에는 다음과 같은 특징이 있습니다:" + request.characteristics.toString() + ", 나에게 적합한 헤어스타일을 제안해 주실 수 있나요? 다양한 헤어스타일에 대한 답을 알려주세요. (json array, style과 description이 포함되어 있습니다)";
        } else {
            throw new LocalizedException("server.error");
        }
        JSONObject payload = new JSONObject();
        JSONArray contents = new JSONArray();
        JSONArray parts = new JSONArray();
        JSONObject textPart = new JSONObject();
        textPart.put("text", askAI);
        parts.put(textPart);
        JSONObject content = new JSONObject();
        content.put("parts", parts);
        contents.put(content);
        payload.put("contents", contents);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestHttp = new HttpEntity<>(payload.toString(), headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response;
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash-latest:generateContent?key=" + apiKey;
        try {
            response = restTemplate.exchange(url, HttpMethod.POST, requestHttp, String.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw new LocalizedException("server.error");
        }
        JSONObject responseBody = new JSONObject(response.getBody());
        JSONArray candidates = responseBody.getJSONArray("candidates");
        if (!candidates.isEmpty()) {
            JSONObject firstCandidate = candidates.getJSONObject(0);
            JSONObject contentObj = firstCandidate.getJSONObject("content");
            JSONArray partsArray = contentObj.getJSONArray("parts");
            return partsArray.getJSONObject(0).getString("text").replace("json", "").replace("```", "");
        }
        return null;
    }

    public String askGpt(ProRequest request){
        try {
            JsonNode jsonNode = fileUploadService.uploadToImgBB(request.image);
            String imgUrl = jsonNode.path("data").path("url").asText();
            String askGpt = """
                {
                                "model": "gpt-4o-mini",
                                "store": true,
                                "messages": [
                                    {
                                        "role": "user",
                                        "content": [
                                            {
                                                "type": "text",
                                                "text": "Detect face shapes and suggest hairstyles based on key features. Provide a JSON object with face characteristics and hairstyles (name, description).No additional text needed"
                                            },
                                            {
                                                "type": "image_url",
                                                "image_url": {
                                                    "url": "%s",
                                                    "detail": "high"
                                                }
                                            }
                                        ]
                                    }
                                ]
                            }
                """;
            askGpt = String.format(askGpt, imgUrl);
            WebClient webClient = WebClient.builder()
                    .baseUrl("https://api.openai.com/v1/chat/completions")
                    .defaultHeader("Content-Type", "application/json")
                    .defaultHeader("Authorization", "Bearer " + gptApiKey)
                    .build();
            String result = webClient.post().bodyValue(askGpt).retrieve().bodyToMono(String.class).block();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(result);
            String content = rootNode.path("choices").get(0).path("message").path("content").asText().replace("json", "").replace("```", "");
            return content.replace("\n", "").strip();
        } catch (Exception e){
            e.printStackTrace();
            return "err";
        }
    }
    public List<String> changeHairStyle2(ChangeHairStyle changeHairStyle) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.set("ailabapi-api-key", ailabToolsApiKey);
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("task_type", "async");
        body.add("auto", 1);
        body.add("hair_style", changeHairStyle.style);
        body.add("color", changeHairStyle.color);
        body.add("image", new FileSystemResource(convertToFile(changeHairStyle.image)));
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity("https://www.ailabapi.com/api/portrait/effects/hairstyle-editor-pro", requestEntity, String.class);
        String taskId = getTaskId(response.toString());
        return getTaskImages(taskId);
    }
    private File convertToFile(MultipartFile file) throws IOException {
        File convFile = new File(System.getProperty("java.io.tmpdir") + "/" + file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convFile)) {
            fos.write(file.getBytes());
        }
        return convFile;
    }
    private String getTaskId(String response){
        try {
            int jsonStartIndex = response.indexOf("{");
            if (jsonStartIndex == -1) {
                throw new LocalizedException("server.error");
            }
            String jsonString = response.substring(jsonStartIndex).trim();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonString);
            String taskId = rootNode.get("task_id").asText();
            return taskId;

        } catch (Exception e) {
            e.printStackTrace();
            throw new LocalizedException("server.error");
        }
    }
    public List<String> getTaskImages(String taskId) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("ailabapi-api-key", ailabToolsApiKey);
            String url =  "https://www.ailabapi.com/api/common/query-async-task-result?task_id=" + taskId;
            HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response.getBody());
            List<String> images = new ArrayList<>();
            JsonNode imagesNode = rootNode.path("data").path("images");
            String statusCode = rootNode.path("task_status").asText();
            if (!statusCode.equals("2")){
                Thread.sleep(1500);
                return getTaskImages(taskId);
            }
            if (imagesNode.isArray()) {
                for (JsonNode imageNode : imagesNode) {
                    images.add(imageNode.asText());
                }
            }
            return images;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
