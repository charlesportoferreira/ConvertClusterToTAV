/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package convertclustertotav;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author charles
 */
public class ConvertClusterToTAV {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if(args.length != 3){
            help();
        }
        ConvertClusterToTAV c = new ConvertClusterToTAV();
        
        try {
            String[] arr = args[2].split(",");
            int[] posicoes = new int[arr.length];
            for (int i = 0; i < arr.length; i++) {
                try {
                    posicoes[i] = Integer.parseInt(arr[i]);
                } catch (NumberFormatException nfe) {
                    System.out.println("Algum numero digitado errado...");
                }
            }

            c.convert(args[0], args[1], posicoes);
        } catch (IOException ex) {
            Logger.getLogger(ConvertClusterToTAV.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void help() {
        System.out.println("Falta digitar os parametros:");
        System.out.println("source file targetFile 0-9,0-9,*");
        System.exit(0);
    }

    public void convert(String oldFile, String newFile, int[] posicoes) throws FileNotFoundException, IOException {
        createHeader(oldFile, newFile, posicoes.length);
        String[] classesInstancias = getClasses("classesAtributos.txt").split(",");
        String linha;
        int i = 0;
        try (FileReader fr = new FileReader(oldFile); BufferedReader br = new BufferedReader(fr)) {
            while (br.ready()) {
                linha = br.readLine();
                if (linha.contains("Attribute ")) {
                    br.readLine();
                    br.readLine();
                    while (br.ready()) {
                        linha = br.readLine();
                        if (linha.length() < 2) {
                            break;
                        }
                        linha = linha.replaceAll("[\\s]+", ",");
                        linha = linha.replaceAll("K[0-9]+,", "");
                        linha = linha.replaceFirst("[0-9]+,|0\\.[0-9]+,", "");
                        ArrayList<String> li = new ArrayList<>(Arrays.asList(linha.split(",")));
                        for (int posicao : posicoes) {
                            li.remove(posicao);
                        }
                        linha = li.toString().replaceAll("\\[|\\]", "");
                        linha += "," + classesInstancias[i];
                        salvaLinhaDados(newFile, linha);
                        i++;
                    }
                }
            }
            br.close();
            fr.close();
        }
    }

    private void salvaLinhaDados(String fileName, String dado) throws IOException {
        try (FileWriter fw = new FileWriter(fileName, true); BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(dado);
            bw.newLine();
            bw.close();
            fw.close();
        }
    }

    private void createHeader(String oldFile, String newFile, int tamanho) throws IOException {
        int numeroColunas = getNumeroColunas(oldFile);
        numeroColunas -= tamanho;
        StringBuilder sb = new StringBuilder();
        sb.append("@RELATION teste").append("\n\n");
        for (int i = 0; i < numeroColunas; i++) {
            sb.append("@ATTRIBUTE ");
            sb.append("C").append(i);
            sb.append("	REAL\n");
        }
        String classes = getClasses("classes.txt");
        sb.append(classes).append("\n");
        sb.append("\n\n");
        sb.append("@DATA");
        salvaLinhaDados(newFile, sb.toString());
    }

    private int getNumeroColunas(String oldFile) throws FileNotFoundException, IOException {
        String linha;
        try (FileReader fr = new FileReader(oldFile); BufferedReader br = new BufferedReader(fr)) {
            while (br.ready()) {
                linha = br.readLine();
                if (linha.contains("Attribute ")) {
                    br.readLine();
                    br.readLine();
                    linha = br.readLine();
                    if (linha.length() < 2) {
                        break;
                    }
                    linha = linha.replaceAll("[\\s]+", ",");
                    linha = linha.replaceAll("K[0-9]+,", "");
                    linha = linha.replaceFirst("[0-9]+,|0\\.[0-9]+,", "");
                    String[] dados = linha.split(",");
                    return dados.length;

                }
            }
            br.close();
            fr.close();
        }
        return 0;
    }

    private String getClasses(String fileName) throws FileNotFoundException, IOException {
        try (FileReader fr = new FileReader(fileName); BufferedReader br = new BufferedReader(fr)) {
            while (br.ready()) {
                return br.readLine();
            }
            br.close();
            fr.close();
        }
        return "";
    }

}
