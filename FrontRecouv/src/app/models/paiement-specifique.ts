import { Facture } from "./facture";
import { Paiement } from "./paiement";

export class PaiementSpecifique {
    idPaiementSp: number;
  montantPaye: number;
  facture: Facture;
  paiement: Paiement;

  constructor(
    idPaiementSp: number,
    montantPaye: number,
    facture: Facture,
    paiement: Paiement
  ) {
    this.idPaiementSp = idPaiementSp;
    this.montantPaye = montantPaye;
    this.facture = facture;
    this.paiement = paiement;
  }
}
