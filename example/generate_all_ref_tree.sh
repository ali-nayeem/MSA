#!/bin/bash

cd "$(dirname "$0")"


core=$1  
if [ -z $core ];then
echo "Error: Provide no. of cores, Exiting..."
exit
fi

echo "Started working" > log
for inputFile in `find ./aligned/ -name BB*.msf_tfa -type f | sort -V`
do
 #inputFile=`find ./aligned/ -name "$input".msf_tfa -type f | sort -V`
 instance=`echo $inputFile | cut -d "/" -f 4 | cut -d "." -f 1` 
 
 outFile1=""$inputFile"_tt1"
 outFile2=""$inputFile"_tt2"
 
 if test -f "$outFile1"; then
    continue
 fi
 #echo $outFile
 echo "################## $instance ########################"
 echo "Starting $instance" >> log
./raxmlHPC-PTHREADS-AVX -f a -m PROTGAMMAAUTO -s $inputFile  -n ref_bs  -p 1234 -x 1234 -#100 -T $core
 
 mv RAxML_bestTree.ref_bs $outFile1
 mv RAxML_bipartitions.ref_bs $outFile2
 
 rm *ref_bs*

# break
done
