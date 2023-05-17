import java.util.ArrayList;

public class ACP {

    public double[] calculVecteurMoyen(double[][] V){
        int nb_echantillon = V.length; // Nb_échantillon prend le nombre de ligne de V
        int dimV = V[0].length; // Nb éléments dans chaque vecteur ici je considère qu'ils font tous la même taille
        double[] mV = new double[dimV]; //mV va stocker le vecteur moyen 
        for (int j=0; j<dimV; j++){
            double somme = 0.0; //j'initialise somme à 0
            for (int i=0; i<nb_echantillon; i++){
                somme += V[i][j]; //somme prend à chaque itérés la somme du vecteur
            }
            mV[j] = somme/nb_echantillon; // Le vecteur moyen est égale à la somme des vecteurs divisé par le nb_échantillon
        }
        return mV;   
    }
    
}

    public double[][] calculMatriceCovariance(double[][] V){
        int nb_echantillon = V.length; // Nb_échantillon prend le nombre de ligne de V
        int dimV = V[0].length; // Nb éléments dans chaque vecteur ici je considère qu'ils font tous la même taille

        double[] mV = calculVecteurMoyen(V); //

        double[][] Gamma = new double[dimV][dimV];
        for (int j=0; j<dimV; j++){
            double somme = 0.0; //j'initialise somme à 0
            for (int i=0; i<nb_echantillon; i++){
                somme += 
            }
        }
        
    }