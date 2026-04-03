package com.bike.shop.service.report;

import com.bike.shop.dto.response.PedidoResponseDTO;
import com.bike.shop.service.PedidoService;



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

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;


import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PedidoReportService {

    private final PedidoService pedidoService;

    private static final BaseColor AZUL = new BaseColor(11, 43, 94);
    private static final BaseColor GRIS_OSCURO = new BaseColor(30, 30, 30);
    private static final BaseColor GRIS_MEDIO  = new BaseColor(100, 100, 100);
    private static final BaseColor BLANCO      = BaseColor.WHITE;

    // ═══════════════════════════════════════════════
    //  PDF — TODOS LOS PEDIDOS
    // ═══════════════════════════════════════════════
    public byte[] generarPDFPedidos() throws Exception {
        List<PedidoResponseDTO> pedidos = pedidoService.listarTodos();
        return generarPDFBase(pedidos, "REPORTE DE PEDIDOS A PROVEEDORES");
    }

    // ═══════════════════════════════════════════════
    //  MÉTODO BASE PDF
    // ═══════════════════════════════════════════════
    private byte[] generarPDFBase(List<PedidoResponseDTO> pedidos, String titulo) throws Exception {
        Document document = new Document(PageSize.A4.rotate(), 40, 40, 60, 40);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, out);
        document.open();

        // Header
        agregarHeader(document, titulo);

        // Tabla principal (pedidos)
        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{0.8f, 2.5f, 2f, 1.5f, 1.5f});

        String[] headers = {"#", "Proveedor", "Fecha", "Estado", "Items"};
        Font fontHeader = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BLANCO);
        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h, fontHeader));
            cell.setBackgroundColor(AZUL);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(10);
            cell.setBorderColor(AZUL);
            table.addCell(cell);
        }

        Font fontDato = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL, GRIS_OSCURO);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        boolean alterna = false;

        for (PedidoResponseDTO p : pedidos) {
            BaseColor bg = alterna ? new BaseColor(245, 245, 245) : BLANCO;

            // Estado con color
            BaseColor colorEstado = p.getEstado().equals("recibido")
                    ? new BaseColor(0, 150, 50) : new BaseColor(230, 160, 0);

            String[] datos = {
                    String.valueOf(p.getId()),
                    p.getNombreProveedor(),
                    p.getFecha().format(fmt),
                    "",  // estado con color separado
                    String.valueOf(p.getDetalles() != null ? p.getDetalles().size() : 0)
            };

            for (int i = 0; i < 2; i++) {
                PdfPCell cell = new PdfPCell(new Phrase(datos[i], fontDato));
                cell.setBackgroundColor(bg);
                cell.setPadding(8);
                cell.setBorderColor(new BaseColor(220, 220, 220));
                table.addCell(cell);
            }
            // Fecha
            PdfPCell fechaCell = new PdfPCell(new Phrase(datos[2], fontDato));
            fechaCell.setBackgroundColor(bg);
            fechaCell.setPadding(8);
            fechaCell.setBorderColor(new BaseColor(220, 220, 220));
            table.addCell(fechaCell);

            // Estado coloreado
            Font fontEstado = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD, colorEstado);
            PdfPCell estadoCell = new PdfPCell(
                    new Phrase(p.getEstado().toUpperCase(), fontEstado));
            estadoCell.setBackgroundColor(bg);
            estadoCell.setPadding(8);
            estadoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            estadoCell.setBorderColor(new BaseColor(220, 220, 220));
            table.addCell(estadoCell);

            // Items
            PdfPCell itemsCell = new PdfPCell(new Phrase(datos[4], fontDato));
            itemsCell.setBackgroundColor(bg);
            itemsCell.setPadding(8);
            itemsCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            itemsCell.setBorderColor(new BaseColor(220, 220, 220));
            table.addCell(itemsCell);

            alterna = !alterna;
        }
        document.add(table);

        // Resumen
        long pendientes = pedidos.stream().filter(p -> p.getEstado().equals("pendiente")).count();
        long recibidos  = pedidos.stream().filter(p -> p.getEstado().equals("recibido")).count();
        Font fontResumen = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, GRIS_OSCURO);
        Paragraph resumen = new Paragraph(
                "\nTotal pedidos: " + pedidos.size() +
                        "   |   Pendientes: " + pendientes +
                        "   |   Recibidos: " + recibidos, fontResumen);
        resumen.setAlignment(Element.ALIGN_RIGHT);
        document.add(resumen);

        agregarFooter(document);
        document.close();
        return out.toByteArray();
    }

    // ═══════════════════════════════════════════════
    //  EXCEL — TODOS LOS PEDIDOS
    // ═══════════════════════════════════════════════
    public byte[] generarExcelPedidos() throws Exception {
        List<PedidoResponseDTO> pedidos = pedidoService.listarTodos();
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Pedidos");

        XSSFCellStyle headerStyle = crearEstiloHeader(workbook);
        XSSFCellStyle altStyle    = crearEstiloAlterna(workbook);

        // Título
        Row tituloRow = sheet.createRow(0);
        tituloRow.setHeight((short)800);
        Cell tituloCell = tituloRow.createCell(0);
        tituloCell.setCellValue("BIKE SHOP — REPORTE DE PEDIDOS");
        tituloCell.setCellStyle(crearEstiloTitulo(workbook));
        sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 4));

        // Headers
        Row headerRow = sheet.createRow(1);
        String[] headers = {"#", "Proveedor", "Fecha", "Estado", "Items"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Datos
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        int rowNum = 2;
        boolean alterna = false;
        for (PedidoResponseDTO p : pedidos) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(p.getId());
            row.createCell(1).setCellValue(p.getNombreProveedor());
            row.createCell(2).setCellValue(p.getFecha().format(fmt));
            row.createCell(3).setCellValue(p.getEstado());
            row.createCell(4).setCellValue(
                    p.getDetalles() != null ? p.getDetalles().size() : 0);
            if (alterna) {
                for (int i = 0; i <= 4; i++) row.getCell(i).setCellStyle(altStyle);
            }
            alterna = !alterna;
        }

        for (int i = 0; i < headers.length; i++) sheet.autoSizeColumn(i);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();
        return out.toByteArray();
    }

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
