package ru.sibdigital.proccovid.model;

import javax.persistence.*;

@Entity
public class InspectionEntityForReport {
        @Id
        private Long id;
        private String organization;
        private String authority;

        public InspectionEntityForReport(Long id, String organization, String authority) {
                this.id = id;
                this.organization = organization;
                this.authority = authority;
        }

        public InspectionEntityForReport() {

        }

        public Long getId() {
                return id;
        }
        public void setId(Long id) {
                this.id = id;
        }

        public String getOrganization() {
                return organization;
        }
        public void setOrganization(String organization) {
                this.organization = organization;
        }

        public String getAuthority() {
                return authority;
        }
        public void setAuthority(String authority) {
                this.authority = authority;
        }
}
