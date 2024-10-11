import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { Client } from '../models/client';
import { ActivatedRoute, Router } from '@angular/router';
import { ClientService } from '../services/client.service';
import { FactureService } from '../services/facture.service';
import { Facture } from '../models/facture';
import { Paiement } from '../models/paiement';
import { PaiementService } from '../services/paiement.service';

@Component({
  selector: 'app-profil-client',
  templateUrl: './profil-client.component.html',
  styleUrls: ['./profil-client.component.css']
})
export class ProfilClientComponent implements OnInit {
  client: Client | undefined;
  totalEchues: number = 0;
  totalNonEchues: number = 0;
  paiements: Paiement[] = [];
  latestPaiement: Paiement | undefined;
  daysLeft: number | null = null;
  factures: Facture[] = [];

  
  sortedByFactures: string = ''; 
  sortDirectionFactures: number = 1;

  sortedByPaiements: string = ''; 
  sortDirectionPaiements: number = 1;

  constructor(
    private route: ActivatedRoute,
    private clientService: ClientService,
    private factureService: FactureService,
    private paiementService: PaiementService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const clientId = params.get('id');
      console.log('Client ID:', clientId);

      if (clientId) {
        const parsedClientId = parseInt(clientId, 10);
        const state = history.state;
        if (state.selectedClient) {
          this.client = state.selectedClient;
        } 
        
        this.fetchClientDetails(parsedClientId);
        this.calculateSums(parsedClientId);
        this.fetchClientPaiements(parsedClientId);
        this.fetchClientFactures(parsedClientId);
      }
    });
  }

  fetchClientDetails(clientId: number): void {
    this.clientService.getClientById(clientId).subscribe(
      (data: Client) => {
        this.client = data;
        console.log("this is him: ", this.client);
      },
      (error) => {
        console.error('Error fetching client:', error);
      }
    );
  }

  calculateSums(clientId: number): void {
    this.factureService.getAllFacturesForClient(clientId).subscribe(
      (factures: Facture[]) => {
        const currentDate = new Date();
        let nearestDueDate: Date | null = null;
  
        factures.forEach(facture => {
          const dueDate = new Date(facture.dateEcheance);
  
          if (dueDate < currentDate) {
            this.totalEchues += facture.montantOuvert;
          } else {
            this.totalNonEchues += facture.montantOuvert;
            if (nearestDueDate === null || dueDate < nearestDueDate) {
              nearestDueDate = dueDate;
            }
          }
        });
  
        if (nearestDueDate !== null) {
          const diffTime = (nearestDueDate as Date).getTime() - currentDate.getTime();
          this.daysLeft = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
        } else {
          this.daysLeft = null;
        }
  
        console.log('Total Echues:', this.totalEchues);
        console.log('Total Non Echues:', this.totalNonEchues);
        console.log('Days left until the nearest due date:', this.daysLeft);
      },
      (error) => {
        console.error('Error fetching factures:', error);
      }
    );
  }

  fetchClientPaiements(clientId: number): void {
    this.paiementService.getPaiementByClientId(clientId).subscribe(
      (data: Paiement[]) => {
        this.paiements = data;
        if (this.paiements.length > 0) {
          this.latestPaiement = this.paiements.reduce((latest, paiement) => 
            new Date(paiement.datePaiement) > new Date(latest.datePaiement) ? paiement : latest
          );
        }
        console.log('Paiements:', this.paiements);
        console.log('Latest Paiement:', this.latestPaiement);
      },
      (error) => {
        console.error('Error fetching paiements:', error);
      }
    );
  }

  fetchClientFactures(clientId: number): void {
    this.factureService.getAllFacturesForClient(clientId).subscribe(
      (factures: Facture[]) => {
        this.factures = factures;
        console.log('Factures:', this.factures); 
      },
      (error) => {
        console.error('Error fetching factures:', error);
      }
    );
  }

  scrollToPaiementDetails(): void {
    const section = document.getElementById('paiement-table');
    if (section) {
      section.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }
  }

      sortByFactures(column: string): void {
        if (this.sortedByFactures === column) {
          this.sortDirectionFactures = this.sortDirectionFactures === 1 ? -1 : 1;
        } else {
          this.sortedByFactures = column;
          this.sortDirectionFactures = 1;
        }
    
        // Perform sorting
        this.factures.sort((a, b) => {
          let aValue, bValue;
          switch (column) {
            case 'referenceFacture':
              aValue = a.referenceFacture;
              bValue = b.referenceFacture;
              break;
            case 'libelle':
              aValue = a.libelle;
              bValue = b.libelle;
              break;
            case 'dateFacture':
              aValue = new Date(a.dateFacture).getTime();
              bValue = new Date(b.dateFacture).getTime();
              break;
            case 'montantNominal':
              aValue = a.montantNominal;
              bValue = b.montantNominal;
              break;
            case 'montantOuvert':
              aValue = a.montantOuvert;
              bValue = b.montantOuvert;
              break;
            case 'dateEcheance':
              aValue = new Date(a.dateEcheance).getTime();
              bValue = new Date(b.dateEcheance).getTime();
              break;
            default:
              aValue = 0;
              bValue = 0;
              break;
          }
    
          if (aValue < bValue) {
            return -1 * this.sortDirectionFactures;
          } else if (aValue > bValue) {
            return 1 * this.sortDirectionFactures;
          } else {
            return 0;
          }
        });
      }
  
    sortByPaiements(column: string): void {
      if (this.sortedByPaiements === column) {
        this.sortDirectionPaiements = this.sortDirectionPaiements === 1 ? -1 : 1;
      } else {
        this.sortedByPaiements = column;
        this.sortDirectionPaiements = 1;
      }
    
      
      this.paiements.sort((a, b) => {
        if (this.sortedByPaiements === 'paiementID' || this.sortedByPaiements === 'montantPaye') {
          return this.sortDirectionPaiements * ((a as any)[this.sortedByPaiements] - (b as any)[this.sortedByPaiements]);
        } else if (this.sortedByPaiements === 'datePaiement') {
          return this.sortDirectionPaiements * (new Date(a[this.sortedByPaiements]).getTime() - new Date(b[this.sortedByPaiements]).getTime());
        } else {
          return 0; 
        }
      });
    }
    onPaiementRowClick(paiement: Paiement): void {
      this.router.navigate(['/payment-details'], { state: { selectedPaiement: paiement } });
    }

    routeToEditProfil() {
      this.router.navigate(['/editProfil'], { state: { selectedClient: this.client } });
    }
}
