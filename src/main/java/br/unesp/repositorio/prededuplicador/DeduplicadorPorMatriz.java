package br.unesp.repositorio.prededuplicador;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

public class DeduplicadorPorMatriz {
	
	private Map<String,Set<String>> indices;
	private File arquivoCsvMatriz;
	private File arquivoCsvAnalisar;
	private File arquivoCsvDuplicados;
	private File arquivoCsvNaoDuplicados;
	private String[] cabecalhoCsvAnalisar;
	private Comparador comparador;

	public DeduplicadorPorMatriz(String[] cabecalhoCsvAnalisar, String caminhoCsvMatriz,
			String caminhoCsvAnalisar, String caminhoCsvDuplicados, String caminhoCsvNaoDuplicados,
			List<String> metadadosSelecionados, Comparador comparador) {	
		this.cabecalhoCsvAnalisar = cabecalhoCsvAnalisar;
		arquivoCsvMatriz = new File(caminhoCsvMatriz);
		arquivoCsvAnalisar = new File(caminhoCsvAnalisar);
		arquivoCsvDuplicados = new File(caminhoCsvDuplicados);
		arquivoCsvNaoDuplicados = new File(caminhoCsvNaoDuplicados);
		this.comparador = comparador;
		criarIndices(metadadosSelecionados);
	}

	private void criarIndices(List<String> metadadosSelecionados) {
		indices = new HashMap<String, Set<String>>();
		for(String metadado : metadadosSelecionados){
			indices.put(metadado, new HashSet<String>());
		}
	}
	
	

	public void deduplicar() throws IOException {
		CSVParser parserCsvMatriz = CSVParser.parse(arquivoCsvMatriz, Charset.forName("utf-8"), CSVFormat.DEFAULT.withHeader(extrairCabecalho(arquivoCsvMatriz)));
		carregarIndices(parserCsvMatriz);
		parserCsvMatriz.close();
		
		
		CSVParser parserCsvAnalisar = CSVParser.parse(arquivoCsvAnalisar, Charset.forName("utf-8"), CSVFormat.DEFAULT.withHeader(cabecalhoCsvAnalisar));
		CSVPrinter printerCsvDuplicados = new CSVPrinter(new PrintWriter(arquivoCsvDuplicados, "utf-8"), CSVFormat.DEFAULT.withHeader(cabecalhoCsvAnalisar));
		CSVPrinter printerCsvNaoDuplicados = new CSVPrinter(new PrintWriter(arquivoCsvNaoDuplicados, "utf-8"), CSVFormat.DEFAULT.withHeader(cabecalhoCsvAnalisar));
		
		for (CSVRecord linhaCsv : parserCsvAnalisar.getRecords()) {
			boolean existeValorIgual = comparador==Comparador.E;
			for(String metadado : indices.keySet()){
				String valor = linhaCsv.get(metadado);
				if(valor.contains("||")){
					for(String v: valor.split("\\|\\|")){
						if(!v.trim().isEmpty())
						existeValorIgual = comparar ( existeValorIgual , (indices.get(metadado).contains(normaliza(v))));
					}
				}else{
					if(!valor.trim().isEmpty())
						existeValorIgual = comparar( existeValorIgual , (indices.get(metadado).contains(normaliza(valor))));
				}
			}
			if(existeValorIgual){
				printerCsvDuplicados.printRecord(IteratorUtils.toArray(linhaCsv.iterator()));
				printerCsvDuplicados.flush();
			}else{
				printerCsvNaoDuplicados.printRecord(IteratorUtils.toArray(linhaCsv.iterator()));
				printerCsvNaoDuplicados.flush();
			}
		}
		
		
		parserCsvAnalisar.close();
		printerCsvDuplicados.close();
		printerCsvNaoDuplicados.close();
	}

	private void carregarIndices(CSVParser parserCsvMatriz) throws IOException {
		for (CSVRecord linhaCsv : parserCsvMatriz.getRecords()) {
			for(String metadado : indices.keySet()){
				String valor = linhaCsv.get(metadado);
				if(valor.contains("||")){
					for(String v: valor.split("\\|\\|")){
						if(!v.trim().isEmpty())
							indices.get(metadado).add(normaliza(v));
					}
				}else{
					if(!valor.trim().isEmpty())
						indices.get(metadado).add(normaliza(valor));
				}
			}
		}
		
	}

	private String[] extrairCabecalho(File arquivoCsvMatriz) throws FileNotFoundException {
		String[] cabecalho = null;
		Scanner scanner = new Scanner(arquivoCsvMatriz);
		cabecalho = scanner.nextLine().replaceAll("\\\"", "").split(",");
		scanner.close();
		return cabecalho;
		
	}
	
	private String normaliza(String texto){
		texto = texto.toLowerCase();
		texto = texto.replaceAll("[\\p{Punct}]", "");
		texto = Normalizer.normalize(texto, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
		texto = texto.trim().replaceAll("\\s{2,}", " ");
		return texto;
	}
	
	private boolean comparar(boolean valor1, boolean valor2){
		if(comparador==Comparador.E){
			return valor1 && valor2;
		}else if(comparador==Comparador.OU){
			return valor1 || valor2;
		}else{
			return false;
		}
	}

}
