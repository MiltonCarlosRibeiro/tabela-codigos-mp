package br.com.pakmatic.tabelacodigosmp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class LinhaResultadoDTO {
    private String aba;
    private Map<String, String> colunas;
}
