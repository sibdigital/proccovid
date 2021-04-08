package ru.sibdigital.proccovid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.sibdigital.proccovid.model.ClsUser;
import ru.sibdigital.proccovid.model.RegUserRole;

import java.util.List;
import java.util.Set;

@Repository
public interface RegUserRoleRepo extends JpaRepository<RegUserRole, Long> {

    @Modifying
    @Transactional
    @Query(nativeQuery = true,
    value = "DELETE FROM reg_user_role WHERE id_user =:id_user AND id_role in (:id_role);")
    void deleteAllByUserIdAndRoleIds(@Param("id_user") Long idUser, @Param("id_role") Set<Long> roleIds);

    List<RegUserRole> findAllByUser(ClsUser clsUser);

}
