package com.bike.shop.controller;

import com.bike.shop.service.report.InventarioReportService;
import com.bike.shop.service.report.PedidoReportService;
import com.bike.shop.service.report.VentaReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class ReporteController {

    private final VentaReportService ventaReportService;
    private final InventarioReportService inventarioReportService;
    private final PedidoReportService pedidoReportService;

    // ═══════════════════════════════════════════════
    //  VENTAS — PDF
    // ═══════════════════════════════════════════════

    // GET /api/reportes/ventas/pdf
    @GetMapping("/ventas/pdf")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> ventasPDF() throws Exception {
        return pdfResponse(ventaReportService.generarPDF(), "ventas.pdf");
    }

    // GET /api/reportes/ventas/pdf?inicio=2026-01-01T00:00:00&fin=2026-12-31T23:59:59
    @GetMapping("/ventas/pdf/fecha")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> ventasPDFPorFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin
    ) throws Exception {
        return pdfResponse(ventaReportService.generarPDFPorFecha(inicio, fin),
                "ventas-por-fecha.pdf");
    }

    // GET /api/reportes/ventas/pdf/mis-ventas  (empleado logueado)
    @GetMapping("/ventas/pdf/mis-ventas")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLEADO')")
    public ResponseEntity<byte[]> misVentasPDF() throws Exception {
        return pdfResponse(ventaReportService.generarPDFMisVentas(), "mis-ventas.pdf");
    }

    // GET /api/reportes/ventas/pdf/empleado/{id}  (solo ADMIN)
    @GetMapping("/ventas/pdf/empleado/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> ventasPDFPorEmpleado(@PathVariable Long id) throws Exception {
        return pdfResponse(ventaReportService.generarPDFPorEmpleado(id),
                "ventas-empleado-" + id + ".pdf");
    }

    // ═══════════════════════════════════════════════
    //  VENTAS — EXCEL
    // ═══════════════════════════════════════════════

    @GetMapping("/ventas/excel")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> ventasExcel() throws Exception {
        return excelResponse(ventaReportService.generarExcel(), "ventas.xlsx");
    }

    @GetMapping("/ventas/excel/fecha")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> ventasExcelPorFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin
    ) throws Exception {
        return excelResponse(ventaReportService.generarExcelPorFecha(inicio, fin),
                "ventas-por-fecha.xlsx");
    }

    @GetMapping("/ventas/excel/mis-ventas")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLEADO')")
    public ResponseEntity<byte[]> misVentasExcel() throws Exception {
        return excelResponse(ventaReportService.generarExcelMisVentas(), "mis-ventas.xlsx");
    }

    @GetMapping("/ventas/excel/empleado/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> ventasExcelPorEmpleado(@PathVariable Long id) throws Exception {
        return excelResponse(ventaReportService.generarExcelPorEmpleado(id),
                "ventas-empleado-" + id + ".xlsx");
    }

    // ═══════════════════════════════════════════════
    //  INVENTARIO — PDF
    // ═══════════════════════════════════════════════

    @GetMapping("/inventario/pdf")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> inventarioPDF() throws Exception {
        return pdfResponse(inventarioReportService.generarPDFInventario(), "inventario.pdf");
    }

    @GetMapping("/inventario/pdf/stock-critico")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> stockCriticoPDF() throws Exception {
        return pdfResponse(inventarioReportService.generarPDFStockCritico(),
                "stock-critico.pdf");
    }

    // ═══════════════════════════════════════════════
    //  INVENTARIO — EXCEL
    // ═══════════════════════════════════════════════

    @GetMapping("/inventario/excel")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> inventarioExcel() throws Exception {
        return excelResponse(inventarioReportService.generarExcelInventario(),
                "inventario.xlsx");
    }

    @GetMapping("/inventario/excel/stock-critico")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> stockCriticoExcel() throws Exception {
        return excelResponse(inventarioReportService.generarExcelStockCritico(),
                "stock-critico.xlsx");
    }

    // ═══════════════════════════════════════════════
    //  PEDIDOS — PDF
    // ═══════════════════════════════════════════════

    @GetMapping("/pedidos/pdf")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> pedidosPDF() throws Exception {
        return pdfResponse(pedidoReportService.generarPDFPedidos(), "pedidos.pdf");
    }

    // ═══════════════════════════════════════════════
    //  PEDIDOS — EXCEL
    // ═══════════════════════════════════════════════

    @GetMapping("/pedidos/excel")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> pedidosExcel() throws Exception {
        return excelResponse(pedidoReportService.generarExcelPedidos(), "pedidos.xlsx");
    }

    // ═══════════════════════════════════════════════
    //  HELPERS
    // ═══════════════════════════════════════════════

    private ResponseEntity<byte[]> pdfResponse(byte[] data, String filename) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(data);
    }

    private ResponseEntity<byte[]> excelResponse(byte[] data, String filename) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(data);
    }
}
