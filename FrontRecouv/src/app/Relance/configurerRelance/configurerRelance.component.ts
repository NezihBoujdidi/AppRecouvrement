import { ChangeDetectorRef, Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { RelanceService } from '../../services/relance.service';
import { StrategieRelance } from '../../models/strategie-relance';
import { MoyenRelance } from '../../models/moyen-relance';
import { Client } from '../../models/client';
import { ClientService } from '../../services/client.service';
import { FactureService } from '../../services/facture.service';
import { Router } from '@angular/router';
import { lastValueFrom } from 'rxjs';
import { Phase } from '../../models/phase';

@Component({
  selector: 'app-strategie-relance',
  templateUrl: './configurerRelance.component.html',
  styleUrls: ['./configurerRelance.component.css']
})
export class ConfigurerRelanceComponent implements OnInit{
  @ViewChild('createStrategiePopUpContainer', { static: false }) createStrategiePopUpContainer!: ElementRef;
  isCreatingStrategie = false;
  isEditingStrategie = false;
  phases = [1, 2, 3, 4, 5];
  delaiPhases: number[] = [0, 0, 0, 0, 0];
  selectedMoyens: (MoyenRelance | null)[] = [null, null, null, null];  // Allow null values
  strategieNom: string = '';
  clients: Client[] = [];
  totalEchues: number = 0;
  totalNonEchues: number = 0;
  strategiesRelance: StrategieRelance[] = [];
  editedStrategieID: number=0;

  showPopup: boolean = false;
  popupMessage: string = '';
  isSuccess: boolean = true;

  moyensRelance = Object.values(MoyenRelance);  // Populate the options from the enum

  constructor(private clientService: ClientService,
              private factureService: FactureService,
              private relanceService: RelanceService, 
              private router: Router,
              private cdr: ChangeDetectorRef) {}

  ngOnInit(): void {
    this.getAllClients();
    this.getAllStrategies(); // Fetch available strategies on init
  }

  addStrategie(): void {
    this.isCreatingStrategie = true;
    this.isEditingStrategie = false;
    this.strategieNom = '';
    this.delaiPhases = [0, 0, 0, 0, 0];
    this.selectedMoyens = [null, null, null, null];
  }


    saveStrategie(): void {
      const phasesList: Phase[] = [];
  
      for (let i = 0; i < this.phases.length; i++) {
        const delai = this.delaiPhases[i];
        const moyen = this.selectedMoyens[i];
  
        if (delai === 0) {
          this.popupMessage = `Vous devez choisir un délai après échéance pour la phase ${i + 1}`;
          this.isSuccess = false;
          this.showPopup = true;
          this.scrollToPopup();
          return;
        } else if (!moyen) {
          this.popupMessage = `Vous devez choisir un moyen de relance pour la phase ${i + 1}`;
          this.isSuccess = false;
          this.showPopup = true;
          this.scrollToPopup();
          return;
        } else {
          const phase = new Phase(0, delai, moyen);  // Create a new Phase object
          phasesList.push(phase);  // Add the phase to the list
        }
      }
  
      if (!this.strategieNom) {
        this.popupMessage = `Vous devez choisir un nom pour la stratégie`;
        this.isSuccess = false;
        this.showPopup = true;
        this.scrollToPopup();
        return;
      }

      if (this.isEditingStrategie && this.editedStrategieID !== null) {
    const updatedStrategie = new StrategieRelance(this.editedStrategieID, this.strategieNom, phasesList);
    // Update the existing strategy
    this.relanceService.updateStrategieRelance(this.editedStrategieID, updatedStrategie).subscribe(response => {
      console.log('StrategieRelance updated successfully:', response);
      this.getAllStrategies();
      this.isEditingStrategie = false;
      this.editedStrategieID = 0;
    }, error => {
      console.error('Error updating StrategieRelance:', error);
      this.popupMessage = 'Erreur lors de la mise à jour de la stratégie de relance';
      this.isSuccess = false;
      this.showPopup = true;
      this.scrollToPopup();
    });

  
      } else {
  
      const newStrategie = new StrategieRelance(0, this.strategieNom, phasesList);
      console.log("Saved strategy: ", newStrategie);
  
      this.relanceService.createStrategieRelance(newStrategie).subscribe(response => {
        console.log('StrategieRelance created successfully:', response);
        this.getAllStrategies();
        this.isCreatingStrategie = false;
      }, error => {
        console.error('Error creating StrategieRelance:', error);
        this.popupMessage = 'Erreur lors de la création de la stratégie de relance';
        this.isSuccess = false;
        this.showPopup = true;
        this.scrollToPopup();
      });
    }
    }
  
  

  getAllClients(): void {
    this.clientService.getAllClients().subscribe(clients => {
      this.clients = clients;
      this.calculateSums();
    });
  }

  getAllStrategies(): void {
    this.relanceService.getAllStrategieRelances().subscribe(strategies => {
      this.strategiesRelance = strategies;
      console.log("hneeeeee: ", this.strategiesRelance);
    });
  }

  ModifierStrategie(strategie: StrategieRelance): void{
    this.isEditingStrategie = true;
    this.isCreatingStrategie = false;

    this.editedStrategieID = strategie.id; 
    this.strategieNom = strategie.nom;
    this.delaiPhases = strategie.phases.map(phase => phase.delai);
    this.selectedMoyens = strategie.phases.map(phase => phase.moyenRelance);

  }

  SupprimerStrategie(strategie: StrategieRelance): void {
    if (confirm(`Voulez vous vraiment supprimer "${strategie.nom}"?`)) {
      this.relanceService.deleteStrategieRelance(strategie.id).subscribe(() => {
        console.log(`StrategieRelance ${strategie.nom} deleted successfully`);
        this.getAllStrategies(); // Refresh the list after deletion
      }, error => {
        console.error('Error deleting StrategieRelance:', error);
        this.popupMessage = 'Erreur lors de la suppression de la stratégie de relance';
        this.isSuccess = false;
        this.showPopup = true;
        this.scrollToPopup();
      });
    }
  }

  async calculateSums(): Promise<void> {
    const currentDate = new Date();

    for (const client of this.clients) {
      const factures = await lastValueFrom(this.factureService.getAllFacturesForClient(client.clientId));
      if (factures) {
        let echues = 0;
        let nonEchues = 0;

        factures.forEach(facture => {
          const dueDate = new Date(facture.dateEcheance);
          if (dueDate < currentDate) {
            echues += facture.montantOuvert;
          } else {
            nonEchues += facture.montantOuvert;
          }
        });

        client.echues = echues;
        client.nonEchues = nonEchues;

        this.totalEchues += echues;
        this.totalNonEchues += nonEchues;
      }
    }
  }

  onStrategieRelanceChange(client: Client): void {
    const updatedClient = { ...client, strategieRelance: client.strategieRelance };

    this.clientService.updateClient(client.clientId, updatedClient).subscribe(response => {
      console.log(`Client ${client.nom} updated successfully with strategy ${client.strategieRelance}`);
    }, error => {
      console.error('Error updating client:', error);
    });
  }

  scrollToPopup() {
    if (this.createStrategiePopUpContainer && this.createStrategiePopUpContainer.nativeElement) {
      console.log('Scrolling to popup:', this.createStrategiePopUpContainer.nativeElement);
      setTimeout(() => {
        this.createStrategiePopUpContainer.nativeElement.scrollIntoView({ behavior: 'smooth', block: 'start' });
      });
    } else {
      console.warn('Popup container not found.');
    }
  }

  closePopup() {
    this.showPopup = false;
  }

  viewClientProfile(client: any): void {
    this.router.navigate(['./profilClient', client.clientId], { state: { selectedClient: client } });
  }
}
