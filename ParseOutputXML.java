import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ParseOutputXML 
{//start class body

	public static void main(String[] args) throws  IOException 
	{
		//initializing the path to the file to the filepath string
		Entry.filepath = args[0];
		
		//parsing the output xml file
		ParseXMLFile();
		
		//debug statement
		//System.out.println("Protein Identity size: "+Entry.proteinIdentities.size()+"\n");
		
		//print out the non redundant proteins and peptide numbers in the output xml file
		Entry.numNonRedundantInArrayList();
		
		System.out.println("done");
	}
	public static void ParseXMLFile() 
	{
		try
        {
	        System.out.println("Parsing " + Entry.filepath+"\n");
	        //create document parser factory instance-contains the tools needed for parsing
	        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
	        //create document builder-this creates the "man power" needed for parsing
	        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
	        //create document parser
	        Document doc = docBuilder.parse(Entry.filepath);
	       
	        
	        //get parent node of xml file
	        Node bioml = doc.getElementsByTagName("bioml").item(0);
	        //grab bioml attributes
	        NamedNodeMap biomlAttrs = bioml.getAttributes();
	        //if there are no attributes, then skip this
	        if(biomlAttrs==null) {}
	        //if there are (there should be)
	        else 
	        {
	        	//grab attribute label-this attribute has the spectra file used to generate this output file
	        	Node label = biomlAttrs.getNamedItem("label");
	        	//if it's null, then skip
	        	if(label==null) {}
	        	//if it's not, then grab the value in the label attribute
	        	else Entry.biomlLabel = label.getNodeValue();
	        }
	        //debug statement
	        //System.out.println("biomlLabel = "+Entry.biomlLabel);
	        
	        //create the tab delimited file (now that we have the biomlLabel)
	        Entry.createFile();
	        //declare parsing object, which collects the values as the output xml file is parsed
	        Entry e;
	        
	        //get group nodes-includes proteins matched with a peptide and the supporting data groups
	        NodeList groups = bioml.getChildNodes();
        
	        //declare and initialize arrays-length is as long as there are groups in the xml output
	        Entry.proteinIdentities = new ArrayList< String >(groups.getLength());
	        Entry.peptideSequences = new ArrayList< String>(groups.getLength());
	        Entry.modificationmasses = new ArrayList< String >(groups.getLength());
	        
		    //declare variables needed for location marking-helps determine when to dump values into arraylists and the file
		    int inAA;
		    int inDomain;
			
	        System.out.println("looping through XML structure...\n");
	        //run through group nodes: to get accession label, expectation value, z, actual mh, and rt
	        for(int i=0; i<groups.getLength(); i++)
	        {//start 1for
	        	//debug statements
	        	//System.out.println("group");
	        	//System.out.println("group iteration = "+i);
            	//grab each group node as you go through the for loop
	            Node group = groups.item(i);
	            //grab the attributes at each node
	            NamedNodeMap groupAttrs = group.getAttributes();
	            //if there's no attributes go on to the next node
	            //***note*** we need all these if statements to see if the attributes are null
	            //because looping through the XML structure is weird and sometimes
	            //there are no attributes when looping-thus, we need to get through the 
	            //no attribute node
	            if(groupAttrs==null) {}
	            //if there are attributes...
	            else
	            {//start 1else
	            	//these groups are proteins matched to peptides
		            //instantiate parsing object so we can collect all the values for each matched peptide
	            	e = new Entry();
	            	//grab attribute rt
		            Node rt = groupAttrs.getNamedItem("rt");
		            //if there's no attribute "rt" go to the next node
		            if(rt==null) {}
		            //if there is an attribute "rt"...
		            else e.retentionTime = rt.getNodeValue();
		            //debug statement
		            // System.out.println("rt = "+e.retentionTime);
		            //grab attribute z
		            Node z = groupAttrs.getNamedItem("z");
		            //if there is no attribute "z" go to the next node
		            if(z==null) {}
		            //if there is an attribute "z"...
		            else e.charge = z.getNodeValue();
		            //debug statement
		            //System.out.println("charge = "+e.charge);
		            //grab each group's subgroups containing more protein info
		            NodeList subgroups = group.getChildNodes();
		            //go through subgroups
		            for(int j=0; j<subgroups.getLength(); j++)
		            {//start 2for
		            	//grab the subgroup 
		                Node subgroup = subgroups.item(j);
		                //debug statement
		                //System.out.println("\tsubgroup iteration = "+j);
		                //get all attributes of the subgroup
		                NamedNodeMap subgroupAttrs = subgroup.getAttributes();
		                //if there are no attributes continue to next subgroup
		                if(subgroupAttrs==null) {}
		                //if there are...
		                else
		                {//start 2else
		                	//grab the attribute "expect" in protein subgroup
		                	Node protein = subgroupAttrs.getNamedItem("expect");
		                	//if no attribute "end" go to next subgroup
		                	if(protein==null) {}
		                	//if there is an "expect"...
		                	else
		                	{//start 3else
		                		//debug statement
		                		//System.out.println("\tprotein");
		            		    //variables needed for location marking-helps determine when to
		            	        //dump values into arraylists
		            			inDomain=0;
		            			inAA=0;
		    	            	//grab the attribute "label" which contains the protein identity name
		    		            Node label = subgroupAttrs.getNamedItem("label");
		    		            //if there is no attribute "label" go on to the next node
		    		            if(label==null) {}
		    		            //if there is an attribute "label"...
		    		            else 
		    		            {
		    		            	//grab the label attribute label
		    		            	String grouplabel = label.getNodeValue();
		    		            	e.proteinIdentity = grouplabel;
		    		            	//debug statement
		    		            	//System.out.println("\tproteinID = "+e.proteinIdentity);
		    		            }
		                		//since only protein has the attribute "expect" grab all of its subgroups-note, file, peptide etc.
		                		NodeList proteinSubGroups = subgroup.getChildNodes();
		                		//go through proteinsubgroups
		                		for(int k = 0; k<proteinSubGroups.getLength(); k++)
		                		{//start 3for
		                			//grab proteinsubgroup
		                			Node proteinSubGroup = proteinSubGroups.item(k);
		                			//System.out.println("\t\tproteinSubGroup iteration = "+k);
		        					if(inAA==0&&inDomain==1)
		        					{
		        						//write values to file if there are no aa modifications
		        						e.writeToFile();
		        						//reset location markers
		        						inDomain=0;
		        						inAA=0;
		        					}
		                			//grab its attributes
		                			NamedNodeMap proteinSubGroupAttrs = proteinSubGroup.getAttributes();
		                			//if there are no attributes go to next proteinsubgroup
		                			if(proteinSubGroupAttrs==null) {}
		                			//if there are...
		                			else
		                			{//start 4else
		                				//grab attribute "end" in peptide
		                				Node peptide = proteinSubGroupAttrs.getNamedItem("end");
		                				//if it doesn't have "end" go to next proteinsubgroup
		                				if(peptide==null) {}
		                				//if it does...
		                				else
		                				{//start 5else
		                					//debug statement
		                					//System.out.println("\t\tpeptide");
		                					//since the domain that matched the protein sequence is 
		                					//a subbranch of peptide, grab the subbranch
		                					NodeList subbranches = proteinSubGroup.getChildNodes();
		                					//go through subbranches (gotta do for loop to go to subbranch)
		                					for(int m = 0; m<subbranches.getLength(); m++)
		                					{//start 4for
		                						//grab domain
		                						Node domain = subbranches.item(m);
		                						//debug statement
		                						//System.out.println("\t\t\tproteinsubranch iteration = "+m);
		                						//get its attributes
		                						NamedNodeMap domainAttrs = domain.getAttributes();
		                						//if the node has no attributes go to the next
		                						if(domainAttrs==null) {}
		                						//if it does...
		                						else
		                						{//end 6else
		                							//debug statement
		                							//System.out.println("\t\t\tdomain");
			                						//parsing location marked
			                						inDomain=1;
			                						//get "seq" attribute
			                						Node sequence = domainAttrs.getNamedItem("seq");
			                						//if it doesn't have "seq" go to next node
			                						if(sequence==null) {}
			                						//if it does...
			                						else
			                						{
			                							//get node value of "seq" and add to peptideSequence
				                						e.peptideSequence = sequence.getNodeValue();
				                						//debug statement
				                						//System.out.println("\t\t\tpeptideSeq = "+e.peptideSequence);
			                						}
			                			            //grab attribute expect
			                			            Node expect = domainAttrs.getNamedItem("expect");
			                			            //if there's no attribute "expect" go on to next node
			                			            if(expect==null) {}
			                			            //if there is an attribute "expect"...
			                			            else 
			                			            {
			                			            	//get node value of "expect" and add to expectations
			                			            	e.expectation = expect.getNodeValue();
			                			            	//debug statement
			                			            	//System.out.println("\t\t\texpectation = "+e.expectation);
			                			            }
			                						//get "delta" attribute
			                						Node delta = domainAttrs.getNamedItem("delta");
			                						//if it doesn't have "delta" go to the next node
			                						if(delta==null) {}
			                						//if it does...
			                						else 
			                						{
			                							//get node value of "delta" and add to deltas
			                							e.delta = delta.getNodeValue();
			                							//debug statement
			                							//System.out.println("\t\t\tdelta = "+e.delta);
			                						}
		                						    //grab child nodes of domains i.e. aa modifications
			                						NodeList domainsubbranches = domain.getChildNodes();
			                						//go through domainsubbranches
			                						for(int n = 0; n<domainsubbranches.getLength(); n++)
			                						{//start 5for
			                							//grab aa
			                							Node aa = domainsubbranches.item(n);
			                							//debug statement
			                							//System.out.println("\t\t\t\tdomainsubbranch iteration = "+n);
				                						if(inAA==1)
			                							{
				                							//write to file all values if there is (are) aa modifications
				                							e.writeToFile();
				                							//reset location markers
				                							inAA=0;
				                							inDomain=0;
			                							}
			                							//get its attributes
			                							NamedNodeMap aaAttrs = aa.getAttributes();
			                							//if the node has nmo attributes go to the next node
			                							if(aaAttrs==null) 
			                							{}
			                							//if it does...
			                							else
			                							{
			                								//debug statement
			                								//System.out.println("\t\t\t\taa");
			                								//parsing location marked
			                								inAA=1;
			                								//get "at" attribute
			                								Node at = aaAttrs.getNamedItem("at");
			                								//if it doesn't have "at go to the next node
			                								if(at==null) {}
			                								//if it does...
			                								else e.aminoAcidNumber = at.getNodeValue();
			                								//debug statement
			                								//System.out.println("\t\t\t\taanum = "+e.aminoAcidNumber);
			                								//grab "modified" attribute
			                								Node modified = aaAttrs.getNamedItem("modified");
			                								//if it doesn't have "modified" go to the next node
			                								if(modified==null) {}
			                								//if it does...
			                								else e.modificationMass = modified.getNodeValue();
			                								//debug statement
			                								//System.out.println("\t\t\t\tmodmass = "+e.modificationMass);
			                								//grab "type" attribute
			                								Node type = aaAttrs.getNamedItem("type");
			                								//if it doesn't have "type" go to the next node
			                								if(type==null) {}
			                								//if it does...
			                								else e.modificationType = type.getNodeValue();
			                								//debug statement
			                								//System.out.println("\t\t\t\tmodtype = "+e.modificationType);
			                							}
			                							if(inDomain==1)
			                							{
			                								//debug statement
			                								//System.out.println("\t\t\tEntering values in arraylists");
			                								//add values to ArrayLists
			                								//debug statement
			                								//System.out.println("\t\t\tpeptideSeq = "+e.peptideSequence);
			                								
			                								Entry.peptideSequences.add(e.peptideSequence);
			                								Entry.proteinIdentities.add(e.proteinIdentity);
			                								Entry.modificationmasses.add(e.modificationMass);
			                							}
			                						}//end 5for
		                						}//end 6else
		                					}//end 4for 
		                				}//end 5else
		                			}//end 4else
		                		}//end 3for
		                	}//end 3else
		                }//end 2else
		            }//end 2for
	            }//end 1else
	        }//end 1for 
	        
	        //make sets for non-redundant counts
	        //debug statement
	        //System.out.println("Making NonRedundant Sets");
			Entry.nonredundantProteins = new HashSet<String>(Entry.proteinIdentities);
			Entry.nonredundantPeptides = new HashSet<String>(Entry.peptideSequences);
			Entry.nonredundantModMasses = new HashSet<String>(Entry.modificationmasses);
			
			
	        System.out.println("values collected from " + Entry.filepath + "\n");
	        
	        
        }//end try
		catch(NullPointerException e) {
			e.printStackTrace();;
		} catch (ParserConfigurationException pce) {
	       pce.printStackTrace();
	   } catch (IOException ioe) {
	        ioe.printStackTrace();
	   } catch (SAXException sae) {
	        sae.printStackTrace();
	     }
	}
}//end class body

