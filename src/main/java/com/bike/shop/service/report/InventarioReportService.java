package com.bike.shop.service.report;

import com.bike.shop.dto.response.BicicletaResponseDTO;
import com.bike.shop.service.BicicletaService;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.Font;


import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;


import lombok.RequiredArgsConstructor;


import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;


import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventarioReportService {

    private final BicicletaService bicicletaService;

    private static final BaseColor AZUL = new BaseColor(11, 43, 94);
    private static final BaseColor ROJO        = new BaseColor(200, 50, 50);
    private static final BaseColor AMARILLO    = new BaseColor(230, 160, 0);
    private static final BaseColor GRIS_OSCURO = new BaseColor(30, 30, 30);
    private static final BaseColor GRIS_MEDIO  = new BaseColor(100, 100, 100);
    private static final BaseColor BLANCO      = BaseColor.WHITE;

    // ═══════════════════════════════════════════════
    //  PDF — INVENTARIO COMPLETO
    // ═══════════════════════════════════════════════
    public byte[] generarPDFInventario() throws Exception {
        List<BicicletaResponseDTO> bicicletas = bicicletaService.listarTodas();
        return generarPDFBicicletas(bicicletas, "INVENTARIO COMPLETO");
    }

    // ═══════════════════════════════════════════════
    //  PDF — STOCK BAJO Y SIN STOCK
    // ═══════════════════════════════════════════════
    public byte[] generarPDFStockCritico() throws Exception {
        List<BicicletaResponseDTO> stockBajo = bicicletaService.listarStockBajo();
        return generarPDFBicicletas(stockBajo, "REPORTE — STOCK CRÍTICO");
    }

    // ═══════════════════════════════════════════════
    //  MÉTODO BASE PDF
    // ═══════════════════════════════════════════════
    private byte[] generarPDFBicicletas(List<BicicletaResponseDTO> bicicletas,
                                        String titulo) throws Exception {
        Document document = new Document(PageSize.A4.rotate(), 40, 40, 60, 40);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, out);
        document.open();

        // Header
        agregarHeader(document, titulo);

        // Tabla
        PdfPTable table = new PdfPTable(8);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{0.6f, 1.5f, 2f, 1.2f, 1.8f, 1.8f, 1f, 1f});

        String[] headers = {"Cód.", "Marca", "Modelo", "Tipo",
                "P. Costo", "P. Venta", "Stock", "Estado"};
        Font fontHeader = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD, BLANCO);
        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h, fontHeader));
            cell.setBackgroundColor(AZUL);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(9);
            cell.setBorderColor(AZUL);
            table.addCell(cell);
        }

        Font fontDato = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL, GRIS_OSCURO);
        boolean alterna = false;

        for (BicicletaResponseDTO b : bicicletas) {
            BaseColor bg = alterna ? new BaseColor(245, 245, 245) : BLANCO;

            // Determinar estado de stock
            String estadoStock;
            BaseColor colorEstado;
            if (b.getCantidad() == 0) {
                estadoStock = "SIN STOCK";
                colorEstado = ROJO;
            } else if (b.getCantidad() <= b.getStockMinimo()) {
                estadoStock = "STOCK BAJO";
                colorEstado = AMARILLO;
            } else {
                estadoStock = "OK";
                colorEstado = new BaseColor(0, 150, 50);
            }

            String[] datos = {
                    String.valueOf(b.getCodigo()),
                    b.getMarca(),
                    b.getModelo(),
                    b.getTipo(),
                    "$ " + String.format("%,.0f", b.getPrecioCosto().doubleValue()),
                    "$ " + String.format("%,.0f", b.getPrecioVenta().doubleValue()),
                    String.valueOf(b.getCantidad()),
                    ""  // el estado lo ponemos con color
            };

            for (int i = 0; i < 7; i++) {
                PdfPCell cell = new PdfPCell(new Phrase(datos[i], fontDato));
                cell.setBackgroundColor(bg);
                cell.setPadding(8);
                cell.setBorderColor(new BaseColor(220, 220, 220));
                table.addCell(cell);
            }

            // Celda de estado con color
            Font fontEstado = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD, colorEstado);
            PdfPCell estadoCell = new PdfPCell(new Phrase(estadoStock, fontEstado));
            estadoCell.setBackgroundColor(bg);
            estadoCell.setPadding(8);
            estadoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            estadoCell.setBorderColor(new BaseColor(220, 220, 220));
            table.addCell(estadoCell);

            alterna = !alterna;
        }
        document.add(table);

        // Resumen
        long sinStock  = bicicletas.stream().filter(b -> b.getCantidad() == 0).count();
        long stockBajo = bicicletas.stream()
                .filter(b -> b.getCantidad() > 0 && b.getCantidad() <= b.getStockMinimo()).count();

        Font fontResumen = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, GRIS_OSCURO);
        Paragraph resumen = new Paragraph(
                "\nTotal bicicletas: " + bicicletas.size() +
                        "   |   Sin stock: " + sinStock +
                        "   |   Stock bajo: " + stockBajo, fontResumen);
        resumen.setAlignment(Element.ALIGN_RIGHT);
        document.add(resumen);

        agregarFooter(document);
        document.close();
        return out.toByteArray();
    }

    // ═══════════════════════════════════════════════
    //  EXCEL — INVENTARIO COMPLETO
    // ═══════════════════════════════════════════════
    public byte[] generarExcelInventario() throws Exception {
        return generarExcelBicicletas(bicicletaService.listarTodas(), "Inventario");
    }

    // ═══════════════════════════════════════════════
    //  EXCEL — STOCK CRÍTICO
    // ═══════════════════════════════════════════════
    public byte[] generarExcelStockCritico() throws Exception {
        return generarExcelBicicletas(bicicletaService.listarStockBajo(), "Stock Critico");
    }

    // ═══════════════════════════════════════════════
    //  MÉTODO BASE EXCEL
    // ═══════════════════════════════════════════════
    private byte[] generarExcelBicicletas(List<BicicletaResponseDTO> bicicletas,
                                          String nombreHoja) throws Exception {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet(nombreHoja);

        XSSFCellStyle headerStyle  = crearEstiloHeader(workbook);
        XSSFCellStyle altStyle     = crearEstiloAlterna(workbook);
        XSSFCellStyle rojoStyle    = crearEstiloColor(workbook, (byte)255, (byte)200, (byte)200);
        XSSFCellStyle amarilloStyle = crearEstiloColor(workbook, (byte)255, (byte)243, (byte)180);

        // Título
        Row tituloRow = sheet.createRow(0);
        tituloRow.setHeight((short)800);
        Cell tituloCell = tituloRow.createCell(0);
        tituloCell.setCellValue("BIKE SHOP — " + nombreHoja.toUpperCase());
        tituloCell.setCellStyle(crearEstiloTitulo(workbook));
        sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 7));

        // Headers
        Row headerRow = sheet.createRow(1);
        String[] headers = {"Código", "Marca", "Modelo", "Tipo",
                "Precio Costo", "Precio Venta", "Stock", "Estado"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Datos
        int rowNum = 2;
        boolean alterna = false;
        for (BicicletaResponseDTO b : bicicletas) {
            Row row = sheet.createRow(rowNum++);

            String estadoStock;
            XSSFCellStyle filaStyle;
            if (b.getCantidad() == 0) {
                estadoStock = "SIN STOCK";
                filaStyle = rojoStyle;
            } else if (b.getCantidad() <= b.getStockMinimo()) {
                estadoStock = "STOCK BAJO";
                filaStyle = amarilloStyle;
            } else {
                estadoStock = "OK";
                filaStyle = alterna ? altStyle : null;
            }

            row.createCell(0).setCellValue(b.getCodigo());
            row.createCell(1).setCellValue(b.getMarca());
            row.createCell(2).setCellValue(b.getModelo());
            row.createCell(3).setCellValue(b.getTipo());
            row.createCell(4).setCellValue(b.getPrecioCosto().doubleValue());
            row.createCell(5).setCellValue(b.getPrecioVenta().doubleValue());
            row.createCell(6).setCellValue(b.getCantidad());
            row.createCell(7).setCellValue(estadoStock);

            if (filaStyle != null) {
                for (int i = 0; i <= 7; i++) {
                    if (row.getCell(i) != null) row.getCell(i).setCellStyle(filaStyle);
                }
            }
            alterna = !alterna;
        }

        for (int i = 0; i < headers.length; i++) sheet.autoSizeColumn(i);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();
        return out.toByteArray();
    }

    // ═══════════════════════════════════════════════
    //  HELPERS
    // ═══════════════════════════════════════════════
    private void agregarHeader(Document document, String titulo) throws Exception {
        Font fontTienda = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD, AZUL);
        PdfPTable header = new PdfPTable(2);
        header.setWidthPercentage(100);
        header.setWidths(new float[]{3, 1});
        header.setSpacingAfter(12);

        PdfPCell nombre = new PdfPCell(new Phrase("BIKE SHOP", fontTienda));
        nombre.setBorder(Rectangle.NO_BORDER);
        nombre.setPadding(8);
        header.addCell(nombre);

        Font fontFecha = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL, GRIS_MEDIO);
        String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        PdfPCell fechaCell = new PdfPCell(new Phrase("Generado: " + fecha, fontFecha));
        fechaCell.setBorder(Rectangle.NO_BORDER);
        fechaCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        header.addCell(fechaCell);
        document.add(header);

        PdfPTable linea = new PdfPTable(1);
        linea.setWidthPercentage(100);
        linea.setSpacingAfter(12);
        PdfPCell lineaCell = new PdfPCell();
        lineaCell.setBackgroundColor(AZUL);
        lineaCell.setFixedHeight(3);
        lineaCell.setBorder(Rectangle.NO_BORDER);
        linea.addCell(lineaCell);
        document.add(linea);

        Font fontTitulo = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, GRIS_OSCURO);
        Paragraph tituloPar = new Paragraph(titulo, fontTitulo);
        tituloPar.setSpacingAfter(12);
        document.add(tituloPar);
    }

    private void agregarFooter(Document document) throws Exception {
        Font fontFooter = new Font(Font.FontFamily.HELVETICA, 8, Font.ITALIC, GRIS_MEDIO);
        Paragraph footer = new Paragraph("\nBike Shop — Sistema de Gestión", fontFooter);
        footer.setAlignment(Element.ALIGN_CENTER);
        document.add(footer);
    }

    private XSSFCellStyle crearEstiloHeader(XSSFWorkbook wb) {
        XSSFCellStyle style = wb.createCellStyle();
        XSSFFont font = wb.createFont();
        font.setBold(true);
        font.setColor(new XSSFColor(new byte[]{(byte)255, (byte)255, (byte)255}));
        style.setFont(font);
        style.setFillForegroundColor(new XSSFColor(new byte[]{(byte)245, (byte)245, (byte)245}));
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private XSSFCellStyle crearEstiloAlterna(XSSFWorkbook wb) {
        XSSFCellStyle style = wb.createCellStyle();
        style.setFillForegroundColor(new XSSFColor(new byte[]{(byte)245, (byte)245, (byte)245}));
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    private XSSFCellStyle crearEstiloColor(XSSFWorkbook wb, byte r, byte g, byte b) {
        XSSFCellStyle style = wb.createCellStyle();
        style.setFillForegroundColor(new XSSFColor(new byte[]{r, g, b}));
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    private XSSFCellStyle crearEstiloTitulo(XSSFWorkbook wb) {
        XSSFCellStyle style = wb.createCellStyle();
        XSSFFont font = wb.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short)14);
        font.setColor(new XSSFColor(new byte[]{(byte)11, (byte)43, (byte)94}));
        style.setFont(font);
        return style;
    }
}
