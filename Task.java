import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class Task {
    private static int popsize = 50;
    private static int maxGenerations = 100;
    private static double pc = 0.7;
    private static double pm = 0.1;
    private int MaxTime;
    private int[] tasks;
    private Random random;

    public Task(int MaxTime, int[] tasks){
        this.MaxTime = MaxTime;
        this.tasks = tasks;
        this.random = new Random();
    }

    private List<int[]> initPopulation(int popsize){
        List<int[]> population = new ArrayList<>();
        for(int i = 0; i < popsize; i++){
            int[] chromosome = new int[tasks.length];
            for(int j = 0; j < tasks.length; j++){
                chromosome[j] = random.nextInt(2);
            }
            population.add(chromosome);
        }
        return population;
    }

    private List<int[]> run(List<int[]> popList){
        List<int[]> newpopList = new ArrayList<>();
        int[] parent1 = selectChromosome(popList);
        int[] parent2 = selectChromosome(popList);
        for(int[] chromosome: popList){
            if(chromosome != parent1 && chromosome != parent2){
                newpopList.add(chromosome);
            }
        }
        List<int[]> offsprings = crossover(parent1, parent2);
        newpopList.addAll(offsprings);
        for(int[] chromosome: offsprings){
            mutation(chromosome);
        }
        int[] elitism = findBestChromosome(popList);
        if(evaluateFitness(elitism) < evaluateFitness(offsprings.get(0)) && evaluateFitness(elitism) > evaluateFitness(offsprings.get(1))){
            newpopList.remove(offsprings.get(0));
            newpopList.add(elitism);
        }
        else if (evaluateFitness(elitism) > evaluateFitness(offsprings.get(0)) && evaluateFitness(elitism) < evaluateFitness(offsprings.get(1))) {
            newpopList.remove(offsprings.get(1));
            newpopList.add(elitism);
        } else {
            newpopList.remove(offsprings.get(0));
            newpopList.add(elitism);
        }
        return newpopList;
    }

    private int[] selectChromosome(List<int[]> popList){
        double totalFitness = popList.stream().mapToDouble(this::evaluateFitness).sum();
        double randomNumber = random.nextDouble() * totalFitness;     //get random number in range 0, totalfitness
        double sum = 0;
        for(int[] chromosome: popList){
            sum += evaluateFitness(chromosome);
            if(sum >= randomNumber){
                return chromosome;
            }
        }
        return popList.get(0); //any chromosome
    }

    private List<int[]> crossover(int[] parent1, int[] parent2){
        List<int[]> children = new ArrayList<>();
        int[] offspring1 = new int[parent1.length];
        int[] offspring2 = new int[parent2.length];
        int r1 = random.nextInt(1, parent1.length - 1); // between 1 and length - 1
        double r2 = random.nextDouble(0, 1); //between 0 and 1
        if(r2 <= pc){
            for(int i = 0; i < r1; i++){
                offspring1[i] = parent1[i];
                offspring2[i] = parent2[i];
            }
            for(int i = r1; i < parent1.length; i++){
                offspring1[i] = parent2[i];
                offspring2[i] = parent1[i];
            }
            children.add(offspring1);
            children.add(offspring2);
        }
        else{
            children.add(parent1);
            children.add(parent2);
        }
        return children;
    }

    private int[] mutation(int[] chromosome){
        for(int i = 0; i < chromosome.length; i++){
            if(random.nextDouble(0, 1) <= pm){
                chromosome[i] = 1 - chromosome[i];
            }
        }
        return chromosome;
    }

    private double evaluateFitness(int[] chromosome){
        int core1 = 0, core2 = 0;
        for(int i = 0; i <chromosome.length; i++){
            if(chromosome[i] == 1){
                core1 += tasks[i];
            }
            else{
                core2 += tasks[i];
            }
        }
        int maxx = Math.max(core1, core2);
        if(core1 > MaxTime || core2 > MaxTime){
            return -1;
        }
        return maxx;
    }

    private int[] findBestChromosome(List<int[]> popList){
        return popList.stream().filter(chromosome -> evaluateFitness(chromosome) != -1)
        .min(Comparator.comparingDouble(this::evaluateFitness)).orElseGet(() -> popList.get(0));
    }

    private void Print(int[] chromosome, int generation) {
        int core1Time = 0, core2Time = 0;
        StringBuilder core1Tasks = new StringBuilder("Core 1 Tasks: ");
        StringBuilder core2Tasks = new StringBuilder("Core 2 Tasks: ");
        for (int i = 0; i < chromosome.length; i++) {
            if (chromosome[i] == 1) {
                core1Time += tasks[i];
                core1Tasks.append("Task ").append(i + 1).append(" (").append(tasks[i]).append(") ");
            } else {
                core2Time += tasks[i];
                core2Tasks.append("Task ").append(i + 1).append(" (").append(tasks[i]).append(") ");
            }
        }
        System.out.printf("Generation: %d%n", generation);
        System.out.printf("Core 1 Total Time: %d | Core 2 Total Time: %d%n", core1Time, core2Time);
        System.out.println(core1Tasks.toString());
        System.out.println(core2Tasks.toString());
        System.out.printf("Debug - Core 1 Time: %d, Core 2 Time: %d%n", core1Time, core2Time);
        System.out.println("Best Time: " + (core1Time > core2Time ? core1Time : core2Time));
        System.out.print("Best Chromosome: [ ");
        for (int gene : chromosome) {
            System.out.print(gene + " ");
        }
        System.out.println("]");
        System.out.println("------------------------------------------------");
    } 

    public void allocate(){
        List<int[]> popList = initPopulation(popsize);
        for(int i = 0; i < maxGenerations; i++){
            popList = run(popList);
            int[] bestChromosome = findBestChromosome(popList);
            Print(bestChromosome, i);
        }
    }
}
