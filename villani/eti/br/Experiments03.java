package villani.eti.br;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.TreeMap;

import mulan.data.MultiLabelInstances;

public class Experiments03 {
	
	private static LogBuilder log;
	private static TreeMap<String,String> entradas;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		log = new LogBuilder("Experiments03.log");
		log.write("Iniciando experimento 3:");
		log.write("Obtendo as entradas do sistema a partir de conf.ini.");
		try{
			init();
		} catch(FileNotFoundException fnfe){
			log.write("Falha ao receber as entradas do sistema: " + fnfe.getMessage());
			System.exit(0);
		}
		
		log.write("Entradas obtidas.");
		log.write("Lendo entradas:");
		log.write("- Conjunto de amostras: " + entradas.get("dataset"));
		log.write("- Caminho da pasta de imagens: " + entradas.get("folder"));
		log.write("- Arquivo com a relacao imagem/IRMA: " + entradas.get("csv"));
		log.write("- Arquivo estrutura codigo irma: " + entradas.get("txt"));
		log.write("- Quantidade de imagens que serao utilizadas: " + entradas.get("images"));
		
		log.write("Obtendo conjunto de amostras:");
		Amostras.setLog(log);
		Amostras.setEntradas(entradas);
		MultiLabelInstances instanciasML = Amostras.obtem();
		
		log.write("Avaliando classificadores: ");
		Classificadores.setLog(log);
		Classificadores.setEntradas(entradas);
		Classificadores.avalia(instanciasML);
		
		log.write("Fim do experimento");
		log.close();

	}
	
	public static boolean init() throws FileNotFoundException{
		File conf = new File("./conf.ini");
		Scanner leitor = new Scanner(conf);
		entradas = new TreeMap<String,String>();
		while(leitor.hasNextLine()){
			String linha = leitor.nextLine();
			String parametros[] = linha.split("=");
			if(parametros.length < 2) continue;
			entradas.put(parametros[0], parametros[1]);
		}
		leitor.close();
		return true;
	}

}
