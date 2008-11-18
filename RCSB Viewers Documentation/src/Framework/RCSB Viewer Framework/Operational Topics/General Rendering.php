<?php
  session_start();
  set_include_path($_SESSION['INCLUDE_PATH']);
  include_once "resources/snippets/prefix.php";
?> 
  <ul class="preface">
    <li>
      Much of this is GL specific, and the namespace reflects that.  Expect these names to change to
      more generic terms.
    </li>
    <li>
      A further goal would be to factor out the GL specific parts of the code and allow the system to be
      switched to a different rendering engine.
    </li>
  </ul>
  <ul class="relevent-classes">
    <li>GlGeometryViewer</li>
    <li>JoglSceneNode</li>
    <li>DisplayListGeometry*</li>
    <li>Renderable</li>
    <li>DisplayListRenderable</li>
  </ul>
<?php
  include_once "resources/snippets/suffix.php";
?>