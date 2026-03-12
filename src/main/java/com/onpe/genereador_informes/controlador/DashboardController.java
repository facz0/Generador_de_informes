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

    public List<Contrato> obtenerDatosParaTabla(){
        return contratoDao.obtenerContratos();
    }

    public void generarInformeActividades(Contrato contratoSeleccionado){

    }

    public void generarfm38(Contrato contratoSeleccionado){

    }
    
    // Generar solo Informes de Actividades
    public void generarSoloInformesActividades() {
        String rutaPlantillaInf = "plantillas/INFORME DE ACTIVIDADES_formato.docx";
        String carpetaTempWord = "C:/Users/inesq/Documents/informess/temp/words/";
        String carpetaTempPdf = "C:/Users/inesq/Documents/informess/temp/pdfs/";
        String carpetaDestino = "C:/Users/inesq/Documents/informess/";

        new File(carpetaTempWord).mkdirs();
        new File(carpetaTempPdf).mkdirs();
        new File(carpetaDestino + "Informes_Actividades").mkdirs();

        GeneradorWord generador = new GeneradorWord();
        List<Contrato> listaContratos = contratoDao.obtenerContratos();
        
        // SIN LÍMITE - Generar todos los informes
        int total = listaContratos.size();

        List<String> pdfsInformes = new ArrayList<>();

        System.out.println("Iniciando generación de " + total + " Informes de Actividades...");

        for (Contrato c : listaContratos) {
            String dni = c.getEmpleado().getDni();
            String wordInf = carpetaTempWord + "INF_" + dni + ".docx";
            String pdfInf = carpetaTempPdf + "INF_" + dni + ".pdf";

            generador.generarDocumento(c, rutaPlantillaInf, wordInf);
            convertirWordAPdfConSpire(wordInf, pdfInf);
            pdfsInformes.add(pdfInf);
        }

        System.out.println("Generando informe consolidado...");
        unirPdfs(pdfsInformes, carpetaDestino + "Informes_Actividades/INFORME DE ACTIVIDADES - MARZO.pdf");
        System.out.println("✅ Informes de Actividades generados correctamente");
    }
    
    // Generar solo FM38
    public void generarSoloFM38() {
        String rutaPlantillaFM38 = "plantillas/FM38_formato.docx";
        String carpetaTempWord = "C:/Users/inesq/Documents/informess/temp/words/";
        String carpetaTempPdf = "C:/Users/inesq/Documents/informess/temp/pdfs/";
        String carpetaDestino = "C:/Users/inesq/Documents/informess/";

        new File(carpetaTempWord).mkdirs();
        new File(carpetaTempPdf).mkdirs();
        new File(carpetaDestino + "Formato_FM38").mkdirs();

        GeneradorWord generador = new GeneradorWord();
        List<Contrato> listaContratos = contratoDao.obtenerContratos();
        
        // Limitar a 10 contratos para pruebas
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

