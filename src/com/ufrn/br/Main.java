package com.ufrn.br;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
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
	static Map<String, List<HashMap<String, Integer>>> usersData = new HashMap<>();
	static HashMap<String, Integer> newUser;

	
	public static void main(String[] args) {
		List <DataCSV> resultado = LerCsv();
		ContarUsuarios(resultado);
		System.out.println(ids);
		System.out.println(animes);
//		System.out.println(usersData);
//		System.out.println(usersData.size());
//		usersData.forEach((key, value) -> System.out.println(key + ":" + value));
		Integer[][] matriz = construirMatrizes();
		calcularSimilaridade(matriz);
		printGrid(matriz);
	}
	
	public static List<DataCSV> LerCsv() {
		Reader reader = null;
		try {
			reader = Files.newBufferedReader(Paths.get("rating3.csv"));
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
		return matriz;
	}
	public static void calcularSimilaridade(Integer[][] matriz) {
		Integer[][] matrizSimilaridade = new Integer[ids.size()][ids.size()];
		
		for(int i = 0; i < ids.size(); i++){
			System.out.println(usersData.get(ids.get(i))+" linha");
		      for(int j = 0; j < ids.size(); j++){
		    	 if(i != j) {
		    		 System.out.println(usersData.get(ids.get(j)).size());
		    		 System.out.println(usersData.get(ids.get(j))+" col");
		    	 }
		      }
		      System.out.println();
		   }
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

}
