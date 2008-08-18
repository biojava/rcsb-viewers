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
#  Revision 1.1  2006/09/20 16:50:43  jbeaver
#  first commit - branched from ProteinWorkshop
#
#  Revision 1.1  2006/08/24 17:39:03  jbeaver
#  *** empty log message ***
#
#  Revision 1.2  2004/04/15 20:45:23  moreland
#  Added chemical component bond dictionary.
#
#  Revision 1.1.1.1  2002/07/16 18:00:21  moreland
#  Imported sources
#
#  Revision 1.0  2002/06/10 23:38:39  moreland
#

include ../../../../../config/Makefile.inc

# NOTE: the chemical component bond dictionary needs to be copied to the
# same directory as the ChemicalComponentBonds class so that the Java
# "getResourceAsStream" method can find it at runtime. This will work
# while the file is in the filesystem directory OR when it is inside of
# a Jar file.
# The $(DICT_INST) target copies the local dictionary file to the
# correct classes directory.
DICT_INST_DIR := $(CLASS_DIR)/edu/sdsc/mbt/util
DICT_FILE := ChemicalComponentBonds.dat
DICT_INST_FILE := $(CLASS_DIR)/$(DICT_FILE)

all:
	( $(CD) $(SOURCE_DIR) ; $(MAKE) )

SOURCES := $(wildcard *.java)
SOURCE_PATHS := $(addprefix `$(PWD)`/, $(SOURCES))
srcls: $(DICT_INST_FILE)
	@$(ECHO) $(SOURCE_PATHS) | $(TR) " " "\n"

$(DICT_INST_FILE): $(DICT_FILE)
	$(MKDIR) -p $(DICT_INST_DIR)
	$(CP) -u $(DICT_FILE) $(DICT_INST_DIR)

clean:
	$(RM) -f $(CLASS_DIR)/edu/sdsc/mbt/util/*.class
	$(RMDIR) $(CLASS_DIR)/edu/sdsc/mbt/util

