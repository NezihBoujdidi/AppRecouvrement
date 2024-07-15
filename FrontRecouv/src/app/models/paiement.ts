import { Client } from "./client";
import { PaiementSpecifique } from "./paiement-specifique";

export class Paiement {
    paiementID: number;
  montantPaye: number;
  libelle: string;
  datePaiement: Date;
  client: Client;
  paiementsSpecifiques: PaiementSpecifique[];
  [key: string]: any;

  constructor(
    paiementID: number,
    montantPaye: number,
    libelle: string,
    datePaiement: Date,
    client: Client,
    paiementsSpecifiques: PaiementSpecifique[] = []
  ) {
    this.paiementID = paiementID; 
    this.montantPaye = montantPaye;
    this.libelle = libelle;
    this.datePaiement = datePaiement;
    this.client = client;
    this.paiementsSpecifiques = paiementsSpecifiques;
  }
}
