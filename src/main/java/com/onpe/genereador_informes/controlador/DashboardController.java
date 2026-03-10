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

    public void generarTodosLosInformes() {
        String rutaPlantillaInf = "C:/Users/fcossio/Downloads/INFORME DE ACTIVIDADES_formato.docx";
        String rutaPlantillaFM38 = "C:/Users/fcossio/Downloads/FM38_formato.docx";

        String carpetaTempWord = "C:/Users/fcossio/Documents/Informes_Lurin/temp/words/";
        String carpetaTempPdf = "C:/Users/fcossio/Documents/Informes_Lurin/temp/pdfs/";
        String carpetaDestino = "C:/Users/fcossio/Documents/Informes_Lurin/";

        new File(carpetaTempWord).mkdirs();
        new File(carpetaTempPdf).mkdirs();

        GeneradorWord generador = new GeneradorWord();
        List<Contrato> listaContratos = contratoDao.obtenerContratos();

        List<String> pdfsInformes = new ArrayList<>();
        List<String> pdfsFM38 = new ArrayList<>();

        System.out.println("Iniciando generación... ");

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

        System.out.println("Generando informes consolidados...");

        // 3. Unir
        unirPdfs(pdfsInformes, carpetaDestino + "Informes_Actividades/INFORME DE ACTIVIDADES - MARZO.pdf");
        unirPdfs(pdfsFM38, carpetaDestino + "Formato_FM38/FORMATO fm38 - MARZO.pdf");

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

