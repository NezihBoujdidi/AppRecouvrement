import { Component, OnInit } from '@angular/core';
import { Client } from '../models/client';
import { ClientService } from '../services/client.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-edit-profil',
  templateUrl: './edit-profil.component.html',
  styleUrls: ['./edit-profil.component.css']
})
export class EditProfilComponent implements OnInit{

  clientToEdit : Client | undefined;
  isLoading: boolean = false;
  errorMessage: string = '';

  constructor(private clientService: ClientService, private router: Router){}

  ngOnInit(): void {
    const state = history.state;
    if (state.selectedClient) {
      this.clientToEdit = state.selectedClient;
  }
}

updateClient(): void {
  if (this.clientToEdit) {
    let id=this.clientToEdit.clientId;
    this.isLoading = true;
    this.clientService.updateClient(this.clientToEdit.clientId, this.clientToEdit).subscribe({
      next: (updatedClient) => {
        this.isLoading = false;
        this.router.navigate(['./profilClient', id]); // Redirect to a success page or client profile page
      },
      error: (error) => {
        this.isLoading = false;
        this.errorMessage = 'Failed to update client. Please try again.';
        console.error(error);
      }
    });
  }
}

}
