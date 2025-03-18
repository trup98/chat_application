package com.learning.real_time_chat_application.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class ApiResponse {
    private int status;
    private String message;
    private Object data;
    @JsonIgnore
    private HttpStatus httpStatus;

    public ApiResponse(HttpStatus httpStatus, String message, Object data) {
        this.message = message;
        this.data = data;
        this.httpStatus = httpStatus;
        this.status = httpStatus.value();
    }
}
