#!/bin/bash

cd "$(dirname "$0")"

log=msaLog
echo "Started working" > $log
for instance in `cat input_ref_tree`
do
	refFile=`find ./aligned/ -name "$instance.msf_tfa"`
	outFile=""$instance"_tool_msa_perf"
	echo "Starting $instance" >> $log
	echo "SP-Score Modeler SPFN SPFP Compression TC" > $outFile
	echo "################## $instance ########################"
	for aligned in `find ./aligned/ -name "$instance*_*" -type f | sort -V`
	    do
	    	if [[ $aligned = *"msf"* ]]; then
		   continue
		fi
		#echo $aligned
		java -Xms2g -Xmx3g -jar FastSP.jar -r $refFile -e $aligned -o out.temp
		cut -f 2 -d ' ' out.temp | tr '\n' ',' >> $outFile
		echo "" >> $outFile
		#./FastTreeMP -lg -quiet $aligned  > $bestTree
	    done
	 
	rm *temp*

	#break
done
