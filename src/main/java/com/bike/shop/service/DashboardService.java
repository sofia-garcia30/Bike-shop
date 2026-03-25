package com.bike.shop.service;

import com.bike.shop.repository.BicicletaRepository;
import com.bike.shop.repository.ClienteRepository;
import com.bike.shop.repository.VentaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final VentaRepository ventaRepository;
    private final ClienteRepository clienteRepository;
    private final BicicletaRepository bicicletaRepository;

    public Map<String, Object> getDashboard() {
        Map<String, Object> data = new HashMap<>();
        data.put("totalVentas", ventaRepository.count());
        data.put("totalClientes", clienteRepository.count());
        data.put("totalBicicletas", bicicletaRepository.count());
        data.put("stockBajo", bicicletaRepository.findStockBajo().size());
        data.put("sinStock", bicicletaRepository.findSinStock().size());
        return data;
    }

    public List<Map<String, Object>> getTopBicicletas() {
        List<Object[]> resultados = ventaRepository.findTopBicicletas();
        List<Map<String, Object>> top = new ArrayList<>();
        for (Object[] fila : resultados) {
            Map<String, Object> item = new HashMap<>();
            item.put("codigo", fila[0]);
            item.put("marca", fila[1]);
            item.put("modelo", fila[2]);
            item.put("totalVendido", fila[3]);
            top.add(item);
        }
        return top;
    }
}