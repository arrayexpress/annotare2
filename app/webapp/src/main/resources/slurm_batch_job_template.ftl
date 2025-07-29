#!/bin/bash
#Submit this script with: sbatch thefilename
#For more details about each parameter, please check SLURM sbatch documentation https://slurm.schedmd.com/sbatch.html

#SBATCH --time=01:00:00   # walltime
#SBATCH --ntasks=1   # number of tasks
#SBATCH --cpus-per-task=1   # number of CPUs Per Task i.e if your code is multi-threaded
#SBATCH --nodes=1   # number of nodes
#SBATCH -p datamover   # partition(s)
#SBATCH --mem=2G   # memory per node
#SBATCH -J "${jobName}"   # job name
#SBATCH -o "${outputFile}"   # job output file
#SBATCH -e "${errorFile}"   # job error file
#SBATCH --mail-user=ae-mgmt@ebi.ac.uk   # email address
#SBATCH --mail-type=FAIL
#SBATCH -W

${commands}