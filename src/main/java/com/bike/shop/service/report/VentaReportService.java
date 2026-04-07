package com.bike.shop.service.report;

import com.bike.shop.dto.response.VentaResponseDTO;
import com.bike.shop.entity.Usuario;
import com.bike.shop.service.VentaService;

// ─── iTextPDF (PDF) ─────────────────────────────────────────
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


import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;  // ← Este es el de Excel (no causa conflicto)
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;




import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VentaReportService {

    private final VentaService ventaService;

    private static final BaseColor AZUL = new BaseColor(11, 43, 94);
    private static final BaseColor GRIS_OSCURO = new BaseColor(30, 30, 30);
    private static final BaseColor GRIS_MEDIO  = new BaseColor(100, 100, 100);
    private static final BaseColor BLANCO      = BaseColor.WHITE;

    // ═══════════════════════════════════════════════
    //  PDF — TODAS LAS VENTAS
    // ═══════════════════════════════════════════════
    public byte[] generarPDF() throws Exception {
        return generarPDFVentas(ventaService.listarTodas(), "REPORTE DE VENTAS");
    }

    // ═══════════════════════════════════════════════
    //  PDF — VENTAS POR FECHA
    // ═══════════════════════════════════════════════
    public byte[] generarPDFPorFecha(LocalDateTime inicio, LocalDateTime fin) throws Exception {
        List<VentaResponseDTO> ventas = ventaService.buscarPorFecha(inicio, fin);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String titulo = "VENTAS DEL " + inicio.format(fmt) + " AL " + fin.format(fmt);
        return generarPDFVentas(ventas, titulo);
    }

    // ═══════════════════════════════════════════════
    //  PDF — MIS VENTAS (empleado logueado)
    // ═══════════════════════════════════════════════
    public byte[] generarPDFMisVentas() throws Exception {
        List<VentaResponseDTO> ventas = ventaService.listarMisVentas();
        Usuario u = (Usuario) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return generarPDFVentas(ventas, "MIS VENTAS — " + u.getNombre().toUpperCase());
    }

    // ═══════════════════════════════════════════════
    //  PDF — VENTAS POR EMPLEADO (solo ADMIN)
    // ═══════════════════════════════════════════════
    public byte[] generarPDFPorEmpleado(Long usuarioId) throws Exception {
        List<VentaResponseDTO> ventas = ventaService.listarPorUsuario(usuarioId);
        String titulo = "VENTAS POR EMPLEADO";
        return generarPDFVentas(ventas, titulo);
    }

    // ═══════════════════════════════════════════════
    //  MÉTODO BASE PDF
    // ═══════════════════════════════════════════════
    private byte[] generarPDFVentas(List<VentaResponseDTO> ventas, String titulo) throws Exception {
        Document document = new Document(PageSize.A4.rotate(), 40, 40, 60, 40);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, out);
        document.open();

        // Header
        agregarHeaderPDF(document, titulo);

        // Tabla
        PdfPTable table = new PdfPTable(7);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{0.6f, 2f, 1.8f, 1.5f, 1.5f, 1.5f, 1.8f});

        String[] headers = {"#", "Cliente", "Empleado", "Fecha", "Forma Pago", "Estado", "Total"};
        agregarHeadersTabla(table, headers);

        Font fontDato = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL, GRIS_OSCURO);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        boolean alterna = false;

        for (VentaResponseDTO v : ventas) {
            BaseColor bg = alterna ? new BaseColor(245, 245, 245) : BLANCO;
            String[] datos = {
                    String.valueOf(v.getId()),
                    v.getNombreCliente(),
                    v.getNombreEmpleado(),
                    v.getFecha().format(fmt),
                    v.getFormaPago() != null ? v.getFormaPago() : "-",
                    v.getEstado(),
                    "$ " + String.format("%,.0f", v.getTotal().doubleValue())
            };
            for (String dato : datos) {
                PdfPCell cell = new PdfPCell(new Phrase(dato, fontDato));
                cell.setBackgroundColor(bg);
                cell.setPadding(8);
                cell.setBorderColor(new BaseColor(220, 220, 220));
                table.addCell(cell);
            }
            alterna = !alterna;
        }
        document.add(table);

        // Total general
        double totalGeneral = ventas.stream()
                .mapToDouble(v -> v.getTotal().doubleValue()).sum();
        Font fontTotal = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, AZUL);
        Paragraph totalP = new Paragraph(
                "\nTOTAL GENERAL: $ " + String.format("%,.0f", totalGeneral), fontTotal);
        totalP.setAlignment(Element.ALIGN_RIGHT);
        document.add(totalP);

        agregarFooterPDF(document);
        document.close();
        return out.toByteArray();
    }

    // ═══════════════════════════════════════════════
    //  EXCEL — TODAS LAS VENTAS
    // ═══════════════════════════════════════════════
    public byte[] generarExcel() throws Exception {
        return generarExcelVentas(ventaService.listarTodas(), "Ventas");
    }

    // ═══════════════════════════════════════════════
    //  EXCEL — VENTAS POR FECHA
    // ═══════════════════════════════════════════════
    public byte[] generarExcelPorFecha(LocalDateTime inicio, LocalDateTime fin) throws Exception {
        return generarExcelVentas(ventaService.buscarPorFecha(inicio, fin), "Ventas por Fecha");
    }

    // ═══════════════════════════════════════════════
    //  EXCEL — MIS VENTAS
    // ═══════════════════════════════════════════════
    public byte[] generarExcelMisVentas() throws Exception {
        return generarExcelVentas(ventaService.listarMisVentas(), "Mis Ventas");
    }

    // ═══════════════════════════════════════════════
    //  EXCEL — VENTAS POR EMPLEADO
    // ═══════════════════════════════════════════════
    public byte[] generarExcelPorEmpleado(Long usuarioId) throws Exception {
        return generarExcelVentas(ventaService.listarPorUsuario(usuarioId), "Ventas Empleado");
    }

    // ═══════════════════════════════════════════════
    //  MÉTODO BASE EXCEL
    // ═══════════════════════════════════════════════
    private byte[] generarExcelVentas(List<VentaResponseDTO> ventas, String nombreHoja) throws Exception {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet(nombreHoja);

        XSSFCellStyle headerStyle = crearEstiloHeader(workbook);
        XSSFCellStyle altStyle    = crearEstiloAlterna(workbook);

        // Título
        Row tituloRow = sheet.createRow(0);
        tituloRow.setHeight((short)800);
        Cell tituloCell = tituloRow.createCell(0);
        tituloCell.setCellValue("🚲 BIKE SHOP — " + nombreHoja.toUpperCase());
        tituloCell.setCellStyle(crearEstiloTitulo(workbook));
        sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 6));

        // Headers
        Row headerRow = sheet.createRow(1);
        String[] headers = {"#", "Cliente", "Empleado", "Fecha", "Forma Pago", "Estado", "Total"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Datos
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        int rowNum = 2;
        boolean alterna = false;
        for (VentaResponseDTO v : ventas) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(v.getId());
            row.createCell(1).setCellValue(v.getNombreCliente());
            row.createCell(2).setCellValue(v.getNombreEmpleado() != null ? v.getNombreEmpleado() : "-");
            row.createCell(3).setCellValue(v.getFecha().format(fmt));
            row.createCell(4).setCellValue(v.getFormaPago() != null ? v.getFormaPago() : "-");
            row.createCell(5).setCellValue(v.getEstado());
            row.createCell(6).setCellValue(v.getTotal().doubleValue());
            if (alterna) {
                for (int i = 0; i <= 6; i++) row.getCell(i).setCellStyle(altStyle);
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
    //  HELPERS PDF
    // ═══════════════════════════════════════════════
    private void agregarHeaderPDF(Document document, String titulo) throws Exception {
        PdfPTable header = new PdfPTable(2);
        header.setWidthPercentage(100);
        header.setWidths(new float[]{3, 1});
        header.setSpacingAfter(15);

        Font fontTienda = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD, AZUL);
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

        // Línea naranja
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

    private void agregarHeadersTabla(PdfPTable table, String[] headers) {
        Font fontHeader = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BLANCO);
        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h, fontHeader));
            cell.setBackgroundColor(AZUL);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(10);
            cell.setBorderColor(AZUL);
            table.addCell(cell);
        }
    }

    private void agregarFooterPDF(Document document) throws Exception {
        Font fontFooter = new Font(Font.FontFamily.HELVETICA, 8, Font.ITALIC, GRIS_MEDIO);
        Paragraph footer = new Paragraph("\nBike Shop — Sistema de Gestión", fontFooter);
        footer.setAlignment(Element.ALIGN_CENTER);
        document.add(footer);
    }

    // ═══════════════════════════════════════════════
    //  HELPERS EXCEL
    // ═══════════════════════════════════════════════
    private XSSFCellStyle crearEstiloHeader(XSSFWorkbook wb) {
        XSSFCellStyle style = wb.createCellStyle();
        XSSFFont font = wb.createFont();
        font.setBold(true);
        font.setColor(new XSSFColor(new byte[]{(byte)255, (byte)255, (byte)255}));
        style.setFont(font);
        style.setFillForegroundColor(new XSSFColor(new byte[]{(byte)11, (byte)43, (byte)94}));
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
