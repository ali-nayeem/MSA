#!/bin/bash

cd "$(dirname "$0")"

RV11="BB11005 BB11018 BB11020 BB11033"
RV12="BB12001 BB12013 BB12022 BB12035 BB12044"
# RV20="BB20001 BB20010 BB20022 BB20033 BB20041"
# RV30="BB30002 BB30008 BB30015 BB30022"
# RV40="BB40001 BB40013 BB40025 BB40038 BB40048"
# RV50="BB50001 BB50005 BB50010 BB50016"
exec="./target/jMetalMSA1.2-1.0-SNAPSHOT-jar-with-dependencies.jar org.uma.jmetalmsa.experiment"

echo "Running NSGAIII for RV11"
java -cp $exec.NSGAIIIStudyBalibase $RV11 2> RV11.NSGAIII

echo "Running NSGAIII for RV12"
java -cp $exec.NSGAIIIStudyBalibase $RV12 2> RV12.NSGAIII

# echo "Running NSGAIII for RV20"
# java -cp $exec.NSGAIIIStudyBalibase $RV20 2> RV20.NSGAIII
#
# echo "Running NSGAIII for RV30"
# java -cp $exec.NSGAIIIStudyBalibase $RV30 2> RV30.NSGAIII
#
# echo "Running NSGAIII for RV40"
# java -cp $exec.NSGAIIIStudyBalibase $RV40 2> RV40.NSGAIII
#
# echo "Running NSGAIII for RV50"
# java -cp $exec.NSGAIIIStudyBalibase $RV50 2> RV50.NSGAIII

echo "Running NSGAII.gap for RV11"
java -cp $exec.NSGAIIStudyBalibase gap $RV11 2> RV11.NSGAII.gap

echo "Running NSGAII.gap for RV12"
java -cp $exec.NSGAIIStudyBalibase gap $RV12 2> RV12.NSGAII.gap

# echo "Running NSGAII.gap for RV20"
# java -cp $exec.NSGAIIStudyBalibase gap $RV20 2> RV20.NSGAII.gap
#
# echo "Running NSGAII.gap for RV30"
# java -cp $exec.NSGAIIStudyBalibase gap $RV30 2> RV30.NSGAII.gap
#
# echo "Running NSGAII.gap for RV40"
# java -cp $exec.NSGAIIStudyBalibase gap $RV40 2> RV40.NSGAII.gap
#
# echo "Running NSGAII.gap for RV50"
# java -cp $exec.NSGAIIStudyBalibase gap $RV50 2> RV50.NSGAII.gap

echo "Running NSGAII.simg for RV11"
java -cp $exec.NSGAIIStudyBalibase simg $RV11 2> RV11.NSGAII.simg

echo "Running NSGAII.simg for RV12"
java -cp $exec.NSGAIIStudyBalibase simg $RV12 2> RV12.NSGAII.simg

# echo "Running NSGAII.simg for RV20"
# java -cp $exec.NSGAIIStudyBalibase simg $RV20 2> RV20.NSGAII.simg
#
# echo "Running NSGAII.simg for RV30"
# java -cp $exec.NSGAIIStudyBalibase simg $RV30 2> RV30.NSGAII.simg
#
# echo "Running NSGAII.simg for RV40"
# java -cp $exec.NSGAIIStudyBalibase simg $RV40 2> RV40.NSGAII.simg
#
# echo "Running NSGAII.simg for RV50"
# java -cp $exec.NSGAIIStudyBalibase simg $RV50 2> RV50.NSGAII.simg
