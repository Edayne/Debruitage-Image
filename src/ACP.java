import java.util.ArrayList;
import java.util.List;
import java.awt.image.BufferedImage;
import Jama.Matrix;
import Jama.EigenvalueDecomposition;
import Jama.*;


public class ACP {
    /**
     * 
     * @param nbPix nb pixels de l'image
     * @param s ecart-type du bruit
     * @return renvoit le seuil calculé par la méthode VisuShrink
     */
    public double VisuShrink(int nbPix, double s){
        return(s*Math.sqrt(2*Math.log((double)nbPix)));
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
     * @param seuil Seuil calculés par les méthodes VisuShrink et BayesShrink
     * @param coefficients Projection des patchs vectorisés dans la base de l'ACP
     * @return La matrice de projection dont les coeffcients inférieurs sont supprimés
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

    /**
     * 
     * @param seuil Seuil calculés par les méthodes VisuShrink et BayesShrink
     * @param coefficients Projection des patchs vectorisés dans la base de l'ACP
     * @return Matrice de projection altérée
     */
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
    
    public double calculateMSE(BufferedImage X, BufferedImage Y) {
        int l = X.getHeight();
        int c = X.getWidth();

        if (l != Y.getHeight() || c != Y.getWidth()) {
            throw new IllegalArgumentException("Input matrices must have the same size.");
        }

        double MSE = 0.0;

        for (int i = 0; i < l; i++) {
            for (int j = 0; j < c; j++) {
                double pixelX = (X.getRGB(i, j)>>16)&0xFF;
                double pixelY = (Y.getRGB(i, j)>>16)&0xFF;
                double diff = pixelX - pixelY;
                MSE += diff * diff;
            }
        }
        MSE = MSE/(l * c);

        return MSE;
    }

    public double calculatePSNR(BufferedImage X, BufferedImage Y){
        double MSE = calculateMSE(X, Y);
        double PSNR = 10*Math.log10(255/MSE);
        return PSNR;
    }

    /**
     * 
     * @param V Tableau de patchs vectorisées
     * @return Renvoit le vecteur moyen des patchs
     */
    public double[] calculVecteurMoyen(List<int[]> V){
        int nb_vecteurs = V.size(); 
        int dimV = V.get(0).length; // Nb éléments dans chaque vecteur ici je considère qu'ils font tous la même taille
        double[] mV = new double[dimV]; //mV va stocker le vecteur moyen 
        for (int j=0; j<dimV; j++){
            double somme = 0.0; 
            for (int i=0; i<nb_vecteurs; i++){ 
                somme += V.get(i)[j]; //j-eme coordonnée du i-eme vecteur
            }
            mV[j] = somme/nb_vecteurs; // Le vecteur moyen est égale à la somme des vecteurs divisé par le nombre de vecteurs
        }
        return mV;
    }

    /**
     * Calcule de la covariance d'une liste de vecteurs
     * @param V Tableau de patchs vectorisés
     * @return Renvoit la matrice de covariance de ces vecteurs
     */
    public double[][] calculMatriceCovariance(List<int[]> V){
        int nb_vecteurs = V.size(); // Nb_échantillon prend le nombre de ligne de V
        int dimV = V.get(0).length; // Nb éléments dans chaque vecteur

        double[] mV = calculVecteurMoyen(V); 

        double[][] Gamma = new double[dimV][dimV]; //Stocker la matrice de Covariance
        for (int j=0; j<dimV; j++){ //Pour les lignes
            for (int k=0; k<dimV; k++){ //Pour les colonnes
            double somme = 0.0; 
                for (int[] vecteur : V){
                    somme += (vecteur[j] - mV[j])*(vecteur[k] - mV[k]); //Cela calcule la covariance entre Vj et Vk
                }
                Gamma[j][k] = somme/(nb_vecteurs);
            }
        }
        return Gamma;
    }

    /**
     * Renvoie les patchs vectorisés centrés
     * @param V Liste des vecteurs des patchs
     * @return List des vecteurs des patchs centrés
     */
    public ArrayList<double[]> calculerVecteursCentres(ArrayList<int[]> V) {
        ArrayList<double[]> Vc = new ArrayList<>();

        int[] vectNum0 = V.get(0);
        int dimV = vectNum0.length;
        double[] mV = calculVecteurMoyen(V);
        

        for (int[] vecteur : V) {
            double[] vectCent = new double[dimV];
            for (int j = 0; j < dimV; j++) {
                vectCent[j] = vecteur[j] - mV[j];
            }
            Vc.add(vectCent);
        }
        return Vc;
    }
    
    public double[][] convertListIntabDouble(List<int[]> V) {
        int nb_vecteurs = V.size();
        int dimV = V.get(0).length;
        
        double[][] tab = new double[nb_vecteurs][dimV];
        int j=0;
        for(int[] element: V) {
        	for(int i=0; i<element.length; i++) {
        		tab[j][i]=element[i];       		
        	}
        	j++;
        }

    	return tab;
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

    /**
     * 
     * @param X
     * @param Y
     * @return
     */
    public double pdtScal(double[] X, double[] Y) {
        double resultat=0;
        for (int i = 0; i < X.length; i++) {
            resultat += X[i]*Y[i]; 
        }
        return resultat;
    }

    public double[][] acp (ArrayList<int[]> V){
        System.out.println("\t\tCalcul de la matrice de covariance...");
        double [][] covariance = calculMatriceCovariance(V);
        //On récupère les valeurs propres de Cov
        Matrix covMatrix = new Matrix(covariance);
        System.out.println("\t\tCalcul des vecteurs propres de la matrice de covariance...");
        EigenvalueDecomposition EvD = new EigenvalueDecomposition(covMatrix);
        
        Matrix vectPropre = EvD.getV(); //Matrice des vecteurs propres 
        double[][] base =  vecteurnormalise(vectPropre).getArray();
        return base;
    }

    /**
     * Renvoit la projection des patchs vectorisés dans la base donnée par l'ACP
     * @param U Base orthonormale donnée par acp()
     * @param V_centree Les patchs vectorisés centrés
     * @return La projection de V_centree dans U
     */
    public double[][] proj(double[][] U, ArrayList<double[]> V_centree ){
        int nbVecteurs = V_centree.size(); //yavait écrit U.length avant
        int dimACP = U[0].length;
        
        double[][] projection = new double[nbVecteurs][dimACP];

        int k = 0; //Indice du vecteur actuel
        double coef;

        for (double[] vecteur : V_centree) {
            for (int i=0; i<U[0].length;i++) { //On calcule la ieme coordonnée du k-eme vecteur
                coef=0;
                for(int j=0; j<vecteur.length;j++) {
                   coef += U[j][i]*vecteur[j];
                }
                projection[k][i] = coef;
            }
            k++;
        }
        return projection;
    }

    // public double[][] proj2(double[][] U, ArrayList<double[]> V_centree ){
    //     double[][] projection = new double[V_centree.size()][U[0].length];

    //     for (int j = 0; j<V_centree.size(); j++) {
    //         double[] vecteur = V_centree.get(j);
    //         double coefProj = 0.0;
    //         int k=0;
    //         for (int i=0; i<U.length; i++){
    //             coefProj += U[i][j]*vecteur[j];
    //         }
            
    //     }
    // }
}//fin