#!/bin/bash

START=0
END=9
EVOLVER=shd_first_improvement

for i in $(seq $START $END); do
  java -cp libs/JGEA.jar:target/ComputerNetwork.jar Main seed=$i evolver=$EVOLVER input=square_small;
  java -cp libs/JGEA.jar:target/ComputerNetwork.jar Main seed=$i evolver=$EVOLVER input=square_medium;
done