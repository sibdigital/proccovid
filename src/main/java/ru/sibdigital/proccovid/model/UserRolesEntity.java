package ru.sibdigital.proccovid.model;

import javax.persistence.*;

@Entity
@NamedNativeQuery(
        name = "get_roles_by_user_id",
        query =
                "WITH\n" +
                        "   current_roles AS (\n" +
                        "       SELECT id_role\n" +
                        "       FROM reg_user_role\n" +
                        "       WHERE id_user = :id_user\n" +
                        "   )\n" +
                        "SELECT id, name,\n" +
                        "       CASE WHEN current_roles.id_role IS NULL THEN FALSE\n" +
                        "            ELSE TRUE\n" +
                        "           END status\n" +
                        "FROM cls_role\n" +
                        "         LEFT JOIN current_roles ON cls_role.id = current_roles.id_role",
        resultSetMapping = "user_role_entity_result"
)
@SqlResultSetMapping(
        name = "user_role_entity_result",
        entities = @EntityResult(
                entityClass  = ru.sibdigital.proccovid.model.UserRolesEntity.class,
                fields = {
                        @FieldResult(name = "id", column = "id"),
                        @FieldResult(name = "name", column = "name"),
                        @FieldResult(name = "status", column = "status")
                }
        )
)
public class UserRolesEntity {
        @Id
        private Long id; // role id
        private String name; // role name
        private Boolean status; // есть ли данная роль у пользователя

        public UserRolesEntity(Long id, String name, Boolean status) {
                this.id = id;
                this.name = name;
                this.status = status;
        }

        public UserRolesEntity() {

        }

        public Long getId() {
                return id;
        }

        public void setId(Long id) {
                this.id = id;
        }

        public String getName() {
                return name;
        }

        public void setName(String name) {
                this.name = name;
        }

        public Boolean getStatus() {
                return status;
        }

        public void setStatus(Boolean status) {
                this.status = status;
        }
}
