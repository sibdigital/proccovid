package ru.sibdigital.proccovid.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.sibdigital.proccovid.model.DocPerson;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class PersonDto {
    private String lastname;
    private String firstname;
    private String patronymic;
    //private Boolean isAgree;
    private String status;

   public DocPerson convertToPersonEntity(){
       return DocPerson.builder().firstname(this.firstname)
               .lastname(this.lastname)
               //.isAgree(this.isAgree)
               .patronymic(this.patronymic)
               .build();
   }

}
