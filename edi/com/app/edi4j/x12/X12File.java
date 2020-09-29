package com.app.edi4j.x12;

import com.app.edi4j.EDIFile;
import com.app.edi4j.x12.structure.X12Interchange;
import java.io.*;


/**
 *
 * @author  kurt
 */
public class X12File extends EDIFile {
    public String subelementSeparartor;
    public String lineTerminator;
    public String elementSeparator;
    public X12Interchange interchange;
    /** Creates a new instance of X12File */
    public X12File(EDIFile ediFile) throws FileNotFoundException, IOException {
        super(ediFile);
        setMarkupCharacters();
        prepFile();
    }
    
    private void prepFile() throws FileNotFoundException, IOException {
        if(getLineCount() == 1){
            String stream = readLine();
            String[] lines = stream.split(lineTerminator);
            PrintStream out = new PrintStream(new BufferedOutputStream(new FileOutputStream(System.getProperty("application.temp") + "TestX12File")));
            for(int i = 0; i < lines.length; i++){
                out.println(lines[i] + lineTerminator);
            }
            out.close();
            raf = new RandomAccessFile(System.getProperty("application.temp") + "TestX12File", "r");
        }
        
        if(!allLinesTerminated()){
            System.out.println("termination problem");
        }
    }
    
    
    
    private void setMarkupCharacters() throws FileNotFoundException, IOException {
        int test = 0;
        long pos = getFilePointer();
        String firstLine = "";
        firstLine = readLine();
        if(firstLine != null){
            elementSeparator = String.valueOf((firstLine.charAt(3)));
            subelementSeparartor = String.valueOf((firstLine.charAt(104)));
            lineTerminator = String.valueOf((firstLine.charAt(105)));
        }
        seek(pos);
    }
    
    public boolean allLinesTerminated() throws FileNotFoundException, IOException {
        int count = 0;
        boolean returnB = true;
        long pos = getFilePointer();
        String firstLine = "";
        while ( (firstLine = readLine()) != null) {
            count++;
            if(!firstLine.endsWith(lineTerminator)){
                System.out.println("line " + count + " is not terminated");
                returnB = false;
            }
        }
        seek(pos);
        return returnB;
    }
    
}
