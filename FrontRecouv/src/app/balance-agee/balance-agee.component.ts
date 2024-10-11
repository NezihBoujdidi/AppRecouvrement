import { Component, OnInit } from '@angular/core';
import { ClientService } from '../services/client.service';
import { FactureService } from '../services/facture.service';
import { Client } from '../models/client';
import { Router } from '@angular/router';
import { Paiement } from '../models/paiement';
import { PaiementService } from '../services/paiement.service';

@Component({
  selector: 'app-balance-agee',
  templateUrl: './balance-agee.component.html',
  styleUrls: ['./balance-agee.component.css']
})
export class BalanceAgeeComponent implements OnInit {

  clients: Client[] = [];
  clientDetails: any[] = [];
  filteredClientDetails: any[] = [];
  sortedBy: string = ''; 
  sortDirection: number = 1;


  constructor(
    private clientService: ClientService,
    private factureService: FactureService,
    private router: Router,
    private paiementService: PaiementService
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

  /* fetchClientDetails(): void {
    this.clients.forEach(client => {
      let nonEchues = 0;
      let echues_0_30 = 0;
      let echues_31_60 = 0;
      let echues_61_90 = 0;
      let echues_plusQue_90 = 0;

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
              echues_plusQue_90 += facture.montantOuvert;
            }
          }
        });

        const totalEchues = echues_0_30 + echues_31_60 + echues_61_90 + echues_plusQue_90;
        const totalEnCours = nonEchues + totalEchues;

        this.clientDetails.push({
          clientId: client.clientId,
          referenceClient: client.referenceClient,
          nom: client.nom,
          prenom: client.prenom,
          nonEchues: nonEchues,
          echues_0_30: echues_0_30,
          echues_31_60: echues_31_60,
          echues_61_90: echues_61_90,
          echues_plusQue_90: echues_plusQue_90,
          totalEchues: totalEchues,
          totalEnCours: totalEnCours
        });
        this.filteredClientDetails=this.clientDetails;
      });
    });
  } */

    fetchClientDetails(): void {
      this.clients.forEach(client => {
        let nonEchues = 0;
        let echues_0_30 = 0;
        let echues_31_60 = 0;
        let echues_61_90 = 0;
        let echues_plusQue_90 = 0;
        let totalPaiements = 0;
  
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
                echues_plusQue_90 += facture.montantOuvert;
              }
            }
          });
  
          // Fetch client payments and calculate the total amount of payments
          this.fetchClientPaiements(client.clientId, (paiements: Paiement[]) => {
            totalPaiements = paiements.reduce((sum, paiement) => sum + paiement.montantPaye, 0);
            
            const totalEchues = echues_0_30 + echues_31_60 + echues_61_90 + echues_plusQue_90;
            const totalEnCours = nonEchues + totalEchues;
  
            this.clientDetails.push({
              clientId: client.clientId,
              referenceClient: client.referenceClient,
              nom: client.nom,
              prenom: client.prenom,
              nonEchues: nonEchues,
              echues_0_30: echues_0_30,
              echues_31_60: echues_31_60,
              echues_61_90: echues_61_90,
              echues_plusQue_90: echues_plusQue_90,
              totalEchues: totalEchues,
              totalEnCours: totalEnCours,
              totalPaiements: totalPaiements // Add totalPaiements to the client details
            });
            this.filteredClientDetails = this.clientDetails;
          });
        });
      });
    }
  
    fetchClientPaiements(clientId: number, callback: (paiements: Paiement[]) => void): void {
      this.paiementService.getPaiementByClientId(clientId).subscribe(
        (data: Paiement[]) => {
          callback(data);
        },
        (error) => {
          console.error('Error fetching paiements:', error);
          callback([]); // In case of an error, return an empty array
        }
      );
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
      }
      else if (this.sortedBy === 'referenceClient') {
        return this.sortDirection * a.referenceClient.localeCompare(b.referenceClient);
      } 
      else {
        return this.sortDirection * (a[this.sortedBy] - b[this.sortedBy]);
      }
    });
  }

  viewClientProfile(clientDetail: any): void {
    this.router.navigate(['./profilClient', clientDetail.clientId], { state: { selectedClient: clientDetail } });
  }

  onSearch(event: Event): void {
    const inputElement = event.target as HTMLInputElement;
    const value = inputElement.value.trim().toLowerCase();
    if (value !== '') {
      this.filteredClientDetails = this.clientDetails.filter(clientDetail =>
        clientDetail.nom.toLowerCase().includes(value) || clientDetail.prenom.toLowerCase().includes(value)
      );
    } else {
      this.filteredClientDetails = this.clientDetails;
    }
  }

  downloadCSV(): void {
    const csvContent = this.convertToCSV(this.filteredClientDetails);
    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
    const downloadLink = document.createElement('a');
    const url = URL.createObjectURL(blob);

    downloadLink.href = url;
    downloadLink.setAttribute('download', 'client-details.csv');
    document.body.appendChild(downloadLink);
    downloadLink.click();
    document.body.removeChild(downloadLink);
  }

  convertToCSV(data: any[]): string {
    const header = Object.keys(data[0]).join(',');
    const rows = data.map(client => Object.values(client).join(',')).join('\n');
    return `${header}\n${rows}`;
  }

}
