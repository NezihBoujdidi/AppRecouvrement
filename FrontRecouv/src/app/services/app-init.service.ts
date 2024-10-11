import { Injectable } from '@angular/core';
import { ClientService } from './client.service';
import { Client } from '../models/client';
import { MoyenRelance } from '../models/moyen-relance';
import { RelanceService } from './relance.service';

@Injectable({
  providedIn: 'root'
})
export class AppInitService {

  constructor(
    private clientService: ClientService,
    private relanceService: RelanceService // Inject RelanceService
  ) {}

  // Initialize the app with clients' relance methods
  initClientsWithRelance(): void {
    // Fetch all clients
    this.clientService.getAllClients().subscribe((clients: Client[]) => {
      // Iterate over all clients
      clients.forEach((client: Client) => {
        // Fetch MoyenRelance for each client
        this.relanceService.getMoyenRelanceForClient(client.clientId).subscribe(
          (moyenRelance: MoyenRelance) => {
            // Assign the fetched MoyenRelance type (niveauRelance) to the client
            client.moyenRelanceActuel = moyenRelance; 
            console.log(`Updated client ${client.nom} with relance method: ${client.moyenRelanceActuel}`);
          },
          (error) => {
            console.error(`Error fetching relance for client ${client.nom}:`, error);
          }
        );
      });
    });
  }

  
}
