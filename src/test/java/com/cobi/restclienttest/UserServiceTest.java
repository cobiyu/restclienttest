package com.cobi.restclienttest;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

import java.net.URI;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.web.util.UriComponentsBuilder;

@RestClientTest( {UserService.class} )
class UserServiceTest {
  @Autowired
  private UserService userService;

  @Autowired
  private MockRestServiceServer mockServer;

  @DisplayName("apiUserGetTest")
  @Test
  void getUserInfoTest(){
    // given
    String userId = "someUserId";
    
    String expectedApiUrl = UserService.API_URL + "/users" + userId;
    int expectedAge = 32;
    String expectedName = "testName";
    String expectedJsonResponse = "{\"no\":16,\"user_id\":\""+userId+"\",\"age\":"+expectedAge+",\"name\":\""+expectedName+"\"}";

    mockServer
      .expect(requestTo(expectedApiUrl))
      .andExpect(method(HttpMethod.GET))
      .andRespond(withSuccess(expectedJsonResponse, MediaType.APPLICATION_JSON));

    // when
    UserInfo userInfo = userService.getUserInfo(userId);

    // then
    assertEquals(userInfo.getUserId(), userId);
    assertEquals(userInfo.getAge(), expectedAge);
    assertEquals(userInfo.getName(), expectedName);
  }
  
  @Test
  void findUserListByAgeTest(){
    Long testAge = 30L;

    String expectedApiUrl = UserService.API_URL + "/users";
    URI uri = UriComponentsBuilder.fromUriString(expectedApiUrl)
      .queryParam("age", testAge)
      .build().encode()
      .toUri();
    
    String expectedUserId = "someId";
    String expectedName = "testName";
    String expectedJsonResponse = "[{\"no\":16,\"user_id\":\""+expectedUserId+"\",\"age\":"+testAge+",\"name\":\""+expectedName+"\"}]";
    
    mockServer
      .expect(requestTo(uri))
      .andExpect(method(HttpMethod.GET))
      .andRespond(withSuccess(expectedJsonResponse, MediaType.APPLICATION_JSON));

    List<UserInfo> userList = userService.findUserListBy(testAge);

    System.out.println(userList);
  }

  @DisplayName("apiUserCreateTest")
  @Test
  void createUserTest(){
    // given
    String expectedApiUrl = UserService.API_URL + "/users";
    String expectedUserId = "someId";
    int expectedAge = 32;
    String expectedName = "testName";
    String expectedJsonRequest = "{\"user_id\":\""+expectedUserId+"\",\"age\":"+expectedAge+",\"name\":\""+expectedName+"\"}";
    String expectedJsonResponse = "{\"no\":16,\"user_id\":\""+expectedUserId+"\",\"age\":"+expectedAge+",\"name\":\""+expectedName+"\"}";
    UserCreate userCreate = UserCreate.builder()
      .userId(expectedUserId)
      .age((long)expectedAge)
      .name(expectedName)
      .build();

    mockServer
      .expect(requestTo(expectedApiUrl))
      .andExpect(method(HttpMethod.POST))
      .andExpect(MockRestRequestMatchers.content().json(expectedJsonRequest))
      .andRespond(withSuccess(expectedJsonResponse, MediaType.APPLICATION_JSON));

    // when
    UserInfo userInfo = userService.createUser(userCreate);

    // then
    assertEquals(userInfo.getUserId(), expectedUserId);
    assertEquals(userInfo.getAge(), expectedAge);
    assertEquals(userInfo.getName(), expectedName);
  }
}
