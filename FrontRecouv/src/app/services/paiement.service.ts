import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Paiement } from '../models/paiement';

@Injectable({
  providedIn: 'root'
})
export class PaiementService {

  private baseUrl = 'http://localhost:8081/api/paiements'; 

  constructor(private http: HttpClient) { }

  getAllPaiements(): Observable<Paiement[]> {
    return this.http.get<Paiement[]>(this.baseUrl);
  }

  getPaiementById(paiementID: number): Observable<Paiement> {
    const url = `${this.baseUrl}/${paiementID}`;
    return this.http.get<Paiement>(url);
  }

  createPaiement(paiement: Paiement): Observable<Paiement> {
    return this.http.post<Paiement>(this.baseUrl, paiement);
  }

  updatePaiement(paiementID: number, paiementDetails: Paiement): Observable<Paiement> {
    const url = `${this.baseUrl}/${paiementID}`;
    return this.http.put<Paiement>(url, paiementDetails);
  }

  deletePaiement(paiementID: number): Observable<void> {
    const url = `${this.baseUrl}/${paiementID}`;
    return this.http.delete<void>(url);
  }

  getPaiementByClientId(clientId: number): Observable<Paiement[]> {
    const url = `${this.baseUrl}/client/${clientId}`;
    return this.http.get<Paiement[]>(url);
  }
}
