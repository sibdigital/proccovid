package ru.sibdigital.proccovid.service;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ImportResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.sibdigital.proccovid.ProccovidApplication;
import ru.sibdigital.proccovid.dto.ClsTypeRequestDto;
import ru.sibdigital.proccovid.model.ClsTypeRequest;

import java.sql.Timestamp;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ProccovidApplication.class)
public class PrescriptionServiceTest {

    @Autowired
    PrescriptionService prescriptionService;

    @Test
    public void savePrescriptionTest() {

        ClsTypeRequestDto dto = ClsTypeRequestDto.builder()
//                .id()
                .activityKind("ActivityKind")
                .shortName("ShortName")
//                .department()
                .prescription("Prescription")
                .prescriptionLink("PrescriptionLink")
                .settings("Settings")
                .statusRegistration(1)
                .beginRegistration(new Timestamp(System.currentTimeMillis()))
                .endRegistration(new Timestamp(System.currentTimeMillis()))
                .statusVisible(1)
                .beginVisible(new Timestamp(System.currentTimeMillis()))
                .endVisible(new Timestamp(System.currentTimeMillis()))
                .sortWeight(10)
                .consent("Consent")
                .build();

        ClsTypeRequest clsTypeRequest = prescriptionService.saveClsTypeRequest(dto);

        Assertions.assertTrue(clsTypeRequest.getId() != null);
    }
}
