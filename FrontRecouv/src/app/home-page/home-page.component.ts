import { HttpClient } from '@angular/common/http';
import { AfterViewInit, Component, OnInit } from '@angular/core';
import { ClientService } from '../services/client.service';
import { FactureService } from '../services/facture.service';
import { Client } from '../models/client';
import { Chart } from 'chart.js';
import { lastValueFrom } from 'rxjs';
import { Router } from '@angular/router';

@Component({
  selector: 'app-home-page',
  templateUrl: './home-page.component.html',
  styleUrls: ['./home-page.component.css']
})
export class HomePageComponent implements OnInit, AfterViewInit {
  clients: Client[] = [];
  totalEchues: number = 0;
  totalNonEchues: number = 0;
  dataLoaded: boolean = false;
  
  constructor(
    private clientService: ClientService,
    private factureService: FactureService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.getAllClients();
  }

  ngAfterViewInit(): void {
    if (this.dataLoaded) {
      this.renderChart();
    }
  }

  getAllClients(): void {
    this.clientService.getAllClients().subscribe(clients => {
      this.clients = clients;
      this.calculateSums();
    });
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
  
      
      console.log('Clients before sorting:', this.clients.map(client => ({
        clientId: client.clientId,
        echues: client.echues,
        nonEchues: client.nonEchues
      })));
  
      // Selection sort to sort clients by echues in descending order
      for (let i = 0; i < this.clients.length - 1; i++) {
        let maxIdx = i;
        for (let j = i + 1; j < this.clients.length; j++) {
          if (this.clients[j].echues > this.clients[maxIdx].echues) {
            maxIdx = j;
          }
        }
        // Swap
        if (maxIdx !== i) {
          const temp = this.clients[i];
          this.clients[i] = this.clients[maxIdx];
          this.clients[maxIdx] = temp;
        }
      }
  
      
      console.log('Clients after sorting:', this.clients.map(client => ({
        clientId: client.clientId,
        echues: client.echues,
        nonEchues: client.nonEchues
      })));
  
      
      this.clients = this.clients.slice(0, 5);
  
      this.dataLoaded = true;
      this.renderChart();
    }

  renderChart(): void {
    const ctx = document.getElementById('piechart') as HTMLCanvasElement;
    console.log('Canvas element:', ctx);
    new Chart(ctx, {
      type: 'pie',
      data: {
        labels: ['Non echues', 'Echues'],
        datasets: [{
          data: [this.totalNonEchues, this.totalEchues],
          backgroundColor: ['#ff6347', '#ffa07a'],
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false
      }
    });
  }

  viewDetails() : void {
    this.router.navigate(['/balanceAgee']);
  }

  viewClientProfile(client: any): void {
    this.router.navigate(['./profilClient', client.clientId], { state: { selectedClient: client } });
  }

}
