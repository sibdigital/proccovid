package ru.sibdigital.proccovid.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class RegMailingMessageDto {

    private Long id;
    private Long mailingId;
    private String sendingTime;
    private String message;
    private Short status;
}