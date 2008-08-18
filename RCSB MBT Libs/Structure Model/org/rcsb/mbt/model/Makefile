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
#  Revision 1.4  2003/07/14 21:26:16  moreland
#  Added "gui" package.
#
#  Revision 1.3  2002/11/07 00:08:00  moreland
#  Removed the "events" directory reference.
#
#  Revision 1.2  2002/10/24 17:54:02  moreland
#  Provides implementation for the improved Structure API/implementation.
#
#  Revision 1.1.1.1  2002/07/16 18:00:18  moreland
#  Imported sources
#
#  Revision 1.0  2002/06/10 23:38:39  moreland
#

include ../../../../config/Makefile.inc

SOURCES := $(wildcard *.java)
CWD := `$(PWD)`
SOURCE_PATHS := $(addprefix $(CWD)/, $(SOURCES))

all:
	( $(CD) $(SOURCE_DIR) ; $(MAKE) )

srcls:
	@$(ECHO) $(SOURCE_PATHS) | $(TR) " " "\n" | $(GREP) -v "^[g]*make"
	( $(CD) filters ; $(MAKE) srcls ) | $(GREP) -v "^[g]*make"
	( $(CD) io ; $(MAKE) srcls ) | $(GREP) -v "^[g]*make"
	( $(CD) util ; $(MAKE) srcls ) | $(GREP) -v "^[g]*make"
	( $(CD) viewables ; $(MAKE) srcls ) | $(GREP) -v "^[g]*make"
	( $(CD) gui ; $(MAKE) srcls ) | $(GREP) -v "^[g]*make"
	( $(CD) viewers ; $(MAKE) srcls ) | $(GREP) -v "^[g]*make"

clean:
	$(RM) -f $(CLASS_DIR)/edu/sdsc/mbt/*.class
	( $(CD) filters ; $(MAKE) clean )
	( $(CD) io ; $(MAKE) clean )
	( $(CD) util ; $(MAKE) clean )
	( $(CD) viewers ; $(MAKE) clean )
	( $(CD) gui ; $(MAKE) clean )
	( $(CD) viewables ; $(MAKE) clean )
	$(RMDIR) $(CLASS_DIR)/edu/sdsc/mbt

