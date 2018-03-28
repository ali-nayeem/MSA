#!/bin/bash

cd "$(dirname "$0")"

fname="BB?????.tfa"
temp="./temp"
for inputFile in `find ./bb3_release/ -name $fname -type f | sort -V`
do
 output=`echo $inputFile | cut -d "/" -f 3-`
 output=./aligned/"$output"_pasta
 echo "################## $output ########################"
 run_pasta.py --auto -d Protein -i $inputFile  -o ./pasta -j msa
 cp ./pasta/msa.marker001.*.aln $temp
 python ~/scripts/stable.py "$inputFile" $temp > $output
 rm -r ./pasta
 rm $temp
 #break
done
