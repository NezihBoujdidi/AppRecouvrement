import { MoyenRelance } from "./moyen-relance";

export class Phase {
    id: number;
  delai: number;
  moyenRelance: MoyenRelance;

  constructor(id: number, delai: number, moyenRelance: MoyenRelance) {
    this.id = id;
    this.delai = delai;
    this.moyenRelance = moyenRelance;
  }
}


