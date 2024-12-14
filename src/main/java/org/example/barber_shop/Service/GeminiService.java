package org.example.barber_shop.Service;

import org.example.barber_shop.DTO.AI.Request;
import org.example.barber_shop.Exception.LocalizedException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GeminiService {
    @Value("${gemini.api.key}")
    private String apiKey;
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
}
