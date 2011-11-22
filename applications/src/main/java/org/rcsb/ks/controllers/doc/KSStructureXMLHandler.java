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
import java.util.List;

import org.rcsb.ks.model.AnnotatedAtom;
import org.rcsb.ks.model.EntityDescriptor;
import org.rcsb.ks.model.GeneralEntityDescriptor;
import org.rcsb.ks.model.IAtomAnnotator;
import org.rcsb.ks.model.JournalArticle;
import org.rcsb.ks.model.KSStructureInfo;
import org.rcsb.ks.model.StructureAuthor;
import org.rcsb.mbt.model.Atom;
import org.rcsb.mbt.structLoader.StructureXMLHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;


/**
 * @author John Beaver
 * @author Jeff Milton
 * @author Rick Berger (revisions)
 * @author Peter Rose (revisions)
 * 
 * This processes a few more elements and and adds types to handle annotations in the atoms,
 * primarily for pulling citation/author references.
 * 
 */
public class KSStructureXMLHandler extends StructureXMLHandler
{
	private List<String> authors = new ArrayList<String>();
	private String structureTitle = "";
	private JournalArticle journalArticle = new JournalArticle();

	protected boolean inStructId = false;
	protected boolean insideEntity = false;
	protected boolean insideStruct = false;
	private boolean isInCitation = false;

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

	protected List<EntityDescriptor> entityDescriptors = new ArrayList<EntityDescriptor>();
	
	protected class XAnnotatedAtom extends Atom implements IAtomAnnotator
	{
		private String annotation = "";
		private String entity_id = "";		

		public String getAnnotation() {
			return annotation;
		}

		public String getEntityId() {
			return entity_id;
		}

		public void setEntityId(String eid) {
			entity_id = eid;
		}		
	}
	
	@Override
	protected Atom createXAtom() { return new XAnnotatedAtom(); }
	
	@Override
	protected Atom createFinalAtom(Atom src) { return new AnnotatedAtom(src); }

	public KSStructureXMLHandler(final String urlString)
	{
		super(urlString);
		
		startElementRunnables.put(xmlPrefix + "audit_author", createXMLRunner__audit_author__Start());
		startElementRunnables.put(xmlPrefix + "entityCategory", createXMLRunnable__entityCategory__Start());
		startElementRunnables.put(xmlPrefix + "entity", createXMLRunnable__entity__Start());
		endElementAtomRunnables.put(xmlPrefix + "label_entity_id", createXMLRunnable__label_entity_id__End());
	}

	@Override
	public void endDocument() throws SAXException
	{
		super.endDocument();
	
		KSStructureInfo structureInfo = new KSStructureInfo();
		
		structureInfo.setDescriptors(entityDescriptors);	
		StructureAuthor structureAuthor = new StructureAuthor();
		structureAuthor.setAuthors(authors);
		structureInfo.setStructureAuthor(structureAuthor);
		structureInfo.setStructureTitle(structureTitle);
		structureInfo.setJournalArticle(journalArticle);
		structure.setStructureInfo(structureInfo);
	}




	@Override
	public void startElement(String namespaceURI, String sName, String qName,
			Attributes attrs) throws SAXException
	{
		super.startElement(namespaceURI, sName, qName, attrs);

		if (qName.equals(xmlPrefix + "citation")) {
			String attribute = attrs.getValue("id");
			if (attribute != null && attribute.equalsIgnoreCase("primary")) {
				isInCitation = true;
			}
		} else if (qName.equals(xmlPrefix + "citation_author")) {
			String attribute = attrs.getValue("citation_id");
			if (attribute != null && attribute.equalsIgnoreCase("primary")) {
				String author = attrs.getValue("name");
				if (author != null) {
					journalArticle.appendAuthor(author.trim());
				}
			}
		}	else if (qName.equals(xmlPrefix + "struct")) {
			insideStruct = true;
		}
	}

	@Override
	public void endElement(String namespaceURI,
			String sName, // simple name
			String qName // qualified name 
			) throws SAXException
	{
	    if (qName.equals(xmlPrefix + "citation")) {
			isInCitation = false;
	    } else if (qName.equals(xmlPrefix + "struct")) {
	    	insideStruct = false;
		} else if (isInCitation
				&& qName.equals(xmlPrefix + "journal_abbrev")) {
				journalArticle.setAbbreviation(buf.trim());
		} else if (isInCitation
				&& qName.equals(xmlPrefix + "journal_volume")) {
				journalArticle.setJournalVolume(buf.trim());
		} else if (isInCitation
				&& qName.equals(xmlPrefix + "author")) {
				journalArticle.appendAuthor(buf.trim());
		} else if (isInCitation && qName.equals(xmlPrefix + "page_first")) {
				try {
					int page = Integer.parseInt(buf.trim());
					journalArticle.setFirstPage(page);
				} catch (Exception e) {
				}
		} else if (isInCitation && qName.equals(xmlPrefix + "page_last")) {
				try {
					int page = Integer.parseInt(buf.trim());
					journalArticle.setLastPage(page);
				} catch (Exception e) {
				}
		} else if (isInCitation && qName.equals(xmlPrefix + "year")) {
				try {
					int year = Integer.parseInt(buf.trim());
					journalArticle.setYear(year);
				} catch (Exception e) {
				}
		} else if (insideEntity
				&& qName.equals(xmlPrefix + "entityCategory")) {
			insideEntity = false;
		} else if (insideEntity 
				&& qName.equals(xmlPrefix + "type")) {
			currentEntity.setType(buf.trim());
		} else if (insideEntity 
				&& qName.equals(xmlPrefix + "pdbx_description")) {
			currentEntity.setDescription(buf.trim()); 
			String entityId = currentEntity.getId();
			String description = currentEntity.getDescription();
			EntityDescriptor ligandEntityDescriptor = new GeneralEntityDescriptor(
					description, entityId);

			entityDescriptors.add(ligandEntityDescriptor);
		} else if (qName.equals(xmlPrefix + "audit_author")) {
			//<PDBx:audit_authorCategory>
			//   <PDBx:audit_author pdbx_ordinal="1">
			//      <PDBx:name>Fedorov, R.</PDBx:name>
			//   </PDBx:audit_author>
			//   <PDBx:audit_author pdbx_ordinal="2">
			//      <PDBx:name>Boehl, M.</PDBx:name>
			//   </PDBx:audit_author>
			//</PDBx:audit_authorCategory>
			String au = buf.trim();
			if (au != null && au.length() > 0) {
				authors.add(au);
			}
		} else if (insideStruct && qName.equals(xmlPrefix + "title")) {
			structureTitle = buf.trim();
		}
		else {
			super.endElement(namespaceURI, sName, qName);
		}
	}
	
	//
	// BEG Runnables class definitions
	//
	protected class KSXMLRunner__audit_author__Start extends XMLRunnable
	{
		public void run()
		{
			// This section handles the legacy files which used attributes
			//  <PDBx:audit_authorCategory>
		    //  <PDBx:audit_author name="Weber, P.C."></PDBx:audit_author>
		    //  <PDBx:audit_author name="Salemme, F.R."></PDBx:audit_author>
		    // </PDBx:audit_authorCategory>

			String au = attrs.getValue("name");
			if (au != null && au.length() > 0) {
			    authors.add(au.trim());
			}
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
				currentEntity.setId(id.trim());
			}
		}
	}

	public XMLRunnable createXMLRunnable__entity__Start()
	{ return new XMLRunnable__entity__Start(); }

	public class XMLRunnable__label_entity_id__End extends XMLRunnable
	{
		public void run ()
		{
			if (curAtom instanceof IAtomAnnotator)
				((IAtomAnnotator)curAtom).setEntityId(buf.trim());				
		}
	}
	
}
