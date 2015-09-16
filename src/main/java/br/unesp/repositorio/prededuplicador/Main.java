package br.unesp.repositorio.prededuplicador;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import br.unesp.repositorio.prededuplicador.acoes.AcaoProcurarCsv;
import br.unesp.repositorio.prededuplicador.acoes.AcaoSalvarCsv;

public class Main {

	private static final class AcaoCarregarMetadados implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			try {
				Scanner scanner = new Scanner(new File(campoCsvAnalisar.getText()));
				cabecalhoCsvAnalisar = scanner.nextLine().replaceAll("\\\"", "").split(",");
				listaMetadados.setListData(cabecalhoCsvAnalisar);
				scanner.close();
			} catch (Exception exception) {
				JOptionPane.showMessageDialog(null, "Erro ao carregar o cabeçalho do arquivo a ser analizado" ,"Erro", JOptionPane.ERROR_MESSAGE);
			}

		}
	}

	private static final class AcaoDeduplicar implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			try {
				if(listaMetadados.getSelectedValuesList().isEmpty()){
					JOptionPane.showMessageDialog(null, "Selecione as colunas a analisar" ,"Erro", JOptionPane.ERROR_MESSAGE);
				}else{
					if(campoCsvMatriz.getText().isEmpty()){
						new DeduplicadorUnicoArquivo(cabecalhoCsvAnalisar,
								campoCsvAnalisar.getText(),
								campoCsvResultanteDuplicados.getText(),
								campoCsvResultanteNaoDuplicados.getText(),
								listaMetadados.getSelectedValuesList(),
								(Comparador) opcaoMetodoComparacao.getSelectedItem()).deduplicar();
						JOptionPane.showMessageDialog(null, "Deduplicado com sucesso" ,"Sucesso", JOptionPane.INFORMATION_MESSAGE);
					}else{
						new DeduplicadorPorMatriz(cabecalhoCsvAnalisar,
								campoCsvMatriz.getText(),
								campoCsvAnalisar.getText(),
								campoCsvResultanteDuplicados.getText(),
								campoCsvResultanteNaoDuplicados.getText(),
								listaMetadados.getSelectedValuesList(),
								(Comparador) opcaoMetodoComparacao.getSelectedItem()).deduplicar();
						JOptionPane.showMessageDialog(null, "Deduplicado com sucesso" ,"Sucesso", JOptionPane.INFORMATION_MESSAGE);
					}
				}

			} catch (Exception exception) {
				JOptionPane.showMessageDialog(null, exception.getMessage() ,"Erro", JOptionPane.ERROR_MESSAGE);
			}

		}
	}

	private static String[] cabecalhoCsvAnalisar;
	private static JLabel rotuloCsvMatriz;
	private static JLabel rotuloCsvAnalisar;
	private static JLabel rotuloListaMetadados;
	private static JLabel rotuloCsvResultanteDuplicados;
	private static JLabel rotuloCsvResultanteNaoDuplicados;
	private static JButton botaoCarregarMetadados;
	private static JButton botaoProcurarCsvMatriz;
	private static JButton botaoProcurarCsvAnalisar;
	private static JButton botaoProcurarCsvResultanteDuplicados;
	private static JButton botaoProcurarCsvResultanteNaoDuplicados;
	private static JButton botaoProcessar;
	private static JTextField campoCsvAnalisar;
	private static JTextField campoCsvMatriz;
	private static JTextField campoCsvResultanteDuplicados;
	private static JTextField campoCsvResultanteNaoDuplicados;
	private static JList<String> listaMetadados;
	private static JScrollPane painelLista;
	private static JComboBox<Comparador> opcaoMetodoComparacao;
	private static Container conteudo;
	private static JLabel rotuloOpcao;

	public static void main(String[] args) {
		montaGui();

	}

	private static void montaGui() {
		JFrame janela = new JFrame("Pr�-deduplicador de registros");
		janela.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		inicializaComponentesJanela(janela);
		desenhaJanela(janela);

		janela.setVisible(true);

	}

	private static void desenhaJanela(JFrame janela) {
		conteudo.setLayout(null);

		conteudo.add(rotuloCsvMatriz);
		rotuloCsvMatriz.setBounds(5, 5, 100, 20);
		conteudo.add(campoCsvMatriz);
		campoCsvMatriz.setBounds(110, 5, 275, 25);
		conteudo.add(botaoProcurarCsvMatriz);
		botaoProcurarCsvMatriz.setBounds(400, 5, 75, 25);


		conteudo.add(rotuloCsvAnalisar);
		rotuloCsvAnalisar.setBounds(5, 35, 100, 20);
		conteudo.add(campoCsvAnalisar);
		campoCsvAnalisar.setBounds(110, 35, 275, 25);
		conteudo.add(botaoProcurarCsvAnalisar);
		botaoProcurarCsvAnalisar.setBounds(400, 35, 75, 25);

		conteudo.add(botaoCarregarMetadados);
		botaoCarregarMetadados.setBounds(5, 70, 470, 25);


		conteudo.add(rotuloListaMetadados);
		rotuloListaMetadados.setBounds(5, 105, 295, 20);
		conteudo.add(painelLista);
		painelLista.setBounds(5, 135, 295, 100);
		painelLista.setViewportView(listaMetadados);
		painelLista.setVerticalScrollBarPolicy(
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		conteudo.add(rotuloOpcao);
		rotuloOpcao.setBounds(310,105,150,20);
		conteudo.add(opcaoMetodoComparacao);
		opcaoMetodoComparacao.setBounds(310, 135, 150, 20);

		conteudo.add(rotuloCsvResultanteDuplicados);
		rotuloCsvResultanteDuplicados.setBounds(5, 270, 200, 20);
		conteudo.add(campoCsvResultanteDuplicados);
		campoCsvResultanteDuplicados.setBounds(205, 270, 180, 25);
		conteudo.add(botaoProcurarCsvResultanteDuplicados);
		botaoProcurarCsvResultanteDuplicados.setBounds(400, 270, 75, 25);

		conteudo.add(rotuloCsvResultanteNaoDuplicados);
		rotuloCsvResultanteNaoDuplicados.setBounds(5, 305, 200, 20);
		conteudo.add(campoCsvResultanteNaoDuplicados);
		campoCsvResultanteNaoDuplicados.setBounds(205, 305, 180, 25);
		conteudo.add(botaoProcurarCsvResultanteNaoDuplicados);
		botaoProcurarCsvResultanteNaoDuplicados.setBounds(400, 305, 75, 25);

		conteudo.add(botaoProcessar);
		botaoProcessar.setBounds(5, 340, 470, 25);


		conteudo.setBounds(0,0,500,500);
		janela.setBounds(0,0,500,500);
	}

	private static void inicializaComponentesJanela(JFrame janela) {
		rotuloCsvMatriz = new JLabel("CSV Matriz");
		rotuloCsvAnalisar = new JLabel("CSV a analisar");
		rotuloListaMetadados = new JLabel("Lista de metadados");
		rotuloCsvResultanteDuplicados = new JLabel("CSV resultante com duplicados");
		rotuloCsvResultanteNaoDuplicados = new JLabel("CSV resultante sem duplicados");
		rotuloOpcao = new JLabel("Comparador");
		botaoCarregarMetadados = new JButton("Carregar lista de metadados");
		botaoProcurarCsvMatriz = new JButton("...");
		botaoProcurarCsvAnalisar = new JButton("...");
		botaoProcurarCsvResultanteDuplicados = new JButton("...");
		botaoProcurarCsvResultanteNaoDuplicados = new JButton("...");
		botaoProcessar = new JButton("Processar");
		campoCsvAnalisar = new JTextField("",30);
		campoCsvMatriz = new JTextField("",30);
		campoCsvResultanteDuplicados = new JTextField("",30);
		campoCsvResultanteNaoDuplicados = new JTextField("",30);
		listaMetadados = new JList<String>();
		painelLista = new JScrollPane();
		conteudo = janela.getContentPane();
		opcaoMetodoComparacao = new JComboBox<Comparador>(new Comparador[]{Comparador.E,Comparador.OU});


		botaoCarregarMetadados.addActionListener(new AcaoCarregarMetadados());
		botaoProcurarCsvMatriz.addActionListener(new AcaoProcurarCsv(campoCsvMatriz));
		botaoProcurarCsvAnalisar.addActionListener(new AcaoProcurarCsv(campoCsvAnalisar));
		botaoProcurarCsvResultanteDuplicados.addActionListener(new AcaoSalvarCsv(campoCsvResultanteDuplicados));
		botaoProcurarCsvResultanteNaoDuplicados.addActionListener(new AcaoSalvarCsv(campoCsvResultanteNaoDuplicados));
		botaoProcessar.addActionListener(new AcaoDeduplicar());
	}

}
