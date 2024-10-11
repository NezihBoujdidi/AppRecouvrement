import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { StrategieRelance } from '../models/strategie-relance';
import { MoyenRelance } from '../models/moyen-relance';


@Injectable({
  providedIn: 'root'
})
export class RelanceService {
  private baseUrl = 'http://localhost:8081/api/strategies'; 

  constructor(private http: HttpClient) { }

  
  createStrategieRelance(strategieRelance: StrategieRelance): Observable<StrategieRelance> {
    return this.http.post<StrategieRelance>(this.baseUrl, strategieRelance);
  }


  getStrategieRelanceById(id: number): Observable<StrategieRelance> {
    const url = `${this.baseUrl}/${id}`;
    return this.http.get<StrategieRelance>(url);
  }


  getAllStrategieRelances(): Observable<StrategieRelance[]> {
    return this.http.get<StrategieRelance[]>(this.baseUrl);
  }


  updateStrategieRelance(id: number, strategieRelance: StrategieRelance): Observable<StrategieRelance> {
    const url = `${this.baseUrl}/${id}`;
    return this.http.put<StrategieRelance>(url, strategieRelance);
  }

  deleteStrategieRelance(id: number): Observable<void> {
    const url = `${this.baseUrl}/${id}`;
    return this.http.delete<void>(url);
  }

  
  deleteAllStrategieRelances(): Observable<void> {
    return this.http.delete<void>(this.baseUrl);
  }

  getMoyenRelanceForClient(clientId: number): Observable<MoyenRelance> {
    const url = `${this.baseUrl}/relance-methode/${clientId}`;
    return this.http.get<MoyenRelance>(url);
  }

  getStrategieRelanceByClientId(clientId: number): Observable<StrategieRelance> {
    const url = `${this.baseUrl}/client/${clientId}`;
    return this.http.get<StrategieRelance>(url);
  }
}
