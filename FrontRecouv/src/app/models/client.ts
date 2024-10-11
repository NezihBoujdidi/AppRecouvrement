import { StrategieRelance } from './strategie-relance';
import { Facture } from "./facture";
import { Paiement } from "./paiement";

export class Client {
  clientId: number;
  referenceClient: string;
  nom: string;
  prenom: string;
  adresse: string;
  numTel: string;
  email: string;
  factures: Facture[];
  paiements: Paiement[];
  echues: number;      
  nonEchues: number;  
  strategieRelance: string; 
  moyenRelanceActuel : string;

  constructor(
    clientId: number,
    referenceClient: string,
    nom: string,
    prenom: string,
    adresse: string,
    numTel: string,
    email: string,
    factures: Facture[] = [],
    paiements: Paiement[] = [],
    echues: number = 0,      
    nonEchues: number = 0,
    strategieRelance: string, 
    moyenRelanceActuel : string   
  ) {
    this.referenceClient= referenceClient;
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
    this.strategieRelance= strategieRelance;
    this.moyenRelanceActuel= moyenRelanceActuel
  }
}
