<?php
  $rootPath = dirname(__FILE__);
  set_include_path($rootPath);
  session_start();
  $_SESSION['INCLUDE_PATH'] = get_include_path();
  
  echo $rootPath . '<br/>';
  include_once "resources/snippets/prefix.php";
?>
<h1>Placeholder</h1>
<br/>
<a href="Framework/RCSB Viewer Framework/Architecture Overview.php">Architecture Overview</a>;
<?php
include_once "resources/snippets/suffix.php";
?>