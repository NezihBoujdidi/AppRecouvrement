import { Component, OnInit } from '@angular/core';
import { Paiement } from '../models/paiement';
import { ActivatedRoute } from '@angular/router';
import { PaiementSpecifique } from '../models/paiement-specifique';
import { PaiementSpecService } from '../services/paiement-spec.service';

@Component({
  selector: 'app-details-paiement',
  templateUrl: './details-paiement.component.html',
  styleUrls: ['./details-paiement.component.css']
})
export class DetailsPaiementComponent implements OnInit {
  paiement: Paiement | undefined;
  paiementSpecifiques: PaiementSpecifique[] = [];

  constructor(private route: ActivatedRoute, private paiementSpecService: PaiementSpecService) { }

  ngOnInit(): void {
    const state = history.state;
    if (state.selectedPaiement) {
      this.paiement = state.selectedPaiement;
      if(this.paiement!=null){
      this.loadPaiementSpecifiques(this.paiement.paiementID);}
    }
  }

  loadPaiementSpecifiques(paiementID: number): void {
    this.paiementSpecService.getPaiementSpecifiqueByPaiementID(paiementID).subscribe(data => {
      this.paiementSpecifiques = data;
    });
  }
}
