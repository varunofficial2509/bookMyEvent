package com.BookMyEvent.bookMyEvent.dto;

import lombok.*;

import java.time.LocalDateTime;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ErrorResponseDTO {
    private String message;
    private LocalDateTime timestamp;
}
