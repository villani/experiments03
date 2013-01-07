package villani.eti.br;

import java.io.File;
import java.util.TreeMap;

import mulan.data.MultiLabelInstances;

public class Amostras {

	private static LogBuilder log;
	private static TreeMap<String, String> entradas;
	private static String arff;
	private static String xml;

	public static void setLog(LogBuilder log) {
		Amostras.log = log;
	}

	public static void setEntradas(TreeMap<String, String> entradas) {
		Amostras.entradas = entradas;
		arff = entradas.get("dataset") + ".arff";
		xml = entradas.get("dataset") + ".xml";
	}

	public static MultiLabelInstances obtem() {
		MultiLabelInstances instanciasML = null;
		try {
			File arffFile = new File(arff);
			File xmlFile = new File(xml);
			if (arffFile.exists()) {
				log.write("- Conjunto de amostras encontrado em: " + arffFile.getName());
				if(xmlFile.exists()) log.write("- Arquivo xml encontrado em: " + xmlFile.getName());
				else log.write("- Arquivo xml NAO encontrado em: " + xmlFile.getName());
				instanciasML = new MultiLabelInstances(arffFile.getName(), xmlFile.getName());
			} else {
				log.write("- O conjunto de amostras ainda nao existe em: " + arffFile.getAbsolutePath());
				log.write("Obtendo novo conjunto de amostras: ");
				Caracteristicas.setLog(log);
				Caracteristicas.setEntradas(entradas);
				instanciasML = Caracteristicas.obtemLBP();
			}
		} catch (Exception e) {
			log.write("- Falha ao obter conjunto de amostras: " + e.fillInStackTrace());
			System.exit(0);
		}
		return instanciasML;
	}

}
