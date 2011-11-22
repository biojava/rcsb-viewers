
#
# JAVA
#

JAVA_HOME := /usr/local/apps/j2sdk1.4.2
JAVA := ${JAVA_HOME}/bin/java
JAVAC := ${JAVA_HOME}/bin/javac

#
# JOGL
#

JOGL_HOME := /scratch/slocal/java/jogl/jogl_1.1b10

CLASSPATH := .
CLASSPATH := ${CLASSPATH}:${JOGL_HOME}/jogl.jar

LD_LIBRARY_PATH = ${JOGL_HOME}/lib/linux

#
# MBT
#

CLASSPATH := ${CLASSPATH}:/projects/misc/mbt/cvs_work/moreland/mbt/classes

#
# TARGETS
#

SRCS := $(wildcard *.java)
OBJS := $(SRCS:.java=.class)

run: $(OBJS) run2

run2:
	$(JAVA) -Xms300m -Xmx300m -classpath "${CLASSPATH}" JoglViewer -p 4hhb

rune: $(OBJS)
	$(JAVA) -classpath "${CLASSPATH}" Extrusion

runt: $(OBJS)
	$(JAVA) -classpath "${CLASSPATH}" TestViewer

%.class : %.java
	$(JAVAC) -deprecation -classpath "${CLASSPATH}" $<

clean:
	rm -rf *.class hs_err*.log *.html package-list resources stylesheet.css

