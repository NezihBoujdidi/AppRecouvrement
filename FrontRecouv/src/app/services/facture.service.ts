import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Facture } from '../models/facture';

@Injectable({
  providedIn: 'root'
})
export class FactureService {

  private baseUrl = 'http://localhost:8081/api/factures'; 

  constructor(private http: HttpClient) { }

  getAllFactures(): Observable<Facture[]> {
    return this.http.get<Facture[]>(this.baseUrl);
  }

  getFactureById(factureID: number): Observable<Facture> {
    const url = `${this.baseUrl}/${factureID}`;
    return this.http.get<Facture>(url);
  }

  createFacture(facture: Facture): Observable<Facture> {
    return this.http.post<Facture>(this.baseUrl, facture);
  }

  updateFacture(factureID: number, factureDetails: Facture): Observable<Facture> {
    const url = `${this.baseUrl}/${factureID}`;
    return this.http.put<Facture>(url, factureDetails);
  }

  deleteFacture(factureID: number): Observable<void> {
    const url = `${this.baseUrl}/${factureID}`;
    return this.http.delete<void>(url);
  }

  getAllFacturesForClient(clientId: number): Observable<Facture[]> {
    const url = `${this.baseUrl}/client/${clientId}`; 
    return this.http.get<Facture[]>(url);
  }

  uploadFactureCsv(file: FormData): Observable<any> {
    return this.http.post(`${this.baseUrl}/upload`, file);
  }
}
