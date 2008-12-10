<?php
  session_start();
  set_include_path($_SESSION['INCLUDE_PATH']);
  include_once "resources/snippets/prefix.php";
?>
<p>
We need to organize the source in such a way that we know how and where to find things.
To a certain extent, we can do this with package namespaces, but when we get a lot
of packages, things can become a bit blurry.</p>
<p>
Note that <em>source division</em> simply describes how the source is divided up, not
specifically any architectural divisions (although there is some corollary - see below</p>
<h2>Approach</h2>
<p>
We can use the Eclipse <em>Source Directory</em> feature to divide the source into
larger, very apparent divisions.</p>
<p>
In examining the MBT, it becomes (eventually) apparent the code basically falls into
five recognizable major categories.  These are listed here, along with the architectural
groups that fall within them:</p>
<img src="images/MBTCodeDivision.png" alt="MBT Source Code Divisions" />
<p>
While these tend to mostly follow architectural lines, two divisions follow toolkit
implementation lines, specifically the <em>UI</em> and <em>GL Scene</em> divisions.</p>
<p>
This is because they are each implemented with a toolkit - <em>Swing</em> and <em>OpenGL</em>
respectively.  We want to isolate these specifically, to allow us to replace them, should
we desire.  Also, we can use this as a check to see if too much controller/model or other
application internal implementation is creeping in here.</p>
<p>
Ideally, these code divisions should:</p>
<ol>
<li>Be the <em>only</em> place where the implementing toolkit code resides.</li>
<li>Contain as little application-implementation code as possible.</li>
</ol>
<p>
Note in the case of the <em>GL Scene</em>, this is currently not the case - in the future,
we may want to break this down into <em>Scene</em> and <em>GL Scene</em> for generic scene
implementation (if there is such a thing) and <em>OpenGL</em>-scene implementation,
respectively.</p>
<h2>Extra Division - Structure Loader</h2>
<p>
The <em>Structure Loader</em> is a fairly large subsystem in and of itself - thus it seemed
appropriate to put it in its own division.<a href="#notes"><sup>1</sup></a>
In architectural terms, technically, it could
be considered part of the <em>DocController</em>, but breaking it out keeps it all together
as a mechanism <em>used</em> by the <em>DocController</em> without cluttering up that code
with too much detail.</p>
<h2 id="h2-jardiv">Jar Division Reflects Code Division</h2>
<p>
Ultimately the MBT is output to one or more jars for loading into an application.  An application
shouldn't have to load any more code than it needs - thus, the multiple jars are created reflecting
the code division.</p>
<img src="images/MBTCodeDivisionJars.png" alt="MBT Jars Division" />
<p>
So, for an analytic application that has no UI or scene or doesn't use the app mechanism,
only following jars need be loaded:</p>
<img src="images/MBTAnalyticAppJars.png" alt="MBT Analytic App Jars.png" />
<hr/>
<div id="notes">
<sup>1</sup>Actually, this turns out to be a benefit.  See the discussion following in the
<a href="#h2-jardiv"><em>Jar Division Reflects Code Division</em></a> section.
</div>
<?php
  include_once "resources/snippets/suffix.php";
?>