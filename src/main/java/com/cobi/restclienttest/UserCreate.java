package com.cobi.restclienttest;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserCreate {
  private String userId;
  private Long age;
  private String name;
}
