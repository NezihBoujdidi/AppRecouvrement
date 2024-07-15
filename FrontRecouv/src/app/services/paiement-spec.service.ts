import { Injectable } from '@angular/core';
import { PaiementSpecifique } from '../models/paiement-specifique';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class PaiementSpecService {
  private baseUrl = 'http://localhost:8081/api/paiementsSpecifiques';

  constructor(private http: HttpClient) { }

  getAllPaiementsSpecifiques(): Observable<PaiementSpecifique[]> {
    return this.http.get<PaiementSpecifique[]>(this.baseUrl);
  }

  getPaiementSpecifiqueById(id: number): Observable<PaiementSpecifique> {
    return this.http.get<PaiementSpecifique>(`${this.baseUrl}/${id}`);
  }

  getPaiementSpecifiqueByPaiementID(paiementID: number): Observable<PaiementSpecifique[]> {
    return this.http.get<PaiementSpecifique[]>(`${this.baseUrl}/paiement/${paiementID}`);
  }

  createPaiementSpecifique(paiementSpecifique: PaiementSpecifique): Observable<PaiementSpecifique> {
    return this.http.post<PaiementSpecifique>(this.baseUrl, paiementSpecifique);
  }

  updatePaiementSpecifique(id: number, paiementSpecifique: PaiementSpecifique): Observable<PaiementSpecifique> {
    return this.http.put<PaiementSpecifique>(`${this.baseUrl}/${id}`, paiementSpecifique);
  }

  deletePaiementSpecifique(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
