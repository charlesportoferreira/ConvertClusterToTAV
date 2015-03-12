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
        String texto = "K0              0.0608     0.0621          0";
        String palavra = texto.replaceAll("[\\s]+", ",");
        String a = palavra.replaceAll("K[0-9]+,", "");
        String b = a.replaceFirst("[0-9]+,|0\\.[0-9]+,", "");
        System.out.println(texto);
        System.out.println(palavra);
        System.out.println(a);
        System.out.println(b);

        ConvertClusterToTAV c = new ConvertClusterToTAV();
        try {
            c.convert("arquivo.txt","newFile.arff");
        } catch (IOException ex) {
            Logger.getLogger(ConvertClusterToTAV.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void convert(String oldFile, String newFile) throws FileNotFoundException, IOException {
        createHeader(oldFile, newFile);
        String linha;
        try (FileReader fr = new FileReader(oldFile); BufferedReader br = new BufferedReader(fr)) {
            while (br.ready()) {
                linha = br.readLine();
                if (linha.contains("Attribute")) {
                    br.readLine();
                    br.readLine();
                    while (br.ready()) {
                        linha = br.readLine();
                        linha = linha.replaceAll("[\\s]+", ",");
                        linha = linha.replaceAll("K[0-9]+,", "");
                        linha = linha.replaceFirst("[0-9]+,|0\\.[0-9]+,", "");
                        salvaLinhaDados("newFile.arff", linha);
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

    private void createHeader(String oldFile, String newFile) throws IOException {
        int numeroColunas = getNumeroColunas(oldFile);
        StringBuilder sb = new StringBuilder();
        sb.append("@RELATION teste").append("\n\n");
        for (int i = 0; i < numeroColunas; i++) {
            sb.append("@ATTRIBUTE ");
            sb.append("A").append(i);
            sb.append("	REAL\n");
        }
        sb.append("\n\n");
        sb.append("@DATA");
        salvaLinhaDados(newFile, sb.toString());
    }

    private int getNumeroColunas(String oldFile) throws FileNotFoundException, IOException {
        String linha;
        try (FileReader fr = new FileReader(oldFile); BufferedReader br = new BufferedReader(fr)) {
            while (br.ready()) {
                linha = br.readLine();
                if (linha.contains("Attribute")) {
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

}
