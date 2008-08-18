#  $Id: Makefile,v 1.1 2007/02/08 02:38:52 jbeaver Exp $
#
#  Copyright (c) 2000-2002  San Diego Supercomputer Center (SDSC),
#  a facility operated jointly by the University of California,
#  San Diego (UCSD) and General Atomics, San Diego, California, USA.
#
#  Users and possessors of this source code are hereby granted a
#  nonexclusive, royalty-free copyright and design patent license to
#  use this code in individual software.  License is not granted for
#  commercial resale, in whole or in part, without prior written
#  permission from SDSC.  This source is provided "AS IS" without express
#  or implied warranty of any kind.
#
#  For further information, please see:  http://mbt.sdsc.edu
#
#  History:
#  $Log: Makefile,v $
#  Revision 1.1  2007/02/08 02:38:52  jbeaver
#  version 1.50
#
#  Revision 1.1  2006/09/20 16:50:42  jbeaver
#  first commit - branched from ProteinWorkshop
#
#  Revision 1.1  2006/08/24 17:39:03  jbeaver
#  *** empty log message ***
#
#  Revision 1.3  2003/12/16 22:22:47  moreland
#  Added pdb exchange dictionary to support NMR models.
#
#  Revision 1.2  2002/10/26 00:51:04  moreland
#  Fixed CIF dictionary copy target.
#
#  Revision 1.1  2002/10/24 17:42:33  moreland
#  Added to cifparse rcsb sub-tree build.
#
#  Revision 1.1.1.1  2002/07/16 18:00:18  moreland
#  Imported sources
#
#  Revision 1.0  2002/06/10 23:38:39  moreland
#

include ../../../../../config/Makefile.inc

SOURCES := $(wildcard *.java)
CWD := `$(PWD)`
SOURCE_PATHS := $(addprefix $(CWD)/, $(SOURCES))

# NOTE: the CIF dictionary needs to be copied to the same directory
# as the CifTokenizer class so that the Java "getResourceAsStream"
# method can find it at runtime. This will work while the file is
# in the filesystem directory OR when it is inside of a Jar file.
# The $(DICT_INST) target copies the local dictionary file to the
# correct classes directory.
DICT_INST_DIR := $(CLASS_DIR)/org/rcsb/openmms/cifparse
DICT_FILE := cif_mm_V2.0.3.dic
DICT_INST_FILE := $(CLASS_DIR)/$(DICT_FILE)
EXCH_DICT_FILE := pdbx_exchange.dic
EXCH_DICT_INST_FILE := $(CLASS_DIR)/$(EXCH_DICT_FILE)

all:
	( $(CD) $(SOURCE_DIR) ; $(MAKE) )

srcls: $(DICT_INST_FILE) $(EXCH_DICT_INST_FILE)
	@$(ECHO) $(SOURCE_PATHS) | $(TR) " " "\n" | $(GREP) -v "^[g]*make"

$(DICT_INST_FILE): $(DICT_FILE)
	$(MKDIR) -p $(DICT_INST_DIR)
	$(CP) -u $(DICT_FILE) $(DICT_INST_DIR)

$(EXCH_DICT_INST_FILE): $(EXCH_DICT_FILE)
	$(MKDIR) -p $(DICT_INST_DIR)
	$(CP) -u $(EXCH_DICT_FILE) $(DICT_INST_DIR)

clean:
	$(RM) -f $(CLASS_DIR)/org/rcsb/openmms/cifparse/*.class
	$(RMDIR) $(CLASS_DIR)/org/rcsb/openmms/cifparse

