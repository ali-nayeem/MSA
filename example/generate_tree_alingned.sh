#!/bin/bash

cd "$(dirname "$0")"

fname="BB?????.msf"
temp="./temp"
bestTree="bestTmp"
for inputFile in `find ./aligned/ -name $fname -type f | sort -V`
do
 instance=`echo $inputFile | cut -d "/" -f 4 | cut -d "." -f 1` 
 outFile=""$inputFile"_tree"
 #echo $outFile
 echo "################## $instance ########################"
 echo "instance: $instance ML trees for precomputed alignmnet" > $outFile
    for aligned in `find ./aligned/ -name "$instance*_*" -type f | sort -V`
    do
    	if [[ $aligned = *"msf"* ]]; then
	   continue
	fi
	#echo $aligned
	
	./FastTreeMP -lg -quiet $aligned  > $bestTree

	#----------append result to outFile
	echo "Tree for $aligned" >> $outFile
	cat $bestTree >> $outFile
    done
 #break
done
rm $bestTree
