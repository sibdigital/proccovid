package ru.sibdigital.proccovid.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "reg_user_role", schema = "public")
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class RegUserRole {
        @Id
        @Column(name = "id", nullable = false)
        @SequenceGenerator(name = "REG_USER_ROLE_GEN", sequenceName = "reg_user_role_id_seq", allocationSize = 1, schema = "public")
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "REG_USER_ROLE_GEN")
        private Long id;
        public Long getId() {return id;}
        public void setId(Long id) {this.id = id;}


        @OneToOne
        @JoinColumn(name = "id_user", referencedColumnName = "id")
        private ClsUser user;
        public ClsUser getUser() {return user;}
        public void setUser(ClsUser user) {this.user = user;}


        @OneToOne
        @JoinColumn(name = "id_role", referencedColumnName = "id")
        private ClsRole role;
        public ClsRole getRole() {return role;}
        public void setRole(ClsRole role) {this.role = role;}

        @Override
        public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                RegUserRole that = (RegUserRole) o;
                return Objects.equals(id, that.id) &&
                        Objects.equals(user, that.user) &&
                        Objects.equals(role, that.role);
        }

        @Override
        public int hashCode() {
                return Objects.hash(id, user, role);
        }
}
