package ru.sibdigital.proccovid.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class UserRolesEntityDto {
        private Long id;
        private String name;
        private Boolean status;
}
