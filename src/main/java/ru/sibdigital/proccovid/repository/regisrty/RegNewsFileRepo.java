package ru.sibdigital.proccovid.repository.regisrty;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sibdigital.proccovid.model.RegNewsFile;

import java.util.List;


@Repository
public interface RegNewsFileRepo extends JpaRepository<RegNewsFile, Long> {
    List<RegNewsFile> findRegNewsFileByNews_IdAndIsDeleted(long id_news, boolean deleted);
}