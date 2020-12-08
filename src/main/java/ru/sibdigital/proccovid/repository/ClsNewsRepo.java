package ru.sibdigital.proccovid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sibdigital.proccovid.model.ClsNews;

import java.util.List;

@Repository
public interface ClsNewsRepo extends JpaRepository<ClsNews, Long> {
}