package com.aluracursos.literalura.principal;

import com.aluracursos.literalura.model.Autor;
import com.aluracursos.literalura.model.Datos;
import com.aluracursos.literalura.model.DatosLibro;
import com.aluracursos.literalura.model.Libro;
import com.aluracursos.literalura.repository.IAutorRepository;
import com.aluracursos.literalura.repository.ILibroRepository;
import com.aluracursos.literalura.service.ConsumoAPI;
import com.aluracursos.literalura.service.ConvierteDatos;
import jakarta.transaction.Transactional;


import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;


public class Principal {
    private Scanner teclado = new Scanner(System.in);
    private ConsumoAPI consumoApi = new ConsumoAPI();
    private final String URL_BASE = "https://gutendex.com/books/";
    private ConvierteDatos conversor = new ConvierteDatos();
    private IAutorRepository iAutorRepository;
    private ILibroRepository iLibroRepository;


    public Principal(IAutorRepository iAutorRepository, ILibroRepository iLibroRepository) {
        this.iAutorRepository = iAutorRepository;
        this.iLibroRepository = iLibroRepository;
    }

    public void menu() {
        var opcion = -1;
        var menu = """
                -------------
                Elija la opcion a través de su numero:
                1 - Buscar libro por titulo
                2 - Listar libros registrados
                3 - Listar autores registrados
                4 - Listar autores vivos en un determinado año
                5 - Listar libros por idioma
                0 - Salir
                """;
        while (opcion != 0) {
            System.out.println(menu);
            try {
                opcion = Integer.valueOf(teclado.nextLine());
                switch (opcion) {
                    case 1:
                        buscarLibroPorTitulo();
                        break;

                    case 0:
                        System.out.println("Hasta la próxima");
                        break;
                    default:
                        System.out.println("Opcion no valida!");
                        break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Opcion no valida: " + e.getMessage());

            }
        }
    }

    private Datos getDatosLibro() {
        System.out.println("Escribe el nombre del libro que deseas buscar");
        var nombreLibro = teclado.nextLine();
        var json = consumoApi.obtenerDatos(URL_BASE + "?search=" + nombreLibro.replace(" ", "+"));
        Datos datos = conversor.obtenerDatos(json, Datos.class);
        return datos;
    }

    @Transactional
    private void buscarLibroPorTitulo() {
        Datos datos = getDatosLibro();
        List<DatosLibro> resultados = datos.resultados();
        if (!resultados.isEmpty()) {
            DatosLibro libroBuscado = resultados.get(0);
            String titulo = libroBuscado.titulo();

            //verificamos si ya existe el libro en la base de datos
            List<Libro> existenciaDelLibro = iLibroRepository.buscarLibroPorTitulo(titulo);
            if (!existenciaDelLibro.isEmpty()) {
                System.out.println("Libro ya registrado");
            } else {
                String autorNombre = libroBuscado.autores().stream().map(a -> a.nombre()).findFirst().orElse(null);
                Integer nacimientoAutor = libroBuscado.autores().stream().map(a -> a.nacimiento()).findFirst().orElse(null);
                Integer muerteAutor = libroBuscado.autores().stream().map(a -> a.muerte()).findFirst().orElse(null);
                if (autorNombre != null) {
                    Optional<Autor> autor = iAutorRepository.buscarAutorPorNombre(autorNombre);
                    // si al buscar el autor en la base de datos este no existe, entonces se registra
                    if (autor.isEmpty()) {
                        // primero es asignar los valores al modelo del autor
                        Autor nuevoAutor = new Autor();
                        nuevoAutor.setLibros(existenciaDelLibro);
                        nuevoAutor.setNombre(autorNombre);
                        nuevoAutor.setNacimiento(nacimientoAutor);
                        nuevoAutor.setMuerte(muerteAutor);
                        //se guarda en la base de datos
                        iAutorRepository.save(nuevoAutor);
                        //instanciamos un objeto de tipo libro para guardar el libro buscado
                        Libro libro = new Libro(libroBuscado);
                        //establecemos el autor del libro
                        libro.setAutor(nuevoAutor);
                        //guardamos el libro
                        iLibroRepository.save(libro);
                        //datos del libro guardado
                        System.out.println(
                                "\n----- LIBRO -----" +
                                        "\nTitulo: " + libroBuscado.titulo() +
                                        "\nAutor: " + libroBuscado.autores().stream()
                                        .map(a -> a.nombre()).limit(1).collect(Collectors.joining()) +
                                        "\nIdioma: " + libroBuscado.idiomas().stream().collect(Collectors.joining()) +
                                        "\nNumero de descargas: " + libroBuscado.descargas() +
                                        "\n-----------------\n"
                        );
                    } else {
                        Autor autorExistente = autor.get();
                        Libro libro = new Libro(libroBuscado);
                        libro.setAutor(autorExistente);
                        iLibroRepository.save(libro);
                        System.out.println(
                                "\n----- LIBRO -----" +
                                        "\nTitulo: " + libroBuscado.titulo() +
                                        "\nAutor: " + libroBuscado.autores().stream()
                                        .map(a -> a.nombre()).limit(1).collect(Collectors.joining()) +
                                        "\nIdioma: " + libroBuscado.idiomas().stream().collect(Collectors.joining()) +
                                        "\nNumero de descargas: " + libroBuscado.descargas() +
                                        "\n-----------------\n"
                        );

                    }
                } else {
                    System.out.println("Autor no encontrado");
                }
            }
        } else {
            System.out.println("libro no encontrado");
        }
    }
}
