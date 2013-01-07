package villani.eti.br;

import java.io.FileWriter;
import java.util.TreeMap;

import mulan.classifier.lazy.BRkNN;
import mulan.classifier.lazy.MLkNN;
import mulan.data.MultiLabelInstances;
import mulan.evaluation.Evaluator;
import mulan.evaluation.MultipleEvaluation;

public class Classificadores {

	private static LogBuilder log;
	private static TreeMap<String, String> entradas;
	private static String dataset;
	private static int folds;

	public static void setLog(LogBuilder log) {
		Classificadores.log = log;
	}

	public static void setEntradas(TreeMap<String, String> entradas) {
		Classificadores.entradas = entradas;
		dataset = Classificadores.entradas.get("dataset");
		folds = Integer.parseInt(Classificadores.entradas.get("folds"));


	}

	public static void avalia(MultiLabelInstances instanciasML) {
		log.write("- Instanciando avaliador");
		Evaluator avaliador = new Evaluator();
		int numFolds = folds;

		log.write("- Instanciando registradores dos resultados");
		MultipleEvaluation avaliacao;
		FileWriter resultados;
		try {

			resultados = new FileWriter(dataset + ".results");

			try{
				log.write("- Instanciando classificador 01");
				MLkNN classificador01 = new MLkNN();
				log.write("- Avaliando classificador " + classificador01.getClass());
				avaliacao = avaliador.crossValidate(classificador01, instanciasML, numFolds);
				resultados.write("RESULTADOS DO CLASSIFICADOR " + classificador01.getClass());
				resultados.write("\n=========================================\n\n");
				resultados.write(avaliacao.toString());
				resultados.write("\n=========================================\n\n");
			} catch(Exception e){
				log.write("- Falha ao avaliar o classificador 01: " + e.getMessage());
			}

			try{
				log.write("- Instanciando classificador 02");
				BRkNN classificador02 = new BRkNN(10);
				log.write("- Avaliando classificador " + classificador02.getClass());
				avaliacao = avaliador.crossValidate(classificador02, instanciasML, numFolds);
				resultados.write("RESULTADOS DO CLASSIFICADOR " + classificador02.getClass());
				resultados.write("\n=========================================\n\n");
				resultados.write(avaliacao.toString());
				resultados.write("\n=========================================\n\n");
			} catch(Exception e){
				log.write("- Falha ao avaliar o classificador 02: " + e.getMessage());
			}

			resultados.close();

		} catch (Exception e) {
			log.write("- Falha ao instancias registradores: " + e.getMessage());
		}
	}

}
