package com.app.edi4j.test;
import java.io.*;
public class DiffFiles {
  public DiffFiles(String oFilePath, String nFilePath) throws IOException{
    File o = new File(oFilePath);
    File n = new File(nFilePath);

    normalizeFile(o, "/home/kurt/Documents/working/ " + o.getName());
    normalizeFile(n, "/home/kurt/Documents/working/ " + n.getName());

    Diff d = new Diff();
    d.doDiff("/home/kurt/Documents/working/ " + o.getName(), "/home/kurt/Documents/working/ " + n.getName());

  }

  /**
   * normalizeFile
   *
   * @param fileToNormalize File
   * @param outPutPath String
   */
  public void normalizeFile(File fileToNormalize, String outPutPath) throws IOException{
    RandomAccessFile ram = new RandomAccessFile(fileToNormalize, "r");
    FileOutputStream out = new FileOutputStream(outPutPath);
    PrintStream p = new PrintStream(out);
    String line;
    while((line = ram.readLine()) != null){
      line = line.replaceAll(" ", "");
      p.println(line);
    }

  }

}
