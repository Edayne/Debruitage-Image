import java.util.ArrayList;
import java.awt.image.BufferedImage;

public class ACP {
    public double VisuShrink(int l, double s){
        return(s*Math.sqrt(2*Math.log(l)));
    }


    public double BayesShrink(double s, BufferedImage photo){
        int n;
        n=0;
        int p;
        p=0;
        for(int j=0;j<photo.getHeight();j++){
            for (int i=0;i <photo.getWidth();i++){
                n+=photo.getRGB(i,j);
                p+=(photo.getRGB(i,j)*photo.getRGB(i,j));
            }   	
        }
        double moyenne;
        double moy;
        double variance;
        moyenne=n/(photo.getHeight()*photo.getWidth());
        moy=p/(photo.getHeight()*photo.getWidth());
        variance=moy-moyenne;

        if (variance-s <=0){
            return(0);


        }else{
            return(Math.sqrt(variance-s));
        }

    }
    public ArrayList<Double> seuillageDur(double seuil, ArrayList<Double> coefficients){
        ArrayList<Double> resultat = new ArrayList<Double>(); //Créer tableau résultat de même taille que coefficients et qui va stocker le résultat du seuillage dur
        int n = coefficients.size();
        
        for (int i = 0; i < n; i++){ // On parcours tous les itérés du tableau 
            if(Math.abs(coefficients.get(i)) <= seuil){
                resultat.add(0.0); //Si valeur absolue du coefficients est inférieure au seuil alors notre coefficients va valoir 
            }else{
                resultat.add(coefficients.get(i)); // Sinon il reste inchangé
            }
        }
        return resultat;
    }
    public ArrayList<Double> seuillageDoux(double seuil, ArrayList<Double> coefficients){
        ArrayList<Double> resultat = new ArrayList<Double>(); //créer tableau résultat de même taille que coefficients et qui va stocker le résultat du seuillage dur
        int n = coefficients.size();
        
        for (int i = 0; i < n; i++){ // On parcours tous les itérés du tableau 
            if(Math.abs(coefficients.get(i)) <= seuil){
                resultat.add(0.0); //Si valeur absolue du coefficient est inférieure au seuil alors notre coefficients va valoir 
            }
            else if((coefficients.get(i)) > seuil){
                resultat.add(coefficients.get(i)-seuil); //Si valeur  du coefficient est inférieure au seuil alors notre coefficients va valoir notre coefficient plus le seuil
            }
            else{
                resultat.add(coefficients.get(i)+seuil); //Sinon valeur du coefficient va valoir notre coefficient plus le seuil
            }
        }
        return resultat;
    }
    public static double calculateMSE(BufferedImage X, BufferedImage Y) {
        int l = X.getHeight();
        int c = X.getWidth();

        if (l != Y.getHeight() || c != Y.getWidth()) {
            throw new IllegalArgumentException("Input matrices must have the same size.");
        }

        double MSE = 0.0;

        for (int i = 0; i < l; i++) {
            for (int j = 0; j < c; j++) {
                double diff = X.getRGB(i, j) - Y.getRGB(i, j);
                MSE += diff * diff;
            }
        }

        MSE = 1/(l * c);

        return MSE;
    }

    public static double calculatePSNR(BufferedImage X, BufferedImage Y){
        double MSE = calculateMSE(X, Y);
        double PSNR = 10*Math.log10(255/MSE);
        return PSNR;
    }


    public double[] calculVecteurMoyen(double[][] V){
        int nb_echantillon = V.length; // Nb_échantillon prend le nombre de ligne de V
        int dimV = V[0].length; // Nb éléments dans chaque vecteur ici je considère qu'ils font tous la même taille
        double[] mV = new double[dimV]; //mV va stocker le vecteur moyen 
        for (int j=0; j<dimV; j++){
            double somme = 0.0;
            for (int i=0; i<nb_echantillon; i++){
                somme += V[i][j]; //somme prend à chaque itérés la somme du vecteur
            }
            mV[j] = somme/nb_echantillon; // Le vecteur moyen est égale à la somme des vecteurs divisé par le nb_échantillon
        }
        return mV;   
    }
    
}

    public double[][] calculMatriceCovariance(double[][]V)