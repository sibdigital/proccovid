package ru.sibdigital.proccovid.model;

import javax.persistence.*;

@Entity
public class InspectionEntityForReport {
        @Id
        private Long id;
        private String organization;
        private String authority;
        private Integer totalOrganization;
        private Integer totalAuthority;

        public InspectionEntityForReport(Long id, String organization, String authority,
                                         Integer totalOrganization, Integer totalAuthority) {
                this.id = id;
                this.organization = organization;
                this.authority = authority;
                this.totalOrganization = totalOrganization;
                this.totalAuthority    = totalAuthority;
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

        public Integer getTotalOrganization() {
                return totalOrganization;
        }
        public void setTotalOrganization(Integer totalOrganization) {
                this.totalOrganization = totalOrganization;
        }

        public Integer getTotalAuthority() {
                return totalAuthority;
        }
        public void setTotalAuthority(Integer totalAuthority) {
                this.totalAuthority = totalAuthority;
        }
}
