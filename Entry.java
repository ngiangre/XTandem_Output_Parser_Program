import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Set;


public class Entry 
{
	//file declarations
	static PrintWriter file;
	static String Column1;
	static String Column2;
	static String Column3;
	static String Column4;
	static String Column5;
	static String Column6;
	static String Column7;
	static String Column8;
	static String Column9;
	
	
	//Parsing declarations
	

		//declarations of arraylists for holding XML node values
		static ArrayList< String > proteinIdentities;
		static ArrayList< String > peptideSequences;
		static ArrayList< String > modificationmasses;
	    
	    //declarations for holding nonredundant arraylist values
	    static Set<String> nonredundantProteins;
	    static Set<String> nonredundantPeptides;
	    static Set<String> nonredundantModMasses;
	  
	    //declarations for string values
		static String filepath;
		String proteinIdentity;
		String peptideSequence;
		String expectation;
		String retentionTime;
		String charge;
		String delta;
		String aminoAcidNumber;
		String modificationMass;
		String modificationType;
		static String biomlLabel="";
	
	public Entry()  
	{
		//initializing string values
		proteinIdentity = "";
		peptideSequence = "";
		expectation = "";
		retentionTime="";
		charge = "";
		delta ="";
		aminoAcidNumber = "";
		modificationMass = "";
		modificationType = "";
			
		//debug statement
		//System.out.println("Creating XML Parsing object...");
	
	}
	public static void createFile() throws IOException
	{
		//initializing column values
		Column1 ="Protein Identity";
		Column2 = "Peptide Sequence";
		Column3 = "Expectation";
		Column4 = "z";
		Column5 = "rt";
		Column6 = "delta=theoretical-actual precursor mass";
		Column7 = "modification sequence number";
		Column8 = "modification mass";
		Column9 = "modification type";
		
		//creating buffered writer "dynamic file"
		//so we can add to it
		file= new PrintWriter(new BufferedWriter(new FileWriter(filepath + ".txt")));
		
		file.println(biomlLabel);
		file.println(Column1+"\t"+Column2+"\t"+Column3+"\t"+Column4+"\t"+Column5+"\t"+Column6+"\t"+Column7+"\t"
				+Column8+"\t"+Column9);
	}
	public void writeToFile()
	{
		file.println(proteinIdentity+"\t"+peptideSequence+"\t"+expectation+"\t"+charge+"\t"+retentionTime+ "\t"+delta+"\t"
						+aminoAcidNumber+"\t"+modificationMass+"\t"+modificationType);
		//debug statement
		//System.out.println("Writing to file...");
	}
	public static void closeFile()
	{
		file.close();
	}
	public static void numNonRedundantInArrayList() 
	{
		System.out.println("Showing unique values\n");

		System.out.println("Unique Proteins matched from Peptides: " + nonredundantProteins.size() + "\n"
				+ "Unique Peptides matched from Spectra: " + nonredundantPeptides.size() + "\n"
				+ "Unique Modifications found for all Peptides: " + nonredundantModMasses.size() + "\n");
		
		
	}
}