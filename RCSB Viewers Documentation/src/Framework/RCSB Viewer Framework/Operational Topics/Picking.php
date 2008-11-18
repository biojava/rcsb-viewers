<?php
  session_start();
  set_include_path($_SESSION['INCLUDE_PATH']);
  include_once "resources/snippets/prefix.php";
?>
  <ul class="preface">
    <li>
      This is an interesting mechanism, but is fiddly and subject to rendering errors if the current render context
      happens to be the screen rendering context when you go through the picking code - you get the odd 'red-shift'
      effect.
    </li>
    <li class="follow-on">
      Suggest this should be replaced with an actual ray-pick.  I doubt if it would be any more expensive than
      the 'glReadPixel' calls (which are quite expensive), and would avoid the afore-mentioned 'red-shift'
      effect.
    </li>
  </ul>
  <ul class="questions">
    <li>
      Where does the dummy context get set up? - I'm conjecturing this happens, because it's the only possible
      solution in my comprehension, but I haven't tracked it down.
    </li>
    <li>
      What is the action that is forwarded on successful pick?
    </li>
  </ul>
  <ul class="relevent-classes">
    <li>GlGeometryViewer</li>
  </ul>
  <p>
    Picking is achieved by intercepting mouse movements and then initiating a redraw, after setting
    a flag, indicating that the requested draw is actually a pick request.</p>
  <p>
    On the redraw event, the action is forwarded to several layers of 'PickOrRedraw' functions.  If picking,
    the execution path sets a dummy context and sets up a 'unique color' scheme - essentially, the material
    for each pickable
    object type is set to a unique color (starting with 1, 0, 0 - dark red) and that association is set in
    a lookup table by color (color -> StructureComponent.)</p>
  <p>
    After rendering, the pixel at the mouse location is read (with a <em>glReadPixel</em>) and the color
    looked up in the table.  If it is found and is associated with a <em>StructureComponent</em> object,
    that object is set as the currently picked object.</p>
<?php
  include_once "resources/snippets/suffix.php";
?>