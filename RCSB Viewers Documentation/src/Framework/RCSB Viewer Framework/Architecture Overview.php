<?php
  session_start();
  set_include_path($_SESSION['INCLUDE_PATH']);
  include_once "resources/snippets/prefix.php";
?>
<p>
The architecture of the <em>RCSB MBT Libs</em> is a blend of two well understood architectures:</p>
<ul>
<li><em>App/Mainframe/Document</em>, and</li>
<li><em>Model/View/Controller</em></li>
</ul>
<p>
The <em>App/Mainfame/Document</em> structure is especially suited to desktop applications (of which
the viewer <em>MBT</em> derivatives belong) and should be familiar to anyone who has worked with
a mainstream windowing system, such as Microsoft's <em>C++/MFC</em> or <em>C#/Forms (application)</em> framework, or
<em>PowerPlant</em> on the <em>Mac</em>, or <em>Viewkit</em> on <em>IRIX</em>, etc.</p>
<p class="newidea">
We also like to think in terms of <em>Model/View/Controller</em>, and we can blend the two
notions as follows:</p>
<img src="images/MBTArchAMDtoMVC.png" alt="AMD to MVC Mapping"/>
<p>
From here, it is easy to determine where most systems fit into the architecture, and how to
extend it, gracefully.</p>
<p>
(Note that <em>Architecture</em> does not necessarily follow <em>Source Division</em>, which is
described in the previous chapter, although there is overlap.)</p>
<h2>Architectural Components Overview</h2>
<p>
A quick overview of the architectural components that make up the MBT (and is reflected
up through the viewers) can be ascertained from the following diagram:</p>
<img src="images/MBTSingleFrameContainmentArch.png" alt="Multiple Frame Containment" />
<p>
The component breakout, with a brief explanation of each is as follows:</p>
<dl>
<dt>App/Master Controller</dt>
<dd>
The <em>app</em> class (in the viewers, derived from <em>VFAppBase</em> from the
<em>Viewer Framework</em> project) is the focal point of the application.  The main
is typically attached to the derivation of this class.  The derived class is typically
named the same as the application.</dd>
<dd>
This class is the means by which all of the other components (directly or indirectly)
are accessed.  Typical components are the <em>Mainframe/DocumentFrame</em> and various
controllers.</dd>
<dt>Mainframe/DocumentFrame</dt>
<dd>
Applications typically have a main frame (or window) which contains a representation of the
document.  We introduce the notion of 'DocumentFrame' to distinguish it from 'Mainframe'
for reasons we will discuss, later.  For now, the entities are one and the same.</dd>
<dd>
The <em>DocumentFrame</em> contains UI necessary to contain, display, and
possibly interact with the document (such as control panels, menus, etc.)  In
that sense, it acts as a 'views controller'.</dd>
<dd>
It is also the access point for the <em>model</em> and doc-centric <em>controllers</em>
(controllers that control some aspect of the <em>model</em>, vs. globally or other parts
of the application.</dd>
<dd>
Note that by associating the <em>model</em> and doc-centric <em>controllers</em> with
a frame, we now are free to expand the above <em>single-framed</em> representation to
a <em>multiple-framed</em> representation:
<img src="images/MBTMDIContainmentArch.png" alt="Multiple Frame Containment"/>
This is simply a repeated structure for each <em>DocumentFrame</em>, with the addition of
two more components:
	<ul>
	<li>
	A <em>Multiple Frame Controller</em> (not implemented this version.)  This component does
	the following:
		<ul>
		<li>Creates the <em>Document Frames</em> as they are requested.</li>
		<li>Provides access to the <em>DocumentFrames</em>, as well as their contained components.</li>
		<li>Maintains 'active frame' status (most access requests will be via the active frame.)</li>
		</ul>
	</li>
	<li><em>Separated 'Other UI'</em> - UI that is not tied to a document, or is updated when the active 
	    <em>Document Frame</em> changes.  An example would be a control panel that is not in a document
      frame.</li>
	</ul>
</dd>
<dt>Model</dt>
<dd>An instance of the data that defines the model.  Currently an array of structures.</dd>
<dt>Controllers (doc-centric)</dt>
<dd>
There are a number of these to control subfunctions/systems.  Currently, these are:
  <ul>
  <li><em>DocController</em> - controls the document, in particular loading/saving.</li>
  <li><em>UpdateController</em> - controls change updates sent to registered listeners.</li>
  <li><em>SceneController</em> - controls creation, access and changes to the scene.</li>
  <li><em>StateController</em> - controls state attributes of the document.</li>
  <li><em>MutatorController</em> - controls changes to the document.</li>
  </ul>
</dd>
<dd>
More are expected to be added as functionality grows or funcional units are further identified.</dd>
<dt>GlViewer</dt>
<dd>The 3d viewer, attached to the <em>DocumentFrame</em>.  Renders the scene.</dd>
<dt>Other UI</dt>
<dd>
Loose definition for various other views/panels/dialogs that are related to the document.</dd>
</dl>
<?php
  include_once "resources/snippets/suffix.php";
?>