<?php
  session_start();
  set_include_path($_SESSION['INCLUDE_PATH']);
  include_once "resources/snippets/prefix.php";
?> 
  <ul class="preface">
    <li>
      Much of this is GL specific, and the namespace reflects that.  Expect these names to change to
      more generic terms.  For example:
      <ul>
        <li>JoglSceneNode -> Scene (node has connotations in scenegraph structures, so would rather either
            ignore the term, or tie to a proper corollary)</li>
        <li>DisplayListGeometry -> ScenePrimitive</li>
        <li>DisplayListRenderable -> SubScene</li>
        <li>DisplayLists -> ??</li>
      </ul>
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
    <li>DisplayLists</li>
  </ul>
<?php
  include_once "resources/snippets/suffix.php";
?>