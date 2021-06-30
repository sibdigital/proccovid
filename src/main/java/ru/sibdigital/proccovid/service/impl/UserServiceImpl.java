package ru.sibdigital.proccovid.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sibdigital.proccovid.config.ApplicationConstants;
import ru.sibdigital.proccovid.dto.ClsUserDto;
import ru.sibdigital.proccovid.dto.UserRolesEntityDto;
import ru.sibdigital.proccovid.model.ClsUser;
import ru.sibdigital.proccovid.model.UserStatuses;
import ru.sibdigital.proccovid.repository.classifier.ClsUserRepo;
import ru.sibdigital.proccovid.service.EmailService;
import ru.sibdigital.proccovid.service.UserService;
import ru.sibdigital.proccovid.utils.PasswordGenerator;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private ApplicationConstants applicationConstants;

    @Autowired
    private ClsUserRepo clsUserRepo;


    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RequestService requestService;

    @Autowired
    private EmailService emailService;

    @Value("${spring.mail.from}")
    private String fromAddress;

    @Override
    public ClsUser findUserByLogin(String login) {
        return clsUserRepo.findByLogin(login);
    }

    @Override
    @Transactional
    public String changeUserPassword(ClsUser clsUser) {
        if (clsUser != null) {
            String newPassword = PasswordGenerator.generatePassword(8);
            clsUser.setPassword(passwordEncoder.encode(newPassword));
            clsUser.setStatus(UserStatuses.NOT_ACTIVE.getValue());
            clsUserRepo.save(clsUser);
            return newPassword;
        }
        return null;
    }

    @Override
    //@Transactional
    public List<ClsUser> loadFromCSV(InputStream inputStream){
        final List<ClsUserDto> userDtoList = new BufferedReader(new InputStreamReader(inputStream))
                .lines()
                .map(this::buildUserDto)
                .collect(Collectors.toList());
        List<ClsUser> users = new ArrayList<>();

        for (ClsUserDto cud : userDtoList){
            try {
                if (cud != null) {
                    ClsUser clsUser = requestService.saveClsUser(cud);
                    users.add(clsUser);
                    emailService.sendSimpleMessage(cud.getEmail(),
                            "Учетные данные на портале " + applicationConstants.getApplicationName(),
                            buildEmailText(cud), fromAddress);
                }else{
                    users.add(null);
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                users.add(null);
            }
        }
        return users;
    }

    private ClsUserDto buildUserDto(String line){
        final String[] fields = line.split(";");
        ClsUserDto cud = null;
        if (fields.length >= 5 ) {

            Long departmentId = 0L;
            UserRolesEntityDto ured = null;

            try {
                Long roleId = Long.parseLong(fields[5].trim());
                ured = UserRolesEntityDto.builder()
                        .id(roleId)
                        .status(true)
                        .build();
            }catch (NumberFormatException e){
                log.error(e.getMessage(), e);
            }

            try {
                departmentId = Long.parseLong(fields[4].trim());
            }catch (NumberFormatException e){
                log.error(e.getMessage(), e);
            }

            cud = ClsUserDto.builder()
                    .lastname(fields[0].trim())
                    .firstname(fields[1].trim())
                    .patronymic(fields[2].trim())
                    .email(fields[3].trim())
                    .departmentId(departmentId)
                    .newPassword(PasswordGenerator.generatePassword(9))
                    .status(UserStatuses.NOT_ACTIVE.getValue())
                    .userRoles(List.of(ured))
                    .admin(false)
                    .login(fields[3].trim())
                    .build();
            if (fields.length >= 7 ){
                try {
                    Long districttId = Long.parseLong(fields[6].trim());
                    cud.setDistrictId(districttId);
                }catch (NumberFormatException e){
                    log.error(e.getMessage(), e);
                }
            }
        }
        return cud;
    }

    private String buildEmailText(ClsUserDto cud){
        return  "Ваши логин от личного кабинета на портале " + applicationConstants.getApplicationName() + ": "
                + cud.getLogin() + "\n"
                + "Ваш пароль: " + cud.getNewPassword() + "\n"
                + "Для входа из сети Интернет перейдите по адресу https://работающаябурятия.рф/коап ";
    }

}
