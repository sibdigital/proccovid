package ru.sibdigital.proccovid.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sibdigital.proccovid.model.ClsUser;
import ru.sibdigital.proccovid.model.UserStatuses;
import ru.sibdigital.proccovid.repository.classifier.ClsUserRepo;
import ru.sibdigital.proccovid.utils.PasswordGenerator;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private ClsUserRepo clsUserRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

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
}
