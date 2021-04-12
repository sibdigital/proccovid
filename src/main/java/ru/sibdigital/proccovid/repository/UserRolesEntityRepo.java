package ru.sibdigital.proccovid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.sibdigital.proccovid.model.UserRolesEntity;

import java.util.List;

@Repository
public interface UserRolesEntityRepo extends JpaRepository<UserRolesEntity, Long> {

    @Query(name = "get_roles_by_user_id", nativeQuery = true)
    List<UserRolesEntity> getRolesByUserId(@Param("id_user") Long idUser);
}
