package villani.eti.br;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.util.TreeMap;

import mulan.data.MultiLabelInstances;
import de.florianbrucker.ml.lbp.LBPModel;
import de.florianbrucker.ml.lbp.LBPParameters;

public class Caracteristicas {

	private static LogBuilder log;
	private static TreeMap<String, String> entradas;
	private static File folder;
	private static String dataset;
	private static String csv;
	private static String txt;
	private static int images;
	private static int bins;
	private static int vizinhos;
	private static int raio;

	public static void setLog(LogBuilder log) {
		Caracteristicas.log = log;
	}

	public static void setEntradas(TreeMap<String, String> entradas) {
		Caracteristicas.entradas = entradas;
		folder = new File(Caracteristicas.entradas.get("folder"));
		dataset = Caracteristicas.entradas.get("dataset");
		csv = Caracteristicas.entradas.get("csv");
		txt = Caracteristicas.entradas.get("txt");
		images = Integer.parseInt(Caracteristicas.entradas.get("images"));
		bins = Integer.parseInt(Caracteristicas.entradas.get("bins"));
		vizinhos = Integer.parseInt(Caracteristicas.entradas.get("vizinhos"));
		raio = Integer.parseInt(Caracteristicas.entradas.get("raio"));
	}

	public static MultiLabelInstances obtemLBP() {

		log.write("Construindo conjunto de instancias LBP");
		MultiLabelInstances instanciasML = null;

		log.write("- Obtendo o conjunto de rotulos IRMA");
		XmlIrmaCodeBuilder xicb;
		try {
			log.write("- Criando arquivo xml com a estrutura de codigos IRMA");
			xicb = new XmlIrmaCodeBuilder(txt, dataset);
			if (xicb.hasXml())
				log.write("- Arquivo xml com a estrutura de codigo IRMA criado com exito");
		} catch (IOException e) {
			log.write("- Falha ao obter relacao nome da imagem/ codigo IRMA: "
					+ e.getMessage());
			System.exit(0);
		}

		log.write("- Definindo atributos do conjunto");
		try {
			RelationBuilder instanciasLBP = new RelationBuilder(dataset);
			for (int i = 0; i < bins; i++)
				instanciasLBP.defineAttribute("lbp" + i, "numeric");

			log.write("- Salvando a lista de atributos e incluindo a lista de rotulos a partir do xml");
			instanciasLBP.saveAttributes();

			log.write("Armazenando no conjunto as amostras com caracteristicas LBP");
			try {
				log.write(" - Obtendo a relacao nome da imagem/codigo IRMA do arquivo: " + csv);
				File relacaoImagemCodigo = new File(csv);
				TreeMap<String, String> relacao = new TreeMap<String, String>();
				Scanner leitor01 = new Scanner(relacaoImagemCodigo);
				while (leitor01.hasNextLine()) {
					String[] campos = leitor01.nextLine().split(";");
					relacao.put(campos[0], campos[1]);
				}
				leitor01.close();

				log.write("- Criando objeto que converte o codigo IRMA para binario e que tambem necessita do xml criado anteriormente");
				IrmaCode conversor = new IrmaCode(dataset);

				log.write("- Obtendo caracteristicas LBP para cada imagem");
				File[] imagens = folder.listFiles();
				int qtdeImagens = 0;
				for (File imagem : imagens) {
					if(qtdeImagens == images) break;
					//if(!imagem.canRead()) System.out.println(imagem.getAbsolutePath());

					// Defino os parametros do operador LBP
					LBPParameters p = new LBPParameters(vizinhos, raio, bins);
					
					// Construo o extrator e forneco a imagem para obter caracteristicas
					LBPModel extrator = new LBPModel(p, imagem);

					// Obtenho o histograma LBP
					float[] histLBP = extrator.subModels[0].patternHist;

					// Crio uma amostra para armazenar as caracteristicas obtidas
					String amostra = "";
					for (float lbp : histLBP) amostra += lbp + ",";

					// Armazeno o respectivo rotulo IRMA binario a amostra
					String nomeImg = imagem.getName().split("\\.")[0];
					amostra += conversor.toBinary(relacao.get(nomeImg));

					// Armazeno a amostra no conjunto de dados
					instanciasLBP.insertData(amostra);
					
					qtdeImagens++;
				}
				
				log.write("Novo conjunto de amostras salvo em: " + dataset + ".arff");
				instanciasML = instanciasLBP.saveRelation(); // armazenando o retorno do método

			} catch (FileNotFoundException e) {
				log.write("Um arquivo não pode ser encontrado: " + e.getMessage());
				System.exit(0);
			} catch (IOException e) {
				log.write("Falha ao ler uma imagem: " + e.getMessage());
				System.exit(0);
			} catch (Exception e) {
				log.write("Falha ao inserir a amostra: " + e.getMessage());
				System.exit(0);
			}

		} catch (Exception e) {
			log.write("Falha ao construir conjunto de instancias LBP: "
					+ e.getMessage());
			System.exit(0);
		}
		
		return instanciasML;
	}
}