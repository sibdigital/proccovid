package ru.sibdigital.proccovid.repository.fias;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sibdigital.proccovid.model.fias.AddrObject;

@Repository
public interface AddrObjectRepo extends JpaRepository<AddrObject, Long> {
    AddrObject findAddrObjectByObjectid(Long objectid);
}
