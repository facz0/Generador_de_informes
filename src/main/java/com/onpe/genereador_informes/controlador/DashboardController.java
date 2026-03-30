package com.onpe.genereador_informes.controlador;

import com.onpe.genereador_informes.DAO.ContratoDao;
import com.onpe.genereador_informes.model.Contrato;
import com.onpe.genereador_informes.servicios.GeneradorWord;
import org.apache.pdfbox.multipdf.PDFMergerUtility;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DashboardController {

    private static final int ID_CARGO_AREA_SUDIME = 58;

    private static final String BASE_DIR = System.getProperty("user.home") + File.separator + "Documents" + File.separator + "Generador_Informes" + File.separator;
    private static final String BASE_DIR_SUDIME = BASE_DIR + "SUDIME" + File.separator;

    private ContratoDao contratoDao;

    public DashboardController() {
        this.contratoDao = new ContratoDao();
    }

    public List<Contrato> obtenerContratosPorCargoArea(int idCargoArea) {
        return contratoDao.obtenerContratos().stream()
            .filter(c -> c.getPersonal().getCargoArea().getIdCargoArea() == idCargoArea)
            .collect(Collectors.toList());
    }

    public List<Contrato> obtenerDatosParaTabla() {
        return contratoDao.obtenerContratos().stream()
            .filter(c -> c.getPersonal().getCargoArea().getIdCargoArea() != ID_CARGO_AREA_SUDIME)
            .collect(Collectors.toList());
    }

    public List<String> obtenerNombresCargos() {
        return contratoDao.obtenerContratos().stream()
            .filter(c -> c.getPersonal().getCargoArea().getIdCargoArea() != ID_CARGO_AREA_SUDIME)
            .map(c -> c.getPersonal().getCargoArea().getCargo().getNombreCargo())
            .distinct()
            .sorted()
            .collect(Collectors.toList());
    }

    public List<String> obtenerNombresAreas() {
        return new com.onpe.genereador_informes.DAO.AreaDAO().obtenerTodas()
            .stream().map(a -> a.getNombreArea()).collect(Collectors.toList());
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
        return contratoDao.obtenerContratos().stream()
            .filter(c -> c.getPersonal().getCargoArea().getIdCargoArea() != ID_CARGO_AREA_SUDIME)
            .filter(c ->
                (dni == null || dni.isEmpty() || c.getPersonal().getDni().toLowerCase().contains(dni.toLowerCase()))
                && (nombre == null || nombre.isEmpty() || (c.getPersonal().getNombre() + " " + c.getPersonal().getApellido()).toLowerCase().contains(nombre.toLowerCase()))
                && (cargo == null || cargo.isEmpty() || c.getPersonal().getCargoArea().getCargo().getNombreCargo().toLowerCase().contains(cargo.toLowerCase()))
                && (gerencia == null || gerencia.isEmpty() || c.getPersonal().getGerencia().getNombreGerencia().toLowerCase().contains(gerencia.toLowerCase()))
                && (area == null || area.isEmpty() || c.getPersonal().getCargoArea().getArea().getNombreArea().toLowerCase().contains(area.toLowerCase()))
            ).collect(Collectors.toList());
    }

    public List<String> obtenerNombresOdpesSudime() {
        return contratoDao.obtenerContratos().stream()
            .filter(c -> c.getPersonal().getCargoArea().getIdCargoArea() == ID_CARGO_AREA_SUDIME)
            .filter(c -> c.getPersonal().getOdpe() != null)
            .map(c -> c.getPersonal().getOdpe().getNombreOdpe())
            .distinct()
            .sorted()
            .collect(Collectors.toList());
    }

    // ===== INFORMES GENERALES (excluye SUDIME) =====

    public boolean generarInformes(List<Contrato> listaContratos) {
        String rutaPlantilla   = "plantillas/INFORME DE ACTIVIDADES_formato.docx";
        String carpetaTempWord = BASE_DIR + "temp" + File.separator + "words" + File.separator;
        String carpetaTempPdf  = BASE_DIR + "temp" + File.separator + "pdfs" + File.separator;
        String carpetaDestino  = BASE_DIR;

        if (listaContratos == null || listaContratos.isEmpty()) {
            listaContratos = obtenerDatosParaTabla();
        } else {
            listaContratos = listaContratos.stream()
                .filter(c -> c.getPersonal().getCargoArea().getIdCargoArea() != ID_CARGO_AREA_SUDIME)
                .collect(Collectors.toList());
        }
        listaContratos = listaContratos.stream()
            .filter(c -> !"INACTIVO".equalsIgnoreCase(c.getPersonal().getEstado()))
            .collect(Collectors.toList());
        if (listaContratos.isEmpty()) return false;

        new File(carpetaTempWord).mkdirs();
        new File(carpetaTempPdf).mkdirs();
        new File(carpetaDestino + "Informes_Actividades").mkdirs();

        GeneradorWord generador = new GeneradorWord();
        List<String> pdfs = new ArrayList<>();

        for (Contrato c : listaContratos) {
            String dni = c.getPersonal().getDni();
            String word = carpetaTempWord + "INF_" + dni + ".docx";
            String pdf  = carpetaTempPdf  + "INF_" + dni + ".pdf";
            generador.generarDocumento(c, rutaPlantilla, word);
            convertirWordAPdfConSpire(word, pdf);
            pdfs.add(pdf);
        }

        return unirPdfs(pdfs, carpetaDestino + "Informes_Actividades/INFORME DE ACTIVIDADES - MARZO.pdf");
    }

    public boolean generarFM38(List<Contrato> listaContratos) {
        String rutaPlantilla   = "plantillas/FM38_formato.docx";
        String carpetaTempWord = BASE_DIR + "temp" + File.separator + "words" + File.separator;
        String carpetaTempPdf  = BASE_DIR + "temp" + File.separator + "pdfs" + File.separator;
        String carpetaDestino  = BASE_DIR;

        if (listaContratos == null || listaContratos.isEmpty()) {
            listaContratos = obtenerDatosParaTabla();
        } else {
            listaContratos = listaContratos.stream()
                .filter(c -> c.getPersonal().getCargoArea().getIdCargoArea() != ID_CARGO_AREA_SUDIME)
                .collect(Collectors.toList());
        }
        listaContratos = listaContratos.stream()
            .filter(c -> !"INACTIVO".equalsIgnoreCase(c.getPersonal().getEstado()))
            .collect(Collectors.toList());
        if (listaContratos.isEmpty()) return false;

        listaContratos = listaContratos.stream()
            .sorted((a, b) -> {
                try {
                    String[] pa = a.getNumeroContrato().split("-");
                    String[] pb = b.getNumeroContrato().split("-");
                    return Integer.compare(Integer.parseInt(pa[2].trim()), Integer.parseInt(pb[2].trim()));
                } catch (Exception e) {
                    return a.getNumeroContrato().compareTo(b.getNumeroContrato());
                }
            }).collect(Collectors.toList());

        new File(carpetaTempWord).mkdirs();
        new File(carpetaTempPdf).mkdirs();
        new File(carpetaDestino + "Formato_FM38").mkdirs();

        GeneradorWord generador = new GeneradorWord();
        List<String> pdfs = new ArrayList<>();

        for (Contrato c : listaContratos) {
            String dni = c.getPersonal().getDni();
            String word = carpetaTempWord + "FM38_" + dni + ".docx";
            String pdf  = carpetaTempPdf  + "FM38_" + dni + ".pdf";
            generador.generarDocumento(c, rutaPlantilla, word);
            convertirWordAPdfConSpire(word, pdf);
            pdfs.add(pdf);
        }

        return unirPdfs(pdfs, carpetaDestino + "Formato_FM38/FORMATO FM38 - MARZO.pdf");
    }

    // ===== SUDIME =====

    public boolean generarInformesSudime(List<Contrato> listaContratos) {
        String rutaPlantilla   = "plantillas/INFORME DE ACTIVIDADES_formato.docx";
        String carpetaTempWord = BASE_DIR_SUDIME + "temp" + File.separator + "words" + File.separator;
        String carpetaTempPdf  = BASE_DIR_SUDIME + "temp" + File.separator + "pdfs" + File.separator;
        String carpetaDestino  = BASE_DIR_SUDIME;

        if (listaContratos == null || listaContratos.isEmpty()) {
            listaContratos = obtenerContratosPorCargoArea(ID_CARGO_AREA_SUDIME);
        }
        listaContratos = listaContratos.stream()
            .filter(c -> !"INACTIVO".equalsIgnoreCase(c.getPersonal().getEstado()))
            .collect(Collectors.toList());
        if (listaContratos.isEmpty()) return false;

        new File(carpetaTempWord).mkdirs();
        new File(carpetaTempPdf).mkdirs();
        new File(carpetaDestino + "Informes_Actividades").mkdirs();

        GeneradorWord generador = new GeneradorWord();
        List<String> pdfs = new ArrayList<>();

        for (Contrato c : listaContratos) {
            String dni = c.getPersonal().getDni();
            String word = carpetaTempWord + "INF_" + dni + ".docx";
            String pdf  = carpetaTempPdf  + "INF_" + dni + ".pdf";
            generador.generarDocumento(c, rutaPlantilla, word);
            convertirWordAPdfConSpire(word, pdf);
            pdfs.add(pdf);
        }

        return unirPdfs(pdfs, carpetaDestino + "Informes_Actividades/SUDIME - INFORME DE ACTIVIDADES.pdf");
    }

    public boolean generarFM38Sudime(List<Contrato> listaContratos) {
        String rutaPlantilla   = "plantillas/FM38_formato.docx";
        String carpetaTempWord = BASE_DIR_SUDIME + "temp" + File.separator + "words" + File.separator;
        String carpetaTempPdf  = BASE_DIR_SUDIME + "temp" + File.separator + "pdfs" + File.separator;
        String carpetaDestino  = BASE_DIR_SUDIME;

        if (listaContratos == null || listaContratos.isEmpty()) {
            listaContratos = obtenerContratosPorCargoArea(ID_CARGO_AREA_SUDIME);
        }
        listaContratos = listaContratos.stream()
            .filter(c -> !"INACTIVO".equalsIgnoreCase(c.getPersonal().getEstado()))
            .collect(Collectors.toList());
        if (listaContratos.isEmpty()) return false;

        listaContratos = listaContratos.stream()
            .sorted((a, b) -> {
                try {
                    String[] pa = a.getNumeroContrato().split("-");
                    String[] pb = b.getNumeroContrato().split("-");
                    return Integer.compare(Integer.parseInt(pa[2].trim()), Integer.parseInt(pb[2].trim()));
                } catch (Exception e) {
                    return a.getNumeroContrato().compareTo(b.getNumeroContrato());
                }
            }).collect(Collectors.toList());

        new File(carpetaTempWord).mkdirs();
        new File(carpetaTempPdf).mkdirs();
        new File(carpetaDestino + "Formato_FM38").mkdirs();

        GeneradorWord generador = new GeneradorWord();
        List<String> pdfs = new ArrayList<>();

        for (Contrato c : listaContratos) {
            String dni = c.getPersonal().getDni();
            String word = carpetaTempWord + "FM38_" + dni + ".docx";
            String pdf  = carpetaTempPdf  + "FM38_" + dni + ".pdf";
            generador.generarDocumento(c, rutaPlantilla, word);
            convertirWordAPdfConSpire(word, pdf);
            pdfs.add(pdf);
        }

        return unirPdfs(pdfs, carpetaDestino + "Formato_FM38/SUDIME - FORMATO FM38.pdf");
    }

    // ===== UTILIDADES =====

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

    private boolean unirPdfs(List<String> rutasPdf, String rutaSalida) {
        try {
            PDFMergerUtility merger = new PDFMergerUtility();
            merger.setDestinationFileName(rutaSalida);
            for (String ruta : rutasPdf) {
                merger.addSource(new File(ruta));
            }
            merger.mergeDocuments(null);
            return true;
        } catch (Exception e) {
            System.err.println("Error uniendo PDFs: " + e.getMessage());
            return false;
        }
    }
}
