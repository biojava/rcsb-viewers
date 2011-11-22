set FN='agreeall.txt'
rm $FN >& /dev/null
foreach NM ( *.gz )
  echo =================================== >>$FN
  echo $NM >>$FN
  zgrep Agree $NM >>$FN
end
