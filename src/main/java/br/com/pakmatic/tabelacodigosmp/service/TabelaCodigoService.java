package br.com.pakmatic.tabelacodigosmp.service;

import br.com.pakmatic.tabelacodigosmp.dto.LinhaResultadoDTO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;

/**
 * Serviço que varre TODAS as abas e TODAS as colunas da planilha Excel,
 * mesmo além dos headers visíveis.
 */
@Service
public class TabelaCodigoService {

    private static final String CAMINHO_ARQUIVO = "C:/tabela-codigos-mp/LISTA MATÉRIA_Mod.xlsm";

    public List<LinhaResultadoDTO> buscarLinhasQueContem(String codigo) {
        List<LinhaResultadoDTO> resultados = new ArrayList<>();
        String codigoBusca = limpar(codigo);

        try (FileInputStream fis = new FileInputStream(new File(CAMINHO_ARQUIVO));
             Workbook workbook = new XSSFWorkbook(fis)) {

            for (int s = 0; s < workbook.getNumberOfSheets(); s++) {
                Sheet sheet = workbook.getSheetAt(s);
                String nomeAba = sheet.getSheetName();

                for (Row row : sheet) {
                    Map<String, String> linha = new LinkedHashMap<>();
                    boolean contemCodigo = false;

                    int colunasTotais = row.getLastCellNum();
                    for (int i = 0; i < colunasTotais; i++) {
                        Cell cell = row.getCell(i);
                        String valor = getValorCelula(cell);
                        String header = "Coluna " + (i + 1);

                        linha.put(header, valor);

                        if (limpar(valor).contains(codigoBusca)) {
                            contemCodigo = true;
                        }
                    }

                    if (contemCodigo) {
                        resultados.add(new LinhaResultadoDTO(nomeAba, linha));
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultados;
    }

    private String getValorCelula(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)) {
                    yield cell.getDateCellValue().toString();
                } else {
                    yield String.valueOf(cell.getNumericCellValue());
                }
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> cell.getCellFormula();
            default -> "";
        };
    }

    private String limpar(String valor) {
        return valor == null ? "" : valor.trim().toUpperCase();
    }
}
