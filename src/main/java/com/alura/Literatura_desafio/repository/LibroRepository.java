package com.alura.Literatura_desafio.repository;

import com.alura.Literatura_desafio.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LibroRepository extends JpaRepository<Libro,Long> { //para guardar en la BD
    Optional<Libro> findByTitulo(String titulo);
    boolean existsByTituloIgnoreCase(String titulo);

    @Query("SELECT l FROM Libro l WHERE l.idioma = :idioma")
    List<Libro> findByIdioma(@Param("idioma") String idioma );

    long countByIdiomaIgnoreCase(String idioma);




}
