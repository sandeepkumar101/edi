package com.app.edi4j;

import com.app.edi4j.test.*;
import com.app.edi4j.EDIFile;
import com.app.edi4j.x12.X12File;
import com.app.edi4j.x12.X12Document;
import com.app.edi4j.x12.structure.*;
import org.jdom.Document;
import java.lang.reflect.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class EDI4JExample {
    private static final Class[] parameters = new Class[]{URL.class};
    public EDI4JExample() {
    }
    
    public static void main(String[] args) {
        String appHome = System.getProperty("java.class.path");
        //String appHome = "/home/kurt/working_projects/edi4j";
        String sep = System.getProperty("file.separator");
        System.setProperty("application.home", appHome);
        System.setProperty("application.templates", appHome + sep + "templates"  + sep);
        System.setProperty("application.temp", appHome + sep + "tmp"  + sep);
        System.setProperty("application.lib", appHome + sep + "lib"  + sep);
        cleanUpTmp();
        addLibrariesToClasspath(new File(System.getProperty("application.lib") + "jdom.jar"));
        /**
         * Simple translation of X12 edi interchange evelope to an XML representation
         *
         */
        try{
            if(args.length > 1){
                translate(args[0], args[1]);
            }
            
            
            
            
            
            /**
             * Example deconstruction of X12 interchange object from interchane to element level
             * object.
             *
             */
            
            EDIFile f1 = new EDIFile(appHome + sep + "test_files"  + sep + "837"  + sep +  "837P_Commercial_Anesthesia_IP");
            if(f1.type == f1.X12){
                X12File x12file = f1.getX12File();
                X12Document x12Doc = new X12Document(x12file);
                X12Interchange interchange = x12Doc.getInterchange();
                Vector functionalGroups = interchange.getGroups();
                for(int i = 0; i < functionalGroups.size(); i++){
                    X12Group group = (X12Group)functionalGroups.get(i);
                    Vector transactions = group.getTransactions();
                    for(int j = 0; j < transactions.size(); j ++){
                        X12Transaction transaction = (X12Transaction)transactions.get(j);
                        X12Loop transLoop = transaction.getLoopFromID("2000B");
                        String loopName = transLoop.getName();
                        String maximumLoopRepeats = transLoop.getRepeat();
                        Vector segments = transLoop.getSegments();
                        for(int k = 0; k < segments.size(); k++){
                            X12Segment segment = (X12Segment)segments.get(k);
                            String maxOccurence = segment.getMaxUse();
                            String requirement = segment.getRequirement();
                            Vector elements = segment.getX12Elements();
                            for(int l = 0; l < elements.size(); l++){
                                X12Element element = (X12Element)elements.get(l);
                                String maximumFieldDataLength = element.getMax();
                                String elementsValue = element.getValue();
                                
                            }
                            
                        }
                        
                    }
                }
            }
            
            /**
             * Example retrieving the functional indentifier code from each group contained in interchange
             * object.
             *
             */
            EDIFile f2 = new EDIFile(appHome + sep + "test_files"  + sep + "837"  + sep +  "837P_Commercial_Anesthesia_IP");
            if(f2.type == f2.X12){
                X12File x12file = f2.getX12File();
                X12Document x12Doc = new X12Document(x12file);
                X12Interchange interchange = x12Doc.getInterchange();
                Vector functionalGroups = interchange.getGroups();
                for(int i = 0; i < functionalGroups.size(); i++){
                    X12Group group = (X12Group)functionalGroups.get(i);
                    String functionIdentifierCode = group.getSegmentFromID("GS").getElementFromReference("GS01").getValue();
                }
            }
        }
        catch (Exception e) {
            System.out.println("error: cannot create X12File");
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        
        
        
    }
    
    public static void translate(String sourcePath, String destPath) {
        try {
            EDIFile f2 = new EDIFile(sourcePath);
            if(f2.type == f2.X12){
                X12File x12file = f2.getX12File();
                X12Document x12Doc = new X12Document(x12file);
                x12Doc.exportXMLTransaction(destPath);
            }
            
            
        }
        catch (Exception e) {
            System.out.println("error: cannot create X12File");
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        
        
    }
    
    static private void addLibrariesToClasspath(File f){
        URLClassLoader sysloader = (URLClassLoader)ClassLoader.getSystemClassLoader();
        Class sysclass = URLClassLoader.class;
        try {
            URL u = f.toURL();
            Method method = sysclass.getDeclaredMethod("addURL",parameters);
            method.setAccessible(true);
            method.invoke(sysloader,new Object[]{ u });
        } catch (Throwable t) {
            t.printStackTrace();
            try{
            throw new IOException("Error, could not add URL to system classloader");
            }catch(Exception e){
                
            }
        }
    }
    
    static void cleanUpTmp(){
        File dir = new File(System.getProperty("application.temp"));
        File[] files = dir.listFiles();
        for(int i = 0; i < files.length; i++){
            File f = files[i];
            try{
            f.delete();
            }catch(Exception e){
                
            }
            
         }
    }
    
    
}
