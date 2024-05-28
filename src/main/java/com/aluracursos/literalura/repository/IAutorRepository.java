package com.aluracursos.literalura.repository;

import com.aluracursos.literalura.model.Autor;
import com.aluracursos.literalura.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface IAutorRepository extends JpaRepository<Autor,Long> {
    @Query("SELECT a FROM Autor a WHERE a.nombre = :nombre")
    Optional<Autor> buscarAutorPorNombre(String nombre);

    @Query("SELECT a FROM Autor a")
    List<Autor> buscarListaDeAutores();

}
