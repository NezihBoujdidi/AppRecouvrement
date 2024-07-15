import { Facture } from "./facture";
import { Paiement } from "./paiement";

export class Client {
  clientId: number;
  nom: string;
  prenom: string;
  adresse: string;
  numTel: string;
  email: string;
  factures: Facture[];
  paiements: Paiement[];
  echues: number;      
  nonEchues: number;   

  constructor(
    clientId: number,
    nom: string,
    prenom: string,
    adresse: string,
    numTel: string,
    email: string,
    factures: Facture[] = [],
    paiements: Paiement[] = [],
    echues: number = 0,      
    nonEchues: number = 0    
  ) {
    this.clientId = clientId;
    this.nom = nom;
    this.prenom = prenom;
    this.adresse = adresse;
    this.numTel = numTel;
    this.email = email;
    this.factures = factures;
    this.paiements = paiements;
    this.echues = echues;
    this.nonEchues = nonEchues;
  }
}
