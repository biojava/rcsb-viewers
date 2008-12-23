/*
 * BioJava development code
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence. This should
 * be distributed with the code. If you do not have a copy,
 * see:
 *
 * http://www.gnu.org/copyleft/lesser.html
 *
 * Copyright for this code is held jointly by the individual
 * authors. These should be listed in @author doc comments.
 *
 * For more information on the BioJava project and its aims,
 * or to join the biojava-l mailing list, visit the home page
 * at:
 *
 * http://www.biojava.org/
 *
 * This code was contributed from the Molecular Biology Toolkit
 * (MBT) project at the University of California San Diego.
 *
 * Please reference J.L. Moreland, A.Gramada, O.V. Buzko, Qing
 * Zhang and P.E. Bourne 2005 The Molecular Biology Toolkit (MBT):
 * A Modular Platform for Developing Molecular Visualization
 * Applications. BMC Bioinformatics, 6:21.
 *
 * The MBT project was funded as part of the National Institutes
 * of Health PPG grant number 1-P01-GM63208 and its National
 * Institute of General Medical Sciences (NIGMS) division. Ongoing
 * development for the MBT project is managed by the RCSB
 * Protein Data Bank(http://www.pdb.org) and supported by funds
 * from the National Science Foundation (NSF), the National
 * Institute of General Medical Sciences (NIGMS), the Office of
 * Science, Department of Energy (DOE), the National Library of
 * Medicine (NLM), the National Cancer Institute (NCI), the
 * National Center for Research Resources (NCRR), the National
 * Institute of Biomedical Imaging and Bioengineering (NIBIB),
 * the National Institute of Neurological Disorders and Stroke
 * (NINDS), and the National Institute of Diabetes and Digestive
 * and Kidney Diseases (NIDDK).
 *
 * Created on 2008/12/22
 *
 */ 
package org.rcsb.ks.controllers.doc;

import java.util.ArrayList;

import org.rcsb.ks.model.AnnotatedAtom;
import org.rcsb.ks.model.DisplayInformation;
import org.rcsb.ks.model.EntityDescriptor;
import org.rcsb.ks.model.GeneralEntityDescriptor;
import org.rcsb.ks.model.JournalIndex;
import org.rcsb.ks.model.KSStructureInfo;
import org.rcsb.ks.model.PrimaryCitation;
import org.rcsb.mbt.model.Atom;
import org.rcsb.mbt.structLoader.StructureXMLHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;


/**
 * @author John Beaver, jeff milton
 * @author rickb (revisions)
 * 
 * This processes a few more elements and and adds types to handle annotations in the atoms,
 * primarily for pulling citation/author references.
 * 
 */
public class KSStructureXMLHandler extends StructureXMLHandler
{
	///
	/// BEG Override runnables
	///
    ///
    /// END Override runnables
    ///
    
	private final ArrayList<String> authors = new ArrayList<String>();

	/**
	 * Constructor
	 * @author rickb
	 *
	 */
	private boolean inRelatedStructures = false;
	protected boolean inStructId = false;
	protected boolean insideEntity = false;

	class EntityObject {
		String id = "";
		String va = "";
		String type = "";
		String description = "";

		EntityObject() {}

		void setId(String _id) {id = _id;}
		public void setType(String _entityType) { type = _entityType; }
		public void setDescription(String _desc) { description = _desc; }

		public String getId() { return id; }
		public String getDescription() { return description; }
	}

	protected EntityObject currentEntity = null;

	protected ArrayList<EntityDescriptor> entityDescriptors = new ArrayList<EntityDescriptor>();
	
	@Override
	protected Atom createAtom() { return new AnnotatedAtom(); }

	public KSStructureXMLHandler(final String urlString)
	{
		super(urlString);
		
		startElementRunnables.put(xmlPrefix + "audit_author", createXMLRunner__audit_author__Start());
		startElementRunnables.put(xmlPrefix + "entityCategory", createXMLRunnable__entityCategory__Start());
		startElementRunnables.put(xmlPrefix + "entity", createXMLRunnable__entity__Start());
		startElementRunnables.put(xmlPrefix + "pdbx_database_related", createXMLRunnable__pdbx_database_related__Start());
		endElementAtomRunnables.put(xmlPrefix + "label_entity_id", createXMLRunnable__label_entity_id__End());
	}

	@Override
	public void endDocument() throws SAXException
	{
		super.endDocument();
		if (journalIndex != null)
		{
			journalIndex.setAuthors(authors);
			PrimaryCitation primaryCitation = new PrimaryCitation(journalIndex,
					authors);
			primaryCitation.setAuthors(authors);

			KSStructureInfo structureInfo = new KSStructureInfo();
			structureInfo.setDescriptors(entityDescriptors);
			structureInfo.setPrimaryCitation(primaryCitation);
			structure.setStructureInfo(structureInfo);
		}
	}


	private boolean isInCitation = false;
	private boolean isInStruct = false;
	
	private JournalIndex journalIndex = new JournalIndex();

	private DisplayInformation displayInformation = new DisplayInformation(
			"Unknown");
		

	/* (non-Javadoc)
	 * @see org.rcsb.mbt.io.StructureXMLHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	@Override
	public void startElement(String namespaceURI, String sName, String qName,
			Attributes attrs) throws SAXException
	{
			super.startElement(namespaceURI, sName, qName, attrs);
			
			if (qName.equals(xmlPrefix + "citation"))
				if (attrs.getValue("id").equalsIgnoreCase("primary"))
					isInCitation = true;
			
			else if (qName.equals(xmlPrefix + "struct"))
				isInStruct = true;
	}

	@Override
	public void endElement(String namespaceURI,
			String sName, // simple name
			String qName // qualified name 
			) throws SAXException
	{
	    if (qName.equals(xmlPrefix + "citation")) {
			this.isInCitation = false;
		} else if (this.isInCitation && qName.equals(xmlPrefix + "title")) {
			String trim = this.buf.trim();
			if (journalIndex != null)
				journalIndex.setTitle(trim);
		} else if (this.isInCitation
				&& qName.equals(xmlPrefix + "journal_abbrev")) {
			String trim = this.buf.trim();
			if (journalIndex != null)
				journalIndex.setJournalAbbreviation(trim);

		} else if (this.isInCitation
				&& qName.equals(xmlPrefix + "journal_volume")) {
			String trim = this.buf.trim();
			if (journalIndex != null)
				journalIndex.setJournalVolume(trim);
		} else if (this.isInCitation && qName.equals(xmlPrefix + "page_first")) {
			String trim = this.buf.trim();
			if (journalIndex != null) {
				try {
					int page = Integer.parseInt(trim);
					journalIndex.setFirstPage(page);
				} catch (Exception e) {
				}
			}

		} else if (this.isInCitation && qName.equals(xmlPrefix + "page_last")) {
			String trim = this.buf.trim();
			if (journalIndex != null) {
				try {
					int page = Integer.parseInt(trim);
					journalIndex.setLastPage(page);
				} catch (Exception e) {
				}
			}
		} else if (this.isInCitation && qName.equals(xmlPrefix + "year")) {
			String trim = this.buf.trim();
			if (journalIndex != null) {
				try {
					int year = Integer.parseInt(trim);
					journalIndex.setYear(year);
				} catch (Exception e) {
				}
			}
		} else if (this.isInCitation
				&& qName.equals(xmlPrefix + "pdbx_database_id_PubMed")) {
			String trim = this.buf.trim();
			if (journalIndex != null) {
				journalIndex.setPubMed(trim);
			}
		} else if (this.isInStruct && qName.equals(xmlPrefix + "title")) {
			String trim = buf.trim();
			displayInformation.setTitle(trim);
		} else if (this.insideEntity
				&& qName.equals(xmlPrefix + "entityCategory")) {
			System.err.println(" we are inside the entity qname : " + qName);
			insideEntity = false;
		} else if (this.insideEntity && qName.equals(xmlPrefix + "type")) {
			String trimmm = buf.trim();

			currentEntity.setType(trimmm);

			// System.out.println( "\t\t ------- - - ----- - - type is : " +
			// trimmm );
		} else if (insideEntity && qName.equals(xmlPrefix + "pdbx_description")) {
			String desc = buf.trim();
			currentEntity.setDescription(desc);
			// 
			String entityId = currentEntity.getId();
			String description = currentEntity.getDescription();
			EntityDescriptor ligandEntityDescriptor = new GeneralEntityDescriptor(
					description, entityId);

			entityDescriptors.add(ligandEntityDescriptor);

		} else if ( qName.equals("label_entity_id")){
			
		}
		else super.endElement(namespaceURI, sName, qName);
	}
	
	//
	// BEG Runnables class definitions
	//
	protected class KSXMLRunner__audit_author__Start extends XMLRunnable
	{
		public void run()
		{
			String au = super.attrs.getValue("name");
			authors.add(au);
		}
	}
	protected XMLRunnable createXMLRunner__audit_author__Start()
	{ return new KSXMLRunner__audit_author__Start(); }
	
	public class XMLRunnable__entityCategory__Start extends XMLRunnable
	{
		public void run() {
			insideEntity = true;
		}
	}
	public XMLRunnable createXMLRunnable__entityCategory__Start()
	{ return new XMLRunnable__entityCategory__Start(); }
	
	public class XMLRunnable__entity__Start extends XMLRunnable
	{
		public void run() {
			if (insideEntity) {

				currentEntity = new EntityObject();
				String id = attrs.getValue("id");
				System.out.println("\t\t id value is :> " + id);
				currentEntity.setId(id);
			}
		}
	}

	public XMLRunnable createXMLRunnable__entity__Start()
	{ return new XMLRunnable__entity__Start(); }

	public class XMLRunnable__pdbx_database_related__Start extends XMLRunnable
	{
		public void run() {
			inRelatedStructures = true;
		}
	}
	public XMLRunnable createXMLRunnable__pdbx_database_related__Start()
	{ return new XMLRunnable__pdbx_database_related__Start(); }
	
	public class XMLRunnable__label_entity_id__End extends XMLRunnable
	{
		public void run ()
		{
			String eid = buf.trim ();
			if (curAtom instanceof AnnotatedAtom)
				((AnnotatedAtom)curAtom).setEntityId(eid);				
		}
	}
	
	public XMLRunnable createXMLRunnable__label_entity_id__End()
	{ return new XMLRunnable__label_entity_id__End(); }
}
