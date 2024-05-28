package com.aluracursos.literalura.repository;

import com.aluracursos.literalura.model.Autor;
import com.aluracursos.literalura.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ILibroRepository extends JpaRepository<Libro,Long> {

    //primero buscar el libro
    @Query("SELECT l FROM Libro l WHERE l.titulo = :titulo")
    List<Libro> buscarLibroPorTitulo(String titulo);

    @Query("SELECT l FROM Libro l WHERE l.titulo = :titulo AND l.autor = :autor")
    List<Libro> buscarLibroPorTituloYAutor(String titulo, Autor autor);

}
