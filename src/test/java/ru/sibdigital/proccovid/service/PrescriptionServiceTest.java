package ru.sibdigital.proccovid.service;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.sibdigital.proccovid.ProccovidApplication;
import ru.sibdigital.proccovid.dto.ClsPrescriptionDto;
import ru.sibdigital.proccovid.model.ClsPrescription;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ProccovidApplication.class)
public class PrescriptionServiceTest {

    @Autowired
    PrescriptionService prescriptionService;

    @Test
    public void savePrescriptionTest() {

        ClsPrescriptionDto dto = ClsPrescriptionDto.builder()
//                .id()
                .name("ShortName")
//                .department()
                .description("Description")
                .build();

        ClsPrescription clsPrescription = prescriptionService.savePrescription(dto);

        Assertions.assertTrue(clsPrescription.getId() != null);
    }
}
