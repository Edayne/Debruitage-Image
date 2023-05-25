import java.util.ArrayList;
import java.awt.List;
import java.awt.image.BufferedImage;
import Jama.Matrix;
import Jama.EigenvalueDecomposition;


public class ACP {
    /**
     * 
     * @param l nb pixels de l'image
     * @param s ecart-type du bruit
     * @return renvoit le seuil calculé par la méthode VisuShrink
     */
    public double VisuShrink(int l, double s){
        return(s*Math.sqrt(2*Math.log(l)));
    
    }

    /**
     * 
     * @param s écart-type du bruit
     * @param photo L'image bruitée
     * @return
     */
    public double BayesShrink(double s, BufferedImage photo){
        int somme = 0;
        int sommeCarr = 0;
        int l = photo.getHeight();
        int c = photo.getWidth();
        for(int i=0; i<l; i++){
            for (int j=0; j<c; j++){
                somme += photo.getRGB(i,j)& 0xff;
                sommeCarr += (photo.getRGB(i,j)& 0xff)*(photo.getRGB(i,j)& 0xff);
            }
        }
        double moyenne;
        double moyCarr;
        double variance;
        moyenne =somme/(l*c);
        moyCarr=sommeCarr/(l*c);
        variance=moyCarr-moyenne;

        if (variance-(s*s) <=0){
            return(0);
        }else{
            return(Math.sqrt(variance-(s*s)));
        }
    }
    
    /**
     * 
     * @param seuil 
     * @param coefficients
     * @return
     */
    public double[][] seuillageDur(double seuil, double[][] coefficients){
        int n = coefficients.length;
        int m = coefficients[0].length;
        double[][] resultat = new double[n][m]; //Créer tableau résultat de même taille que coefficients et qui va stocker le résultat du seuillage dur
        
        for (int i = 0; i < n; i++){ // On parcours tous les itérés du tableau 
            for (int j=0; j<m; j++){
                if(Math.abs(coefficients[i][j]) <= seuil){
                    resultat[i][j] = 0.0; //Si valeur absolue du coefficients est inférieure au seuil alors notre coefficient va valoir 0
                }else{
                    resultat[i][j] = coefficients[i][j]; // Sinon il reste inchangé
                }
            }
        }
        return resultat;
    }

    public double[][] seuillageDoux(double seuil, double[][] coefficients){
        int n = coefficients.length;
        int m = coefficients[0].length;
        double[][] resultat = new double[n][m]; //créer tableau résultat de même taille que coefficients et qui va stocker le résultat du seuillage dur
        
        
        for (int i = 0; i < n; i++){ // On parcours tous les itérés du tableau 
            for (int j=0; j<m; j++) {
                if(Math.abs(coefficients[i][j]) <= seuil){
                    resultat[i][j] = 0.0; //Si valeur absolue du coefficient est inférieure au seuil alors notre coefficients va valoir 
                }
                else if((coefficients[i][j]) > seuil){
                    resultat[i][j] = coefficients[i][j]-seuil; //Si valeur  du coefficient est inférieure au seuil alors notre coefficients va valoir notre coefficient moins le seuil
                }
                else{
                    resultat[i][j] = coefficients[i][j]+seuil; //Sinon valeur du coefficient va valoir notre coefficient plus le seuil
                }
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

    /**
     * 
     * @param V Tableau de patchs vectorisées
     * @return Renvoit le vecteur moyen des patchs
     */
    public double[] calculVecteurMoyen(double[][] V){
        int nb_vecteurs = V.length; // Nb_échantillon prend le nombre de ligne de V
        int dimV = V[0].length; // Nb éléments dans chaque vecteur ici je considère qu'ils font tous la même taille
        double[] mV = new double[dimV]; //mV va stocker le vecteur moyen 
        for (int j=0; j<dimV; j++){
            double somme = 0.0; 
            for (int i=0; i<nb_vecteurs; i++){ 
                somme += V[i][j]; 
            }
            mV[j] = somme/nb_vecteurs; // Le vecteur moyen est égale à la somme des vecteurs divisé par le nombre de vecteurs
        }
        return mV;   
    }
    /**
     * 
     * @param V Tableau de patchs vectorisés
     * @return Renvoit la matrice de covariance de ces vecteurs
     */
    public double[][] calculMatriceCovariance(double[][] V){
        int nb_vecteurs = V.length; // Nb_échantillon prend le nombre de ligne de V
        int dimV = V[0].length; // Nb éléments dans chaque vecteur ici je considère qu'ils font tous la même taille

        double[] mV = calculVecteurMoyen(V); 

        double[][] Gamma = new double[dimV][dimV]; //Stocker la matrice de Covariance
        for (int j=0; j<dimV; j++){ //Pour les lignes
            for (int k=0; k<dimV; k++){ //Pour les colonnes
            double somme = 0.0; //j'initialise somme à 0
                for (int i=0; i<nb_vecteurs; i++){ //Pour chaque échantillon
                    somme += (V[i][j] - mV[j])*(V[i][k] - mV[k]); //Cela calcule la covariance entre Vj et Vk
                }
                Gamma[j][k] = somme/(nb_vecteurs);
            }
        }
        return Gamma;
    }

    public double[][] calculerVecteursCentres(double[][] V) {
        int nb_vecteurs = V.length;
        int dimV = V[0].length;

        double[] mV = calculVecteurMoyen(V);

        double[][] Vc = new double[nb_vecteurs][dimV];
        for (int i = 0; i < nb_vecteurs; i++) {
            for (int j = 0; j < dimV; j++) {
                Vc[i][j] = V[i][j] - mV[j];
            }
        }
        return Vc;
    }


    public Matrix vecteurnormalise (Matrix vectPropre){
        int nbL = vectPropre.getRowDimension(); //Récupère nombre ligne
        int nbC = vectPropre.getColumnDimension(); //Récupère nombre colonne
        for (int i=0; i<nbC; i++){ // Pour chaque vecteur propre
            Matrix vecteurn = vectPropre.getMatrix(0,nbL-1,i,i); //Extrait les vecteurs propres de la matrice
            double norm = vecteurn.normF(); //Calcule la norme de chaque vecteur
            vecteurn =vecteurn.times(1.0/norm); //Normalise le vecteur(Produit matrice * scalaire)
            vectPropre.setMatrix(0,nbL-1,i,i,vecteurn); //Remplace dans la matrice d'origine les vecteurs propres par les vecteurs propres normalisés
        }
        return vectPropre;
    }

    public double[][] acp (double[][] V){
        double [][] covariance = calculMatriceCovariance(V);
        //On récupère les valeurs propres de Cov
        Matrix covMatrix = new Matrix(covariance);
        EigenvalueDecomposition EvD = new EigenvalueDecomposition(covMatrix);
        
        Matrix vectPropre = EvD.getV(); //Matrice des vecteurs propres 
        double[][] vect =  vecteurnormalise(vectPropre).getArray();
        return vect;
    }



    public double[][] Proj(double[][] U, double[][] V_centree ){
        double[][] projection = new double[V_centree.length][U[0].length];
        int k;
        k=0;
        double coef;
        for (double[] vecteur : V_centree) {
            for (int i=0; i<U[0].length;i++) {
                coef=0;
                for(int j=0; j<vecteur.length;j++) {
                   coef+= vecteur[j]*U[i][j];
                }
                projection[k][i]= coef;
            }
            k++;
            
        }
        return projection;
    }
    
    
    
}//fin