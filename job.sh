#!/bin/bash

START=0
END=9

for i in $(seq $START $END); do
  java -cp libs/JGEA.jar:target/ComputerNetwork.jar Main seed=$i input=square_small;
done