package com.cobi.restclienttest;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class UserService {
  public static final String API_URL = "https://user-api.com";
  private final RestTemplate restTemplate;
  private final ObjectMapper objectMapper;

  /**
   * constructor
   */
  public UserService(RestTemplateBuilder restTemplateBuilder, ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
    DefaultUriBuilderFactory uriBuilder = new DefaultUriBuilderFactory(API_URL);

    restTemplate = restTemplateBuilder
      .uriTemplateHandler(uriBuilder)
      .defaultHeader("Authorization", "Basic SomeToken")
      .build();
  }

  /**
   * user 정보 조회
   */
  public UserInfo getUserInfo(String userId){
    String userApiPath = "/users" + userId;
    ParameterizedTypeReference<UserInfo> responseType = new ParameterizedTypeReference<>() {};

    ResponseEntity<UserInfo> exchange = restTemplate.exchange(
      userApiPath,
      HttpMethod.GET,
      null,
      responseType
    );

    UserInfo userInfo = exchange.getBody();

    return userInfo;
  }

  /**
   * user 연령 검색
   */
  public List<UserInfo> findUserListBy(Long age){
    String userApiPath = "/users";
    MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
    queryParams.add("age", String.valueOf(age));

    String urlWithQueryString = UriComponentsBuilder.fromUriString(userApiPath)
      .queryParams(queryParams)
      .build().encode()
      .toUriString();


    ParameterizedTypeReference<List<UserInfo>> responseType = new ParameterizedTypeReference<>() {};

    ResponseEntity<List<UserInfo>> exchange = restTemplate.exchange(
      urlWithQueryString,
      HttpMethod.GET,
      null,
      responseType
    );

    List<UserInfo> userInfoList = exchange.getBody();

    return userInfoList;
  }

  /**
   * user 생성
   */
  public UserInfo createUser(UserCreate userCreate){
    String userApiPath = "/users";

    Map<String, Object> userCreateMap = objectMapper.convertValue(userCreate, new TypeReference<>() {});

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<Map<String, Object>> request = new HttpEntity<>(userCreateMap, headers);

    ParameterizedTypeReference<UserInfo> responseType = new ParameterizedTypeReference<>() {};

    ResponseEntity<UserInfo> exchange = restTemplate.exchange(
      userApiPath,
      HttpMethod.POST,
      request,
      responseType
    );

    UserInfo createdUserInfo = exchange.getBody();

    return createdUserInfo;
  }
}
