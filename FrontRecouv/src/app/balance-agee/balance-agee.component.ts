import { Component, OnInit } from '@angular/core';
import { ClientService } from '../services/client.service';
import { FactureService } from '../services/facture.service';
import { Client } from '../models/client';
import { Router } from '@angular/router';

@Component({
  selector: 'app-balance-agee',
  templateUrl: './balance-agee.component.html',
  styleUrls: ['./balance-agee.component.css']
})
export class BalanceAgeeComponent implements OnInit {

  clients: Client[] = [];
  clientDetails: any[] = [];
  sortedBy: string = ''; 
  sortDirection: number = 1;

  constructor(
    private clientService: ClientService,
    private factureService: FactureService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.getAllClients();
  }

  getAllClients(): void {
    this.clientService.getAllClients().subscribe(clients => {
      this.clients = clients;
      this.fetchClientDetails();
    });
  }

  fetchClientDetails(): void {
    this.clients.forEach(client => {
      let nonEchues = 0;
      let echues_0_30 = 0;
      let echues_31_60 = 0;
      let echues_61_90 = 0;
      let echues_above_90 = 0;

      this.factureService.getAllFacturesForClient(client.clientId).subscribe(factures => {
        factures.forEach(facture => {
          const currentDate = new Date();
          const dueDate = new Date(facture.dateEcheance);

          if (dueDate > currentDate) {
            nonEchues += facture.montantOuvert;
          } else {
            const diffDays = this.getDifferenceInDays(currentDate, dueDate);
            if (diffDays <= 30) {
              echues_0_30 += facture.montantOuvert;
            } else if (diffDays <= 60) {
              echues_31_60 += facture.montantOuvert;
            } else if (diffDays <= 90) {
              echues_61_90 += facture.montantOuvert;
            } else {
              echues_above_90 += facture.montantOuvert;
            }
          }
        });

        const totalEchues = echues_0_30 + echues_31_60 + echues_61_90 + echues_above_90;
        const totalMontantOuvert = nonEchues + totalEchues;

        this.clientDetails.push({
          clientId: client.clientId,
          nom: client.nom,
          nonEchues: nonEchues,
          echues_0_30: echues_0_30,
          echues_31_60: echues_31_60,
          echues_61_90: echues_61_90,
          echues_above_90: echues_above_90,
          totalEchues: totalEchues,
          totalMontantOuvert: totalMontantOuvert
        });
      });
    });
  }

  private getDifferenceInDays(date1: Date, date2: Date): number {
    const diffTime = Math.abs(date2.getTime() - date1.getTime());
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
    return diffDays;
  }

  sortBy(column: string): void {
    if (this.sortedBy === column) {
      this.sortDirection = this.sortDirection === 1 ? -1 : 1;
    } else {
      this.sortedBy = column;
      this.sortDirection = 1;
    }

    // Perform sorting
    this.clientDetails.sort((a, b) => {
      if (this.sortedBy === 'nom') {
        return this.sortDirection * a.nom.localeCompare(b.nom);
      } else {
        return this.sortDirection * (a[this.sortedBy] - b[this.sortedBy]);
      }
    });
  }

  viewClientProfile(clientDetail: any): void {
    this.router.navigate(['./profilClient', clientDetail.clientId], { state: { selectedClient: clientDetail } });
  }
}
