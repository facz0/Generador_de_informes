package com.onpe.genereador_informes.controlador;

import com.onpe.genereador_informes.DAO.ContratoDao;
import com.onpe.genereador_informes.model.Contrato;
import com.onpe.genereador_informes.servicios.GeneradorWord;
import org.apache.pdfbox.multipdf.PDFMergerUtility;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DashboardController {

    private ContratoDao contratoDao;

    public DashboardController(){
        this.contratoDao = new ContratoDao();
    }

    public List<Contrato> obtenerContratosPorCargoArea(int idCargoArea) {
        return contratoDao.obtenerContratos().stream()
            .filter(c -> c.getIdCargoArea() == idCargoArea)
            .collect(java.util.stream.Collectors.toList());
    }

    public List<Contrato> obtenerDatosParaTabla(){
        return contratoDao.obtenerContratos();
    }

    public List<String> obtenerNombresCargos() {
        return new com.onpe.genereador_informes.DAO.CargoDAO().obtenerTodos()
            .stream().map(c -> c.getNombreCargo()).collect(java.util.stream.Collectors.toList());
    }

    public List<String> obtenerNombresAreas() {
        return new com.onpe.genereador_informes.DAO.AreaDAO().obtenerTodas()
            .stream().map(a -> a.getNombreArea()).collect(java.util.stream.Collectors.toList());
    }

    public List<String> obtenerNombresGerencias() {
        List<String> lista = new ArrayList<>();
        String sql = "SELECT nombre_gerencia FROM tb_gerencia ORDER BY nombre_gerencia";
        try {
            java.sql.ResultSet rs = com.onpe.genereador_informes.database.Conexion.obtenerConexion()
                .createStatement().executeQuery(sql);
            while (rs.next()) lista.add(rs.getString(1));
            rs.close();
        } catch (Exception e) {
            System.err.println("Error al obtener gerencias: " + e.getMessage());
        }
        return lista;
    }

    public List<Contrato> filtrarContratos(String dni, String nombre, String cargo, String gerencia, String area) {
        return contratoDao.obtenerContratos().stream().filter(c ->
            (dni == null || dni.isEmpty() || c.getEmpleado().getDni().toLowerCase().contains(dni.toLowerCase()))
            && (nombre == null || nombre.isEmpty() || (c.getEmpleado().getNombres() + " " + c.getEmpleado().getApellidos()).toLowerCase().contains(nombre.toLowerCase()))
            && (cargo == null || cargo.isEmpty() || c.getCargo().getNombreCargo().toLowerCase().contains(cargo.toLowerCase()))
            && (gerencia == null || gerencia.isEmpty() || c.getArea().getNombreArea().toLowerCase().contains(gerencia.toLowerCase()))
            && (area == null || area.isEmpty() || c.getCargo().getNombreCargo().toLowerCase().contains(area.toLowerCase()))
        ).collect(java.util.stream.Collectors.toList());
    }
    
    // Generar solo Informes de Actividades
    public boolean generarSoloInformesActividades() {
        List<Contrato> lista = contratoDao.obtenerContratos();
        return generarInformesSeleccionados(lista);
    }

    public boolean generarInformesSudime(List<Contrato> listaContratos) {
        String rutaPlantillaInf = "plantillas/INFORME DE ACTIVIDADES_formato.docx";
        String carpetaTempWord = "C:/Users/inesq/Documents/informess/SUDIME/temp/words/";
        String carpetaTempPdf = "C:/Users/inesq/Documents/informess/SUDIME/temp/pdfs/";
        String carpetaDestino = "C:/Users/inesq/Documents/informess/SUDIME/";

        new File(carpetaTempWord).mkdirs();
        new File(carpetaTempPdf).mkdirs();
        new File(carpetaDestino + "Informes_Actividades").mkdirs();

        GeneradorWord generador = new GeneradorWord();
        List<String> pdfs = new ArrayList<>();

        for (Contrato c : listaContratos) {
            String dni = c.getEmpleado().getDni();
            String word = carpetaTempWord + "INF_" + dni + ".docx";
            String pdf = carpetaTempPdf + "INF_" + dni + ".pdf";
            generador.generarDocumento(c, rutaPlantillaInf, word);
            convertirWordAPdfConSpire(word, pdf);
            pdfs.add(pdf);
        }

        unirPdfs(pdfs, carpetaDestino + "Informes_Actividades/SUDIME - INFORME DE ACTIVIDADES.pdf");
        return true;
    }

    public boolean generarFM38Sudime(List<Contrato> listaContratos) {
        String rutaPlantillaFM38 = "plantillas/FM38_formato.docx";
        String carpetaTempWord = "C:/Users/inesq/Documents/informess/SUDIME/temp/words/";
        String carpetaTempPdf = "C:/Users/inesq/Documents/informess/SUDIME/temp/pdfs/";
        String carpetaDestino = "C:/Users/inesq/Documents/informess/SUDIME/";

        new File(carpetaTempWord).mkdirs();
        new File(carpetaTempPdf).mkdirs();
        new File(carpetaDestino + "Formato_FM38").mkdirs();

        // Ordenar por número de contrato
        listaContratos = listaContratos.stream()
            .sorted((a, b) -> {
                try {
                    String[] pa = a.getNumeroContrato().split("-");
                    String[] pb = b.getNumeroContrato().split("-");
                    return Integer.compare(Integer.parseInt(pa[2].trim()), Integer.parseInt(pb[2].trim()));
                } catch (Exception e) {
                    return a.getNumeroContrato().compareTo(b.getNumeroContrato());
                }
            }).collect(java.util.stream.Collectors.toList());

        GeneradorWord generador = new GeneradorWord();
        List<String> pdfs = new ArrayList<>();

        for (Contrato c : listaContratos) {
            String dni = c.getEmpleado().getDni();
            String word = carpetaTempWord + "FM38_" + dni + ".docx";
            String pdf = carpetaTempPdf + "FM38_" + dni + ".pdf";
            generador.generarDocumento(c, rutaPlantillaFM38, word);
            convertirWordAPdfConSpire(word, pdf);
            pdfs.add(pdf);
        }

        unirPdfs(pdfs, carpetaDestino + "Formato_FM38/SUDIME - FORMATO FM38.pdf");
        return true;
    }

    public boolean generarInformesSeleccionados(List<Contrato> listaContratos) {
        String rutaPlantillaInf = "plantillas/INFORME DE ACTIVIDADES_formato.docx";
        String carpetaTempWord = "C:/Users/inesq/Documents/informess/temp/words/";
        String carpetaTempPdf = "C:/Users/inesq/Documents/informess/temp/pdfs/";
        String carpetaDestino = "C:/Users/inesq/Documents/informess/";

        new File(carpetaTempWord).mkdirs();
        new File(carpetaTempPdf).mkdirs();
        new File(carpetaDestino + "Informes_Actividades").mkdirs();

        GeneradorWord generador = new GeneradorWord();
        List<String> pdfsInformes = new ArrayList<>();

        for (Contrato c : listaContratos) {
            String dni = c.getEmpleado().getDni();
            String wordInf = carpetaTempWord + "INF_" + dni + ".docx";
            String pdfInf = carpetaTempPdf + "INF_" + dni + ".pdf";
            generador.generarDocumento(c, rutaPlantillaInf, wordInf);
            convertirWordAPdfConSpire(wordInf, pdfInf);
            pdfsInformes.add(pdfInf);
        }

        unirPdfs(pdfsInformes, carpetaDestino + "Informes_Actividades/INFORME DE ACTIVIDADES - MARZO.pdf");
        return true;
    }
    
    public boolean generarFM38Seleccionados(List<Contrato> listaContratos) {
        String rutaPlantillaFM38 = "plantillas/FM38_formato.docx";
        String carpetaTempWord = "C:/Users/inesq/Documents/informess/temp/words/";
        String carpetaTempPdf = "C:/Users/inesq/Documents/informess/temp/pdfs/";
        String carpetaDestino = "C:/Users/inesq/Documents/informess/";

        new File(carpetaTempWord).mkdirs();
        new File(carpetaTempPdf).mkdirs();
        new File(carpetaDestino + "Formato_FM38").mkdirs();

        // Ordenar por el número del contrato (3er segmento separado por "-")
        listaContratos = listaContratos.stream()
            .sorted((a, b) -> {
                try {
                    // Formato: N° LS-EG2026-1064-2026-ONPE → split por "-" → [N° LS, EG2026, 1064, 2026, ONPE]
                    String[] partsA = a.getNumeroContrato().split("-");
                    String[] partsB = b.getNumeroContrato().split("-");
                    int numA = Integer.parseInt(partsA[2].trim());
                    int numB = Integer.parseInt(partsB[2].trim());
                    return Integer.compare(numA, numB);
                } catch (Exception e) {
                    return a.getNumeroContrato().compareTo(b.getNumeroContrato());
                }
            })
            .collect(java.util.stream.Collectors.toList());

        GeneradorWord generador = new GeneradorWord();
        List<String> pdfsFM38 = new ArrayList<>();

        for (Contrato c : listaContratos) {
            String dni = c.getEmpleado().getDni();
            String wordFM = carpetaTempWord + "FM38_" + dni + ".docx";
            String pdfFM = carpetaTempPdf + "FM38_" + dni + ".pdf";
            generador.generarDocumento(c, rutaPlantillaFM38, wordFM);
            convertirWordAPdfConSpire(wordFM, pdfFM);
            pdfsFM38.add(pdfFM);
        }

        unirPdfs(pdfsFM38, carpetaDestino + "Formato_FM38/FORMATO fm38 - MARZO.pdf");
        return true;
    }

    // Generar solo FM38
    public boolean generarSoloFM38() {
        String rutaPlantillaFM38 = "plantillas/FM38_formato.docx";
        String carpetaTempWord = "C:/Users/inesq/Documents/informess/temp/words/";
        String carpetaTempPdf = "C:/Users/inesq/Documents/informess/temp/pdfs/";
        String carpetaDestino = "C:/Users/inesq/Documents/informess/";

        new File(carpetaTempWord).mkdirs();
        new File(carpetaTempPdf).mkdirs();
        new File(carpetaDestino + "Formato_FM38").mkdirs();

        GeneradorWord generador = new GeneradorWord();
        List<Contrato> listaContratos = contratoDao.obtenerContratos();
        int limite = Math.min(10, listaContratos.size());
        listaContratos = listaContratos.subList(0, limite);
        List<String> pdfsFM38 = new ArrayList<>();

        for (Contrato c : listaContratos) {
            String dni = c.getEmpleado().getDni();
            String wordFM = carpetaTempWord + "FM38_" + dni + ".docx";
            String pdfFM = carpetaTempPdf + "FM38_" + dni + ".pdf";
            generador.generarDocumento(c, rutaPlantillaFM38, wordFM);
            convertirWordAPdfConSpire(wordFM, pdfFM);
            pdfsFM38.add(pdfFM);
        }

        unirPdfs(pdfsFM38, carpetaDestino + "Formato_FM38/FORMATO fm38 - MARZO.pdf");
        return true;
    }

    public void generarTodosLosInformes() {
        String rutaPlantillaInf = "plantillas/INFORME DE ACTIVIDADES_formato.docx";
        String rutaPlantillaFM38 = "plantillas/FM38_formato.docx";
        
        String carpetaTempWord = "C:/Users/inesq/Documents/informess/temp/words/";
        String carpetaTempPdf = "C:/Users/inesq/Documents/informess/temp/pdfs/";
        String carpetaDestino = "C:/Users/inesq/Documents/informess/";
        String carpetaSUDIME = "C:/Users/inesq/Documents/informess/SUDIME/";

        new File(carpetaTempWord).mkdirs();
        new File(carpetaTempPdf).mkdirs();
        new File(carpetaSUDIME + "Informes_Actividades").mkdirs();
        new File(carpetaSUDIME + "Formato_FM38").mkdirs();

        GeneradorWord generador = new GeneradorWord();
        List<Contrato> todosLosContratos = contratoDao.obtenerContratos();
        
        // Filtrar solo contratos con id_cargo = 5
        List<Contrato> listaContratos = new ArrayList<>();
        for (Contrato c : todosLosContratos) {
            if (c.getCargo().getIdCargo() == 5) {
                listaContratos.add(c);
            }
        }

        List<String> pdfsInformes = new ArrayList<>();
        List<String> pdfsFM38 = new ArrayList<>();

        System.out.println("Iniciando generación de " + listaContratos.size() + " informes SUDIME (cargo ID 5)...");

        for (Contrato c : listaContratos) {
            String dni = c.getEmpleado().getDni();

            String wordInf = carpetaTempWord + "INF_" + dni + ".docx";
            String pdfInf = carpetaTempPdf + "INF_" + dni + ".pdf";

            String wordFM = carpetaTempWord + "FM38_" + dni + ".docx";
            String pdfFM = carpetaTempPdf + "FM38_" + dni + ".pdf";

            // 1. Generar Word con Apache POI
            generador.generarDocumento(c, rutaPlantillaInf, wordInf);
            generador.generarDocumento(c, rutaPlantillaFM38, wordFM);

            // 2. Convertir a PDF con Spire
            convertirWordAPdfConSpire(wordInf, pdfInf);
            convertirWordAPdfConSpire(wordFM, pdfFM);

            pdfsInformes.add(pdfInf);
            pdfsFM38.add(pdfFM);
        }

        System.out.println("Generando informes consolidados SUDIME...");

        // 3. Unir en carpeta SUDIME
        unirPdfs(pdfsInformes, carpetaSUDIME + "Informes_Actividades/SUDIME - INFORME DE ACTIVIDADES - MARZO.pdf");
        unirPdfs(pdfsFM38, carpetaSUDIME + "Formato_FM38/SUDIME - FORMATO fm38 - MARZO.pdf");

        System.out.println("PROCESO TERMINADO");

    }

    //conversión
    private void convertirWordAPdfConSpire(String rutaWord, String rutaPdf) {
        try {
            com.spire.doc.Document doc = new com.spire.doc.Document();
            doc.loadFromFile(rutaWord);
            doc.saveToFile(rutaPdf, com.spire.doc.FileFormat.PDF);
            doc.close();
        } catch (Exception e) {
            System.err.println("Error de conversión: " + rutaWord);
        }
    }

    //union
    private void unirPdfs(List<String> rutasPdf, String rutaSalida) {
        try {
            PDFMergerUtility merger = new PDFMergerUtility();
            merger.setDestinationFileName(rutaSalida);
            for (String ruta : rutasPdf) {
                merger.addSource(new File(ruta));
            }
            merger.mergeDocuments(null);
        } catch (Exception e) {
            System.err.println("❌ Error uniendo PDFs: " + e.getMessage());
        }
    }

}

