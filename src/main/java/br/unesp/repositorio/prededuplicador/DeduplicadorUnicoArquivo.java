package br.unesp.repositorio.prededuplicador;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

public class DeduplicadorUnicoArquivo {
	private Map<String, Set<String>> indices;
	private File arquivoCsvAnalisar;
	private File arquivoCsvDuplicados;
	private File arquivoCsvNaoDuplicados;
	private String[] cabecalhoCsvAnalisar;
	private Comparador comparador;

	public DeduplicadorUnicoArquivo(String[] cabecalhoCsvAnalisar,
			String caminhoCsvAnalisar, String caminhoCsvDuplicados,
			String caminhoCsvNaoDuplicados, List<String> metadadosSelecionados,
			Comparador comparador) {
		this.cabecalhoCsvAnalisar = cabecalhoCsvAnalisar;
		arquivoCsvAnalisar = new File(caminhoCsvAnalisar);
		arquivoCsvDuplicados = new File(caminhoCsvDuplicados);
		arquivoCsvNaoDuplicados = new File(caminhoCsvNaoDuplicados);
		this.comparador = comparador;
		criarIndices(metadadosSelecionados);
	}

	private void criarIndices(List<String> metadadosSelecionados) {
		indices = new HashMap<String, Set<String>>();
		for (String metadado : metadadosSelecionados) {
			indices.put(metadado, new HashSet<String>());
		}
	}

	public void deduplicar() throws IOException {

		CSVParser parserCsvAnalisar = CSVParser.parse(arquivoCsvAnalisar,
				Charset.forName("utf-8"),
				CSVFormat.DEFAULT.withHeader(cabecalhoCsvAnalisar));
		CSVPrinter printerCsvDuplicados = new CSVPrinter(new PrintWriter(
				arquivoCsvDuplicados, "utf-8"),
				CSVFormat.DEFAULT.withHeader(cabecalhoCsvAnalisar));
		CSVPrinter printerCsvNaoDuplicados = new CSVPrinter(new PrintWriter(
				arquivoCsvNaoDuplicados, "utf-8"),
				CSVFormat.DEFAULT.withHeader(cabecalhoCsvAnalisar));

		for (CSVRecord linhaCsv : parserCsvAnalisar.getRecords()) {
			boolean existeValorIgual = comparador == Comparador.E;
			System.out.println("registro id: "+linhaCsv.get("id"));
			for (String metadado : indices.keySet()) {
				System.out.println("verificando metadado: "+metadado);
				String valor = linhaCsv.get(metadado);
				if (valor.contains("||")) {
					for (String v : valor.split("\\|\\|")) {
						if (!v.trim().isEmpty()) {
							String valorNormalizado = normaliza(v);
							System.out.println("Valor normalizado: " + valorNormalizado);
							boolean existeMetadado = indices.get(metadado)
									.contains(valorNormalizado);
							System.out.println("Foi encontrado no indice?(true/false) :"+existeMetadado);
							existeValorIgual = comparar(existeValorIgual,
									existeMetadado);
							indices.get(metadado).add(valorNormalizado);
						} 

					}
				} else {
					if (!valor.trim().isEmpty()) {
						String valorNormalizado = normaliza(valor);
						System.out.println("Valor normalizado: " + valorNormalizado);
						boolean existeMetadado = indices.get(metadado)
								.contains(valorNormalizado);
						System.out.println("Foi encontrado no indice?(true/false) :"+existeMetadado);
						existeValorIgual = comparar(existeValorIgual,
								existeMetadado);
						indices.get(metadado).add(valorNormalizado);
					} 
				}
			}
			if (existeValorIgual) {
				printerCsvDuplicados.printRecord(IteratorUtils.toArray(linhaCsv
						.iterator()));
				printerCsvDuplicados.flush();
			} else {
				printerCsvNaoDuplicados.printRecord(IteratorUtils
						.toArray(linhaCsv.iterator()));
				printerCsvNaoDuplicados.flush();
			}
		}

		parserCsvAnalisar.close();
		printerCsvDuplicados.close();
		printerCsvNaoDuplicados.close();
	}

	private String normaliza(String texto) {
		texto = texto.toLowerCase();
		texto = texto.replaceAll("[\\p{Punct}]", "");
		texto = Normalizer.normalize(texto, Normalizer.Form.NFD).replaceAll(
				"[^\\p{ASCII}]", "");
		texto = texto.trim().replaceAll("\\s+", " ");
		return texto;
	}

	private boolean comparar(boolean valor1, boolean valor2) {
		if (comparador == Comparador.E) {
			return valor1 && valor2;
		} else if (comparador == Comparador.OU) {
			return valor1 || valor2;
		} else {
			return false;
		}
	}
}
