#!/bin/bash

cd "$(dirname "$0")"

id=1
if [ -z $id ];then
echo "Error: Provide ID, Exiting..."
exit
fi
#-----Sensitive parameters
instance=$1  #"R0"
if [ -z $instance ];then
echo "Error: Provide Instance name, Exiting..."
exit
fi
#total=1936 #200 300
#core=4
javaBinary="./jdk1.8.0_141/bin/java"
varFile="uniqueCombined_$instance"
sourceFile=`find ./example/bb3_release/ -name "$instance.tfa"`
linePerVar=`grep '>' -co $sourceFile`         #102
linePerVar=$[ linePerVar + 2]
#raxmlBinary="raxmlHPC-PTHREADS-AVX"


startVar=1 #$[($id-1)*$total+1]
totalLine=`wc -l $varFile | cut -d ' ' -f 1`
endVar=$[$totalLine/$linePerVar]
inFile="MSA.temp_$instance"
outFile="$instance"_MLtrees_id_$id
logFile="log_$instance"
bestTree="bestTree_$instance"
encodedVar="encodedVar.temp_$instance"

echo "instance: $instance id: $id, start_var: $startVar, end_var: $endVar" > $logFile
date >> $logFile
echo "instance: $instance ML trees for $startVar-th var to $endVar-th var" > $outFile

for((i=startVar;i<=endVar;i++))
do

echo "Start working for $i-th var" >> $logFile
echo "Start working for $i-th var"
#----------extract ith var from varFile to encodedVar.temp

#startLine=$[($i-1)*$linePerVar+1]
endLine=$[$i*$linePerVar]
#echo $startLine $endLine
head -n $endLine $varFile | tail -n $linePerVar > $encodedVar


#----------convert inFile to MSA inFile

$javaBinary -cp jMetalMSA1.2-1.0-SNAPSHOT-jar-with-dependencies.jar org.uma.jmetalmsa.stat.DecodeVAR_Balibase $instance $encodedVar $inFile

#----------run raxmlBinary for inFile

#./$raxmlBinary -m GTRGAMMA -s $inFile -n best -N 1 -p 1234 -T $core
./FastTreeMP -lg -quiet $inFile  > $bestTree

#----------append result to outFile

echo "Tree for $i-th var" >> $outFile
cat $bestTree >> $outFile

#----------delete raxml generated files

#rm *best*

done
date >> $logFile
