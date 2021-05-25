package it.polito.tdp.meteo.model;

import java.util.LinkedList;
import java.util.*;

import it.polito.tdp.meteo.DAO.MeteoDAO;

public class Model {
	
	private final static int COST = 100;
	private final static int NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN = 3;
	private final static int NUMERO_GIORNI_CITTA_MAX = 6;
	private final static int NUMERO_GIORNI_TOTALI = 15;
	MeteoDAO meteodao;
	public Model() {
  meteodao=new MeteoDAO();
	}

	// of course you can change the String output with what you think works best
	public double getUmiditaMedia(int mese,String localita) {
		List <Rilevamento> ril= new LinkedList<Rilevamento>(this.meteodao.getAllRilevamentiLocalitaMese(mese, localita));
		int tot=0;
		for(Rilevamento r:ril) {
			 tot=tot+r.getUmidita();
		}
		
		return (double)(tot/(double)ril.size());
	}
	Map <String,List<Rilevamento >> sol = new HashMap<String,List<Rilevamento>>();
	List <Citta> migliore;
	
	
	// of course you can change the String output with what you think works best
	public List<Citta>trovaSequenza(int mese) {
		List <Citta> parziale = new ArrayList <Citta>();
		 migliore= null;
		
		for(Citta c: this.getTuttelocalita())
		 sol.put(c.getNome(),this.meteodao.getAllRilevamentiLocalitaMese(mese, c.getNome()));//HO FATTO UNA MAPPA INVECE DI SFRUTTARE LA CLASSE CITTA
			cerca(parziale,0);
		
		return  migliore;
	}
	
	;
	public void cerca(List<Citta>parziale,int livello) {
		
		if(livello==15) {  //SE HO CALCOLATO LA SEQUENZA PER 15 GIORNI  POSSO CALCOLARE I COSTI 
			double costo= calcoloCosti(parziale);

			if( migliore== null||costo<calcoloCosti(migliore))
				migliore= new ArrayList<>(parziale);
		}else {
			for(Citta c:this.getTuttelocalita()) {
				//PRIMA DI AGGIUNGERE LA CITTA DEVO VEDERE SE NON HA SUPERATO I GIORNI
				if(aggiuntaValida(parziale,c)==true) {
				parziale.add(c);
				
				cerca(parziale,livello+1);
				parziale.remove(parziale.size()-1);
				}
				//DOPO AVER AGGIUNTO LA CITTA CONTROLLO I COSTI E VEDO SE RAPPRESENTA UNA DELLE SOLUZIONI MIGLIORI
				//POI TOLGO LA CITTA
				//AGGIUNGO UN ALTRA CITTA IMPLEMENTANDO IL LIVELLO 
				
			}
			
			
		}
	}
	private double calcoloCosti(List<Citta> parziale) {
		double costo=0.0;
		double costoUltimo=0;
		for(int i=0;i<parziale.size()-1;i++) {
			List<Rilevamento> ril=new ArrayList<Rilevamento>( sol.get(parziale.get(i).getNome()));
		costoUltimo=ril.get(parziale.size()-1).getUmidita();
			if(parziale.get(i).getNome().compareTo(parziale.get(i+1).getNome())!=0)
				
			costo=costo+ COST+ril.get(i).getUmidita();
			else
				costo=costo+ril.get(i).getUmidita();	}
			return costo+costoUltimo; //aggiungo il costo ultimo perchè nel for mi fermo prima dell'ultimo giorno 
		}
	
	
	private boolean aggiuntaValida(List<Citta> parziale,Citta citta) {
		int count=0;
		for(int i=0;i<parziale.size();i++) 
			if(citta.getNome().compareTo(parziale.get(i).getNome())==0)  //VERIFICO I 6 GIORNI MASSIMI 
				count++;
			if(count>=NUMERO_GIORNI_CITTA_MAX)
				return false;
	
		// SE E' LA PRIMA CITTA DA INSERIRE MI SBALLA L'ARRAY 
		if(parziale.size()==0)
			return true; // PRIMO GIORNO POSSO INSERIRE QUALSIASI CITTA
		
		if(parziale.size()==1 || parziale.size()==2) // DEVO ASSICURAMI CHE NON SI SPOSTI 
			return parziale.get(parziale.size()-1).equals(citta); // se la citta da inserire è uguale alla precedente
		
		if (parziale.get(parziale.size()-1).equals(citta))
			return true; 
		/*for(int i=parziale.size();i>parziale.size()-3;i--) {
			if(citta.getNome().compareTo(parziale.get(i).getNome())==0)//VERIFICO I GIORNI CONSECUTIVI 
				counts++;*/   //POSSO EVITARE DI FARE UN FOR CHE MI INCASINA
		if(parziale.get(parziale.size()-1).equals(parziale.get(parziale.size()-2)) &&parziale.get(parziale.size()-3).equals(parziale.get(parziale.size()-2)) )
		return true;
		return false;
	}
	
 public List<Citta> getTuttelocalita (){
	 return this.meteodao.localita();
 }
}
