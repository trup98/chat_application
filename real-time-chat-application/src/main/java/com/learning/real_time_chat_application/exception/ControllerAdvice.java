package com.learning.real_time_chat_application.exception;


import com.learning.real_time_chat_application.dto.response.ApiResponse;
import com.learning.real_time_chat_application.dto.response.ErrorDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class ControllerAdvice {


    /**
     * <p>
     * This method handles Exception and gives custom response
     * </p>
     *
     * @param req httpServletRequest request
     * @param e   Exception
     * @return ResponseEntity &lt;ApiResponse&gt;
     * @see Exception
     * @see ApiResponse
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleException(HttpServletRequest req, Exception e) {
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        ErrorDTO errorDTO = new ErrorDTO(httpStatus, new Date().getTime(),
                "Something went wrong", req.getServletPath());
        ApiResponse apiResponse = new ApiResponse(httpStatus, e.getMessage(), errorDTO);
        log.error("handleException ::", e);
        return ResponseEntity.status(httpStatus).body(apiResponse);
    }

    /**
     * <p>
     * This Method handles CustomException and returns ApiResponse
     * </p>
     *
     * @param req httpServletRequest request
     * @param e   CustomException
     * @return ResponseEntity &lt;ApiResponse&gt;
     * @see CustomException
     * @see ApiResponse
     */
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse> handleCustomException(HttpServletRequest req, CustomException e) {
        HttpStatus httpStatus = e.getHttpStatus();
        ErrorDTO errorDTO = new ErrorDTO(httpStatus, new Date().getTime(), e.getMessage(), req.getServletPath());
        errorDTO.setError(e.getMessage());
        errorDTO.setMessage(httpStatus.name());
        ApiResponse apiResponse = new ApiResponse(httpStatus, e.getMessage(), errorDTO);
        log.error("handleCustomException :: ", e);
        return ResponseEntity.status(httpStatus).body(apiResponse);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse> handleAccessDeniedException(HttpServletRequest req, AccessDeniedException e) {
        HttpStatus httpStatus = HttpStatus.FORBIDDEN;
        ErrorDTO errorDTO = new ErrorDTO(httpStatus, new Date().getTime(), e.getMessage(), req.getServletPath());
        errorDTO.setError(e.getMessage());
        errorDTO.setMessage(httpStatus.name());
        ApiResponse apiResponse = new ApiResponse(httpStatus, e.getMessage(), errorDTO);
        log.error("handleAccessDeniedException :: ", e);
        return ResponseEntity.status(httpStatus).body(apiResponse);
    }

    /**
     * <p>
     * This handles IllegalArgumentException and gives custom response
     * </p>
     *
     * @param req HttpServletRequest   request
     * @param e   IllegalArgumentException
     * @return ResponseEntity &lt;ApiResponse&gt;
     * @see IllegalArgumentException
     * @see ApiResponse
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse> handleIllegalArgumentException(HttpServletRequest req,
                                                                      IllegalArgumentException e) {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        ErrorDTO errorDTO = new ErrorDTO(httpStatus, new Date().getTime(),
                "Something went wrong", req.getServletPath());
        ApiResponse apiResponse = new ApiResponse(httpStatus, e.getMessage(), errorDTO);
        log.error("handleIllegalArgumentException :: ", e);
        return ResponseEntity.status(httpStatus).body(apiResponse);
    }

    /**
     * <p>
     * This handles MethodArgumentNotValidException and gives custom response which
     * contains list of errors
     * </p>
     *
     * @param req httpServletRequest request
     * @param e   MethodArgumentNotValidException
     * @return ResponseEntity &lt;ApiResponse&gt;
     * @see MethodArgumentNotValidException
     * @see ApiResponse
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleMethodArgumentNotValidException(HttpServletRequest req,
                                                                             MethodArgumentNotValidException e) {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        List<String> errorMessages = new ArrayList<>();
        e.getBindingResult().getAllErrors().forEach(error -> errorMessages.add(error.getDefaultMessage()));
        ErrorDTO errorDTO = new ErrorDTO(httpStatus, new Date().getTime(), httpStatus.name(), req.getServletPath(),
                errorMessages);
        ApiResponse apiResponse = new ApiResponse(httpStatus, httpStatus.name(), errorDTO);
        log.error("handleMethodArgumentNotValidException :: ", e);
        return ResponseEntity.status(httpStatus).body(apiResponse);
    }


}
