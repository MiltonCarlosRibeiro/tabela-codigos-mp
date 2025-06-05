package br.com.pakmatic.tabelacodigosmp.controller;

import br.com.pakmatic.tabelacodigosmp.dto.LinhaResultadoDTO;
import br.com.pakmatic.tabelacodigosmp.service.TabelaCodigoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class TabelaCodigoController {

    @Autowired
    private TabelaCodigoService service;

    @GetMapping("/buscar")
    public List<LinhaResultadoDTO> buscarPorCodigo(@RequestParam("codigo") String codigo) {
        return service.buscarLinhasQueContem(codigo);
    }
}
