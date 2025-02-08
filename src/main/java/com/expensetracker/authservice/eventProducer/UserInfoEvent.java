package com.expensetracker.authservice.eventProducer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserInfoEvent {
    private String firstName;
    private String lastName;
    private String email;
    private Long phoneNumber;
    private String userId;
}
