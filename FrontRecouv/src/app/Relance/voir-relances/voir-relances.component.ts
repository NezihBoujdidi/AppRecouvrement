import { RelanceService } from './../../services/relance.service';
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { lastValueFrom } from 'rxjs';
import { Client } from 'src/app/models/client';
import { MoyenRelance } from 'src/app/models/moyen-relance';
import { Phase } from 'src/app/models/phase';
import { StrategieRelance } from 'src/app/models/strategie-relance';
import { ClientService } from 'src/app/services/client.service';
import { FactureService } from 'src/app/services/facture.service';

@Component({
  selector: 'app-voir-relances',
  templateUrl: './voir-relances.component.html',
  styleUrls: ['./voir-relances.component.css']
})
export class VoirRelancesComponent implements OnInit{
  clientsRelance: Client[] = [];
  totalEchues: number = 0;
  totalNonEchues: number = 0;
  selectedClient: Client | null = null;
  dataLoaded: boolean = false;
  filteredClients: Client[] = [];
  searchTerm: string = '';
  totalAmount: number = 0;
  displayedClients: Client[] = [];
  currentPage: number = 1;
  clientsPerPage: number = 5; 
  totalPages: number = 0;
  activeTab: string = 'factures'; 
  email = {
    toEmail: '',
    subject: '',
    content: ''
  };
  selectedFile: File | null = null;
  selectedClientStrategie: StrategieRelance | null=null ;
  selectedMoyen: MoyenRelance | null = null;
  
  constructor(private clientService: ClientService,
    private factureService: FactureService, 
    private relanceService: RelanceService, 
    private router: Router) {}

  ngOnInit(): void {
    this.getAllClients();
    setTimeout(() => {
      this.dataLoaded = true;
    }, 2000); // Simulates a 2-second load time
  }
  

  getAllClients(): void {
    this.clientService.getAllClients().subscribe(async clients => {
      this.clientsRelance = clients;
      await this.calculateSums();
      this.clientsRelance = this.clientsRelance.filter(client => client.echues > 0);
      this.dataLoaded = true;
      this.totalPages = Math.ceil(this.clientsRelance.length / this.clientsPerPage);
      this.currentPage = 1;
      this.updateDisplayedClients();
      if (this.clientsRelance.length >= 1 && this.dataLoaded) {
        this.selectedClient = this.clientsRelance[0];
        this.totalAmount = this.selectedClient.echues+ this.selectedClient.nonEchues;
      }
    });
  }

  async calculateSums(): Promise<void> {
    const currentDate = new Date();

    for (const client of this.clientsRelance) {
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

  onSearch(event: Event): void {
    const inputElement = event.target as HTMLInputElement;
    this.searchTerm = inputElement.value.trim().toLowerCase(); // Update searchTerm directly
    if (this.searchTerm !== '') {
        this.filteredClients = this.clientsRelance.filter(client =>
            client.nom.toLowerCase().includes(this.searchTerm) || client.prenom.toLowerCase().includes(this.searchTerm)
        );
    } else {
        this.filteredClients = [];
    }
  }

  updateDisplayedClients(): void {
    // Logic to slice the clients array based on currentPage
    const startIndex = (this.currentPage - 1) * this.clientsPerPage;
    const endIndex = startIndex + this.clientsPerPage;
    this.displayedClients = this.clientsRelance.slice(startIndex, endIndex);
  }

  goToNextPage(): void {
    if (this.currentPage < this.totalPages) {
      this.currentPage++;
      this.updateDisplayedClients();
    }
  }

  goToPreviousPage(): void {
    if (this.currentPage > 1) {
      this.currentPage--;
      this.updateDisplayedClients();
    }
  }

  /* calculateNiveauRelance(client: Client, oldestDueDate: Date): void {
    const currentDate = new Date();
    const delay = Math.floor((currentDate.getTime() - oldestDueDate.getTime()) / (1000 * 60 * 60 * 24)); // Delay in days
    const strategieRelance = client.strategieRelance as unknown as StrategieRelance;

    if (strategieRelance && strategieRelance.phases) {
      let matchedPhase: Phase | null = null;

      for (const phase of strategieRelance.phases) {
        if (delay >= phase.delai) {
          matchedPhase = phase; // Find the most advanced phase
        } else {
          break; // Phases are assumed to be ordered, so exit loop when the delay is less than the phase's delai
        }
      }

      if (matchedPhase) {
        this.moyen = matchedPhase.moyenRelance;
      } else {
        this.moyen = null;
      }
    }
  } */


  clearFilteredClients(): void {
    this.filteredClients = [];
  }

  viewClientDetails(client: Client): void {
    this.relanceService.getStrategieRelanceByClientId(client.clientId).subscribe({
      next: (strategieRelance: StrategieRelance) => {
        this.selectedClientStrategie = strategieRelance;
      },
      error: (err) => {
        console.error('Error fetching StrategieRelance:', err);
      }
    });
    this.selectedClient = client; 
    this.relanceService.getMoyenRelanceForClient(client.clientId).subscribe({next: (moyenRelance: MoyenRelance) => {
      this.selectedMoyen = moyenRelance;
    },
    error: (err) => {
      console.error('Error fetching StrategieRelance:', err);
    }
  });
    const clientIndex = this.clientsRelance.findIndex(c => c.clientId === client.clientId); 
    if (clientIndex !== -1) {
        const pageNumber = Math.floor(clientIndex / this.clientsPerPage) + 1;
        this.currentPage = pageNumber;
        this.updateDisplayedClients();
      }
    this.searchTerm = '';
}

setActiveTab(tab: string): void {
  this.activeTab = tab;
}

onMoyenChange(event: Event): void {
  const selectElement = event.target as HTMLSelectElement;
  this.selectedMoyen = JSON.parse(selectElement.value); // Assuming 'phase' is serialized properly
}

  navigateToConfigurerRelance(): void {
    this.router.navigate(['/configurerRelance']);
  }

  onFileChange(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.selectedFile = input.files[0];
    }
  }
  

  // Method to send the email
  sendEmail() {
    // Create a FormData object to include the file if necessary
    const formData = new FormData();
    formData.append('toEmail', this.email.toEmail);
    formData.append('subject', this.email.subject);
    formData.append('content', this.email.content);
    if (this.selectedFile) {
      formData.append('attachment', this.selectedFile, this.selectedFile.name);
    }
  }
}
