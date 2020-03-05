package com.ufrn.br;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

public class Main {
	private static List<String> ids = new ArrayList<String>();
	private static List<String> animes = new ArrayList<String>();
	private static List<Integer[]> colunas = new ArrayList<Integer[]>();
	static Map<String, List<HashMap<String, Integer>>> usersData = new HashMap<>();
	static HashMap<String, Integer> newUser;

	
	public static void main(String[] args) {
		List <DataCSV> resultado = LerCsv();
		ContarUsuarios(resultado);
		System.out.println(ids);
		System.out.println(animes);
		Integer[][] matriz = construirMatrizes();
		Double[][] matrizSimilaridade = calcularSimilaridade(matriz);
		printCosineMatrix(matrizSimilaridade, colunas.size());
	}
	
	public static List<DataCSV> LerCsv() {
		Reader reader = null;
		try {
			reader = Files.newBufferedReader(Paths.get("rating2.csv"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        CsvToBean<DataCSV> csvToBean = new CsvToBeanBuilder(reader)
                .withType(DataCSV.class)
                .build();

        List<DataCSV> pessoas = csvToBean.parse();

        return pessoas;

	}
	
	public static void ContarUsuarios(List<DataCSV> resultado) {
		for(DataCSV data : resultado) {
			newUser = new HashMap<String, Integer>();
			List<HashMap<String, Integer>> newRec = new ArrayList<HashMap<String, Integer>>();
			
			if(!ids.contains(data.getUser_id())) {
				ids.add(data.getUser_id());
			}
			
			if(!animes.contains(data.getAnime_id())) {
				animes.add(data.getAnime_id());
			}
			
			if(!usersData.containsKey(data.getUser_id())) {
				newUser.put(data.getAnime_id(), data.getRating());
				newRec.add(newUser);
				usersData.put(data.getUser_id(), newRec);
				
			} else {
				newUser.put(data.getAnime_id(), data.getRating());
				usersData.get(data.getUser_id()).add(newUser);
			}
		}
	}
	
	public static Integer[][] construirMatrizes() {
		Integer[][] matriz = new Integer[ids.size()][animes.size()];
		int index1 = 0;
		
		for (String user : ids) {
			int index2 = 0;
			for (String anime : animes) {
				for(HashMap<String, Integer> rec : usersData.get(user)) {
					if(rec.containsKey(anime)) {
						if(rec.get(anime) == -1) {
							matriz[index1][index2] = 0;
						}else {
							matriz[index1][index2] = rec.get(anime);
						}						
					}
				}
				if(matriz[index1][index2] == null) {
					matriz[index1][index2] = 0;
				}
				index2++;
			}
			index1++;
		}
//		usersData.clear();
		return matriz;
	}
	public static double cosineSimilarity(Integer[] firstVector, Integer[] secondVector) {
	    double dotProduct = 0;
	    double normA = 0;
	    double normB = 0;
	    
	    for (int i = 0; i < firstVector.length; i++) {
	        dotProduct += firstVector[i] * secondVector[i];
	        normA += Math.pow(firstVector[i], 2);
	        normB += Math.pow(secondVector[i], 2);
	    }   
	    double result = dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
	    return result;
	}
	public static Double[][] calcularSimilaridade(Integer[][] matriz) {
	
	
		for(int i = 0; i < animes.size(); i++){
			Integer[] coluna = new Integer[ids.size()];
			for(int j = 0; j < ids.size(); j++){
			    coluna[j] = matriz[j][i];	 
			}
			colunas.add(coluna);
		}

		Double[][] matrizSimilaridade = new Double[colunas.size()][colunas.size()];
		for(int i = 0; i < colunas.size(); i++) {
			for(int j = 0; j < colunas.size(); j++) {
				matrizSimilaridade[i][j] = cosineSimilarity(colunas.get(i), colunas.get(j));
			}	
		}
		
		return matrizSimilaridade;
	}
	
	public static void printGrid(Integer[][] matriz)
	{
	   for(int i = 0; i < ids.size(); i++)
	   {
	      for(int j = 0; j < animes.size(); j++)
	      {	
	         System.out.printf("%5d ", matriz[i][j]);
	      }
	      System.out.println();
	   }
	}
	public static void printCosineMatrix(Double[][] matriz, int size)
	{
		DecimalFormat df2 = new DecimalFormat("#.##");
	   for(int i = 0; i < size; i++)
	   {
	      for(int j = 0; j < size; j++)
	      {	
	         System.out.print("  |  " + df2.format(matriz[i][j]));
	      }
	      System.out.println("  |");
	      System.out.println("-------------------------------------------------------------------------------------------------");
	   }
	}
}
