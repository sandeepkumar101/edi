package com.app.edi4j.x12;
import java.io.*;
/**
 *
 * @author  kurt
 */
public class X12ParserFile extends File{
    public RandomAccessFile raf;
    public long FileEndPointer;
    public String subelementSeparartor;
    public String lineTerminator;
    public String elementSeparator;
    
    
    /** Creates a new instance of X12FileSegment */
    public X12ParserFile(File f) throws FileNotFoundException, IOException {
        super(f.getAbsolutePath());
        raf = new RandomAccessFile(this, "r");
        
    }
    
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
    
}
