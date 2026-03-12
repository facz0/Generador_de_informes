package com.onpe.genereador_informes.servicios;

import com.onpe.genereador_informes.model.Actividad;
import com.onpe.genereador_informes.model.Contrato;
import com.onpe.genereador_informes.model.Firma;
import org.apache.poi.xwpf.usermodel.*;
import com.onpe.genereador_informes.DAO.FirmaDAO;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GeneradorWord {
    private FirmaDAO firmaDAO = new FirmaDAO();
    public void generarDocumento(Contrato contrato, String rutaPlantilla, String rutaSalida){
        try{
            //Abrir el archivo
            FileInputStream fis = new FileInputStream(rutaPlantilla);
            XWPFDocument documento = new XWPFDocument(fis);




            String nombreCompleto = (contrato.getEmpleado().getApellidos() + " " + contrato.getEmpleado().getNombres()).toUpperCase();
            String dni = contrato.getEmpleado().getDni();
            String cargo = contrato.getCargo().getNombreCargo();
            String area = contrato.getArea().getNombreArea();
            String num_contrato = contrato.getNumeroContrato();
            List<String> actividades = new ArrayList<>();
            if (contrato.getCargo() != null && contrato.getCargo().getListaActividades() != null){
                for (Actividad a : contrato.getCargo().getListaActividades()){
                    actividades.add(a.getDescripcion());
                }
            }
            String descripcion_firma = "Gerente de la Gerencia de Gestión Electoral";
            String nombreEncargado = "José Edilberto Samamé Blas";
            String nombreEncargadoFM38 = nombreEncargado.toUpperCase();
            LocalDate hoy = LocalDate.now();
            Locale idiomaEspaniol = new Locale("es", "ES");
            String mesAnio = hoy.format(DateTimeFormatter.ofPattern("MMMM yyyy", idiomaEspaniol)).toUpperCase();
            String fechaEmision = hoy.format(DateTimeFormatter.ofPattern("dd 'DE' MMMM 'DE' yyyyy", idiomaEspaniol)).toUpperCase();

            LocalDate primerDiaMes = hoy.withDayOfMonth(1);
            LocalDate ultimoDiaMes = hoy.withDayOfMonth(hoy.lengthOfMonth());

            LocalDate inicioInforme = contrato.getFechaInicio().isBefore(primerDiaMes)
                    ? primerDiaMes
                    : contrato.getFechaInicio();

            LocalDate finInforme = contrato.getFechaFin().isAfter(ultimoDiaMes)
                    ? ultimoDiaMes
                    : contrato.getFechaFin();
            DateTimeFormatter diaFmt = DateTimeFormatter.ofPattern("dd");
            DateTimeFormatter mesAnioFmt = DateTimeFormatter.ofPattern("'DE' MMMM 'DE' yyyy", new Locale("es", "ES"));
            String periodo = "DEL " + inicioInforme.format(diaFmt) +
                    " AL " + finInforme.format(diaFmt) +
                    " " + finInforme.format(mesAnioFmt).toUpperCase();
            
            // Configurar ODPE
            String descripcionOdpe = "";
            String nombreOdpe = "";
            if (contrato.getEmpleado().getOdpe() != null) {
                descripcionOdpe = " en la ODPE";
                nombreOdpe = " " + contrato.getEmpleado().getOdpe().getNombreOdpe();
            }

            //informe actividades
            for (XWPFParagraph parrafo : documento.getParagraphs()) {
                reemplazarEnParrafo(parrafo, "{{NOMBRE_ENCARGADO}}", nombreEncargado);
                reemplazarEnParrafo(parrafo, "{{DESCRIPCION_FIRMA}}", descripcion_firma);
                reemplazarEnParrafo(parrafo, "{{NOMBRE_EMPLEADO}}", nombreCompleto);
                reemplazarEnParrafo(parrafo, "{{CARGO}}", cargo);
                reemplazarEnParrafo(parrafo, "{{NOMBRE_AREA}}", area);
                reemplazarEnParrafo(parrafo, "{{PERIODO_PRESTACION}}", periodo);
                reemplazarEnParrafo(parrafo, "{{DNI_EMPLEADO}}", dni);
                reemplazarEnParrafo(parrafo, "{{DESCRIPCION}}", descripcionOdpe);
                reemplazarEnParrafo(parrafo, "{{ODPE}}", nombreOdpe);
            }
            
            // Reemplazar actividades (solo una vez, fuera del loop)
            reemplazarActividadesSeguro(documento, actividades);

            //fm38
            for (XWPFTable tabla : documento.getTables()) {
                for (XWPFTableRow fila : tabla.getRows()) {
                    for (XWPFTableCell celda : fila.getTableCells()) {
                        for (XWPFParagraph parrafo : celda.getParagraphs()) {
                            reemplazarEnParrafo(parrafo, "{{NUMERO_CONTRATO}}", num_contrato);
                            reemplazarEnParrafo(parrafo, "{{CARGO}}", cargo);
                            reemplazarEnParrafo(parrafo, "{{NOMBRE_EMPLEADO}}", nombreCompleto);
                            reemplazarEnParrafo(parrafo, "{{PERIODO_PRESTACION}}", periodo);
                            reemplazarEnParrafo(parrafo, "{{NOMBRE_ENCARGADO}}", nombreEncargadoFM38);
                            reemplazarEnParrafo(parrafo, "{{DESCRIPCION_FIRMA}}", descripcion_firma);
                            reemplazarEnParrafo(parrafo, "{{FECHA_ANIO_ACTUAL}}", mesAnio);
                        }
                    }
                }
            }


            FileOutputStream fos = new FileOutputStream(rutaSalida);
            documento.write(fos);
            fos.close();
            fis.close();
            documento.close();
        } catch (Exception e){
            System.out.println("❌ Error al generar el Word: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void reemplazarEnParrafo(XWPFParagraph parrafo, String buscar, String reemplazo) {
        if (reemplazo == null) reemplazo = "";

        List<XWPFRun> runs = parrafo.getRuns();
        if (runs != null) {
            for (XWPFRun run : runs) {
                String texto = run.getText(0);
                if (texto != null && texto.contains(buscar)) {
                    texto = texto.replace(buscar, reemplazo);
                    run.setText(texto, 0);
                }
            }
        }
    }

    //private void reemplazarListaParrafo(XWPFParagraph parrafo, String buscar, List<String> actividades){
    //    if (actividades == null || actividades.isEmpty()) return;
    //    List<XWPFRun> runs = parrafo.getRuns();
    //    if (runs != null){
    //        for (XWPFRun run : runs){
    //            String texto = run.getText(0);
    //            if (texto != null){
    //                texto = texto.replace(buscar, "");
    //                run.setText(texto, 0);
    //                for (int i = 0; i < actividades.size(); i++){
    //                    run.setText(String.valueOf(i + 1) + ".-" + actividades.get(i));
    //                    if (i < actividades.size() - 1){
    //                        run.addBreak();
    //                    }
    //                }
    //            }
    //        }
    //    }
    //}

    public void reemplazarActividadesSeguro(XWPFDocument doc, List<String> actividades) {
        String busqueda = "XXXACTIVIDADESXXX";
        boolean encontrado = false;

        // 1. Buscar en párrafos principales del documento
        for (XWPFParagraph p : doc.getParagraphs()) {
            if (p.getText().contains(busqueda)) {
                inyectarActividades(p, actividades);
                encontrado = true;
                break;
            }
        }

        // 2. Si no se encontró, buscar en tablas
        if (!encontrado) {
            for (XWPFTable tabla : doc.getTables()) {
                for (XWPFTableRow fila : tabla.getRows()) {
                    for (XWPFTableCell celda : fila.getTableCells()) {
                        for (XWPFParagraph p : celda.getParagraphs()) {
                            if (p.getText().contains(busqueda)) {
                                inyectarActividades(p, actividades);
                                encontrado = true;
                                break;
                            }
                        }
                        if (encontrado) break;
                    }
                    if (encontrado) break;
                }
                if (encontrado) break;
            }
        }

        if (!encontrado) {
            System.out.println("❌ ERROR: No se encontro el tag XXXACTIVIDADESXXX en el documento.");
        }
    }

    private void inyectarActividades(XWPFParagraph parrafo, List<String> actividades) {
        // Limpiamos todo el contenido del párrafo
        while (!parrafo.getRuns().isEmpty()) {
            parrafo.removeRun(0);
        }
        
        // Configurar alineación izquierda (más natural que justificado)
        parrafo.setAlignment(ParagraphAlignment.LEFT);
        parrafo.setSpacingBetween(1.15); // Interlineado de 1.15

        // Inyectamos las actividades
        for (int i = 0; i < actividades.size(); i++) {
            XWPFRun run = parrafo.createRun();
            run.setText((i + 1) + ". " + actividades.get(i));
            run.setColor("000000");
            run.setFontFamily("Arial");
            run.setFontSize(10);

            // Agregar salto de línea entre actividades (excepto la última)
            if (i < actividades.size() - 1) {
                run.addBreak();
            }
        }
    }
}


