package com.aluracursos.literalura.model;

import jakarta.persistence.*;
import java.util.List;
import java.util.OptionalDouble;

@Entity
@Table(name="tbl_libros")
public class Libro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String titulo;
    @ManyToOne//(cascade = CascadeType.PERSIST)
    private Autor autor;
    private String idioma;
    private Double descargas;
    public Libro() {
    }

    public Libro(DatosLibro datosLibro) {
        this.titulo = datosLibro.titulo();
        this.autor = datosLibro.autores() != null && !datosLibro.autores().isEmpty() ? new Autor(datosLibro.autores().get(0)) : null;
        this.idioma = datosLibro.idiomas() != null && !datosLibro.idiomas().isEmpty() ? datosLibro.idiomas().get(0) : null;
        this.descargas = datosLibro.descargas() != null ? datosLibro.descargas().doubleValue() : null;
    }

    @Override
    public String toString() {
        return
                "Título: " + titulo + "\n" + "Autor: " + autor.getNombre() + "\n" +
                "Idioma: " + idioma + "\n" +
                "Descargas: " + descargas + "\n";
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getTitulo() {
        return titulo;
    }
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    public Autor getAutor() {
        return autor;
    }
    public void setAutor(Autor autor) {
        this.autor = autor;
    }
    public String getIdioma() {
        return idioma;
    }
    public void setIdioma(String idioma) {
        this.idioma = idioma;
    }
    public Double getDescargas() {
        return descargas;
    }
    public void setDescargas(Double descargas) {
        this.descargas = descargas;
    }
}