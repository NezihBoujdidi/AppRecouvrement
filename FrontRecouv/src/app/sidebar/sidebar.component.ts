import { ChangeDetectorRef, Component, ElementRef, Input, OnInit, ViewChild } from '@angular/core';
import { Client } from '../models/client';
import { ClientService } from '../services/client.service';
import { HttpClient } from '@angular/common/http';
import { FactureService } from '../services/facture.service';
import { PaiementService } from '../services/paiement.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.css']
})
export class SidebarComponent implements OnInit {
  @Input() isCollapsed: boolean = false;
  clients: Client[] = []; 
  filteredClients: Client[] = [];
  isProfileMenuOpen: boolean = false;
  /* selectedClientFile: File | null = null;
  selectedFactureFile: File | null = null;
  selectedPaiementFile: File | null = null;
  popupMessage: string = '';
  isSuccess: boolean = true; */

  @ViewChild('uploadResultPopUpContainer', { static: false }) uploadResultPopUpContainer!: ElementRef;

  constructor(private clientService: ClientService,
              private router: Router,
              private http: HttpClient,
              private factureService: FactureService,
              private paiementService: PaiementService,
              private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
      this.fetchAllClients();
  }


    onSearch(event: Event): void {
        const inputElement = event.target as HTMLInputElement;
        const value = inputElement.value.trim().toLowerCase();
        if (value !== '') {
            this.filteredClients = this.clients.filter(client =>
                client.nom.toLowerCase().includes(value) || client.prenom.toLowerCase().includes(value)
            );
        } else {
            this.filteredClients = [];
        }
      }

  fetchAllClients(): void {
      this.clientService.getAllClients().subscribe(
          (clients: Client[]) => {
              this.clients = clients;
          },
          (error) => {
              console.error('Error fetching clients:', error);
          }
      );
  }

  clearFilteredClients(): void {
    this.filteredClients = [];
  }

  toggleProfileMenu(): void {
    this.isProfileMenuOpen = !this.isProfileMenuOpen;
  }

  goToProfile(): void {
    // Navigate to profile page
    this.router.navigate(['/mon-profil']);
  }

  logout(): void {
    // Logic to log out the user
    console.log('User logged out');
  }

  /* onClientFileChanged(event: any): void {
    this.selectedClientFile = event.target.files[0];
    if (this.selectedClientFile) {
        this.onUploadClient();
    }
}

    onUploadClient(): void {
      if (this.selectedClientFile) {
        const formData = new FormData();
        formData.append('file', this.selectedClientFile);
        this.clientService.uploadClientCsv(formData).subscribe(response => {
          if (response && typeof response === 'object' && 'message' in response) {
            this.popupMessage = 'Clients importés avec succées!';
            if (response.existingClients && Array.isArray(response.existingClients) && response.existingClients.length > 0) {
              const existingClientNames = response.existingClients.map((client: Client) => `${client.nom} ${client.prenom}`).join(', ');
              this.popupMessage +=`<br>Les clients suivants existent déjà : ${existingClientNames}`;
            }
          } 
          this.isSuccess = true;
          this.showPopup = true;
          setTimeout(() => {
            this.cdr.detectChanges(); // Ensure Angular detects the change
            this.scrollToPopup();
          }, 0);
          this.fetchAllClients();
        }, error => {
          console.error('File upload error:', error);
          this.popupMessage = 'Erreur lors de l\'importation des clients :' + this.getErrorMessage(error);
          console.log('Popup Message Error:', this.popupMessage); // Debugging
          this.isSuccess = false;
          this.showPopup = true;
          setTimeout(() => {
            this.cdr.detectChanges(); // Ensure Angular detects the change
            this.scrollToPopup();
          }, 0);
        });
      }
    }
  
    getErrorMessage(error: any): string {
      if (error && error.error) {
        try {
          const parsedError = JSON.parse(error.error);
          return parsedError.message || error.error;
        } catch (e) {
          return error.error.message || error.message || 'An error occurred while uploading clients.';
        }
      }
      return 'An error occurred while uploading clients.';
    }

  onFactureFileChanged(event: any): void {
    this.selectedFactureFile = event.target.files[0];
    if (this.selectedFactureFile) {
        this.onUploadFacture();
    }
  }

      onUploadFacture(): void {
        if (this.selectedFactureFile) {
          const formData = new FormData();
          formData.append('file', this.selectedFactureFile);
      
          this.factureService.uploadFactureCsv(formData).subscribe(response => {
            console.log('Response received:', response); // Log the entire response object
      
            // Check if response contains a 'message' and 'savedFactures'
            if (response && typeof response === 'object' && 'message' in response && response.savedFactures) {
              this.popupMessage = `${response.savedFactures} Factures importées avec succès!`;
              this.isSuccess = true;
            } else {
              this.popupMessage = 'Unexpected response format';
              this.isSuccess = false;
            }
      
            // Check if response contains 'missingClients'
            if (response.missingClients && Array.isArray(response.missingClients) && response.missingClients.length > 0) {
              console.log('Missing client IDs:', response.missingClients); // Log missing client IDs
              const missingClientRefs = response.missingClients.map((ref: string) => `${ref}`).join(', ');
              this.popupMessage += `<br>Les IDs des clients suivants n'existent pas : ${missingClientRefs}<br>Factures rejetées!`;
            } else {
              console.log('No missing client IDs or array is empty');
            }
      
            this.showPopup = true;
            setTimeout(() => {
              this.cdr.detectChanges(); // Ensure Angular detects the change
              this.scrollToPopup();
            }, 0);
          }, error => {
            console.error('Facture upload error:', error);
            this.popupMessage = 'Erreur lors de l\'importation des factures : ' + this.getErrorMessage(error);
            this.isSuccess = false;
            this.showPopup = true;
            setTimeout(() => {
              this.cdr.detectChanges(); // Ensure Angular detects the change
              this.scrollToPopup();
            }, 0);
          });
        }
      }
      
      onPaiementFileChanged(event: any): void {
        this.selectedPaiementFile = event.target.files[0];
        if (this.selectedPaiementFile) {
            this.onUploadPaiements();
        }
    }
      
      onUploadPaiements(): void {
        if (this.selectedPaiementFile) {
          const formData = new FormData();
          formData.append('file', this.selectedPaiementFile);
          this.paiementService.uploadPaiementCsv(formData).subscribe(response => {
            if (response && typeof response === 'object' && 'message' in response ) {
              if ((response.savedPaiements > 0)){
              this.popupMessage = 'Paiements importés avec succès!';}
              
              if (response.missingClients && Array.isArray(response.missingClients) && response.missingClients.length > 0) {
                const missingClientRefs = response.missingClients.join(', ');
                this.popupMessage += `<br>Les clients suivants n\'existent pas : ${missingClientRefs}<br>Paiements pour ces clients rejetés!`;
              }
              
              if (response.missingFactures && Array.isArray(response.missingFactures) && response.missingFactures.length > 0) {
                const missingFactureRefs = response.missingFactures.join(', ');
                this.popupMessage += `<br>Les factures suivantes n\'existent pas ou n'appartiennent pas aux clients spécifiés : ${missingFactureRefs}<br>Paiements pour ces factures rejetés!`;
              }
            } 
            this.isSuccess = true;
            this.showPopup = true;
            setTimeout(() => {
              this.cdr.detectChanges(); // Ensure Angular detects the change
              this.scrollToPopup();
            }, 0);
          }, error => {
            console.error('File upload error:', error);
            this.popupMessage = 'Erreur lors de l\'importation des paiements : ' + this.getErrorMessage(error);
            console.log('Popup Message Error:', this.popupMessage); // Debugging
            this.isSuccess = false;
            this.showPopup = true;
            setTimeout(() => {
              this.cdr.detectChanges(); // Ensure Angular detects the change
              this.scrollToPopup();
            }, 0);
          });
        }
      }

    scrollToPopup() {
      if (this.uploadResultPopUpContainer && this.uploadResultPopUpContainer.nativeElement) {
        console.log('Scrolling to popup:', this.uploadResultPopUpContainer.nativeElement);
        setTimeout(() => {
          this.uploadResultPopUpContainer.nativeElement.scrollIntoView({ behavior: 'smooth', block: 'start' });
        });
      } else {
        console.warn('Popup container not found.');
      }
    }

  closePopup() {
    this.showPopup = false;
  } */
}