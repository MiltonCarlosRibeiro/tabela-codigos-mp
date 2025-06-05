package br.com.pakmatic.tabelacodigosmp.service;

import br.com.pakmatic.tabelacodigosmp.dto.LinhaResultadoDTO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;

/**
 * Serviço que busca apenas nas colunas A e B (índices 0 e 1),
 * mas retorna a linha completa da planilha se encontrar correspondência.
 */
@Service
public class TabelaCodigoService {

    private static final String CAMINHO_ARQUIVO = "U:/IFS - MILTON/LISTA MATÉRIA_Mod.xlsm";

    public List<LinhaResultadoDTO> buscarLinhasQueContem(String codigo) {
        List<LinhaResultadoDTO> resultados = new ArrayList<>();
        String codigoBusca = limpar(codigo);

        try (FileInputStream fis = new FileInputStream(new File(CAMINHO_ARQUIVO));
             Workbook workbook = new XSSFWorkbook(fis)) {

            for (int s = 0; s < workbook.getNumberOfSheets(); s++) {
                Sheet sheet = workbook.getSheetAt(s);
                String nomeAba = sheet.getSheetName();
                Iterator<Row> rowIterator = sheet.iterator();

                List<String> headers = new ArrayList<>();
                boolean primeiraLinha = true;

                while (rowIterator.hasNext()) {
                    Row row = rowIterator.next();

                    if (primeiraLinha) {
                        for (Cell cell : row) {
                            headers.add(getValorCelula(cell));
                        }
                        primeiraLinha = false;
                        continue;
                    }

                    // Verifica colunas A (0) e B (1)
                    String colA = limpar(getValorCelula(row.getCell(0)));
                    String colB = limpar(getValorCelula(row.getCell(1)));

                    if (colA.contains(codigoBusca) || colB.contains(codigoBusca)) {
                        Map<String, String> linha = new LinkedHashMap<>();
                        int colunasTotais = Math.max(row.getLastCellNum(), headers.size());

                        for (int i = 0; i < colunasTotais; i++) {
                            Cell cell = row.getCell(i);
                            String valor = getValorCelula(cell);
                            String header = i < headers.size() ? headers.get(i) : "Coluna " + (i + 1);
                            linha.put(header, valor);
                        }

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

        try {
            switch (cell.getCellType()) {
                case STRING:
                    return cell.getStringCellValue();

                case NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        return cell.getDateCellValue().toString();
                    }
                    return String.valueOf(cell.getNumericCellValue());

                case BOOLEAN:
                    return String.valueOf(cell.getBooleanCellValue());

                case FORMULA:
                    // Retorna valor visível da fórmula
                    switch (cell.getCachedFormulaResultType()) {
                        case STRING:
                            return cell.getStringCellValue();
                        case NUMERIC:
                            return String.valueOf(cell.getNumericCellValue());
                        case BOOLEAN:
                            return String.valueOf(cell.getBooleanCellValue());
                        default:
                            return cell.getCellFormula(); // fallback
                    }

                default:
                    return "";
            }
        } catch (Exception e) {
            return "";
        }
    }

    private String limpar(String valor) {
        return valor == null ? "" : valor.trim().toUpperCase();
    }
}
