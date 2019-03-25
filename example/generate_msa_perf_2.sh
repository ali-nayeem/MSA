#!/bin/bash

cd "$(dirname "$0")"

log=msaLog
echo "Started working" > $log
for instance in `cat input_ref_tree`
do
	refFile=`find /media/ali_nayeem/secondary/UBUNTU/precomputedInit/Balibase/GenML_Balibase/example/xml/ -name "$instance.xml"`
	outFile=""$instance"_tool_msa_perf2"
	echo "Starting $instance" >> $log
	echo "SP TC" > $outFile
	echo "################## $instance ########################"
	for aligned in `find ./aligned/ -name "$instance*_*" -type f | sort -V`
	    do
	    	if [[ $aligned = *"msf"* ]]; then
		   continue
		fi
		#echo $aligned
		#java -Xms2g -Xmx3g -jar FastSP.jar -r $refFile -e $aligned -o out.temp
		seqlim -outfmt msf -o msf.temp cnvt  $aligned
		./bali_score $refFile msf.temp | tail -n 1 | cut -d " " -f 3-4 >> $outFile
		#cut -f 2 -d ' ' out.temp | tr '\n' ',' >> $outFile
		#echo "" >> $outFile
		#./FastTreeMP -lg -quiet $aligned  > $bestTree
	    done
	 
	rm *temp*

	#break
done
