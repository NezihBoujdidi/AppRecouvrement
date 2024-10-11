import { Phase } from "./phase";

export class StrategieRelance {
  id: number;
  nom: string;
  phases: Phase[];

  constructor(id: number, nom: string, phases: Phase[] = []) {
    this.id = id;
    this.nom = nom;
    this.phases = phases;
  }
  }