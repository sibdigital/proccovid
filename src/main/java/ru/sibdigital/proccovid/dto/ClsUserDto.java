package ru.sibdigital.proccovid.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class ClsUserDto {

    private Long id;
    private Long departmentId;
    private String lastname;
    private String firstname;
    private String patronymic;
    private String login;
    private String newPassword;
    private Boolean admin;

}
