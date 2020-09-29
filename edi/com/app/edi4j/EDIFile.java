
package com.app.edi4j;

import java.io.*;
import com.app.edi4j.x12.X12File;

public class EDIFile
extends File {
    public RandomAccessFile raf;
    public static int X12 = 0;
    public  static int UNEDIFACT = 1;
    public static int HL7 = 2;
    private String line;
    private int lineNumber;
    public int type;
    
    
    
    /**
     * getFilePointer
     *
     * @return long
     */
    public long getFilePointer() throws IOException{
        return raf.getFilePointer();
    }
    
    /**
     * readLine
     *
     * @return String
     */
    public String readLine() throws IOException{
        String line = raf.readLine();
        if(line == null){
            return null;
        }
        else{
            return line;
        }
    }
    
    
    
    
    
    /**
     * seek
     *
     * @param position long
     */
    public void seek(long position) throws IOException{
        raf.seek(position);
    }
    
    
    
    /**
     * EDIFile
     *
     * @param aFile File
     */
    public EDIFile(File aFile)  throws FileNotFoundException, IOException {
        super(aFile.getAbsolutePath());
        raf = new RandomAccessFile(aFile.getAbsolutePath(), "r");
        lineNumber = 0;
        if(isX12()){
            type = X12;
        }
        
    }
    
    
    
    
    /**
     * EDIFile
     *
     * @param path a fully qualified string to x12 file
     */
    public EDIFile(String path) throws FileNotFoundException, IOException {
        super(path);
        raf = new RandomAccessFile(this, "r");
        lineNumber = 0;
        if(isX12()){
            type = X12;
        }
        
    }
    
    boolean isX12() throws FileNotFoundException, IOException {
        boolean returnB = false;
        int test = 0;
        long pos = getFilePointer();
        String firstLine = "";
        firstLine = readLine();
        if(firstLine != null){
            String indentifier = String.valueOf((firstLine.charAt(82)));
            if(indentifier.compareToIgnoreCase("U") == 0){
                returnB = true;
            }
        }
        seek(pos);
        return returnB;
    }
    
    public X12File getX12File() throws FileNotFoundException, IOException {
        return new X12File(this);
    }
    
    public int getLineCount()throws FileNotFoundException, IOException {
        int count = 0;
        long pos = getFilePointer();
        String firstLine = "";
        while ( (firstLine = readLine()) != null) {
            count++;
        }
        seek(pos);
        return count;
    }
    
}
