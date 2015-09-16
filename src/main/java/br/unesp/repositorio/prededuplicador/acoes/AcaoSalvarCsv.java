package br.unesp.repositorio.prededuplicador.acoes;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

public class AcaoSalvarCsv implements
		ActionListener {
	private JTextField campoDestino;
	
	public AcaoSalvarCsv(JTextField campodestino) {
		this.campoDestino = campodestino;
	}
	
	public void actionPerformed(ActionEvent e) {
		JFileChooser fc = new JFileChooser();
		fc.setFileFilter(new FileNameExtensionFilter("Arquivo CSV", "csv"));
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setAcceptAllFileFilterUsed(true);
		fc.showSaveDialog(null);
		if(fc.getSelectedFile()!=null){
			String caminho = fc.getSelectedFile().getAbsolutePath();
			if(!caminho.endsWith(".csv"))caminho=caminho+".csv";
			campoDestino.setText(caminho);
		}
		
	}
}