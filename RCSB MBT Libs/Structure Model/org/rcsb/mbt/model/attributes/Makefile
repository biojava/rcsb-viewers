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
#  Revision 1.1  2006/08/24 17:39:02  jbeaver
#  *** empty log message ***
#
#  Revision 1.1  2002/10/24 18:07:31  moreland
#  Added to build the "viewables" sub-tree.
#
#  Revision 1.1.1.1  2002/07/16 18:00:21  moreland
#  Imported sources
#
#  Revision 1.0  2002/06/10 23:38:39  moreland
#

include ../../../../../config/Makefile.inc

all:
	( $(CD) $(SOURCE_DIR) ; $(MAKE) )

SOURCES := $(wildcard *.java)
SOURCE_PATHS := $(addprefix `$(PWD)`/, $(SOURCES))
srcls:
	@$(ECHO) $(SOURCE_PATHS) | $(TR) " " "\n" | $(GREP) -v "^[g]*make"

clean:
	$(RM) -f $(CLASS_DIR)/edu/sdsc/mbt/viewables/*.class
	$(RMDIR) $(CLASS_DIR)/edu/sdsc/mbt/viewables

