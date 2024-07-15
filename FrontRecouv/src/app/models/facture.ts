import { Client } from "./client";

export class Facture {
    factureID: number;
  libelle: string;
  dateFacture: Date;
  dateEcheance: Date;
  montantNominal: number;
  montantOuvert: number;
  client: Client;
  [key: string]: any;

  constructor(
    factureID: number,
    libelle: string,
    dateFacture: Date,
    dateEcheance: Date,
    montantNominal: number,
    montantOuvert: number,
    client: Client
  ) {
    this.factureID = factureID;
    this.libelle = libelle;
    this.dateFacture = dateFacture;
    this.dateEcheance = dateEcheance;
    this.montantNominal = montantNominal;
    this.montantOuvert = montantOuvert;
    this.client = client;
  }
}
